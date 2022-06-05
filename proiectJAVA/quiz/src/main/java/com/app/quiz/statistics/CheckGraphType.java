package com.app.quiz.statistics;

import com.app.quiz.models.Person;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.util.*;

public class CheckGraphType {
    private Map<Person, List<Person>> bipartition = new HashMap<>();
    private Map<Person, Integer> catalogIDS = new HashMap<>();
    private int[][] adjMatrix;
    private Graph<Person, DefaultEdge> graph;
    private Map<Person, Person> teams = new HashMap<>();
    private int numOfVertices;

    public CheckGraphType(Graph<Person, DefaultEdge> graph){
        this.graph = graph;
        assignIds();
        this.numOfVertices = graph.vertexSet().size();
    }
    public boolean isBipartite()
    {
        Person src = graph.vertexSet().iterator().next();
        Map<Person, Integer> colors = new HashMap<>();
        for (Person vertex : graph.vertexSet()) {
            colors.put(vertex, -1);
        }

        // Assign first color to source
        colors.put(src, 1);

        LinkedList<Person> q = new LinkedList<>();
        q.add(src);

        while (q.size() != 0) {
            // Dequeue a vertex from queue
            Person u = q.poll();

            // Find all non-colored adjacent vertices
            for (Person v : graph.vertexSet()) {
                // An edge from u to v exists
                // and destination v is not colored
                if ((graph.containsEdge(u, v) || graph.containsEdge(v, u)) && colors.get(v).equals(-1)) {
                    colors.put(v, 1 - colors.get(u));
                    q.add(v);
                }
                // An edge from u to v exists and destination
                //  v is colored with same color as u
                else if ((graph.containsEdge(u, v) || graph.containsEdge(v, u)) && colors.get(v).equals(colors.get(u))) {
                    return false;
                }
            }
        }
        List<Person> leftPartition = new ArrayList<>();
        List<Person> rightPartition = new ArrayList<>();
        for(Map.Entry<Person, Integer> entry : colors.entrySet()){
            if(entry.getValue().equals(0)){
                leftPartition.add(entry.getKey());
            }
            else{
                rightPartition.add(entry.getKey());
            }
        }
        for(Person right : rightPartition){
            List<Person> maybePartners = new ArrayList<>();
            for(Person left : leftPartition){
                if(graph.containsEdge(right, left) || graph.containsEdge(left, right)){
                    maybePartners.add(left);
                }
            }
            if(!maybePartners.isEmpty()) {
                bipartition.put(right, maybePartners);
            }
        }
        return true;
    }

    public Map<Person, List<Person>> getBipartition() {
        return bipartition;
    }
    private void assignIds(){
        int id = 0;
        for(Person vertex : graph.vertexSet()){
            catalogIDS.put(vertex, id++);
        }
    }
    public void makeAdjacencyMatrix(){
        adjMatrix = new int[numOfVertices][numOfVertices];
        for(Map.Entry<Person, List<Person>> entry : bipartition.entrySet()){
            for(Person maybePartner : entry.getValue()){
                adjMatrix[catalogIDS.get(entry.getKey())][catalogIDS.get(maybePartner)] = 1;
            }
        }
    }
    public Map<Person, Person> maxMatching() {
        makeAdjacencyMatrix();
        int players1 = numOfVertices;
        int players2 = numOfVertices;

        int[] assign = new int[players2];
        for (int i = 0; i < players2; i++) {
            assign[i] = -1;
        }

        for (int player = 0; player < players1; player++) {
            boolean[] visited = new boolean[players2];
            bipartiteMatch(player, visited, assign);
        }
        for(int i = 0; i < players2; i++){
            if(assign[i] != -1){
                teams.put(getKey(i), getKey(assign[i]));
            }
        }
        return teams;
    }
    private Person getKey(int value){
        for (Map.Entry<Person, Integer> entry : catalogIDS.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return null;
    }
    private boolean bipartiteMatch(int player, boolean[] visited, int[] assign) {
        for (int job = 0; job < graph.vertexSet().size(); job++) {
            if (adjMatrix[player][job] == 1 && !visited[job]) {
                visited[job] = true;
                int assignedApplicant = assign[job];
                if (assignedApplicant < 0 || bipartiteMatch(assignedApplicant, visited, assign)) {
                    assign[job] = player;
                    return true;
                }
            }
        }
        return false;
    }
}
