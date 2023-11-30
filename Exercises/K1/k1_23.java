// K1 23

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

class InvalidOperationException extends Exception {
    InvalidOperationException(String opt) {
        super(String.format("%s is not allowed option for this question", opt));
    }
}

class Question implements Comparable<Question> {
    String type;
    String name;
    int points;
    String answer;

    Question() {
        type = "none";
        name = "none";
        points = 0;
        answer = null;
    }

    Question(String type, String name, int points, String answer) {
        this.type = type;
        this.name = name;
        this.points = points;
        this.answer = answer;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public int getPoints() {
        return points;
    }

    public String getAnswer() {
        return answer;
    }

    @Override
    public String toString() {
        String questionType = type.equals("MC") ? "Multiple Choice" : "True/False";
        String c = type.equals("TF") ? ":" : "";
        return String.format("%s Question: %s Points%s %d Answer: %s", questionType, name, c, points, answer);
    }

    @Override
    public int compareTo(Question o) {
        return Integer.compare(o.getPoints(), points);
    }
}

class Quiz {
    ArrayList<Question> questions;

    Quiz() {
        questions = new ArrayList<>();
    }

    void addQuestion(String questionData) throws InvalidOperationException {
        String [] data = questionData.split(";");

        if(Objects.equals(data[0], "MC")) {
            String [] validAnswers = {"A", "B", "C", "D", "E"};
            boolean valid = false;
            for(String ans : validAnswers) {
                if (Objects.equals(data[3], ans)) {
                    valid = true;
                    break;
                }
            }
            if(!valid) throw new InvalidOperationException(data[3]);
        }

        questions.add(new Question(data[0], data[1], Integer.parseInt(data[2]), data[3]));
    }

    void printQuiz(OutputStream outputStream) {
        PrintWriter pw = new PrintWriter(outputStream);
        questions.stream().sorted(Question::compareTo).forEach(pw::println);
        pw.flush();
    }

    void answerQuiz(List<String> answers, OutputStream outputStream) {
        if(answers.size() != questions.size()){
            System.out.println("Answers and questions must be of same length!");
            return;
        }

        PrintWriter pw = new PrintWriter(outputStream);

        double totalPoints = 0;

        for(int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            String ans = answers.get(i);

            double points = 0.0;

            if(q.getType().equals("TF") && q.getAnswer().equals(ans)) {
                points = q.getPoints();
            } else if(q.getType().equals("MC")) {
                if(q.getAnswer().equals(ans)) points = q.getPoints();
                else points = -(q.getPoints() * 0.2);
            }

            totalPoints += points;
            pw.println(String.format("%d. %.2f", i + 1, points));
        }

        pw.println(String.format("Total points: %.2f", totalPoints));
        pw.flush();
    }
}

public class QuizTest {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        Quiz quiz = new Quiz();

        int questions = Integer.parseInt(sc.nextLine());

        for (int i=0;i<questions;i++) {
            try {
                quiz.addQuestion(sc.nextLine());
            } catch(Exception e) {
                System.out.println(e.getMessage());
            }
        }

        List<String> answers = new ArrayList<>();

        int answersCount =  Integer.parseInt(sc.nextLine());

        for (int i=0;i<answersCount;i++) {
            answers.add(sc.nextLine());
        }

        int testCase = Integer.parseInt(sc.nextLine());

        if (testCase==1) {
            quiz.printQuiz(System.out);
        } else if (testCase==2) {
            quiz.answerQuiz(answers, System.out);
        } else {
            System.out.println("Invalid test case");
        }
    }
}
