package org.agreement_technologies.common.map_planner;

public interface Condition {
	// Returns the condition type (EQUAL or DISTINCT)
    ConditionType getConditionType();

	// Returns the code of the variable
	int getVarCode();
	
	// Returns the code of the value
	int getValueCode();

    String toKey();

	String labeled(PlannerFactory pf);

    enum ConditionType {

        EQUAL("="),
        DISTINCT("<>");

        private String symbol;

        ConditionType(String symbol) {
            this.symbol = symbol;
        }

        @Override
        public String toString() {
            return symbol;
        }
    }
}
