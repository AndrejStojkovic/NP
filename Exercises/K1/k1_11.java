// K1 11

import java.util.Scanner;

class ZeroDenominatorException extends Exception {
    ZeroDenominatorException() {
        super("Denominator cannot be zero");
    }
}

class GenericFraction<T extends Number, U extends Number> {
    T numerator;
    U denominator;

    GenericFraction(T numerator, U denominator) throws ZeroDenominatorException {
        if(denominator.doubleValue() == 0) {
            throw new ZeroDenominatorException();
        }
        this.numerator = numerator;
        this.denominator = denominator;
    }

    public GenericFraction<Double, Double> add(GenericFraction<? extends Number, ? extends Number> gf) throws ZeroDenominatorException {
        GenericFraction<Double, Double> r = null;
        try {
            Double b = getDenominator() * gf.getDenominator();
            Double a = (getNumerator() * (b / getDenominator()) + gf.getNumerator() * (b / gf.getDenominator()));
            r = new GenericFraction<Double, Double>(a, b);
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
        return r;
    }

    public double toDouble() {
        return getNumerator() / getDenominator();
    }

    public Double getNumerator() {
        return numerator.doubleValue();
    }

    public Double getDenominator() {
        return denominator.doubleValue();
    }

    public double nzs(double a, double b) {
        return b == 0 ? a : nzs(b, a % b);
    }

    @Override
    public String toString() {
        double nzs = nzs(getNumerator(), getDenominator());
        return String.format("%.2f / %.2f", getNumerator() / nzs, getDenominator() / nzs);
    }
}

public class GenericFractionTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        double n1 = scanner.nextDouble();
        double d1 = scanner.nextDouble();
        float n2 = scanner.nextFloat();
        float d2 = scanner.nextFloat();
        int n3 = scanner.nextInt();
        int d3 = scanner.nextInt();
        try {
            GenericFraction<Double, Double> gfDouble = new GenericFraction<Double, Double>(n1, d1);
            GenericFraction<Float, Float> gfFloat = new GenericFraction<Float, Float>(n2, d2);
            GenericFraction<Integer, Integer> gfInt = new GenericFraction<Integer, Integer>(n3, d3);
            System.out.printf("%.2f\n", gfDouble.toDouble());
            System.out.println(gfDouble.add(gfFloat));
            System.out.println(gfInt.add(gfFloat));
            System.out.println(gfDouble.add(gfInt));
            gfInt = new GenericFraction<Integer, Integer>(n3, 0);
        } catch(ZeroDenominatorException e) {
            System.out.println(e.getMessage());
        }

        scanner.close();
    }
}