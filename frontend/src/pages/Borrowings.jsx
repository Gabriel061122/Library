import { useState, useEffect } from 'react';
import { getBorrowings } from '../api/api';

export default function Borrowings() {
  const [borrowings, setBorrowings] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchBorrowings();
  }, []);

  const fetchBorrowings = async () => {
    setLoading(true);
    try {
      const res = await getBorrowings();
      setBorrowings(res.data);
    } catch {
      console.error('Failed to load borrowings');
    } finally {
      setLoading(false);
    }
  };

  const isOverdue = (returnDate) => {
    return new Date(returnDate) < new Date();
  };

  if (loading) return <div className="loading">Loading borrowings...</div>;

  return (
    <div className="borrowings-page">
      <h2>My Borrowings</h2>
      {borrowings.length === 0 ? (
        <div className="empty">No borrowings yet. Browse books to borrow one!</div>
      ) : (
        <div className="borrowings-list">
          {borrowings.map((b) => (
            <div
              key={b.id}
              className={`borrowing-card ${isOverdue(b.returnDate) ? 'overdue' : ''}`}
            >
              <div className="borrowing-header">
                <span className="borrowing-id">Borrowing #{b.id}</span>
                {isOverdue(b.returnDate) && <span className="overdue-badge">OVERDUE</span>}
              </div>
              <div className="borrowing-info">
                <p>
                  <strong>Book:</strong> {b.copy?.book?.title || 'N/A'} (Copy #{b.copy?.id})
                </p>
                <p>
                  <strong>Status:</strong> {b.copy?.avlbl}
                </p>
                <p>
                  <strong>Borrowed:</strong> {b.borrowingDate}
                </p>
                <p>
                  <strong>Due:</strong> {b.returnDate}
                </p>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
