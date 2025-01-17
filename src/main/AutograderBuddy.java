package main;

import browser.NgordnetQueryHandler;
import ngrams.NGramMap;


public class AutograderBuddy {
    /** Returns a HyponymHandler */
    public static NgordnetQueryHandler getHyponymsHandler(
            String wordFile, String countFile,
            String synsetFile, String hyponymFile) {

        return new HyponymsHandler(new WordNet(hyponymFile, synsetFile,
                new NGramMap(wordFile, countFile)));
    }
}
