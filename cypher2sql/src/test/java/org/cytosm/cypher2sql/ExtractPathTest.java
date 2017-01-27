package org.cytosm.cypher2sql;

import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import org.cytosm.cypher2sql.expandpaths.ExtractPath;
import org.cytosm.cypher2sql.expandpaths.PathPlusHints;

public class ExtractPathTest {

    @Test
    public void splitNoPath() {
        String query = "MATCH (m:Movie) WHERE m.released > 1998 OR m.released > 2000 RETURN m.title";
        String expect = "(m:Movie)";
        splitCheckPath(query, expect);
    }

    @Test
    public void splitPath() {
        String query = "MATCH (p:Person)-[:acted_in]->(m:Movie) RETURN p.name, m.title";
        String expect = "(p:Person)-[:acted_in]->(m:Movie)";
        splitCheckPath(query, expect);
    }

    @Test
    public void splitPath2() {
        String query =
                "MATCH (director:Person)-[:person_id_directed_person_id]->(m) RETURN director.name, count(*) ORDER BY count(*) DESC";
        String expect = "(director:Person)-[:person_id_directed_person_id]->(m)";
        splitCheckPath(query, expect);
    }

    @Test
    public void splitPath3() {
        String query = "MATCH (keanu:Person {name:\"Keanu Reeves\"})-[:acted_in]->(m:Movie) RETURN m.title";
        String expect = "(keanu:Person {name:\"Keanu Reeves\"})-[:acted_in]->(m:Movie)";
        splitCheckPath(query, expect);
    }

    @Test
    public void splitPath4() {
        String query = "MATCH (n)-[]->(keanu:Person {name:\"Keanu Reeves\"}) return n";
        String expect = "(n)-[]->(keanu:Person {name:\"Keanu Reeves\"})";
        splitCheckPath(query, expect);
    }

    @Test
    public void splitPath5() {
        String query = "MATCH (m:Movie {released:\"1999\"})-[]->(p:Person) return m";
        String expect = "(m:Movie {released:\"1999\"})-[]->(p:Person)";
        splitCheckPath(query, expect);
    }

    @Test
    public void splitPath6() {
        String query = "MATCH (m:Movie {released:\"1999\"})-[]->(keanu:Person {name:\"Keanu Reeves\"}) return m";
        String expect = "(m:Movie {released:\"1999\"})-[]->(keanu:Person {name:\"Keanu Reeves\"})";
        splitCheckPath(query, expect);
    }

    @Test
    public void splitPath7() {
        String query = "MATCH (n)-[]->(p:Person) return n";
        String expect = "(n)-[]->(p:Person)";
        splitCheckPath(query, expect);
    }

    @Test
    public void splitMultiPath() {
        String query =
                "MATCH (keanu:Person {name:\"Keanu Reeves\"})-[:acted_in]->(m:Movie),"
                        + "(director:Person)-[:person_id_directed_person_id]->(m) RETURN director.name, count(*) "
                        + "ORDER BY count(*) DESC";
        String expect = "(keanu:Person {name:\"Keanu Reeves\"})-[:acted_in]->(m:Movie)";
        String expect2 = "(director:Person)-[:person_id_directed_person_id]->(m)";
        splitCheckPath(query, expect, expect2);
    }

    @Test
    public void splitMultiPath2() {
        String query =
                "MATCH (customer:Customers {companyName:\"Blauer See Delikatessen\"})-[:Customers_CustomerID_Orders_CustomerID]->(orders) "
                        + "MATCH (orders)-[:OrderID_OrderDetails_OrderID]->()-[:Categories_CategoryID_Products_ProductID*0..1]->(categories) RETURN orders.orderId, categories.categoryId";
        String expect =
                "(customer:Customers {companyName:\"Blauer See Delikatessen\"})-[:Customers_CustomerID_Orders_CustomerID]->(orders)";
        String expect2 =
                "(orders)-[:OrderID_OrderDetails_OrderID]->()-[:Categories_CategoryID_Products_ProductID*0..1]->(categories)";
        splitCheckPath(query, expect, expect2);
    }

