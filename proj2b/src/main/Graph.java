package main;
import java.util.*;

public class Graph {
    // adjList (arr of lists) with size = # synsets
    // each list contains all vertices to which index has an edge
    private Map<Integer, List<Integer>> adjacencyList;

    public Graph() {
        adjacencyList = new HashMap<>();
    }

    public void addEdge(int from, int to) {
        adjacencyList.putIfAbsent(from, new ArrayList<>());
        adjacencyList.get(from).add(to);
    }

    public List<Integer> adjacentTo(int v) {
        return adjacencyList.get(v);
    }

    public Set<Integer> getHypos(int v) {
        Set<Integer> visited = new HashSet<>();
        dfsHelper(v, visited);
        visited.add(v);
        return visited;
    }

    private void dfsHelper(int v, Set<Integer> visited) {
        if (visited.contains(v)) {
            return;
        }
        visited.add(v);
        if (adjacencyList.containsKey(v)) {
            for (int i : adjacencyList.get(v)) {
                dfsHelper(i, visited);
                visited.add(i);
            }
        }
    }
}

