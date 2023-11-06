// Lab 3.2

import java.io.*;
import java.util.*;

class InvalidNameException extends Exception {
    public String name;
    InvalidNameException(String name) { this.name = name; }
}

class InvalidNumberException extends Exception {
    InvalidNumberException() { }
}

class MaximumSizeExceddedException extends Exception {
    MaximumSizeExceddedException() { }
}

class InvalidFormatException extends Exception {
    InvalidFormatException() { }
}

class Contact {
    private String name;
    private String [] phoneNumbers;

    Contact(String name, String... phoneNumbers) throws InvalidNameException, InvalidNumberException, MaximumSizeExceddedException {
        if(name.length() < 4 || name.length() > 10) {
            throw new InvalidNameException(name);
        }

        if(!isValidName(name)) {
            throw new InvalidNameException(name);
        }

        if(phoneNumbers.length > 5) {
            throw new MaximumSizeExceddedException();
        }

        if(!isValidNumbers(phoneNumbers)) {
            throw new InvalidNumberException();
        }

        this.name = name;
        this.phoneNumbers = phoneNumbers;
    }

    public String getName() {
        return name;
    }

    public String [] getNumbers() {
        String [] sortedNumbers = Arrays.copyOf(phoneNumbers, phoneNumbers.length);
        Arrays.sort(sortedNumbers);
        return sortedNumbers;
    }

    public void addNumber(String number) throws MaximumSizeExceddedException, InvalidNumberException {
        if(phoneNumbers.length >= 5) {
            throw new MaximumSizeExceddedException();
        }

        if (!isValidNumber(number)) {
            throw new InvalidNumberException();
        }

        int len = phoneNumbers.length;
        String [] newPhoneNumbers = Arrays.copyOf(phoneNumbers, len + 1);
        newPhoneNumbers[len] = number;
        phoneNumbers = newPhoneNumbers;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(name).append("\n").append(phoneNumbers.length).append("\n");
        String [] sortedNumbers = getNumbers();
        for(String num : sortedNumbers) {
            str.append(num).append("\n");
        }
        return str.toString();
    }

    public static Contact valueOf(String s) throws InvalidFormatException {
        String [] data = s.split("-");
        String [] numbers = data[1].split("_");

        try {
            return new Contact(data[0], numbers);
        } catch(Exception e) {
            throw new InvalidFormatException();
        }
    }

    public boolean isValidName(String targetName) {
        for(int i = 0; i < targetName.length(); i++) {
            char c = targetName.charAt(i);
            if(!Character.isDigit(c) && !Character.isLetter(c)) {
                return false;
            }
        }
        return true;
    }

    public boolean isValidNumbers(String [] numbers) {
        for (String num : numbers) {
            if(!isValidNumber(num)) {
                return false;
            }
        }
        return true;
    }

    public boolean isValidNumber(String targetNumber) {
        String [] validPrefix = { "070", "071", "072", "075", "076", "077", "078" };

        if(targetNumber.length() != 9) {
            return false;
        }

        try {
            Integer.parseInt(targetNumber);
        } catch(Exception e) {
            return false;
        }

        String prefix = targetNumber.substring(0, 3);
        for (String s : validPrefix) {
            if (Objects.equals(prefix, s)) {
                return true;
            }
        }

        return false;
    }

    public boolean findNumberWithPrefix(String prefix) {
        for(String num : phoneNumbers) {
            String sub = num.substring(0, 3);
            if(prefix.equals(sub)) {
                return true;
            }
        }
        return false;
    }

    public int compare(Contact c) {
        return name.compareTo(c.getName());
    }
}

class PhoneBook {
    private Contact [] contacts;

    PhoneBook() {
        this.contacts = new Contact[0];
    }

    public void addContact(Contact c) throws MaximumSizeExceddedException, InvalidNameException {
        String name = c.getName();

        for (Contact contact : contacts) {
            if (Objects.equals(name, contact.getName())) {
                throw new InvalidNameException(name);
            }
        }

        if(contacts.length >= 250) {
            throw new MaximumSizeExceddedException();
        }

        Contact [] newContacts = Arrays.copyOf(contacts, contacts.length + 1);
        newContacts[newContacts.length - 1] = c;
        contacts = newContacts;
    }

