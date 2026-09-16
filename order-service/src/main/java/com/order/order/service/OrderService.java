package com.order.order.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.order.order.client.ProductResponse;
import com.order.order.client.ProductServiceClient;
import com.order.order.client.UpdateStockRequest;
import com.order.order.dto.request.CreateOrderRequest;
import com.order.order.dto.request.UpdateOrderStatusRequest;
import com.order.order.dto.response.OrderResponse;
import com.order.order.exception.InsufficientProductStockException;
import com.order.order.exception.InvalidOrderStatusTransitionException;
import com.order.order.exception.OrderNotFoundException;
import com.order.order.model.Order;
import com.order.order.model.OrderStatus;
import com.order.order.repository.OrderRepository;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductServiceClient productServiceClient;

    public OrderService(
            OrderRepository orderRepository,
            ProductServiceClient productServiceClient
    ) {
        this.orderRepository = orderRepository;
        this.productServiceClient =
                productServiceClient;
    }

    @Transactional
    public OrderResponse createOrder(
            CreateOrderRequest request
    ) {

        // Step 1: Check that product exists
        ProductResponse product =
                productServiceClient.getProduct(
                        request.getProductId()
                );

        // Step 2: Check stock before attempting
        // to reduce it
        if (product.getStockQuantity()
                < request.getQuantity()) {

            throw new InsufficientProductStockException(
                    "Insufficient stock for product ID: "
                            + request.getProductId()
            );
        }

        // Step 3: Reduce product stock
        UpdateStockRequest stockRequest =
        new UpdateStockRequest(-request.getQuantity());

        productServiceClient.updateStock(
                request.getProductId(),
                stockRequest
        );

        // Step 4: Create the order
        Order order = new Order(
                null,
                request.getCustomerId(),
                request.getProductId(),
                request.getQuantity(),
                OrderStatus.PENDING,
                LocalDateTime.now()
        );

        // Step 5: Save order
        Order savedOrder =
                orderRepository.save(order);

        return OrderResponse.from(savedOrder);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getAllOrders(
            Pageable pageable
    ) {

        return orderRepository
                .findAll(pageable)
                .map(OrderResponse::from);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(
            Long orderId
    ) {

        Order order = findOrderById(orderId);

        return OrderResponse.from(order);
    }

    @Transactional
    public OrderResponse updateOrderStatus(
            Long orderId,
            UpdateOrderStatusRequest request
    ) {

        Order order = findOrderById(orderId);

        OrderStatus currentStatus =
                order.getStatus();

        OrderStatus requestedStatus =
                request.getStatus();

        validateStatusTransition(
                currentStatus,
                requestedStatus
        );

        order.setStatus(requestedStatus);

        Order updatedOrder =
                orderRepository.save(order);

        return OrderResponse.from(updatedOrder);
    }

    @Transactional
    public OrderResponse cancelOrder(
            Long orderId
    ) {

        Order order = findOrderById(orderId);

        OrderStatus currentStatus =
                order.getStatus();

        validateStatusTransition(
                currentStatus,
                OrderStatus.CANCELLED
        );

        order.setStatus(OrderStatus.CANCELLED);

        Order cancelledOrder =
                orderRepository.save(order);

        return OrderResponse.from(cancelledOrder);
    }

    private Order findOrderById(
            Long orderId
    ) {

        return orderRepository
                .findById(orderId)
                .orElseThrow(() ->
                        new OrderNotFoundException(
                                "Order not found with ID: "
                                        + orderId
                        )
                );
    }

    private void validateStatusTransition(
            OrderStatus currentStatus,
            OrderStatus requestedStatus
    ) {

        if (currentStatus == requestedStatus) {

            throw new InvalidOrderStatusTransitionException(
                    "Order is already in "
                            + currentStatus
                            + " status"
            );
        }

        boolean validTransition =
                switch (currentStatus) {

                    case PENDING ->
                            requestedStatus
                                    == OrderStatus.CONFIRMED
                            || requestedStatus
                                    == OrderStatus.CANCELLED;

                    case CONFIRMED ->
                            requestedStatus
                                    == OrderStatus.COMPLETED
                            || requestedStatus
                                    == OrderStatus.CANCELLED;

                    case CANCELLED, COMPLETED ->
                            false;
                };

        if (!validTransition) {

            throw new InvalidOrderStatusTransitionException(
                    "Order cannot be changed from "
                            + currentStatus
                            + " to "
                            + requestedStatus
            );
        }
    }
}