package org.core.grpcdemo.service;

import io.grpc.stub.StreamObserver;
import org.core.grpcdemo.entity.ProductEntity;
import org.core.grpcdemo.proto.*;
import org.core.grpcdemo.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.grpc.server.service.GrpcService;

import java.util.Optional;
import java.util.stream.Collectors;

@GrpcService
public class ProductServiceImpl extends ProductServiceGrpc.ProductServiceImplBase {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void createProduct(CreateProductRequest request, StreamObserver<ProductResponse> responseObserver) {
        ProductEntity entity = new ProductEntity();
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setPrice(request.getPrice());
        entity.setQuantity(request.getQuantity());

        ProductEntity savedEntity = productRepository.save(entity);

        Product product = Product.newBuilder()
                .setId(savedEntity.getId())
                .setName(savedEntity.getName())
                .setDescription(savedEntity.getDescription())
                .setPrice(savedEntity.getPrice())
                .setQuantity(savedEntity.getQuantity())
                .build();

        ProductResponse response = ProductResponse.newBuilder()
                .setProduct(product)
                .setMessage("Product created successfully")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getProduct(GetProductRequest request, StreamObserver<ProductResponse> responseObserver) {
        Optional<ProductEntity> entityOpt = productRepository.findById(request.getId());

        if (entityOpt.isPresent()) {
            ProductEntity entity = entityOpt.get();
            Product product = Product.newBuilder()
                    .setId(entity.getId())
                    .setName(entity.getName())
                    .setDescription(entity.getDescription())
                    .setPrice(entity.getPrice())
                    .setQuantity(entity.getQuantity())
                    .build();

            ProductResponse response = ProductResponse.newBuilder()
                    .setProduct(product)
                    .setMessage("Product found")
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(new RuntimeException("Product not found with id: " + request.getId()));
        }
    }

    @Override
    public void updateProduct(UpdateProductRequest request, StreamObserver<ProductResponse> responseObserver) {
        Optional<ProductEntity> entityOpt = productRepository.findById(request.getId());

        if (entityOpt.isPresent()) {
            ProductEntity entity = entityOpt.get();
            entity.setName(request.getName());
            entity.setDescription(request.getDescription());
            entity.setPrice(request.getPrice());
            entity.setQuantity(request.getQuantity());

            ProductEntity updatedEntity = productRepository.save(entity);

            Product product = Product.newBuilder()
                    .setId(updatedEntity.getId())
                    .setName(updatedEntity.getName())
                    .setDescription(updatedEntity.getDescription())
                    .setPrice(updatedEntity.getPrice())
                    .setQuantity(updatedEntity.getQuantity())
                    .build();

            ProductResponse response = ProductResponse.newBuilder()
                    .setProduct(product)
                    .setMessage("Product updated successfully")
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(new RuntimeException("Product not found with id: " + request.getId()));
        }
    }

    @Override
    public void deleteProduct(DeleteProductRequest request, StreamObserver<DeleteProductResponse> responseObserver) {
        Optional<ProductEntity> entityOpt = productRepository.findById(request.getId());

        if (entityOpt.isPresent()) {
            productRepository.deleteById(request.getId());

            DeleteProductResponse response = DeleteProductResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Product deleted successfully")
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } else {
            DeleteProductResponse response = DeleteProductResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Product not found with id: " + request.getId())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void listProducts(ListProductsRequest request, StreamObserver<ListProductsResponse> responseObserver) {
        int page = request.getPage() > 0 ? request.getPage() : 0;
        int size = request.getSize() > 0 ? request.getSize() : 10;

        Page<ProductEntity> productPage = productRepository.findAll(PageRequest.of(page, size));

        ListProductsResponse response = ListProductsResponse.newBuilder()
                .addAllProducts(productPage.getContent().stream()
                        .map(entity -> Product.newBuilder()
                                .setId(entity.getId())
                                .setName(entity.getName())
                                .setDescription(entity.getDescription())
                                .setPrice(entity.getPrice())
                                .setQuantity(entity.getQuantity())
                                .build())
                        .collect(Collectors.toList()))
                .setTotal((int) productPage.getTotalElements())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
