// K1 25

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class InvalidOperationException extends Exception {
    InvalidOperationException(String text) {
        super(text);
    }
}

class Item implements Comparable<Item> {
    int id;
    String name;
    String type;
    double price;
    double quantity;

    Item() {
        id = 0;
        name = "item";
        type = "none";
        price = 0;
        quantity = 0.0;
    }

    Item(int id, String name, String type, double price, double quantity) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.price = price;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public double getPrice() {
        return price;
    }

    public double getQuantity() {
        return quantity;
    }

    public Double getTotalPrice() {
        if(type.equals("WS")) {
            return quantity * price;
        }
        return (quantity / 1000.0) * price;
    }

    public void discount() {
        price = price - (price * 0.1);
    }

    public Double getDiscount() {
        return price - price * 0.1f;
    }

    @Override
    public int compareTo(Item o) {
        return o.getTotalPrice().compareTo(getTotalPrice());
    }

    @Override
    public String toString() {
        return String.format("%d - %.2f", id, getTotalPrice());
    }
}

class ShoppingCart {
    ArrayList<Item> items;

    ShoppingCart() {
        items = new ArrayList<>();
    }

    public void addItem(String itemData) throws InvalidOperationException {
        String [] data = itemData.split(";");

        int id = Integer.parseInt(data[1]);
        String itemName = data[2];
        int price = Integer.parseInt(data[3]);
        double quantity = Double.parseDouble(data[4]);

        if(quantity == 0) {
            throw new InvalidOperationException(String.format("The quantity of the product with id %d can not be 0.", id));
        }

        items.add(new Item(id, itemName, data[0], price, quantity));
    }

    public void printShoppingCart(OutputStream outputStream) {
        PrintWriter pw = new PrintWriter(outputStream);
        items.stream().sorted(Item::compareTo).forEach(pw::println);
        pw.flush();
    }

    public void blackFridayOffer(List<Integer> discountItems, OutputStream outputStream) throws InvalidOperationException {
        if(discountItems.isEmpty()) {
            throw new InvalidOperationException("There are no products with discount.");
        }

        PrintWriter pw = new PrintWriter(outputStream);
        ArrayList<Item> discounted = new ArrayList<>();

        for(Item item : items) {
            for(Integer discountItem : discountItems) {
                if (item.getId() == discountItem) {
                    discounted.add(item);
                }
            }
        }

        for(Item item : discounted) {
            double original = item.getTotalPrice();
            item.discount();
            pw.println(String.format("%s - %.2f", item.getId(), original - item.getTotalPrice()));
        }

        pw.flush();
    }
}

public class ShoppingTest {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ShoppingCart cart = new ShoppingCart();

        int items = Integer.parseInt(sc.nextLine());
        for (int i = 0; i < items; i++) {
            try {
                cart.addItem(sc.nextLine());
            } catch (InvalidOperationException e) {
                System.out.println(e.getMessage());
            }
        }

        List<Integer> discountItems = new ArrayList<>();
        int discountItemsCount = Integer.parseInt(sc.nextLine());
        for (int i = 0; i < discountItemsCount; i++) {
            discountItems.add(Integer.parseInt(sc.nextLine()));
        }

        int testCase = Integer.parseInt(sc.nextLine());
        if (testCase == 1) {
            cart.printShoppingCart(System.out);
        } else if (testCase == 2) {
            try {
                cart.blackFridayOffer(discountItems, System.out);
            } catch (InvalidOperationException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("Invalid test case");
        }
    }
}
