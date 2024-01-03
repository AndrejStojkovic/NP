// K2 3

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

class Price {
    int price;
    int discounted;

    Price(int price, int discounted) {
        this.price = price;
        this.discounted = discounted;
    }

    public int getPercent() {
        return (int)Math.floor(100.0 - (100.0 / price * discounted));
    }

    public int getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return String.format("%2d%% %d/%d", getPercent(), discounted, price);
    }
}

class Store {
    String name;
    List<Price> prices;

    Store() {
        name = "store";
        prices = new ArrayList<>();
    }

    Store(String name, List<Price> prices) {
        this.name = name;
        this.prices = prices;
    }

    public double averageDiscount() {
        return prices.stream().mapToInt(Price::getPercent).average().orElse(0);
    }

    public int absoluteSum() {
        return prices.stream().mapToInt(x -> Math.abs(x.price - x.discounted)).sum();
    }

    @Override
    public String toString() {
        String result = prices.stream().sorted(Comparator.comparing(Price::getPercent).thenComparing(Price::getPrice).reversed())
                .map(x -> String.format("%s", x))
                .collect(Collectors.joining("\n"));
        return String.format("%s\nAverage discount: %.1f%%\nTotal discount: %d\n%s", name, averageDiscount(), absoluteSum(), result);
    }
}

class Discounts {
    List<Store> stores;

    Discounts() {
        stores = new ArrayList<>();
    }

    public int readStores(InputStream inputStream) {
        Scanner in = new Scanner(inputStream);

        while(in.hasNextLine()) {
            String line = in.nextLine();
            String [] split = line.split(" ");

            String name = split[0];
            ArrayList<Price> prices = new ArrayList<>();

            for(int i = 1; i < split.length; i++) {
                if(split[i].isEmpty()) continue;
                String [] curr = split[i].split(":");
                prices.add(new Price(Integer.parseInt(curr[1]), Integer.parseInt(curr[0])));
            }

            stores.add(new Store(name, prices));
        }

        return stores.size();
    }

    public List<Store> byAverageDiscount() {
        return stores.stream()
                .sorted(Comparator.comparing(Store::averageDiscount).reversed())
                .limit(3).collect(Collectors.toList());
    }

    public List<Store> byTotalDiscount() {
        return stores.stream()
                .sorted(Comparator.comparing(Store::absoluteSum))
                .limit(3).collect(Collectors.toList());
    }
}

public class DiscountsTest {
    public static void main(String[] args) {
        Discounts discounts = new Discounts();
        int stores = discounts.readStores(System.in);
        System.out.println("Stores read: " + stores);
        System.out.println("=== By average discount ===");
        discounts.byAverageDiscount().forEach(System.out::println);
        System.out.println("=== By total discount ===");
        discounts.byTotalDiscount().forEach(System.out::println);
    }
}
