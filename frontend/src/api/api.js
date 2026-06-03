import axios from 'axios';

const API_BASE = import.meta.env.VITE_API_URL || '';

const api = axios.create({
  baseURL: API_BASE,
  headers: { 'Content-Type': 'application/json' },
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Auth
export const login = (email, password) =>
  api.post('/auth/login', { email, password });

// Users
export const getUsers = () => api.get('/users');
export const getUserById = (id) => api.get(`/users/${id}`);
export const createUser = (data) => api.post('/auth/register', data);
export const updateUser = (id, data) => api.put(`/users/${id}`, data);
export const deleteUser = (id) => api.delete(`/users/${id}`);

// Books
export const getBooks = () => api.get('/books');
export const getBookByIsbn = (isbn) => api.get(`/books/${isbn}`);
export const filterBooks = (params) => api.get('/books/filter', { params });
export const createBook = (data) => api.post('/books', data);
export const updateBook = (isbn, data) => api.put(`/books/${isbn}`, data);
export const deleteBook = (isbn) => api.delete(`/books/${isbn}`);

// Orders
export const getOrders = () => api.get('/orders');
export const getOrderById = (id) => api.get(`/orders/${id}`);
export const createOrder = (data) => api.post('/orders', data);
export const updateOrder = (id, data) => api.put(`/orders/${id}`, data);
export const deleteOrder = (id) => api.delete(`/orders/${id}`);

// Buys
export const getBuys = () => api.get('/buys');
export const createBuy = (data) => api.post('/buys', data);

// Borrowings
export const getBorrowings = () => api.get('/borrowings');
export const getBorrowingById = (id) => api.get(`/borrowings/${id}`);
export const createBorrowing = (data) => api.post('/borrowings', data);
export const updateBorrowing = (id, data) => api.put(`/borrowings/${id}`, data);

// Borrowing Copies
export const getBorrowingCopies = () => api.get('/borrowing-copies');
export const getBorrowingCopyById = (id) => api.get(`/borrowing-copies/${id}`);
export const createBorrowingCopy = (data) => api.post('/borrowing-copies', data);
export const createBorrowingCopyForBook = (isbn) =>
  api.post(`/borrowing-copies/books/${isbn}`);
export const deleteBorrowingCopy = (id) => api.delete(`/borrowing-copies/${id}`);
export const deleteLastBorrowingCopy = (isbn) =>
  api.delete(`/borrowing-copies/books/${isbn}/last`);

// User Types
export const getUserTypes = () => api.get('/user-types');

export default api;
