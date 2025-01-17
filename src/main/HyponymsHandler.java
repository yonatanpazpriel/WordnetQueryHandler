package main;

import browser.NgordnetQuery;
import browser.NgordnetQueryHandler;

import java.util.List;

public class HyponymsHandler extends NgordnetQueryHandler {
    WordNet wn;
    public HyponymsHandler(WordNet wn) {
        this.wn = wn;
    }

    @Override
    public String handle(NgordnetQuery q) {
        List<String> words = q.words();
        int startYear = q.startYear();
        int endYear = q.endYear();
        int k = q.k();
        return wn.getHypos(words, startYear, endYear, k);

    }
}
// turn words into graph
// search graph
// never need to removeEdge/add??
// addedge?addnode?traverse?size? constructor.
