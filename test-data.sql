CREATE DATABASE IF NOT EXISTS library;
USE library;

SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE borrowing;
TRUNCATE TABLE buy;
TRUNCATE TABLE orders;
TRUNCATE TABLE borrowing_copy;
TRUNCATE TABLE book_genre;
TRUNCATE TABLE user_user_types;
TRUNCATE TABLE genre;
TRUNCATE TABLE user_type;
TRUNCATE TABLE users;
TRUNCATE TABLE book;
SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO user_type (id, type) VALUES
  (1, 'CUSTOMER'),
  (2, 'LIBRARIAN'),
  (3, 'ADMIN');

INSERT INTO users (id, email, name, password, phone, address, city, state, country, postal_code) VALUES
  (1, 'ana@example.com', 'Ana Garcia', 'password-claro-para-pruebas', '600111222', 'Calle Mayor 1', 'Madrid', 'Madrid', 'Spain', '28001'),
  (2, 'bibliotecario@example.com', 'Luis Biblioteca', 'password-claro-para-pruebas', '600333444', 'Avenida Libros 10', 'Sevilla', 'Sevilla', 'Spain', '41001');

INSERT INTO user_user_types (user_id, user_type_id) VALUES
  (1, 1),
  (2, 1),
  (2, 2);

INSERT INTO genre (id, name) VALUES
  (1, 'Fiction'),
  (2, 'Science'),
  (3, 'History');

INSERT INTO book (isbn, title, author, price, stock) VALUES
  ('9780000000001', 'El Archivo Perdido', 'Clara Montes', 1999, 5),
  ('9780000000002', 'Fisica para Lectores', 'Mario Newton', 2499, 2),
  ('9780000000003', 'Historia del Papel', 'Elena Ruiz', 1599, 0);

INSERT INTO book_genre (isbn, genre_id) VALUES
  ('9780000000001', 1),
  ('9780000000002', 2),
  ('9780000000003', 3),
  ('9780000000003', 1);

INSERT INTO borrowing_copy (id, book_id, avlbl) VALUES
  (1, '9780000000001', 'AVALIABLE'),
  (2, '9780000000001', 'RESERVED'),
  (3, '9780000000002', 'AVALIABLE'),
  (4, '9780000000003', 'NOT_AVALIABLE');

INSERT INTO orders (id, user_id, order_date, state) VALUES
  (1, 1, '2026-06-01 10:00:00', 'PENDING'),
  (2, 1, '2026-06-02 10:00:00', 'DELIVERED');

INSERT INTO buy (id, book_id, quantity, price, discount, order_id) VALUES
  (1, '9780000000001', 1, 1999, 0, 1),
  (2, '9780000000002', 2, 2499, 200, 1),
  (3, '9780000000003', 1, 1599, 100, 2);

INSERT INTO borrowing (id, user_id, copy_id, borrowing_date, return_date) VALUES
  (1, 1, 1, '2026-06-01', '2026-06-15'),
  (2, 2, 3, '2026-06-02', '2026-06-16');
