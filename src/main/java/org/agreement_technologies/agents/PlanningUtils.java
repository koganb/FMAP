package org.agreement_technologies.agents;

import com.google.common.collect.Sets;
import org.agreement_technologies.common.map_planner.Plan;
import org.agreement_technologies.common.map_planner.Step;
import org.agreement_technologies.service.map_planner.POPAction;
import org.agreement_technologies.service.map_planner.POPPrecEff;
import org.agreement_technologies.service.map_planner.POPStep;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.summarizingInt;
import static java.util.stream.Collectors.toList;

/**
 * Created by rachel on 7/27/2016.
 */
public class PlanningUtils {

    private static Logger logger = LoggerFactory.getLogger(PlanningUtils.class);

    public static Set<Plan> filterDuplicatePlans(Set<Plan> plans) {
        /**
         * implements custom equals and hash function for Plan
         */
        class PlanWrapper {
            private Plan plan;
            private List<String> actions;

            public PlanWrapper(Plan plan) {
                assert plan != null;
                this.plan = plan;
                actions = plan.getStepsArray().stream().map(Step::getActionName).collect(Collectors.toList());
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;

                PlanWrapper that = (PlanWrapper) o;

                return actions != null ? actions.equals(that.actions) : that.actions == null;

            }

            @Override
            public int hashCode() {
                return actions != null ? actions.hashCode() : 0;
            }
        }

        return plans.stream().
                map(PlanWrapper::new).        //create stream of wrappers
                collect(Collectors.toSet()).  //remove duplicates using wrapper class equals hashcode functions
                stream().map(p -> p.plan).     //create stream of original objects
                collect(Collectors.toSet());  //back to collection

    }

    public static List<Set<Integer>> getPlanCombinations(Integer[] keys, int goalSize, int agentSize) {
        logger.info("getPlanCombinations keys {}, goalSize {}, agentSize {}", keys, goalSize, agentSize);

        String[] zeroArray = new String[goalSize];
        String[] onesArray = new String[goalSize];

        List<Set<Integer>> allPossibleSums =
                PlanningUtils.findAllSums(keys, 0, new HashSet<>(), (int) Math.pow(2, goalSize) - 1).stream().
                        filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());


