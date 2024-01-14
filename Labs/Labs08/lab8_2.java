// Lab 8.2

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

interface XMLComponent {
    void addAttribute(String attribute, String value);
    String getType();
}

class XML implements XMLComponent {
    String tag;
    Map<String, String> attributes;

    XML(String tag) {
        this.tag = tag;
        this.attributes = new TreeMap<>(Comparator.reverseOrder());
    }

    @Override
    public void addAttribute(String attribute, String value) {
        attributes.put(attribute, value);
    }

    @Override
    public String getType() {
        return "COMPONENT";
    }
}

class XMLComposite extends XML {
    List<XMLComponent> components;

    XMLComposite(String tag) {
        super(tag);
        components = new ArrayList<>();
    }

    public void addComponent(XMLComponent component) {
        components.add(component);
    }

    @Override
    public String getType() {
        return "COMPOSITE";
    }

    String toStringRecursive(XMLComposite xml, int level) {
        StringBuilder str = new StringBuilder();

        str.append("\t".repeat(level));

        str.append("<").append(xml.tag);
        if(!xml.attributes.isEmpty()) {
            str.append(" ").append(xml.attributes.entrySet().stream().sorted().map(x ->
                    String.format("%s=\"%s\"", x.getKey(), x.getValue())).collect(Collectors.joining(" ")));
        }
        str.append(">\n");

        for (XMLComponent component : xml.components) {
            if (component.getType().equals("LEAF")) {
                str.append("\t".repeat(level + 1)).append(component).append("\n");
            } else if (component.getType().equals("COMPOSITE")) {
                str.append(toStringRecursive((XMLComposite) component, level + 1)).append("\n");
            }
        }

        str.append("\t".repeat(level));
        str.append("</").append(xml.tag).append(">");
        return str.toString();
    }

    @Override
    public String toString() {
        return toStringRecursive(this, 0);
    }
}

class XMLLeaf extends XML {
    String value;

    XMLLeaf(String tag, String value) {
        super(tag);
        this.value = value;
    }

    @Override
    public String getType() {
        return "LEAF";
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("<").append(tag);
        if(!attributes.isEmpty()) {
            str.append(" ");
            str.append(attributes.entrySet().stream().map(x ->
                    String.format("%s=\"%s\"", x.getKey(), x.getValue())).collect(Collectors.joining(" ")));
        }
        str.append(">").append(value).append("</").append(tag).append(">");
        return str.toString();
    }
}

public class XMLTest {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int testCase = sc.nextInt();
        XMLComponent component = new XMLLeaf("student", "Trajce Trajkovski");
        component.addAttribute("type", "redoven");
        component.addAttribute("program", "KNI");

        XMLComposite composite = new XMLComposite("name");
        composite.addComponent(new XMLLeaf("first-name", "trajce"));
        composite.addComponent(new XMLLeaf("last-name", "trajkovski"));
        composite.addAttribute("type", "redoven");
        component.addAttribute("program", "KNI");

        if (testCase==1) {
            System.out.println(component);
        } else if(testCase==2) {
            System.out.println(composite);
        } else if (testCase==3) {
            XMLComposite main = new XMLComposite("level1");
            main.addAttribute("level","1");
            XMLComposite lvl2 = new XMLComposite("level2");
            lvl2.addAttribute("level","2");
            XMLComposite lvl3 = new XMLComposite("level3");
            lvl3.addAttribute("level","3");
            lvl3.addComponent(component);
            lvl2.addComponent(lvl3);
            lvl2.addComponent(composite);
            lvl2.addComponent(new XMLLeaf("something", "blabla"));
            main.addComponent(lvl2);
            main.addComponent(new XMLLeaf("course", "napredno programiranje"));
            System.out.println(main);
        }
    }
}
