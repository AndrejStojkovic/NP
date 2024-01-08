// K2 36 (orderFood() needs to done)

import java.util.*;

interface IOrder {
    double total();
    double avg();
}

class Restaurant implements IOrder {
    String id;
    String name;
    Location location;
    List<Float> orders;

    Restaurant(String id, String name, Location location) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.orders = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void addOrder(float cost) {
        orders.add(cost);
    }

    @Override
    public double total() {
        return orders.stream().mapToDouble(x -> x).sum();
    }

    @Override
    public double avg() {
        return orders.stream().mapToDouble(x -> x).average().orElse(0);
    }

    @Override
    public String toString() {
        String fees = String.format("Total amount earned: %.2f Average amount earned: %.2f", total(), avg());
        return String.format("ID: %s Name: %s Total orders: %d %s", id, name, orders.size(), fees);
    }
}

class User implements IOrder {
    String id;
    String name;
    Map<String, Location> locations;
    List<Float> orders;

    User(String id, String name) {
        this.id = id;
        this.name = name;
        this.locations = new HashMap<>();
        this.orders = new ArrayList<>();
    }

    public void addAddress(String addressName, Location location) {
        locations.put(addressName, location);
    }

    public String getId() {
        return id;
    }

    public Location getLocation(String address) {
        return locations.get(address);
    }

    public void addOrder(float cost) {
        orders.add(cost);
    }

    @Override
    public double total() {
        return orders.stream().mapToDouble(x -> x).sum();
    }

    @Override
    public double avg() {
        return orders.stream().mapToDouble(x -> x).average().orElse(0);
    }

    @Override
    public String toString() {
        String fees = String.format("Total amount spent: %.2f Average amount spent: %.2f", total(), avg());
        return String.format("ID: %s Name: %s Total orders: %d %s", id, name, orders.size(), fees);
    }
}

class DeliveryPerson implements IOrder {
    String id;
    String name;
    Location currentLocation;
    List<Float> orders;

    DeliveryPerson(String id, String name, Location currentLocation) {
        this.id = id;
        this.name = name;
        this.currentLocation = currentLocation;
        this.orders = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void addOrder(float cost) {
        orders.add(cost);
    }

    @Override
    public double total() {
        return orders.stream().mapToDouble(x -> x).sum();
    }

    @Override
    public double avg() {
        return orders.stream().mapToDouble(x -> x).average().orElse(0);
    }

    @Override
    public String toString() {
        String fees = String.format("Total delivery fee: %.2f Average delivery fee: %.2f", total(), avg());
        return String.format("ID: %s Name: %s Total deliveries: %d %s", id, name, orders.size(), fees);
    }
}

class DeliveryApp {
    String name;
    Map<String, Restaurant> restaurants;
    Map<String, User> users;
    Map<String, DeliveryPerson> deliveryPeople;

    DeliveryApp(String name) {
        this.name = name;
        this.restaurants = new HashMap<>();
        this.users = new HashMap<>();
        this.deliveryPeople = new HashMap<>();
    }

    public void registerDeliveryPerson(String id, String name, Location currentLocation) {
        deliveryPeople.put(id, new DeliveryPerson(id, name, currentLocation));
    }

    public void addRestaurant(String id, String name, Location location) {
        restaurants.put(id, new Restaurant(id, name, location));
    }

    public void addUser(String id, String name) {
        users.put(id, new User(id, name));
    }

    public void addAddress(String id, String addressName, Location location) {
        if(!users.containsKey(id)) {
            return;
        }
        users.get(id).addAddress(addressName, location);
    }

    public void orderFood(String userId, String userAddressName, String restaurantId, float cost) {
        users.get(userId).addOrder(cost);
        restaurants.get(restaurantId).addOrder(cost);
        Location location = users.get(userId).getLocation(userAddressName);
    }

    public void printUsers() {
        users.values().stream()
                .sorted(Comparator.comparing(User::total).thenComparing(User::getId).reversed())
                .forEach(System.out::println);
    }

    public void printRestaurants() {
        restaurants.values().stream()
                .sorted(Comparator.comparing(Restaurant::total).thenComparing(Restaurant::getId).reversed())
                .forEach(System.out::println);
    }

    public void printDeliveryPeople() {
        deliveryPeople.values().stream()
                .sorted(Comparator.comparing(DeliveryPerson::total).thenComparing(DeliveryPerson::getId).reversed())
                .forEach(System.out::println);
    }
}

/*
DO NOT MODIFY THE interfaces and classes below!!!
*/

interface Location {
    int getX();

    int getY();

    default int distance(Location other) {
        int xDiff = Math.abs(getX() - other.getX());
        int yDiff = Math.abs(getY() - other.getY());
        return xDiff + yDiff;
    }
}

class LocationCreator {
    public static Location create(int x, int y) {

        return new Location() {
            @Override
            public int getX() {
                return x;
            }

            @Override
            public int getY() {
                return y;
            }
        };
    }
}

public class DeliveryAppTester {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String appName = sc.nextLine();
        DeliveryApp app = new DeliveryApp(appName);
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split(" ");

            if (parts[0].equals("addUser")) {
                String id = parts[1];
                String name = parts[2];
                app.addUser(id, name);
            } else if (parts[0].equals("registerDeliveryPerson")) {
                String id = parts[1];
                String name = parts[2];
                int x = Integer.parseInt(parts[3]);
                int y = Integer.parseInt(parts[4]);
                app.registerDeliveryPerson(id, name, LocationCreator.create(x, y));
            } else if (parts[0].equals("addRestaurant")) {
                String id = parts[1];
                String name = parts[2];
                int x = Integer.parseInt(parts[3]);
                int y = Integer.parseInt(parts[4]);
                app.addRestaurant(id, name, LocationCreator.create(x, y));
            } else if (parts[0].equals("addAddress")) {
                String id = parts[1];
                String name = parts[2];
                int x = Integer.parseInt(parts[3]);
                int y = Integer.parseInt(parts[4]);
                app.addAddress(id, name, LocationCreator.create(x, y));
            } else if (parts[0].equals("orderFood")) {
                String userId = parts[1];
                String userAddressName = parts[2];
                String restaurantId = parts[3];
                float cost = Float.parseFloat(parts[4]);
                app.orderFood(userId, userAddressName, restaurantId, cost);
            } else if (parts[0].equals("printUsers")) {
                app.printUsers();
            } else if (parts[0].equals("printRestaurants")) {
                app.printRestaurants();
            } else {
                app.printDeliveryPeople();
            }

        }
    }
}
