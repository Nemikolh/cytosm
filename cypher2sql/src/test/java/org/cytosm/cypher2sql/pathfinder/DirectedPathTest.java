package org.cytosm.pathfinder;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.cytosm.cypher2sql.expandpaths.CypherConverter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.cytosm.common.gtop.GTopInterfaceImpl;
import org.cytosm.common.gtop.RelationalGTopInterface;
import org.cytosm.pathfinder.routeelements.ExpansionElement;

public class DirectedPathTest extends BasePathTest {

    GTopInterfaceImpl gTopInter;

    @Before
    public void initTest() {
        // load gtop
        String path = "src" + File.separatorChar + "test" + File.separatorChar + "resources" + File.separatorChar;


        // special Gtop with a directed connection between Orders and Products
        try {
            gTopInter = new RelationalGTopInterface(new File(path + "northwindDirect.gtop"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void pairOfNodesWithoutVariables() {

        String cypherInput = "()-->()";

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

        Assert.assertTrue(checkIfAllPossibilitiesMatch(allPossibilities, routes));
    }

    @Test
    public void pairOfNodesWithoutVariablesReverse() {

        String cypherInput = "()<--()";

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

        Assert.assertTrue(checkIfAllPossibilitiesMatch(allPossibilities, routes));
    }

    @Test
    public void pairOfNodesWithVariables() {

        String cypherInput = "(n)-->()";

        PathFinder matcher = new PathFinder(new CypherConverter());

        CanonicalRoutes paths = matcher.enumerate(cypherInput, gTopInter);

        List<List<ExpansionElement>> routes = paths.getAllPossibleRoutes();

        // matches all the number of matches
        Assert.assertEquals(5, routes.size());

        // All sequences will have exactly 3 elements
        Assert.assertTrue(routes.stream().allMatch(list -> list.size() == 3));

        List<String> route1 = new ArrayList<>();
        route1.addAll(Arrays.asList("employees", "sold", "orders"));

        List<String> route2 = new ArrayList<>();
        route2.addAll(Arrays.asList("customers", "purchased", "orders"));

        List<String> route3 = new ArrayList<>();
        route3.addAll(Arrays.asList("products", "part_of", "categories"));

        List<String> route4 = new ArrayList<>();
        route4.addAll(Arrays.asList("employees", "reports_to", "employees"));

        List<String> route5 = new ArrayList<>();
        route5.addAll(Arrays.asList("orders", "product", "products"));


        List<List<String>> allPossibilities = new ArrayList<>();

        allPossibilities.add(route1);
        allPossibilities.add(route2);
        allPossibilities.add(route3);
        allPossibilities.add(route4);
        allPossibilities.add(route5);


        Assert.assertTrue(checkIfAllPossibilitiesMatch(allPossibilities, routes));

        // Check if variable is conserved:
        Assert.assertTrue(routes.stream().allMatch(route -> route.get(0).getVariable().equals("n")));
    }

    @Test
    public void pairOfNodesWithVariablesReverse() {

        String cypherInput = "(n)<--()";

        PathFinder matcher = new PathFinder(new CypherConverter());

        CanonicalRoutes paths = matcher.enumerate(cypherInput, gTopInter);

        List<List<ExpansionElement>> routes = paths.getAllPossibleRoutes();

        // matches all the number of matches
        Assert.assertEquals(5, routes.size());

        // All sequences will have exactly 3 elements
        Assert.assertTrue(routes.stream().allMatch(list -> list.size() == 3));

        List<String> route1 = new ArrayList<>();
        route1.addAll(Arrays.asList("employees", "sold", "orders"));

        List<String> route2 = new ArrayList<>();
        route2.addAll(Arrays.asList("customers", "purchased", "orders"));

        List<String> route3 = new ArrayList<>();
        route3.addAll(Arrays.asList("products", "part_of", "categories"));

        List<String> route4 = new ArrayList<>();
        route4.addAll(Arrays.asList("employees", "reports_to", "employees"));

        List<String> route5 = new ArrayList<>();
        route5.addAll(Arrays.asList("orders", "product", "products"));

        List<List<String>> allPossibilities = new ArrayList<>();

        allPossibilities.add(route1);
        allPossibilities.add(route2);
        allPossibilities.add(route3);
        allPossibilities.add(route4);
        allPossibilities.add(route5);


        Assert.assertTrue(checkIfAllPossibilitiesMatch(allPossibilities, routes));

        // Check if variable is conserved:
        Assert.assertTrue(routes.stream().allMatch(route -> route.get(0).getVariable().equals("n")));
    }

    @Test
    public void pairOfNodesWithOneHint() {

        // expecting route to employees & orders
        String cypherInput = "(:Employees)-->()";

        PathFinder matcher = new PathFinder(new CypherConverter());

        CanonicalRoutes paths = matcher.enumerate(cypherInput, gTopInter);

        List<List<ExpansionElement>> routes = paths.getAllPossibleRoutes();

        // matches all the number of matches
        Assert.assertEquals(2, routes.size());

        // All sequences will have exactly 3 elements
        Assert.assertTrue(routes.stream().allMatch(list -> list.size() == 3));

        List<String> route1 = new ArrayList<>();
        route1.addAll(Arrays.asList("employees", "reports_to", "employees"));

        List<String> route2 = new ArrayList<>();
        route2.addAll(Arrays.asList("employees", "sold", "orders"));

        List<List<String>> allPossibilities = new ArrayList<>();

        allPossibilities.add(route1);
        allPossibilities.add(route2);

        Assert.assertTrue(checkIfAllPossibilitiesMatch(allPossibilities, routes));
    }

    @Test
    public void pairOfNodesWithOneHintReverse() {

        // expecting route to employees & orders
        String cypherInput = "(:Employees)<--()";

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

        Assert.assertTrue(checkIfAllPossibilitiesMatch(allPossibilities, routes));
    }

    @Test
    public void pairOfNodesWithTwoNodeHints() {

        String cypherInput = "(:Employees)-->(:Employees)";

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

        Assert.assertTrue(checkIfAllPossibilitiesMatch(allPossibilities, routes));

    }

    @Test
    public void pairOfNodesWithTwoNodeHintsReverse() {

        String cypherInput = "(:Employees)<--(:Employees)";

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

        Assert.assertTrue(checkIfAllPossibilitiesMatch(allPossibilities, routes));

    }

    @Test
    public void pairOfNodesWithOneNodeOneEdgeHint() {

        String cypherInput = "(:employees)-[:sold]->()";

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

        Assert.assertTrue(checkIfAllPossibilitiesMatch(allPossibilities, routes));
    }

    @Test
    public void pairOfNodesWithOneNodeOneEdgeHintReverse() {

        String cypherInput = "(:employees)<-[:sold]-()";

        PathFinder matcher = new PathFinder(new CypherConverter());

        CanonicalRoutes paths = matcher.enumerate(cypherInput, gTopInter);

        List<List<ExpansionElement>> routes = paths.getAllPossibleRoutes();

        // matches all the number of matches
        Assert.assertEquals(0, routes.size());
    }

    @Test
    public void pairOfNodesWithImpossibleNodeEdgeCombination() {

        String cypherInput = "(:Employees)-[:part_of]->()";

        PathFinder matcher = new PathFinder(new CypherConverter());

        CanonicalRoutes paths = matcher.enumerate(cypherInput, gTopInter);

        List<List<ExpansionElement>> routes = paths.getAllPossibleRoutes();

        // matches all the number of matches
        Assert.assertEquals(0, routes.size());

    }

    @Test
    public void pairOfNodesWithImpossibleNodeEdgeCombinationReverse() {

        String cypherInput = "(:Employees)<-[:part_of]-()";

        PathFinder matcher = new PathFinder(new CypherConverter());

        CanonicalRoutes paths = matcher.enumerate(cypherInput, gTopInter);

        List<List<ExpansionElement>> routes = paths.getAllPossibleRoutes();

        // matches all the number of matches
        Assert.assertEquals(0, routes.size());

    }

    @Test
    public void pairOfNodesWithImpossibleNodeNodeCombination() {

        String cypherInput = "(:Employees)-->(:Customers)";

        PathFinder matcher = new PathFinder(new CypherConverter());

        CanonicalRoutes paths = matcher.enumerate(cypherInput, gTopInter);

        List<List<ExpansionElement>> routes = paths.getAllPossibleRoutes();

        // matches all the number of matches
        Assert.assertEquals(0, routes.size());

    }

    @Test
    public void pairOfNodesWithImpossibleNodeNodeCombinationReverse() {

        String cypherInput = "(:Employees)<--(:Customers)";

        PathFinder matcher = new PathFinder(new CypherConverter());

        CanonicalRoutes paths = matcher.enumerate(cypherInput, gTopInter);

        List<List<ExpansionElement>> routes = paths.getAllPossibleRoutes();

        // matches all the number of matches
        Assert.assertEquals(0, routes.size());

    }


    @Test
    public void triplesOfNodesWithoutVariables() {

        String cypherInput = "()-->()-->()";

        PathFinder matcher = new PathFinder(new CypherConverter());

        CanonicalRoutes paths = matcher.enumerate(cypherInput, gTopInter);

        List<List<ExpansionElement>> routes = paths.getAllPossibleRoutes();

        // matches all the number of matches (listed each one in @PathSerializerTest).
        Assert.assertEquals(5, routes.size());

        // All sequences will have exactly 5 elements
        Assert.assertTrue(routes.stream().allMatch(list -> list.size() == 5));
    }

    @Test
    public void triplesOfNodesWithoutVariablesReversed() {

        String cypherInput = "()<--()<--()";

        PathFinder matcher = new PathFinder(new CypherConverter());

        CanonicalRoutes paths = matcher.enumerate(cypherInput, gTopInter);

        List<List<ExpansionElement>> routes = paths.getAllPossibleRoutes();

        // matches all the number of matches (listed each one in @PathSerializerTest).
        Assert.assertEquals(5, routes.size());

        // All sequences will have exactly 5 elements
        Assert.assertTrue(routes.stream().allMatch(list -> list.size() == 5));
    }

    @Test
    public void triplesOfAnonNodesWithSharedMiddleInbound() {

        String cypherInput = "()-->()<--()";

        PathFinder matcher = new PathFinder(new CypherConverter());

        CanonicalRoutes paths = matcher.enumerate(cypherInput, gTopInter);

        List<List<ExpansionElement>> routes = paths.getAllPossibleRoutes();

        Assert.assertEquals(6, routes.size());

        List<String> route1 = new ArrayList<>();
        route1.addAll(Arrays.asList("customers", "purchased", "orders", "purchased", "customers"));
        List<String> route2 = new ArrayList<>();
        route2.addAll(Arrays.asList("customers", "purchased", "orders", "sold", "employees"));
        List<String> route3 = new ArrayList<>();
        route3.addAll(Arrays.asList("employees", "reports_to", "employees", "reports_to", "employees"));
        List<String> route4 = new ArrayList<>();
        route4.addAll(Arrays.asList("products", "part_of", "categories", "part_of", "products"));
        List<String> route5 = new ArrayList<>();
        route5.addAll(Arrays.asList("orders", "product", "products", "product", "orders"));
        List<String> route6 = new ArrayList<>();
        route6.addAll(Arrays.asList("employees", "sold", "orders", "sold", "employees"));

        List<List<String>> allPossibilities = new ArrayList<>();

        allPossibilities.add(route1);
        allPossibilities.add(route2);
        allPossibilities.add(route3);
        allPossibilities.add(route4);
        allPossibilities.add(route5);
        allPossibilities.add(route6);

        Assert.assertTrue(checkIfAllPossibilitiesMatch(allPossibilities, routes));

        // All sequences will have exactly 5 elements
        Assert.assertTrue(routes.stream().allMatch(list -> list.size() == 5));
    }

    @Test
    public void triplesOfAnonNodesWithSharedMiddleOutbound() {

        String cypherInput = "()<--()-->()";

        PathFinder matcher = new PathFinder(new CypherConverter());

        CanonicalRoutes paths = matcher.enumerate(cypherInput, gTopInter);

        List<List<ExpansionElement>> routes = paths.getAllPossibleRoutes();
        // matches all the number of matches (listed each one in @PathSerializerTest).
        Assert.assertEquals(6, routes.size());

        List<String> route1 = new ArrayList<>();
        route1.addAll(Arrays.asList("employees", "reports_to", "employees", "reports_to", "employees"));
        List<String> route2 = new ArrayList<>();
        route2.addAll(Arrays.asList("employees", "reports_to", "employees", "sold", "orders"));
        List<String> route3 = new ArrayList<>();
        route3.addAll(Arrays.asList("orders", "sold", "employees", "sold", "orders"));
        List<String> route4 = new ArrayList<>();
        route4.addAll(Arrays.asList("products", "product", "orders", "product", "products"));
        List<String> route5 = new ArrayList<>();
        route5.addAll(Arrays.asList("categories", "part_of", "products", "part_of", "categories"));
        List<String> route6 = new ArrayList<>();
        route6.addAll(Arrays.asList("orders", "purchased", "customers", "purchased", "orders"));

        List<List<String>> allPossibilities = new ArrayList<>();

        allPossibilities.add(route1);
        allPossibilities.add(route2);
        allPossibilities.add(route3);
        allPossibilities.add(route4);
        allPossibilities.add(route5);
        allPossibilities.add(route6);

        Assert.assertTrue(checkIfAllPossibilitiesMatch(allPossibilities, routes));

        // All sequences will have exactly 5 elements
        Assert.assertTrue(routes.stream().allMatch(list -> list.size() == 5));
    }

    @Test
    public void triplesOfNodesWithSharedMiddleOutbound() {

        String cypherInput = "()<--(:Orders)-->()";

        PathFinder matcher = new PathFinder(new CypherConverter());

        CanonicalRoutes paths = matcher.enumerate(cypherInput, gTopInter);

        List<List<ExpansionElement>> routes = paths.getAllPossibleRoutes();

        // All sequences will have exactly 5 elements
        Assert.assertTrue(routes.stream().allMatch(list -> list.size() == 5));

        List<String> route1 = new ArrayList<>();
        route1.addAll(Arrays.asList("products", "product", "orders", "product", "products"));

        List<List<String>> allPossibilities = new ArrayList<>();

        allPossibilities.add(route1);

        Assert.assertTrue(checkIfAllPossibilitiesMatch(allPossibilities, routes));
    }

    @Test
    public void triplesOfNodesWithSharedMiddleInbound() {

        String cypherInput = "()-->(:Orders)<--()";

        PathFinder matcher = new PathFinder(new CypherConverter());

        CanonicalRoutes paths = matcher.enumerate(cypherInput, gTopInter);

        List<List<ExpansionElement>> routes = paths.getAllPossibleRoutes();

        // matches all the number of matches (listed each one in @PathSerializerTest).
        Assert.assertEquals(3, routes.size());

        // All sequences will have exactly 5 elements
        Assert.assertTrue(routes.stream().allMatch(list -> list.size() == 5));

        List<String> route1 = new ArrayList<>();
        route1.addAll(Arrays.asList("employees", "sold", "orders", "purchased", "customers"));

        List<String> route2 = new ArrayList<>();
        route2.addAll(Arrays.asList("employees", "sold", "orders", "sold", "employees"));

        List<String> route3 = new ArrayList<>();
        route3.addAll(Arrays.asList("customers", "purchased", "orders", "purchased", "customers"));

        List<List<String>> allPossibilities = new ArrayList<>();

        allPossibilities.add(route1);
        allPossibilities.add(route2);
        allPossibilities.add(route3);

        Assert.assertTrue(checkIfAllPossibilitiesMatch(allPossibilities, routes));
    }



    @Test
    public void wildCardExpansion() {

        String cypherInput = "(:Employees)-[*1..2]->()";

        PathFinder matcher = new PathFinder(new CypherConverter());

        CanonicalRoutes paths = matcher.enumerate(cypherInput, gTopInter);

        List<List<ExpansionElement>> routes = paths.getAllPossibleRoutes();

        // Two single hop possibilities
        Assert.assertEquals(2, routes.stream().filter(list -> list.size() == 3).count());

        // four two hop possibilities
        Assert.assertEquals(3, routes.stream().filter(list -> list.size() == 5).count());

        // All the possible number of matches
        Assert.assertEquals(5, routes.size());

    }


    @Test
    public void wildCardExpansionReverse() {

        String cypherInput = "(:Employees)<-[*1..2]-()";

        PathFinder matcher = new PathFinder(new CypherConverter());

        CanonicalRoutes paths = matcher.enumerate(cypherInput, gTopInter);

        List<List<ExpansionElement>> routes = paths.getAllPossibleRoutes();

        // Two single hop possibilities
        Assert.assertEquals(1, routes.stream().filter(list -> list.size() == 3).count());

        // four two hop possibilities
        Assert.assertEquals(1, routes.stream().filter(list -> list.size() == 5).count());

        // All the possible number of matches
        Assert.assertEquals(2, routes.size());

    }

    @Test
    public void anonymousReportToStar2to2() {

        String cypherInput = "()-[:REPORTS_TO*2..2]->()";

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

        Assert.assertTrue(checkIfAllPossibilitiesMatch(allPossibilities, routes));
    }

    @Test
    public void anonymousReportToStar2to2Reverse() {

        String cypherInput = "()<-[:REPORTS_TO*2..2]-()";

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

        Assert.assertTrue(checkIfAllPossibilitiesMatch(allPossibilities, routes));
    }

    @Test
    public void fourHopTest() {

        String cypherInput = "(:Customers)-->(:Orders)-->(:Products)-->(:Categories)";

        PathFinder matcher = new PathFinder(new CypherConverter());

        CanonicalRoutes paths = matcher.enumerate(cypherInput, gTopInter);

        List<List<ExpansionElement>> routes = paths.getAllPossibleRoutes();

        // matches all the number of matches
        Assert.assertEquals(1, routes.size());
    }

    @Test
    public void pairWithProperties() {

        // expecting route to employees & orders
        String cypherInput = "(:Employees { firstName: 'Keanu Reeves' })-->()";

        PathFinder matcher = new PathFinder(new CypherConverter());

        CanonicalRoutes paths = matcher.enumerate(cypherInput, gTopInter);

        List<List<ExpansionElement>> routes = paths.getAllPossibleRoutes();

        // matches all the number of matches
        Assert.assertEquals(2, routes.size());

        // All sequences will have exactly 3 elements
        Assert.assertTrue(routes.stream().allMatch(list -> list.size() == 3));

        List<String> route1 = new ArrayList<>();
        route1.addAll(Arrays.asList("employees", "reports_to", "employees"));

        List<String> route2 = new ArrayList<>();
        route2.addAll(Arrays.asList("employees", "sold", "orders"));

        List<List<String>> allPossibilities = new ArrayList<>();

        allPossibilities.add(route1);
        allPossibilities.add(route2);

        Assert.assertTrue(checkIfAllPossibilitiesMatch(allPossibilities, routes));
    }

    @Test
    public void anoymousToNode() {

        // expecting route to employees
        String cypherInput = "(n) --> (Employees { firstName: 'Keanu Reeves' })";

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

        Assert.assertTrue(checkIfAllPossibilitiesMatch(allPossibilities, routes));
    }

    @Test
    public void anonymousToOrders() {

        String cypherInput = "(n) --> (:orders)";

        PathFinder matcher = new PathFinder(new CypherConverter());

        CanonicalRoutes paths = matcher.enumerate(cypherInput, gTopInter);

        List<List<ExpansionElement>> routes = paths.getAllPossibleRoutes();

        // matches all the number of matches
        Assert.assertEquals(2, routes.size());

        List<String> route1 = new ArrayList<>();
        route1.addAll(Arrays.asList("customers", "purchased", "orders"));
        List<String> route2 = new ArrayList<>();
        route2.addAll(Arrays.asList("employees", "sold", "orders"));
        List<List<String>> allPossibilities = new ArrayList<>();

        allPossibilities.add(route1);
        allPossibilities.add(route2);

        Assert.assertTrue(checkIfAllPossibilitiesMatch(allPossibilities, routes));
    }


    @Test
    public void anonymousToOrdersWithAttributes() {

        String cypherInput = "(n) --> (:Orders {ShipName: \"Island Trading\"})";

        PathFinder matcher = new PathFinder(new CypherConverter());

        CanonicalRoutes paths = matcher.enumerate(cypherInput, gTopInter);

        List<List<ExpansionElement>> routes = paths.getAllPossibleRoutes();

        // matches all the number of matches
        Assert.assertEquals(2, routes.size());

        List<String> route1 = new ArrayList<>();
        route1.addAll(Arrays.asList("customers", "purchased", "orders"));
        List<String> route2 = new ArrayList<>();
        route2.addAll(Arrays.asList("employees", "sold", "orders"));
        List<List<String>> allPossibilities = new ArrayList<>();

        allPossibilities.add(route1);
        allPossibilities.add(route2);

        Assert.assertTrue(checkIfAllPossibilitiesMatch(allPossibilities, routes));
    }



}
