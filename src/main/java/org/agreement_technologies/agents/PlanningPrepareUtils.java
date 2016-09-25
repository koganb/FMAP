package org.agreement_technologies.agents;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Collections.singletonList;

/**
 * Created by rachel on 9/12/2016.
 */
public class PlanningPrepareUtils {


    static Stack<ImmutablePair<Set<Integer>, Set<Integer>>> assignGoalsToAgents(int agentNumber, int goalNumber) {

        Stack<ImmutablePair<Set<Integer>, Set<Integer>>> agentsToGoals = new Stack<>();

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
                        agentsToGoals.add(new ImmutablePair<>(
                                Sets.newHashSet(agents.get(i)),
                                Sets.newHashSet(finalGoalGroups.get(i))));
                    } else {
                        agentsToGoals.add(new ImmutablePair<>(
                                Sets.newHashSet(agents.get(i)),
                                Sets.newHashSet()));
                    }
                }
        );


        return agentsToGoals;

    }


//    public static Collection<ImmutablePair<String, String>> assignGoalsToAgentsBinary(int agentNumber, int goalNumber) {
//        Collection<ImmutablePair<Collection<Integer>, Collection<Integer>>> goalsToAgents =
//                assignGoalsToAgents(agentNumber, goalNumber);
//
//        return goalsToAgents.stream().map(i  -> new ImmutablePair<>(
//                convertNumbersToBinaryStr(i.getLeft(), agentNumber),
//                convertNumbersToBinaryStr(i.getRight(), goalNumber))).
//                collect(Collectors.toList());
//    }
//
//    private static String convertNumbersToBinaryStr(Collection<Integer> numbers, int totalSize) {
//        if (totalSize <= 0) {
//            return "";
//        }
//        int collectionSum = numbers.stream().map(i -> (int)Math.pow(2, i)).reduce(0, (a, b) -> (a | b));
//        String binaryFormat = String.format("%0" + totalSize + "d", Integer.parseInt(Long.toBinaryString(collectionSum)));
//        return binaryFormat;
//    }

    static List<Integer> createRandomCollectionByNumber(int size) {
        List<Integer> list = IntStream.range(0, size).boxed().collect(Collectors.toList());
        Collections.shuffle(list);
        return list;

    }
}
