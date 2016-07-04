package org.agreement_technologies.service.map_planner;

import java.util.ArrayList;
import org.agreement_technologies.common.map_planner.CausalLink;
import org.agreement_technologies.common.map_planner.Ordering;
import org.agreement_technologies.common.map_planner.Planner;
import org.agreement_technologies.common.map_planner.Step;
import org.agreement_technologies.service.tools.CustomArrayList;

/**
 *
 * @author Alex
 */
public interface ExtendedPlanner extends Planner {
    CustomArrayList<CausalLink> getTotalCausalLinks();
    Step getInitialStep();
    Step getFinalStep();
    CustomArrayList<Ordering> getTotalOrderings();
    POPIncrementalPlan[] getAntecessors();
    boolean getModifiedCausalLinks();
    boolean getModifiedOrderings();
    public void solveThreat(POPInternalPlan father, boolean isFinalStep);
    
    void setNumCausalLinks(int n);
    void setModifiedCausalLinks(boolean m);
    void setNumOrderings(int n);
    void setModifiedOrderings(boolean m);

    void addCausalLink(CausalLink cl);
    void addOrdering(Ordering o);
    int getNumCausalLinks();
}
