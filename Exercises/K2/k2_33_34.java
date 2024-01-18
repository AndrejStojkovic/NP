// K2 33 - 34

import java.util.*;
import java.util.stream.Collectors;

enum LogType {
    INFO,
    WARN,
    ERROR
}

class Log {
    LogType type;
    String text;
    long timestamp;

    Log(String type, String text, long timestamp) {
        this.text = text;
        this.timestamp = timestamp;
        switch(type) {
            case "INFO":
                this.type = LogType.INFO;
                break;
            case "WARN":
                this.type = LogType.WARN;
                break;
            case "ERROR":
                this.type = LogType.ERROR;
                break;
        }
    }

    public int getSeverity() {
        int result = 0;
        switch(type) {
            case WARN:
                result = text.contains("might cause error") ? 2 : 1;
                break;
            case ERROR:
                result = 3;
                if(text.contains("fatal")) result += 2;
                if(text.contains("exception")) result += 3;
                break;
        }
        return result;
    }
}

class Microservice {
    String name;
    List<Log> logs;

    Microservice(String name) {
        this.name = name;
        this.logs = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<Log> getLogs() {
        return logs;
    }

    public void addLog(String type, String text, long timestamp) {
        logs.add(new Log(type, text, timestamp));
    }

    public int logCount() {
        return logs.size();
    }

    public double averageSeverity() {
        return logs.stream().mapToDouble(Log::getSeverity).average().orElse(0);
    }
}

class LogCollector {
    Map<String, List<Microservice>> services;

    LogCollector() {
        services = new HashMap<>();
    }

    public void addLog(String s) {
        String [] line = s.split(" ");
        services.putIfAbsent(line[0], new ArrayList<>());
        Microservice microservice = new Microservice(line[1]);
        String text = "";
        for(int i = 3; i < line.length - 1; i++) {
            text += line[i];
            if(i < line.length - 2) text += " ";
        }
        microservice.addLog(line[2], text, Long.parseLong(line[line.length - 1]));
        services.get(line[0]).add(microservice);
    }

    public void printServicesBySeverity() {
        services.forEach((key, value) -> {
            String str = "Service name: " + key + " " +
                    "Count of microservices: " + value.size() + " " +
                    "Total logs in service: " +
                    value.stream().mapToInt(Microservice::logCount).sum() + " " +
                    "Average severity for all logs: " +
                    String.format("%.2f", value.stream().mapToDouble(Microservice::averageSeverity).average().orElse(0)) + " " +
                    "Average number of logs per microservice: " +
                    String.format("%.2f", value.stream().mapToInt(Microservice::logCount).average().orElse(0));
            System.out.println(str);
        });
    }

    public Map<Integer, Integer> getSeverityDistribution(String service, String microservice) {
        Map<Integer, Integer> result = new HashMap<>();
        // TO-DO
        return result;
    }

    public void displayLogs(String service, String microservice, String order) {
        // TO-DO
    }
}

public class LogsTester {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        LogCollector collector = new LogCollector();
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (line.startsWith("addLog")) {
                collector.addLog(line.replace("addLog ", ""));
            } else if (line.startsWith("printServicesBySeverity")) {
                collector.printServicesBySeverity();
            } else if (line.startsWith("getSeverityDistribution")) {
                String[] parts = line.split("\\s+");
                String service = parts[1];
                String microservice = null;
                if (parts.length == 3) {
                    microservice = parts[2];
                }
                collector.getSeverityDistribution(service, microservice).forEach((k,v)-> System.out.printf("%d -> %d%n", k,v));
            } else if (line.startsWith("displayLogs")){
                String[] parts = line.split("\\s+");
                String service = parts[1];
                String microservice = null;
                String order = null;
                if (parts.length == 4) {
                    microservice = parts[2];
                    order = parts[3];
                } else {
                    order = parts[2];
                }
                System.out.println(line);

                collector.displayLogs(service, microservice, order);
            }
        }
    }
}
