package org.agreement_technologies.agents;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by rachel on 9/12/2016.
 */
public class PlanningPrepareUtils {


    public static Map<Integer, Collection<Integer>> assignGoalsToAgents(int agentNumber, int goalNumber) {

        Map<Integer, Collection<Integer>> agentsToGoals = new HashMap<>();

        if (agentNumber < 1) {
            return agentsToGoals;
        }

        List<Integer> agents = createRandomCollectionByNumber(agentNumber);
        List<Integer> goals = createRandomCollectionByNumber(goalNumber);

        int numberOfGoalsFlour = goalNumber / agentNumber;
        int numberOfAgentsWithGoalCel = goalNumber % agentNumber;

        int numberOfGoalsCel = numberOfGoalsFlour + 1;
        int numberOfAgentsWithGoalFlour = agentNumber - numberOfAgentsWithGoalCel;

        List<List<Integer>> goalGroups = new ArrayList<>();

        if (numberOfGoalsCel > 0) {
            goalGroups = ListUtils.union(
                    new ArrayList<>(Lists.partition(
                            new ArrayList<>(goals.subList(numberOfGoalsFlour * numberOfAgentsWithGoalFlour, goals.size())),
                            numberOfGoalsCel)), goalGroups);


        }


        if (numberOfGoalsFlour > 0) {
            goalGroups = ListUtils.union(
                    new ArrayList<>(Lists.partition(new ArrayList<>(goals.subList(0, numberOfGoalsFlour * numberOfAgentsWithGoalFlour)), numberOfGoalsFlour)),
                    goalGroups);
        }


        List<List<Integer>> finalGoalGroups = goalGroups;
        IntStream.range(0, agentNumber).forEach(
                i -> {
                    if (i < finalGoalGroups.size()) {
                        agentsToGoals.put(agents.get(i), finalGoalGroups.get(i));
                    } else {
                        agentsToGoals.put(agents.get(i), new ArrayList<>());
                    }
                }
        );


        return agentsToGoals;

    }

    static List<Integer> createRandomCollectionByNumber(int size) {
        List<Integer> list = IntStream.range(0, size).boxed().collect(Collectors.toList());
        Collections.shuffle(list);
        return list;

    }
}
