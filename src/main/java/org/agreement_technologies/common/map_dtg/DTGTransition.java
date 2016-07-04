package org.agreement_technologies.common.map_dtg;

import java.util.ArrayList;

import org.agreement_technologies.common.map_grounding.GroundedCond;
import org.agreement_technologies.common.map_grounding.GroundedEff;
import org.agreement_technologies.common.map_grounding.GroundedVar;

public interface DTGTransition {
	GroundedVar getVar();

	String getStartValue();

	String getFinalValue();

	ArrayList<GroundedCond> getCommonPreconditions();

	ArrayList<GroundedEff> getCommonEffects();

	ArrayList<String> getAgents();
}
