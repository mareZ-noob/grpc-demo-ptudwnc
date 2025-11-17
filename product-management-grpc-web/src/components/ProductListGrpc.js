// src/components/ProductListGrpc.jsx
import React, { useState, useEffect } from 'react';
import productGrpcService from '../services/productGrpcService';
import './ProductList.css';

const ProductListGrpc = () => {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [pagination, setPagination] = useState({ page: 0, size: 10, total: 0 });

  // Form state
  const [showForm, setShowForm] = useState(false);
  const [editingProduct, setEditingProduct] = useState(null);
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    price: '',
    quantity: ''
  });

  useEffect(() => {
    loadProducts();
  }, [pagination.page]);

  const loadProducts = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await productGrpcService.listProducts(
        pagination.page,
        pagination.size
      );
      setProducts(response.data);
      setPagination(prev => ({ ...prev, total: response.pagination.total }));
    } catch (err) {
      setError(`Failed to load products: ${err.message}`);
      console.error('gRPC Error:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      if (editingProduct) {
        await productGrpcService.updateProduct(
          editingProduct.id,
          formData.name,
          formData.description,
          parseFloat(formData.price),
          parseInt(formData.quantity)
        );
      } else {
        await productGrpcService.createProduct(
          formData.name,
          formData.description,
          parseFloat(formData.price),
          parseInt(formData.quantity)
        );
      }

      resetForm();
      loadProducts();
    } catch (err) {
      setError(`Operation failed: ${err.message}`);
      console.error('gRPC Error:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleEdit = (product) => {
    setEditingProduct(product);
    setFormData({
      name: product.name,
      description: product.description,
      price: product.price,
      quantity: product.quantity
    });
    setShowForm(true);
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this product?')) {
      return;
    }

    setLoading(true);
    setError(null);
    try {
      await productGrpcService.deleteProduct(id);
      loadProducts();
    } catch (err) {
      setError(`Delete failed: ${err.message}`);
      console.error('gRPC Error:', err);
    } finally {
      setLoading(false);
    }
  };

  const resetForm = () => {
    setFormData({ name: '', description: '', price: '', quantity: '' });
    setEditingProduct(null);
    setShowForm(false);
  };

  const handleNextPage = () => {
    if ((pagination.page + 1) * pagination.size < pagination.total) {
      setPagination(prev => ({ ...prev, page: prev.page + 1 }));
    }
  };

  const handlePrevPage = () => {
    if (pagination.page > 0) {
      setPagination(prev => ({ ...prev, page: prev.page - 1 }));
    }
  };

  const formatPrice = (price) => {
    return new Intl.NumberFormat('vi-VN', {
      style: 'currency',
      currency: 'VND'
    }).format(price);
  };

  return (
    <div className="product-container">
      <h1>Product Management (gRPC-Web)</h1>

      <div className="connection-status">
        <span className="status-indicator"></span>
        Connected to gRPC via Envoy Proxy
      </div>

      {error && <div className="error-message">{error}</div>}

      <div className="actions">
        <button
          className="btn btn-primary"
          onClick={() => setShowForm(!showForm)}
        >
          {showForm ? 'Cancel' : 'Add New Product'}
        </button>
      </div>

      {showForm && (
        <div className="product-form">
          <h2>{editingProduct ? 'Edit Product' : 'Add New Product'}</h2>
          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label>Name:</label>
              <input
                type="text"
                name="name"
                value={formData.name}
                onChange={handleInputChange}
                required
              />
            </div>

            <div className="form-group">
              <label>Description:</label>
              <textarea
                name="description"
                value={formData.description}
                onChange={handleInputChange}
                rows="3"
              />
            </div>

            <div className="form-row">
              <div className="form-group">
                <label>Price (VND):</label>
                <input
                  type="number"
                  name="price"
                  value={formData.price}
                  onChange={handleInputChange}
                  required
                  min="0"
                  step="0.01"
                />
              </div>

              <div className="form-group">
                <label>Quantity:</label>
                <input
                  type="number"
                  name="quantity"
                  value={formData.quantity}
                  onChange={handleInputChange}
                  required
                  min="0"
                />
              </div>
            </div>

            <div className="form-actions">
              <button type="submit" className="btn btn-success" disabled={loading}>
                {loading ? 'Saving...' : editingProduct ? 'Update' : 'Create'}
              </button>
              <button type="button" className="btn btn-secondary" onClick={resetForm}>
                Cancel
              </button>
            </div>
          </form>
        </div>
      )}

      <div className="product-table">
        {loading && !showForm ? (
          <div className="loading">Loading via gRPC...</div>
        ) : (
          <>
            <table>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Name</th>
                  <th>Description</th>
                  <th>Price</th>
                  <th>Quantity</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {products.length === 0 ? (
                  <tr>
                    <td colSpan="6" className="no-data">No products found</td>
                  </tr>
                ) : (
                  products.map(product => (
                    <tr key={product.id}>
                      <td>{product.id}</td>
                      <td>{product.name}</td>
                      <td>{product.description}</td>
                      <td>{formatPrice(product.price)}</td>
                      <td>{product.quantity}</td>
                      <td>
                        <button
                          className="btn btn-sm btn-edit"
                          onClick={() => handleEdit(product)}
                        >
                          Edit
                        </button>
                        <button
                          className="btn btn-sm btn-delete"
                          onClick={() => handleDelete(product.id)}
                        >
                          Delete
                        </button>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>

            <div className="pagination">
              <button
                onClick={handlePrevPage}
                disabled={pagination.page === 0}
                className="btn btn-secondary"
              >
                Previous
              </button>
              <span>
                Page {pagination.page + 1} of {Math.ceil(pagination.total / pagination.size) || 1}
              </span>
              <button
                onClick={handleNextPage}
                disabled={(pagination.page + 1) * pagination.size >= pagination.total}
                className="btn btn-secondary"
              >
                Next
              </button>
            </div>
          </>
        )}
      </div>
    </div>
  );
};

export default ProductListGrpc;