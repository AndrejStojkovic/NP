// K1 27

import java.io.InputStream;
import java.util.Arrays;
import java.util.Scanner;

class Risk {
    public void processAttacksData(InputStream inputStream) {
        Scanner in = new Scanner(inputStream);

        while(in.hasNextLine()) {
            String line = in.nextLine();

            String [] playerOne = line.split(";")[0].split(" ");
            String [] playerTwo = line.split(";")[1].split(" ");

            int [] p1 = new int[3];
            int [] p2 = new int[3];

            for(int i = 0; i < 3; i++) {
                p1[i] = Integer.parseInt(playerOne[i]);
                p2[i] = Integer.parseInt(playerTwo[i]);
            }

            Arrays.sort(p1);
            Arrays.sort(p2);

            int x = 0, y = 0;
            for(int i = 0; i < 3; i++) {
                if(p1[i] > p2[i]) x++;
                else y++;
            }

            System.out.println(x + " " + y);
        }
    }
}

public class RiskTester {
    public static void main(String[] args) {
        Risk risk = new Risk();
        risk.processAttacksData(System.in);
    }
}
