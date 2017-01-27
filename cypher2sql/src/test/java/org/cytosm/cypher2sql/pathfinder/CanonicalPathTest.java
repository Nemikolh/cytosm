package org.cytosm.pathfinder;


import java.util.List;
import java.util.stream.Collectors;

import org.cytosm.cypher2sql.expandpaths.CypherConverter;
import org.junit.Assert;
import org.junit.Test;

import org.cytosm.pathfinder.routeelements.ExpansionEdge;
import org.cytosm.pathfinder.routeelements.ExpansionElement;

public class CanonicalPathTest {

    @Test
    public void AnnonymousNodeWithoutDirection() {

        String cypherInput = "()--()--( )--()";

        PathFinder matcher = new PathFinder(new CypherConverter());

        CanonicalRoutes paths = matcher.generateCanonicalRoutesFromInput(cypherInput);

        List<List<ExpansionElement>> routes = paths.getAllPossibleRoutes();

        Assert.assertEquals(1, routes.size());
        Assert.assertEquals(7, routes.get(0).size());

        boolean anyDirected;
        anyDirected = routes.get(0).stream().anyMatch((element) -> {
            boolean isdirected = false;
            if (element instanceof ExpansionEdge) {
                if (((ExpansionEdge) element).isDirected()) {
                    isdirected = true;
                }
            }
            return isdirected;
        });

        // no directed edges
        Assert.assertEquals(anyDirected, false);
    }

    @Test
    public void AnnonymousNodeWithOneDirection() {

        String cypherInput = "()-->()--( )--()";

        PathFinder matcher = new PathFinder(new CypherConverter());

        CanonicalRoutes paths = matcher.generateCanonicalRoutesFromInput(cypherInput);

        List<List<ExpansionElement>> routes = paths.getAllPossibleRoutes();

        Assert.assertEquals(1, routes.size());
        Assert.assertEquals(7, routes.get(0).size());

        List<ExpansionElement> directedList = routes.get(0).stream().filter((element) -> {
            boolean isdirected = false;
            if (element instanceof ExpansionEdge) {
                if (((ExpansionEdge) element).isDirected()) {
                    isdirected = true;
                }
            }
            return isdirected;
        }).collect(Collectors.toList());

        // one directed edges
        Assert.assertEquals(1, directedList.size());

        // only the second element (first edge) is directed:
        Assert.assertEquals(directedList.get(0), routes.get(0).get(1));
    }

    @Test
    public void AnnonymousNodeWithControversalDirection() {

        String cypherInput = "()-->()--( )<--()";

        PathFinder matcher = new PathFinder(new CypherConverter());

        CanonicalRoutes paths = matcher.generateCanonicalRoutesFromInput(cypherInput);

        List<List<ExpansionElement>> routes = paths.getAllPossibleRoutes();

        Assert.assertEquals(1, routes.size());
        Assert.assertEquals(7, routes.get(0).size());

        List<ExpansionElement> directedList = routes.get(0).stream().filter((element) -> {
            boolean isdirected = false;
            if (element instanceof ExpansionEdge) {
                if (((ExpansionEdge) element).isDirected()) {
                    isdirected = true;
                }
            }
            return isdirected;
        }).collect(Collectors.toList());

        // two directed edges
        Assert.assertEquals(2, directedList.size());

        // only the second element and sixth (first and third edge) are directed:
        Assert.assertTrue(directedList.contains(routes.get(0).get(1)));
        Assert.assertTrue(directedList.contains(routes.get(0).get(5)));
    }

    @Test
    public void AnnonymousNodeAllDirected() {

        String cypherInput = "()-->()-->( )<--()";

        PathFinder matcher = new PathFinder(new CypherConverter());

        CanonicalRoutes paths = matcher.generateCanonicalRoutesFromInput(cypherInput);

        List<List<ExpansionElement>> routes = paths.getAllPossibleRoutes();

        Assert.assertEquals(1, routes.size());
        Assert.assertEquals(7, routes.get(0).size());

        List<ExpansionElement> directedList = routes.get(0).stream().filter((element) -> {
            boolean isdirected = false;
            if (element instanceof ExpansionEdge) {
                if (((ExpansionEdge) element).isDirected()) {
                    isdirected = true;
                }
            }
            return isdirected;
        }).collect(Collectors.toList());

        List<ExpansionElement> edgeList =
                routes.get(0).stream().filter((element) -> element instanceof ExpansionEdge)
                        .collect(Collectors.toList());

        // three directed edges
        Assert.assertEquals(3, directedList.size());

        // All elements should be in both lists
        edgeList.forEach(edge -> Assert.assertTrue(directedList.contains(edge)));
    }