        List<Set<Integer>> allPlanCombinations = allPossibleSums.stream().
                //leave only combinations which cover all goals
                        filter(s -> {
                    Arrays.fill(zeroArray, "0");
                    Arrays.fill(onesArray, "1");
                    String[] reduceResult = s.stream().map(i -> String.format("%0" + goalSize + "d",
                            Integer.parseInt(Integer.toBinaryString(i))).split("")).
                            reduce(zeroArray,
                                    (a, b) -> {
                                        for (int i = 0; i < a.length; i++) {
                                            a[i] = Integer.toString(Integer.parseInt(b[i]) + Integer.parseInt(a[i]));
                                        }
                                        return a;
                                    });
                    return Arrays.equals(reduceResult, onesArray);
                }).
                //number of agents should be equal or greater of number of goals
                        filter(s -> s.size() <= agentSize).collect(toList());
        logger.info("All plan combinations {}", allPlanCombinations);
        return allPlanCombinations;

    }

    public static void mergePlans(Collection<Plan> plans) {
        logger.debug("merge plans {}", (Object) plans);

        HashSet<String> initialFinalStepNames = new HashSet<>(Arrays.asList("Initial", "Final"));


        Set<List<Step>> linearizedSteps = new HashSet<>();
        for (Plan plan : plans) {
            List<Step> stepsArray = new ArrayList<>(plan.getStepsArray());
            if (stepsArray.size() > 2) {
                List<Step> steps = new ArrayList<>();
                int[] actionIndexes = plan.linearizePlan(Plan.CoDMAP_CENTRALIZED, null);

                stepsArray.remove(0);
                stepsArray.remove(0);  //remove initial and final steps

                Arrays.stream(Arrays.copyOfRange(actionIndexes, 2, actionIndexes.length)).forEach(
                        i -> steps.add(stepsArray.get(i))
                );

                linearizedSteps.add(steps);
            }

        }

        final List<Step> zipStepList = new ArrayList<>();
        IntStream.range(0,
                linearizedSteps.stream().map(List::size).collect(summarizingInt(Integer::intValue)).getMax()).
                forEach(i -> {
                    zipStepList.addAll(linearizedSteps.stream().filter(stepsList -> i < stepsList.size()).map(stepsList -> stepsList.get(i)).collect(Collectors.toList()));
                });

        logger.debug("zip list {}", zipStepList);

        Step initialStep = mergeInitialFinalSteps(0, "Initial", plans);
        Step finalStep = mergeInitialFinalSteps(1, "Final", plans);

        List<Step> allSteps = ListUtils.union(singletonList(initialStep), zipStepList);
        allSteps.add(finalStep);

        Map<String, String> conditionMap = new HashMap<>();
        List<Step> applicableStepsList = allSteps.stream().filter(step -> {
            if (step.getActionName().equals("Initial") || (ArrayUtils.isNotEmpty(step.getPreconditions()) &&
                    Arrays.stream(step.getPreconditions()).allMatch(
                            precondition -> checkPreconditions(step.getAgent(), precondition, conditionMap)))) {
                Arrays.stream(step.getEffects()).forEach(effect -> {
                    updateEffects(step.getAgent(), effect, conditionMap);
                });
                return true;
            } else {
                return false;
            }
        }).collect(Collectors.toList());

        logger.debug("applicableStepsList {}", applicableStepsList);


        if (applicableStepsList.stream().anyMatch(p -> p.getActionName().equals("Final"))) {
            System.out.println("Solution found");

            Map<String, List<Step>> agentToStepMap = new HashMap<>();
            applicableStepsList.stream().
                    filter(s -> StringUtils.isNotEmpty(s.getAgent())).
                    forEach(
                            s -> {
                                List<Step> steps = agentToStepMap.get(s.getAgent());
                                if (steps == null) {
                                    steps = new ArrayList<>();
                                }
                                steps.add(s);
                                agentToStepMap.put(s.getAgent(), steps);
                            }
                    );

            System.out.println("Printing merged plan");

            Map<String, String> currentConditionMap = new HashMap<>();
            Map<String, String> stepConditionMap = new HashMap<>();

            applicableStepsList.stream().
                    filter(s -> s.getActionName().equals("Initial")).
                    forEach(s -> Arrays.stream(s.getEffects()).forEach(effect ->
                            updateEffects(s.getAgent(), effect, currentConditionMap)));

            stepConditionMap.putAll(currentConditionMap);
            final int[] stageIndex = {0};
            final long numberOfActions = agentToStepMap.values().stream().map(List::size).collect(summarizingInt(Integer::intValue)).getSum();

            int MAX_STEP_SIZE = 20;
            while (agentToStepMap.values().stream().map(List::size).collect(summarizingInt(Integer::intValue)).getMax() > 0 &&
                    stageIndex[0] < MAX_STEP_SIZE) {
                System.out.println("Stage: " + stageIndex[0]);

                agentToStepMap.entrySet().stream().forEach(
                        entry -> {
                            if (entry.getValue().size() > 0 &&
                                    Arrays.stream(entry.getValue().get(0).getPreconditions()).allMatch(
                                            precondition -> checkPreconditions(entry.getKey(), precondition, stepConditionMap))) {
                                Arrays.stream(entry.getValue().get(0).getEffects()).forEach(effect -> {
                                    updateEffects(entry.getKey(), effect, currentConditionMap);
                                });
                                System.out.println(format("Agent: %-13s, Action %s", entry.getKey(), entry.getValue().get(0).getAction()));
                                entry.getValue().remove(0);
                            }
                        }
                );
                stageIndex[0]++;
                stepConditionMap.clear();
                stepConditionMap.putAll(currentConditionMap);
            }

            if (stageIndex[0] == MAX_STEP_SIZE) {
                System.out.println("Plan merging failed!!!");
            } else {
                System.out.println(format("Number of stages:  %s", stageIndex[0]));
                System.out.println(format("Number of actions: %s", numberOfActions));
                System.out.println("==============================================");
            }

        }
    }


    private static boolean checkPreconditions(String agent, POPPrecEff precondition, Map<String, String> conditionMap) {
        if (precondition.getFunction() != null) {
            return ObjectUtils.equals(conditionMap.get(precondition.getFunction().toKey()), precondition.getValue());
        }

        return Objects.equals(
                conditionMap.get(getAgentKey(agent, precondition)),
                Integer.toString(precondition.getCondition().getValueCode()));
    }

    private static void updateEffects(String agent, POPPrecEff effect, Map<String, String> conditionMap) {
        if (effect.getFunction() != null) {
            conditionMap.put(effect.getFunction().toKey(), effect.getValue());
        }
        conditionMap.put(getAgentKey(agent, effect),
                Integer.toString(effect.getCondition().getValueCode()));
    }

    private static String getAgentKey(String agent, POPPrecEff precEff) {
        String agentName = agent == null ? precEff.getEffectedAgent() : agent;
        return agentName + "@" + precEff.getCondition().getVarCode();
    }


    private static Step mergeInitialFinalSteps(int stepIndex, String actionName, Collection<Plan> plans) {
        logger.debug("merging action {}", actionName);
        return plans.stream().
                //filter initial or final steps
                        flatMap(p -> p.getStepsArray().stream().filter(s -> s.getActionName().equals(actionName))).
                        reduce(new POPStep(new POPAction(actionName,
                                        new ArrayList<>(), new ArrayList<>()), stepIndex, null),
                                (a, b) -> {
                                    //add preconditions and effects

                                    logger.debug("adding precondition {}, effects {}", b.getAction().getPrecs(), b.getAction().getEffects());
                                    a.getAction().addPreconditionsAndEffects(
                                            b.getAction().getPrecs(),
                                            b.getAction().getEffects());
                                    return a;
                                });
    }


    static List<Optional<Set<Integer>>> findAllSums(Integer[] arrayOfNumbers, int index, Set<Integer> currentRes, int maxRes) {
        ArrayList<Optional<Set<Integer>>> res = new ArrayList<>();
        if (index == arrayOfNumbers.length) {
            if (currentRes.size() > 0) {
                res.add(Optional.of(currentRes));
            }
            return res;
        }

        if (currentRes.stream().collect(summarizingInt(Integer::intValue)).getSum() > maxRes) {
            res.add(Optional.empty());
            return res;
        }

        Set<Integer> currentResNew = Sets.newHashSet(currentRes);
        currentResNew.add(arrayOfNumbers[index]);
        res.addAll(findAllSums(arrayOfNumbers, index + 1, currentResNew, maxRes));
        res.addAll(findAllSums(arrayOfNumbers, index + 1, new HashSet<>(currentRes), maxRes));

        return res;
    }
}
