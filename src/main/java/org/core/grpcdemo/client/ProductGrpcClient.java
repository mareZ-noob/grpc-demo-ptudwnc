package org.core.grpcdemo.client;

import org.core.grpcdemo.proto.*;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class ProductGrpcClient {

    private final ProductServiceGrpc.ProductServiceBlockingStub productServiceStub;

    @Autowired
    public ProductGrpcClient(ProductServiceGrpc.ProductServiceBlockingStub productServiceStub) {
        this.productServiceStub = productServiceStub;
    }

    public ProductResponse createProduct(String name, String description, double price, int quantity) {
        CreateProductRequest request = CreateProductRequest.newBuilder()
                .setName(name)
                .setDescription(description)
                .setPrice(price)
                .setQuantity(quantity)
                .build();

        return productServiceStub.createProduct(request);
    }

    public ProductResponse getProduct(long id) {
        GetProductRequest request = GetProductRequest.newBuilder()
                .setId(id)
                .build();

        return productServiceStub.getProduct(request);
    }

    public ProductResponse updateProduct(long id, String name, String description, double price, int quantity) {
        UpdateProductRequest request = UpdateProductRequest.newBuilder()
                .setId(id)
                .setName(name)
                .setDescription(description)
                .setPrice(price)
                .setQuantity(quantity)
                .build();

        return productServiceStub.updateProduct(request);
    }

    public DeleteProductResponse deleteProduct(long id) {
        DeleteProductRequest request = DeleteProductRequest.newBuilder()
                .setId(id)
                .build();

        return productServiceStub.deleteProduct(request);
    }

    public ListProductsResponse listProducts(int page, int size) {
        ListProductsRequest request = ListProductsRequest.newBuilder()
                .setPage(page)
                .setSize(size)
                .build();

        return productServiceStub.listProducts(request);
    }
}