    public boolean removeContact(String name) {
        int idx = getContact(name);
        if(idx != -1) {
            Contact [] newContacts = Arrays.copyOf(contacts, contacts.length - 1);
            int ct = 0;
            for(int i = 0; i < contacts.length; i++) {
                if(idx != i) newContacts[ct++] = contacts[i];
            }
            contacts = newContacts;
            return true;
        }
        return false;
    }

    public Contact getContactForName(String name) {
        int idx = getContact(name);
        if(idx != -1) return contacts[idx];
        return null;
    }

    public Contact [] getContactsForNumber(String prefix) {
        Contact [] temp = new Contact[contacts.length];
        int idx = 0;

        for(Contact c : contacts) {
            if(c.findNumberWithPrefix(prefix)) {
                temp[idx++] = c;
            }
        }

        if(idx == 0) return null;
        return Arrays.copyOf(temp, idx);
    }

    public int numberOfContacts() {
        return contacts.length;
    }

    public Contact [] getContacts() {
        Contact [] sorted = Arrays.copyOf(contacts, contacts.length);
        for(int i = 0; i < sorted.length - 1; i++) {
            for(int j = i + 1; j < sorted.length; j++) {
                if(sorted[i].compare(sorted[j]) > 0) {
                    Contact temp = sorted[i];
                    sorted[i] = sorted[j];
                    sorted[j] = temp;
                }
            }
        }
        return sorted;
    }

