package org.agreement_technologies.common.map_planner;

import org.agreement_technologies.service.map_planner.IPlan;

/**
 * Common interface for a planner
 * @author Alex
 */
public interface Planner {
    /**
     * Computes a solution plan
     */
    IPlan computePlan(long start, long timeoutSeconds);

    int getIterations();

}
