package orders;

import menu.*;

public class OrderLine {

    private MenuItem menu_item;
    private int quantity;

    public OrderLine(MenuItem item, int quantity) {
        menu_item = item; 
        this.quantity = quantity;
    }

    public static OrderLine createOrderLine(MenuItem item, int quantity) {
        return new OrderLine(item, quantity);
    }

    public MenuItem getMenuItem() { return menu_item; }
    public int getQuantity() { return quantity; }
}