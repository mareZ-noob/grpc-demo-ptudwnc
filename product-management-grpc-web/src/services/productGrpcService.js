// src/services/productGrpcService.js
import { ProductServiceClient } from '../proto/product_grpc_web_pb';
import {
  CreateProductRequest,
  GetProductRequest,
  UpdateProductRequest,
  DeleteProductRequest,
  ListProductsRequest
} from '../proto/product_pb';

// Initialize gRPC-Web client
const client = new ProductServiceClient('http://localhost:8080', null, null);

const productGrpcService = {
  /**
   * Create a new product
   */
  createProduct: (name, description, price, quantity) => {
    return new Promise((resolve, reject) => {
      const request = new CreateProductRequest();
      request.setName(name);
      request.setDescription(description);
      request.setPrice(price);
      request.setQuantity(quantity);

      client.createProduct(request, {}, (err, response) => {
        if (err) {
          reject(err);
        } else {
          const product = response.getProduct();
          resolve({
            success: true,
            message: response.getMessage(),
            data: {
              id: product.getId(),
              name: product.getName(),
              description: product.getDescription(),
              price: product.getPrice(),
              quantity: product.getQuantity()
            }
          });
        }
      });
    });
  },

  /**
   * Get product by ID
   */
  getProduct: (id) => {
    return new Promise((resolve, reject) => {
      const request = new GetProductRequest();
      request.setId(id);

      client.getProduct(request, {}, (err, response) => {
        if (err) {
          reject(err);
        } else {
          const product = response.getProduct();
          resolve({
            success: true,
            data: {
              id: product.getId(),
              name: product.getName(),
              description: product.getDescription(),
              price: product.getPrice(),
              quantity: product.getQuantity()
            }
          });
        }
      });
    });
  },

  /**
   * Update product
   */
  updateProduct: (id, name, description, price, quantity) => {
    return new Promise((resolve, reject) => {
      const request = new UpdateProductRequest();
      request.setId(id);
      request.setName(name);
      request.setDescription(description);
      request.setPrice(price);
      request.setQuantity(quantity);

      client.updateProduct(request, {}, (err, response) => {
        if (err) {
          reject(err);
        } else {
          const product = response.getProduct();
          resolve({
            success: true,
            message: response.getMessage(),
            data: {
              id: product.getId(),
              name: product.getName(),
              description: product.getDescription(),
              price: product.getPrice(),
              quantity: product.getQuantity()
            }
          });
        }
      });
    });
  },

  /**
   * Delete product
   */
  deleteProduct: (id) => {
    return new Promise((resolve, reject) => {
      const request = new DeleteProductRequest();
      request.setId(id);

      client.deleteProduct(request, {}, (err, response) => {
        if (err) {
          reject(err);
        } else {
          resolve({
            success: response.getSuccess(),
            message: response.getMessage()
          });
        }
      });
    });
  },

  /**
   * List all products with pagination
   */
  listProducts: (page = 0, size = 10) => {
    return new Promise((resolve, reject) => {
      const request = new ListProductsRequest();
      request.setPage(page);
      request.setSize(size);

      client.listProducts(request, {}, (err, response) => {
        if (err) {
          reject(err);
        } else {
          const products = response.getProductsList().map(product => ({
            id: product.getId(),
            name: product.getName(),
            description: product.getDescription(),
            price: product.getPrice(),
            quantity: product.getQuantity()
          }));

          resolve({
            success: true,
            data: products,
            pagination: {
              page: page,
              size: size,
              total: response.getTotal()
            }
          });
        }
      });
    });
  }
};

export default productGrpcService;