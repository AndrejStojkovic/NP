// K2 10 (Partial)
// Tolku glupa zadacha za reshavanje ne sum videl do sega

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

interface ILocation{
    double getLongitude();
    double getLatitude();
    LocalDateTime getTimestamp();
}

class UserAlreadyExistException extends Exception {
    UserAlreadyExistException(String id) {
        super("User with id " + id + " already exists");
    }
}

class User implements Comparable<User> {
    String name;
    String id;
    List<ILocation> locations;
    boolean infected;
    LocalDateTime date;


    User(String name, String id) {
        this.name = name;
        this.id = id;
        this.locations = new ArrayList<>();
        this.infected = false;
        this.date = null;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getDate() {
        return date != null ? date : LocalDateTime.MAX;
    }

    public void addLocations(List<ILocation> toAdd) {
        locations.addAll(toAdd);
    }

    public void setInfected(boolean infected, LocalDateTime date) {
        this.infected = infected;
        this.date = date;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s", name, id, date);
    }

    public String hiddenUser() {
        return String.format("%s %s***", name, id.substring(0, 4));
    }

    @Override
    public int compareTo(User o) {
        return id.compareTo(o.id);
    }
}

class StopCoronaApp {
    Map<String, User> users;

    public static Comparator<User> directContactComparator = Comparator.comparing(User::getDate).thenComparing(User::getName);
    public static Comparator<User> indirectContactComparator = Comparator.comparing(User::getName).thenComparing(User::getId);

    StopCoronaApp() {
        users = new HashMap<>();
    }

    public void addUser(String name, String id) throws UserAlreadyExistException {
        if(users.containsKey(id)) {
            throw new UserAlreadyExistException(id);
        }
        users.putIfAbsent(id, new User(name, id));
    }

    public void addLocations(String id, List<ILocation> locations) {
        users.get(id).addLocations(locations);
    }

    public void detectNewCase(String id, LocalDateTime date) {
        users.get(id).setInfected(true, date);
    }

    public Map<User, Integer> getDirectContacts(User u) {
        Map<User, Integer> result = new TreeMap<>(directContactComparator);
//        Map<User, Integer> result = new TreeMap<>();
        users.keySet().stream().filter(id -> !id.equals(u.getId())).forEach(id -> {
            int res = Calculator.contactsCounter(u, users.get(id));
            if(res != 0) result.put(users.get(id), res);
        });
        return result;
    }

    public Collection<User> getIndirectContacts(User u) {
        Set<User> result = new TreeSet<>(indirectContactComparator);
        getDirectContacts(u).keySet().stream()
                .flatMap(x -> getDirectContacts(x).keySet().stream())
                .filter(x -> !result.contains(x) && !getDirectContacts(u).containsKey(x) && !x.equals(u))
                .forEach(result::add);
        return result;
    }

    public void createReport() {
        List<User> infected = users.values().stream().filter(x -> x.infected)
                .sorted(directContactComparator).collect(Collectors.toList());

        infected.forEach(x -> {
            System.out.println(x);

            System.out.println("Direct contacts:");
            getDirectContacts(x).entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .forEach(c -> System.out.println(c.getKey().hiddenUser() + " " + c.getValue()));
            System.out.println("Count of direct contacts: " + getDirectContacts(x).values().stream().mapToInt(i -> i).sum());

            System.out.println("Indirect contacts:");
            getIndirectContacts(x).forEach(c -> System.out.println(c.hiddenUser()));
            System.out.println("Count of indirect contacts: " + getIndirectContacts(x).size());
        });

        double directContacts = infected.stream().mapToInt(x -> getDirectContacts(x).values().stream().mapToInt(i -> i).sum()).sum();
        double indirectContacts = infected.stream().mapToInt(x -> getIndirectContacts(x).size()).sum();

        System.out.printf("Average direct contacts: %.4f%n", directContacts / infected.size());
        System.out.printf("Average indirect contacts: %.4f%n", indirectContacts / infected.size());
    }
}

// Ova e ukradeno od _totallynormalhuman :KEKW:
class Calculator {
    public static double distanceBetween(ILocation location1 , ILocation location2){
        return Math.sqrt(Math.pow(location1.getLatitude() - location2.getLatitude(), 2)
                        +Math.pow(location1.getLongitude() - location2.getLongitude(),2));
    }

    public static double timeBetweenInSeconds(ILocation location1, ILocation location2) {
        return Math.abs(Duration.between(location1.getTimestamp(), location2.getTimestamp()).getSeconds());
    }

    public static boolean isDangerContact(ILocation location1, ILocation location2){
        return distanceBetween(location1, location2) <= 2 && timeBetweenInSeconds(location1, location2) <= 300;
    }

    public static int contactsCounter(User u1, User u2){
        int ct = 0;
        for(ILocation iLocation : u1.locations){
            for (ILocation iLocation1 : u2.locations){
                if (isDangerContact(iLocation1, iLocation)) ct++;
            }
        }
        return ct;
    }
}

public class StopCoronaTest {
    public static double timeBetweenInSeconds(ILocation location1, ILocation location2) {
        return Math.abs(Duration.between(location1.getTimestamp(), location2.getTimestamp()).getSeconds());
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        StopCoronaApp stopCoronaApp = new StopCoronaApp();

        while (sc.hasNext()) {
            String line = sc.nextLine();
            String[] parts = line.split("\\s+");

            switch (parts[0]) {
                case "REG": //register
                    String name = parts[1];
                    String id = parts[2];
                    try {
                        stopCoronaApp.addUser(name, id);
                    } catch (UserAlreadyExistException e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case "LOC": //add locations
                    id = parts[1];
                    List<ILocation> locations = new ArrayList<>();
                    for (int i = 2; i < parts.length; i += 3) {
                        locations.add(createLocationObject(parts[i], parts[i + 1], parts[i + 2]));
                    }
                    stopCoronaApp.addLocations(id, locations);

                    break;
                case "DET": //detect new cases
                    id = parts[1];
                    LocalDateTime timestamp = LocalDateTime.parse(parts[2]);
                    stopCoronaApp.detectNewCase(id, timestamp);

                    break;
                case "REP": //print report
                    stopCoronaApp.createReport();
                    break;
                default:
                    break;
            }
        }
    }

    private static ILocation createLocationObject(String lon, String lat, String timestamp) {
        return new ILocation() {
            @Override
            public double getLongitude() {
                return Double.parseDouble(lon);
            }

            @Override
            public double getLatitude() {
                return Double.parseDouble(lat);
            }

            @Override
            public LocalDateTime getTimestamp() {
                return LocalDateTime.parse(timestamp);
            }
        };
    }
}
