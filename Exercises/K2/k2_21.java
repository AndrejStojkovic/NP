// K2 21

import java.util.*;
import java.util.stream.Collectors;

class DuplicateNumberException extends Exception {
    DuplicateNumberException(String number) {
        super("Duplicate number: " + number);
    }
}

class PhoneBook {
    Map<String, Set<String>> contacts;

    PhoneBook() {
        contacts = new HashMap<>();
    }

    public void addContact(String name, String number) throws DuplicateNumberException {
        contacts.computeIfAbsent(name, x -> new TreeSet<>());
        if(contacts.get(name).contains(number)) {
            throw new DuplicateNumberException(number);
        }
        contacts.computeIfPresent(name, (k, v) -> { v.add(number); return v; });
    }

    public void contactsByNumber(String number) {
        Map<String, String> allContacts = new HashMap<>();

        for(String contact : contacts.keySet()) {
            for(String num : contacts.get(contact)) {
                allContacts.put(num, contact);
            }
        }

        allContacts = allContacts.entrySet().stream().filter(x -> x.getKey().contains(number))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b, HashMap::new));

        if(allContacts.isEmpty()) {
            System.out.println("NOT FOUND");
            return;
        }

        allContacts.entrySet().stream()
                .sorted(Map.Entry.<String, String>comparingByValue().thenComparing(Map.Entry.comparingByKey()))
                .forEach(x -> System.out.println(x.getValue() + " " + x.getKey()));
    }

    public void contactsByName(String name) {
        if(!contacts.containsKey(name)) {
            System.out.println("NOT FOUND");
            return;
        }

//        if(contacts.get(name).isEmpty()) {
//            return;
//        }

//        System.out.println("[DEBUG] " + name + " contacts: " + contacts.size());

        contacts.get(name).forEach(x -> System.out.println(name + " " + x));
    }
}

public class PhoneBookTest {
    public static void main(String[] args) {
        PhoneBook phoneBook = new PhoneBook();
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] parts = line.split(":");
            try {
                phoneBook.addContact(parts[0], parts[1]);
            } catch (DuplicateNumberException e) {
                System.out.println(e.getMessage());
            }
        }
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            System.out.println(line);
            String[] parts = line.split(":");
            if (parts[0].equals("NUM")) {
                phoneBook.contactsByNumber(parts[1]);
            } else {
                phoneBook.contactsByName(parts[1]);
            }
        }
    }
}
