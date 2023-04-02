package com.example.assignmenttwo_starter.web.rest.controllers;

import com.example.assignmenttwo_starter.model.Order;
import com.example.assignmenttwo_starter.services.OrderService;
import com.example.assignmenttwo_starter.utilities.OrderPdfPrinter;
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
public class OrderRestController {
    @Autowired
    private OrderService orderService;

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

        order.add(linkTo(methodOn(OrderRestController.class).getOrderById(orderId)).withSelfRel());
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


    @GetMapping(value = "/{orderId}/active")
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

        if (!order.getOrderStatus().isPendingOrProcessing()) {
            response.setStatus(HttpStatus.METHOD_NOT_ALLOWED.value());
            response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE);
            try {
                response.getWriter().write("Order is not active. Order has been shipped, delivered or cancelled.");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }


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

            Link selfLink = linkTo(methodOn(OrderRestController.class).getOrderById(orderId)).withSelfRel();
            order.add(selfLink);
        }
    }
}
