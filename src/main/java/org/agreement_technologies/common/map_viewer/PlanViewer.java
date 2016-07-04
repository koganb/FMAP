package org.agreement_technologies.common.map_viewer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import org.agreement_technologies.common.map_planner.Plan;
import org.agreement_technologies.common.map_planner.PlannerFactory;

public interface PlanViewer {

	void setBackground(Color bgColor);

	void setPreferredSize(Dimension dimension);

	Component getComponent();

	void showPlan(Plan plan, PlannerFactory pf);

	int getMakespan();
}
