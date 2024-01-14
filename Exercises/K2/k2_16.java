// K2 16

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class BlockContainer<T extends Comparable<T>> {
    List<List<T>> blocks;
    int size;

    BlockContainer(int size) {
        this.blocks = new ArrayList<>();
        this.size = size;
    }

    public void add(T block) {
        if(blocks.isEmpty() || blocks.get(blocks.size() - 1).size() == size) {
            blocks.add(new ArrayList<>());
        }
        blocks.get(blocks.size() - 1).add(block);
        Collections.sort(blocks.get(blocks.size() - 1));
    }

    public boolean remove(T block) {
        for(List<T> list : blocks) {
            if(list.removeIf(x -> x.equals(block))) {
                if(list.isEmpty()) blocks.remove(list);
                return true;
            }
        }
        return false;
    }

    public void sort() {
        List<T> sorted = blocks.stream().flatMap(Collection::stream).sorted().collect(Collectors.toList());
        blocks.clear();
        sorted.forEach(this::add);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        IntStream.range(0, blocks.size()).forEach(i -> {
            str.append(blocks.get(i).toString());
            if(i < blocks.size() - 1) str.append(",");
        });
        return str.toString();
    }
}

public class BlockContainerTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int size = scanner.nextInt();
        BlockContainer<Integer> integerBC = new BlockContainer<Integer>(size);
        scanner.nextLine();
        Integer lastInteger = null;
        for(int i = 0; i < n; ++i) {
            int element = scanner.nextInt();
            lastInteger = element;
            integerBC.add(element);
        }
        System.out.println("+++++ Integer Block Container +++++");
        System.out.println(integerBC);
        System.out.println("+++++ Removing element +++++");
        integerBC.remove(lastInteger);
        System.out.println("+++++ Sorting container +++++");
        integerBC.sort();
        System.out.println(integerBC);
        BlockContainer<String> stringBC = new BlockContainer<String>(size);
        String lastString = null;
        for(int i = 0; i < n; ++i) {
            String element = scanner.next();
            lastString = element;
            stringBC.add(element);
        }
        System.out.println("+++++ String Block Container +++++");
        System.out.println(stringBC);
        System.out.println("+++++ Removing element +++++");
        stringBC.remove(lastString);
        System.out.println("+++++ Sorting container +++++");
        stringBC.sort();
        System.out.println(stringBC);
    }
}