    @Test
    public void splitMultiPath3() {
        String query =
                "MATCH (n:Employees {firstName:\"Anne\"})-[:Employees_EmployeeID_Employees_ReportsTo]->()-[:Employees_EmployeeID_Employees_ReportsTo]->(bigboss:Employees) RETURN n.firstName, bigboss.firstName";

        String expect =
                "(n:Employees {firstName:\"Anne\"})-[:Employees_EmployeeID_Employees_ReportsTo]->()-[:Employees_EmployeeID_Employees_ReportsTo]->(bigboss:Employees)";
        splitCheckPath(query, expect);
    }

    @Test
    public void splitMultiPath4() {
        String query =
                "MATCH (n:Employees {firstName:\"Anne\"})-[:Employees_EmployeeID_Employees_ReportsTo*3..3]->(bigboss) RETURN n.firstName, bigboss.firstName";

        String expect = "(n:Employees {firstName:\"Anne\"})-[:Employees_EmployeeID_Employees_ReportsTo*3..3]->(bigboss)";
        splitCheckPath(query, expect);
    }

    @Test
    public void splitMultiPath5() {
        String query =
                "MATCH (:Customers {ContactTitle:\"Sales Representative\"})-[]->(o:Orders)<-[]-(:Employees {firstName:\"Anne\"})  RETURN o";

        String expect =
                "(:Customers {ContactTitle:\"Sales Representative\"})-[]->(o:Orders)<-[]-(:Employees {firstName:\"Anne\"})";
        splitCheckPath(query, expect);
    }

    @Test
    public void splitWhere() {
        String query =
                "MATCH (n:Employees) WHERE (n)-[:Employees_EmployeeID_Orders_EmployeeID]->(:Orders {shipName:\"North/South\"}) RETURN n";
        String expect = "(n:Employees)";
        String expect2 = "(n)-[:Employees_EmployeeID_Orders_EmployeeID]->(:Orders {shipName:\"North/South\"})";
        List<PathPlusHints> pathAndHints = splitCheckPath(query, expect, expect2);
        for (PathPlusHints pathPlusHint : pathAndHints) {
            System.out.println(pathPlusHint.getPath() + " - " + pathPlusHint.getHints());
        }
    }

    @Test
    public void splitWhere2() {
        String query = "MATCH (n:Employees) WHERE n.name = 'james' and p.foo = 'asd'";
        String expect = "(n:Employees)";
        List<PathPlusHints> pathAndHints = splitCheckPath(query, expect);
        Assert.assertEquals(1, pathAndHints.size());
        Assert.assertEquals("foo", pathAndHints.get(0).getHintsIntoList().get("p").get(0));
        Assert.assertEquals("name", pathAndHints.get(0).getHintsIntoList().get("n").get(0));
        Assert.assertTrue(pathAndHints.get(0).getHints().keySet().contains("p"));
        Assert.assertTrue(pathAndHints.get(0).getHints().keySet().contains("n"));
    }

    @Test
    public void splitWhereLessThanOrEqual() {
        String query = "MATCH (n:Customers) WHERE n.fax <= 1 RETURN n.fax";

        String expect = "(n:Customers)";
        List<PathPlusHints> pathAndHints = splitCheckPath(query, expect);
        Assert.assertEquals(1, pathAndHints.size());
        Assert.assertEquals("fax", pathAndHints.get(0).getHintsIntoList().get("n").get(0));
    }

    @Test
    public void splitWhereGreaterOrEqualThan() {
        String query = "MATCH (n:Customers) WHERE n.fax >= 1 RETURN n.fax";
        String expect = "(n:Customers)";
        List<PathPlusHints> pathAndHints = splitCheckPath(query, expect);
        Assert.assertEquals(1, pathAndHints.size());
        Assert.assertEquals("fax", pathAndHints.get(0).getHintsIntoList().get("n").get(0));
    }

    @Test
    public void splitWhereLessThan() {
        String query = "MATCH (n:Customers) WHERE n.fax < 1 RETURN n.fax";
        String expect = "(n:Customers)";
        List<PathPlusHints> pathAndHints = splitCheckPath(query, expect);
        Assert.assertEquals(1, pathAndHints.size());
        Assert.assertEquals("fax", pathAndHints.get(0).getHintsIntoList().get("n").get(0));
    }

    @Test
    public void splitWhereContainsNot() {
        String query = "MATCH (n:Customers) WHERE  not((friend)-[:IS_LOCATED_IN]->()-[:IS_PART_OF]->(countryX))";
        String[] expects = {"(n:Customers)", "(friend)-[:IS_LOCATED_IN]->()-[:IS_PART_OF]->(countryX)"};
        List<PathPlusHints> pathAndHints = splitCheckPath(query, expects);
        Assert.assertEquals(2, pathAndHints.size());
    }

