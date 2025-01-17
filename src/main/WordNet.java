
package main;

import edu.princeton.cs.algs4.In;
import ngrams.NGramMap;
import java.util.*;

public class WordNet {
    // wrapper for a graph
    // contains graph, and adds helper functions
    private Graph graph;
    private HashMap<String, List<String>> synToHypoEng; // word,List<word hypos>
    private HashMap<String, List<String>> synsets; // #,List<# hypos>
    private ArrayList<SynsetList> synsetsW2s; // List<# hypos>, #
    private NGramMap ngm;

    HashMap<List<String>, List<String>> synToHypEng;

    private class SynsetList {
        List<String> syns;
        String id;
        public SynsetList(List<String> synsets, String id) {
            this.syns = synsets;
            this.id = id;
        }
    }


    public WordNet(String hypoFile, String synsetFile, NGramMap ngm) {
        // build the graph into graph var - add all the edges
        this.ngm = ngm; //
        this.synsets = new HashMap<>();
        this.synsetsW2s = new ArrayList<>();
        In inSynsets = new In(synsetFile);
        while (inSynsets.hasNextLine()) {
            String line = inSynsets.readLine();
            if (!line.isEmpty()) {
                List<String> fields = List.of(line.split(","));
                String hyper = fields.get(0);
                List<String> wordS = List.of(fields.get(1).split(" "));
                synsetsW2s.add(new SynsetList(wordS, hyper));

                if (synsets.containsKey(hyper)) {
                    synsets.get(hyper).addAll(wordS);
                } else {
                    synsets.put(hyper, wordS);
                }
            }
        }
        graph = new Graph();

        In inHyponyms = new In(hypoFile);
        while (inHyponyms.hasNextLine()) {
            String line = inHyponyms.readLine();
            if (!line.isEmpty()) {
                List<String> fields = List.of(line.split(","));
                String hyper = fields.get(0);
                List<String> hypos = fields.subList(1, fields.size());

                for (String hypo : hypos) {
                    graph.addEdge(Integer.parseInt(hyper), Integer.parseInt(hypo));
                }
            }
        }

        synToHypEng = new HashMap<>();
        for (String s : synsets.keySet()) {
            List<String> hypos = new ArrayList<>();
            hypos.add(s);
            if (graph.adjacentTo(Integer.parseInt(s)) != null) {
                for (int adj : graph.adjacentTo(Integer.parseInt(s))) {
                    hypos.addAll(synsets.get(String.valueOf(adj)));
                }
                synToHypEng.put(synsets.get(s), hypos);
            }
        }
    }

    public List<Integer> findSynsets(String word) {
        List<Integer> parents = new ArrayList<>();
        for (int i = 0; i < synsets.size(); i++) {
            if (synsets.get(String.valueOf(i)).contains(word)) {
                parents.add(i);
            }
        }
        return parents;
    }
    // return all hypos for each word, including different variations of the word
    public Set<String> findHyponymsOfWord(String word) {
        List<Integer> allSynsetsOfWord = findSynsets(word);
        Set<Integer> snsets = new HashSet<>();
        for (int synset : allSynsetsOfWord) {
            snsets.addAll(graph.getHypos(synset));
        }
        Set<String> words = new HashSet<>();
        for (int synset : snsets) {
            words.addAll(this.synsets.get(String.valueOf(synset)));
        }
        return words;
    }

    public Set<String> returnAllHypos(List<String> words) {
        if (words.isEmpty()) {
            return null;
        }

        Set<String> finals = findHyponymsOfWord(words.get(0));
        for (String word : words) {
            finals.retainAll(findHyponymsOfWord(word));
        }

        return finals;
    }


    public String getHypos(List<String> words, int startYear, int endYear, int k) {
        List<List<String>> keys = new ArrayList<>();
        List<Set<String>> result = new ArrayList<>();
        Set<Integer> temp = new HashSet<>(); // collect hypos of all entered words
        HashSet<Integer> indices = new HashSet<>();
        //Map<List<String>, String> copiedMap = new HashMap<>(synsetsW2s);
        for (String word : words) {
            List<Integer> synsetsContainingWord = findSynsets(word); // corresponds to all syns containing one word

            for (SynsetList synsID : synsetsW2s) {
                if (synsID.syns.contains(word)) {
                    indices.add(Integer.parseInt(synsID.id));
                }
            }
            if (synsetsContainingWord.isEmpty()) {
                return "[]";
            }
        }

        // bug is that you access synsetsW2s.get("thing") twice, but it only sees one of two identical entries

        List<String> allHypos = new ArrayList<>(returnAllHypos(words));

        // contains ALL k=0 hyponyms
        if (k > 0) {
            return kH2(allHypos, startYear, endYear, k); // get top K
        }
        allHypos.sort(null); // if k=0, just sort and return
        return allHypos.toString();
    }

    private String kH2(List<String> hypos, int startYear, int endYear, int k) {
        PriorityQueue<WordCount> minHeap = new PriorityQueue<>(k, Comparator.comparingDouble(wc -> wc.count));

        for (String hypo : hypos) {
            double hypoCount = 0;
            for (double yearOfData : ngm.countHistory(hypo, startYear, endYear).data()) {
                hypoCount += yearOfData;
            }
            WordCount currHypo = new WordCount(hypo, hypoCount);

            if (hypoCount > 0) {
                minHeap.add(currHypo);
            }
            if (minHeap.size() > k) {
                minHeap.poll();
            }
        }
        List<String> finalList = new ArrayList<>();
        for (WordCount wc : minHeap) {
            finalList.add(wc.word);
        }

        finalList.sort(null);
        return finalList.toString();

    }


    private class WordCount {
        String word;
        double count;

        public WordCount(String word, double count) {
            this.word = word;
            this.count = count;
        }

        public static Comparator<WordCount> byCount() {
            return (wc1, wc2) -> (int) (wc1.count - wc2.count); //Comparator.comparingDouble(wc -> wc.count);
        }
    }
}