    @Test
    public void AnnonymousNodeWithNoWildcard() {

        String cypherInput = "()-[:knows]->()";

        PathFinder matcher = new PathFinder(new CypherConverter());

        CanonicalRoutes paths = matcher.generateCanonicalRoutesFromInput(cypherInput);

        List<List<ExpansionElement>> routes = paths.getAllPossibleRoutes();

        Assert.assertEquals(1, routes.size());
        Assert.assertEquals(3, routes.get(0).size());

        // test first possibility:
        List<ExpansionElement> directedList = routes.get(0).stream().filter((element) -> {
            boolean isdirected = false;
            if (element instanceof ExpansionEdge) {
                if (((ExpansionEdge) element).isDirected()) {
                    isdirected = true;
                }
            }
            return isdirected;
        }).collect(Collectors.toList());

        List<ExpansionElement> edgeList =
                routes.get(0).stream().filter((element) -> element instanceof ExpansionEdge)
                        .collect(Collectors.toList());

        // one directed edges
        Assert.assertEquals(1, directedList.size());

        // All elements should be in both lists
        directedList.forEach(edge -> Assert.assertTrue(edgeList.contains(edge)));

        // Assert that there's none edge pointing to the left
        Assert.assertEquals(0, directedList.stream().filter((edge) -> ((ExpansionEdge) edge).isToLeft()).count());
    }

    @Test
    public void AnnonymousNodeWithNoWildcard1() {

        String cypherInput = "()-[:knows*1]->()";

        PathFinder matcher = new PathFinder(new CypherConverter());

        CanonicalRoutes paths = matcher.generateCanonicalRoutesFromInput(cypherInput);

        List<List<ExpansionElement>> routes = paths.getAllPossibleRoutes();

        Assert.assertEquals(1, routes.size());
        Assert.assertEquals(3, routes.get(0).size());

        // test first possibility:
        List<ExpansionElement> directedList = routes.get(0).stream().filter((element) -> {
            boolean isdirected = false;
            if (element instanceof ExpansionEdge) {
                if (((ExpansionEdge) element).isDirected()) {
                    isdirected = true;
                }
            }
            return isdirected;
        }).collect(Collectors.toList());

        List<ExpansionElement> edgeList =
                routes.get(0).stream().filter((element) -> element instanceof ExpansionEdge)
                        .collect(Collectors.toList());

        // one directed edges
        Assert.assertEquals(1, directedList.size());

        // All elements should be in both lists
        directedList.forEach(edge -> Assert.assertTrue(edgeList.contains(edge)));

        // Assert that there's none edge pointing to the left
        Assert.assertEquals(0, directedList.stream().filter((edge) -> ((ExpansionEdge) edge).isToLeft()).count());
    }


    @Test
    public void AnnonymousNodeWith2WildCard() {

        String cypherInput = "()-[:knows*2]->()";

        PathFinder matcher = new PathFinder(new CypherConverter());

        CanonicalRoutes paths = matcher.generateCanonicalRoutesFromInput(cypherInput);

        List<List<ExpansionElement>> routes = paths.getAllPossibleRoutes();

        Assert.assertEquals(1, routes.size());
        Assert.assertEquals(5, routes.get(0).size());


        // test first possibility:
        List<ExpansionElement> directedList = routes.get(0).stream().filter((element) -> {
            boolean isdirected = false;
            if (element instanceof ExpansionEdge) {
                if (((ExpansionEdge) element).isDirected()) {
                    isdirected = true;
                }
            }
            return isdirected;
        }).collect(Collectors.toList());

        List<ExpansionElement> edgeList =
                routes.get(0).stream().filter((element) -> element instanceof ExpansionEdge)
                        .collect(Collectors.toList());

        // one directed edges
        Assert.assertEquals(2, directedList.size());

        // All elements should be in both lists
        directedList.forEach(edge -> Assert.assertTrue(edgeList.contains(edge)));
    }


