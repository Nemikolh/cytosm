package org.cytosm.pathfinder;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import org.cytosm.cypher2sql.expandpaths.CypherConverter;
import org.cytosm.common.gtop.GTopInterfaceImpl;
import org.cytosm.common.gtop.RelationalGTopInterface;
import org.cytosm.common.gtop.abstraction.AbstractionEdge;
import org.cytosm.common.gtop.abstraction.AbstractionNode;
import org.cytosm.pathfinder.routeelements.ExpansionEdge;
import org.cytosm.pathfinder.routeelements.ExpansionElement;
import org.cytosm.pathfinder.routeelements.ExpansionNode;

/***
 * Compares the Hints with the values actually provided on GTop
 *
 *
 */
public class HintMatchingTest {

    GTopInterfaceImpl gTopInter;

    @Before
    public void initTest() {
        // load gtop
        String path = "src" + File.separatorChar + "test" + File.separatorChar + "resources" + File.separatorChar;

        try {
            gTopInter = new RelationalGTopInterface(new File(path + "movies.gtop"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void annonymousNodeWithoutHint() {

        String cypherInput = "()--()";

        PathFinder matcher = new PathFinder(new CypherConverter());

        CanonicalRoutes paths = matcher.generateCanonicalRoutesFromInput(cypherInput);

        matcher.evaluateEnumerationStrategy(paths, gTopInter);

        int numberOfHints = 0;

        for (ExpansionElement element : paths.getAllPossibleRoutes().get(0)) {
            if (element.isHintAvailable()) {
                numberOfHints++;
            }
        }

        Assert.assertEquals(0, numberOfHints);
    }

    @Test
    public void annonymousNodeWithOneHint() {

        String cypherInput = "(p:person)--()";

        PathFinder matcher = new PathFinder(new CypherConverter());

        CanonicalRoutes paths = matcher.generateCanonicalRoutesFromInput(cypherInput);
        matcher.evaluateEnumerationStrategy(paths, gTopInter);

        int numberOfHints = 0;

        for (ExpansionElement element : paths.getAllPossibleRoutes().get(0)) {
            if (element.isHintAvailable()) {
                numberOfHints++;
            }
        }

        Assert.assertEquals(1, numberOfHints);

        ExpansionNode hintNode = (ExpansionNode) paths.getAllPossibleRoutes().get(0).get(0);

        Assert.assertTrue(hintNode.isHintAvailable());

        ArrayList<Object> abstractObjects = new ArrayList<>(hintNode.getMatchedGtopAbstractionEntities());
        AbstractionNode absNode = (AbstractionNode) abstractObjects.get(0);
        Assert.assertEquals(gTopInter.getAbstractionNodesBySynonym("person").get(0), absNode);

    }

    @Test
    public void annonymousNodeWithTwoHints() {

        String cypherInput = "(:person)-->(k:movie)-->( )";

        PathFinder matcher = new PathFinder(new CypherConverter());

        CanonicalRoutes paths = matcher.generateCanonicalRoutesFromInput(cypherInput);
        matcher.evaluateEnumerationStrategy(paths, gTopInter);

        int numberOfHints = 0;

        for (ExpansionElement element : paths.getAllPossibleRoutes().get(0)) {
            if (element.isHintAvailable()) {
                numberOfHints++;
            }
        }

        Assert.assertEquals(2, numberOfHints);

        ExpansionNode hintNode0 = (ExpansionNode) paths.getAllPossibleRoutes().get(0).get(0);
        ExpansionNode hintNode1 = (ExpansionNode) paths.getAllPossibleRoutes().get(0).get(2);

        Assert.assertTrue(hintNode0.isHintAvailable());
        Assert.assertTrue(hintNode1.isHintAvailable());

        ArrayList<Object> abstractObjects = new ArrayList<>(hintNode0.getMatchedGtopAbstractionEntities());
        ArrayList<Object> abstractObjects2 = new ArrayList<>(hintNode1.getMatchedGtopAbstractionEntities());

        AbstractionNode absNode0 = (AbstractionNode) abstractObjects.get(0);
        AbstractionNode absNode1 = (AbstractionNode) abstractObjects2.get(0);

        Assert.assertEquals(gTopInter.getAbstractionNodesBySynonym("Person").get(0), absNode0);
        Assert.assertEquals(gTopInter.getAbstractionNodesBySynonym("movie").get(0), absNode1);
    }

    @Test
    public void annonymousWithEdgeHint() {

        String cypherInput = "()-[:person_id_acted_in_person_id]->()";

        PathFinder matcher = new PathFinder(new CypherConverter());

        CanonicalRoutes paths = matcher.generateCanonicalRoutesFromInput(cypherInput);
        matcher.evaluateEnumerationStrategy(paths, gTopInter);

        int numberOfHints = 0;

        for (ExpansionElement element : paths.getAllPossibleRoutes().get(0)) {
            if (element.isHintAvailable()) {
                numberOfHints++;
            }
        }

        Assert.assertEquals(1, numberOfHints);

        ExpansionEdge hintEdge = (ExpansionEdge) paths.getAllPossibleRoutes().get(0).get(1);

        Assert.assertTrue(hintEdge.isHintAvailable());

        ArrayList<Object> abstractObjects = new ArrayList<>(hintEdge.getMatchedGtopAbstractionEntities());
        AbstractionEdge absEdge = (AbstractionEdge) abstractObjects.get(0);
        Assert.assertEquals(gTopInter.getAbstractionEdgesBySynonym("person_id_acted_in_person_id").get(0), absEdge);
    }

    @Test
    public void edgeHintAndNodeHint() {

        String cypherInput = "(:Person)-[:person_id_acted_in_person_id]-()";

        PathFinder matcher = new PathFinder(new CypherConverter());

        CanonicalRoutes paths = matcher.generateCanonicalRoutesFromInput(cypherInput);
        matcher.evaluateEnumerationStrategy(paths, gTopInter);

        int numberOfHints = 0;

        for (ExpansionElement element : paths.getAllPossibleRoutes().get(0)) {
            if (element.isHintAvailable()) {
                numberOfHints++;
            }
        }

        Assert.assertEquals(2, numberOfHints);

        ExpansionEdge hintEdge = (ExpansionEdge) paths.getAllPossibleRoutes().get(0).get(1);
        ExpansionNode hintNode = (ExpansionNode) paths.getAllPossibleRoutes().get(0).get(0);

        Assert.assertTrue(hintEdge.isHintAvailable());

        ArrayList<Object> abstractObjects = new ArrayList<>(hintEdge.getMatchedGtopAbstractionEntities());
        ArrayList<Object> abstractObjects2 = new ArrayList<>(hintNode.getMatchedGtopAbstractionEntities());

        AbstractionEdge absEdge = (AbstractionEdge) abstractObjects.get(0);
        AbstractionNode absNode = (AbstractionNode) abstractObjects2.get(0);

        Assert.assertEquals(gTopInter.getAbstractionEdgesBySynonym("person_id_acted_in_person_id").get(0), absEdge);
        Assert.assertEquals(gTopInter.getAbstractionNodesBySynonym("person").get(0), absNode);
    }

    // Exception should be thrown here:
    @Test
    public void wrongEdgeHint() {

        String cypherInput = "()-[:wrote_paper]-()";

        PathFinder matcher = new PathFinder(new CypherConverter());

        CanonicalRoutes paths = matcher.generateCanonicalRoutesFromInput(cypherInput);
        matcher.evaluateEnumerationStrategy(paths, gTopInter);
    }

    // Exception should be thrown here:
    @Test
    public void wrongNodeHint() {

        String cypherInput = "(:Driver)--()";

        PathFinder matcher = new PathFinder(new CypherConverter());

        CanonicalRoutes paths = matcher.generateCanonicalRoutesFromInput(cypherInput);
        matcher.evaluateEnumerationStrategy(paths, gTopInter);
    }

    // Remove
    @Test
    public void annonymousNodeWithOneExternalNodeHint() {

        String cypherInput = "(p)--()";

        HashMap<String, List<String>> externalHint = new HashMap<>();

        List<String> pAttributeList = new ArrayList<>();
        pAttributeList.add("name");

        externalHint.put("p", pAttributeList);

        PathFinder matcher = new PathFinder(new CypherConverter());
        matcher.getInputFormat().addExternalContext(externalHint);

        CanonicalRoutes paths = matcher.generateCanonicalRoutesFromInput(cypherInput);
        matcher.evaluateEnumerationStrategy(paths, gTopInter);

        int numberOfHints = 0;

        for (ExpansionElement element : paths.getAllPossibleRoutes().get(0)) {
            if (element.isHintAvailable()) {
                numberOfHints++;
            }
        }

        Assert.assertEquals(1, numberOfHints);

        ExpansionNode hintNode = (ExpansionNode) paths.getAllPossibleRoutes().get(0).get(0);

        Assert.assertTrue(hintNode.isHintAvailable());

        ArrayList<Object> abstractObjects = new ArrayList<>(hintNode.getMatchedGtopAbstractionEntities());
        AbstractionNode absNode = (AbstractionNode) abstractObjects.get(0);
        Assert.assertEquals(gTopInter.getAbstractionNodesBySynonym("person").get(0), absNode);
    }

    @Test
    public void annonymousNodeWithOneExternalEdgeHint() {

        String cypherInput = "()-[e]->()";

        PathFinder matcher = new PathFinder(new CypherConverter());

        HashMap<String, List<String>> externalHint = new HashMap<>();

        List<String> eAttributeList = new ArrayList<>();
        eAttributeList.add("role");


        externalHint.put("e", eAttributeList);

        matcher.getInputFormat().addExternalContext(externalHint);

        CanonicalRoutes paths = matcher.generateCanonicalRoutesFromInput(cypherInput);
        matcher.evaluateEnumerationStrategy(paths, gTopInter);

        int numberOfHints = 0;

        for (ExpansionElement element : paths.getAllPossibleRoutes().get(0)) {
            if (element.isHintAvailable()) {
                numberOfHints++;
            }
        }

        Assert.assertEquals(1, numberOfHints);

        ExpansionEdge hintEdge = (ExpansionEdge) paths.getAllPossibleRoutes().get(0).get(1);

        Assert.assertTrue(hintEdge.isHintAvailable());

        ArrayList<Object> abstractObjects = new ArrayList<>(hintEdge.getMatchedGtopAbstractionEntities());
        AbstractionEdge absEdge = (AbstractionEdge) abstractObjects.get(0);
        Assert.assertEquals(gTopInter.getAbstractionEdgesBySynonym("person_id_acted_in_person_id").get(0), absEdge);

    }

    @Test
    public void annonymousNodeWithHint() {

        String cypherInput = "(p:person)-->()";

        HashMap<String, List<String>> externalHint = new HashMap<>();
        List<String> pAttributeList = new ArrayList<>();
        pAttributeList.add("name");

        externalHint.put("p", pAttributeList);

        PathFinder matcher = new PathFinder(new CypherConverter());
        matcher.getInputFormat().addExternalContext(externalHint);

        CanonicalRoutes paths = matcher.generateCanonicalRoutesFromInput(cypherInput);
        matcher.evaluateEnumerationStrategy(paths, gTopInter);


        int numberOfHints = 0;

        for (ExpansionElement element : paths.getAllPossibleRoutes().get(0)) {
            if (element.isHintAvailable()) {
                numberOfHints++;
            }
        }

        Assert.assertEquals(1, numberOfHints);

        ExpansionNode hintNode = (ExpansionNode) paths.getAllPossibleRoutes().get(0).get(0);

        Assert.assertTrue(hintNode.isHintAvailable());
        Assert.assertEquals("p", hintNode.getVariable());

        ArrayList<Object> abstractObjects = new ArrayList<>(hintNode.getMatchedGtopAbstractionEntities());
        AbstractionNode absNode = (AbstractionNode) abstractObjects.get(0);
        Assert.assertEquals(gTopInter.getAbstractionNodesBySynonym("person").get(0), absNode);

    }

    @Test
    public void annonymousNodeWithTwoHintsOnSameVariable() {

        String cypherInput = "(p:person)-->()";

        HashMap<String, List<String>> externalHint = new HashMap<>();
        List<String> pAttributeList = new ArrayList<>();
        pAttributeList.add("name");
        pAttributeList.add("born");

        externalHint.put("p", pAttributeList);

        PathFinder matcher = new PathFinder(new CypherConverter());
        matcher.getInputFormat().addExternalContext(externalHint);

        CanonicalRoutes paths = matcher.generateCanonicalRoutesFromInput(cypherInput);
        matcher.evaluateEnumerationStrategy(paths, gTopInter);

        int numberOfHints = 0;

        for (ExpansionElement element : paths.getAllPossibleRoutes().get(0)) {
            if (element.isHintAvailable()) {
                numberOfHints++;
            }
        }

        Assert.assertEquals(1, numberOfHints);

        ExpansionNode hintNode = (ExpansionNode) paths.getAllPossibleRoutes().get(0).get(0);

        Assert.assertTrue(hintNode.isHintAvailable());
        Assert.assertEquals("p", hintNode.getVariable());

        ArrayList<Object> abstractObjects = new ArrayList<>(hintNode.getMatchedGtopAbstractionEntities());
        AbstractionNode absNode = (AbstractionNode) abstractObjects.get(0);
        Assert.assertEquals(gTopInter.getAbstractionNodesBySynonym("person").get(0), absNode);
        
        // Should have two attributes
        Assert.assertEquals(hintNode.getAttributeMap().keySet().size(), pAttributeList.size());
        Assert.assertEquals(hintNode.getAttributeMap().keySet().contains(pAttributeList.get(0)), true);
        Assert.assertEquals(hintNode.getAttributeMap().keySet().contains(pAttributeList.get(1)), true);
    }
    
    /***
     * The external context to narrow down the search is invalid should not work in this scenario.
     * The result should be the same as if the external was not added/ignored.
     */
    @Test
    public void annonymousNodeWithOneExternalInvalidNodeHint() {

        String cypherInput = "()-->()";

        HashMap<String, List<String>> externalHint = new HashMap<>();

        List<String> kAttributeList = new ArrayList<>();
        kAttributeList.add("eyeColors");

        externalHint.put("k", kAttributeList);

        PathFinder matcher = new PathFinder(new CypherConverter());
        matcher.getInputFormat().addExternalContext(externalHint);

        CanonicalRoutes paths = matcher.generateCanonicalRoutesFromInput(cypherInput);
        matcher.evaluateEnumerationStrategy(paths, gTopInter);

        int numberOfHints = 0;

        for (ExpansionElement element : paths.getAllPossibleRoutes().get(0)) {
            if (element.isHintAvailable()) {
                numberOfHints++;
            }
        }

        Assert.assertEquals(0, numberOfHints);
    }

    /***
     * The external context to narrow down the search is invalid should not work in this scenario.
     * The result should be the same as if the external was not added/ignored.
     */
    @Test
    public void annonymousNodeWithOneExternalInvalidEdgeHint() {

        String cypherInput = "()-[e]->()";

        PathFinder matcher = new PathFinder(new CypherConverter());

        HashMap<String, List<String>> externalHint = new HashMap<>();


        List<String> yAttributeList = new ArrayList<>();
        yAttributeList.add("Drives");

        externalHint.put("y", yAttributeList);

        matcher.getInputFormat().addExternalContext(externalHint);

        CanonicalRoutes paths = matcher.generateCanonicalRoutesFromInput(cypherInput);
        matcher.evaluateEnumerationStrategy(paths, gTopInter);

        int numberOfHints = 0;

        for (ExpansionElement element : paths.getAllPossibleRoutes().get(0)) {
            if (element.isHintAvailable()) {
                numberOfHints++;
            }
        }

        Assert.assertEquals(0, numberOfHints);
    }
}
