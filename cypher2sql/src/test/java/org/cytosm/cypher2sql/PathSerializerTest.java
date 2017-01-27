package org.cytosm.cypher2sql;


import java.io.File;
import java.io.IOException;
import java.util.List;

import org.cytosm.cypher2sql.expandpaths.CypherConverter;
import org.cytosm.pathfinder.CanonicalRoutes;
import org.cytosm.pathfinder.PathFinder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import org.cytosm.common.gtop.GTopInterfaceImpl;
import org.cytosm.common.gtop.RelationalGTopInterface;
import org.cytosm.pathfinder.output.PathSerializer;
import org.cytosm.pathfinder.output.Serializer;

public class PathSerializerTest {
    GTopInterfaceImpl gTopInter;
    GTopInterfaceImpl gTopInterDirect;
    Serializer serializer = new PathSerializer();

    @Before
    public void initTest() {
        // load gtop
        String path = "src" + File.separatorChar + "test" + File.separatorChar + "resources" + File.separatorChar;

        try {
            gTopInter = new RelationalGTopInterface(new File(path + "northwindUndirect.gtop"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            gTopInterDirect = new RelationalGTopInterface(new File(path + "northwindDirect.gtop"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void anonymousStartAndEndWithPath() {
        List<String> expandedPaths = expand("()-[:sold]-()");
        Assert.assertEquals(1, expandedPaths.size());
        Assert.assertTrue(expandedPaths.contains("(:orders)-[:sold]-(:employees)"));
    }

    @Test
    public void anonymousEndWithPath() {
        List<String> expandedPaths = expand("(orders)-[:sold]-()");
        Assert.assertEquals(2, expandedPaths.size());
        Assert.assertTrue(expandedPaths.contains("(orders:employees)-[:sold]-(:orders)"));
        Assert.assertTrue(expandedPaths.contains("(orders:orders)-[:sold]-(:employees)"));
    }

    @Test
    public void anonymousEndWithPathWithAttribute() {
        List<String> expandedPaths = expand("(orders)-[X:sold]-()");
        Assert.assertEquals(2, expandedPaths.size());
        Assert.assertTrue(expandedPaths.contains("(orders:employees)-[X:sold]-(:orders)"));
        Assert.assertTrue(expandedPaths.contains("(orders:orders)-[X:sold]-(:employees)"));
    }

    @Test
    public void anonymousStartWithPath() {
        List<String> expandedPaths = expand("()-[:sold]-(employees)");
        Assert.assertEquals(2, expandedPaths.size());
        Assert.assertTrue(expandedPaths.contains("(:employees)-[:sold]-(employees:orders)"));
        Assert.assertTrue(expandedPaths.contains("(:orders)-[:sold]-(employees:employees)"));
    }

    @Test
    public void simplePath() {
        List<String> expandedPaths = expand("(:orders)");
        Assert.assertEquals(1, expandedPaths.size());
        Assert.assertEquals("(:orders)", expandedPaths.get(0));
    }

    @Test
    public void anonymousPath() {
        List<String> expandedPaths = expand("(:ORDERS)--(:employees)");
        Assert.assertEquals(1, expandedPaths.size());
        Assert.assertTrue(expandedPaths.contains("(:orders)-[:sold]-(:employees)"));
    }

    @Test
    public void anonymousPathWithStart() {
        List<String> expandedPaths = expand("(:orders)--()");
        Assert.assertEquals(3, expandedPaths.size());

        Assert.assertTrue(expandedPaths.contains("(:orders)-[:purchased]-(:customers)"));
        Assert.assertTrue(expandedPaths.contains("(:orders)-[:product]-(:products)"));
        Assert.assertTrue(expandedPaths.contains("(:orders)-[:sold]-(:employees)"));
    }

    @Test
    public void anonymousPathWithEnd() {
        List<String> expandedPaths = expand("()--(:orders)");
        Assert.assertEquals(3, expandedPaths.size());

        Assert.assertTrue(expandedPaths.contains("(:customers)-[:purchased]-(:orders)"));
        Assert.assertTrue(expandedPaths.contains("(:products)-[:product]-(:orders)"));
        Assert.assertTrue(expandedPaths.contains("(:employees)-[:sold]-(:orders)"));
    }

    @Test
    public void anonymousTwo() {
        List<String> expandedPaths = expand("()--()");
        Assert.assertEquals(5, expandedPaths.size());

        Assert.assertTrue(expandedPaths.contains("(:employees)-[:reports_to]-(:employees)"));
        Assert.assertTrue(expandedPaths.contains("(:orders)-[:purchased]-(:customers)"));
        Assert.assertTrue(expandedPaths.contains("(:orders)-[:product]-(:products)"));
        Assert.assertTrue(expandedPaths.contains("(:products)-[:part_of]-(:categories)"));
        Assert.assertTrue(expandedPaths.contains("(:orders)-[:sold]-(:employees)"));
    }

    @Test
    public void anonymousReportTo() {
        List<String> expandedPaths = expand("()-[:REPORTS_TO]-()");

        Assert.assertEquals(1, expandedPaths.size());
        Assert.assertTrue(expandedPaths.contains("(:employees)-[:reports_to]-(:employees)"));
    }

    @Test
    @Ignore
    public void anonymousReportToStar1() {
        List<String> expandedPaths = expand("()-[:REPORTS_TO*1]-()");
        Assert.assertEquals(5, expandedPaths.size());

        Assert.assertTrue(expandedPaths.contains("(:employees)-[:reports_to]-(:employees)"));
        Assert.assertTrue(expandedPaths.contains("(:orders)-[:purchased]-(:customers)"));
        Assert.assertTrue(expandedPaths.contains("(:orders)-[:product]-(:products)"));
        Assert.assertTrue(expandedPaths.contains("(:products)-[:part_of]-(:categories)"));
        Assert.assertTrue(expandedPaths.contains("(:orders)-[:sold]-(:employees)"));
    }

    @Test
    @Ignore
    public void anonymousReportToStar2() {
        List<String> expandedPaths = expand("()-[:REPORTS_TO*2]-()");
        Assert.assertEquals(14, expandedPaths.size());
        Assert.assertTrue(expandedPaths.contains("(:orders)-[:sold]-(:employees)-[:sold]-(:orders)"));
        Assert.assertTrue(expandedPaths.contains("(:orders)-[:purchased]-(:customers)-[:purchased]-(:orders)"));
        Assert.assertTrue(expandedPaths.contains("(:orders)-[:product]-(:products)-[:product]-(:orders)"));
        Assert.assertTrue(expandedPaths.contains("(:products)-[:part_of]-(:categories)-[:part_of]-(:products)"));
        Assert.assertTrue(expandedPaths.contains("(:products)-[:product]-(:orders)-[:product]-(:products)"));
        Assert.assertTrue(expandedPaths.contains("(:categories)-[:part_of]-(:products)-[:part_of]-(:categories)"));
        Assert.assertTrue(expandedPaths.contains("(:employees)-[:sold]-(:orders)-[:sold]-(:employees)"));
        Assert.assertTrue(expandedPaths.contains("(:employees)-[:reports_to]-(:employees)-[:reports_to]-(:employees)"));
        Assert.assertTrue(expandedPaths.contains("(:customers)-[:purchased]-(:orders)-[:purchased]-(:customers)"));
        Assert.assertTrue(expandedPaths.contains("(:orders)-[:product]-(:products)-[:part_of]-(:categories)"));
        Assert.assertTrue(expandedPaths.contains("(:products)-[:product]-(:orders)-[:purchased]-(:customers)"));
        Assert.assertTrue(expandedPaths.contains("(:products)-[:product]-(:orders)-[:sold]-(:employees)"));
        Assert.assertTrue(expandedPaths.contains("(:orders)-[:sold]-(:employees)-[:reports_to]-(:employees)"));
        Assert.assertTrue(expandedPaths.contains("(:employees)-[:sold]-(:orders)-[:purchased]-(:customers)"));
    }

    @Test
    @Ignore
    public void anonymousReportToStar1to2() {
        List<String> expandedPaths = expand("()-[:REPORTS_TO*1..2]-()");
        Assert.assertEquals(19, expandedPaths.size());
        Assert.assertTrue(expandedPaths.contains("(:employees)-[:reports_to]-(:employees)"));
        Assert.assertTrue(expandedPaths.contains("(:orders)-[:sold]-(:employees)-[:sold]-(:orders)"));
        Assert.assertTrue(expandedPaths.contains("(:orders)-[:purchased]-(:customers)-[:purchased]-(:orders)"));
        Assert.assertTrue(expandedPaths.contains("(:orders)-[:product]-(:products)-[:product]-(:orders)"));
        Assert.assertTrue(expandedPaths.contains("(:products)-[:part_of]-(:categories)-[:part_of]-(:products)"));
        Assert.assertTrue(expandedPaths.contains("(:products)-[:product]-(:orders)-[:product]-(:products)"));
        Assert.assertTrue(expandedPaths.contains("(:categories)-[:part_of]-(:products)-[:part_of]-(:categories)"));
        Assert.assertTrue(expandedPaths.contains("(:employees)-[:sold]-(:orders)-[:sold]-(:employees)"));
        Assert.assertTrue(expandedPaths.contains("(:employees)-[:reports_to]-(:employees)-[:reports_to]-(:employees)"));
        Assert.assertTrue(expandedPaths.contains("(:customers)-[:purchased]-(:orders)-[:purchased]-(:customers)"));
        Assert.assertTrue(expandedPaths.contains("(:orders)-[:product]-(:products)"));
        Assert.assertTrue(expandedPaths.contains("(:orders)-[:product]-(:products)-[:part_of]-(:categories)"));
        Assert.assertTrue(expandedPaths.contains("(:products)-[:product]-(:orders)-[:purchased]-(:customers)"));
        Assert.assertTrue(expandedPaths.contains("(:products)-[:product]-(:orders)-[:sold]-(:employees)"));
        Assert.assertTrue(expandedPaths.contains("(:orders)-[:sold]-(:employees)-[:reports_to]-(:employees)"));
        Assert.assertTrue(expandedPaths.contains("(:products)-[:part_of]-(:categories)"));
        Assert.assertTrue(expandedPaths.contains("(:orders)-[:sold]-(:employees)"));
        Assert.assertTrue(expandedPaths.contains("(:orders)-[:purchased]-(:customers)"));
        Assert.assertTrue(expandedPaths.contains("(:employees)-[:sold]-(:orders)-[:purchased]-(:customers)"));
    }


    @Test
    @Ignore
    public void anonymousReportToStar2to2() {
        List<String> expandedPaths = expand("()-[:REPORTS_TO*2..2]-()");
        Assert.assertEquals(14, expandedPaths.size());
        Assert.assertTrue(expandedPaths.contains("(:orders)-[:sold]-(:employees)-[:sold]-(:orders)"));
        Assert.assertTrue(expandedPaths.contains("(:orders)-[:purchased]-(:customers)-[:purchased]-(:orders)"));
        Assert.assertTrue(expandedPaths.contains("(:orders)-[:product]-(:products)-[:product]-(:orders)"));
        Assert.assertTrue(expandedPaths.contains("(:products)-[:part_of]-(:categories)-[:part_of]-(:products)"));
        Assert.assertTrue(expandedPaths.contains("(:products)-[:product]-(:orders)-[:product]-(:products)"));
        Assert.assertTrue(expandedPaths.contains("(:categories)-[:part_of]-(:products)-[:part_of]-(:categories)"));
        Assert.assertTrue(expandedPaths.contains("(:employees)-[:sold]-(:orders)-[:sold]-(:employees)"));
        Assert.assertTrue(expandedPaths.contains("(:employees)-[:reports_to]-(:employees)-[:reports_to]-(:employees)"));
        Assert.assertTrue(expandedPaths.contains("(:customers)-[:purchased]-(:orders)-[:purchased]-(:customers)"));
        Assert.assertTrue(expandedPaths.contains("(:orders)-[:product]-(:products)-[:part_of]-(:categories)"));
        Assert.assertTrue(expandedPaths.contains("(:products)-[:product]-(:orders)-[:purchased]-(:customers)"));
        Assert.assertTrue(expandedPaths.contains("(:products)-[:product]-(:orders)-[:sold]-(:employees)"));
        Assert.assertTrue(expandedPaths.contains("(:orders)-[:sold]-(:employees)-[:reports_to]-(:employees)"));
        Assert.assertTrue(expandedPaths.contains("(:employees)-[:sold]-(:orders)-[:purchased]-(:customers)"));
    }

    @Test
    public void anonymousThree() {
        List<String> expandedPaths = expand("()--()--()");
        Assert.assertEquals(14, expandedPaths.size());

        Assert.assertTrue(expandedPaths.contains("(:orders)-[:sold]-(:employees)-[:sold]-(:orders)"));
        Assert.assertTrue(expandedPaths.contains("(:orders)-[:purchased]-(:customers)-[:purchased]-(:orders)"));
        Assert.assertTrue(expandedPaths.contains("(:orders)-[:product]-(:products)-[:product]-(:orders)"));
        Assert.assertTrue(expandedPaths.contains("(:products)-[:part_of]-(:categories)-[:part_of]-(:products)"));
        Assert.assertTrue(expandedPaths.contains("(:products)-[:product]-(:orders)-[:product]-(:products)"));
        Assert.assertTrue(expandedPaths.contains("(:categories)-[:part_of]-(:products)-[:part_of]-(:categories)"));
        Assert.assertTrue(expandedPaths.contains("(:employees)-[:sold]-(:orders)-[:sold]-(:employees)"));
        Assert.assertTrue(expandedPaths.contains("(:employees)-[:reports_to]-(:employees)-[:reports_to]-(:employees)"));
        Assert.assertTrue(expandedPaths.contains("(:customers)-[:purchased]-(:orders)-[:purchased]-(:customers)"));
        Assert.assertTrue(expandedPaths.contains("(:orders)-[:product]-(:products)-[:part_of]-(:categories)"));
        Assert.assertTrue(expandedPaths.contains("(:products)-[:product]-(:orders)-[:purchased]-(:customers)"));
        Assert.assertTrue(expandedPaths.contains("(:products)-[:product]-(:orders)-[:sold]-(:employees)"));
        Assert.assertTrue(expandedPaths.contains("(:orders)-[:sold]-(:employees)-[:reports_to]-(:employees)"));
        Assert.assertTrue(expandedPaths.contains("(:employees)-[:sold]-(:orders)-[:purchased]-(:customers)"));
    }

    @Test
    @Ignore
    // test marked as ignore as we aren't <--> yet
    public void bidirectionalQuery() {
        List<String> expandedPaths = expand("(:orders) <--> (:employees)");
        Assert.assertEquals(1, expandedPaths.size());
        Assert.assertTrue(expandedPaths.contains("(:orders)-[:sold]-(:employees)"));
    }

    @Test
    @Ignore
    // test marked as ignore as we aren't <--> yet
    public void annoymousBidirectionalQuery() {
        List<String> expandedPaths = expand("() <--> ()");
        Assert.assertEquals(1, expandedPaths.size());
        Assert.assertTrue(expandedPaths.contains("(:orders)-[:sold]-(:employees)"));
    }

    @Test
    @Ignore
    // test marked as ignore as we aren't supporting 0 in the relationship range
    public void anonymousReportToStar0to2() {
        List<String> expandedPaths = expand("()-[:REPORTS_TO*0..2]-()");
        Assert.assertEquals(20, expandedPaths.size());
        Assert.assertTrue(expandedPaths.contains("(:employees)-[:reports_to]-(:employees)"));
    }

    @Test
    public void anonymousPathWithAttributes() {
        List<String> expandedPaths = expand("(:ORDERS)--(:employees {firstName:\"Test\"})");
        Assert.assertEquals(1, expandedPaths.size());
        Assert.assertTrue(expandedPaths.contains("(:orders)-[:sold]-(:employees {firstName:\"Test\"})"));
    }

    @Test
    public void anonymousStartWithPathWithMismatchAttributes() {
        List<String> expandedPaths = expand("()-[:SOLD {UnitPrice:\"foo\"}]-(o)");
        Assert.assertEquals(0, expandedPaths.size());
    }

    @Test
    public void anonymousStartWithPathWithAttributes() {
        List<String> expandedPaths = expand("()-[:product {UnitPrice:\"foo\"}]-(o)");

        Assert.assertEquals(2, expandedPaths.size());

        Assert.assertTrue(expandedPaths.contains("(:products)-[:product {UnitPrice:\"foo\"}]-(o:orders)"));
        Assert.assertTrue(expandedPaths.contains("(:orders)-[:product {UnitPrice:\"foo\"}]-(o:products)"));
    }

    @Test
    public void anonymousStartWithDirect() {

        List<String> expandedPaths = expandDirect("(n) --> (:orders)");

        Assert.assertEquals(2, expandedPaths.size());
        for (String string : expandedPaths) {
            System.out.println(string);
        }
        Assert.assertTrue(expandedPaths.contains("(n:employees)-[:sold]->(:orders)"));
        Assert.assertTrue(expandedPaths.contains("(n:customers)-[:purchased]->(:orders)"));
    }

    @Test
    public void anonymousStartWithDirectPlusAttributes() {

        List<String> expandedPaths = expandDirect("(n) --> (:Orders {ShipName: \"Island Trading\"})");

        Assert.assertEquals(2, expandedPaths.size());
        Assert.assertTrue(expandedPaths.contains("(n:employees)-[:sold]->(:orders {ShipName:\"Island Trading\"})"));
        Assert.assertTrue(expandedPaths.contains("(n:customers)-[:purchased]->(:orders {ShipName:\"Island Trading\"})"));
    }

    private List<String> expand(final String cypherInput) {
        PathFinder matcher = new PathFinder(new CypherConverter());

        CanonicalRoutes paths = matcher.enumerate(cypherInput, gTopInter);
        List<String> expandedPaths = serializer.serialize(paths.getAllPossibleRoutes());
        return expandedPaths;
    }

    private List<String> expandDirect(final String cypherInput) {
        PathFinder matcher = new PathFinder(new CypherConverter());

        CanonicalRoutes paths = matcher.enumerate(cypherInput, gTopInterDirect);
        List<String> expandedPaths = serializer.serialize(paths.getAllPossibleRoutes());
        return expandedPaths;
    }
}
