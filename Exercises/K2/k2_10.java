// K2 10 (In progress)

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

class UserAlreadyExistException extends Exception {
    UserAlreadyExistException(String name) {
        super("User " + name + " already exists!");
    }
}

interface ILocation{
    double getLongitude();

    double getLatitude();

    LocalDateTime getTimestamp();
}

class User {
    String name;
    String id;
    List<ILocation> locations;
    LocalDateTime timestampDetected;

    User(String name, String id) {
        this.name = name;
        this.id = id;
        timestampDetected = null;
    }

    public void addLocations(List<ILocation> locations) {
        this.locations.addAll(locations);
    }

    public void register(LocalDateTime timestamp) {
        timestampDetected = timestamp;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}

class StopCoronaApp {
    List<User> users;

    StopCoronaApp() {
        users = new ArrayList<>();
    }

    public void addUser(String name, String id) throws UserAlreadyExistException {
        User user = users.stream().filter(x -> x.id.equals(id)).findFirst().orElse(null);
        if(user != null) {
            throw new UserAlreadyExistException(user.name);
        }
        users.add(new User(name, id));
    }

    public void addLocations(String id, List<ILocation> iLocations) {
        users.stream().filter(x -> x.id.equals(id)).findFirst().ifPresent(user -> user.addLocations(iLocations));
    }

    public void detectNewCase(String id, LocalDateTime timestamp) {
        users.stream().filter(x -> x.id.equals(id)).findFirst().ifPresent(user -> user.register(timestamp));
    }

    public Map<User, Integer> getDirectContacts(User u) {
        // TO-DO
        return null;
    }

    public Collection<User> getIndirectContacts(User u) {
        // TO-DO
        return null;
    }

    public void createReport() {
        // TO-DO
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
