// K1 16

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.Objects;
import java.util.Scanner;

class AmountNotAllowedException extends Exception {
    AmountNotAllowedException(int sum) {
        super(String.format("Receipt with amount %d is not allowed to be scanned", sum));
    }
}

class Item {
    String type;
    int price;

    Item() {
        type = "A";
        price = 0;
    }

    Item(String type, int price) {
        this.type = type;
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public int getPrice() {
        return price;
    }

    public double getTax() {
        if(type.equals("A")) {
            return price * 0.18 * 0.15;
        } else if(type.equals("B")) {
            return price * 0.05 * 0.15;
        }
        return 0;
    }
}

class Fiskalna {
    String id;
    ArrayList<Item> arr;

    Fiskalna() {
        id = "None";
        arr = new ArrayList<>();
    }

    Fiskalna(String id, ArrayList<Item> arr) {
        this.id = id;
        this.arr = arr;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void addItem(Item item) {
        arr.add(item);
    }

    public void check() throws AmountNotAllowedException {
        if(getTotalPrice() > 30000) {
            throw new AmountNotAllowedException(getTotalPrice());
        }
    }

    public int getTotalPrice() {
        return arr.stream().mapToInt(Item::getPrice).sum();
    }

    public double getTotalTax() {
        return arr.stream().mapToDouble(Item::getTax).sum();
    }

    @Override
    public String toString() {
        return String.format("%10s\t%10d\t%10.5f", id, getTotalPrice(), getTotalTax());
    }
}

class MojDDV {
    ArrayList<Fiskalna> list;

    MojDDV() {
        list = new ArrayList<>();
    }

    public void readRecords(InputStream in) {
        Scanner input = new Scanner(in);

        while(input.hasNextLine()) {
            String lineInput = input.nextLine();
            String [] line = lineInput.split(" ");

            Fiskalna f = new Fiskalna();
            f.setId(line[0]);

            for(int i = 1; i < line.length; i += 2) {
                f.addItem(new Item(line[i + 1], Integer.parseInt(line[i])));
            }

            try {
                f.check();
            } catch(Exception e) {
                System.out.println(e.getMessage());
                continue;
            }

            list.add(f);
        }
    }

    public void printTaxReturns(OutputStream out) {
        PrintWriter pw = new PrintWriter(out);
        list.forEach(pw::println);
        pw.flush();
    }

    public void printStatistics(OutputStream out) {
        PrintWriter pw = new PrintWriter(out);

        DoubleSummaryStatistics summary = list.stream().mapToDouble(Fiskalna::getTotalTax).summaryStatistics();
        pw.println(String.format("min:\t%.3f", summary.getMin()));
        pw.println(String.format("max:\t%.3f", summary.getMax()));
        pw.println(String.format("sum:\t%.3f", summary.getSum()));
        pw.println(String.format("count:\t%d", summary.getCount()));
        pw.println(String.format("avg:\t%.3f", summary.getAverage()));

        pw.flush();
    }
}

public class MojDDVTest {

    public static void main(String[] args) {

        MojDDV mojDDV = new MojDDV();

        System.out.println("===READING RECORDS FROM INPUT STREAM===");
        mojDDV.readRecords(System.in);

        System.out.println("===PRINTING TAX RETURNS RECORDS TO OUTPUT STREAM ===");
        mojDDV.printTaxReturns(System.out);

        System.out.println("===PRINTING SUMMARY STATISTICS FOR TAX RETURNS TO OUTPUT STREAM===");
        mojDDV.printStatistics(System.out);

    }
}
