package org.agreement_technologies.agents;

import org.agreement_technologies.common.map_planner.Plan;
import org.agreement_technologies.common.map_planner.Step;
import org.agreement_technologies.service.map_planner.POPAction;
import org.agreement_technologies.service.map_planner.POPStep;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.summarizingInt;

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


    public static void mergePlans(Plan... plans) {
        logger.debug("merge plans {}", (Object) plans);

        HashSet<String> initialFinalStepNames = new HashSet<>(Arrays.asList("Initial", "Final"));
        final List<Step> zipStepList = new ArrayList<>();
        IntStream.range(0,
                Arrays.stream(plans).map(p -> p.getStepsArray().size()).collect(summarizingInt(Integer::intValue)).getMax()).
                forEach(i -> {
                    for (Plan plan : plans) {
                        if (i < plan.getStepsArray().size() &&
                                !initialFinalStepNames.contains(plan.getStepsArray().get(i).getActionName())) {
                            zipStepList.add(plan.getStepsArray().get(i));
                        }
                    }
                });

        logger.debug("zip list {}", zipStepList);

        Step initialStep = mergeInitialFinalSteps(0, "Initial", plans);
        Step finalStep = mergeInitialFinalSteps(1, "Final", plans);

        List<Step> allSteps = ListUtils.union(singletonList(initialStep), zipStepList);
        allSteps.add(finalStep);

        Map<String, String> conditionMap = new HashMap<>();
        List<Step> applicableStepsList = allSteps.stream().filter(step -> {
            if (step.getActionName().equals("Initial") ||
                    Arrays.stream(step.getPreconditions()).allMatch(
                            precondition -> Objects.equals(conditionMap.get(precondition.getFunction().toKey()), precondition.getValue()))) {
                Arrays.stream(step.getEffects()).forEach(effect ->
                        conditionMap.put(effect.getFunction().toKey(), effect.getValue()));
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
                            currentConditionMap.put(effect.getFunction().toKey(), effect.getValue())));

            stepConditionMap.putAll(currentConditionMap);
            final int[] stageIndex = {0};
            final long numberOfActions = agentToStepMap.values().stream().map(List::size).collect(summarizingInt(Integer::intValue)).getSum();

            while (agentToStepMap.values().stream().map(List::size).collect(summarizingInt(Integer::intValue)).getMax() > 0) {
                System.out.println("Stage: " + stageIndex[0]);

                agentToStepMap.entrySet().stream().forEach(
                        entry -> {
                            if (entry.getValue().size() > 0 &&
                                    Arrays.stream(entry.getValue().get(0).getPreconditions()).allMatch(
                                            precondition -> Objects.equals(stepConditionMap.get(precondition.getFunction().toKey()), precondition.getValue()))) {
                                Arrays.stream(entry.getValue().get(0).getEffects()).forEach(effect ->
                                        currentConditionMap.put(effect.getFunction().toKey(), effect.getValue()));

                                System.out.println(format("Agent: %-13s, Action %s", entry.getKey(), entry.getValue().get(0).getAction()));
                                entry.getValue().remove(0);
                            }
                        }
                );
                stageIndex[0]++;
                stepConditionMap.clear();
                stepConditionMap.putAll(currentConditionMap);
            }
            System.out.println(format("Number of stages:  %s", --stageIndex[0]));
            System.out.println(format("Number of actions: %s", numberOfActions));
            System.out.println("==============================================");

        }
    }

    private static Step mergeInitialFinalSteps(int stepIndex, String actionName, Plan... plans) {
        return Arrays.stream(plans).
                //filter initial or final steps
                        flatMap(p -> p.getStepsArray().stream().filter(s -> s.getActionName().equals(actionName))).
                        reduce(new POPStep(new POPAction(actionName,
                                        new ArrayList<>(), new ArrayList<>()), stepIndex, null),
                                (a, b) -> {
                                    //add preconditions and effects
                                    a.getAction().addPreconditionsAndEffects(
                                            b.getAction().getPrecs(),
                                            b.getAction().getEffects());
                                    return a;
                                });
    }

}
