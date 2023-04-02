package com.example.assignmenttwo_starter.web.rest.controllers;

import com.example.assignmenttwo_starter.model.Customer;
import com.example.assignmenttwo_starter.model.Order;
import com.example.assignmenttwo_starter.services.CustomerService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/customers")
public class CustomerController {
    @Autowired
    private CustomerService customerService;
    @Autowired
    private PagedResourcesAssembler<Customer> pagedResourcesAssembler;

    /**
     * Create a new customer
     *
     * @param newCustomer - Customer object to be created
     * @return - Returns the customer object created. If an error occurs, return a bad request
     */
    @Operation(summary = " Create a new customer")
    @PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer newCustomer) {

        try {
            Customer customer = customerService.updateCustomer(newCustomer);
            return ResponseEntity.ok(customer);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Delete a customer
     *
     * @param customerId The id of the customer to be deleted
     * @return - Returns a string indicating if the customer was deleted successfully or not. If a customer with the specified id is not found, return a not found response
     */
    @Operation(
            summary = "Delete a customer",
            parameters = {
                    @Parameter(name = "customerId", description = "The Id of the customer to be deleted", schema = @Schema(type = "integer")),
            })
    @DeleteMapping(value = "/{customerId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, MediaType.TEXT_XML_VALUE})
    public ResponseEntity<String> deleteCustomer(@PathVariable("customerId") Integer customerId) {
        var customerOptional = customerService.findById(customerId);
        if (customerOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        try {
            customerService.deleteCustomer(customerId);
            return ResponseEntity.ok("Customer Deleted Successfully");
        } catch (Exception e) {
            return ResponseEntity.ok("Failed to Delete Customer");
        }
    }

    /**
     * Get a customer by id
     *
     * @param customerId The ID of the customer to be retrieved
     * @return - Returns the customer for the specified ID. If a customer with the specified id is not found, return a not found response
     */
    @GetMapping(value = "/{customerId}/orders", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Operation(summary = "Get a customer by id")
    public CollectionModel<Order> getCustomerAssociatedOrdersByCustomerId(@PathVariable("customerId") Integer customerId) {
        Optional<Customer> customerOptional = customerService.findById(customerId);

        if (customerOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");
        }

        Customer customer = customerOptional.get();
        return CollectionModel.of(customer.getOrders());
    }

    /**
     * Get a customer by id
     *
     * @param customerId The ID of the customer to be retrieved
     * @return - Returns the customer for the specified ID. If a customer with the specified id is not found, return a not found response
     */
    @GetMapping(value = "/{customerId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Operation(summary = "Get a customer by id")
    public ResponseEntity<Customer> getCustomerById(@PathVariable("customerId") Integer customerId) {
        Optional<Customer> customerOptional = customerService.findById(customerId);

        if (customerOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Customer customer = customerOptional.get();

        customer.add(linkTo(methodOn(CustomerController.class).getCustomerById(customerId)).withSelfRel());
        customer.add(linkTo(methodOn(getClass()).getCustomers()).withRel("customers"));

        return ResponseEntity.ok(customer);
    }


    /**
     * Get all customers
     *
     * @return - Returns a list of all customers
     */
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Operation(summary = "Get all customers")
    public CollectionModel<Customer> getCustomers() {
        List<Customer> customers = customerService.findAll();
        addLinksToCustomers(customers);

        return CollectionModel.of(customers, linkTo(methodOn(getClass()).getCustomers()).withSelfRel());
    }

    @Operation(summary = "Getting customers through pagination")
    @GetMapping(value = "/page", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public PagedModel<EntityModel<Customer>> getAll(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize
    )
    {
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
        Page<Customer> page = customerService.findAll(pageRequest);
        for (Customer customer : page.getContent())
        {
            Integer id = customer.getId();

            Link customerLink = linkTo(methodOn(CustomerController.class).getCustomerById(id)).withRel("details");
            customer.add(customerLink);
        }
        Link link = linkTo(getClass()).withSelfRel();

        return pagedResourcesAssembler.toModel(page, link);
    }
    /**
     * Updates a customer
     *
     * @param customerId      The ID of the customer to update
     * @param updatedCustomer The customer object with the updated information to save
     * @return If successful, returns the updated customer object. If unsuccessful, returns a bad request response. If the customer ID in the path does not match the customer ID in the body, returns a bad request response.
     */
    @PutMapping(value = "/{customerId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Operation(summary = "Updates a customer")
    public ResponseEntity<Customer> updateCustomer(@PathVariable int customerId, @RequestBody Customer updatedCustomer) {
        if (updatedCustomer.getId() != null && updatedCustomer.getId() != customerId) {
            return ResponseEntity.badRequest().build();
        }
        try {
            Customer customer = customerService.updateCustomer(updatedCustomer);
            return ResponseEntity.ok(customer);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    // Static Methods

    /**
     * Adds links to a list of customers
     *
     * @param customers The list of customers to add links to each customer
     */
    public static void addLinksToCustomers(List<Customer> customers) {
        for (Customer customer : customers) {
            Integer customerId = customer.getId();

            Link additionalOrdersLink = linkTo(methodOn(CustomerController.class).getCustomerAssociatedOrdersByCustomerId(customerId)).withRel("Additional Orders");
            customer.add(additionalOrdersLink);

            Link selfLink = linkTo(methodOn(CustomerController.class).getCustomerById(customerId)).withSelfRel();
            customer.add(selfLink);
        }
    }
}
