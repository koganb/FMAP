package org.agreement_technologies.service.map_planner.utils;

import org.agreement_technologies.common.map_planner.CausalLink;
import org.agreement_technologies.common.map_planner.Ordering;
import org.agreement_technologies.service.map_planner.POPIncrementalPlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

/**
 * Created by rachel on 7/13/2016.
 */
public class LinearlizationUtils {
    private static Logger logger = LoggerFactory.getLogger(LinearlizationUtils.class);

    public static int[] linearize(POPIncrementalPlan plan) {
        boolean visited[] = new boolean[plan.getNumSteps()];

        return calcPlanWithPredecessors(plan).stream().
                filter(p -> !visited[p.getStep().getIndex()]).
                map(s -> LinearlizationUtils.linearizeSinglePlan(s, visited)).
                reduce(Stream::concat).
                orElse(Stream.empty()).
                mapToInt(Integer::valueOf).toArray();
    }

    private static Stream<Integer> linearizeSinglePlan(POPIncrementalPlan plan, boolean visited[]) {
        Map<Integer, List<Integer>> orderingIndex = createOrderingIndex(plan);
        Map<Integer, POPIncrementalPlan> indexToPlanMap = createIndexToPlanMap(plan);

        return createTotalOrderArr(plan, visited, orderingIndex, indexToPlanMap);
    }

    private static Stream<Integer> createTotalOrderArr(POPIncrementalPlan plan,
                                                       boolean visited[],
                                                       Map<Integer, List<Integer>> orderingIndex,
                                                       Map<Integer, POPIncrementalPlan> indexToPlanMap) {

        int planIndex = plan.getStep().getIndex();
        visited[planIndex] = true;

        Supplier<Stream<POPIncrementalPlan>> allDependentPlans = () -> Stream.concat(
                //all causal links indexes
                Arrays.stream(
                        Optional.ofNullable(plan.getCausalLinks()).orElse(new CausalLink[0])).map(Ordering::getIndex1),

                //all ordering indexes
                Optional.ofNullable(orderingIndex.get(planIndex)).orElse(new ArrayList<>()).stream()).

                //filter out visited
                        filter(i -> !visited[i]).
                //get plan by index
                        map(indexToPlanMap::get);

//        if (logger.isDebugEnabled()) {
//            allDependentPlans.get().forEach(s -> logger.debug("Dependent plan for {} - {}",
//                    plan.getStep().getIndex(), s.getStep().getIndex()));
//        }

        //recursively concat the plan index and force stream evaluation
        List<Integer> integerStream = Stream.concat(
                allDependentPlans.get().flatMap(p -> createTotalOrderArr(p, visited, orderingIndex, indexToPlanMap)),
                Stream.of(planIndex)).
                collect(Collectors.toList());

        return integerStream.stream();


    }

    /**
     * @param plan
     * @return create <Index2> -> <List<Index1>> map for ordering of plan and its predecessors
     */
    public static Map<Integer, List<Integer>> createOrderingIndex(POPIncrementalPlan plan) {
        return calcPlanWithPredecessors(plan).stream().    //get all planes
                flatMap(p -> Optional.ofNullable(p.getOrderings()).orElse(new ArrayList<>()).stream()).   //get all orderings
                collect(Collectors.groupingBy(Ordering::getIndex2,
                mapping(Ordering::getIndex1, toList())));


    }

    public static Map<Integer, POPIncrementalPlan> createIndexToPlanMap(POPIncrementalPlan plan) {
        return calcPlanWithPredecessors(plan).stream().collect(
                Collectors.toMap(x -> x.getStep().getIndex(), x -> x));

    }

    private static List<POPIncrementalPlan> calcPlanWithPredecessors(POPIncrementalPlan plan) {
        List<POPIncrementalPlan> planWithParentsCol = new ArrayList<>();
        while (plan != null) {
            planWithParentsCol.add(plan);
            plan = plan.getFather();
        }
        return planWithParentsCol;
    }


}
