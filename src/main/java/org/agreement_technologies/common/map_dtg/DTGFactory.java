package org.agreement_technologies.common.map_dtg;

import org.agreement_technologies.common.map_grounding.GroundedTask;
import org.agreement_technologies.common.map_planner.PlannerFactory;

public interface DTGFactory {
	DTGSet create(GroundedTask task);
}