    private int getContact(String name) {
        for(int i = 0; i < contacts.length; i++) {
            if(Objects.equals(name, contacts[i].getName())) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public String toString() {
        Contact [] sorted = getContacts();
        StringBuilder str = new StringBuilder();
        for(Contact c : sorted) {
            str.append(c).append("\n");
        }
        return str.toString();
    }

    public static boolean saveAsTextFile(PhoneBook phonebook, String path) {
        try {
            PrintWriter pw = new PrintWriter(path);
            pw.println(phonebook.toString());
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static PhoneBook loadFromTextFile(String path) throws IOException, InvalidFormatException {
        BufferedReader bf = new BufferedReader(new FileReader(path));
        String line = null;
        PhoneBook phoneBook = new PhoneBook();

        while((line = bf.readLine()) != null){
            StringBuilder toConvert = new StringBuilder(line + "-");
            int n = Integer.parseInt(bf.readLine());

            for(int i = 0; i < n; i++){
                if(i == n - 1) {
                    toConvert.append(bf.read());
                } else {
                    toConvert.append(bf.readLine()).append("_");
                }
            }

            Contact toAdd = Contact.valueOf(toConvert.toString());

            try {
                phoneBook.addContact(toAdd);
            } catch (Exception ignore) {
                throw new InvalidFormatException();
            }

            bf.readLine();
        }
        return phoneBook;
    }
}

public class PhonebookTester {
    public static void main(String[] args) throws Exception {
        Scanner jin = new Scanner(System.in);
        String line = jin.nextLine();
        switch( line ) {
            case "test_contact":
                testContact(jin);
                break;
            case "test_phonebook_exceptions":
                testPhonebookExceptions(jin);
                break;
            case "test_usage":
                testUsage(jin);
                break;
        }
    }

    private static void testFile(Scanner jin) throws Exception {
        PhoneBook phonebook = new PhoneBook();
        while ( jin.hasNextLine() )
            phonebook.addContact(new Contact(jin.nextLine(),jin.nextLine().split("\\s++")));
        String text_file = "phonebook.txt";
        PhoneBook.saveAsTextFile(phonebook,text_file);
        PhoneBook pb = PhoneBook.loadFromTextFile(text_file);
        if ( ! pb.equals(phonebook) ) System.out.println("Your file saving and loading doesn't seem to work right");
        else System.out.println("Your file saving and loading works great. Good job!");
    }

    private static void testUsage(Scanner jin) throws Exception {
        PhoneBook phonebook = new PhoneBook();
        while ( jin.hasNextLine() ) {
            String command = jin.nextLine();
            switch ( command ) {
                case "add":
                    phonebook.addContact(new Contact(jin.nextLine(),jin.nextLine().split("\\s++")));
                    break;
                case "remove":
                    phonebook.removeContact(jin.nextLine());
                    break;
                case "print":
                    System.out.println(phonebook.numberOfContacts());
                    System.out.println(Arrays.toString(phonebook.getContacts()));
                    System.out.println(phonebook.toString());
                    break;
                case "get_name":
                    System.out.println(phonebook.getContactForName(jin.nextLine()));
                    break;
                case "get_number":
                    System.out.println(Arrays.toString(phonebook.getContactsForNumber(jin.nextLine())));
                    break;
            }
        }
    }

    private static void testPhonebookExceptions(Scanner jin) {
        PhoneBook phonebook = new PhoneBook();
        boolean exception_thrown = false;
        try {
            while ( jin.hasNextLine() ) {
                phonebook.addContact(new Contact(jin.nextLine()));
            }
        }
        catch ( InvalidNameException e ) {
            System.out.println(e.name);
            exception_thrown = true;
        }
        catch ( Exception e ) {}
        if ( ! exception_thrown ) System.out.println("Your addContact method doesn't throw InvalidNameException");
        /*
		exception_thrown = false;
		try {
		phonebook.addContact(new Contact(jin.nextLine()));
		} catch ( MaximumSizeExceddedException e ) {
			exception_thrown = true;
		}
		catch ( Exception e ) {}
		if ( ! exception_thrown ) System.out.println("Your addContact method doesn't throw MaximumSizeExcededException");
        */
    }

    private static void testContact(Scanner jin) throws Exception {
        boolean exception_thrown = true;
        String names_to_test[] = { "And\nrej","asd","AAAAAAAAAAAAAAAAAAAAAA","Ð�Ð½Ð´Ñ€ÐµÑ˜A123213","Andrej#","Andrej<3"};
        for ( String name : names_to_test ) {
            try {
                new Contact(name);
                exception_thrown = false;
            } catch (InvalidNameException e) {
                exception_thrown = true;
            }
            if ( ! exception_thrown ) System.out.println("Your Contact constructor doesn't throw an InvalidNameException");
        }
        String numbers_to_test[] = { "+071718028","number","078asdasdasd","070asdqwe","070a56798","07045678a","123456789","074456798","073456798","079456798" };
        for ( String number : numbers_to_test ) {
            try {
                new Contact("Andrej",number);
                exception_thrown = false;
            } catch (InvalidNumberException e) {
                exception_thrown = true;
            }
            if ( ! exception_thrown ) System.out.println("Your Contact constructor doesn't throw an InvalidNumberException");
        }
        String nums[] = new String[10];
        for ( int i = 0 ; i < nums.length ; ++i ) nums[i] = getRandomLegitNumber();
        try {
            new Contact("Andrej",nums);
            exception_thrown = false;
        } catch (MaximumSizeExceddedException e) {
            exception_thrown = true;
        }
        if ( ! exception_thrown ) System.out.println("Your Contact constructor doesn't throw a MaximumSizeExceddedException");
        Random rnd = new Random(5);
        Contact contact = new Contact("Andrej",getRandomLegitNumber(rnd),getRandomLegitNumber(rnd),getRandomLegitNumber(rnd));
        System.out.println(contact.getName());
        System.out.println(Arrays.toString(contact.getNumbers()));
        System.out.println(contact.toString());
        contact.addNumber(getRandomLegitNumber(rnd));
        System.out.println(Arrays.toString(contact.getNumbers()));
        System.out.println(contact.toString());
        contact.addNumber(getRandomLegitNumber(rnd));
        System.out.println(Arrays.toString(contact.getNumbers()));
        System.out.println(contact.toString());
    }

    static String[] legit_prefixes = {"070","071","072","075","076","077","078"};
    static Random rnd = new Random();

    private static String getRandomLegitNumber() {
        return getRandomLegitNumber(rnd);
    }

    private static String getRandomLegitNumber(Random rnd) {
        StringBuilder sb = new StringBuilder(legit_prefixes[rnd.nextInt(legit_prefixes.length)]);
        for ( int i = 3 ; i < 9 ; ++i )
            sb.append(rnd.nextInt(10));
        return sb.toString();
    }
}