    @Test
    public void splitMultipleWhere() {
        String query =
                "MATCH (n:Customers) WHERE n.fax < 1 and (b)-[]-(n) MATCH (b:Foo) WHERE b.name = 'james' and (c)-[]-(d) RETURN n.fax";
        String[] expect = {"(n:Customers)", "(b)-[]-(n)", "(b:Foo)", "(c)-[]-(d)"};
        //        List<PathPlusHints> pathAndHints = splitCheckPath(query, expect);
        ExtractPath extractPath = new ExtractPath();
        List<PathPlusHints> pathAndHints = splitCheckPath(query, expect);

        Assert.assertEquals(4, pathAndHints.size());
        Assert.assertEquals("fax", pathAndHints.get(1).getHintsIntoList().get("n").get(0));
        Assert.assertEquals("name", pathAndHints.get(3).getHintsIntoList().get("b").get(0));
    }

    @Test
    public void splitWhereGreaterThan() {
        String query = "MATCH (n:Customers) WHERE n.fax > 1 RETURN n.fax";
        String expect = "(n:Customers)";
        List<PathPlusHints> pathAndHints = splitCheckPath(query, expect);
        Assert.assertEquals(1, pathAndHints.size());
        Assert.assertEquals("fax", pathAndHints.get(0).getHintsIntoList().get("n").get(0));
    }
    
    @Test
    public void returnHint() {
        // Expect that n is a customer, according to northwind data.
        String query = "MATCH (n) RETURN n.fax";
        String expect = "(n)";
        List<PathPlusHints> pathAndHints = splitCheckPath(query, expect);
        Assert.assertEquals(1, pathAndHints.size());
        // We can't use return value accesses as hint as in cypher it would return null times the number of result
        // for that column.
        Assert.assertEquals(0, pathAndHints.get(0).getHints().size());
//        Assert.assertEquals("fax", pathAndHints.get(0).getHintsIntoList().get("n").get(0));
    }
    
    @Test
    public void returnWithDistinctHint() {
        // Expect that n is a customer, according to northwind data.
        String query = "MATCH (n) RETURN DISTINCT n.fax";
        String expect = "(n)";
        List<PathPlusHints> pathAndHints = splitCheckPath(query, expect);
        Assert.assertEquals(1, pathAndHints.size());
        // We can't use return value accesses as hint as in cypher it would return null times the number of result
        // for that column.
        Assert.assertEquals(0, pathAndHints.get(0).getHints().size());
//        Assert.assertEquals("fax", pathAndHints.get(0).getHintsIntoList().get("n").get(0));
    }
    
    @Test
    public void returnWithAsHint() {
        // Expect that n is a customer, according to northwind data.
        String query = "MATCH (n) RETURN n.fax AS faxNumber";
        String expect = "(n)";
        List<PathPlusHints> pathAndHints = splitCheckPath(query, expect);
        Assert.assertEquals(1, pathAndHints.size());
        // We can't use return value accesses as hint as in cypher it would return null times the number of result
        // for that column.
        Assert.assertEquals(0, pathAndHints.get(0).getHints().size());
//        Assert.assertEquals("fax", pathAndHints.get(0).getHintsIntoList().get("n").get(0));
    }
    
    @Test
    public void returnWithStarHint() {
        // Expect that n is a customer, according to northwind data.
        String query = "MATCH (n) RETURN *";
        String expect = "(n)";
        List<PathPlusHints> pathAndHints = splitCheckPath(query, expect);
        Assert.assertEquals(1, pathAndHints.size());
        Assert.assertEquals(0, pathAndHints.get(0).getHints().size());
    }
    
    @Test
    public void returnWithLimitHint() {
        // Expect that n is a customer, according to northwind data.
        String query = "MATCH (n) RETURN n.fax LIMIT 5";
        String expect = "(n)";
        List<PathPlusHints> pathAndHints = splitCheckPath(query, expect);
        Assert.assertEquals(1, pathAndHints.size());
        Assert.assertEquals(0, pathAndHints.get(0).getHints().size());
    }
    
