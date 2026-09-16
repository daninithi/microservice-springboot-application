package com.order.order.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.order.order.client.ProductServiceClient;
import com.order.order.dto.request.CreateOrderRequest;
import com.order.order.dto.request.UpdateOrderStatusRequest;
import com.order.order.dto.response.OrderResponse;
import com.order.order.service.OrderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final ProductServiceClient productServiceClient;

    public OrderController(
        OrderService orderService,
        ProductServiceClient productServiceClient
    ) {
        this.orderService = orderService;
        this.productServiceClient = productServiceClient;
    }

    // Create an order
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request
    ) {

        OrderResponse response = orderService.createOrder(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // Get all orders with pagination and sorting
    @GetMapping
    public ResponseEntity<Page<OrderResponse>> getAllOrders(
            Pageable pageable
    ) {

        Page<OrderResponse> responses =
                orderService.getAllOrders(pageable);

        return ResponseEntity.ok(responses);
    }

    // Get one order by ID
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable Long orderId
    ) {

        OrderResponse response =
                orderService.getOrderById(orderId);

        return ResponseEntity.ok(response);
    }

    // Update order status
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request
    ) {

        OrderResponse response =
                orderService.updateOrderStatus(orderId, request);

        return ResponseEntity.ok(response);
    }

    // Cancel an order
    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(
            @PathVariable Long orderId
    ) {

        OrderResponse response =
                orderService.cancelOrder(orderId);

        return ResponseEntity.ok(response);
    }

}