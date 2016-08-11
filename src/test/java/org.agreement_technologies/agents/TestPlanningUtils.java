package org.agreement_technologies.agents;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

/**
 * Created by rachel on 8/3/2016.
 */
public class TestPlanningUtils {

    @Test
    public void testGetPlanCombinations() {

        List<Set<Integer>> planCombinations = PlanningUtils.getPlanCombinations(new Integer[]{1, 2, 3, 4, 6, 8, 12, 15}, 4, 5);


        planCombinations = planCombinations.stream().map(Sets::newLinkedHashSet).collect(Collectors.toList());

        assertEquals(Lists.newArrayList(
                Sets.newLinkedHashSet(Arrays.asList(1, 2, 4, 8)),
                Sets.newLinkedHashSet(Arrays.asList(1, 2, 12)),
                Sets.newLinkedHashSet(Arrays.asList(1, 6, 8)),
                Sets.newLinkedHashSet(Arrays.asList(3, 4, 8)),
                Sets.newLinkedHashSet(Arrays.asList(3, 12)),
                Sets.newLinkedHashSet(Collections.singletonList(15))), planCombinations);
    }

    @Test
    public void testGetPlanCombinations2() {
        List<Set<Integer>> planCombinations = PlanningUtils.getPlanCombinations(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30}, 5, 2);
        assertEquals(Lists.newArrayList(
                Sets.newLinkedHashSet(Arrays.asList(1, 30)),
                Sets.newLinkedHashSet(Arrays.asList(2, 29)),
                Sets.newLinkedHashSet(Arrays.asList(3, 28)),
                Sets.newLinkedHashSet(Arrays.asList(4, 27)),
                Sets.newLinkedHashSet(Arrays.asList(5, 26)),
                Sets.newLinkedHashSet(Arrays.asList(6, 25)),
                Sets.newLinkedHashSet(Arrays.asList(7, 24)),
                Sets.newLinkedHashSet(Arrays.asList(23, 8)),
                Sets.newLinkedHashSet(Arrays.asList(22, 9)),
                Sets.newLinkedHashSet(Arrays.asList(21, 10)),
                Sets.newLinkedHashSet(Arrays.asList(20, 11)),
                Sets.newLinkedHashSet(Arrays.asList(19, 12)),
                Sets.newLinkedHashSet(Arrays.asList(18, 13)),
                Sets.newLinkedHashSet(Arrays.asList(17, 14)),
                Sets.newLinkedHashSet(Arrays.asList(16, 15))), planCombinations);

    }


}
