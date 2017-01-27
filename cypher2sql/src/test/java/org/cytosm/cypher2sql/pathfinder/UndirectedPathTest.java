package org.cytosm.pathfinder;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.cytosm.cypher2sql.expandpaths.CypherConverter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.cytosm.common.gtop.GTopInterfaceImpl;
import org.cytosm.common.gtop.RelationalGTopInterface;
import org.cytosm.pathfinder.routeelements.ExpansionElement;

public class UndirectedPathTest extends BasePathTest {

    GTopInterfaceImpl gTopInter;

    @Before
    public void initTest() {
        // load gtop
        String path = "src" + File.separatorChar + "test" + File.separatorChar + "resources" + File.separatorChar;

        try {
            gTopInter = new RelationalGTopInterface(new File(path + "northwindUndirect.gtop"));
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Test
    public void pairOfNodesWithoutVariables() {

        String cypherInput = "()--()";

        PathFinder matcher = new PathFinder(new CypherConverter());

        CanonicalRoutes paths = matcher.enumerate(cypherInput, gTopInter);

        List<List<ExpansionElement>> routes = paths.getAllPossibleRoutes();

        // matches all the number of matches
        Assert.assertEquals(5, routes.size());

        // All sequences will have exactly 3 elements
        Assert.assertTrue(routes.stream().allMatch(list -> list.size() == 3));

        List<String> route1 = new ArrayList<>();
        route1.addAll(Arrays.asList("employees", "sold", "orders"));

        List<String> route3 = new ArrayList<>();
        route3.addAll(Arrays.asList("customers", "purchased", "orders"));

        List<String> route4 = new ArrayList<>();
        route4.addAll(Arrays.asList("products", "part_of", "categories"));

        List<String> route5 = new ArrayList<>();
        route5.addAll(Arrays.asList("employees", "reports_to", "employees"));

        List<String> route6 = new ArrayList<>();
        route6.addAll(Arrays.asList("orders", "product", "products"));

        List<List<String>> allPossibilities = new ArrayList<>();

        allPossibilities.add(route1);
        allPossibilities.add(route3);
        allPossibilities.add(route4);
        allPossibilities.add(route5);
        allPossibilities.add(route6);

        Assert.assertTrue(checkIfAllPossibilitiesMatchSymmetric(allPossibilities, routes));
    }

    @Test
    public void pairOfNodesWithVariables() {

        String cypherInput = "(n)--()";

        PathFinder matcher = new PathFinder(new CypherConverter());

        CanonicalRoutes paths = matcher.enumerate(cypherInput, gTopInter);

        List<List<ExpansionElement>> routes = paths.getAllPossibleRoutes();

        // matches all the number of matches
        Assert.assertEquals(9, routes.size());

        // All sequences will have exactly 3 elements
        Assert.assertTrue(routes.stream().allMatch(list -> list.size() == 3));

        List<String> route1 = new ArrayList<>();
        route1.addAll(Arrays.asList("employees", "sold", "orders"));

        List<String> route3 = new ArrayList<>();
        route3.addAll(Arrays.asList("customers", "purchased", "orders"));

        List<String> route4 = new ArrayList<>();
        route4.addAll(Arrays.asList("products", "part_of", "categories"));

        List<String> route5 = new ArrayList<>();
        route5.addAll(Arrays.asList("employees", "reports_to", "employees"));

        List<String> route6 = new ArrayList<>();
        route6.addAll(Arrays.asList("orders", "product", "products"));

        List<String> route7 = new ArrayList<>();
        route7.addAll(Arrays.asList("orders", "sold", "employees"));

        List<String> route9 = new ArrayList<>();
        route9.addAll(Arrays.asList("orders", "purchased", "customers"));

        List<String> route10 = new ArrayList<>();
        route10.addAll(Arrays.asList("categories", "part_of", "products"));

        List<String> route11 = new ArrayList<>();
        route11.addAll(Arrays.asList("products", "product", "orders"));

        List<List<String>> allPossibilities = new ArrayList<>();

        allPossibilities.add(route1);
        allPossibilities.add(route3);
        allPossibilities.add(route4);
        allPossibilities.add(route5);
        allPossibilities.add(route6);
        allPossibilities.add(route7);
        allPossibilities.add(route9);
        allPossibilities.add(route10);
        allPossibilities.add(route11);

        Assert.assertTrue(checkIfAllPossibilitiesMatchAsymmetric(allPossibilities, routes));

        // Check if variable is conserved:
        Assert.assertTrue(routes.stream().allMatch(route -> route.get(0).getVariable().equals("n")));
    }

    @Test
    public void pairOfNodesWithOneHint() {

        String cypherInput = "(:Employees)--()";

        PathFinder matcher = new PathFinder(new CypherConverter());

        CanonicalRoutes paths = matcher.enumerate(cypherInput, gTopInter);

        List<List<ExpansionElement>> routes = paths.getAllPossibleRoutes();

        // matches all the number of matches
        Assert.assertEquals(2, routes.size());

        // All sequences will have exactly 3 elements
        Assert.assertTrue(routes.stream().allMatch(list -> list.size() == 3));

        List<String> route1 = new ArrayList<>();
        route1.addAll(Arrays.asList("employees", "reports_to", "employees"));

        List<String> route3 = new ArrayList<>();
        route3.addAll(Arrays.asList("orders", "sold", "employees"));

        List<List<String>> allPossibilities = new ArrayList<>();

        allPossibilities.add(route1);
        allPossibilities.add(route3);

        Assert.assertTrue(checkIfAllPossibilitiesMatchAsymmetric(allPossibilities, routes));
    }

    @Test
    public void pairOfNodesWithTwoNodeHints() {

        String cypherInput = "(:Employees)--(:Employees)";

        PathFinder matcher = new PathFinder(new CypherConverter());

        CanonicalRoutes paths = matcher.enumerate(cypherInput, gTopInter);

        List<List<ExpansionElement>> routes = paths.getAllPossibleRoutes();

        // matches all the number of matches
        Assert.assertEquals(1, routes.size());

        // All sequences will have exactly 3 elements
        Assert.assertTrue(routes.stream().allMatch(list -> list.size() == 3));

        List<String> route1 = new ArrayList<>();
        route1.addAll(Arrays.asList("employees", "reports_to", "employees"));

        List<List<String>> allPossibilities = new ArrayList<>();

        allPossibilities.add(route1);

        Assert.assertTrue(checkIfAllPossibilitiesMatchAsymmetric(allPossibilities, routes));

    }

    @Test
    public void pairOfNodesWithOneNodeOneEdgeHint() {

        String cypherInput = "(:employees)-[:sold]-()";

        PathFinder matcher = new PathFinder(new CypherConverter());

        CanonicalRoutes paths = matcher.enumerate(cypherInput, gTopInter);

        List<List<ExpansionElement>> routes = paths.getAllPossibleRoutes();

        // matches all the number of matches
        Assert.assertEquals(1, routes.size());

        // All sequences will have exactly 3 elements
        Assert.assertTrue(routes.stream().allMatch(list -> list.size() == 3));


        List<String> route1 = new ArrayList<>();
        route1.addAll(Arrays.asList("employees", "sold", "orders"));

        List<List<String>> allPossibilities = new ArrayList<>();

        allPossibilities.add(route1);

        Assert.assertTrue(checkIfAllPossibilitiesMatchAsymmetric(allPossibilities, routes));
    }

    @Test
    public void pairOfNodesWithImpossibleNodeEdgeCombination() {

        String cypherInput = "(:Employees)-[:part_of]-()";

        PathFinder matcher = new PathFinder(new CypherConverter());

        CanonicalRoutes paths = matcher.enumerate(cypherInput, gTopInter);

        List<List<ExpansionElement>> routes = paths.getAllPossibleRoutes();

        // matches all the number of matches
        Assert.assertEquals(0, routes.size());

    }

    @Test
    public void pairOfNodesWithImpossibleNodeNodeCombination() {

        String cypherInput = "(:Employees)--(:Customers)";

        PathFinder matcher = new PathFinder(new CypherConverter());

        CanonicalRoutes paths = matcher.enumerate(cypherInput, gTopInter);

        List<List<ExpansionElement>> routes = paths.getAllPossibleRoutes();

        // matches all the number of matches
        Assert.assertEquals(0, routes.size());

    }



    /***
     * Checks if the sequence exists in the list, as (car)--(person) or (person)--(car)
     *
     * @param allPossibilities
     * @param routes
     * @return
     */
    private boolean checkIfAllPossibilitiesMatchSymmetric(final List<List<String>> allPossibilities,
            final List<List<ExpansionElement>> routes) {

        boolean matched = true;

        for (List<String> possibility : allPossibilities) {
            // if the list or the reverse of it is available, returns true.
            List<String> reversedPossibilites = possibility.stream().collect(Collectors.toList());
            Collections.reverse(reversedPossibilites);

            if (!containsRoute(possibility, routes) && !containsRoute(reversedPossibilites, routes)) {
                matched = false;
                break;
            }
        }


        return matched;
    }

    /***
     * Checks if the sequence exists in the list, as (car)--(person) will match (h:car)--(person)
     * only
     *
     * @param allPossibilities
     * @param routes
     * @return
     */
    private boolean checkIfAllPossibilitiesMatchAsymmetric(final List<List<String>> allPossibilities,
            final List<List<ExpansionElement>> routes) {

        boolean matched = true;

        for (List<String> possibility : allPossibilities) {

            if (!containsRoute(possibility, routes)) {
                matched = false;
                break;
            }
        }

        return matched;
    }


    @Test
    public void triplesOfNodesWithoutVariables() {

        String cypherInput = "()--()--()";

        PathFinder matcher = new PathFinder(new CypherConverter());

        CanonicalRoutes paths = matcher.enumerate(cypherInput, gTopInter);

        List<List<ExpansionElement>> routes = paths.getAllPossibleRoutes();

        // matches all the number of matches (listed each one in @PathSerializerTest).
        Assert.assertEquals(14, routes.size());

        // All sequences will have exactly 5 elements
        Assert.assertTrue(routes.stream().allMatch(list -> list.size() == 5));
    }

    @Test
    public void wildCardExpansion() {

        String cypherInput = "(:Employees)-[*1..2]-()";

        PathFinder matcher = new PathFinder(new CypherConverter());

        CanonicalRoutes paths = matcher.enumerate(cypherInput, gTopInter);

        List<List<ExpansionElement>> routes = paths.getAllPossibleRoutes();

        // Two single hop possibilities
        Assert.assertEquals(2, routes.stream().filter(list -> list.size() == 3).count());

        // four two hop possibilities
        Assert.assertEquals(5, routes.stream().filter(list -> list.size() == 5).count());

        // All the possible number of matches
        Assert.assertEquals(7, routes.size());

    }

    @Test
    public void anonymousReportToStar2to2() {

        String cypherInput = "()-[:REPORTS_TO*2..2]-()";

        PathFinder matcher = new PathFinder(new CypherConverter());

        CanonicalRoutes paths = matcher.enumerate(cypherInput, gTopInter);

        List<List<ExpansionElement>> routes = paths.getAllPossibleRoutes();

        // one two hop possibilities
        Assert.assertEquals(1, routes.stream().filter(list -> list.size() == 5).count());

        // All the possible number of matches
        Assert.assertEquals(1, routes.size());

        List<String> route1 = new ArrayList<>();
        route1.addAll(Arrays.asList("employees", "reports_to", "employees", "reports_to", "employees"));

        List<List<String>> allPossibilities = new ArrayList<>();

        allPossibilities.add(route1);

        Assert.assertTrue(checkIfAllPossibilitiesMatchAsymmetric(allPossibilities, routes));
    }
}
