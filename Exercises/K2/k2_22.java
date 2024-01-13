// K2 22

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class WrongDateException extends Exception {
    WrongDateException(Date date) {
        super("Wrong date: " + date);
    }
}

class Event implements Comparable<Event> {
    String name;
    String location;
    Date date;

    Event(String name, String location, Date date) {
        this.name = name;
        this.location = location;
        this.date = date;
    }

    public int getMonth() {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.MONTH);
    }

    @Override
    public int compareTo(Event o) {
        int d = date.compareTo(o.date);
        return d != 0 ? d : name.compareTo(o.name);
    }

    @Override
    public String toString() {
        DateFormat dateFormat = new SimpleDateFormat("dd MMM, yyyy HH:mm");
        return String.format("%s at %s, %s", dateFormat.format(date), location, name);
    }
}

class EventCalendar {
    int year;
    Set<Event> events;

    EventCalendar(int year) {
        this.year = year;
        events = new TreeSet<>();
    }

    public void addEvent(String name, String location, Date date) throws WrongDateException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if(calendar.get(Calendar.YEAR) != year) {
            throw new WrongDateException(date);
        }
        events.add(new Event(name, location, date));
    }

    public void listEvents(Date date) {
        List<Event> filtered = events.stream().filter(x -> compareDates(x.date, date)).collect(Collectors.toList());
        if(filtered.isEmpty()) {
            System.out.println("No events on this day!");
            return;
        }
        filtered.forEach(System.out::println);
    }

    public void listByMonth() {
        IntStream.range(0, 12).forEach(i -> {
                long count = events.stream().filter(x -> x.getMonth() == i).count();
                System.out.printf("%d : %d%n", (i + 1), count);
            });
    }

    public boolean compareDates(Date date1, Date date2) {
        Calendar a = Calendar.getInstance(), b = Calendar.getInstance();
        a.setTime(date1);
        b.setTime(date2);

        return a.get(Calendar.DAY_OF_MONTH) == b.get(Calendar.DAY_OF_MONTH) &&
                a.get(Calendar.MONTH) == b.get(Calendar.MONTH) &&
                a.get(Calendar.YEAR) == b.get(Calendar.YEAR);
    }
}

public class EventCalendarTest {
    public static void main(String[] args) throws ParseException {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        int year = scanner.nextInt();
        scanner.nextLine();
        EventCalendar eventCalendar = new EventCalendar(year);
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] parts = line.split(";");
            String name = parts[0];
            String location = parts[1];
            Date date = df.parse(parts[2]);
            try {
                eventCalendar.addEvent(name, location, date);
            } catch (WrongDateException e) {
                System.out.println(e.getMessage());
            }
        }
        Date date = df.parse(scanner.nextLine());
        eventCalendar.listEvents(date);
        eventCalendar.listByMonth();
    }
}
