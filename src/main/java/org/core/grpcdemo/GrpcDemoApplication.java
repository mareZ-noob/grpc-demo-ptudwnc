package org.core.grpcdemo;

import org.core.grpcdemo.client.ProductGrpcClient;
import org.core.grpcdemo.proto.DeleteProductResponse;
import org.core.grpcdemo.proto.ListProductsResponse;
import org.core.grpcdemo.proto.Product;
import org.core.grpcdemo.proto.ProductResponse;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GrpcDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(GrpcDemoApplication.class, args);
    }

    @Bean
    public CommandLineRunner testGrpcOperations(ProductGrpcClient client) {
        return args -> {
            System.out.println("\n=== Testing gRPC Product Service ===\n");

            // Test 1: Create Products
            System.out.println("1. Creating products...");
            ProductResponse createResponse1 = client.createProduct(
                    "Laptop Dell XPS 15",
                    "High-performance laptop with Intel i7",
                    25000000.0,
                    10
            );
            System.out.println("Created: " + createResponse1.getProduct().getName() +
                    " (ID: " + createResponse1.getProduct().getId() + ")");

            ProductResponse createResponse2 = client.createProduct(
                    "iPhone 15 Pro",
                    "Latest iPhone with A17 chip",
                    30000000.0,
                    15
            );
            System.out.println("Created: " + createResponse2.getProduct().getName() +
                    " (ID: " + createResponse2.getProduct().getId() + ")");

            ProductResponse createResponse3 = client.createProduct(
                    "Samsung Galaxy S24",
                    "Flagship Android phone",
                    22000000.0,
                    20
            );
            System.out.println("Created: " + createResponse3.getProduct().getName() +
                    " (ID: " + createResponse3.getProduct().getId() + ")");

            // Test 2: Get Product
            System.out.println("\n2. Getting product by ID...");
            long productId = createResponse1.getProduct().getId();
            ProductResponse getResponse = client.getProduct(productId);
            Product product = getResponse.getProduct();
            System.out.println("Found product:");
            System.out.println("  ID: " + product.getId());
            System.out.println("  Name: " + product.getName());
            System.out.println("  Description: " + product.getDescription());
            System.out.println("  Price: " + product.getPrice() + " VND");
            System.out.println("  Quantity: " + product.getQuantity());

            // Test 3: Update Product
            System.out.println("\n3. Updating product...");
            ProductResponse updateResponse = client.updateProduct(
                    productId,
                    "Laptop Dell XPS 15 (Updated)",
                    "High-performance laptop with Intel i9 and 32GB RAM",
                    28000000.0,
                    8
            );
            Product updatedProduct = updateResponse.getProduct();
            System.out.println("Updated product:");
            System.out.println("  Name: " + updatedProduct.getName());
            System.out.println("  Description: " + updatedProduct.getDescription());
            System.out.println("  Price: " + updatedProduct.getPrice() + " VND");
            System.out.println("  Quantity: " + updatedProduct.getQuantity());

            // Test 4: List Products
            System.out.println("\n4. Listing all products...");
            ListProductsResponse listResponse = client.listProducts(0, 10);
            System.out.println("Total products: " + listResponse.getTotal());
            System.out.println("\nProduct list:");
            for (Product p : listResponse.getProductsList()) {
                System.out.println("  - " + p.getName() + " | Price: " + p.getPrice() +
                        " VND | Qty: " + p.getQuantity());
            }

            // Test 5: Delete Product
            System.out.println("\n5. Deleting product...");
            long deleteId = createResponse2.getProduct().getId();
            DeleteProductResponse deleteResponse = client.deleteProduct(deleteId);
            System.out.println("Delete result: " + deleteResponse.getMessage());
            System.out.println("Success: " + deleteResponse.getSuccess());

            // Test 6: List Products after deletion
            System.out.println("\n6. Listing products after deletion...");
            ListProductsResponse listResponse2 = client.listProducts(0, 10);
            System.out.println("Total products: " + listResponse2.getTotal());
            System.out.println("\nProduct list:");
            for (Product p : listResponse2.getProductsList()) {
                System.out.println("  - " + p.getName() + " | Price: " + p.getPrice() +
                        " VND | Qty: " + p.getQuantity());
            }

            System.out.println("\n=== Testing completed ===\n");
        };
    }
}
