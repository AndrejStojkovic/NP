// K2 20

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.*;

enum TemperatureType {
    CELSIUS,
    FAHRENHEIT
}

class TemperatureStats implements Comparable<TemperatureStats> {
    int day;
    List<Double> temperatures;
    TemperatureType type;

    TemperatureStats(int day, List<Double> temperatures, TemperatureType type) {
        this.day = day;
        this.temperatures = temperatures;
        this.type = type;
    }

    public int count() {
        return temperatures.size();
    }

    public double min(TemperatureType type) {
        return convert(temperatures.stream().mapToDouble(x -> x).min().orElse(0), type);
    }

    public double max(TemperatureType type) {
        return convert(temperatures.stream().mapToDouble(x -> x).max().orElse(0), type);
    }

    public double avg(TemperatureType type) {
        return convert(temperatures.stream().mapToDouble(x -> x).average().orElse(0), type);
    }

    public double convert(double val, TemperatureType type) {
        if(this.type == type) {
            return val;
        }
        return type == TemperatureType.CELSIUS ? ((val - 32) * 5) / 9 : (val * 9) / 5 + 32;
    }

    @Override
    public int compareTo(TemperatureStats o) {
        return Integer.compare(day, o.day);
    }

    public String toString(TemperatureType type) {
        String c = type == TemperatureType.CELSIUS ? "C " : "F ";
        return String.format("%3d", day) + ": Count: " + String.format("%3d ", count()) +
                "Min: " + String.format("%6.2f", min(type)) + c +
                "Max: " + String.format("%6.2f", max(type)) + c +
                "Avg: " + String.format("%6.2f", avg(type)) + c;
    }
}
class DailyTemperatures {
    Set<TemperatureStats> stats;

    DailyTemperatures() {
        stats = new HashSet<>();
    }

    public void readTemperatures(InputStream inputStream) {
        Scanner scanner = new Scanner(inputStream);

        while(scanner.hasNextLine()) {
            String [] line = scanner.nextLine().split(" ");

            int day = Integer.parseInt(line[0]);
            List<Double> temps = new ArrayList<>();
            TemperatureType type = line[1].charAt(line[1].length() - 1) == 'C' ? TemperatureType.CELSIUS : TemperatureType.FAHRENHEIT;

            for(int i = 1; i < line.length; i++) {
                temps.add(Double.parseDouble(line[i].substring(0, line[i].length() - 1)));
            }

            stats.add(new TemperatureStats(day, temps, type));
        }
    }

    public void writeDailyStats(OutputStream outputStream, char scale) {
        PrintWriter pw = new PrintWriter(outputStream);
        stats.stream().sorted().forEach(x -> pw.println(x.toString(scale == 'C' ? TemperatureType.CELSIUS : TemperatureType.FAHRENHEIT)));
        pw.flush();
    }
}

public class DailyTemperatureTest {
    public static void main(String[] args) {
        DailyTemperatures dailyTemperatures = new DailyTemperatures();
        dailyTemperatures.readTemperatures(System.in);
        System.out.println("=== Daily temperatures in Celsius (C) ===");
        dailyTemperatures.writeDailyStats(System.out, 'C');
        System.out.println("=== Daily temperatures in Fahrenheit (F) ===");
        dailyTemperatures.writeDailyStats(System.out, 'F');
    }
}
