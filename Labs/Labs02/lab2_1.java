// Lab 2.1

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

abstract class Contact {
    private String date;

    Contact() {
        date = "1980-01-01";
    }

    Contact(String date) {
        this.date = date;
    }

    public int getDay() {
        return Integer.parseInt(date.split("-")[2]);
    }

    public int getMonth() {
        return Integer.parseInt(date.split("-")[1]);
    }

    public int getYear() {
        return Integer.parseInt(date.split("-")[0]);
    }

    public boolean isNewerThan(Contact c) {
        int year = getYear(), month = getMonth(), day = getDay();

        if(year < c.getYear())  return false;
        if(year == c.getYear() && month < c.getMonth()) return false;
        if(year == c.getYear() && month == c.getMonth() && day < c.getDay()) return false;
        return true;
    }

    public String getType() {
        return "None";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contact contact = (Contact) o;
        return Objects.equals(date, contact.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date);
    }
}

class EmailContact extends Contact {
    private String email;

    EmailContact(String date, String email) {
        super(date);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getType() {
        return "Email";
    }

    @Override
    public String toString() {
        return email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        EmailContact that = (EmailContact) o;
        return Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), email);
    }
}

enum Operator { VIP, ONE, TMOBILE };

class PhoneContact extends Contact {
    private String phone;
    private Operator operator;

    PhoneContact(String date, String phone) {
        super(date);
        this.phone = phone;

        char n = this.phone.charAt(2);
        if(n == '0' || n == '1' || n == '2') {
            operator = Operator.TMOBILE;
        } else if(n == '5' || n == '6') {
            operator = Operator.ONE;
        } else {
            operator = Operator.VIP;
        }
    }

    public String getPhone() {
        return phone;
    }

    public Operator getOperator() {
        return operator;
    }

    @Override
    public String getType() {
        return "Phone";
    }

    @Override
    public String toString() {
        return phone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PhoneContact that = (PhoneContact) o;
        return Objects.equals(phone, that.phone) && operator == that.operator;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), phone, operator);
    }
}

class Student {
    private String firstName;
    private String lastName;
    private String city;
    private int age;
    private long index;
    private Contact [] contacts;

    Student(String firstName, String lastName, String city, int age, long index) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.city = city;
        this.age = age;
        this.index = index;
        this.contacts = new Contact[0];
    }

    public void addEmailContact(String date, String email) {
        Contact [] newContacts = new Contact[contacts.length + 1];
        for(int i = 0; i < contacts.length; i++) newContacts[i] = contacts[i];
        newContacts[newContacts.length - 1] = new EmailContact(date, email);
        contacts = Arrays.copyOf(newContacts, newContacts.length);
    }

    public void addPhoneContact(String date, String phone) {
        Contact [] newContacts = new Contact[contacts.length + 1];
        for(int i = 0; i < contacts.length; i++) newContacts[i] = contacts[i];
        newContacts[newContacts.length - 1] = new PhoneContact(date, phone);
        contacts = Arrays.copyOf(newContacts, newContacts.length);
    }

    public Contact[] getEmailContacts() {
        int ct = 0;

        for(int i = 0; i < contacts.length; i++) {
            if(contacts[i].getType().equals("Email")) {
                ct++;
            }
        }

        Contact [] emailContacts = new Contact[ct];
        int idx = 0;
        for(int i = 0; i < contacts.length; i++) {
            if(contacts[i].getType().equals("Email")) {
                emailContacts[idx++] = contacts[i];
            }
        }
        return emailContacts;
    }

    public Contact[] getPhoneContacts() {
        int ct = 0;

        for(int i = 0; i < contacts.length; i++) {
            if(contacts[i].getType().equals("Phone")) {
                ct++;
            }
        }

        Contact [] phoneContacts = new Contact[ct];
        int idx = 0;
        for(int i = 0; i < contacts.length; i++) {
            if(contacts[i].getType().equals("Phone")) {
                phoneContacts[idx++] = contacts[i];
            }
        }
        return phoneContacts;
    }

    public int getContactsLen() {
        return contacts.length;
    }

    public String getCity() {
        return city;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public long getIndex() {
        return index;
    }

    public Contact getLatestContact() {
        int idx = -1;
        for(int i = 0; i < contacts.length; i++) {
            if(idx == -1 || contacts[i].isNewerThan(contacts[idx])) {
                idx = i;
            }
        }
        return contacts[idx];
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        result.append("{\"ime\":\"").append(firstName).append("\", \"prezime\":\"").append(lastName).append("\", \"vozrast\":").append(age).append(", ");
        result.append("\"grad\":\"").append(city).append("\", \"indeks\":").append(index).append(", ");

        result.append("\"telefonskiKontakti\":[");

        Contact [] phoneContacts = getPhoneContacts();
        Contact [] emailContacts = getEmailContacts();

        for(int i = 0; i < phoneContacts.length; i++) {
            result.append("\"").append(phoneContacts[i]).append("\"");
            if(i < phoneContacts.length - 1) {
                result.append(", ");
            }
        }

        result.append("], \"emailKontakti\":[");

        for(int i = 0; i < emailContacts.length; i++) {
            result.append("\"").append(emailContacts[i]).append("\"");
            if(i < emailContacts.length - 1) {
                result.append(", ");
            }
        }

        result.append("]}");
        return result.toString();
    }
}

class Faculty {
    private String name;
    private Student [] students;

