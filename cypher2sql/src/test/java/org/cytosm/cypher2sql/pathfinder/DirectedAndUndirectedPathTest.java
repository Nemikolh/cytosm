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

public class DirectedAndUndirectedPathTest extends BasePathTest {

    GTopInterfaceImpl gTopInter;

    @Before
    public void initTest() {
        // load gtop
        String path = "src" + File.separatorChar + "test" + File.separatorChar + "resources" + File.separatorChar;

        // special Gtop with a undirected connection between Orders and Products
        try {
            gTopInter = new RelationalGTopInterface(new File(path + "northwindDirectAndUndirect.gtop"));
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Test
    public void mixtureOfDirectedAndUndirectedWithHints() {

        String cypherInput = "(:Customers)-->(:Orders)--(:Products)-->(:Categories)";

        PathFinder matcher = new PathFinder(new CypherConverter());

        CanonicalRoutes paths = matcher.enumerate(cypherInput, gTopInter);

        List<List<ExpansionElement>> routes = paths.getAllPossibleRoutes();


        // matches all the number of matches
        Assert.assertEquals(1, routes.size());

        List<String> route1 = new ArrayList<>();
        route1.addAll(Arrays.asList("customers", "purchased", "orders", "product", "products", "part_of", "categories"));

        List<List<String>> allPossibilities = new ArrayList<>();

        allPossibilities.add(route1);

        Assert.assertTrue(checkIfAllPossibilitiesMatch(allPossibilities, routes));
    }

    @Test
    public void mixtureOfDirectedAndUndirectedWithSomeHints() {

        String cypherInput = "(:Customers)-->()--()-->()";

        PathFinder matcher = new PathFinder(new CypherConverter());

        CanonicalRoutes paths = matcher.enumerate(cypherInput, gTopInter);

        List<List<ExpansionElement>> routes = paths.getAllPossibleRoutes();

        // for (List<ExpansionElement> list : routes) {
        // StringBuilder sb = new StringBuilder();
        // for (ExpansionElement expansionElement : list) {
        // if (expansionElement.getEquivalentMaterializedGsch() instanceof AbstractionNode) {
        // sb.append("(" + ((AbstractionNode)
        // expansionElement.getEquivalentMaterializedGsch()).getSynonyms()
        // + ")");
        // } else {
        // sb.append(((AbstractionEdge)
        // expansionElement.getEquivalentMaterializedGsch()).getSynonyms());
        // }
        // sb.append(" - ");
        // }
        // System.out.println(sb.toString());
        // }

        // matches all the number of matches
        Assert.assertEquals(4, routes.size());

        List<List<String>> allPossibilities = new ArrayList<>();

        allPossibilities.add(Arrays.asList("customers", "purchased", "orders", "product", "products", "part_of", "categories"));
        allPossibilities.add(Arrays.asList("customers", "purchased", "orders", "sold", "employees", "reports_to", "employees"));
        allPossibilities.add(Arrays.asList("customers", "purchased", "orders", "purchased", "customers", "purchased", "orders"));
        allPossibilities.add(Arrays.asList("customers", "purchased", "orders", "sold", "employees", "sold", "orders"));

        Assert.assertTrue(checkIfAllPossibilitiesMatch(allPossibilities, routes));
    }

    @Test
    public void mixtureOfDirectedAndUndirected() {

        String cypherInput = "()-->()--()-->()";

        PathFinder matcher = new PathFinder(new CypherConverter());

        CanonicalRoutes paths = matcher.enumerate(cypherInput, gTopInter);

        List<List<ExpansionElement>> routes = paths.getAllPossibleRoutes();
        // matches all the number of matches
        Assert.assertEquals(11, routes.size());

        List<List<String>> allPossibilities = new ArrayList<>();
        allPossibilities.add(Arrays.asList("employees", "sold", "orders", "sold", "employees", "sold", "orders"));
        allPossibilities.add(Arrays.asList("employees", "sold", "orders", "purchased", "customers", "purchased", "orders"));
        allPossibilities.add(Arrays.asList("employees", "reports_to", "employees", "reports_to", "employees", "reports_to", "employees"));
        allPossibilities.add(Arrays.asList("customers", "purchased", "orders", "product", "products", "part_of", "categories"));
        allPossibilities.add(Arrays.asList("employees", "reports_to", "employees", "reports_to", "employees", "sold", "orders"));
        allPossibilities.add(Arrays.asList("customers", "purchased", "orders", "sold", "employees", "reports_to", "employees"));
        allPossibilities.add(Arrays.asList("customers", "purchased", "orders", "purchased", "customers", "purchased", "orders"));
        allPossibilities.add(Arrays.asList("employees", "sold", "orders", "product", "products", "part_of", "categories"));
        allPossibilities.add(Arrays.asList("products", "part_of", "categories", "part_of", "products", "part_of", "categories"));
        allPossibilities.add(Arrays.asList("customers", "purchased", "orders", "sold", "employees", "sold", "orders"));
        allPossibilities.add(Arrays.asList("employees", "sold", "orders", "sold", "employees", "reports_to", "employees"));

        Assert.assertTrue(checkIfAllPossibilitiesMatch(allPossibilities, routes));

    }
}