    @Test
    public void AnnonymousNodeWithWildCardAndLeftEdge() {

        String cypherInput = "()-[:knows*1..3]->()-->( )<--()";

        PathFinder matcher = new PathFinder(new CypherConverter());

        CanonicalRoutes paths = matcher.generateCanonicalRoutesFromInput(cypherInput);

        List<List<ExpansionElement>> routes = paths.getAllPossibleRoutes();

        Assert.assertEquals(3, routes.size());
        Assert.assertEquals(7, routes.get(0).size());
        Assert.assertEquals(9, routes.get(1).size());
        Assert.assertEquals(11, routes.get(2).size());

        // test first possibility:
        List<ExpansionElement> directedList = routes.get(0).stream().filter((element) -> {
            boolean isdirected = false;
            if (element instanceof ExpansionEdge) {
                if (((ExpansionEdge) element).isDirected()) {
                    isdirected = true;
                }
            }
            return isdirected;
        }).collect(Collectors.toList());

        List<ExpansionElement> edgeList =
                routes.get(0).stream().filter((element) -> element instanceof ExpansionEdge)
                        .collect(Collectors.toList());

        // one directed edges
        Assert.assertEquals(3, directedList.size());

        // All elements should be in both lists
        directedList.forEach(edge -> Assert.assertTrue(edgeList.contains(edge)));

        // Assert that there's only one edge pointing to the left
        Assert.assertEquals(1, directedList.stream().filter((edge) -> ((ExpansionEdge) edge).isToLeft()).count());


        // Test second possibility
        List<ExpansionElement> directedList2 = routes.get(1).stream().filter((element) -> {
            boolean isdirected = false;
            if (element instanceof ExpansionEdge) {
                if (((ExpansionEdge) element).isDirected()) {
                    isdirected = true;
                }
            }
            return isdirected;
        }).collect(Collectors.toList());

        List<ExpansionElement> edgeList2 =
                routes.get(1).stream().filter((element) -> element instanceof ExpansionEdge)
                        .collect(Collectors.toList());

        // one directed edges
        Assert.assertEquals(4, directedList2.size());

        // All elements should be in both lists
        directedList2.forEach(edge -> Assert.assertTrue(edgeList2.contains(edge)));

        // Assert that there's only one edge pointing to the left
        Assert.assertEquals(1, directedList2.stream().filter((edge) -> ((ExpansionEdge) edge).isToLeft()).count());

        // Test Third possibility
        List<ExpansionElement> directedList3 = routes.get(2).stream().filter((element) -> {
            boolean isdirected = false;
            if (element instanceof ExpansionEdge) {
                if (((ExpansionEdge) element).isDirected()) {
                    isdirected = true;
                }
            }
            return isdirected;
        }).collect(Collectors.toList());

        List<ExpansionElement> edgeList3 =
                routes.get(2).stream().filter((element) -> element instanceof ExpansionEdge)
                        .collect(Collectors.toList());

        // one directed edges
        Assert.assertEquals(5, directedList3.size());

        // All elements should be in both lists
        directedList3.forEach(edge -> Assert.assertTrue(edgeList3.contains(edge)));

        // Assert that there's only one edge pointing to the left
        Assert.assertEquals(1, directedList3.stream().filter((edge) -> ((ExpansionEdge) edge).isToLeft()).count());
    }

