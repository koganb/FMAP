package org.agreement_technologies.service.map_planner;

import org.agreement_technologies.common.map_planner.Condition;
import org.agreement_technologies.common.map_planner.Condition.ConditionType;

/**
 * Function-value tuple that defines preconditions and effects in a POP; implements the PreconditionEffect interface.
 * Parameters: reference to the original grounded condition, variable, current value, condition (equal o distinct).
 * @author Alex
 */
public class POPPrecEff {
    static final boolean IS_PREC = true;
    static final boolean IS_EFF  = false;
    private Condition condition;
    private POPFunction function;
    private String value;
    private ConditionType conditionType;
    private String key;
    //private int minTime;
    //private ArrayList<String> agents;
    //private int producers;
    private int index;
    private String effectedAgent;

    public POPPrecEff(Condition cond, POPFunction var, String val, ConditionType co) {
        //, int mt, ArrayList<String> ag, int prod) {
        //this.minTime = var.getVariable().getMinTime(val);
        this.condition = cond;
        this.conditionType = co;
        this.function = var;
        this.value = val;
        //this.minTime = mt;
        //this.agents = ag;
        //this.producers = prod;
        this.key = null;
        this.key = this.toKey();
    }

    public String getEffectedAgent() {
        return effectedAgent;
    }

    public void setEffectedAgent(String effectedAgent) {
        this.effectedAgent = effectedAgent;
    }

	public int getVarCode() 							{return condition.getVarCode();} 
    public int getValueCode() 							{return condition.getValueCode();} 

    public int getIndex()                               {return this.index;}

    public void setIndex(int i) {
        this.index = i;
    }

    public String getValue()                            {return this.value;}

    public ConditionType getType() {
        return this.conditionType;
    }

    //public ArrayList<String> getAgents()                {return agents;}
    //public void setAgents(ArrayList<String> agents)     {this.agents = agents;}
    public Condition getCondition()          			{return this.condition;}

    //public int getMinTime()                             {return this.minTime;}
    //public int getProducers()                           {return this.producers;}
    public void setGroundedCondition(Condition gc)   	{this.condition = gc;}

    public String toKey() {
        if (this.key == null) {
            return "" + condition.getVarCode() + this.conditionType + condition.getValueCode();
        } else return this.key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        POPPrecEff that = (POPPrecEff) o;

        if (function != null ? !function.toKey().equals(that.function.toKey()) : that.function != null) {
            return false;
        }
        if (key != null ? !key.equals(that.key) : that.key != null) {
            return false;
        }
        return effectedAgent != null ? effectedAgent.equals(that.effectedAgent) : that.effectedAgent == null;

    }

    @Override
    public int hashCode() {
        int result = function != null ? function.toKey().hashCode() : 0;
        result = 31 * result + (key != null ? key.hashCode() : 0);
        result = 31 * result + (effectedAgent != null ? effectedAgent.hashCode() : 0);
        return result;
    }

/*
    public String[] getAgentsArray() {
        int i;
        String[] ag = new String[this.agents.size()];

        for(i = 0; i < this.agents.size(); i++)
            ag[i] = this.agents.get(i);

        return ag;
    }*/

    public String toString() {
        return String.format("%s %s %s", function, conditionType, this.getValue());
    }

    public POPFunction getFunction() {
		return function;
	}

    //public void setProducers(int producers)             {this.producers = producers;}
    //public POPFunction getFunction()                    {return this.function;}
    public void setFunction(POPFunction v) {
        this.function = v;
    }
}
