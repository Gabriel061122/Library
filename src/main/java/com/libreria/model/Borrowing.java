package com.libreria.model;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class Borrowing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(mappedBy = "borrowing", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(mappedBy = "borrowing", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Book book;


    private Date borrowingDate;
    private Date returnDate;

    public Borrowing(User user, Book book, Date borrowingDate, Date returnDate) {
        this.user = user;
        this.book = book;
        this.borrowingDate = borrowingDate;
        this.returnDate = returnDate;
    }

    @Override
    public boolean equals(Object o) {
        boolean isEqual = false;
        if (o instanceof Borrowing) {
            isEqual = this.id.equals(((Borrowing) o).id);
        }
        return isEqual;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}