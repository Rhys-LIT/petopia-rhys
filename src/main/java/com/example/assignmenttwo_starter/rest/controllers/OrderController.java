package com.example.assignmenttwo_starter.rest.controllers;

import com.example.assignmenttwo_starter.model.Order;
import com.example.assignmenttwo_starter.services.OrderService;
import com.example.assignmenttwo_starter.utilities.OrderPdfPrinter;
import com.itextpdf.text.DocumentException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.annotation.AfterReturning;
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
     *
     * @param newOrder - Order object to be created
     * @return - Returns the order object created. If an error occurs, return a bad request
     */
    @PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Operation(summary = " Create a new order")
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
     *
     * @param orderId The id of the order to be deleted
     * @return - Returns a string indicating if the order was deleted successfully or not. If a order with the specified id is not found, return a not found response
     */
    @DeleteMapping(value = "/{orderId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, MediaType.TEXT_XML_VALUE})
    @Operation(summary = "Delete a order")
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
     *
     * @param orderId The ID of the order to be retrieved
     * @return - Returns the order for the specified ID. If a order with the specified id is not found, return a not found response
     */
    @GetMapping(value = "/{orderId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Operation(summary = "Get a order by id")
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
     *
     * @return - Returns a list of all orders
     */
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Operation(summary = "Get all orders")
    public CollectionModel<Order> getOrders() {
        List<Order> orders = orderService.findAll();
        addLinksToOrders(orders);

        return CollectionModel.of(orders, linkTo(methodOn(getClass()).getOrders()).withSelfRel());
    }

    /**
     * Updates an order
     *
     * @param orderId      The ID of the order to update
     * @param updatedOrder The order object with the updated information to save
     * @return If successful, returns the updated order object. If unsuccessful, returns a bad request response. If the order ID in the path does not match the order ID in the body, returns a bad request response.
     */
    @Operation(
            summary = "Updates a order",
            parameters = {
                    @Parameter(name = "orderId", description = "The ID of the order to update"),
                    @Parameter(name = "updatedOrder", description = "The order object with the updated information to save",
                            schema = @Schema(implementation = Order.class))
            }
    )
    @PutMapping(value = "/{orderId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Order> updateOrder(@PathVariable int orderId, @RequestBody Order updatedOrder) {
        ResponseEntity<Order> result;
        if (updatedOrder.getId() != null && updatedOrder.getId() != orderId) {
            result = ResponseEntity.badRequest().build();
        } else {
            try {
                Order order = orderService.updateOrder(updatedOrder);
                result = ResponseEntity.ok(order);
            } catch (Exception e) {
                result = ResponseEntity.badRequest().build();
            }
        }
        return result;
    }

    @GetMapping(value = "/{orderId}/pdf")
    public void getOrderDocumentById(HttpServletResponse response, @PathVariable("orderId") Integer orderId) {
        Optional<Order> orderOptional = orderService.findById(orderId);

        if (orderOptional.isEmpty()) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE);
            try {
                response.getWriter().write("Order not found");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        Order order = orderOptional.get();
        String fileName = "order-" + order.getId() + ".pdf";
        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        String productsDirectoryPath = "static/assets/images/thumbs/";
        var invoicePdfBuilder = new OrderPdfPrinter(order, productsDirectoryPath);
        try {
            invoicePdfBuilder.generatePdfReport(response.getOutputStream());
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE);
            try {
                response.getOutputStream().close();
                response.getWriter().write("Failed to generate PDF");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
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
