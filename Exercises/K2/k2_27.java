// K2 27

import java.util.*;
import java.util.stream.Collectors;

class Names {
    Map<String, Integer> names;

    Names() {
        names = new TreeMap<>();
    }

    public void addName(String name) {
        names.putIfAbsent(name, 0);
        names.computeIfPresent(name, (k, v) -> v + 1);
    }

    public void printN(int n) {
        names.entrySet().stream()
                .filter(x -> x.getValue() >= n)
                .forEach(x -> {
                    Set<Character> characters = x.getKey().chars()
                            .mapToObj(c -> (char)Character.toLowerCase(c))
                            .collect(Collectors.toCollection(HashSet::new));
                    System.out.printf("%s (%d) %d\n", x.getKey(), x.getValue(), characters.size());
                });
    }

    public String findName(int len, int idx) {
        List<String> filtered = names.keySet().stream()
                .filter(x -> x.length() < len)
                .collect(Collectors.toList());

        int ct = 0, targetIdx = idx % filtered.size();
        for(String name : filtered) {
            if(ct == targetIdx) return name;
            ct = (ct + 1) % filtered.size();
        }

        return "Not found";
    }
}

public class NamesTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        Names names = new Names();
        for (int i = 0; i < n; ++i) {
            String name = scanner.nextLine();
            names.addName(name);
        }
        n = scanner.nextInt();
        System.out.printf("===== PRINT NAMES APPEARING AT LEAST %d TIMES =====\n", n);
        names.printN(n);
        System.out.println("===== FIND NAME =====");
        int len = scanner.nextInt();
        int index = scanner.nextInt();
        System.out.println(names.findName(len, index));
        scanner.close();

    }
}
