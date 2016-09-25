package org.agreement_technologies.agents;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by rachel on 9/13/2016.
 */
@RunWith(Parameterized.class)
public class TestPlanningPrepareUtils {

    private static Logger logger = LoggerFactory.getLogger(TestPlanningPrepareUtils.class);

    private int agentNumber;
    private int goalsNumber;

    public TestPlanningPrepareUtils(int agentNumber, int goalsNumber) {
        this.agentNumber = agentNumber;
        this.goalsNumber = goalsNumber;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {5, 3},
                {3, 10},
                {3, 0},
                {0, 5}
        });
    }

    @Test
    public void testAssignGoalsToAgents() {
        Collection<ImmutablePair<Set<Integer>, Set<Integer>>> agentsToGoals = PlanningPrepareUtils.assignGoalsToAgents(agentNumber, goalsNumber);
        logger.info("agentsToGoals {}", agentsToGoals);

        if (agentNumber == 0) {
            assertEquals(agentsToGoals.size(), 0);
        } else {

            assertEquals(CollectionUtils.intersection(
                    agentsToGoals.stream().map(ImmutablePair::getLeft)
                            .flatMap(Collection::stream)
                            .collect(Collectors.toList()),
                    PlanningPrepareUtils.createRandomCollectionByNumber(agentNumber)).size(), agentNumber);

            assertEquals(CollectionUtils.intersection(
                    agentsToGoals.stream().map(ImmutablePair::getRight)
                            .flatMap(Collection::stream)
                            .collect(Collectors.toList()),
                    PlanningPrepareUtils.createRandomCollectionByNumber(goalsNumber)).size(), goalsNumber);

            assertEquals(agentsToGoals.stream().map(ImmutablePair::getLeft)
                    .flatMap(Collection::stream).count(), agentNumber);
            agentsToGoals.stream().map(ImmutablePair::getRight).allMatch(
                    s -> s.size() == goalsNumber / agentNumber ||
                            s.size() == goalsNumber / agentNumber + 1);

        }
    }

//    @Test
//    public void testAssignGoalsToAgentsBinary() {
//        Collection<ImmutablePair<String, String>> agentsToGoalsBinary = PlanningPrepareUtils.assignGoalsToAgentsBinary(agentNumber, goalsNumber);
//        logger.info("agentsToGoals {}", agentsToGoalsBinary);
//
//        int result1 = agentsToGoalsBinary.stream().map(ImmutablePair::getLeft).mapToInt(i -> Integer.parseInt(i, 2)).reduce(0, (a, b) -> a | b);
//        assertEquals( result1, (int) Math.pow(2, agentNumber) - 1);
//
//        int result2 = ( goalsNumber <=0 ) ? 0 :
//                agentsToGoalsBinary.stream().map(ImmutablePair::getRight).
//                        mapToInt(i -> Integer.parseInt(i, 2)).reduce(0, (a, b) -> a | b);
//
//        assertEquals( result2,
//                agentNumber <=0 ? 0 //if agent number is zero the goals are zero as well
//                        :(int) Math.pow(2, goalsNumber) - 1);
//
//    }


}
