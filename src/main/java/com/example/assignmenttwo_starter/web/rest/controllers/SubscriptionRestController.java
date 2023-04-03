package com.example.assignmenttwo_starter.web.rest.controllers;

import com.example.assignmenttwo_starter.model.Subscription;
import com.example.assignmenttwo_starter.services.SubscriptionService;
import com.example.assignmenttwo_starter.utilities.QrCodeUtility;
import com.example.assignmenttwo_starter.utilities.SubscriptionVCardUtility;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/subscriptions")
public class SubscriptionRestController {
    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    private PagedResourcesAssembler<Subscription> pagedResourcesAssembler;

    /**
     * Create a new subscription
     *
     * @param newSubscription - Subscription object to be created
     * @return - Returns the subscription object created. If an error occurs, return a bad request
     */
    @PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Subscription> createSubscription(@Valid @RequestBody Subscription newSubscription) {
        try {
            Subscription subscription = subscriptionService.createSubscription(newSubscription);
            return ResponseEntity.ok(subscription);
        } catch (Exception exception) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Delete a subscription
     *
     * @param subscriptionId The id of the subscription to be deleted
     * @return - Returns a string indicating if the subscription was deleted successfully or not. If a subscription with the specified id is not found, return a not found response
     */
    @Operation(
            summary = "Delete a subscription",
            parameters = {
                    @Parameter(name = "subscriptionId", description = "The Id of the subscription to be deleted", schema = @Schema(type = "integer")),
            })
    @DeleteMapping(value = "/{subscriptionId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, MediaType.TEXT_XML_VALUE})
    public ResponseEntity<String> deleteSubscription(@PathVariable("subscriptionId") Integer subscriptionId) {
        var subscriptionOptional = subscriptionService.findById(subscriptionId);
        if (subscriptionOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        try {
            subscriptionService.deleteSubscription(subscriptionId);
            return ResponseEntity.ok("Subscription Deleted Successfully");
        } catch (Exception e) {
            return ResponseEntity.ok("Failed to Delete Subscription");
        }
    }

    /**
     * Get a subscription by id
     *
     * @param subscriptionId The ID of the subscription to be retrieved
     * @return - Returns the subscription for the specified ID. If a subscription with the specified id is not found, return a not found response
     */
    @GetMapping(value = "/{subscriptionId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Operation(summary = "Get a subscription by id")
    public ResponseEntity<Subscription> getSubscriptionById(@PathVariable("subscriptionId") Integer subscriptionId) {
        Optional<Subscription> subscriptionOptional = subscriptionService.findById(subscriptionId);

        if (subscriptionOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Subscription subscription = subscriptionOptional.get();

        subscription.add(linkTo(methodOn(SubscriptionRestController.class).getSubscriptionById(subscriptionId)).withSelfRel());
        subscription.add(linkTo(methodOn(getClass()).getSubscriptions()).withRel("subscriptions"));

        return ResponseEntity.ok(subscription);
    }

    @GetMapping(value = "/{subscriptionId}/qrcode", produces = MediaType.IMAGE_PNG_VALUE)
    @Operation(summary = "Get a subscription link by id")
    public ResponseEntity<BufferedImage> getSubscriptionLinkQrCodeById(@PathVariable("subscriptionId") int subscriptionId) throws Exception {
        Optional<Subscription> subscriptionOptional = subscriptionService.findById(subscriptionId);

        if (subscriptionOptional.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        Subscription subscription = subscriptionOptional.get();

        var bufferedImage = QrCodeUtility.getQrCodeImage(subscription.getUrl(),275);
        return ResponseEntity.ok(bufferedImage);
    }

    @GetMapping(value = "/{subscriptionId}/vcard", produces = MediaType.IMAGE_PNG_VALUE)
    @Operation(summary = "Get a subscription vcard by id")
    public ResponseEntity<BufferedImage> getSubscriptionVCardQrCodeById(@PathVariable("subscriptionId") int subscriptionId) throws Exception {
        Optional<Subscription> subscriptionOptional = subscriptionService.findById(subscriptionId);

        if (subscriptionOptional.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);


        Subscription subscription = subscriptionOptional.get();


        var vCard = SubscriptionVCardUtility.getVCard(subscription);

        var bufferedImage = QrCodeUtility.getQrCodeImage(vCard.toString(),275);
        return ResponseEntity.ok(bufferedImage);
    }

    /**
     * Get all subscriptions
     *
     * @return - Returns a list of all subscriptions
     */
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Operation(summary = "Get all subscriptions")
    public CollectionModel<Subscription> getSubscriptions() {
        List<Subscription> subscriptions = subscriptionService.findAll();
        addLinksToSubscriptions(subscriptions);

        return CollectionModel.of(subscriptions, linkTo(methodOn(getClass()).getSubscriptions()).withSelfRel());
    }

    @Operation(summary = "Getting subscriptions through pagination")
    @GetMapping(value = "/page", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public PagedModel<EntityModel<Subscription>> getAll(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize
    ) {
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
        Page<Subscription> page = subscriptionService.findAll(pageRequest);
        for (Subscription subscription : page.getContent()) {
            Integer id = subscription.getId();

            Link subscriptionLink = linkTo(methodOn(SubscriptionRestController.class).getSubscriptionById(id)).withRel("details");
            subscription.add(subscriptionLink);
        }
        Link link = linkTo(getClass()).withSelfRel();

        return pagedResourcesAssembler.toModel(page, link);
    }

    /**
     * Updates a subscription
     *
     * @param subscriptionId      The ID of the subscription to update
     * @param updatedSubscription The subscription object with the updated information to save
     * @return If successful, returns the updated subscription object. If unsuccessful, returns a bad request response. If the subscription ID in the path does not match the subscription ID in the body, returns a bad request response.
     */
    @PutMapping(value = "/{subscriptionId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Operation(summary = "Updates a subscription")
    public ResponseEntity<Subscription> updateSubscription(@PathVariable int subscriptionId,@Valid @RequestBody Subscription updatedSubscription) {
        if (updatedSubscription.getId() != subscriptionId) {
            return ResponseEntity.badRequest().build();
        }
        try {
            Subscription subscription = subscriptionService.updateSubscription(updatedSubscription);
            return ResponseEntity.ok(subscription);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    // Private Methods

    /**
     * Handles validation exceptions
     * Required for @Valid annotation to work
     *
     * @link <a href="https://www.baeldung.com/spring-boot-bean-validation">Spring Boot Bean Validation</a>
     * @param ex The exception to handle
     * @return A map of the validation errors
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    // Static Methods

    /**
     * Adds links to a list of subscriptions
     *
     * @param subscriptions The list of subscriptions to add links to each subscription
     */
    public static void addLinksToSubscriptions(List<Subscription> subscriptions) {
        for (Subscription subscription : subscriptions) {
            Integer subscriptionId = subscription.getId();

            Link selfLink = linkTo(methodOn(SubscriptionRestController.class).getSubscriptionById(subscriptionId)).withSelfRel();
            subscription.add(selfLink);
        }
    }
}
