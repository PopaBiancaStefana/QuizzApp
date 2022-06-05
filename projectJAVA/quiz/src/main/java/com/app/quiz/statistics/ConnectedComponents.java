package com.app.quiz.statistics;

import com.app.quiz.models.Person;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.util.*;

public class ConnectedComponents {
    private Graph<Person, DefaultEdge> graph;
    private Map<Person, Integer> catalogIDS = new HashMap<>();
    private LinkedList<Integer> adj[];
    private List<Person> players;
    private List<Integer> biggestScoreFromComp = new ArrayList<>();
    private final int numOfVertices;
    private int numOfConnComponents = 0;

    public int getNumOfConnComponents() {
        return numOfConnComponents;
    }

    public Map<Person, Integer> getCatalogIDS() {
        return catalogIDS;
    }

    public LinkedList<Integer>[] getAdj() {
        return adj;
    }

    public ConnectedComponents(Graph<Person, DefaultEdge> graph, List<Person> players) {
        this.graph = graph;
        this.numOfVertices = graph.vertexSet().size();
        this.players = players;
        makeAdjacencyList();
    }

    public void makeAdjacencyList() {
        assignIds();
        LinkedList<Integer> adj[] = new LinkedList[numOfVertices];
        for (int i = 0; i < numOfVertices; i++) {
            adj[i] = new LinkedList();
        }

        for (Person vertex1 : graph.vertexSet()) {
            for (Person vertex2 : graph.vertexSet()) {
                if (graph.containsEdge(vertex1, vertex2) || graph.containsEdge(vertex2, vertex1)) {
                    adj[catalogIDS.get(vertex1)].add(catalogIDS.get(vertex2));
                }
            }
        }
        this.adj = adj;
    }

    private void assignIds() {
        int id = 0;
        for (Person vertex : graph.vertexSet()) {
            catalogIDS.put(vertex, id++);
        }
    }

    private Integer DFSUtil(Integer current, boolean[] visited, Integer biggestScore) {
        // Mark the current node as visited and print it
        visited[current] = true;
        System.out.print(current + " ");
        // Recur for all the vertices
        // adjacent to this vertex
        for (Integer x : adj[current]) {
            if ((getKey(x).getPunctaj() != null && getKey(biggestScore).getPunctaj() != null) && (getKey(x).getPunctaj() > getKey(biggestScore).getPunctaj())) {
                biggestScore = x;
            }
            if (!visited[x])
                DFSUtil(x, visited, biggestScore);
        }
        return biggestScore;
    }

    public void connectComponents() {
        // Mark all the vertices as not visited
        boolean[] visited = new boolean[numOfVertices];
        for (int vertex = 0; vertex < numOfVertices; ++vertex) {
            if (!visited[vertex]) {
                //get the node with the biggest score, from the connect component
                biggestScoreFromComp.add(DFSUtil(vertex, visited, vertex));
                numOfConnComponents++;
            }
        }
    }

    private Person getKey(Integer value) {
        for (Map.Entry<Person, Integer> entry : catalogIDS.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public Graph<Person, DefaultEdge> makeConnexGraph() {
        for (int i = 0; i < biggestScoreFromComp.size() - 1; i++) {
            this.graph.addEdge(getKey(biggestScoreFromComp.get(i)), getKey(biggestScoreFromComp.get(i + 1)));
        }
        return this.graph;
    }


}
