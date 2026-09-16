package com.order.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "product-service",
        url = "${product-service.url}"
)
public interface ProductServiceClient {

    @GetMapping("/api/products/{productId}")
    ProductResponse getProduct(
            @PathVariable("productId") Long productId
    );

    @PatchMapping("/api/products/{productId}/stock")
    ProductResponse updateStock(
            @PathVariable("productId") Long productId,
            @RequestBody UpdateStockRequest request
    );
}