    Faculty(String name, Student [] students) {
         this.name = name;
         this.students = students.clone();
    }

    public String getName() {
        return name;
    }

    public Student [] getStudents() {
        return students;
    }

    public Student getStudent(long index) {
        for(int i = 0; i < students.length; i++) {
            if(students[i].getIndex() == index) {
                return students[i];
            }
        }

        return null;
    }

    public int countStudentsFromCity(String cityName) {
        int ct = 0;
        for(int i = 0; i < students.length; i++) {
            if(students[i].getCity().equals(cityName)) {
                ct++;
            }
        }
        return ct;
    }

    public double getAverageNumberOfContacts() {
        double s = 0;
        for(int i = 0; i < students.length; i++) s += students[i].getContactsLen();
        return s / students.length;
    }

    public Student getStudentWithMostContacts() {
        int idx = -1;
        for(int i = 0; i < students.length; i++) {
            if(idx == -1 || students[i].getContactsLen() > students[idx].getContactsLen() ||
                    (students[i].getContactsLen() == students[idx].getContactsLen() && students[i].getIndex() > students[idx].getIndex())) {
                idx = i;
            }
        }
        return idx != -1 ? students[idx] : null;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("{\"fakultet\":\"").append(name).append("\", \"studenti\":[");
        for(int i = 0; i < students.length; i++) {
            result.append(students[i]);
            if(i < students.length - 1) {
                result.append(", ");
            }
        }
        result.append("]}");
        return result.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Faculty faculty = (Faculty) o;
        return Objects.equals(name, faculty.name) && Arrays.equals(students, faculty.students);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(name);
        result = 31 * result + Arrays.hashCode(students);
        return result;
    }
}

public class ContactsTester {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int tests = scanner.nextInt();
        Faculty faculty = null;

        int rvalue = 0;
        long rindex = -1;

        DecimalFormat df = new DecimalFormat("0.00");

        for (int t = 0; t < tests; t++) {

            rvalue++;
            String operation = scanner.next();

            switch (operation) {
                case "CREATE_FACULTY": {
                    String name = scanner.nextLine().trim();
                    int N = scanner.nextInt();

                    Student[] students = new Student[N];

                    for (int i = 0; i < N; i++) {
                        rvalue++;

                        String firstName = scanner.next();
                        String lastName = scanner.next();
                        String city = scanner.next();
                        int age = scanner.nextInt();
                        long index = scanner.nextLong();

                        if ((rindex == -1) || (rvalue % 13 == 0))
                            rindex = index;

                        Student student = new Student(firstName, lastName, city,
                                age, index);
                        students[i] = student;
                    }

                    faculty = new Faculty(name, students);
                    break;
                }

                case "ADD_EMAIL_CONTACT": {
                    long index = scanner.nextInt();
                    String date = scanner.next();
                    String email = scanner.next();

                    rvalue++;

                    if ((rindex == -1) || (rvalue % 3 == 0))
                        rindex = index;

                    faculty.getStudent(index).addEmailContact(date, email);
                    break;
                }

                case "ADD_PHONE_CONTACT": {
                    long index = scanner.nextInt();
                    String date = scanner.next();
                    String phone = scanner.next();

                    rvalue++;

                    if ((rindex == -1) || (rvalue % 3 == 0))
                        rindex = index;

                    faculty.getStudent(index).addPhoneContact(date, phone);
                    break;
                }

                case "CHECK_SIMPLE": {
                    System.out.println("Average number of contacts: "
                            + df.format(faculty.getAverageNumberOfContacts()));

                    rvalue++;

                    String city = faculty.getStudent(rindex).getCity();
                    System.out.println("Number of students from " + city + ": "
                            + faculty.countStudentsFromCity(city));

                    break;
                }

                case "CHECK_DATES": {

                    rvalue++;

                    System.out.print("Latest contact: ");
                    Contact latestContact = faculty.getStudent(rindex)
                            .getLatestContact();
                    if (latestContact.getType().equals("Email"))
                        System.out.println(((EmailContact) latestContact)
                                .getEmail());
                    if (latestContact.getType().equals("Phone"))
                        System.out.println(((PhoneContact) latestContact)
                                .getPhone()
                                + " ("
                                + ((PhoneContact) latestContact).getOperator()
                                .toString() + ")");

                    if (faculty.getStudent(rindex).getEmailContacts().length > 0
                            && faculty.getStudent(rindex).getPhoneContacts().length > 0) {
                        System.out.print("Number of email and phone contacts: ");
                        System.out
                                .println(faculty.getStudent(rindex)
                                        .getEmailContacts().length
                                        + " "
                                        + faculty.getStudent(rindex)
                                        .getPhoneContacts().length);

                        System.out.print("Comparing dates: ");
                        int posEmail = rvalue
                                % faculty.getStudent(rindex).getEmailContacts().length;
                        int posPhone = rvalue
                                % faculty.getStudent(rindex).getPhoneContacts().length;

                        System.out.println(faculty.getStudent(rindex)
                                .getEmailContacts()[posEmail].isNewerThan(faculty
                                .getStudent(rindex).getPhoneContacts()[posPhone]));
                    }

                    break;
                }

                case "PRINT_FACULTY_METHODS": {
                    System.out.println("Faculty: " + faculty.toString());
                    System.out.println("Student with most contacts: "
                            + faculty.getStudentWithMostContacts().toString());
                    break;
                }

            }

        }

        scanner.close();
    }
}
