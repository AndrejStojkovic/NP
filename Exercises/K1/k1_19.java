// K1 19

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

class SubtitleTime {
    int hour;
    int minute;
    int second;
    int millisecond;

    SubtitleTime() {
        hour = minute = second = millisecond;
    }

    SubtitleTime(int hour, int minute, int second, int millisecond) {
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        this.millisecond = millisecond;
    }

    SubtitleTime(String pattern) {
        if(pattern.charAt(0) == ' ') {
            pattern = pattern.substring(1);
        }
        String [] time = pattern.split(",");
        String [] std = time[0].split(":");

        hour = toInt(std[0]);
        minute = toInt(std[1]);
        second = toInt(std[2]);
        millisecond = toInt(time[1]);
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public int getSecond() {
        return second;
    }

    public int getMillisecond() {
        return millisecond;
    }

    public int toInt(String str) {
        return Integer.parseInt(str);
    }

    public void shift(int ms) {
        millisecond += ms;

        if(millisecond > 999) {
            millisecond %= 1000;
            second++;
        } else if(millisecond < 0) {
            millisecond += 1000;
            second--;
        }

        if(second > 59) {
            second -= 60;
            minute++;
        } else if(second < 0) {
            second += 60;
            minute--;
        }

        if(minute > 59) {
            minute -= 60;
            hour++;
        } else if(minute < 0) {
            minute += 60;
            hour--;
        }
    }

    @Override
    public String toString() {
        return String.format("%02d:%02d:%02d,%03d", hour, minute, second, millisecond);
    }
}

class Subtitle {
    int index;
    SubtitleTime from;
    SubtitleTime to;
    String text;

    Subtitle() {
        index = 0;
        from = new SubtitleTime();
        to = new SubtitleTime();
        text = "subtitle";
    }

    Subtitle(int index, SubtitleTime from, SubtitleTime to, String text) {
        this.index = index;
        this.from = from;
        this.to = to;
        this.text = text;
    }

    public int getIndex() {
        return index;
    }

    public SubtitleTime getFromTime() {
        return from;
    }

    public SubtitleTime getToFrom() {
        return to;
    }

    public String getText() {
        return text;
    }

    public void shift(int ms) {
        from.shift(ms);
        to.shift(ms);
    }

    @Override
    public String toString() {
        return String.format("%d\n%s --> %s\n%s", index, from, to, text);
    }
}

class Subtitles {
    ArrayList<Subtitle> arr;

    Subtitles() {
        arr = new ArrayList<>();
    }

    public int loadSubtitles(InputStream inputStream) {
        Scanner in = new Scanner(inputStream);

        int loaded = 0;

        while(in.hasNext()) {
            int index = in.nextInt();
            String from = in.next();
            in.next();
            String to = in.nextLine();
            String text = "";

            while(in.hasNextLine()) {
                String add = in.nextLine();
                if(add.isEmpty()) break;
                text += add + "\n";
            }

            arr.add(new Subtitle(index, new SubtitleTime(from), new SubtitleTime(to), text));
            loaded++;
        }

        return loaded;
    }

    public void print() {
        arr.forEach(System.out::println);
    }

    public void shift(int ms) {
        arr.forEach(x -> x.shift(ms));
    }
}

public class SubtitlesTest {
    public static void main(String[] args) {
        Subtitles subtitles = new Subtitles();
        int n = subtitles.loadSubtitles(System.in);
        System.out.println("+++++ ORIGINIAL SUBTITLES +++++");
        subtitles.print();
        int shift = n * 37;
        shift = (shift % 2 == 1) ? -shift : shift;
        System.out.println(String.format("SHIFT FOR %d ms", shift));
        subtitles.shift(shift);
        System.out.println("+++++ SHIFTED SUBTITLES +++++");
        subtitles.print();
    }
}
