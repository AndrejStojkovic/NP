// K2 25

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

enum COMPARATOR_TYPE {
    NEWEST_FIRST,
    OLDEST_FIRST,
    LOWEST_PRICE_FIRST,
    HIGHEST_PRICE_FIRST,
    MOST_SOLD_FIRST,
    LEAST_SOLD_FIRST
}

class ProductNotFoundException extends Exception {
    ProductNotFoundException(String message) {
        super(message);
    }
}

class Product {
    String id;
    String name;
    LocalDateTime createdAt;
    double price;
    int quantitySold;

    Product(String id, String name, LocalDateTime createdAt, double price) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.price = price;
        this.quantitySold = 0;
    }

    public void sell(int quantity) {
        quantitySold += quantity;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantitySold() {
        return quantitySold;
    }

    public static Comparator<Product> productComparator(COMPARATOR_TYPE type){
        switch(type) {
            case OLDEST_FIRST:
                return Comparator.comparing(Product::getCreatedAt);
            case NEWEST_FIRST:
                return Comparator.comparing(Product::getCreatedAt).reversed();
            case LOWEST_PRICE_FIRST:
                return Comparator.comparing(Product::getPrice);
            case HIGHEST_PRICE_FIRST:
                return Comparator.comparing(Product::getPrice).reversed();
            case LEAST_SOLD_FIRST:
                return Comparator.comparing(Product::getQuantitySold);
            case MOST_SOLD_FIRST:
                return Comparator.comparing(Product::getQuantitySold).reversed();
        }
        return productComparator(COMPARATOR_TYPE.NEWEST_FIRST);
    }

    @Override
    public String toString() {
        String df = new DecimalFormat("#.##").format(price);
        return String.format("Product{id='%s', name='%s', createdAt=%s, price=%s, quantitySold=%d}", id, name, createdAt, df, quantitySold);
    }
}

class OnlineShop {
    Map<String, Product> allProducts;
    Map<String, Set<Product>> products;

    OnlineShop() {
        allProducts = new HashMap<>();
        products = new HashMap<>();
    }

    void addProduct(String category, String id, String name, LocalDateTime createdAt, double price) {
        Product product = new Product(id, name, createdAt, price);
        products.computeIfAbsent(category, x -> new HashSet<>());
        products.computeIfPresent(category, (k, v) -> { v.add(product); return v; });
        allProducts.putIfAbsent(id, product);
    }

    double buyProduct(String id, int quantity) throws ProductNotFoundException {
        Product product = allProducts.get(id);

        if(product == null) {
            throw new ProductNotFoundException("Product with id " + id + " does not exist in the online shop!");
        }

        product.sell(quantity);
        return product.price * quantity;
    }

    List<List<Product>> listProducts(String category, COMPARATOR_TYPE comparatorType, int pageSize) {
        List<List<Product>> result = new ArrayList<>();
        result.add(new ArrayList<>());
        List<Product> sortedProducts;

        if(category == null) {
            sortedProducts = allProducts.values().stream()
                    .sorted(Product.productComparator(comparatorType))
                    .collect(Collectors.toList());
        } else {
            sortedProducts = products.get(category).stream()
                    .sorted(Product.productComparator(comparatorType))
                    .collect(Collectors.toList());
        }

        int idx = 0;
        for(Product p : sortedProducts) {
            if(result.get(idx).size() == pageSize) {
                result.add(new ArrayList<>());
                idx++;
            }
            result.get(idx).add(p);
        }

        return result;
    }
}

public class OnlineShopTest {
    public static void main(String[] args) {
        OnlineShop onlineShop = new OnlineShop();
        double totalAmount = 0.0;
        Scanner sc = new Scanner(System.in);
        String line;
        while (sc.hasNextLine()) {
            line = sc.nextLine();
            String[] parts = line.split("\\s+");
            if (parts[0].equalsIgnoreCase("addproduct")) {
                String category = parts[1];
                String id = parts[2];
                String name = parts[3];
                LocalDateTime createdAt = LocalDateTime.parse(parts[4]);
                double price = Double.parseDouble(parts[5]);
                onlineShop.addProduct(category, id, name, createdAt, price);
            } else if (parts[0].equalsIgnoreCase("buyproduct")) {
                String id = parts[1];
                int quantity = Integer.parseInt(parts[2]);
                try {
                    totalAmount += onlineShop.buyProduct(id, quantity);
                } catch (ProductNotFoundException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                String category = parts[1];
                if (category.equalsIgnoreCase("null"))
                    category=null;
                String comparatorString = parts[2];
                int pageSize = Integer.parseInt(parts[3]);
                COMPARATOR_TYPE comparatorType = COMPARATOR_TYPE.valueOf(comparatorString);
                printPages(onlineShop.listProducts(category, comparatorType, pageSize));
            }
        }
        System.out.println("Total revenue of the online shop is: " + totalAmount);

    }

    private static void printPages(List<List<Product>> listProducts) {
        for (int i = 0; i < listProducts.size(); i++) {
            System.out.println("PAGE " + (i + 1));
            listProducts.get(i).forEach(System.out::println);
        }
    }
}

