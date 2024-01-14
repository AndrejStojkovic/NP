// K2 8

import java.io.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

class InvalidIDException extends Exception {
    InvalidIDException(String id) {
        super("ID " + id + " is not valid");
    }
}

class InvalidDimensionException extends Exception {
    InvalidDimensionException() {
        super("Dimension 0 is not allowed!");
    }
}

interface IShape {
    double getPerimeter();
    double getArea();
    void scale(double coef);
}

class Circle implements IShape {
    double radius;

    Circle(double radius) {
        this.radius = radius;
    }

    @Override
    public double getPerimeter() {
        return radius * 2 * Math.PI;
    }

    @Override
    public double getArea() {
        return radius * radius * Math.PI;
    }

    @Override
    public void scale(double coef) {
        radius *= coef;
    }

    @Override
    public String toString() {
        return String.format("Circle -> Radius: %.2f Area: %.2f Perimeter: %.2f", radius, getArea(), getPerimeter());
    }
}

class Square implements IShape {
    double side;

    Square(double side) {
        this.side = side;
    }

    @Override
    public double getPerimeter() {
        return side * 4;
    }

    @Override
    public double getArea() {
        return side * side;
    }

    @Override
    public void scale(double coef) {
        side *= coef;
    }

    @Override
    public String toString() {
        return String.format("Square: -> Side: %.2f Area: %.2f Perimeter: %.2f", side, getArea(), getPerimeter());
    }
}

class Rectangle implements IShape {
    double w;
    double h;

    Rectangle(double w, double h) {
        this.w = w;
        this.h = h;
    }

    @Override
    public double getPerimeter() {
        return w * 2 + h * 2;
    }

    @Override
    public double getArea() {
        return w * h;
    }

    @Override
    public void scale(double coef) {
        w *= coef;
        h *= coef;
    }

    @Override
    public String toString() {
        return String.format("Rectangle: -> Sides: %.2f, %.2f Area: %.2f Perimeter: %.2f", w, h, getArea(), getPerimeter());
    }
}

class ShapeCollection {
    String id;
    List<IShape> shapes;

    ShapeCollection(String id) {
        this.id = id;
        shapes = new ArrayList<>();
    }

    public void addShape(IShape shape) {
        shapes.add(shape);
    }

    public void scale(double coef) {
        shapes.forEach(x -> x.scale(coef));
    }

    public int shapesCount() {
        return shapes.size();
    }

    public double sumOfAreas() {
        return shapes.stream().mapToDouble(IShape::getArea).sum();
    }

    @Override
    public String toString() {
        return shapes.stream()
                .sorted(Comparator.comparing(IShape::getPerimeter))
                .map(String::valueOf)
                .collect(Collectors.joining("\n"));
    }
}

class Canvas {
    Map<String, ShapeCollection> users;
    Set<IShape> allShapes;

    Canvas() {
        users = new HashMap<>();
        allShapes = new TreeSet<>(Comparator.comparing(IShape::getArea));
    }

    public void readShapes(InputStream inputStream) throws InvalidDimensionException {
        Scanner scanner = new Scanner(inputStream);

        while(scanner.hasNextLine()) {
            String [] line = scanner.nextLine().split(" ");
            String id = line[1];
            try {
                if(checkId(id)) throw new InvalidIDException(id);
            } catch(InvalidIDException e) {
                System.out.println(e.getMessage());
                continue;
            }

            IShape shape;
            double a = Double.parseDouble(line[2]);
            if(a == 0) throw new InvalidDimensionException();

            if(line[0].equals("1")) {
                shape = new Circle(a);
            } else if(line[0].equals("2")) {
                shape = new Square(a);
            } else {
                double b = Double.parseDouble(line[3]);
                if(b == 0) throw new InvalidDimensionException();
                shape = new Rectangle(a, b);
            }

            users.computeIfAbsent(id, x -> new ShapeCollection(id));
            users.get(id).addShape(shape);
            allShapes.add(shape);
        }
    }

    public void scaleShapes(String userId, double coef) {
        if(!users.containsKey(userId)) {
            return;
        }
        users.get(userId).scale(coef);
    }

    public void printAllShapes(OutputStream outputStream) {
        PrintWriter pw = new PrintWriter(outputStream);
        allShapes.forEach(pw::println);
        pw.flush();
    }

    public void printByUserId(OutputStream outputStream) {
        PrintWriter pw = new PrintWriter(outputStream);

//        users.forEach((key, value) -> pw.println("Shapes of user: " + key));
        users.values().stream()
                .sorted(Comparator.comparing(ShapeCollection::shapesCount).reversed().thenComparing(ShapeCollection::sumOfAreas))
                .forEach(x -> {
                    pw.println("Shapes of user: " + x.id);
                    pw.println(x);
                });

        pw.flush();
    }

    public void statistics(OutputStream outputStream) {
        PrintWriter pw = new PrintWriter(outputStream);
        DoubleSummaryStatistics ds = allShapes.stream().mapToDouble(IShape::getArea).summaryStatistics();
        pw.println(String.format("count: %d\nsum: %.2f\nmin: %.2f\naverage: %.2f\nmax: %.2f",
                ds.getCount(), ds.getSum(), ds.getMin(), ds.getAverage(), ds.getMax()));
        pw.flush();
    }

    private boolean checkId(String str) {
        if(str.length() != 6) return true;
        for(int i = 0; i < str.length(); i++) {
            if(!Character.isLetterOrDigit(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }
}

public class CanvasTest {
    public static void main(String[] args) {
        Canvas canvas = new Canvas();

        System.out.println("READ SHAPES AND EXCEPTIONS TESTING");

        try {
            canvas.readShapes(System.in);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        System.out.println("BEFORE SCALING");
        canvas.printAllShapes(System.out);
        canvas.scaleShapes("123456", 1.5);
        System.out.println("AFTER SCALING");
        canvas.printAllShapes(System.out);

        System.out.println("PRINT BY USER ID TESTING");
        canvas.printByUserId(System.out);

        System.out.println("PRINT STATISTICS");
        canvas.statistics(System.out);
    }
}
