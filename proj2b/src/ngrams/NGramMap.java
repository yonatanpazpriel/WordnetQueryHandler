package ngrams;

import edu.princeton.cs.algs4.In;
import java.util.*;

import static ngrams.TimeSeries.MAX_YEAR;
import static ngrams.TimeSeries.MIN_YEAR;

/**
 * An object that provides utility methods for making queries on the
 * Google NGrams dataset (or a subset thereof).
 *
 * An NGramMap stores pertinent data from a "words file" and a "counts
 * file". It is not a map in the strict sense, but it does provide additional
 * functionality.
 *
 * @author Josh Hug
 */
public class NGramMap {

    HashMap<String, TimeSeries> wordsMap;
    TimeSeries countsTS;

    /**
     * Constructs an NGramMap from WORDSFILENAME and COUNTSFILENAME.
     */
    public NGramMap(String wordsFilename, String countsFilename) {

        wordsMap = new HashMap<>();
        countsTS = new TimeSeries();

        In inW = new In(wordsFilename);
        while (inW.hasNextLine()) {
            String line = inW.readLine();
            String[] fields = line.split("\t");
            String word = fields[0];
            Integer year = Integer.parseInt(fields[1]);
            Double count = Double.parseDouble(fields[2]);

            if (wordsMap.containsKey(word)) {
                wordsMap.get(word).put(year, count);
            } else {
                TimeSeries ts = new TimeSeries();
                ts.put(year, count);
                wordsMap.put(word, ts);
            }
        }

        In inC = new In(countsFilename);
        while (inC.hasNextLine()) {
            String line = inC.readLine();
            String[] fields = line.split(",");
            int yr = Integer.parseInt(fields[0]);
            Double count = Double.parseDouble(fields[1]);

            countsTS.put(yr, count);
        }
    }

    /**
     * Provides the history of WORD between STARTYEAR and ENDYEAR, inclusive of both ends. The
     * returned TimeSeries should be a copy, not a link to this NGramMap's TimeSeries. In other
     * words, changes made to the object returned by this function should not also affect the
     * NGramMap. This is also known as a "defensive copy". If the word is not in the data files,
     * returns an empty TimeSeries.
     */
    public TimeSeries countHistory(String word, int startYear, int endYear) {
        if (!wordsMap.containsKey(word)) {
            return new TimeSeries();
        }

        return new TimeSeries(wordsMap.get(word), startYear, endYear);

    }

    /**
     * Provides the history of WORD. The returned TimeSeries should be a copy, not a link to this
     * NGramMap's TimeSeries. In other words, changes made to the object returned by this function
     * should not also affect the NGramMap. This is also known as a "defensive copy". If the word
     * is not in the data files, returns an empty TimeSeries.
     */
    public TimeSeries countHistory(String word) {

        if (!wordsMap.containsKey(word)) {
            return new TimeSeries();
        }

        TimeSeries words = wordsMap.get(word);
        List<Integer> years = words.years();
        return new TimeSeries(words, years.get(0), years.get(years.size() - 1));

    }

    /**
     * Returns a defensive copy of the total number of words recorded per year in all volumes.
     */
    public TimeSeries totalCountHistory() {
        return new TimeSeries(countsTS, MIN_YEAR, MAX_YEAR);
    }

    /**
     * Provides a TimeSeries containing the relative frequency per year of WORD between STARTYEAR
     * and ENDYEAR, inclusive of both ends. If the word is not in the data files, returns an empty
     * TimeSeries.
     */
    public TimeSeries weightHistory(String word, int startYear, int endYear) {
        if (!wordsMap.containsKey(word)) {
            return new TimeSeries();
        }
        return new TimeSeries(wordsMap.get(word).dividedBy(countsTS), startYear, endYear);
    }

    /**
     * Provides a TimeSeries containing the relative frequency per year of WORD compared to all
     * words recorded in that year. If the word is not in the data files, returns an empty
     * TimeSeries.
     */
    public TimeSeries weightHistory(String word) {
        if (!wordsMap.containsKey(word)) {
            return new TimeSeries();
        }
        return wordsMap.get(word).dividedBy(countsTS);
    }

    /**
     * Provides the summed relative frequency per year of all words in WORDS between STARTYEAR and
     * ENDYEAR, inclusive of both ends. If a word does not exist in this time frame, ignore it
     * rather than throwing an exception.
     */
    public TimeSeries summedWeightHistory(Collection<String> words, int startYear, int endYear) {
        TimeSeries sum = new TimeSeries();
        for (String word : words) {
            sum = sum.plus(weightHistory(word, startYear, endYear));
        }
        return sum;
    }

    /**
     * Returns the summed relative frequency per year of all words in WORDS. If a word does not
     * exist in this time frame, ignore it rather than throwing an exception.
     */
    public TimeSeries summedWeightHistory(Collection<String> words) {
        TimeSeries sum = new TimeSeries();
        for (String word : words) {
            sum = sum.plus(weightHistory(word));
        }
        return sum;
    }

}
