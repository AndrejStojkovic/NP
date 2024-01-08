// K2 14

import java.util.*;
import java.util.stream.Collectors;

class Car implements Comparable<Car> {
    String manufacturer;
    String model;
    int price;
    float power;

    Car(String manufacturer, String model, int price, float power) {
        this.manufacturer = manufacturer;
        this.model = model;
        this.price = price;
        this.power = power;
    }

    public String getModel() {
        return model;
    }

    @Override
    public int compareTo(Car o) {
        int p = Integer.compare(price, o.price);
        return p == 0 ? Float.compare(power, o.power) : p;
    }

    @Override
    public String toString() {
        return String.format("%s %s (%dKW) %d", manufacturer, model, (int)power, price);
    }
}

class CarCollection {
    List<Car> cars;

    CarCollection() {
        cars = new ArrayList<>();
    }

    public void addCar(Car car) {
        cars.add(car);
    }

    public void sortByPrice(boolean ascending) {
        if(ascending) {
            cars = cars.stream().sorted().collect(Collectors.toList());
        } else {
            cars = cars.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
        }
    }

    public List<Car> filterByManufacturer(String manufacturer) {
        return cars.stream().filter(x -> x.manufacturer.equalsIgnoreCase(manufacturer))
                .sorted(Comparator.comparing(Car::getModel))
                .collect(Collectors.toList());
    }

    public List<Car> getList() {
        return cars;
    }
}

public class CarTest {
    public static void main(String[] args) {
        CarCollection carCollection = new CarCollection();
        String manufacturer = fillCollection(carCollection);
        carCollection.sortByPrice(true);
        System.out.println("=== Sorted By Price ASC ===");
        print(carCollection.getList());
        carCollection.sortByPrice(false);
        System.out.println("=== Sorted By Price DESC ===");
        print(carCollection.getList());
        System.out.printf("=== Filtered By Manufacturer: %s ===\n", manufacturer);
        List<Car> result = carCollection.filterByManufacturer(manufacturer);
        print(result);
    }

    static void print(List<Car> cars) {
        for (Car c : cars) {
            System.out.println(c);
        }
    }

    static String fillCollection(CarCollection cc) {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            String[] parts = line.split(" ");
            if(parts.length < 4) return parts[0];
            Car car = new Car(parts[0], parts[1], Integer.parseInt(parts[2]),
                    Float.parseFloat(parts[3]));
            cc.addCar(car);
        }
        scanner.close();
        return "";
    }
}
