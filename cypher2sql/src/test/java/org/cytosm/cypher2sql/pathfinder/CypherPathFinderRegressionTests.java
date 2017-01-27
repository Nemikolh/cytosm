package org.cytosm.pathfinder;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({CanonicalPathTest.class, DirectedAndUndirectedPathTest.class, DirectedPathTest.class,
        HintMatchingTest.class, UndirectedPathTest.class})
public class CypherPathFinderRegressionTests {
}