// Lab 7.4

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

class TermFrequency {
    Map<String, Integer> words;
    int total;

    TermFrequency(InputStream inputStream, String[] stopWords) {
        words = new TreeMap<>();
        total = 0;

        Scanner scanner = new Scanner(inputStream);
        List<String> stop = Arrays.asList(stopWords);

        while(scanner.hasNext()) {
            String w = scanner.next();
            w = w.toLowerCase().replace(',', '\0').replace('.', '\0').trim();
            if(w.isEmpty() || stop.contains(w)) continue;
            int v = words.computeIfAbsent(w, x -> 0);
            words.put(w, ++v);
            total++;
        }
    }

    public int countTotal() {
        return total;
    }

    public int countDistinct() {
        return words.size();
    }

    public List<String> mostOften(int k) {
        return words.keySet().stream()
                .sorted(Comparator.comparing(x -> words.get(x)).reversed())
                .limit(k)
                .collect(Collectors.toList());
    }
}

public class TermFrequencyTest {
    public static void main(String[] args) throws FileNotFoundException {
        String[] stop = new String[] { "во", "и", "се", "за", "ќе", "да", "од",
                "ги", "е", "со", "не", "тоа", "кои", "до", "го", "или", "дека",
                "што", "на", "а", "но", "кој", "ја" };
        TermFrequency tf = new TermFrequency(System.in,
                stop);
        System.out.println(tf.countTotal());
        System.out.println(tf.countDistinct());
        System.out.println(tf.mostOften(10));
    }
}
