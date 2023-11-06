// Lab 6.2

import java.util.*;
import java.util.stream.Collectors;

class SuperString {
    private LinkedList<String> list;
    private List<String> lastAdded;

    SuperString() {
        list = new LinkedList<>();
        lastAdded = new ArrayList<>();
    }

    public void append(String s) {
        list.add(s);
        lastAdded.add(0, s);
    }

    public void insert(String s) {
        list.add(0, s);
        lastAdded.add(0, s);
    }

    public boolean contains(String s) {
        return toString().contains(s);
    }

    public void reverse() {
        Collections.reverse(list);
        for(int i = 0; i < list.size(); i++) {
            list.set(i, reverseString(list.get(i)));
            lastAdded.set(i, reverseString(lastAdded.get(i)));
        }
    }

    public void removeLast(int k) {
        if(list.isEmpty()) return;
        for(int i = 0; i < k; i++) {
            list.remove(lastAdded.get(i));
        }
        lastAdded = lastAdded.subList(k, lastAdded.size());
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for(String s : list) {
            str.append(s);
        }
        return str.toString();
    }

    public String reverseString(String s) {
        StringBuilder reverse = new StringBuilder();
        for(int i = s.length() - 1; i >= 0; i--) reverse.append(s.charAt(i));
        return reverse.toString();
    }
}

public class SuperStringTest {

    public static void main(String[] args) {
        Scanner jin = new Scanner(System.in);
        int k = jin.nextInt();
        if (  k == 0 ) {
            SuperString s = new SuperString();
            while ( true ) {
                int command = jin.nextInt();
                if ( command == 0 ) {//append(String s)
                    s.append(jin.next());
                }
                if ( command == 1 ) {//insert(String s)
                    s.insert(jin.next());
                }
                if ( command == 2 ) {//contains(String s)
                    System.out.println(s.contains(jin.next()));
                }
                if ( command == 3 ) {//reverse()
                    s.reverse();
                }
                if ( command == 4 ) {//toString()
                    System.out.println(s);
                }
                if ( command == 5 ) {//removeLast(int k)
                    s.removeLast(jin.nextInt());
                }
                if ( command == 6 ) {//end
                    break;
                }
            }
        }
    }

}
