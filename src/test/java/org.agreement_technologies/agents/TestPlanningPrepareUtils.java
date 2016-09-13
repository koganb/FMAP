package org.agreement_technologies.agents;

import org.apache.commons.collections4.CollectionUtils;
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
        return Arrays.asList(new Object[][] {
                { 5, 3 }, { 3, 10 }, { 3, 0 }, { 0, 5 }
        });
    }

    @Test
    public void testAssignGoalsToAgents() {
        Map<Integer, Collection<Integer>> agentsToGoals = PlanningPrepareUtils.assignGoalsToAgents(agentNumber, goalsNumber);
        logger.info("agentsToGoals {}" , agentsToGoals);

        if (agentNumber == 0) {
            assertEquals(agentsToGoals.size(), 0);
        }
        else {

            assertEquals(CollectionUtils.intersection(
                    agentsToGoals.keySet(),
                    PlanningPrepareUtils.createRandomCollectionByNumber(agentNumber)).size(), agentNumber);

            assertEquals(CollectionUtils.intersection(
                    agentsToGoals.values().stream()
                            .flatMap(Collection::stream)
                            .collect(Collectors.toList()),
                    PlanningPrepareUtils.createRandomCollectionByNumber(goalsNumber)).size(), goalsNumber);

            assertEquals(agentsToGoals.keySet().size(), agentNumber);
            agentsToGoals.entrySet().stream().allMatch(
                    s -> s.getValue().size() == goalsNumber / agentNumber ||
                            s.getValue().size() == goalsNumber / agentNumber + 1);


        }
    }
}
