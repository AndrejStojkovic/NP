// K1 5

import java.util.Scanner;

class MinMax<T extends Comparable<T>> {
    T minElement;
    T maxElement;

    int updates;
    int countMin;
    int countMax;

    MinMax() {
        minElement = null;
        maxElement = null;

        updates = 0;
        countMin = 0;
        countMax = 0;
    }

    void update(T element) {
        if(element == null) {
            return;
        }

        if(minElement == null || minElement.compareTo(element) > 0) {
            minElement = element;
            countMin = 1;
        } else if(minElement.compareTo(element) == 0) {
            countMin++;
        }

        if(maxElement == null || maxElement.compareTo(element) < 0) {
            maxElement = element;
            countMax = 1;
        } else if(maxElement.compareTo(element) == 0) {
            countMax++;
        }

        updates++;
    }

    T max() {
        return maxElement;
    }

    T min() {
        return minElement;
    }

    @Override
    public String toString() {
        return String.format("%s %s %d\n", min(), max(), updates - countMax - countMin);
    }
}

public class MinAndMax {
    public static void main(String[] args) throws ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        MinMax<String> strings = new MinMax<String>();
        for(int i = 0; i < n; ++i) {
            String s = scanner.next();
            strings.update(s);
        }
        System.out.println(strings);
        MinMax<Integer> ints = new MinMax<Integer>();
        for(int i = 0; i < n; ++i) {
            int x = scanner.nextInt();
            ints.update(x);
        }
        System.out.println(ints);
    }
}
