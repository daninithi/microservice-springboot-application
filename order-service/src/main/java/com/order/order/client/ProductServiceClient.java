package com.order.order.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.order.order.exception.ProductServiceException;

@Component
public class ProductServiceClient {

    private final RestClient restClient;

    public ProductServiceClient(
            RestClient.Builder restClientBuilder,
            @Value("${product-service.url}")
            String productServiceUrl
    ) {

        this.restClient = restClientBuilder
                .baseUrl(productServiceUrl)
                .build();
    }

    public ProductResponse getProduct(
            Long productId
    ) {

        try {

            return restClient
                    .get()
                    .uri(
                            "/api/products/{productId}",
                            productId
                    )
                    .retrieve()
                    .body(ProductResponse.class);

        } catch (org.springframework.web.client
                .RestClientResponseException exception) {

            throw new ProductServiceException(
                    exception.getStatusCode().value(),
                    "Product Service error: "
                            + exception.getResponseBodyAsString()
            );

        } catch (
                org.springframework.web.client
                        .ResourceAccessException exception
        ) {

            throw new ProductServiceException(
                    503,
                    "Product Service is unavailable"
            );
        }
    }

    public ProductResponse updateStock(
            Long productId,
            int quantityChange
    ) {

        try {

            return restClient
                    .patch()
                    .uri(
                            "/api/products/{productId}/stock",
                            productId
                    )
                    .body(
                            new UpdateStockRequest(
                                    quantityChange
                            )
                    )
                    .retrieve()
                    .body(ProductResponse.class);

        } catch (org.springframework.web.client
                .RestClientResponseException exception) {

            throw new ProductServiceException(
                    exception.getStatusCode().value(),
                    "Product Service error: "
                            + exception.getResponseBodyAsString()
            );

        } catch (
                org.springframework.web.client
                        .ResourceAccessException exception
        ) {

            throw new ProductServiceException(
                    503,
                    "Product Service is unavailable"
            );
        }
    }

    private static class UpdateStockRequest {

        private final Integer quantity;

        public UpdateStockRequest(
                Integer quantity
        ) {
            this.quantity = quantity;
        }

        public Integer getQuantity() {
            return quantity;
        }
    }
}