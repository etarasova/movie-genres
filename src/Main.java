import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws Exception {

        //read in csv-file and creates list with movies
        List<Movie> movies = readMovies(new File("data/movies.csv"));

        //parse movies to extract genres (all data)
        Map<String, Integer> genreToCountMap = extractGenres(movies, null);

        //parse movies to extract genres (from 2013)
        Map<String, Integer> genreToCountMap2013 = extractGenres(movies, 2013);

        //sort map by value
        Map<String, Integer> sortedMap = sortGenresByCount(genreToCountMap);

        //save the result in file
        writeCSVFile("output/report1.csv", sortedMap);

        //create chart that counts all data
        writeMovieGenreCountsChartDataFile(new File("output/genre-counts-all-data.json"), genreToCountMap);

        //create chart that counts last 5 years
        writeMovieGenreCountsChartDataFile(new File("output/genre-counts-last-5-years-data.json"), genreToCountMap2013);


        //create map that count how many movies came out for each genre each year
        Map <Integer, Map <String, Integer>> yearToGenreMovieCountMap = createYearToGenreMovieCountMap(movies);

        List<Integer> years = yearToGenreMovieCountMap.keySet().stream()
                .sorted()
                .collect(Collectors.toList());

        //create chart with sorted years
        writeMovieYearsChartDataFile(new File("output/movie-years-data.json"), years);

        Map<String, List<Integer>> genreToMovieCount = new HashMap<>();
        genreToCountMap.keySet().forEach(genre -> {
            List<Integer> yearCounts = new ArrayList<>();
            genreToMovieCount.put(genre, yearCounts);
            years.forEach(year -> {
                Integer count = yearToGenreMovieCountMap.get(year).get(genre);
                yearCounts.add(count);
            });
        });

        //create chart that shows how many movies came out for each genre each year
        writeGenreToYearMovieCountChartDataFile(new File("output/genre-year-movie-count-data.json"), genreToMovieCount);

    }


    //read movies from file to list
    private static List<Movie> readMovies(File fileName) throws Exception {
        List<Movie> movies = new LinkedList<>();
        List<String[]> movieLines = readCSVFile(fileName);
        for (String[] movieLine : movieLines) {
            movies.add(Movie.parseLine(movieLine));
        }
        return movies;
    }

    //read csv file using openCSV library
    private static List<String[]> readCSVFile(File filename) throws IOException, CsvException {
        //Start reading from line number 1 (line numbers start from zero)
        CSVReader reader = new CSVReaderBuilder(new FileReader(filename))
                .withSkipLines(1)
                .build();
        //Read all rows at once
        return reader.readAll();
    }

    //write map to file
    private static void writeCSVFile(String filename, Map<String, Integer> map) throws IOException {
        List<String[]> rows = map.entrySet()
                .stream()
                .map(entry -> new String[]{
                        entry.getKey(), entry.getValue().toString()
                })
                .collect(Collectors.toList());
        try (CSVWriter writer = new CSVWriter(new FileWriter(filename))) {
            writer.writeNext(new String[]{"Genre", "Count"});
            writer.writeAll(rows);
        }
    }

    //extract genres from movies and count them
    private static Map<String, Integer> extractGenres(List<Movie> movies, Integer minReleaseYear) {
        Map<String, Integer> genres = new HashMap<>();
        movies.stream()
                .filter(movie -> movie.isWithinRange(minReleaseYear))
                .forEach(movie -> {
                    for (String genre : movie.getGenres()) {
                        int count = genres.getOrDefault(genre, 0);
                        genres.put(genre, ++count);
                    }
                });
        return genres;
    }

    //map that sort genres and count them
    private static Map<String, Integer> sortGenresByCount(Map<String, Integer> map) {
        return map.entrySet()
                .stream()
                .sorted((Map.Entry.<String, Integer>comparingByValue().reversed()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    //create chart with sorted years
    private static void writeMovieYearsChartDataFile(File filename, List<Integer> years) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        writer.writeValue(filename, years);
    }

    //method that writes in file how many movies came out for each genre each year
    private static void writeGenreToYearMovieCountChartDataFile(File filename, Map<String, List<Integer>> genreToMovieCount) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        writer.writeValue(filename, genreToMovieCount.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> value = new HashMap<>();
                    value.put("name", entry.getKey());
                    value.put("data", entry.getValue());
                    return value;
                })
                .collect(Collectors.toList())
        );
    }

    //create map that counts movies and genres for each year
    private static Map<Integer, Map<String, Integer>> createYearToGenreMovieCountMap(List<Movie> movies) {
        Map<Integer, Map<String, Integer>> map = new LinkedHashMap<>();
        movies.forEach(movie -> {
            int year = movie.getReleasedYear();
            Map<String, Integer> genreToCount = map.get(year);
            if (genreToCount == null) {
                genreToCount = new HashMap<>();
                map.put(year, genreToCount);
            }
            for (String genre : movie.getGenres()) {
                int count = genreToCount.getOrDefault(genre, 0);
                genreToCount.put(genre, ++count);
            }
        });
        return map;
    }

    //create chart from map with movie genres
    private static void writeMovieGenreCountsChartDataFile(File filename, Map<String, Integer> genreToCountMap) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        writer.writeValue(filename, genreToCountMap.entrySet()
                .stream()
                .map(entry -> {
                    Map<String, Object> value = new HashMap<>();
                    value.put("name", entry.getKey());
                    value.put("y", entry.getValue());
                    return value;
                })
                .collect(Collectors.toList()));
    }
}

class Movie implements Comparable<Movie> {
    private static final Pattern TITLE_PATTERN = Pattern.compile("^(.+)\\s\\(?(\\d{4})\\)?$");

    private int movieID;
    private String title;
    private int releasedYear;
    private String[] genres;

    public static Movie parseLine(String[] line) {
        Movie movie = new Movie();
        movie.fromLine(line);
        return movie;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getReleasedYear() {
        return releasedYear;
    }

    public void setReleasedYear(int releasedYear) {
        this.releasedYear = releasedYear;
    }

    public int getMovieID() {
        return movieID;
    }

    public void setMovieID(int movieID) {
        this.movieID = movieID;
    }

    public String[] getGenres() {
        return genres;
    }

    public void setGenres(String[] genres) {
        this.genres = genres;
    }

    public void fromLine(String[] line) {
        setMovieID(Integer.parseInt(line[0]));
        setTitleAndReleaseYear(line[1], getMovieID());
        setGenres(line[2].split("\\|"));
    }

    private void setTitleAndReleaseYear(String titleAndYear, int movieId) {
        Matcher matcher = TITLE_PATTERN.matcher(titleAndYear.trim());
        setTitle(titleAndYear + " - " + movieId);
        if (matcher.find()) {
            setReleasedYear(Integer.parseInt(matcher.group(2)));
        } else {
            //System.out.println("Invalid title: " + titleAndYear);
            setReleasedYear(9999);
        }
    }

    public boolean isWithinRange(Integer minReleaseYear) {
        return minReleaseYear == null || getReleasedYear() >= minReleaseYear;
    }

    @Override
    public String toString() {
        return getTitle();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Movie)) {
            return false;
        }
        Movie other = (Movie) obj;
        return Objects.equals(this.getMovieID(), other.getMovieID());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.getMovieID());
    }

    @Override
    public int compareTo(Movie other) {
        return this.getTitle().compareTo(other.getTitle());
    }
}

