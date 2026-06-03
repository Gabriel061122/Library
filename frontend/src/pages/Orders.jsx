import { useState, useEffect } from 'react';
import { getOrders, getOrderById, getBuys } from '../api/api';
import { useAuth } from '../context/AuthContext';

export default function Orders() {
  const { user } = useAuth();
  const [orders, setOrders] = useState([]);
  const [buys, setBuys] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedOrder, setSelectedOrder] = useState(null);

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    setLoading(true);
    try {
      const [ordersRes, buysRes] = await Promise.all([getOrders(), getBuys()]);
      setOrders(ordersRes.data);
      setBuys(buysRes.data);
    } catch {
      console.error('Failed to load orders');
    } finally {
      setLoading(false);
    }
  };

  const getBuysForOrder = (orderId) => {
    return buys.filter((b) => b.order?.id === orderId);
  };

  const statusColor = (state) => {
    const colors = {
      PENDING: '#f59e0b',
      SENDED: '#3b82f6',
      IN_DISTIBUITION: '#8b5cf6',
      DELIVERED: '#10b981',
    };
    return colors[state] || '#6b7280';
  };

  if (loading) return <div className="loading">Loading orders...</div>;

  return (
    <div className="orders-page">
      <h2>My Orders</h2>
      {orders.length === 0 ? (
        <div className="empty">No orders yet. Browse books to place an order!</div>
      ) : (
        <div className="orders-list">
          {orders.map((order) => (
            <div
              key={order.id}
              className={`order-card ${selectedOrder?.id === order.id ? 'selected' : ''}`}
              onClick={() => setSelectedOrder(selectedOrder?.id === order.id ? null : order)}
            >
              <div className="order-header">
                <span className="order-id">Order #{order.id}</span>
                <span
                  className="order-status"
                  style={{ backgroundColor: statusColor(order.state) }}
                >
                  {order.state?.replace('_', ' ')}
                </span>
              </div>
              <p className="order-date">
                {new Date(order.orderDate).toLocaleDateString()}
              </p>
              {selectedOrder?.id === order.id && (
                <div className="order-details">
                  <h4>Items</h4>
                  {getBuysForOrder(order.id).length === 0 ? (
                    <p className="empty">No items in this order</p>
                  ) : (
                    <table>
                      <thead>
                        <tr>
                          <th>Book</th>
                          <th>Qty</th>
                          <th>Price</th>
                          <th>Discount</th>
                        </tr>
                      </thead>
                      <tbody>
                        {getBuysForOrder(order.id).map((buy) => (
                          <tr key={buy.id}>
                            <td>{buy.book?.title || buy.book?.isbn}</td>
                            <td>{buy.quantity}</td>
                            <td>${buy.price}</td>
                            <td>{buy.discount}%</td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  )}
                </div>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
