// K1 7

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

class UnsupportedFormatException extends Exception {
    UnsupportedFormatException(String time) { super(time); }
}

class InvalidTimeException extends Exception {
    InvalidTimeException(String time) { super(time); }
}

class Time implements Comparable<Time> {
    int hour;
    int minute;

    Time() {
        hour = minute = 0;
    }

    Time(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public String getTime(TimeFormat format) {
        StringBuilder str = new StringBuilder();

        if(format == TimeFormat.FORMAT_24) {
            str.append(String.format("%2d:%02d", hour, minute));
        }

        if(format == TimeFormat.FORMAT_AMPM) {
            boolean isPM = hour >= 12 && hour <= 23;
            if(hour == 0 && minute >= 0 && minute <= 59) {
                str.append(String.format("%2d:%02d AM", hour + 12, minute));
            } else {
                str.append(String.format("%2d:%02d %s", isPM && hour > 12 ? hour - 12 : hour, minute, isPM ? "PM" : "AM"));
            }
        }
        return str.toString();
    }

    @Override
    public int compareTo(Time o) {
        if(hour == o.getHour()) {
            return Integer.compare(minute, o.getMinute());
        }
        return Integer.compare(hour, o.getHour());
    }
}

class TimeTable {
    ArrayList<Time> times;

    TimeTable() {
        times = new ArrayList<>();
    }

    public void readTimes(InputStream inputStream) throws UnsupportedFormatException, InvalidTimeException {
        Scanner input = new Scanner(inputStream);

        while(input.hasNext()) {
            String t = input.next();

            if(!t.contains(".") && !t.contains(":")) {
                throw new UnsupportedFormatException(t);

            }

            String splitChar = ":";
            if(t.contains(".")) splitChar = "\\.";
            String [] time = t.split(splitChar);

            int hour = Integer.parseInt(time[0]);
            int minute = Integer.parseInt(time[1]);

            if((hour < 0 || hour > 23) && (minute < 0 || minute > 59)) {
                throw new InvalidTimeException(t);
            }

            times.add(new Time(hour, minute));
        }
    }

    public void writeTimes(OutputStream outputStream, TimeFormat format) {
        PrintWriter pw = new PrintWriter(outputStream);
        times.stream().sorted(Time::compareTo).forEach(x -> pw.println(x.getTime(format)));
        pw.flush();
    }
}

enum TimeFormat {
    FORMAT_24, FORMAT_AMPM
}

public class TimesTest {
    public static void main(String[] args) {
        TimeTable timeTable = new TimeTable();
        try {
            timeTable.readTimes(System.in);
        } catch (UnsupportedFormatException e) {
            System.out.println("UnsupportedFormatException: " + e.getMessage());
        } catch (InvalidTimeException e) {
            System.out.println("InvalidTimeException: " + e.getMessage());
        }
        System.out.println("24 HOUR FORMAT");
        timeTable.writeTimes(System.out, TimeFormat.FORMAT_24);
        System.out.println("AM/PM FORMAT");
        timeTable.writeTimes(System.out, TimeFormat.FORMAT_AMPM);
    }
}
