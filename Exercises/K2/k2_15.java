// K2 15

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

enum FlightType {
    ARRIVING,
    DEPARTING
}

class Flight {
    String from;
    String to;
    LocalTime time;
    int duration;
    FlightType type;

    public static Comparator<Flight> COMPARATOR = Comparator.comparing(Flight::flightTo)
                    .thenComparing(Flight::getTime).thenComparing(Flight::getDuration);

    Flight(String from, String to, int time, int duration, FlightType type) {
        this.from = from;
        this.to = to;
        this.time = LocalTime.of(0, 0, 0);
        this.time = this.time.plusMinutes(time);
        this.duration = duration;
        this.type = type;
    }

    public String flightTo() {
        return to;
    }

    public LocalTime getTime() {
        return time;
    }

    public int getDuration() {
        return duration;
    }

    public FlightType getType() {
        return type;
    }

    public static String formatTime(LocalTime time, int duration) {
        StringBuilder str = new StringBuilder();
        int total = time.getHour() * 60 + time.getMinute() + duration;
        if(total >= 1440) {
            str.append("+").append(total / 1440).append("d ");
        }
        int hours = duration / 60;
        str.append(String.format("%dh%02dm", hours, duration - (hours * 60)));
        return str.toString();
    }

    @Override
    public String toString() {
        return String.format("%s-%s %s-%s %s", from, to, time, time.plusMinutes(duration), formatTime(time, duration));
    }
}

class Airport {
    String name;
    String country;
    String code;
    int passengers;
    Set<Flight> flights;

    Airport(String name, String country, String code, int passengers) {
        this.name = name;
        this.country = country;
        this.code = code;
        this.passengers = passengers;
        flights = new TreeSet<>(Flight.COMPARATOR);
    }

    public void addFlight(String from, String to, int time, int duration, FlightType type) {
        flights.add(new Flight(from, to, time, duration, type));
    }

    @Override
    public String toString() {
        return String.format("%s (%s)\n%s\n%d", name, code, country, passengers);
    }
}

class Airports {
    Map<String, Airport> airports;

    Airports() {
        airports = new HashMap<>();
    }

    public void addAirport(String name, String country, String code, int passengers) {
        airports.put(code, new Airport(name, country, code, passengers));
    }

    public void addFlights(String from, String to, int time, int duration) {
        airports.get(from).addFlight(from, to, time, duration, FlightType.DEPARTING);
        airports.get(to).addFlight(from, to, time, duration, FlightType.ARRIVING);
    }

    public void showFlightsFromAirport(String code) {
        Airport airport = airports.get(code);

        System.out.println(airport);
        List<Flight> flights = airport.flights.stream()
                .filter(x -> x.getType() == FlightType.DEPARTING)
                .collect(Collectors.toList());
        IntStream.range(0, flights.size()).forEach(i -> System.out.println((i + 1) + ". " + flights.get(i)));
    }

    public void showDirectFlightsFromTo(String from, String to) {
        Airport airport = airports.get(from);

        List<Flight> flights = airport.flights.stream()
                .filter(x -> x.flightTo().equals(to))
                .collect(Collectors.toList());

        if(flights.isEmpty()) {
            System.out.println("No flights from " + from + " to " + to);
            return;
        }

        flights.forEach(System.out::println);
    }

    public void showDirectFlightsTo(String to) {
        airports.get(to).flights.stream()
                .filter(x -> x.getType().equals(FlightType.ARRIVING))
                .forEach(System.out::println);
    }
}

public class AirportsTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Airports airports = new Airports();
        int n = scanner.nextInt();
        scanner.nextLine();
        String[] codes = new String[n];
        for (int i = 0; i < n; ++i) {
            String al = scanner.nextLine();
            String[] parts = al.split(";");
            airports.addAirport(parts[0], parts[1], parts[2], Integer.parseInt(parts[3]));
            codes[i] = parts[2];
        }
        int nn = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < nn; ++i) {
            String fl = scanner.nextLine();
            String[] parts = fl.split(";");
            airports.addFlights(parts[0], parts[1], Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
        }
        int f = scanner.nextInt();
        int t = scanner.nextInt();
        String from = codes[f];
        String to = codes[t];
        System.out.printf("===== FLIGHTS FROM %S =====\n", from);
        airports.showFlightsFromAirport(from);
        System.out.printf("===== DIRECT FLIGHTS FROM %S TO %S =====\n", from, to);
        airports.showDirectFlightsFromTo(from, to);
        t += 5;
        t = t % n;
        to = codes[t];
        System.out.printf("===== DIRECT FLIGHTS TO %S =====\n", to);
        airports.showDirectFlightsTo(to);
    }
}
