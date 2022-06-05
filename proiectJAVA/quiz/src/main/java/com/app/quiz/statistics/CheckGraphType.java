package com.app.quiz.statistics;

import com.app.quiz.models.Person;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.util.*;

public class CheckGraphType {
    private Map<Person, List<Person>> bipartition = new HashMap<>();
    private Map<Person, Integer> catalogIDS = new HashMap<>();
    private int[][] adjMatrix;
    private int[][] adjancencyMatrix;
    private Graph<Person, DefaultEdge> graph;
    private Map<Person, Person> teams = new HashMap<>();
    private int numOfVertices;
    private LinkedList<Integer> adj[];

    public CheckGraphType(Graph<Person, DefaultEdge> graph){
        this.graph = graph;
        assignIds();
        this.numOfVertices = graph.vertexSet().size();
        adj = new LinkedList[numOfVertices];
        for (int i = 0; i < numOfVertices; ++i){
            adj[i] = new LinkedList();
        }
        adjancencyMatrix = new int[numOfVertices][numOfVertices];
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
                if (graph.containsEdge(u, v) && colors.get(v).equals(-1)) {
                    colors.put(v, 1 - colors.get(u));
                    q.add(v);
                }
                // An edge from u to v exists and destination
                //  v is colored with same color as u
                else if (graph.containsEdge(u, v) && colors.get(v).equals(colors.get(u))) {
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
                if(graph.containsEdge(right, left)){
                    maybePartners.add(left);
                }
            }
            if(!maybePartners.isEmpty()) {
                bipartition.put(right, maybePartners);
            }
        }
        for(Map.Entry<Person, List<Person>> people : bipartition.entrySet()){
            System.out.print(people.getKey() + "-->");
            for(Person partner : people.getValue()){
                System.out.print(partner + ", ");
            }
            System.out.println();
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
        adjMatrix = new int[graph.vertexSet().size()][graph.vertexSet().size()];
        for(Map.Entry<Person, List<Person>> entry : bipartition.entrySet()){
            for(Person maybePartner : entry.getValue()){
                adjMatrix[catalogIDS.get(entry.getKey())][catalogIDS.get(maybePartner)] = 1;
            }
        }
//        for(int i = 0; i < graph.vertexSet().size(); i++){
//            for(int j = 0; j < graph.vertexSet().size(); j++){
//                System.out.print(adjMatrix[i][j] + ", ");
//            }
//            System.out.println();
//        }
    }
    public void makeAdjacencyList(){
        for(String vertex1 : graph.vertexSet()){
            for(String vertex2 : graph.vertexSet()) {
                if(graph.containsEdge(vertex1, vertex2)){
                    adj[catalogIDS.get(vertex1)].add(catalogIDS.get(vertex2));
                }
            }
        }
        for(int i = 0; i < numOfVertices; i++){
            System.out.print("v=" + getKey((i)) + "-->");
            for(int j : adj[i]){
                System.out.print(getKey(j) + ", ");
            }
            System.out.println();
        }
    }
    public Map<Person, Person> maxMatching() {
        int players1 = graph.vertexSet().size();
        int players2 = graph.vertexSet().size();

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