    @Test
    public void AnnonymousNodeWithTwoWildCard() {

        String cypherInput = "()-[:knows*1..3]->()-->( )<-[:DancesWith*1..2]-()";

        PathFinder matcher = new PathFinder(new CypherConverter());

        CanonicalRoutes paths = matcher.generateCanonicalRoutesFromInput(cypherInput);

        List<List<ExpansionElement>> routes = paths.getAllPossibleRoutes();

        Assert.assertEquals(6, routes.size());
        Assert.assertEquals(7, routes.get(0).size());
        Assert.assertEquals(9, routes.get(1).size());
        Assert.assertEquals(9, routes.get(2).size());
        Assert.assertEquals(11, routes.get(3).size());
        Assert.assertEquals(11, routes.get(4).size());
        Assert.assertEquals(13, routes.get(5).size());

        // FIRST POSSIBILITY:
        List<ExpansionElement> directedList = routes.get(0).stream().filter((element) -> {
            boolean isdirected = false;
            if (element instanceof ExpansionEdge) {
                if (((ExpansionEdge) element).isDirected()) {
                    isdirected = true;
                }
            }
            return isdirected;
        }).collect(Collectors.toList());

        List<ExpansionElement> edgeList =
                routes.get(0).stream().filter((element) -> element instanceof ExpansionEdge)
                        .collect(Collectors.toList());

        // 3 directed edges
        Assert.assertEquals(3, directedList.size());

        // All elements should be in both lists
        directedList.forEach(edge -> Assert.assertTrue(edgeList.contains(edge)));

        // Assert that there's only 1 edges pointing to the left
        Assert.assertEquals(1, directedList.stream().filter((edge) -> ((ExpansionEdge) edge).isToLeft()).count());
        // Assert that there's only 2 edges pointing to the right
        Assert.assertEquals(2, directedList.stream().filter((edge) -> ((ExpansionEdge) edge).isToRight()).count());


        // SECOND POSSIBILITY:
        List<ExpansionElement> directedList2 = routes.get(1).stream().filter((element) -> {
            boolean isdirected = false;
            if (element instanceof ExpansionEdge) {
                if (((ExpansionEdge) element).isDirected()) {
                    isdirected = true;
                }
            }
            return isdirected;
        }).collect(Collectors.toList());

        List<ExpansionElement> edgeList2 =
                routes.get(1).stream().filter((element) -> element instanceof ExpansionEdge)
                        .collect(Collectors.toList());

        // 4 directed edges
        Assert.assertEquals(4, directedList2.size());

        // All elements should be in both lists
        directedList2.forEach(edge -> Assert.assertTrue(edgeList2.contains(edge)));

        // Assert that there's only 2 edge pointing to the left
        Assert.assertEquals(2, directedList2.stream().filter((edge) -> ((ExpansionEdge) edge).isToLeft()).count());
        // Assert that there's only 2 edges pointing to the right
        Assert.assertEquals(2, directedList2.stream().filter((edge) -> ((ExpansionEdge) edge).isToRight()).count());

        // THIRD POSSIBILITY:
        List<ExpansionElement> directedList3 = routes.get(2).stream().filter((element) -> {
            boolean isdirected = false;
            if (element instanceof ExpansionEdge) {
                if (((ExpansionEdge) element).isDirected()) {
                    isdirected = true;
                }
            }
            return isdirected;
        }).collect(Collectors.toList());

        List<ExpansionElement> edgeList3 =
                routes.get(2).stream().filter((element) -> element instanceof ExpansionEdge)
                        .collect(Collectors.toList());

        // 4 directed edges
        Assert.assertEquals(4, directedList3.size());

        // All elements should be in both lists
        directedList3.forEach(edge -> Assert.assertTrue(edgeList3.contains(edge)));

        // Assert that there's only 1 edges pointing to the left
        Assert.assertEquals(1, directedList3.stream().filter((edge) -> ((ExpansionEdge) edge).isToLeft()).count());
        // Assert that there's only 3 edges pointing to the right
        Assert.assertEquals(3, directedList3.stream().filter((edge) -> ((ExpansionEdge) edge).isToRight()).count());

        // FORTH POSSIBILITY:
        List<ExpansionElement> directedList4 = routes.get(3).stream().filter((element) -> {
            boolean isdirected = false;
            if (element instanceof ExpansionEdge) {
                if (((ExpansionEdge) element).isDirected()) {
                    isdirected = true;
                }
            }
            return isdirected;
        }).collect(Collectors.toList());

        List<ExpansionElement> edgeList4 =
                routes.get(3).stream().filter((element) -> element instanceof ExpansionEdge)
                        .collect(Collectors.toList());

        // 5 directed edges
        Assert.assertEquals(5, directedList4.size());

        // All elements should be in both lists
        directedList4.forEach(edge -> Assert.assertTrue(edgeList4.contains(edge)));

        // Assert that there's only 2 edges pointing to the left
        Assert.assertEquals(2, directedList4.stream().filter((edge) -> ((ExpansionEdge) edge).isToLeft()).count());
        // Assert that there's only 3 edges pointing to the right
        Assert.assertEquals(3, directedList4.stream().filter((edge) -> ((ExpansionEdge) edge).isToRight()).count());

        // FIFTH POSSIBILITY:
        List<ExpansionElement> directedList5 = routes.get(4).stream().filter((element) -> {
            boolean isdirected = false;
            if (element instanceof ExpansionEdge) {
                if (((ExpansionEdge) element).isDirected()) {
                    isdirected = true;
                }
            }
            return isdirected;
        }).collect(Collectors.toList());

        List<ExpansionElement> edgeList5 =
                routes.get(4).stream().filter((element) -> element instanceof ExpansionEdge)
                        .collect(Collectors.toList());

        // 5 directed edges
        Assert.assertEquals(5, directedList5.size());

        // All elements should be in both lists
        directedList5.forEach(edge -> Assert.assertTrue(edgeList5.contains(edge)));

        // Assert that there's only 1 edges pointing to the left
        Assert.assertEquals(1, directedList5.stream().filter((edge) -> ((ExpansionEdge) edge).isToLeft()).count());
        // Assert that there's only 4 edges pointing to the right
        Assert.assertEquals(4, directedList5.stream().filter((edge) -> ((ExpansionEdge) edge).isToRight()).count());

        // SIXTH POSSIBILITY:
        List<ExpansionElement> directedList6 = routes.get(5).stream().filter((element) -> {
            boolean isdirected = false;
            if (element instanceof ExpansionEdge) {
                if (((ExpansionEdge) element).isDirected()) {
                    isdirected = true;
                }
            }
            return isdirected;
        }).collect(Collectors.toList());

        List<ExpansionElement> edgeList6 =
                routes.get(5).stream().filter((element) -> element instanceof ExpansionEdge)
                        .collect(Collectors.toList());

        // 6 directed edges
        Assert.assertEquals(6, directedList6.size());

        // All elements should be in both lists
        directedList6.forEach(edge -> Assert.assertTrue(edgeList6.contains(edge)));

        // Assert that there's only two edges pointing to the left
        Assert.assertEquals(2, directedList6.stream().filter((edge) -> ((ExpansionEdge) edge).isToLeft()).count());
        // Assert that there's only 4 edges pointing to the right
        Assert.assertEquals(4, directedList6.stream().filter((edge) -> ((ExpansionEdge) edge).isToRight()).count());
    }

