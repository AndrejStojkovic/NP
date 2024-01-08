// K2 26 (Distribution needs to be done)

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.*;

class Student implements Comparable<Student> {
    String id;
    String major;
    List<Integer> grades;

    Student(String id, String major, List<Integer> grades) {
        this.id = id;
        this.major = major;
        this.grades = grades;
    }

    public double avg() {
        return grades.stream().mapToDouble(x -> x).average().orElse(0);
    }

    public long count(int grade) {
        return grades.stream().filter(x -> x == grade).count();
    }

    public int sortByGrade(Student o) {
        return Long.compare(o.count(10), count(10));
    }

    @Override
    public int compareTo(Student o) {
        int a = Double.compare(o.avg(), avg());
        return a == 0 ? id.compareTo(o.id) : a;
    }

    @Override
    public String toString() {
        return String.format("%s %.2f", id, avg());
    }
}

class StudentRecords {
    Map<String, Set<Student>> students;

    StudentRecords() {
        students = new TreeMap<>();
    }

    public int readRecords(InputStream inputStream) {
        Scanner scanner = new Scanner(inputStream);

        int ct = 0;
        while(scanner.hasNextLine()) {
            String [] line = scanner.nextLine().split(" ");
            List<Integer> grades = new ArrayList<>();

            for(int i = 2; i < line.length; i++) {
                grades.add(Integer.parseInt(line[i]));
            }

            Student student = new Student(line[0], line[1], grades);
            students.computeIfAbsent(line[1], x -> new HashSet<>());
            students.computeIfPresent(line[1], (k, v) -> { v.add(student); return v; });

            ct++;
        }

        return ct;
    }

    public void writeTable(OutputStream outputStream) {
        PrintWriter pw = new PrintWriter(outputStream);
        students.forEach((k, v) -> {
            pw.println(k);
            v.stream().sorted().forEach(pw::println);
        });
        pw.flush();
    }

    public void writeDistribution(OutputStream outputStream) {
        PrintWriter pw = new PrintWriter(outputStream);
        // TO-DO
        pw.flush();
    }
}

public class StudentRecordsTest {
    public static void main(String[] args) {
        System.out.println("=== READING RECORDS ===");
        StudentRecords studentRecords = new StudentRecords();
        int total = studentRecords.readRecords(System.in);
        System.out.printf("Total records: %d\n", total);
        System.out.println("=== WRITING TABLE ===");
        studentRecords.writeTable(System.out);
        System.out.println("=== WRITING DISTRIBUTION ===");
        studentRecords.writeDistribution(System.out);
    }
}
