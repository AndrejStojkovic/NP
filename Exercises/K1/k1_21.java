// K1 21

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

class LapTime implements Comparable<LapTime> {
    int minutes;
    int seconds;
    int milliseconds;

    LapTime() {
        minutes = seconds = milliseconds = 0;
    }

    LapTime(int minutes, int seconds, int milliseconds) {
        this.minutes = minutes;
        this.seconds = seconds;
        this.milliseconds = milliseconds;
    }

    LapTime(String pattern) {
        String [] time = pattern.split(":");
        minutes = toInt(time[0]);
        seconds = toInt(time[1]);
        milliseconds = toInt(time[2]);
    }

    public int toInt(String str) {
        return Integer.parseInt(str);
    }

    public int getMinutes() {
        return minutes;
    }

    public int getSeconds() {
        return seconds;
    }

    public int getMilliseconds() {
        return milliseconds;
    }

    @Override
    public String toString() {
        return String.format("%d:%02d:%03d", minutes, seconds, milliseconds);
    }

    @Override
    public int compareTo(LapTime o) {
        if(minutes == o.getMinutes() && seconds == o.getSeconds() && milliseconds == o.getMilliseconds()) {
            return 0;
        }

        if(minutes < o.getMinutes()) {
            return -1;
        } else if(minutes > o.getMinutes()) {
            return 1;
        }

        if(seconds < o.getSeconds()) {
            return -1;
        } else if(seconds > o.getSeconds()) {
            return 1;
        }

        if(milliseconds < o.getMilliseconds()) {
            return -1;
        } else if(milliseconds > o.getMilliseconds()) {
            return 1;
        }

        return 1;
    }
}
class Lap implements Comparable<Lap> {
    String driver;
    LapTime lap1;
    LapTime lap2;
    LapTime lap3;

    Lap() {
        driver = "driver";
        lap1 = lap2 = lap3 = new LapTime();
    }

    Lap(String driver, LapTime lap1, LapTime lap2, LapTime lap3) {
        this.driver = driver;
        this.lap1 = lap1;
        this.lap2 = lap2;
        this.lap3 = lap3;
    }

    Lap(String pattern) {
        String [] lap = pattern.split(" ");
        driver = lap[0];
        lap1 = new LapTime(lap[1]);
        lap2 = new LapTime(lap[2]);
        lap3 = new LapTime(lap[3]);
    }

    public LapTime getFastestLap() {
        LapTime fastestLap = lap1;
        if(lap2.compareTo(lap1) < 0 && lap2.compareTo(lap3) < 0)
            fastestLap = lap2;
        else if(lap3.compareTo(lap1) < 0 && lap3.compareTo(lap2) < 0)
            fastestLap = lap3;

        return fastestLap;
    }

    @Override
    public String toString() {
        return String.format("%-10s%10s", driver, getFastestLap());
    }

    @Override
    public int compareTo(Lap o) {
        return getFastestLap().compareTo(o.getFastestLap());
    }
}

class F1Race {
    ArrayList<Lap> laps;

    F1Race() {
        laps = new ArrayList<>();
    }

    void readResults(InputStream inputStream) {
        Scanner in = new Scanner(inputStream);

        while(in.hasNextLine()) {
            String line = in.nextLine();
            laps.add(new Lap(line));
        }
    }

    void printSorted(OutputStream outputStream) {
        PrintWriter pw = new PrintWriter(outputStream);
        laps.sort(Lap::compareTo);
        for(int i = 0; i < laps.size(); i++) {
            pw.println(String.format("%d. %s", (i + 1), laps.get(i)));
        }
        pw.flush();
    }
}

public class F1Test {
    public static void main(String[] args) {
        F1Race f1Race = new F1Race();
        f1Race.readResults(System.in);
        f1Race.printSorted(System.out);
    }
}
