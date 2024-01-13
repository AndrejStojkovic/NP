// K2 28
// Not a fan of this solution but it works, the 'Course' class should not exist
// There is a better way to do this, might refactor it later on

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.IntStream;

class OperationNotAllowedException extends Exception {
    OperationNotAllowedException(String message) {
        super(message);
    }
}

class Course implements Comparable<Course> {
    String name;
    Set<Student> listeners;
    List<Integer> grades;

    Course(String name) {
        this.name = name;
        listeners = new HashSet<>();
        grades = new ArrayList<>();
    }

    public void addListener(Student student) {
        listeners.add(student);
    }

    public void addGrade(int grade) {
        grades.add(grade);
    }

    public double average() {
        return grades.stream().mapToDouble(x -> x).average().orElse(0);
    }

    public int getListeners() {
        return listeners.size();
    }

    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("0.00");
        return String.format("%s %d %s", name, getListeners(), df.format(average()));
    }

    @Override
    public int compareTo(Course o) {
        int num = Integer.compare(getListeners(), o.getListeners());
        return num != 0 ? num : Double.compare(average(), o.average());
    }
}

class Student implements Comparable<Student> {
    String id;
    int yearsOfStudies;
    Set<String> courses;
    Map<Integer, List<Integer>> grades;

    Student(String id, int yearsOfStudies) {
        this.id = id;
        this.yearsOfStudies = yearsOfStudies;
        this.courses = new TreeSet<>();
        this.grades = new HashMap<>();
        IntStream.range(1, yearsOfStudies * 2 + 1).forEach(i -> grades.put(i, new ArrayList<>()));
    }

    public int getYearsOfStudies() {
        return yearsOfStudies;
    }

    public void addGrade(int term, String courseName, int grade) {
        grades.computeIfAbsent(term, x -> new ArrayList<>());
        grades.get(term).add(grade);
        courses.add(courseName);
    }

    public long totalGrades() {
        return grades.values().stream().mapToLong(Collection::size).sum();
    }

    public int canAddGrade(int term) {
        grades.computeIfAbsent(term, x -> new ArrayList<>());
        if((yearsOfStudies == 3 && term > 6) || term > 8) return -1;
        return grades.get(term).size() < 3 ? 1 : 0;
    }

    public boolean canGraduate() {
        long g = totalGrades();
        return (yearsOfStudies == 3 && g >= 18) || (yearsOfStudies == 4 && g >= 24);
    }

    public double averageGrade() {
        return grades.values().stream().flatMap(Collection::stream).mapToDouble(x -> x).average().orElse(5);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        DecimalFormat df = new DecimalFormat("0.00");
        str.append("Student: ").append(id).append("\n");
        grades.forEach((key, value) -> {
            str.append("Term ").append(key).append("\n");
            str.append("Courses: ").append(value.size()).append("\n");
            str.append("Average grade for term: ");
            str.append(df.format(value.stream().mapToDouble(x -> x).average().orElse(5))).append("\n");
        });
        str.append(String.format("Average grade: %s", df.format(averageGrade()))).append("\n");
        str.append("Courses attended: ").append(String.join(",", courses));
        return str.toString();
    }

    @Override
    public int compareTo(Student o) {
        int num = Long.compare(o.totalGrades(), totalGrades());
        if(num != 0) return num;

        int gr = Double.compare(o.averageGrade(), averageGrade());
        if(gr != 0) return gr;

        return o.id.compareTo(id);
    }
}

class Faculty {
    Map<String, Student> students;
    List<String> logs;
    Map<String, Course> availableCourses;

    Faculty() {
        students = new HashMap<>();
        logs = new ArrayList<>();
        availableCourses = new TreeMap<>();
    }

    public void addStudent(String id, int yearsOfStudies) {
        students.put(id, new Student(id, yearsOfStudies));
    }

