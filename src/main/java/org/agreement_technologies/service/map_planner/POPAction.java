package org.agreement_technologies.service.map_planner;

import org.agreement_technologies.common.map_grounding.Action;
import org.agreement_technologies.common.map_planner.Condition;
import org.apache.commons.collections4.ListUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

//Acciones para el POP; sustituye a la antigua clase PDDLAction
public class POPAction {
    private String actionName;
    //private Action operator;
    //private ArrayList<String> params;
    private List<POPPrecEff> precs = new ArrayList<>();
    private List<POPPrecEff> effects = new ArrayList<>();
    private Condition[] precConds;
    private Condition[] effConds;
    //private int minTime;
    private boolean[] effectInVariable;

    public POPAction(String actionName, ArrayList<POPPrecEff> precs, ArrayList<POPPrecEff> effs) {
        int i;
        this.actionName = actionName;
        addPreconditionsAndEffects(precs, effs);
    }

    //Pasamos en el constructor las precondiciones y efectos ya como POPFunctions
    public POPAction(Action act, ArrayList<POPPrecEff> precs, ArrayList<POPPrecEff> effs) {
        int i;
        //this.params = null;
        //this.operator = act;
        if (act != null) {
            this.actionName = act.getOperatorName();
            //if (actionName.equalsIgnoreCase("initial") || actionName.equalsIgnoreCase("final"))
            //	System.out.println("aqui");

            //this.minTime = act.getMinTime();
            //this.params = new ArrayList<String>(act.getParams().length);
            for (i = 0; i < act.getParams().length; i++)
                this.actionName += " " + act.getParams()[i];
            // this.params.add(act.getParams()[i]);
        } else {
            this.actionName = null;
            //this.minTime = -1;
        }

        addPreconditionsAndEffects(precs, effs);
    }

    public void addPreconditionsAndEffects(List<POPPrecEff> preconditions, List<POPPrecEff> effects) {
        addPreconditions(preconditions);
        addEffects(effects);
        addEffectInVariable(preconditions, effects);

    }

    private void addPreconditions(List<POPPrecEff> preconditions) {
        this.precs = Stream.concat(this.precs.stream(), emptyIfNull(preconditions).stream()).
                distinct().
                collect(Collectors.toList());

        this.precConds = this.precs.stream().map(POPPrecEff::getCondition).toArray(Condition[]::new);
    }

    private void addEffects(List<POPPrecEff> effects) {
        this.effects = Stream.concat(this.effects.stream(), emptyIfNull(effects).stream()).
                distinct().
                collect(Collectors.toList());

        this.effConds = this.effects.stream().map(POPPrecEff::getCondition).toArray(Condition[]::new);
    }

    private void addEffectInVariable(List<POPPrecEff> preconditions, List<POPPrecEff> effects) {
        final List<POPPrecEff> finalPreconditions = ListUtils.emptyIfNull(preconditions);
        final List<POPPrecEff> finalEffects = ListUtils.emptyIfNull(effects);

        effectInVariable = new boolean[this.precs.size()];
        IntStream.range(0, this.precs.size()).forEach(
                precIndex -> {
                    if (finalEffects.stream().anyMatch(effect ->
                            finalPreconditions.get(precIndex).getVarCode() == effect.getVarCode())) {
                        effectInVariable[precIndex] = true;
                    }
                }
        );
    }


    public boolean hasEffectInVariable(int index) {
        return effectInVariable[index];
    }

    //public Action getOperator()                 {return this.operator;}
    public String getName() {
        return this.actionName;
    }

    //public int getMinTime()                     {return this.minTime;}
    public void setName(String name) {
        this.actionName = name;
    }

    //public ArrayList<String> getParams()        {return this.params;}
    public List<POPPrecEff> getPrecs() {
        return this.precs;
    }

    public List<POPPrecEff> getEffects() {
        return this.effects;
    }

    public String toString() {
        return this.actionName;
    }

    public Condition[] getPrecConditions() {
        return precConds;
    }

    public Condition[] getEffConditions() {
        return effConds;
    }
}