    @Test
    public void AnnonymousNodeWithThreeWildCard() {

        String cypherInput = "()-[:knows*1..2]->()-[:knowsnone*1..2]->( )-[:knowssomeone*1..2]->()";

        PathFinder matcher = new PathFinder(new CypherConverter());

        CanonicalRoutes paths = matcher.generateCanonicalRoutesFromInput(cypherInput);

        List<List<ExpansionElement>> routes = paths.getAllPossibleRoutes();

        Assert.assertEquals(8, routes.size());

        Assert.assertEquals(7, routes.get(0).size());
        Assert.assertEquals(9, routes.get(1).size());
        Assert.assertEquals(9, routes.get(2).size());
        Assert.assertEquals(11, routes.get(3).size());
        Assert.assertEquals(9, routes.get(4).size());
        Assert.assertEquals(11, routes.get(5).size());
        Assert.assertEquals(11, routes.get(6).size());
        Assert.assertEquals(13, routes.get(7).size());

        // FIRST POSSIBILITY:
        List<ExpansionElement> directedList = routes.get(0).stream().filter((element) -> {
            boolean isdirected = false;
            if (element instanceof ExpansionEdge) {
                if (((ExpansionEdge) element).isDirected()) {
                    isdirected = true;
                }
            }
            return isdirected;
        }).collect(Collectors.toList());

        List<ExpansionElement> edgeList =
                routes.get(0).stream().filter((element) -> element instanceof ExpansionEdge)
                        .collect(Collectors.toList());

        // 3 directed edges
        Assert.assertEquals(3, directedList.size());

        // All elements should be in both lists
        directedList.forEach(edge -> Assert.assertTrue(edgeList.contains(edge)));

        // Assert that there's no edges pointing to the left
        Assert.assertEquals(0, directedList.stream().filter((edge) -> ((ExpansionEdge) edge).isToLeft()).count());
        // Assert that there's only 3 edges pointing to the right
        Assert.assertEquals(3, directedList.stream().filter((edge) -> ((ExpansionEdge) edge).isToRight()).count());


        // SECOND POSSIBILITY:
        List<ExpansionElement> directedList2 = routes.get(1).stream().filter((element) -> {
            boolean isdirected = false;
            if (element instanceof ExpansionEdge) {
                if (((ExpansionEdge) element).isDirected()) {
                    isdirected = true;
                }
            }
            return isdirected;
        }).collect(Collectors.toList());

        List<ExpansionElement> edgeList2 =
                routes.get(1).stream().filter((element) -> element instanceof ExpansionEdge)
                        .collect(Collectors.toList());

        // 4 directed edges
        Assert.assertEquals(4, directedList2.size());

        // All elements should be in both lists
        directedList2.forEach(edge -> Assert.assertTrue(edgeList2.contains(edge)));

        // Assert that there's no edge pointing to the left
        Assert.assertEquals(0, directedList2.stream().filter((edge) -> ((ExpansionEdge) edge).isToLeft()).count());
        // Assert that there's only 4 edges pointing to the right
        Assert.assertEquals(4, directedList2.stream().filter((edge) -> ((ExpansionEdge) edge).isToRight()).count());

        // THIRD POSSIBILITY:
        List<ExpansionElement> directedList3 = routes.get(2).stream().filter((element) -> {
            boolean isdirected = false;
            if (element instanceof ExpansionEdge) {
                if (((ExpansionEdge) element).isDirected()) {
                    isdirected = true;
                }
            }
            return isdirected;
        }).collect(Collectors.toList());

        List<ExpansionElement> edgeList3 =
                routes.get(2).stream().filter((element) -> element instanceof ExpansionEdge)
                        .collect(Collectors.toList());

        // 4 directed edges
        Assert.assertEquals(4, directedList3.size());

        // All elements should be in both lists
        directedList3.forEach(edge -> Assert.assertTrue(edgeList3.contains(edge)));

        // Assert that there's no edges pointing to the left
        Assert.assertEquals(0, directedList3.stream().filter((edge) -> ((ExpansionEdge) edge).isToLeft()).count());
        // Assert that there's only 4 edges pointing to the right
        Assert.assertEquals(4, directedList3.stream().filter((edge) -> ((ExpansionEdge) edge).isToRight()).count());

        // FORTH POSSIBILITY:
        List<ExpansionElement> directedList4 = routes.get(3).stream().filter((element) -> {
            boolean isdirected = false;
            if (element instanceof ExpansionEdge) {
                if (((ExpansionEdge) element).isDirected()) {
                    isdirected = true;
                }
            }
            return isdirected;
        }).collect(Collectors.toList());

        List<ExpansionElement> edgeList4 =
                routes.get(3).stream().filter((element) -> element instanceof ExpansionEdge)
                        .collect(Collectors.toList());

        // 5 directed edges
        Assert.assertEquals(5, directedList4.size());

        // All elements should be in both lists
        directedList4.forEach(edge -> Assert.assertTrue(edgeList4.contains(edge)));

        // Assert that there's no edges pointing to the left
        Assert.assertEquals(0, directedList4.stream().filter((edge) -> ((ExpansionEdge) edge).isToLeft()).count());
        // Assert that there's only 4 edges pointing to the right
        Assert.assertEquals(5, directedList4.stream().filter((edge) -> ((ExpansionEdge) edge).isToRight()).count());

        // FIFTH POSSIBILITY:
        List<ExpansionElement> directedList5 = routes.get(4).stream().filter((element) -> {
            boolean isdirected = false;
            if (element instanceof ExpansionEdge) {
                if (((ExpansionEdge) element).isDirected()) {
                    isdirected = true;
                }
            }
            return isdirected;
        }).collect(Collectors.toList());

        List<ExpansionElement> edgeList5 =
                routes.get(4).stream().filter((element) -> element instanceof ExpansionEdge)
                        .collect(Collectors.toList());

        // four directed edges
        Assert.assertEquals(4, directedList5.size());

        // All elements should be in both lists
        directedList5.forEach(edge -> Assert.assertTrue(edgeList5.contains(edge)));

        // Assert that there's no edges pointing to the left
        Assert.assertEquals(0, directedList5.stream().filter((edge) -> ((ExpansionEdge) edge).isToLeft()).count());
        // Assert that there's only 4 edges pointing to the right
        Assert.assertEquals(4, directedList5.stream().filter((edge) -> ((ExpansionEdge) edge).isToRight()).count());

        // SIXTH POSSIBILITY:
        List<ExpansionElement> directedList6 = routes.get(5).stream().filter((element) -> {
            boolean isdirected = false;
            if (element instanceof ExpansionEdge) {
                if (((ExpansionEdge) element).isDirected()) {
                    isdirected = true;
                }
            }
            return isdirected;
        }).collect(Collectors.toList());

        List<ExpansionElement> edgeList6 =
                routes.get(5).stream().filter((element) -> element instanceof ExpansionEdge)
                        .collect(Collectors.toList());

        // 5 directed edges
        Assert.assertEquals(5, directedList6.size());

        // All elements should be in both lists
        directedList6.forEach(edge -> Assert.assertTrue(edgeList6.contains(edge)));

        // Assert that there's no edges pointing to the left
        Assert.assertEquals(0, directedList6.stream().filter((edge) -> ((ExpansionEdge) edge).isToLeft()).count());
        // Assert that there's only 5 edges pointing to the right
        Assert.assertEquals(5, directedList6.stream().filter((edge) -> ((ExpansionEdge) edge).isToRight()).count());

        // SEVENTH POSSIBILITY:
        List<ExpansionElement> directedList7 = routes.get(6).stream().filter((element) -> {
            boolean isdirected = false;
            if (element instanceof ExpansionEdge) {
                if (((ExpansionEdge) element).isDirected()) {
                    isdirected = true;
                }
            }
            return isdirected;
        }).collect(Collectors.toList());

        List<ExpansionElement> edgeList7 =
                routes.get(6).stream().filter((element) -> element instanceof ExpansionEdge)
                        .collect(Collectors.toList());

        // 5 directed edges
        Assert.assertEquals(5, directedList7.size());

        // All elements should be in both lists
        directedList7.forEach(edge -> Assert.assertTrue(edgeList7.contains(edge)));

        // Assert that there's no edges pointing to the left
        Assert.assertEquals(0, directedList7.stream().filter((edge) -> ((ExpansionEdge) edge).isToLeft()).count());
        // Assert that there's only 5 edges pointing to the right
        Assert.assertEquals(5, directedList7.stream().filter((edge) -> ((ExpansionEdge) edge).isToRight()).count());

        // EIGHTH POSSIBILITY:
        List<ExpansionElement> directedList8 = routes.get(7).stream().filter((element) -> {
            boolean isdirected = false;
            if (element instanceof ExpansionEdge) {
                if (((ExpansionEdge) element).isDirected()) {
                    isdirected = true;
                }
            }
            return isdirected;
        }).collect(Collectors.toList());

        List<ExpansionElement> edgeList8 =
                routes.get(7).stream().filter((element) -> element instanceof ExpansionEdge)
                        .collect(Collectors.toList());

        // 6 directed edges
        Assert.assertEquals(6, directedList8.size());

        // All elements should be in both lists
        directedList8.forEach(edge -> Assert.assertTrue(edgeList8.contains(edge)));

        // Assert that there's no edges pointing to the left
        Assert.assertEquals(0, directedList8.stream().filter((edge) -> ((ExpansionEdge) edge).isToLeft()).count());
        // Assert that there's only 6 edges pointing to the right
        Assert.assertEquals(6, directedList8.stream().filter((edge) -> ((ExpansionEdge) edge).isToRight()).count());
    }

