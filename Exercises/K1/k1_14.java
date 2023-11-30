// K1 14

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

class WeatherDay implements Comparable<WeatherDay> {
    float temperature;
    float humidity;
    float wind;
    float visibility;
    Date date;

    WeatherDay() {
        temperature = humidity = wind = visibility = 0;
        date = new Date();
    }

    WeatherDay(float temperature, float humidity, float wind, float visibility, Date date) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.wind = wind;
        this.visibility = visibility;
        this.date = date;
    }

    public float getTemperature() {
        return temperature;
    }

    public float getHumidity() {
        return humidity;
    }

    public float getWind() {
        return wind;
    }

    public float getVisibility() {
        return visibility;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public String toString() {
        String dateString = date.toString();
        dateString = dateString.replace("UTC", "GMT");
        return String.format("%.1f %.1f km/h %.1f%% %.1f km %s", temperature, humidity, wind, visibility, dateString);
    }

    @Override
    public int compareTo(WeatherDay o) {
        return date.compareTo(o.getDate());
    }
}

class WeatherStation {
    int n;
    ArrayList<WeatherDay> list;
    public static int ms = 86400000;

    WeatherStation() {
        n = 0;
        list = new ArrayList<>();
    }

    WeatherStation(int n) {
        this.n = n;
        this.list = new ArrayList<>();
    }

    public void addMeasurment(float temperature, float wind, float humidity, float visibility, Date date) {
        WeatherDay w = new WeatherDay(temperature, wind, humidity, visibility, date);

        if(list.isEmpty()) {
            list.add(w);
            return;
        }

        Calendar now = Calendar.getInstance();
        now.setTime(date);

        Calendar lastTime = Calendar.getInstance();
        lastTime.setTime(list.get(list.size() - 1).getDate());

        if(Math.abs(now.getTimeInMillis() - lastTime.getTimeInMillis()) < 2.5 * 60 * 1000) {
            return;
        }

        list.add(w);

        ArrayList<WeatherDay> toRemove = new ArrayList<>();
        for(WeatherDay day : list) {
            Calendar c = Calendar.getInstance();
            c.setTime(day.getDate());

            if(Math.abs(now.getTimeInMillis() - c.getTimeInMillis()) > (long)n * ms) {
                toRemove.add(day);
            }
        }

        list.removeAll(toRemove);
    }

    public int total() {
        return list.size();
    }

    public void status(Date from, Date to) {
        ArrayList<WeatherDay> arr = new ArrayList<>();

        double avg = 0.0;
        int ct = 0;
        for(WeatherDay w : list) {
            Date d = w.getDate();
            if((d.after(from) || d.equals(from)) && (d.before(to) || d.equals(to))) {
                arr.add(w);
                avg += w.getTemperature();
                ct++;
            }
        }

        if(arr.isEmpty()) {
            throw new RuntimeException();
        }

        StringBuilder str = new StringBuilder();
        arr.forEach(x -> str.append(x).append("\n"));
        System.out.print(str.toString());
        System.out.printf("Average temperature: %.2f\n", avg / ct);
    }
}

public class WeatherStationTest {
    public static void main(String[] args) throws ParseException {
        Scanner scanner = new Scanner(System.in);
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        int n = scanner.nextInt();
        scanner.nextLine();
        WeatherStation ws = new WeatherStation(n);
        while (true) {
            String line = scanner.nextLine();
            if (line.equals("=====")) {
                break;
            }
            String[] parts = line.split(" ");
            float temp = Float.parseFloat(parts[0]);
            float wind = Float.parseFloat(parts[1]);
            float hum = Float.parseFloat(parts[2]);
            float vis = Float.parseFloat(parts[3]);
            line = scanner.nextLine();
            Date date = df.parse(line);
            ws.addMeasurment(temp, wind, hum, vis, date);
        }
        String line = scanner.nextLine();
        Date from = df.parse(line);
        line = scanner.nextLine();
        Date to = df.parse(line);
        scanner.close();
        System.out.println(ws.total());
        try {
            ws.status(from, to);
        } catch (RuntimeException e) {
            System.out.println(e);
        }
    }
}
