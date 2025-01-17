package ngrams;

import java.util.*;

/**
 * An object for mapping a year number (e.g. 1996) to numerical data. Provides
 * utility methods useful for data analysis.
 *
 * @author Josh Hug
 */
public class TimeSeries extends TreeMap<Integer, Double> {

    /**
     * If it helps speed up your code, you can assume year arguments to your NGramMap
     * are between 1400 and 2100. We've stored these values as the constants
     * MIN_YEAR and MAX_YEAR here.
     */
    public static final int MIN_YEAR = 1400;
    public static final int MAX_YEAR = 2100;

    /**
     * Constructs a new empty TimeSeries.
     */
    public TimeSeries() {
        super();
    }

    /**
     * Creates a copy of TS, but only between STARTYEAR and ENDYEAR,
     * inclusive of both end points.
     */
    public TimeSeries(TimeSeries ts, int startYear, int endYear) {
        super();
        for (int year = startYear; year <= endYear; year++) {
            if (ts.years().contains(year)) {
                this.put(year, ts.get(year));
            }
        }
    }

    /**
     * Returns all years for this time series in ascending order.
     */
    public List<Integer> years() {
        List<Integer> yrs = new ArrayList<>();
        for (int year = MIN_YEAR; year <= MAX_YEAR; year++) {
            if (this.containsKey(year)) {
                yrs.add(year);
            }
        }
        return yrs;
    }

    /**
     * Returns all data for this time series. Must correspond to the
     * order of years().
     */
    public List<Double> data() {
        List<Double> dta = new ArrayList<>();
        for (int year : this.years()) {
            dta.add(this.get(year));
        }
        return dta;
    }

    /**
     * Returns the year-wise sum of this TimeSeries with the given TS. In other words, for
     * each year, sum the data from this TimeSeries with the data from TS. Should return a
     * new TimeSeries (does not modify this TimeSeries).
     * <p>
     * If both TimeSeries don't contain any years, return an empty TimeSeries.
     * If one TimeSeries contains a year that the other one doesn't, the returned TimeSeries
     * should store the value from the TimeSeries that contains that year.
     */
    public TimeSeries plus(TimeSeries ts) {
        // List of years in either TS
        List<Integer> comb = this.years();
        for (int year : ts.years()) {
            if (!comb.contains(year)) {
                comb.add(year);
            }
        }

        TimeSeries sum = new TimeSeries();
        for (int year : comb) {
            if (this.containsKey(year) && ts.containsKey(year)) {
                sum.put(year, this.get(year) + ts.get(year));
            } else if (this.containsKey(year) && !ts.containsKey(year)) {
                sum.put(year, this.get(year));
            } else if (!this.containsKey(year) && ts.containsKey(year)) {
                sum.put(year, ts.get(year));
            }
        }
        return sum;
    }

    /**
     * Returns the quotient of the value for each year this TimeSeries divided by the
     * value for the same year in TS. Should return a new TimeSeries (does not modify this
     * TimeSeries).
     * <p>
     * If TS is missing a year that exists in this TimeSeries, throw an
     * IllegalArgumentException.
     * If TS has a year that is not in this TimeSeries, ignore it.
     */
    public TimeSeries dividedBy(TimeSeries ts) {
        TimeSeries t = new TimeSeries();
        for (int year : this.years()) {
            if (!ts.containsKey(year)) {
                throw new IllegalArgumentException();
            }
            t.put(year, this.get(year) / ts.get(year));
        }

        return t;
    }
}
