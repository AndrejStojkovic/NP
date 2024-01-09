// K2 36 (orderFood() needs to done)

import java.util.*;

class Base  {
    String id;
    String name;
    List<Float> orders;

    Base(String id, String name) {
        this.id = id;
        this.name = name;
        this.orders = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public int getOrders() {
        return orders.size();
    }

    public void addOrder(float cost) {
        orders.add(cost);
    }

    public double total() {
        return orders.stream().mapToDouble(x -> x).sum();
    }

    public double avg() {
        return orders.stream().mapToDouble(x -> x).average().orElse(0);
    }

    public String format(String a, String b) {
        return String.format(" Total %s: %d Total %s: %.2f Average %s: %.2f", a, orders.size(), b, total(), b, avg());
    }

    @Override
    public String toString() {
        return String.format("ID: %s Name: %s", id, name);
    }
}

class Restaurant extends Base {
    Location location;

    Restaurant(String id, String name, Location location) {
        super(id, name);
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return super.toString() + format("orders", "amount earned");
    }
}

class User extends Base {
    Map<String, Location> locations;

    User(String id, String name) {
        super(id, name);
        this.locations = new HashMap<>();
    }

    public void addAddress(String addressName, Location location) {
        locations.put(addressName, location);
    }

    public Location getLocation(String address) {
        return locations.get(address);
    }

    @Override
    public String toString() {
        return super.toString() + format("orders", "amount spent");
    }
}

class DeliveryPerson extends Base {
    Location currentLocation;

    DeliveryPerson(String id, String name, Location currentLocation) {
        super(id, name);
        this.currentLocation = currentLocation;
    }

    public void setCurrentLocation(Location newLocation) {
        currentLocation = newLocation;
    }

    public int distanceValue(Location other) {
        return (currentLocation.distance(other) / 10) * 10;
    }

    @Override
    public String toString() {
        return super.toString() + format("deliveries", "delivery fee");
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
        Restaurant restaurant = restaurants.get(restaurantId);
        restaurant.addOrder(cost);
        Location location = users.get(userId).getLocation(userAddressName);

        DeliveryPerson person = deliveryPeople.values().stream()
                .min(Comparator.comparing((DeliveryPerson x) -> x.currentLocation.distance(restaurant.getLocation()))
                .thenComparing(Base::getOrders)
                .thenComparing(Base::getId)).orElse(null);

        if(person != null) {
            person.addOrder(90 + person.distanceValue(restaurant.getLocation()));
            person.setCurrentLocation(location);
        }
    }

    public void printUsers() {
        users.values().stream()
                .sorted(Comparator.comparing(User::total).thenComparing(User::getId).reversed())
                .forEach(System.out::println);
    }

    public void printRestaurants() {
        restaurants.values().stream()
                .sorted(Comparator.comparing(Restaurant::avg).thenComparing(Restaurant::getId).reversed())
                .forEach(System.out::println);
    }

    public void printDeliveryPeople() {
        deliveryPeople.values().stream()
                .sorted(Comparator.comparing(DeliveryPerson::total).thenComparing(DeliveryPerson::getId).reversed())
                .forEach(System.out::println);
    }
}

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
