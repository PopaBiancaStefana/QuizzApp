package com.app.quiz.statistics;

import com.app.quiz.models.Person;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Link;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.dot.DOTExporter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.NoSuchFileException;
import java.util.*;

import static guru.nidi.graphviz.model.Factory.mutGraph;
import static guru.nidi.graphviz.model.Factory.mutNode;

public class Statistics {
    private List<Person> people;

    private Map<String, List<String>> peopleDomains = new HashMap<>();

    public List<Person> getPeople() {
        return people;
    }

    public void setPeople(List<Person> people) {
        this.people = people;
    }

    public Map<String, List<String>> getPeopleDomains() {
        return peopleDomains;
    }


    public Statistics(List<Person> people) {
        this.people = people;
    }

    public void makeNetwork() {
        addDomains();
        makeDot(getGraph());         //OPTIONAL
        makeSVG(getMutableGraph(getGraph()), "network.svg");
    }
    public void makeTeamsSVG(Graph<Person, DefaultEdge> graph){
        addDomains();
        makeDot(graph);         //OPTIONAL
        makeSVG(getMutableTeamsGraph(graph), "networkBipartite.svg");
    }

    private void makeSVG(MutableGraph mutableGraph, String fileName) {
        String pathname = "src/main/resources/" + fileName;
        File network = new File(pathname);
        network.delete();
        try {
            Graphviz.fromGraph(mutableGraph).width(700).render(Format.SVG).toFile(new File("./" + pathname));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public MutableGraph getMutableGraph(Graph<Person, DefaultEdge> simpleGraph) {

        MutableGraph mutableGraph = mutGraph("network").setDirected(false);
        for (Person person : people) {
            List<Person> partners = findPartners(person);
            MutableNode node1 = mutNode(person.getEmail());
            for (Person partner : partners) {
                if (simpleGraph.getEdge(partner, person) == null) {
                    node1.addLink(Link.to(mutNode(partner.getEmail())));
                }
            }
            if (partners.size() != 0) {
                mutableGraph.add(node1);
            }
        }
        return mutableGraph;
    }
    public MutableGraph getMutableTeamsGraph(Graph<Person, DefaultEdge> simpleGraph){
        MutableGraph mutableGraph = mutGraph("network").setDirected(false);
        for(Person person : simpleGraph.vertexSet()){
            MutableNode node1 = mutNode(person.getEmail());
            for(Person partner : simpleGraph.vertexSet()){
                if(simpleGraph.containsEdge(person, partner)){
                    node1.addLink(Link.to(mutNode(partner.getEmail())));
                }
            }
            mutableGraph.add(node1);
        }
        return mutableGraph;
    }

    public  Graph<Person, DefaultEdge> getGraph() {
        Graph<Person, DefaultEdge> simpleGraph = new SimpleDirectedGraph<>(DefaultEdge.class);
        this.people.forEach(simpleGraph::addVertex);

        for (Person person : people) {
            List<Person> partners = findPartners(person);
            for (Person partner : partners) {
                if(!partners.isEmpty()){
                    if (simpleGraph.getEdge(partner, person) == null) {
                        simpleGraph.addEdge(person, partner);
                    }
                }
            }
        }
        return simpleGraph;
    }
    public  Graph<Person, DefaultEdge> getGraphForBipartition() {
        Graph<Person, DefaultEdge> simpleGraph = new SimpleDirectedGraph<>(DefaultEdge.class);
        this.people.forEach(simpleGraph::addVertex);

        for (Person person : people) {
            List<Person> partners = findPartners(person);
            if(partners.isEmpty()){
                simpleGraph.removeVertex(person);
            }
            for (Person partner : partners) {
                if (simpleGraph.getEdge(partner, person) == null) {
                    simpleGraph.addEdge(person, partner);
                }
            }
        }
        return simpleGraph;
    }

    //function that returns the people that have at least 5 domains in common
    public List<Person> findPartners(Person person) {
        List<Person> partners = new ArrayList<>();
        List<String> personD = peopleDomains.get(person.getEmail());
        for (Person partner : people) {
            if (!person.equals(partner)) {
                List<String> partnerD = peopleDomains.get(partner.getEmail());
                int match = 0;
                for (String personDomain : personD) {
                    for (String partnerDomain : partnerD) {
                        if (personDomain.equals(partnerDomain)) {
                            match++;
                        }
                    }
                    if (match >= 5) {
                        partners.add(partner);
                        break;
                    }
                }
            }
        }
        return partners;
    }

    //function that populates the map with email - domains entries
    public void addDomains() {
        for (Person p : people) {
            String[] tokens = p.getDomenii().split(",");
            List<String> domenii = new ArrayList<>();
            for (String t : tokens) {
                domenii.add(t);
            }
            peopleDomains.put(p.getEmail(), domenii);
        }
    }


    private void makeDot(Graph<Person, DefaultEdge> graph) {
        DOTExporter<Person, DefaultEdge> exporter = new DOTExporter<>();

        exporter.setVertexAttributeProvider((v) -> {
            Map<String, Attribute> map = new LinkedHashMap<>();
            map.put("label", DefaultAttribute.createAttribute(v.toString()));
            return map;
        });


        Writer writer = new StringWriter();
        exporter.exportGraph(graph, writer);

        try {
            FileWriter fileWriter = new FileWriter("./src/main/resources/network.dot");
            fileWriter.write(writer.toString());
            fileWriter.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void calculateStatistics() {
        addDomains();
    }
}
