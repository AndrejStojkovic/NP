// K1 13

import java.util.*;

class InvalidPositionException extends Exception {
    InvalidPositionException(int position) {
        super(String.format("Invalid position %d, alredy taken!", position));
    }
}

class Component implements Comparable<Component> {
    String color;
    int weight;
    Set<Component> components;

    Component() {
        color = "none";
        weight = 0;
        components = new TreeSet<>();
    }

    Component(String color, int weight) {
        this.color = color;
        this.weight = weight;
        this.components = new TreeSet<>();
    }

    public String getColor() {
        return color;
    }

    public int getWeight() {
        return weight;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void addComponent(Component component) {
        components.add(component);
    }

    public void changeColor(int weight, String color) {
        if(this.weight < weight) {
            this.color = color;
        }
        for(Component c : components) {
            c.changeColor(weight, color);
        }
    }

    public String format(String lines) {
        StringBuilder str = new StringBuilder();
        str.append(String.format("%s%d:%s\n", lines, weight, color));
        components.forEach(x -> str.append(x.format(lines + "---")));
        return str.toString();
    }

    @Override
    public int compareTo(Component o) {
        if(weight == o.getWeight()) {
            return color.compareTo(o.getColor());
        }
        return Integer.compare(weight, o.getWeight());
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(String.format("%d:%s\n", weight, color));
        components.forEach(x -> str.append(x.format("---")));
        return str.toString();
    }
}

class Window {
    String name;
    Map<Integer, Component> components;

    Window() {
        name = "none";
        components = new TreeMap<Integer, Component>();
    }

    Window(String name) {
        this.name = name;
        this.components = new TreeMap<Integer, Component>();
    }

    public String getName() {
        return name;
    }

    public void addComponent(int position, Component component) throws InvalidPositionException {
        if(components.containsKey(position)) {
            throw new InvalidPositionException(position);
        }
        components.put(position, component);
    }

    public void changeColor(int weight, String color) {
        components.values().forEach(c -> c.changeColor(weight, color));
    }

    public void swichComponents(int pos1, int pos2) {
        Component temp = components.get(pos1);
        components.put(pos1, components.get(pos2));
        components.put(pos2, temp);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(String.format("WINDOW %s\n", name));
        int idx = 0;
        for(Component c : components.values()) {
            str.append(idx + 1).append(":").append(c);
            idx++;
        }
        return str.toString();
    }
}

public class ComponentTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String name = scanner.nextLine();
        Window window = new Window(name);
        Component prev = null;
        while (true) {
            try {
                int what = scanner.nextInt();
                scanner.nextLine();
                if (what == 0) {
                    int position = scanner.nextInt();
                    window.addComponent(position, prev);
                } else if (what == 1) {
                    String color = scanner.nextLine();
                    int weight = scanner.nextInt();
                    Component component = new Component(color, weight);
                    prev = component;
                } else if (what == 2) {
                    String color = scanner.nextLine();
                    int weight = scanner.nextInt();
                    Component component = new Component(color, weight);
                    prev.addComponent(component);
                    prev = component;
                } else if (what == 3) {
                    String color = scanner.nextLine();
                    int weight = scanner.nextInt();
                    Component component = new Component(color, weight);
                    prev.addComponent(component);
                } else if(what == 4) {
                    break;
                }

            } catch (InvalidPositionException e) {
                System.out.println(e.getMessage());
            }
            scanner.nextLine();
        }

        System.out.println("=== ORIGINAL WINDOW ===");
        System.out.println(window);
        int weight = scanner.nextInt();
        scanner.nextLine();
        String color = scanner.nextLine();
        window.changeColor(weight, color);
        System.out.println(String.format("=== CHANGED COLOR (%d, %s) ===", weight, color));
        System.out.println(window);
        int pos1 = scanner.nextInt();
        int pos2 = scanner.nextInt();
        System.out.println(String.format("=== SWITCHED COMPONENTS %d <-> %d ===", pos1, pos2));
        window.swichComponents(pos1, pos2);
        System.out.println(window);
    }
}
