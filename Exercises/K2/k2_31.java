// K2 31

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

class Student {
    String index;
    String name;
    Map<String, Integer> activities;

    Student(String index, String name) {
        this.index = index;
        this.name = name;
        activities = new HashMap<>();
        activities.put("midterm1", 0);
        activities.put("midterm2", 0);
        activities.put("labs", 0);
    }

    public String getIndex() {
        return index;
    }

    public void update(String activity, int points) {
        activities.compute(activity, (k, v) -> points);
    }

    public double summary() {
        return get("midterm1") * 0.45 + get("midterm2") * 0.45 + get("labs");
    }

    private int get(String key) {
        return activities.get(key);
    }

    public int grade() {
        int sum = (int)Math.ceil(summary() / 10.0);
        return sum >= 6 ? sum : 5;
    }

    @Override
    public String toString() {
        return String.format(
                "ID: %s Name: %s First midterm: %d Second midterm %d Labs: %d Summary points: %.2f Grade: %d"
                , index, name, get("midterm1"), get("midterm2"), get("labs"), summary(), grade()
        );
    }
}

class AdvancedProgrammingCourse {
    Map<String, Student> students;

    AdvancedProgrammingCourse() {
        students = new HashMap<>();
    }

    public void addStudent(Student s) {
        students.put(s.getIndex(), s);
    }

    public void updateStudent(String index, String activity, int points) {
        Student student = students.get(index);

        int maxPoints = 100;
        if(activity.equals("labs")) {
            maxPoints = 10;
        }

        if(student == null || points < 0 || points > maxPoints) {
            return;
        }

        student.update(activity, points);
    }

    public List<Student> getFirstNStudents(int n) {
        return students.values().stream()
                .sorted(Comparator.comparing(Student::summary).reversed())
                .limit(n)
                .collect(Collectors.toList());
    }

    public Map<Integer, Integer> getGradeDistribution() {
        Map<Integer, Integer> result = new TreeMap<>();
        IntStream.range(5, 11).forEach(i -> result.put(i, 0));
        students.values().forEach(x -> result.computeIfPresent(x.grade(), (k, v) -> v + 1));
        return result;
    }

    public void printStatistics() {
        List<Student> passedStudents = students.values().stream().filter(x -> x.grade() >= 6).collect(Collectors.toList());
        DoubleSummaryStatistics ds = passedStudents.stream().mapToDouble(Student::summary).summaryStatistics();

        System.out.printf("Count: %d Min: %.2f Average: %.2f Max: %.2f\n",
                passedStudents.size(), ds.getMin(), ds.getAverage(), ds.getMax());
    }
}

public class CourseTest {
    public static void printStudents(List<Student> students) {
        students.forEach(System.out::println);
    }

    public static void printMap(Map<Integer, Integer> map) {
        map.forEach((k, v) -> System.out.printf("%d -> %d%n", k, v));
    }

    public static void main(String[] args) {
        AdvancedProgrammingCourse advancedProgrammingCourse = new AdvancedProgrammingCourse();

        Scanner sc = new Scanner(System.in);

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split("\\s+");

            String command = parts[0];

            if (command.equals("addStudent")) {
                String id = parts[1];
                String name = parts[2];
                advancedProgrammingCourse.addStudent(new Student(id, name));
            } else if (command.equals("updateStudent")) {
                String idNumber = parts[1];
                String activity = parts[2];
                int points = Integer.parseInt(parts[3]);
                advancedProgrammingCourse.updateStudent(idNumber, activity, points);
            } else if (command.equals("getFirstNStudents")) {
                int n = Integer.parseInt(parts[1]);
                printStudents(advancedProgrammingCourse.getFirstNStudents(n));
            } else if (command.equals("getGradeDistribution")) {
                printMap(advancedProgrammingCourse.getGradeDistribution());
            } else {
                advancedProgrammingCourse.printStatistics();
            }
        }
    }
}
