package com.example.assignmenttwo_starter.rest.controllers;

import com.example.assignmenttwo_starter.model.Order;
import com.example.assignmenttwo_starter.services.OrderService;
import com.example.assignmenttwo_starter.utilities.OrderPdfPrinter;
import com.itextpdf.text.DocumentException;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    /**
     * Create a new order
     * @param newOrder - Order object to be created
     * @return - Returns the order object created. If an error occurs, return a bad request
     */
    @PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Operation(summary = " Create a new order\n" +
                        " @param newOrder - Order object to be created\n" +
                        "* @return - Returns the order object created. If an error occurs, return a bad request")
    public ResponseEntity<Order> createOrder(@RequestBody Order newOrder) {

        try {
            Order order = orderService.updateOrder(newOrder);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Delete a order
     * @param orderId The id of the order to be deleted
     * @return - Returns a string indicating if the order was deleted successfully or not. If a order with the specified id is not found, return a not found response
     */
    @DeleteMapping(value = "/{orderId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, MediaType.TEXT_XML_VALUE})
    @Operation(summary = "Delete a order\n" +
              " @param orderId The id of the order to be deleted\n" +
              " * @return - Returns a string indicating if the order was deleted successfully or not. If a order with the specified id is not found, return a not found response")
    public ResponseEntity<String> deleteOrder(@PathVariable("orderId") Integer orderId) {
        var orderOptional = orderService.findById(orderId);
        if (orderOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        try {
            orderService.deleteOrder(orderId);
            return ResponseEntity.ok("Order Deleted Successfully");
        } catch (Exception e) {
            return ResponseEntity.ok("Failed to Delete Order");
        }
    }

    /**
     * Get a order by id
     * @param orderId The ID of the order to be retrieved
     * @return - Returns the order for the specified ID. If a order with the specified id is not found, return a not found response
     */
    @GetMapping(value = "/{orderId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Operation(summary = "Get a order by id\n" +
              " @param orderId The ID of the order to be retrieved\n" +
              " * @return - Returns the order for the specified ID. If a order with the specified id is not found, return a not found response")
    public ResponseEntity<Order> getOrderById(@PathVariable("orderId") Integer orderId) {
        Optional<Order> orderOptional = orderService.findById(orderId);

        if (orderOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Order order = orderOptional.get();

        order.add(linkTo(methodOn(OrderController.class).getOrderById(orderId)).withSelfRel());
        order.add(linkTo(methodOn(getClass()).getOrders()).withRel("orders"));

        return ResponseEntity.ok(order);
    }


    /**
     * Get all orders
     * @return - Returns a list of all orders
     */
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Operation(summary = "Get all orders\n" +
              " * @return - Returns a list of all orders")
    public CollectionModel<Order> getOrders() {
        List<Order> orders = orderService.findAll();
        addLinksToOrders(orders);

        return CollectionModel.of(orders, linkTo(methodOn(getClass()).getOrders()).withSelfRel());
    }

    /**
     * Updates a order
     * @param orderId The ID of the order to update
     * @param updatedOrder The order object with the updated information to save
     * @return If successful, returns the updated order object. If unsuccessful, returns a bad request response. If the order ID in the path does not match the order ID in the body, returns a bad request response.
     */
    @PutMapping(value = "/{orderId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Operation(summary = "Updates a order\n" +
              " @param orderId The ID of the order to update\n" +
              " @param updatedOrder The order object with the updated information to save\n" +
              " * @return If successful, returns the updated order object. If unsuccessful, returns a bad request response. If the order ID in the path does not match the order ID in the body, returns a bad request response.")
    public ResponseEntity<Order> updateOrder(@PathVariable int orderId, @RequestBody Order updatedOrder) {
        if (updatedOrder.getId() != null && updatedOrder.getId() != orderId) {
            return ResponseEntity.badRequest().build();
        }
        try {
            Order order = orderService.updateOrder(updatedOrder);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    // Static Methods

    /**
     * Adds links to a list of orders
     *
     * @param orders The list of orders to add links to each order
     */
    public static void addLinksToOrders(List<Order> orders) {
        for (Order order : orders) {
            Integer orderId = order.getId();


            Link selfLink = linkTo(methodOn(OrderController.class).getOrderById(orderId)).withSelfRel();
            order.add(selfLink);
        }
    }
}
