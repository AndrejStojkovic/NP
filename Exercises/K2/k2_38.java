// K2 38

import java.io.InputStream;
import java.util.*;

class QuizProcessor {
    public static Map<String, Double> processAnswers(InputStream inputStream) {
        Scanner scanner = new Scanner(inputStream);
        Map<String, Double> map = new LinkedHashMap<>();

        while(scanner.hasNextLine()) {
            String [] line = scanner.nextLine().split(";");
            String [] questions = line[1].split(", ");
            String [] answers = line[2].split(", ");

            if(questions.length != answers.length) {
                System.out.println("A quiz must have same number of correct and selected answers");
                continue;
            }

            double result = 0;
            for(int i = 0; i < questions.length; i++) {
                result += questions[i].equals(answers[i]) ? 1 : -0.25;
            }
            map.put(line[0], result);
        }

        return map;
    }
}

public class QuizProcessorTest {
    public static void main(String[] args) {
        QuizProcessor.processAnswers(System.in).forEach((k, v) -> System.out.printf("%s -> %.2f%n", k, v));
    }
}
