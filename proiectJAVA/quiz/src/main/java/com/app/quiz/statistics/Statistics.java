package com.app.quiz.statistics;

import com.app.quiz.models.Person;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.parse.Parser;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.dot.DOTExporter;

import java.io.*;
import java.util.*;

public class Statistics{
    private List<Person> people;
    private Map<String, List<String>> peopleDomains = new HashMap<>();


    public Statistics(List<Person> people) {
        setPeople(people);
    }

    public List<Person> getPeople() {
        return people;
    }

    public void setPeople(List<Person> people) {
        this.people = people;
    }

    public Map<String, List<String>> getPeopleDomains() {
        return peopleDomains;
    }


    public void calculateStatistics() {
        this.addDomains();
        createSVG(this.getGraph());
    }

    public synchronized void  createSVG(Graph<Person, DefaultEdge> graph) {

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
        try (InputStream dot = getClass().getResourceAsStream("/network.dot")) {
            MutableGraph g = new Parser().read(dot);
            Graphviz.fromGraph(g).width(700).render(Format.SVG).toFile(new File("./src/main/resources/network.svg"));
        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }


    public Graph<Person, DefaultEdge> getGraph() {
        Graph<Person, DefaultEdge> graph = new SimpleDirectedGraph<>(DefaultEdge.class);

        this.people.forEach(graph::addVertex);


        for (Person person : people){
            List<Person> partners = findPartners(person);
            for( Person partner : partners){
                graph.addEdge(person,partner);
            }
        }
        return graph;
    }

    //function that returns the people that have at least 3 domains in common
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
                    if (match >= 3) {
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

}
