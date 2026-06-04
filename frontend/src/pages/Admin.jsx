import { useState, useEffect } from 'react';
import {
  getBooks,
  createBook,
  updateBook,
  deleteBook,
  createBorrowingCopyForBook,
  deleteLastBorrowingCopy,
  getBorrowingCopies,
  getUserTypes,
} from '../api/api';

export default function Admin() {
  const [tab, setTab] = useState('books');
  const [books, setBooks] = useState([]);
  const [copies, setCopies] = useState([]);
  const [userTypes, setUserTypes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState({ text: '', type: '' });

  // Book form
  const emptyBook = { isbn: '', title: '', author: '', price: 0, stock: 0, genreo: [] };
  const [bookForm, setBookForm] = useState(emptyBook);
  const [editingBook, setEditingBook] = useState(null);
  const [genreInput, setGenreInput] = useState('');

  // Copy form
  const [copyIsbn, setCopyIsbn] = useState('');

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    setLoading(true);
    try {
      const [booksRes, copiesRes, typesRes] = await Promise.all([
        getBooks(),
        getBorrowingCopies(),
        getUserTypes(),
      ]);
      setBooks(booksRes.data);
      setCopies(copiesRes.data);
      setUserTypes(typesRes.data);
    } catch {
      setMessage({ text: 'Failed to load data', type: 'error' });
    } finally {
      setLoading(false);
    }
  };

  // Book CRUD
  const handleBookSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editingBook) {
        await updateBook(editingBook.isbn, bookForm);
        setMessage({ text: 'Book updated!', type: 'success' });
      } else {
        await createBook(bookForm);
        setMessage({ text: 'Book created!', type: 'success' });
      }
      setBookForm(emptyBook);
      setEditingBook(null);
      fetchData();
    } catch {
      setMessage({ text: 'Failed to save book', type: 'error' });
    }
  };

  const handleEditBook = (book) => {
    setEditingBook(book);
    setBookForm({ ...book });
  };

  const handleDeleteBook = async (isbn) => {
    if (!confirm('Delete this book?')) return;
    try {
      await deleteBook(isbn);
      setMessage({ text: 'Book deleted!', type: 'success' });
      fetchData();
    } catch {
      setMessage({ text: 'Failed to delete book', type: 'error' });
    }
  };

  const addGenre = () => {
    if (genreInput.trim()) {
      setBookForm({
        ...bookForm,
        genreo: [...bookForm.genreo, { id: Date.now(), name: genreInput.trim() }],
      });
      setGenreInput('');
    }
  };

  const removeGenre = (idx) => {
    setBookForm({
      ...bookForm,
      genreo: bookForm.genreo.filter((_, i) => i !== idx),
    });
  };

  // Copy management
  const handleAddCopy = async () => {
    if (!copyIsbn.trim()) {
      setMessage({ text: 'Enter an ISBN', type: 'error' });
      return;
    }
    try {
      await createBorrowingCopyForBook(copyIsbn.trim());
      setMessage({ text: 'Borrowing copy added!', type: 'success' });
      setCopyIsbn('');
      fetchData();
    } catch {
      setMessage({ text: 'Failed to add copy. Check the ISBN.', type: 'error' });
    }
  };

  const handleDeleteLastCopy = async (isbn) => {
    if (!confirm('Delete last copy of this book?')) return;
    try {
      await deleteLastBorrowingCopy(isbn);
      setMessage({ text: 'Last copy removed!', type: 'success' });
      fetchData();
    } catch {
      setMessage({ text: 'Failed to remove copy', type: 'error' });
    }
  };

  const getCopiesForBook = (isbn) => {
    return copies.filter((c) => c.book?.isbn === isbn);
  };

  if (loading) return <div className="loading">Loading admin panel...</div>;

  return (
    <div className="admin-page">
      <h2>Admin Panel</h2>

      {message.text && (
        <div className={`alert alert-${message.type}`}>
          {message.text}
          <button onClick={() => setMessage({ text: '', type: '' })}>×</button>
        </div>
      )}

      <div className="admin-tabs">
        <button
          className={`tab ${tab === 'books' ? 'active' : ''}`}
          onClick={() => setTab('books')}
        >
          Manage Books
        </button>
        <button
          className={`tab ${tab === 'copies' ? 'active' : ''}`}
          onClick={() => setTab('copies')}
        >
          Borrowing Copies
        </button>
        <button
          className={`tab ${tab === 'usertypes' ? 'active' : ''}`}
          onClick={() => setTab('usertypes')}
        >
          User Types
        </button>
      </div>

      {tab === 'books' && (
        <div className="admin-section">
          <div className="admin-form-container">
            <h3>{editingBook ? 'Edit Book' : 'Add New Book'}</h3>
            <form onSubmit={handleBookSubmit} className="admin-form">
              <div className="form-group">
                <label>ISBN</label>
                <input
                  value={bookForm.isbn}
                  onChange={(e) => setBookForm({ ...bookForm, isbn: e.target.value })}
                  required
                  disabled={!!editingBook}
                />
              </div>
              <div className="form-group">
                <label>Title</label>
                <input
                  value={bookForm.title}
                  onChange={(e) => setBookForm({ ...bookForm, title: e.target.value })}
                  required
                />
              </div>
              <div className="form-group">
                <label>Author</label>
                <input
                  value={bookForm.author}
                  onChange={(e) => setBookForm({ ...bookForm, author: e.target.value })}
                  required
                />
              </div>
              <div className="form-row">
                <div className="form-group">
                  <label>Price</label>
                  <input
                    type="number"
                    value={bookForm.price}
                    onChange={(e) =>
                      setBookForm({ ...bookForm, price: Number(e.target.value) })
                    }
                    required
                  />
                </div>
                <div className="form-group">
                  <label>Stock</label>
                  <input
                    type="number"
                    value={bookForm.stock}
                    onChange={(e) =>
                      setBookForm({ ...bookForm, stock: Number(e.target.value) })
                    }
                    required
                  />
                </div>
              </div>
              <div className="form-group">
                <label>Genres</label>
                <div className="genre-input-row">
                  <input
                    value={genreInput}
                    onChange={(e) => setGenreInput(e.target.value)}
                    placeholder="Add genre..."
                    onKeyDown={(e) => e.key === 'Enter' && (e.preventDefault(), addGenre())}
                  />
                  <button type="button" className="btn btn-sm" onClick={addGenre}>
                    Add
                  </button>
                </div>
                <div className="genre-tags">
                  {bookForm.genreo.map((g, i) => (
                    <span key={i} className="genre-tag">
                      {g.name}
                      <button type="button" onClick={() => removeGenre(i)}>
                        ×
                      </button>
                    </span>
                  ))}
                </div>
              </div>
              <div className="form-actions">
                <button className="btn btn-primary" type="submit">
                  {editingBook ? 'Update' : 'Create'}
                </button>
                {editingBook && (
                  <button
                    className="btn"
                    type="button"
                    onClick={() => {
                      setEditingBook(null);
                      setBookForm(emptyBook);
                    }}
                  >
                    Cancel
                  </button>
                )}
              </div>
            </form>
          </div>

          <div className="admin-list">
            <h3>Existing Books ({Array.isArray(books) ? books.length : 0})</h3>
            <table>
              <thead>
                <tr>
                  <th>ISBN</th>
                  <th>Title</th>
                  <th>Author</th>
                  <th>Price</th>
                  <th>Stock</th>
                  <th>Copies</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {Array.isArray(books) && books.map((book) => (
                  <tr key={book.isbn}>
                    <td>{book.isbn}</td>
                    <td>{book.title}</td>
                    <td>{book.author}</td>
                    <td>${book.price}</td>
                    <td>{book.stock}</td>
                    <td>{getCopiesForBook(book.isbn).length}</td>
                    <td className="actions">
                      <button className="btn btn-sm" onClick={() => handleEditBook(book)}>
                        Edit
                      </button>
                      <button
                        className="btn btn-sm btn-danger"
                        onClick={() => handleDeleteBook(book.isbn)}
                      >
                        Delete
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {tab === 'copies' && (
        <div className="admin-section">
          <div className="admin-form-container">
            <h3>Add Borrowing Copy</h3>
            <div className="inline-form">
              <div className="form-group">
                <label>Book ISBN</label>
                <input
                  value={copyIsbn}
                  onChange={(e) => setCopyIsbn(e.target.value)}
                  placeholder="Enter ISBN..."
                />
              </div>
              <button className="btn btn-primary" onClick={handleAddCopy}>
                Add Copy
              </button>
            </div>
          </div>

          <div className="admin-list">
            <h3>All Borrowing Copies ({copies.length})</h3>
            <table>
              <thead>
                <tr>
                  <th>Copy ID</th>
                  <th>Book Title</th>
                  <th>ISBN</th>
                  <th>Availability</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {copies.map((copy) => (
                  <tr key={copy.id}>
                    <td>{copy.id}</td>
                    <td>{copy.book?.title}</td>
                    <td>{copy.book?.isbn}</td>
                    <td>
                      <span
                        className={`availability-badge ${copy.avlbl?.toLowerCase()}`}
                      >
                        {copy.avlbl}
                      </span>
                    </td>
                    <td>
                      <button
                        className="btn btn-sm btn-danger"
                        onClick={() => handleDeleteLastCopy(copy.book?.isbn)}
                      >
                        Remove Last
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {tab === 'usertypes' && (
        <div className="admin-section">
          <h3>User Types</h3>
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Type</th>
              </tr>
            </thead>
            <tbody>
              {userTypes.map((ut) => (
                <tr key={ut.id}>
                  <td>{ut.id}</td>
                  <td>{ut.type}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
