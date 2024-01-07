// K2 29

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Stats implements Comparable<Stats> {
    int goals;
    int wins;
    int draws;
    int loses;

    Stats() {
        goals = wins = draws = loses = 0;
    }

    public void update(int type) {
        if(type > 0) {
            wins++;
        } else if(type < 0) {
            loses++;
        } else {
            draws++;
        }
    }

    public void changeGoals(int g) {
        goals += g;
    }

    public int plays() {
        return wins + draws + loses;
    }

    public int points() {
        return (wins * 3) + draws;
    }

    @Override
    public String toString() {
        return String.format("%5d%5d%5d%5d%5d", plays(), wins, draws, loses, points());
    }

    @Override
    public int compareTo(Stats o) {
        int p = Integer.compare(points(), o.points());
        if(p != 0) {
            return p;
        }
        return Integer.compare(goals, o.goals);
    }
}

class FootballTable {
    Map<String, Stats> teams;

    FootballTable() {
        teams = new HashMap<>();
    }

    public void addGame(String homeTeam, String awayTeam, int homeGoals, int awayGoals) {
        teams.computeIfAbsent(homeTeam, x -> new Stats());
        teams.computeIfAbsent(awayTeam, x -> new Stats());

        Stats home = teams.get(homeTeam);
        Stats away = teams.get(awayTeam);

        if(homeGoals > awayGoals) {
            home.update(1);
            away.update(-1);
        } else if(homeGoals < awayGoals) {
            home.update(-1);
            away.update(1);
        } else {
            home.update(0);
            away.update(0);
        }

        home.changeGoals(homeGoals - awayGoals);
        away.changeGoals(awayGoals - homeGoals);
    }

    public void printTable() {
        Map<String, Stats> sorted = teams.entrySet().stream()
                .sorted(Map.Entry.<String, Stats>comparingByValue()
                        .thenComparing(Map.Entry.comparingByKey(Comparator.reverseOrder())).reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a, LinkedHashMap::new
                ));

        int idx = 1;
        for(String team : sorted.keySet()) {
            System.out.printf("%2d. %-15s%s\n", idx, team, teams.get(team));
            idx++;
        }
    }
}

public class FootballTableTest {
    public static void main(String[] args) throws IOException {
        FootballTable table = new FootballTable();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        reader.lines()
                .map(line -> line.split(";"))
                .forEach(parts -> table.addGame(parts[0], parts[1],
                        Integer.parseInt(parts[2]),
                        Integer.parseInt(parts[3])));
        reader.close();
        System.out.println("=== TABLE ===");
        System.out.printf("%-19s%5s%5s%5s%5s%5s\n", "Team", "P", "W", "D", "L", "PTS");
        table.printTable();
    }
}