    @Test
    public void AnnonymousNodeWithOneWildCardAndUndirected() {

        String cypherInput = "()-[:knows*1..3]->()--( )";

        PathFinder matcher = new PathFinder(new CypherConverter());

        CanonicalRoutes paths = matcher.generateCanonicalRoutesFromInput(cypherInput);

        List<List<ExpansionElement>> routes = paths.getAllPossibleRoutes();

        Assert.assertEquals(3, routes.size());
        Assert.assertEquals(5, routes.get(0).size());
        Assert.assertEquals(7, routes.get(1).size());
        Assert.assertEquals(9, routes.get(2).size());

        // test first possibility:
        List<ExpansionElement> directedList = routes.get(0).stream().filter((element) -> {
            boolean isdirected = false;
            if (element instanceof ExpansionEdge) {
                if (((ExpansionEdge) element).isDirected()) {
                    isdirected = true;
                }
            }
            return isdirected;
        }).collect(Collectors.toList());

        List<ExpansionElement> edgeList =
                routes.get(0).stream().filter((element) -> element instanceof ExpansionEdge)
                        .collect(Collectors.toList());

        // one directed edges
        Assert.assertEquals(1, directedList.size());

        // All elements should be in both lists
        directedList.forEach(edge -> Assert.assertTrue(edgeList.contains(edge)));

        // Test second possibility
        List<ExpansionElement> directedList2 = routes.get(1).stream().filter((element) -> {
            boolean isdirected = false;
            if (element instanceof ExpansionEdge) {
                if (((ExpansionEdge) element).isDirected()) {
                    isdirected = true;
                }
            }
            return isdirected;
        }).collect(Collectors.toList());

        List<ExpansionElement> edgeList2 =
                routes.get(1).stream().filter((element) -> element instanceof ExpansionEdge)
                        .collect(Collectors.toList());

        // one directed edges
        Assert.assertEquals(2, directedList2.size());

        // All elements should be in both lists
        directedList2.forEach(edge -> Assert.assertTrue(edgeList2.contains(edge)));

        // Test Third possibility
        List<ExpansionElement> directedList3 = routes.get(2).stream().filter((element) -> {
            boolean isdirected = false;
            if (element instanceof ExpansionEdge) {
                if (((ExpansionEdge) element).isDirected()) {
                    isdirected = true;
                }
            }
            return isdirected;
        }).collect(Collectors.toList());

        List<ExpansionElement> edgeList3 =
                routes.get(2).stream().filter((element) -> element instanceof ExpansionEdge)
                        .collect(Collectors.toList());

        // one directed edges
        Assert.assertEquals(3, directedList3.size());

        // All elements should be in both lists
        directedList3.forEach(edge -> Assert.assertTrue(edgeList3.contains(edge)));
    }

