// Lab 1.2

import java.sql.PreparedStatement;
import java.util.Scanner;
import java.util.stream.IntStream;

public class RomanConverterTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        IntStream.range(0, n)
                .forEach(x -> System.out.println(RomanConverter.toRoman(scanner.nextInt())));
        scanner.close();
    }
}

class RomanConverter {
    /**
     * Roman to decimal converter
     *
     * @param n number in decimal format
     * @return string representation of the number in Roman numeral
     */
    public static String toRoman(int n) {
        String [] roman = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
        int [] values = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};

        StringBuilder result = new StringBuilder();

        for(int i = 0; i < roman.length; i++) {
            while(n >= values[i]) {
                n -= values[i];
                result.append(roman[i]);
            }
        }

        return result.toString();
    }

}
