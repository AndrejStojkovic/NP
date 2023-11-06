// Lab 3.1

import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

class InvalidExtraTypeException extends Exception { }

class InvalidPizzaTypeException extends Exception { }

class ItemOutOfStockException extends Exception {
    ItemOutOfStockException(Item item) {
        super(String.format("%s out of stock!", item));
    }
}

class EmptyOrder extends Exception { }

class OrderLockedException extends Exception { }

interface Item {
    public int getPrice();
    public String getType();
}

class ExtraItem implements Item {
    private String type;

    ExtraItem(String type) throws InvalidExtraTypeException {
        if(!Objects.equals(type, "Coke") && !Objects.equals(type, "Ketchup")) {
            throw new InvalidExtraTypeException();
        }
        this.type = type;
    }

    @Override
    public int getPrice() {
        if(Objects.equals(type, "Ketchup")) {
            return 3;
        } else if(Objects.equals(type, "Coke")) {
            return 5;
        }
        return 0;
    }

    @Override
    public String getType() {
        return type;
    }
}

class PizzaItem implements Item {
    private String type;

    PizzaItem(String type) throws InvalidPizzaTypeException {
        if(!Objects.equals(type, "Standard") && !Objects.equals(type, "Pepperoni") && !Objects.equals(type, "Vegetarian")) {
            throw new InvalidPizzaTypeException();
        }
        this.type = type;
    }

    @Override
    public int getPrice() {
        if(Objects.equals(type, "Standard")) {
            return 10;
        } else if(Objects.equals(type, "Pepperoni")) {
            return 12;
        } else if(Objects.equals(type, "Vegetarian")) {
            return 8;
        }
        return 0;
    }

    @Override
    public String getType() {
        return type;
    }
}

class Order {
    private Item [] items;
    private int [] quantity;
    private boolean isLocked;

    Order() {
        items = new Item[0];
        quantity = new int[0];
        isLocked = false;
    }

    public void addItem(Item item, int count) throws ItemOutOfStockException, OrderLockedException {
        if(isLocked) {
            throw new OrderLockedException();
        }

        if(count > 10) {
            throw new ItemOutOfStockException(item);
        }

        for(int i = 0; i < items.length; i++) {
            if(Objects.equals(item.getType(), items[i].getType())) {
                quantity[i] = count;
                return;
            }
        }

        Item [] newItems = new Item[items.length + 1];
        int [] newQuantity = new int[items.length + 1];

        for(int i = 0; i < items.length; i++) {
            newItems[i] = items[i];
            newQuantity[i] = quantity[i];
        }

        newItems[newItems.length - 1] = item;
        newQuantity[newItems.length - 1] = count;
        items = newItems;
        quantity = newQuantity;
    }

    public int getPrice() {
        int sum = 0;
        for(int i = 0; i < items.length; i++) {
            sum += (items[i].getPrice() * quantity[i]);
        }
        return sum;
    }

    public void displayOrder() {
        StringBuilder str = new StringBuilder();
        for(int i = 0; i < items.length; i++) {
            str.append(String.format("%3d.%-15sx%2d%5d$\n", i + 1, items[i].getType(), quantity[i], items[i].getPrice() * quantity[i]));
        }
        str.append(String.format("%-22s%5d$", "Total:", getPrice()));
        System.out.println(str.toString());
    }

    public void removeItem(int idx) throws OrderLockedException {
        if(idx < 0 || idx >= items.length) {
            throw new ArrayIndexOutOfBoundsException(idx);
        }

        if(isLocked) {
            throw new OrderLockedException();
        }

        Item [] newItems = new Item[items.length - 1];
        int [] newQuantity = new int[items.length - 1];

        int ct = 0;
        for(int i = 0; i < items.length; i++) {
            if(i == idx) { continue; }
            newItems[ct] = items[i];
            newQuantity[ct] = quantity[i];
            ct++;
        }

        items = newItems;
        quantity = newQuantity;
    }

    public void lock() throws EmptyOrder {
        if(items.length <= 0) {
            throw new EmptyOrder();
        }
        isLocked = true;
    }
}

public class PizzaOrderTest {
    public static void main(String[] args) {
        Scanner jin = new Scanner(System.in);
        int k = jin.nextInt();
        if (k == 0) { //test Item
            try {
                String type = jin.next();
                String name = jin.next();
                Item item = null;
                if (type.equals("Pizza")) item = new PizzaItem(name);
                else item = new ExtraItem(name);
                System.out.println(item.getPrice());
            } catch (Exception e) {
                System.out.println(e.getClass().getSimpleName());
            }
        }
        if (k == 1) { // test simple order
            Order order = new Order();
            while (true) {
                try {
                    String type = jin.next();
                    String name = jin.next();
                    Item item = null;
                    if (type.equals("Pizza")) item = new PizzaItem(name);
                    else item = new ExtraItem(name);
                    if (!jin.hasNextInt()) break;
                    order.addItem(item, jin.nextInt());
                } catch (Exception e) {
                    System.out.println(e.getClass().getSimpleName());
                }
            }
            jin.next();
            System.out.println(order.getPrice());
            order.displayOrder();
            while (true) {
                try {
                    String type = jin.next();
                    String name = jin.next();
                    Item item = null;
                    if (type.equals("Pizza")) item = new PizzaItem(name);
                    else item = new ExtraItem(name);
                    if (!jin.hasNextInt()) break;
                    order.addItem(item, jin.nextInt());
                } catch (Exception e) {
                    System.out.println(e.getClass().getSimpleName());
                }
            }
            System.out.println(order.getPrice());
            order.displayOrder();
        }
        if (k == 2) { // test order with removing
            Order order = new Order();
            while (true) {
                try {
                    String type = jin.next();
                    String name = jin.next();
                    Item item = null;
                    if (type.equals("Pizza")) item = new PizzaItem(name);
                    else item = new ExtraItem(name);
                    if (!jin.hasNextInt()) break;
                    order.addItem(item, jin.nextInt());
                } catch (Exception e) {
                    System.out.println(e.getClass().getSimpleName());
                }
            }
            jin.next();
            System.out.println(order.getPrice());
            order.displayOrder();
            while (jin.hasNextInt()) {
                try {
                    int idx = jin.nextInt();
                    order.removeItem(idx);
                } catch (Exception e) {
                    System.out.println(e.getClass().getSimpleName());
                }
            }
            System.out.println(order.getPrice());
            order.displayOrder();
        }
        if (k == 3) { //test locking & exceptions
            Order order = new Order();
            try {
                order.lock();
            } catch (Exception e) {
                System.out.println(e.getClass().getSimpleName());
            }
            try {
                order.addItem(new ExtraItem("Coke"), 1);
            } catch (Exception e) {
                System.out.println(e.getClass().getSimpleName());
            }
            try {
                order.lock();
            } catch (Exception e) {
                System.out.println(e.getClass().getSimpleName());
            }
            try {
                order.removeItem(0);
            } catch (Exception e) {
                System.out.println(e.getClass().getSimpleName());
            }
        }
    }
}
