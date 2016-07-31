package org.agreement_technologies.common.map_planner;


import org.agreement_technologies.service.map_planner.POPAction;
import org.agreement_technologies.service.map_planner.POPPrecEff;

/**
 * Common interface for plan steps
 * @author Alex
 */
public interface Step {
    String getActionName();
 	Condition[] getPrecs();
 	Condition[] getEffs();
    int getIndex();

    POPAction getAction();

    String getAgent();

    POPPrecEff[] getPreconditions();

    POPPrecEff[] getEffects();


    int getTimeStep();
    void setTimeStep(int st);
}