    @Test
    public void returnWithPreviousPathHint() {
        // Expect that n is a customer, according to northwind data.
        String query = "MATCH (n:Employees {firstName:\"Anne\"})-[:Employees_EmployeeID_Orders_EmployeeID]-(orders:Orders) return n.firstName, orders.shipName";
        String expect = "(n:Employees {firstName:\"Anne\"})-[:Employees_EmployeeID_Orders_EmployeeID]-(orders:Orders)";
        List<PathPlusHints> pathAndHints = splitCheckPath(query, expect);
        Assert.assertEquals(1, pathAndHints.size());
        // We can't use return value accesses as hint as in cypher it would return null times the number of result
        // for that column.
        Assert.assertEquals(0, pathAndHints.get(0).getHints().size());
//        Assert.assertEquals("firstName", pathAndHints.get(0).getHintsIntoList().get("n").get(0));
//        Assert.assertEquals("shipName", pathAndHints.get(0).getHintsIntoList().get("orders").get(0));
    }


    @Test
    public void splitMultiPathAndWhere() {
        String query =
                "MATCH (keanu:Person {name:\"Keanu Reeves\"})-[:acted_in]->(m:Movie),"
                        + "(director:Person)-[:person_id_directed_person_id]->(m) "
                        + "WHERE (n)-[:Employees_EmployeeID_Orders_EmployeeID]->(:Orders {shipName:\"North/South\"}) RETURN director.name, count(*) "
                        + "ORDER BY count(*) DESC";
        String expect = "(keanu:Person {name:\"Keanu Reeves\"})-[:acted_in]->(m:Movie)";
        String expect2 = "(director:Person)-[:person_id_directed_person_id]->(m)";
        String expect3 = "(n)-[:Employees_EmployeeID_Orders_EmployeeID]->(:Orders {shipName:\"North/South\"})";
        splitCheckPath(query, expect, expect2, expect3);
    }

    @Ignore
    public List<PathPlusHints> splitCheckPath(final String query, final String... expect) {
        ExtractPath extractPath = new ExtractPath();
        List<PathPlusHints> matches = extractPath.split(query);
        for (PathPlusHints pathPlusHints : matches) {
            System.out.println(pathPlusHints.getPath() + " " + pathPlusHints.getHints());
        }
        Assert.assertEquals(expect.length, matches.size());
        int i = 0;
        for (PathPlusHints actual : matches) {
            Assert.assertEquals(expect[i].trim(), actual.getPath().trim());
            i++;
        }
        return matches;
    }

    @Test
    public void testDodgyCypherSplitting(){
        String query = "MATCH (where) WHERE where.name=\"Tom Hanks\" OPTIONAL MATCH (where)-[:person_id_produced_person_id]->(movie) RETURN tom, movie";

        String[] expected =
                {"(where)"
                 ,"(where)-[:person_id_produced_person_id]->(movie)"};

        splitCheckPath(query, expected);
    }

    @Test
    public void testQuery3splitting() {
        String query =
                "MATCH (person:Person {id:933})-[:KNOWS*1..2]->(friend:Person)<-[:HAS_CREATOR]-(messageX), (messageX)-[:IS_LOCATED_IN]->(countryX:Country) "
                        + "WHERE not(person=friend) "
                        + "WITH friend, count(DISTINCT messageX) "
                        + "MATCH (friend)<-[:HAS_CREATOR]-(messageY)-[:IS_LOCATED_IN]->(countryY:Country) "
                        + "WHERE not((friend)-[:IS_LOCATED_IN]->()-[:IS_PART_OF]->(countryY)) "
                        + "WITH friend.id AS friendId RETURN friendId ORDER BY friendId ASC";

        String[] expected =
                {"(person:Person {id:933})-[:KNOWS*1..2]->(friend:Person)<-[:HAS_CREATOR]-(messageX)",
                        "(messageX)-[:IS_LOCATED_IN]->(countryX:Country)",
                        "(friend)<-[:HAS_CREATOR]-(messageY)-[:IS_LOCATED_IN]->(countryY:Country)",
                        "(friend)-[:IS_LOCATED_IN]->()-[:IS_PART_OF]->(countryY)"};

        splitCheckPath(query, expected);
    }

    @Test
    public void tesNotQuery() {
        String query =
                "MATCH (person:Employees {firstName:\"Anne\"})-[:Employees_EmployeeID_Employees_ReportsTo]-(n:Employees) MATCH (boss:Employees {firstName:\"Steven\"}) where not(n=boss) return n, boss";
        String[] expected =
                {"(person:Employees {firstName:\"Anne\"})-[:Employees_EmployeeID_Employees_ReportsTo]-(n:Employees)",
                        "(boss:Employees {firstName:\"Steven\"})"};

        splitCheckPath(query, expected);
    }

