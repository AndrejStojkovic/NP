// K2 24

import java.util.*;
import java.util.stream.Collectors;

class Movie implements Comparable<Movie> {
    String name;
    List<Integer> ratings;

    Movie(String name, int [] ratings) {
        this.name = name;
        this.ratings = new ArrayList<>();
        this.ratings.addAll(Arrays.stream(ratings).boxed().collect(Collectors.toList()));
    }

    public double averageRating() {
        return ratings.stream().mapToDouble(x -> x).average().orElse(0);
    }

    public double ratingCoef(int maxRatings) {
        return averageRating() * ratings.size() / maxRatings;
    }

    @Override
    public int compareTo(Movie o) {
        int rating = Double.compare(o.averageRating(), averageRating());
        if(rating == 0) {
            return name.compareTo(o.name);
        }
        return rating;
    }

    @Override
    public String toString() {
        return String.format("%s (%.2f) of %d ratings", name, averageRating(), ratings.size());
    }
}

class MoviesList {
    List<Movie> movies;

    MoviesList() {
        movies = new ArrayList<>();
    }

    public void addMovie(String title, int [] ratings) {
        movies.add(new Movie(title, ratings));
    }

    public List<Movie> top10ByAvgRating() {
        return movies.stream().sorted().limit(10).collect(Collectors.toList());
    }

    public List<Movie> top10ByRatingCoef() {
        int maxRating = movies.stream().map(x -> x.ratings.size()).reduce(0, Math::max);

        Comparator<Movie> comparator = (o1, o2) -> {
            int rating = Double.compare(o2.ratingCoef(maxRating), o1.ratingCoef(maxRating));
            if(rating == 0) {
                return o1.name.compareTo(o2.name);
            }
            return rating;
        };

        return movies.stream().sorted(comparator).limit(10).collect(Collectors.toList());
    }

}

public class MoviesTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        MoviesList moviesList = new MoviesList();
        int n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String title = scanner.nextLine();
            int x = scanner.nextInt();
            int[] ratings = new int[x];
            for (int j = 0; j < x; ++j) {
                ratings[j] = scanner.nextInt();
            }
            scanner.nextLine();
            moviesList.addMovie(title, ratings);
        }
        scanner.close();
        List<Movie> movies = moviesList.top10ByAvgRating();
        System.out.println("=== TOP 10 BY AVERAGE RATING ===");
        for (Movie movie : movies) {
            System.out.println(movie);
        }
        movies = moviesList.top10ByRatingCoef();
        System.out.println("=== TOP 10 BY RATING COEFFICIENT ===");
        for (Movie movie : movies) {
            System.out.println(movie);
        }
    }
}
