# Build
./mvnw protobuf:generate
./mvnw clean:clean

# Install
go install github.com/fullstorydev/grpcurl/cmd/grpcurl@latest
echo 'export PATH=$PATH:$(go env GOPATH)/bin' >> ~/.zshrc

# List
grpcurl -plaintext localhost:9090 list

# Describe
grpcurl -plaintext localhost:9090 describe product.ProductService

# Create
grpcurl -plaintext -d '{
  "name": "Laptop Dell XPS 15",
  "description": "High-performance laptop with Intel i7",
  "price": 25000000.0,
  "quantity": 10
}' localhost:9090 product.ProductService/CreateProduct

# Get
grpcurl -plaintext -d '{"id": 1}' localhost:9090 product.ProductService/GetProduct

# Update
grpcurl -plaintext -d '{
  "id": 1,
  "name": "Laptop Dell XPS 15 (Updated)",
  "description": "High-performance laptop with Intel i9 and 32GB RAM",
  "price": 28000000.0,
  "quantity": 8
}' localhost:9090 product.ProductService/UpdateProduct

# Pagination
grpcurl -plaintext -d '{"page": 0, "size": 10}' localhost:9090 product.ProductService/ListProducts

# Delete
grpcurl -plaintext -d '{"id": 1}' localhost:9090 product.ProductService/DeleteProduct