// K1 2

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.*;

class IrregularCanvasException extends Exception {
    IrregularCanvasException(String id, double maxArea) {
        super(String.format("Canvas %s has a shape with area larger than %.2f", id, maxArea));
    }
}

enum Type {
    Square,
    Circle
}

class Shape implements Comparable<Shape> {
    protected Type type;
    protected int size;

    Shape(Type type, int size) {
        this.type = type;
        this.size = size;
    }

    public Type getType() {
        return type;
    }

    public double getArea() {
        return 0;
    }

    @Override
    public int compareTo(Shape o) {
        return Double.compare(getArea(), o.getArea());
    }
}

class Square extends Shape {
    public Square(int size) {
        super(Type.Square, size);
    }

    @Override
    public double getArea() {
        return size * size;
    }
}

class Circle extends Shape {
    public Circle(int size) {
        super(Type.Circle, size);
    }

    @Override
    public double getArea() {
        return size * size * Math.PI;
    }
}

class Canvas implements Comparable<Canvas> {
    private String canvasId;
    private ArrayList<Shape> shapes;

    Canvas() {
        canvasId = "";
        shapes = new ArrayList<>();
    }

    Canvas(String canvasId, ArrayList<Shape> shapes) {
        this.canvasId = canvasId;
        this.shapes = shapes;
    }

    public String getCanvasId() {
        return canvasId;
    }

    public double minArea() {
        return Collections.min(shapes).getArea();
    }

    public double maxArea() {
        return Collections.max(shapes).getArea();
    }

    public double averageArea() {
        return shapes.stream().mapToDouble(Shape::getArea).average().orElse(0);
    }

    public double getTotalArea() {
        return shapes.stream().mapToDouble(Shape::getArea).sum();
    }

    public long getSquares() {
        return shapes.stream().filter(x -> x.getType().equals(Type.Square)).count();
    }

    public long getCircles() {
        return shapes.stream().filter(x -> x.getType().equals(Type.Circle)).count();
    }

    @Override
    public String toString() {
        return String.format("%s %d %d %d %.2f %.2f %.2f", canvasId, shapes.size(), getCircles(), getSquares(), minArea(), maxArea(), averageArea());
    }

    @Override
    public int compareTo(Canvas o) {
        return Double.compare(getTotalArea(), o.getTotalArea());
    }
}

class ShapesApplication {
    private ArrayList<Canvas> list;
    private double maxArea;
    private

    ShapesApplication() {
        list = new ArrayList<>();
        maxArea = 0;
    }

    ShapesApplication(double maxArea) {
        this.list = new ArrayList<>();
        this.maxArea = maxArea;
    }

    public void addCanvas(Canvas c) throws IrregularCanvasException {
        if(c.maxArea() > maxArea) {
            throw new IrregularCanvasException(c.getCanvasId(), maxArea);
        }
        list.add(c);
    }

    public void readCanvases(InputStream inputStream) {
        Scanner input = new Scanner(inputStream);

        while(input.hasNextLine()) {
            String canvas = input.nextLine();

            String canvasId = canvas.split(" ")[0];
            String [] str = canvas.split(" ");

            ArrayList<Shape> shapes = new ArrayList<>();

            for(int i = 1; i < str.length; i += 2) {
                int value = Integer.parseInt(str[i + 1]);

                if(Objects.equals(str[i], "C")) {
                    shapes.add(new Circle(value));
                } else if(Objects.equals(str[i], "S")) {
                    shapes.add(new Square(value));
                }
            }

            try {
                addCanvas(new Canvas(canvasId, shapes));
            } catch(Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void printCanvases(OutputStream outputStream) {
        PrintWriter pw = new PrintWriter(outputStream);
        list.stream().sorted(Collections.reverseOrder()).forEach(pw::println);
        pw.flush();
    }
}

public class Shapes2Test {
    public static void main(String[] args) {
        ShapesApplication shapesApplication = new ShapesApplication(10000);

        System.out.println("===READING CANVASES AND SHAPES FROM INPUT STREAM===");
        shapesApplication.readCanvases(System.in);

        System.out.println("===PRINTING SORTED CANVASES TO OUTPUT STREAM===");
        shapesApplication.printCanvases(System.out);
    }
}
