package org.cytosm.pathfinder;

import java.util.ArrayList;
import java.util.List;

import org.cytosm.common.gtop.abstraction.AbstractionEdge;
import org.cytosm.common.gtop.abstraction.AbstractionNode;
import org.cytosm.pathfinder.routeelements.ExpansionElement;



public class BasePathTest {

    public BasePathTest() {
        super();
    }

    /***
     * Checks if the sequence exists in the list
     *
     * @param allPossibilities
     * @param routes
     * @return
     */
    protected boolean checkIfAllPossibilitiesMatch(final List<List<String>> allPossibilities,
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

    protected boolean containsRoute(final List<String> possibility, final List<List<ExpansionElement>> routes) {

        boolean contains = false;
        routeCheck: for (List<ExpansionElement> route : routes) {
            List<String> synonymsList = new ArrayList<>();

            for (ExpansionElement element : route) {
                String synonym;
                if (element.isNode()) {
                    synonym = ((AbstractionNode) element.getEquivalentMaterializedGtop()).getSynonyms().get(0);
                } else {
                    synonym = ((AbstractionEdge) element.getEquivalentMaterializedGtop()).getSynonyms().get(0);
                }

                synonymsList.add(synonym.toLowerCase());
            }

            if (synonymsList.containsAll(possibility) && possibility.containsAll(synonymsList)) {
                contains = true;
                break routeCheck;
            }
        }
        return contains;
    }



}
