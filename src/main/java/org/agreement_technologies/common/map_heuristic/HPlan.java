package org.agreement_technologies.common.map_heuristic;

import java.util.ArrayList;
import java.util.HashMap;

import org.agreement_technologies.common.map_planner.Plan;
import org.agreement_technologies.common.map_planner.PlannerFactory;
import org.agreement_technologies.common.map_planner.Step;

public interface HPlan extends Plan {

	int[] linearization();

	HashMap<String, ArrayList<String>> computeMultiState(int totalOrder[], PlannerFactory pf);
	
	HashMap<String, String> computeState(int totalOrder[], PlannerFactory pf);
	
	int[] computeCodeState(int totalOrder[], int numVars);
			
	Step lastAddedStep();
	
}
