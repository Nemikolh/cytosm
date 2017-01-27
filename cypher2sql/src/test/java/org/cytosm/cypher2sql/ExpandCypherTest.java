package org.cytosm.cypher2sql;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.cytosm.common.gtop.GTopInterfaceImpl;
import org.cytosm.common.gtop.RelationalGTopInterface;
import org.cytosm.cypher2sql.expandpaths.ExpandCypher;

public class ExpandCypherTest {

    GTopInterfaceImpl gtopInterface = null;

    @Before
    public void setup() {
        String filePath = "src" + File.separatorChar + "test" + File.separatorChar + "resources" + File.separatorChar;

        try {
            gtopInterface = new RelationalGTopInterface(new File(filePath + "northwindUndirect.gtop"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testAnonymousOneHop() {
        String query = "match ()--(o) return o";
        List<String> queries = ExpandCypher.expandCypher(gtopInterface, query);
        Assert.assertEquals(9, queries.size());
    }

    @Test
    public void testAnonymousMulti() {
        String query = "match (p:orders)--(n) where (n)-[:sold]-(p) return n,p";
        List<String> queries = ExpandCypher.expandCypher(gtopInterface, query);
        Assert.assertEquals(1, queries.size());
    }

    @Test
    public void testAnonymousNotMulti() {
        String query = "match (p:orders)--(n) where not((n)-[:sold]-(p)) return n,p";
        List<String> queries = ExpandCypher.expandCypher(gtopInterface, query);
        Assert.assertEquals(1, queries.size());
    }


    @Test
    public void testMulti() {
        String query = "match (:orders)--(n) return n";
        List<String> queries = ExpandCypher.expandCypher(gtopInterface, query);
        Assert.assertEquals(3, queries.size());
    }

    @Test
    public void testCommaSeperatedWithCommonElement() {
        String query = "match (order:orders)--(n:employees), (order)--(product:products) return order, product";
        List<String> queries = ExpandCypher.expandCypher(gtopInterface, query);
        Assert.assertEquals(1, queries.size());
    }

    @Test
    public void testLargerQuery() {
        String query =
                "MATCH (person:Employees) - [:REPORTS_TO*1..2] - (friend:Employees), "
                        + "(friend) - [:SOLD] - (order:Orders) - [:PURCHASED] - "
                        + "(customer:Customers {companyName:\"White Clover Markets\"}) "
                        + "MATCH (customer) - [:PURCHASED] - (order2:Orders) " + "with distinct order2, order, friend "
                        + "MATCH (order2) - [:PRODUCT] - (commonProduct:Products) "
                        + "RETURN order2 as Order2, order2.orderID, count(commonProduct) as productCount "
                        + "ORDER BY productCount DESC, Order2.orderID";
        List<String> queries = ExpandCypher.expandCypher(gtopInterface, query);
        Assert.assertEquals(2, queries.size());
    }

    @Test
    public void testNotQuery() {
        String query =
                "MATCH (person:Employees {firstName: 'Anne'})-[:REPORTS_TO]-(n:Employees) MATCH (boss:Employees {firstName:'Steven'}) where not(n=boss) return n, boss";
        List<String> queries = ExpandCypher.expandCypher(gtopInterface, query);
        Assert.assertEquals(1, queries.size());
    }


    @Test
    public void testQuery3() throws IOException {
        String path = "src" + File.separatorChar + "test" + File.separatorChar + "resources";
        String jsonInString = FileUtils.readFileToString(new File(path + "/ldbc.gtop"));
        GTopInterfaceImpl gTopInterface = new RelationalGTopInterface(jsonInString);
        String query =
                "MATCH (person:Person {id:2199023259437})-[:KNOWS*1..2]->(friend:Person)<-[:HAS_CREATOR]-(messageX), (messageX)-[:IS_LOCATED_IN]->(countryX:Country) "
                        + "WHERE not(person=friend) AND not((friend)-[:IS_LOCATED_IN]->()-[:IS_PART_OF]->(countryX)) AND countryX.url='http://dbpedia.org/resource/France' AND messageX.creationDate>='1990-7-7' AND messageX.creationDate<'2016-7-7' "
                        + "WITH friend, count(DISTINCT messageX) AS xCount MATCH (friend)<-[:HAS_CREATOR]-(messageY)-[:IS_LOCATED_IN]->(countryY:Country) "
                        + "WHERE countryY.url='http://dbpedia.org/resource/France' AND not((friend)-[:IS_LOCATED_IN]->()-[:IS_PART_OF]->(countryY)) AND messageY.creationDate>='1990-7-7' AND messageY.creationDate<'2016-7-7' "
                        + "WITH friend.id AS friendId, friend.firstName AS friendFirstName, friend.lastName AS friendLastName, xCount, count(DISTINCT messageY) AS yCount "
                        + "RETURN friendId, friendFirstName, friendLastName, xCount, yCount, xCount + yCount AS xyCount ORDER BY xyCount DESC, friendId ASC LIMIT 5";
        List<String> queries = ExpandCypher.expandCypher(gTopInterface, query);
        Assert.assertEquals(8, queries.size());
        for (String q : queries) {
            System.out.println(q);
        }
    }

    @Test
    public void testQuery3v2() throws IOException {
        String path = "src" + File.separatorChar + "test" + File.separatorChar + "resources";
        String jsonInString = FileUtils.readFileToString(new File(path + "/ldbc.gtop"));
        GTopInterfaceImpl gTopInterface = new RelationalGTopInterface(jsonInString);
        String query =
                "MATCH (person:Person {id:2199023259437})-[:KNOWS]->(friend:Person)<-[:HAS_CREATOR]-(messageX:Post), (messageX)-[:IS_LOCATED_IN]->(countryX:Country) "
                        + "WHERE not(person=friend) AND not((friend)-[:IS_LOCATED_IN]->()-[:IS_PART_OF]->(countryX)) AND countryX.url='http://dbpedia.org/resource/France' AND messageX.creationDate>='1990-7-7' AND messageX.creationDate<'2016-7-7' "
                        + "WITH friend, count(DISTINCT messageX) AS xCount MATCH (friend)<-[:HAS_CREATOR]-(messageY)-[:IS_LOCATED_IN]->(countryY:Country) "
                        + "WHERE countryY.url='http://dbpedia.org/resource/France' AND not((friend)-[:IS_LOCATED_IN]->()-[:IS_PART_OF]->(countryY)) AND messageY.creationDate>='1990-7-7' AND messageY.creationDate<'2016-7-7' "
                        + "WITH friend.id AS friendId, friend.firstName AS friendFirstName, friend.lastName AS friendLastName, xCount, count(DISTINCT messageY) AS yCount "
                        + "RETURN friendId, friendFirstName, friendLastName, xCount, yCount, xCount + yCount AS xyCount ORDER BY xyCount DESC, friendId ASC LIMIT 5";
        List<String> queries = ExpandCypher.expandCypher(gTopInterface, query);
        Assert.assertEquals(2, queries.size());
        for (String q : queries) {
            System.out.println(q);
        }
    }

    @Test
    public void testQuery3v3() throws IOException {
        String path = "src" + File.separatorChar + "test" + File.separatorChar + "resources";
        String jsonInString = FileUtils.readFileToString(new File(path + "/ldbc.gtop"));
        GTopInterfaceImpl gTopInterface = new RelationalGTopInterface(jsonInString);
        String query =
                "MATCH (person:Person {id:2199023259437})-[:KNOWS*1]->(friend:Person)<-[:HAS_CREATOR]-(messageX:Post), (messageX)-[:IS_LOCATED_IN]->(countryX:Country) "
                        + "WHERE not(person=friend) AND not((friend)-[:IS_LOCATED_IN]->()-[:IS_PART_OF]->(countryX)) AND countryX.url='http://dbpedia.org/resource/France' AND messageX.creationDate>='1990-7-7' AND messageX.creationDate<'2016-7-7' "
                        + "WITH friend, count(DISTINCT messageX) AS xCount MATCH (friend)<-[:HAS_CREATOR]-(messageY:Post)-[:IS_LOCATED_IN]->(countryY:Country) "
                        + "WHERE countryY.url='http://dbpedia.org/resource/France' AND not((friend)-[:IS_LOCATED_IN]->()-[:IS_PART_OF]->(countryY)) AND messageY.creationDate>='1990-7-7' AND messageY.creationDate<'2016-7-7' "
                        + "WITH friend.id AS friendId, friend.firstName AS friendFirstName, friend.lastName AS friendLastName, xCount, count(DISTINCT messageY) AS yCount "
                        + "RETURN friendId, friendFirstName, friendLastName, xCount, yCount, xCount + yCount AS xyCount ORDER BY xyCount DESC, friendId ASC LIMIT 5";
        List<String> queries = ExpandCypher.expandCypher(gTopInterface, query);
        Assert.assertEquals(1, queries.size());
        for (String q : queries) {
            System.out.println(q);
        }

    }

    @Test
    public void testQuery6() throws IOException {
        String path = "src" + File.separatorChar + "test" + File.separatorChar + "resources";
        String jsonInString = FileUtils.readFileToString(new File(path + "/ldbc.gtop"));
        GTopInterfaceImpl gTopInterface = new RelationalGTopInterface(jsonInString);
        String query = "MATCH (friend:Person)<-[:HAS_CREATOR]-(friendPost:Post)-[:HAS_TAG]->(knownTag:Tag)";
        List<String> queries = ExpandCypher.expandCypher(gTopInterface, query);
        Assert.assertEquals(1, queries.size());
        for (String q : queries) {
            System.out.println(q);
        }
    }

    @Test
    public void testQueryValid() {
        String cypher =
                "MATCH  (person:person {id:2199023259437})-[:knows]->(:person)-[:knows]->(friend:person)<-[:has_creator]-(messageX:comment) ,  (messageX:comment)-[:is_located_in]->(countryX:country)  WHERE not (person=friend:person)  AND not( (friend:person)-[:is_located_in]->(:city)-[:is_part_of]->(countryX:country) ) AND countryX.url='http://dbpedia.org/resource/France' AND messageX.creationDate>='1990-7-7' AND messageX.creationDate<'2016-7-7' WITH friend, count (DISTINCT messageX:company)  AS xCount MATCH  (friend:person)<-[:has_creator]-(messageY:comment)-[:is_located_in]->(countryY:country)  WHERE countryY.url='http://dbpedia.org/resource/France' AND not( (friend:person)-[:is_located_in]->(:city)-[:is_part_of]->(countryY:country) ) AND messageY.creationDate>='1990-7-7' AND messageY.creationDate<'2016-7-7' WITH friend.id AS friendId, friend.firstName AS friendFirstName, friend.lastName AS friendLastName, xCount, count (DISTINCT messageY:company)  AS yCount RETURN friendId, friendFirstName, friendLastName, xCount, yCount, xCount + yCount AS xyCount ORDER BY xyCount DESC, friendId ASC LIMIT 5";
        Assert.assertFalse(ExpandCypher.queryValid(cypher));
    }

}