    @Test
    public void testCommaSeperated() {
        String query =
                "MATCH (person:Employees {firstName:\"Anne\"})-[:reports_to]-(friend:Employees), (friend:Employees)-[:sold]-(orders:Orders)";
        splitCheckPath(query, "(person:Employees {firstName:\"Anne\"})-[:reports_to]-(friend:Employees)",
                "(friend:Employees)-[:sold]-(orders:Orders)");
    }

    @Test
    public void testWith() {
        String query =
                "MATCH (person:Employees {firstName:\"Anne\"})-[:reports_to]-(friend:Employees) WITH person MATCH (friend:Employees)-[:sold]-(orders:Orders)";
        splitCheckPath(query, "(person:Employees {firstName:\"Anne\"})-[:reports_to]-(friend:Employees)",
                "(friend:Employees)-[:sold]-(orders:Orders)");
    }


    @Test
    public void testCommaSeperatedAndWith() {
        String query =
                "MATCH (person:Employees {firstName:\"Anne\"})-[:reports_to]-(friend:Employees), (friend:Employees)-[:sold]-(orders:Orders) WITH person MATCH (friend:Employees)-[:sold]-(orders:Orders)";
        splitCheckPath(query, "(person:Employees {firstName:\"Anne\"})-[:reports_to]-(friend:Employees)",
                "(friend:Employees)-[:sold]-(orders:Orders)", "(friend:Employees)-[:sold]-(orders:Orders)");
    }


    @Test
    public void testCommaSeperatedAndWith2() {
        String query =
                "MATCH (person:Employees {firstName:\"Anne\"})-[:reports_to]-(friend:Employees) WITH person MATCH (friend:Employees)-[:sold]-(orders:Orders), (friend:Employees)-[:sold]-(orders:Orders)";
        splitCheckPath(query, "(person:Employees {firstName:\"Anne\"})-[:reports_to]-(friend:Employees)",
                "(friend:Employees)-[:sold]-(orders:Orders)", "(friend:Employees)-[:sold]-(orders:Orders)");

    }

    @Test
    public void testExtractQuery3() {
        String query =
                "MATCH (person:Person {id:2199023259437})-[:KNOWS*1..2]->(friend:Person)<-[:HAS_CREATOR]-(messageX), (messageX)-[:IS_LOCATED_IN]->(countryX:Country) "
                        + "WHERE not(person=friend) AND not((friend)-[:IS_LOCATED_IN]->()-[:IS_PART_OF]->(countryX)) AND countryX.url='http://dbpedia.org/resource/France' AND messageX.creationDate>='1990-7-7' AND messageX.creationDate<'2016-7-7' "
                        + "WITH friend, count(DISTINCT messageX) AS xCount "
                        + "MATCH (friend)<-[:HAS_CREATOR]-(messageY)-[:IS_LOCATED_IN]->(countryY:Country) "
                        + "WHERE countryY.url='http://dbpedia.org/resource/France' AND not((friend)-[:IS_LOCATED_IN]->()-[:IS_PART_OF]->(countryY)) AND messageY.creationDate>='1990-7-7' AND messageY.creationDate<'2016-7-7' "
                        + "WITH friend.id AS friendId, friend.firstName AS friendFirstName, friend.lastName AS friendLastName, xCount, count(DISTINCT messageY) AS yCount "
                        + "RETURN friendId, friendFirstName, friendLastName, xCount, yCount, xCount + yCount AS xyCount ORDER BY xyCount DESC, friendId ASC LIMIT 5";
        String[] expects =
                {"(person:Person {id:2199023259437})-[:KNOWS*1..2]->(friend:Person)<-[:HAS_CREATOR]-(messageX)",
                        "(messageX)-[:IS_LOCATED_IN]->(countryX:Country)",
                        "(friend)-[:IS_LOCATED_IN]->()-[:IS_PART_OF]->(countryX)",
                        "(friend)<-[:HAS_CREATOR]-(messageY)-[:IS_LOCATED_IN]->(countryY:Country)",
                        "(friend)-[:IS_LOCATED_IN]->()-[:IS_PART_OF]->(countryY)"};
        splitCheckPath(query, expects);
    }
}
