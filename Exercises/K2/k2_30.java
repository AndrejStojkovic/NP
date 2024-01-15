// K2 30

import java.util.*;

class DurationConverter {
    public static String convert(long duration) {
        long minutes = duration / 60;
        duration %= 60;
        return String.format("%02d:%02d", minutes, duration);
    }
}

class Call implements Comparable<Call> {
    String uuid;
    String dialer;
    String receiver;
    long timestamp;
    long timestampCall;
    long timestampEnd;
    long hold;
    long totalHoldTime;

    Call(String uuid, String dialer, String receiver, long timestamp) {
        this.uuid = uuid;
        this.dialer = dialer;
        this.receiver = receiver;
        this.timestamp = timestamp;
        this.timestampCall = this.hold = -1;
        this.timestampEnd = this.totalHoldTime = 0;
    }

    public void answer(long timestamp) {
        timestampCall = timestamp;
    }

    public void end(long timestamp) {
        timestampEnd = timestamp;
        if(hold != -1) {
            totalHoldTime += (timestamp - hold);
        }
    }

    public void hold(long timestamp) {
        hold = timestamp;
    }

    public void resume(long timestamp) {
        totalHoldTime += timestamp - hold;
        hold = -1;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getDuration() {
        if(timestampCall == -1) {
            return 0;
        }
        long res = timestampEnd - timestampCall - totalHoldTime;
        return res >= 0 ? res : 0;
    }

    @Override
    public String toString() {
        return String.format("%s <-> %s", dialer, receiver);
    }

    public String print(String phoneNumber) {
        String type = phoneNumber.equals(dialer) ? "D" : "R";
        String other = phoneNumber.equals(dialer) ? receiver : dialer;
        if(timestampCall == -1) {
            return String.format("%s %s %d MISSED CALL 00:00", type, other, timestampEnd);
        }
        return String.format("%s %s %d %d %s", type, other, timestampCall, timestampEnd, DurationConverter.convert(getDuration()));
    }

    @Override
    public int compareTo(Call o) {
        return Long.compare(o.getDuration(), getDuration());
    }
}

class TelcoApp {
    Map<String, Call> calls;

    TelcoApp() {
        calls = new HashMap<>();
    }

    public void addCall(String uuid, String dialer, String receiver, long timestamp) {
        calls.put(uuid, new Call(uuid, dialer, receiver, timestamp));
    }

    public void updateCall(String uuid, long timestamp, String action) {
        Call call = calls.get(uuid);
        if(call == null) return;

        switch(action) {
            case "ANSWER":
                call.answer(timestamp);
                break;
            case "END":
                call.end(timestamp);
                break;
            case "HOLD":
                call.hold(timestamp);
                break;
            case "RESUME":
                call.resume(timestamp);
                break;
        }
    }

    public void printChronologicalReport(String phoneNumber) {
        calls.values().stream()
                .filter(x -> x.dialer.equals(phoneNumber) || x.receiver.equals(phoneNumber))
                .sorted(Comparator.comparing(Call::getTimestamp))
                .forEach(x -> System.out.println(x.print(phoneNumber)));
    }

    public void printReportByDuration(String phoneNumber) {
        calls.values().stream()
                .filter(x -> x.dialer.equals(phoneNumber) || x.receiver.equals(phoneNumber))
                .sorted(Comparator.comparing(Call::getDuration).thenComparing(Call::getTimestamp).reversed())
                .forEach(x -> System.out.println(x.print(phoneNumber)));
    }

    public void printCallsDuration() {
        Map<String, Long> map = new HashMap<>();

        calls.values().forEach(x -> {
            map.putIfAbsent(x.toString(), 0L);
            long value = map.get(x.toString());
            map.put(x.toString(), value + x.getDuration());
        });

        map.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEach(x -> System.out.printf("%s : %s\n", x.getKey(), DurationConverter.convert(x.getValue())));
    }
}

public class TelcoTest2 {
    public static void main(String[] args) {
        TelcoApp app = new TelcoApp();

        Scanner sc = new Scanner(System.in);

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split("\\s+");
            String command = parts[0];

            if (command.equals("addCall")) {
                String uuid = parts[1];
                String dialer = parts[2];
                String receiver = parts[3];
                long timestamp = Long.parseLong(parts[4]);
                app.addCall(uuid, dialer, receiver, timestamp);
            } else if (command.equals("updateCall")) {
                String uuid = parts[1];
                long timestamp = Long.parseLong(parts[2]);
                String action = parts[3];
                app.updateCall(uuid, timestamp, action);
            } else if (command.equals("printChronologicalReport")) {
                String phoneNumber = parts[1];
                app.printChronologicalReport(phoneNumber);
            } else if (command.equals("printReportByDuration")) {
                String phoneNumber = parts[1];
                app.printReportByDuration(phoneNumber);
            } else {
                app.printCallsDuration();
            }
        }

    }
}