    public void addGradeToStudent(String studentId, int term, String courseName, int grade) throws OperationNotAllowedException {
        Student student = students.get(studentId);
        String message;

        if(student.canAddGrade(term) == -1) {
            message = String.format("Term %d is not possible for student with ID %s", term, studentId);
            throw new OperationNotAllowedException(message);
        }

        if(student.canAddGrade(term) == 0) {
            message = String.format("Student %s already has 3 grades in term %d", studentId, term);
            throw new OperationNotAllowedException(message);
        }

        student.addGrade(term, courseName, grade);
        availableCourses.computeIfAbsent(courseName, x -> new Course(courseName));
        Course c = availableCourses.get(courseName);
        c.addGrade(grade);
        c.addListener(student);

        if(student.canGraduate()) {
            logs.add(String.format("Student with ID %s graduated with average grade %.2f in %d years.",
                    studentId, student.averageGrade(), student.getYearsOfStudies()));
            students.remove(studentId);
        }
    }

    public String getFacultyLogs() {
        return String.join("\n", logs);
    }

    public String getDetailedReportForStudent(String id) {
        return students.get(id).toString();
    }

    public void printFirstNStudents(int n) {
        students.values().stream().sorted().limit(n).forEach(x ->
                System.out.printf("Student: %s Courses passed: %d Average grade: %.2f%n", x.id, x.totalGrades(), x.averageGrade()));
    }

    public void printCourses() {
        availableCourses.values().stream().sorted().forEach(System.out::println);
    }
}

public class FacultyTest {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int testCase = sc.nextInt();

