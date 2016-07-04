package org.agreement_technologies.common.map_planner;


/**
 * Common interface for plan steps
 * @author Alex
 */
public interface Step {
    String getActionName();
 	Condition[] getPrecs();
 	Condition[] getEffs();
    int getIndex();
    String getAgent();
    int getTimeStep();
    void setTimeStep(int st);
}
