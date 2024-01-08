// K2 23

import java.util.*;
import java.util.stream.Collectors;

class Student {
    String index;
    List<Integer> points;

    Student(String index, List<Integer> points) {
        this.index = index;
        this.points = points;
    }

    public String getIndex() {
        return index;
    }

    public Integer getYear() {
        return 20 - Integer.parseInt(index.substring(0, 2));
    }

    public int sumPoints() {
        return points.stream().mapToInt(x -> x).sum();
    }

    public double averagePoints() {
        return sumPoints() / 10.0;
    }

    public boolean potpis() {
        return points.size() >= 8;
    }

    @Override
    public String toString() {
        return String.format("%s %s %.2f", index, potpis() ? "YES" : "NO", averagePoints());
    }
}

class LabExercises {
    List<Student> students;

    LabExercises() {
        students = new ArrayList<>();
    }

    public void addStudent(Student student) {
        students.add(student);
    }

    public void printByAveragePoints(boolean ascending, int n) {
        Comparator<Student> comparator = Comparator.comparing(Student::averagePoints).thenComparing(Student::getIndex);
        students.stream().sorted(ascending ? comparator : comparator.reversed()).limit(n).forEach(System.out::println);
    }

    public List<Student> failedStudents() {
        return students.stream().filter(x -> !x.potpis())
                .sorted(Comparator.comparing(Student::getIndex).thenComparing(Student::sumPoints))
                .collect(Collectors.toList());
    }

    public Map<Integer, Double> getStatisticsByYear() {
        return students.stream()
                .filter(Student::potpis)
                .collect(Collectors.groupingBy(
                        Student::getYear,
                        Collectors.averagingDouble(Student::averagePoints)
                ));
    }
}

public class LabExercisesTest {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        LabExercises labExercises = new LabExercises();
        while (sc.hasNextLine()) {
            String input = sc.nextLine();
            String[] parts = input.split("\\s+");
            String index = parts[0];
            List<Integer> points = Arrays.stream(parts).skip(1)
                    .mapToInt(Integer::parseInt)
                    .boxed()
                    .collect(Collectors.toList());

            labExercises.addStudent(new Student(index, points));
        }

        System.out.println("===printByAveragePoints (ascending)===");
        labExercises.printByAveragePoints(true, 100);
        System.out.println("===printByAveragePoints (descending)===");
        labExercises.printByAveragePoints(false, 100);
        System.out.println("===failed students===");
        labExercises.failedStudents().forEach(System.out::println);
        System.out.println("===statistics by year");
        labExercises.getStatisticsByYear().entrySet().stream()
                .map(entry -> String.format("%d : %.2f", entry.getKey(), entry.getValue()))
                .forEach(System.out::println);
    }
}