        if (testCase == 1) {
            System.out.println("TESTING addStudent AND printFirstNStudents");
            Faculty faculty = new Faculty();
            for (int i = 0; i < 10; i++) {
                faculty.addStudent("student" + i, (i % 2 == 0) ? 3 : 4);
            }
            faculty.printFirstNStudents(10);

        } else if (testCase == 2) {
            System.out.println("TESTING addGrade and exception");
            Faculty faculty = new Faculty();
            faculty.addStudent("123", 3);
            faculty.addStudent("1234", 4);
            try {
                faculty.addGradeToStudent("123", 7, "NP", 10);
            } catch (OperationNotAllowedException e) {
                System.out.println(e.getMessage());
            }
            try {
                faculty.addGradeToStudent("1234", 9, "NP", 8);
            } catch (OperationNotAllowedException e) {
                System.out.println(e.getMessage());
            }
        } else if (testCase == 3) {
            System.out.println("TESTING addGrade and exception");
            Faculty faculty = new Faculty();
            faculty.addStudent("123", 3);
            faculty.addStudent("1234", 4);
            for (int i = 0; i < 4; i++) {
                try {
                    faculty.addGradeToStudent("123", 1, "course" + i, 10);
                } catch (OperationNotAllowedException e) {
                    System.out.println(e.getMessage());
                }
            }
            for (int i = 0; i < 4; i++) {
                try {
                    faculty.addGradeToStudent("1234", 1, "course" + i, 10);
                } catch (OperationNotAllowedException e) {
                    System.out.println(e.getMessage());
                }
            }
        } else if (testCase == 4) {
            System.out.println("Testing addGrade for graduation");
            Faculty faculty = new Faculty();
            faculty.addStudent("123", 3);
            faculty.addStudent("1234", 4);
            int counter = 1;
            for (int i = 1; i <= 6; i++) {
                for (int j = 1; j <= 3; j++) {
                    try {
                        faculty.addGradeToStudent("123", i, "course" + counter, (i % 2 == 0) ? 7 : 8);
                    } catch (OperationNotAllowedException e) {
                        System.out.println(e.getMessage());
                    }
                    ++counter;
                }
            }
            counter = 1;
            for (int i = 1; i <= 8; i++) {
                for (int j = 1; j <= 3; j++) {
                    try {
                        faculty.addGradeToStudent("1234", i, "course" + counter, (j % 2 == 0) ? 7 : 10);
                    } catch (OperationNotAllowedException e) {
                        System.out.println(e.getMessage());
                    }
                    ++counter;
                }
            }
            System.out.println("LOGS");
            System.out.println(faculty.getFacultyLogs());
            System.out.println("PRINT STUDENTS (there shouldn't be anything after this line!");
            faculty.printFirstNStudents(2);
        } else if (testCase == 5 || testCase == 6 || testCase == 7) {
            System.out.println("Testing addGrade and printFirstNStudents (not graduated student)");
            Faculty faculty = new Faculty();
            for (int i = 1; i <= 10; i++) {
                faculty.addStudent("student" + i, ((i % 2) == 1 ? 3 : 4));
                int courseCounter = 1;
                for (int j = 1; j < ((i % 2 == 1) ? 6 : 8); j++) {
                    for (int k = 1; k <= ((j % 2 == 1) ? 3 : 2); k++) {
                        try {
                            faculty.addGradeToStudent("student" + i, j, ("course" + courseCounter), i % 5 + 6);
                        } catch (OperationNotAllowedException e) {
                            System.out.println(e.getMessage());
                        }
                        ++courseCounter;
                    }
                }
            }
            if (testCase == 5)
                faculty.printFirstNStudents(10);
            else if (testCase == 6)
                faculty.printFirstNStudents(3);
            else
                faculty.printFirstNStudents(20);
        } else if (testCase == 8 || testCase == 9) {
            System.out.println("TESTING DETAILED REPORT");
            Faculty faculty = new Faculty();
            faculty.addStudent("student1", ((testCase == 8) ? 3 : 4));
            int grade = 6;
            int counterCounter = 1;
            for (int i = 1; i < ((testCase == 8) ? 6 : 8); i++) {
                for (int j = 1; j < 3; j++) {
                    try {
                        faculty.addGradeToStudent("student1", i, "course" + counterCounter, grade);
                    } catch (OperationNotAllowedException e) {
                        e.printStackTrace();
                    }
                    grade++;
                    if (grade == 10)
                        grade = 5;
                    ++counterCounter;
                }
            }
            System.out.println(faculty.getDetailedReportForStudent("student1"));
        } else if (testCase==10) {
            System.out.println("TESTING PRINT COURSES");
            Faculty faculty = new Faculty();
            for (int i = 1; i <= 10; i++) {
                faculty.addStudent("student" + i, ((i % 2) == 1 ? 3 : 4));
                int courseCounter = 1;
                for (int j = 1; j < ((i % 2 == 1) ? 6 : 8); j++) {
                    for (int k = 1; k <= ((j % 2 == 1) ? 3 : 2); k++) {
                        int grade = sc.nextInt();
                        try {
                            faculty.addGradeToStudent("student" + i, j, ("course" + courseCounter), grade);
                        } catch (OperationNotAllowedException e) {
                            System.out.println(e.getMessage());
                        }
                        ++courseCounter;
                    }
                }
            }
            faculty.printCourses();
        } else if (testCase==11) {
            System.out.println("INTEGRATION TEST");
            Faculty faculty = new Faculty();
            for (int i = 1; i <= 10; i++) {
                faculty.addStudent("student" + i, ((i % 2) == 1 ? 3 : 4));
                int courseCounter = 1;
                for (int j = 1; j <= ((i % 2 == 1) ? 6 : 8); j++) {
                    for (int k = 1; k <= ((j % 2 == 1) ? 2 : 3); k++) {
                        int grade = sc.nextInt();
                        try {
                            faculty.addGradeToStudent("student" + i, j, ("course" + courseCounter), grade);
                        } catch (OperationNotAllowedException e) {
                            System.out.println(e.getMessage());
                        }
                        ++courseCounter;
                    }
                }

            }

            for (int i=11;i<15;i++) {
                faculty.addStudent("student" + i, ((i % 2) == 1 ? 3 : 4));
                int courseCounter = 1;
                for (int j = 1; j <= ((i % 2 == 1) ? 6 : 8); j++) {
                    for (int k = 1; k <= 3; k++) {
                        int grade = sc.nextInt();
                        try {
                            faculty.addGradeToStudent("student" + i, j, ("course" + courseCounter), grade);
                        } catch (OperationNotAllowedException e) {
                            System.out.println(e.getMessage());
                        }
                        ++courseCounter;
                    }
                }
            }
            System.out.println("LOGS");
            System.out.println(faculty.getFacultyLogs());
            System.out.println("DETAILED REPORT FOR STUDENT");
            System.out.println(faculty.getDetailedReportForStudent("student2"));
            try {
                System.out.println(faculty.getDetailedReportForStudent("student11"));
                System.out.println("The graduated students should be deleted!!!");
            } catch (NullPointerException e) {
                System.out.println("The graduated students are really deleted");
            }
            System.out.println("FIRST N STUDENTS");
            faculty.printFirstNStudents(10);
            System.out.println("COURSES");
            faculty.printCourses();
        }
    }
}
