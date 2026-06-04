import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { filterBooks, getBorrowingCopies, createBorrowing, createOrder, createBuy } from '../api/api';
import { useAuth } from '../context/AuthContext';

export default function Books() {
  const { user } = useAuth();
  const [books, setBooks] = useState([]);
  const [filters, setFilters] = useState({ title: '', author: '', sortBy: 'isbn', order: 'asc' });
  const [loading, setLoading] = useState(true);
  const [borrowModal, setBorrowModal] = useState(null);
  const [orderModal, setOrderModal] = useState(null);
  const [copies, setCopies] = useState([]);
  const [selectedCopy, setSelectedCopy] = useState('');
  const [borrowDate, setBorrowDate] = useState({ borrowingDate: '', returnDate: '' });
  const [orderQty, setOrderQty] = useState(1);
  const [message, setMessage] = useState({ text: '', type: '' });

  const fetchBooks = async () => {
    setLoading(true);
    try {
      const res = await filterBooks(filters);
      setBooks(res.data);
    } catch {
      setMessage({ text: 'Failed to load books', type: 'error' });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchBooks();
  }, []);

  const handleFilter = (e) => {
    e.preventDefault();
    fetchBooks();
  };

  const openBorrowModal = async (book) => {
    setBorrowModal(book);
    try {
      const res = await getBorrowingCopies();
      const available = res.data.filter(
        (c) => c.book?.isbn === book.isbn && c.avlbl === 'AVALIABLE'
      );
      setCopies(available);
    } catch {
      setMessage({ text: 'Failed to load copies', type: 'error' });
    }
  };

  const handleBorrow = async () => {
    if (!selectedCopy || !borrowDate.borrowingDate || !borrowDate.returnDate) {
      setMessage({ text: 'Please fill all fields', type: 'error' });
      return;
    }
    try {
      await createBorrowing({
        copy: { id: Number(selectedCopy) },
        borrowingDate: borrowDate.borrowingDate,
        returnDate: borrowDate.returnDate,
      });
      setMessage({ text: 'Borrowing request submitted!', type: 'success' });
      setBorrowModal(null);
      setSelectedCopy('');
      setBorrowDate({ borrowingDate: '', returnDate: '' });
    } catch {
      setMessage({ text: 'Failed to create borrowing', type: 'error' });
    }
  };

  const handleOrder = async () => {
    if (orderQty < 1) {
      setMessage({ text: 'Quantity must be at least 1', type: 'error' });
      return;
    }
    try {
      const orderRes = await createOrder({
        orderDate: new Date().toISOString(),
        state: 'PENDING',
        buys: [],
      });
      await createBuy({
        book: { isbn: orderModal.isbn },
        order: { id: orderRes.data.id },
        quantity: orderQty,
        price: orderModal.price,
        discount: 0,
      });
      setMessage({ text: 'Order placed successfully!', type: 'success' });
      setOrderModal(null);
      setOrderQty(1);
    } catch {
      setMessage({ text: 'Failed to place order', type: 'error' });
    }
  };

  return (
    <div className="books-page">
      <h2>Book Catalog</h2>

      {message.text && (
        <div className={`alert alert-${message.type}`}>
          {message.text}
          <button onClick={() => setMessage({ text: '', type: '' })}>×</button>
        </div>
      )}

      <form className="filter-form" onSubmit={handleFilter}>
        <input
          placeholder="Search by title..."
          value={filters.title}
          onChange={(e) => setFilters({ ...filters, title: e.target.value })}
        />
        <input
          placeholder="Search by author..."
          value={filters.author}
          onChange={(e) => setFilters({ ...filters, author: e.target.value })}
        />
        <select
          value={filters.sortBy}
          onChange={(e) => setFilters({ ...filters, sortBy: e.target.value })}
        >
          <option value="isbn">ISBN</option>
          <option value="title">Title</option>
          <option value="author">Author</option>
          <option value="price">Price</option>
        </select>
        <select
          value={filters.order}
          onChange={(e) => setFilters({ ...filters, order: e.target.value })}
        >
          <option value="asc">Ascending</option>
          <option value="desc">Descending</option>
        </select>
        <button className="btn btn-primary" type="submit">
          Filter
        </button>
      </form>

      {loading ? (
        <div className="loading">Loading books...</div>
      ) : !Array.isArray(books) || books.length === 0 ? (
        <div className="empty">No books found</div>
      ) : (
        <div className="books-grid">
          {books.map((book) => (
            <div key={book.isbn} className="book-card">
              <div className="book-info">
                <h3>{book.title}</h3>
                <p className="book-author">by {book.author}</p>
                <p className="book-isbn">ISBN: {book.isbn}</p>
                <p className="book-price">${book.price}</p>
                <p className="book-stock">Stock: {book.stock}</p>
                {book.genreo && book.genreo.length > 0 && (
                  <div className="book-genres">
                    {book.genreo.map((g) => (
                      <span key={g.id} className="genre-tag">
                        {g.name}
                      </span>
                    ))}
                  </div>
                )}
              </div>
              <div className="book-actions">
                {user && (
                  <>
                    <button className="btn btn-sm" onClick={() => openBorrowModal(book)}>
                      Borrow
                    </button>
                    <button className="btn btn-sm btn-primary" onClick={() => setOrderModal(book)}>
                      Order
                    </button>
                  </>
                )}
              </div>
            </div>
          ))}
        </div>
      )}

      {borrowModal && (
        <div className="modal-overlay" onClick={() => setBorrowModal(null)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <h3>Borrow: {borrowModal.title}</h3>
            {copies.length === 0 ? (
              <p className="empty">No available copies for this book.</p>
            ) : (
              <>
                <div className="form-group">
                  <label>Select a copy</label>
                  <select value={selectedCopy} onChange={(e) => setSelectedCopy(e.target.value)}>
                    <option value="">-- Select --</option>
                    {copies.map((c) => (
                      <option key={c.id} value={c.id}>
                        Copy #{c.id}
                      </option>
                    ))}
                  </select>
                </div>
                <div className="form-group">
                  <label>Borrowing Date</label>
                  <input
                    type="date"
                    value={borrowDate.borrowingDate}
                    onChange={(e) =>
                      setBorrowDate({ ...borrowDate, borrowingDate: e.target.value })
                    }
                  />
                </div>
                <div className="form-group">
                  <label>Return Date</label>
                  <input
                    type="date"
                    value={borrowDate.returnDate}
                    onChange={(e) =>
                      setBorrowDate({ ...borrowDate, returnDate: e.target.value })
                    }
                  />
                </div>
                <div className="modal-actions">
                  <button className="btn btn-primary" onClick={handleBorrow}>
                    Confirm
                  </button>
                  <button className="btn" onClick={() => setBorrowModal(null)}>
                    Cancel
                  </button>
                </div>
              </>
            )}
          </div>
        </div>
      )}

      {orderModal && (
        <div className="modal-overlay" onClick={() => setOrderModal(null)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <h3>Order: {orderModal.title}</h3>
            <p>Price: ${orderModal.price}</p>
            <div className="form-group">
              <label>Quantity</label>
              <input
                type="number"
                min="1"
                value={orderQty}
                onChange={(e) => setOrderQty(Number(e.target.value))}
              />
            </div>
            <p>Total: ${orderModal.price * orderQty}</p>
            <div className="modal-actions">
              <button className="btn btn-primary" onClick={handleOrder}>
                Place Order
              </button>
              <button className="btn" onClick={() => setOrderModal(null)}>
                Cancel
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
