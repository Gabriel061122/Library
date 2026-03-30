package com.libreria.model;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Buy {
    private Long id;
    private User user;
    private Book book;
    private Date buyDate;
    private int quantity;
    private int price;
    private int discount;


    public Buy(User user, Book book, Date buyDate, int quantity, int price, int discount) {
        this.user = user;
        this.book = book;
        this.buyDate = buyDate;
        this.quantity = quantity;
        this.price = price;
        this.discount = discount;
    }

    @Override
    public boolean equals(Object o) {
        boolean isEqual = false;
        if (o instanceof Buy) {
            isEqual = this.id.equals(((Buy) o).id);
        }
        return isEqual;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public int getTotalPrice() {
        return price * quantity - getTotalDiscount();
    }

    public int getTotalDiscount() {
        return discount * quantity;
    }

    public int getTotalPriceWithoutDiscount() {
        return price * quantity;
    }
}