    @Test
    public void AnnonymousNodeWithOneWildCard() {

        String cypherInput = "()-[:knows*1..3]->()";

        PathFinder matcher = new PathFinder(new CypherConverter());

        CanonicalRoutes paths = matcher.generateCanonicalRoutesFromInput(cypherInput);

        List<List<ExpansionElement>> routes = paths.getAllPossibleRoutes();

        Assert.assertEquals(3, routes.size());
        Assert.assertEquals(3, routes.get(0).size());
        Assert.assertEquals(5, routes.get(1).size());
        Assert.assertEquals(7, routes.get(2).size());

        // test first possibility:
        List<ExpansionElement> directedList = routes.get(0).stream().filter((element) -> {
            boolean isdirected = false;
            if (element instanceof ExpansionEdge) {
                if (((ExpansionEdge) element).isDirected()) {
                    isdirected = true;
                }
            }
            return isdirected;
        }).collect(Collectors.toList());

        List<ExpansionElement> edgeList =
                routes.get(0).stream().filter((element) -> element instanceof ExpansionEdge)
                        .collect(Collectors.toList());

        // one directed edges
        Assert.assertEquals(1, directedList.size());

        // All elements should be in both lists
        directedList.forEach(edge -> Assert.assertTrue(edgeList.contains(edge)));

        // Test second possibility
        List<ExpansionElement> directedList2 = routes.get(1).stream().filter((element) -> {
            boolean isdirected = false;
            if (element instanceof ExpansionEdge) {
                if (((ExpansionEdge) element).isDirected()) {
                    isdirected = true;
                }
            }
            return isdirected;
        }).collect(Collectors.toList());

        List<ExpansionElement> edgeList2 =
                routes.get(1).stream().filter((element) -> element instanceof ExpansionEdge)
                        .collect(Collectors.toList());

        // one directed edges
        Assert.assertEquals(2, directedList2.size());

        // All elements should be in both lists
        directedList2.forEach(edge -> Assert.assertTrue(edgeList2.contains(edge)));

        // Test Third possibility
        List<ExpansionElement> directedList3 = routes.get(2).stream().filter((element) -> {
            boolean isdirected = false;
            if (element instanceof ExpansionEdge) {
                if (((ExpansionEdge) element).isDirected()) {
                    isdirected = true;
                }
            }
            return isdirected;
        }).collect(Collectors.toList());

        List<ExpansionElement> edgeList3 =
                routes.get(2).stream().filter((element) -> element instanceof ExpansionEdge)
                        .collect(Collectors.toList());

        // one directed edges
        Assert.assertEquals(3, directedList3.size());

        // All elements should be in both lists
        directedList3.forEach(edge -> Assert.assertTrue(edgeList3.contains(edge)));
    }
}
