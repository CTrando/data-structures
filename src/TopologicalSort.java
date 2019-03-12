import org.junit.jupiter.api.Test;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.*;


class Graph {
    private Map<String, List<String>> adjList = new HashMap<>();

    void makeEdge(String source, List<String> others) {
        for (String other : others)
            makeEdge(source, other);
    }

    void makeEdge(String source, String other) {
        adjList.computeIfAbsent(source, k -> new ArrayList<>());
        adjList.computeIfAbsent(other, k -> new ArrayList<>());

        List<String> edges = adjList.get(source);
        edges.add(other);
    }

    List<String> getAdjacent(String source) {
        List<String> ret = adjList.get(source);
        if (ret == null) throw new NoSuchElementException("Not in the graph");
        return ret;
    }

    Iterator<String> iter() {
        return adjList.keySet().iterator();
    }
}


class TopologicalSort {

    private Graph fGraph;

    TopologicalSort(Graph g) {
        fGraph = g;
    }

    List<String> sort() {
        Map<String, Boolean> visited = new HashMap<>();
        List<String> ret = new ArrayList<>();

        for (Iterator<String> it = fGraph.iter(); it.hasNext(); ) {
            String node = it.next();
            dfs(node, visited, ret);
        }
        Collections.reverse(ret);
        return ret;
    }

    void dfs(String source, Map<String, Boolean> visited, List<String> ret) {
        if (visited.get(source) != null && visited.get(source))
            return;

        for (String adj : fGraph.getAdjacent(source)) {
            if (visited.get(adj) != null && visited.get(adj))
                continue;

            dfs(adj, visited, ret);
        }
        ret.add(source);
        visited.put(source, true);
    }

}


class TopologicalSortTest {

    Graph createGraph() {
        Graph g = new Graph();

        g.makeEdge("e", Arrays.asList("g", "d"));
        g.makeEdge("f", Arrays.asList("e", "g"));
        g.makeEdge("d", Arrays.asList("g"));
        g.makeEdge("c", Arrays.asList("a", "d"));
        g.makeEdge("b", Arrays.asList("c", "f", "a"));
        g.makeEdge("a", Arrays.asList("e", "d"));
        return g;
    }

    boolean isValidTopologicalOrdering(List<String> questionableOrdering, Graph graph) {
        Map<String, Integer> strToPos = new HashMap<>();

        for (int i = 0; i < questionableOrdering.size(); i++) {
            strToPos.put(questionableOrdering.get(i), i);
        }

        for (int i = 0; i < questionableOrdering.size(); i++) {
            String node = questionableOrdering.get(i);


            List<String> adj = graph.getAdjacent(node);
            for(String adjacentNode: adj) {
                if(strToPos.get(adjacentNode) <= i)
                    return false;
            }
        }
        return true;
    }

    @Test
    void testGraphImplementation() {
        Graph g = createGraph();
        assertThat(g.getAdjacent("a"), containsInAnyOrder("d", "e"));
    }

    @Test
    void topologicalSort() {
        Graph g = createGraph();
        TopologicalSort topologicalSort = new TopologicalSort(g);

        List<String> actual = topologicalSort.sort();
        List<String> expected = Arrays.asList("b", "c", "a", "f", "e", "d", "g");

        List<String> sanityCheck = Arrays.asList("b", "c", "a", "g", "e", "d", "f");

        assertFalse(isValidTopologicalOrdering(sanityCheck, g));
        assertTrue(isValidTopologicalOrdering(expected,g));
        assertTrue(isValidTopologicalOrdering(actual, g));
    }
}
