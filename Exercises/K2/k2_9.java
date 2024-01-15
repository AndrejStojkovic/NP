// K2 9

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.IntStream;

class SeatNotAllowedException extends Exception {
    SeatNotAllowedException() { }
}

class SeatTakenException extends Exception {
    SeatTakenException() { }
}

class Sector {
    String code;
    int seats;
    Map<Integer, Integer> map;

    Sector(String code, int seats) {
        this.code = code;
        this.seats = seats;
        this.map = new HashMap<>();
    }

    public String getCode() {
        return code;
    }

    public void buyTicket(int seat, int type) {
        map.put(seat, type);
    }

    public boolean isSeatTaken(int seat) {
        return map.containsKey(seat);
    }

    public boolean containsValue(int value) {
        return map.containsValue(value);
    }

    public int availableSeats() {
        return seats - map.size();
    }

    private double getPercent() {
        return map.size() / (double)seats * 100.0;
    }

    @Override
    public String toString() {
        return String.format("%s\t%d/%d\t%.1f%%", code, availableSeats(), seats, getPercent());
    }
}

class Stadium {
    String name;
    Map<String, Sector> sectors;

    Stadium(String name) {
        this.name = name;
        this.sectors = new HashMap<>();
    }

    public void createSectors(String [] sectorNames, int [] sizes) {
        IntStream.range(0, sectorNames.length)
                .forEach(i -> sectors.put(sectorNames[i], new Sector(sectorNames[i], sizes[i])));
    }

    public void buyTicket(String sectorName, int seat, int type) throws SeatTakenException, SeatNotAllowedException {
        if(sectors.get(sectorName).isSeatTaken(seat)) {
            throw new SeatTakenException();
        }
        if(type != 0 && sectors.get(sectorName).containsValue(((type - 1) ^ 1) + 1)) {
            throw new SeatNotAllowedException();
        }
        sectors.get(sectorName).buyTicket(seat, type);
    }

    public void showSectors() {
        sectors.values().stream()
                .sorted(Comparator.comparing(Sector::availableSeats).reversed().thenComparing(Sector::getCode))
                .forEach(System.out::println);
    }
}

public class StaduimTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        String[] sectorNames = new String[n];
        int[] sectorSizes = new int[n];
        String name = scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] parts = line.split(";");
            sectorNames[i] = parts[0];
            sectorSizes[i] = Integer.parseInt(parts[1]);
        }
        Stadium stadium = new Stadium(name);
        stadium.createSectors(sectorNames, sectorSizes);
        n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] parts = line.split(";");
            try {
                stadium.buyTicket(parts[0], Integer.parseInt(parts[1]),
                        Integer.parseInt(parts[2]));
            } catch (SeatNotAllowedException e) {
                System.out.println("SeatNotAllowedException");
            } catch (SeatTakenException e) {
                System.out.println("SeatTakenException");
            }
        }
        stadium.showSectors();
    }
}
