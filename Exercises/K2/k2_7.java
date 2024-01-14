// K2 7

import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class BonusNotAllowedException extends Exception {
    BonusNotAllowedException() { }
}

interface IEmployee {
    double salary();
    double overtimeSalary();
}

enum BonusType {
    FIXED,
    PERCENT
}

class Employee implements Comparable<Employee>, IEmployee {
    String id;
    String level;
    double rate;
    BonusType bonusType;
    double bonus;

    Employee(String id, String level, double rate) {
        this.id = id;
        this.level = level;
        this.rate = rate;
    }

    public void setBonus(BonusType bonusType, double bonus) {
        this.bonusType = bonusType;
        this.bonus = bonus;
    }

    public String getLevel() {
        return level;
    }

    @Override
    public double salary() {
        return 0;
    }

    @Override
    public double overtimeSalary() {
        return 0;
    }

    @Override
    public int compareTo(Employee o) {
        int s = Double.compare(o.salary(), salary());
        return s != 0 ? s : level.compareTo(o.level);
    }

    @Override
    public String toString() {
        return String.format("Employee ID: %s Level: %s Salary: %.2f", id, level, salary());
    }
}

class HourlyEmployee extends Employee {
    double hours;

    HourlyEmployee(String id, String level, double rate, double hours) {
        super(id, level, rate);
        this.hours = hours;
    }

    public double getRegularHours() {
        return Math.min(hours, 40);
    }

    public double getOvertimeHours() {
        return Math.max(0, hours - 40);
    }

    @Override
    public double salary() {
        return (getRegularHours() * rate) + (getOvertimeHours() * (rate * 1.5));
    }

    @Override
    public double overtimeSalary() {
        return bonusType == BonusType.FIXED ? getOvertimeHours() + bonus : getOvertimeHours() * (1 + bonus / 100.0);
    }

    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("0.00");
        return String.format("%s Regular hours: %s Overtime hours: %s",
                super.toString(), df.format(getRegularHours()), df.format(getOvertimeHours()));
    }
}

class FreelanceEmployee extends Employee {
    List<Integer> points;

    FreelanceEmployee(String id, String level, double rate, List<Integer> points) {
        super(id, level, rate);
        this.points = points;
    }

    @Override
    public double salary() {
        return points.stream().mapToInt(x -> x).sum() * rate;
    }

    @Override
    public double overtimeSalary() {
        return bonusType == BonusType.FIXED ? salary() + bonus : salary() * (1 + bonus / 100.0);
    }

    @Override
    public String toString() {
        return String.format("%s Tickets count: %d Tickets points: %d",
                super.toString(), points.size(), points.stream().mapToInt(x -> x).sum());
    }
}

class PayrollSystem {
    List<Employee> employees;
    Map<String, Double> hourlyRateByLevel;
    Map<String, Double> ticketRateByLevel;

    PayrollSystem(Map<String, Double> hourlyRateByLevel, Map<String, Double> ticketRateByLevel) {
        this.employees = new ArrayList<>();
        this.hourlyRateByLevel = hourlyRateByLevel;
        this.ticketRateByLevel = ticketRateByLevel;
    }

    public Employee createEmployee(String input) throws BonusNotAllowedException {
        String [] line = input.split(" ");
        String [] e = line[0].split(";");

        Employee employee;
        String id = e[1];
        String level = e[2];

        if(e[0].equals("F")) {
            List<Integer> points = new ArrayList<>();
            IntStream.range(3, e.length).forEach(i -> points.add(Integer.parseInt(e[i])));
            employee = new FreelanceEmployee(id, level, ticketRateByLevel.get(level), points);
        } else {
            employee = new HourlyEmployee(id, level, hourlyRateByLevel.get(level), Double.parseDouble(e[3]));
        }

        double value;
        BonusType type;
        if(line.length > 1) {
            boolean isPercent = line[1].contains("%");
            value = Double.parseDouble(isPercent ? line[1].substring(0, line[1].length() - 1) : line[1]);
            type = isPercent ? BonusType.PERCENT : BonusType.FIXED;

            if((isPercent && value > 20) || (!isPercent && value > 1000)) {
                throw new BonusNotAllowedException();
            }

            employee.setBonus(type, value);
        }

        employees.add(employee);
        return employee;
    }

    public Map<String, Double> getOvertimeSalaryForLevels() {
        // TO-Do
        return new HashMap<>();
    }

    public void printStatisticsForOvertimeSalary() {
        // TO-DO
    }

    public Map<String, Integer> ticketsDoneByLevel() {
        // TO-DO
        return new HashMap<>();
    }

    public Collection<Employee> getFirstNEmployeesByBonus(int n) {
        return employees.stream()
                .sorted(Comparator.comparing(Employee::overtimeSalary).reversed())
                .limit(n).collect(Collectors.toList());
    }
}

public class PayrollSystemTest2 {

    public static void main(String[] args) {

        Map<String, Double> hourlyRateByLevel = new LinkedHashMap<>();
        Map<String, Double> ticketRateByLevel = new LinkedHashMap<>();
        for (int i = 1; i <= 10; i++) {
            hourlyRateByLevel.put("level" + i, 11 + i * 2.2);
            ticketRateByLevel.put("level" + i, 5.5 + i * 2.5);
        }

        Scanner sc = new Scanner(System.in);

        int employeesCount = Integer.parseInt(sc.nextLine());

        PayrollSystem ps = new PayrollSystem(hourlyRateByLevel, ticketRateByLevel);
        Employee emp = null;
        for (int i = 0; i < employeesCount; i++) {
            try {
                emp = ps.createEmployee(sc.nextLine());
            } catch (BonusNotAllowedException e) {
                System.out.println(e.getMessage());
            }
        }

        int testCase = Integer.parseInt(sc.nextLine());

        switch (testCase) {
            case 1: //Testing createEmployee
                if (emp != null)
                    System.out.println(emp);
                break;
            case 2: //Testing getOvertimeSalaryForLevels()
                ps.getOvertimeSalaryForLevels().forEach((level, overtimeSalary) -> {
                    System.out.printf("Level: %s Overtime salary: %.2f\n", level, overtimeSalary);
                });
                break;
            case 3: //Testing printStatisticsForOvertimeSalary()
                ps.printStatisticsForOvertimeSalary();
                break;
            case 4: //Testing ticketsDoneByLevel
                ps.ticketsDoneByLevel().forEach((level, overtimeSalary) -> {
                    System.out.printf("Level: %s Tickets by level: %d\n", level, overtimeSalary);
                });
                break;
            case 5: //Testing getFirstNEmployeesByBonus (int n)
                ps.getFirstNEmployeesByBonus(Integer.parseInt(sc.nextLine())).forEach(System.out::println);
                break;
        }

    }
}
