package org.agreement_technologies.agents;

import org.agreement_technologies.agents.GUIBootMultiAlg.AlgorithmType;
import org.agreement_technologies.common.map_communication.AgentCommunication;
import org.agreement_technologies.common.map_communication.PlanningAgentListener;
import org.agreement_technologies.common.map_grounding.GroundedTask;
import org.agreement_technologies.common.map_landmarks.Landmarks;
import org.agreement_technologies.common.map_parser.AgentList;
import org.agreement_technologies.common.map_planner.Plan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author Oscar Sapena Planning agent
 */
public class PlanningAgent extends Thread implements AgentListener {

    protected PlanningAlgorithm alg;            // Plannning algorithm

    protected String name;                              // Agent name
    protected int timeout;

    private Object monitor;
    /**
     * Constructor of a planning agent
     *
     * @param domain            Domain filename
     * @param problem           Problem filename
     * @param name              Agent name
     * @param sameObjects       Same object enabled
     * @param traceOn           Activate trace
     * @param h                 Heuristic
     * @param searchPerformance Search type
     * @param negotiation       Negotiation method
     * @param isAnytime         Anytime behaviour
     * @param timeout           Timeout
     * @param monitor
     * @param removeAgents
     * @param solutionMap
     * @param planningAgents
     * @throws Exception Platform error
     */
    public PlanningAgent(String name, String domainFile, String problemFile, AgentList agList,
                         boolean waitSynch, int sameObjects, boolean traceOn, int h, int searchPerformance,
                         int negotiation, boolean isAnytime, int timeout, AlgorithmType algorithmType, int goalIndex,
                         Object monitor, Collection<String> removeAgents, Map<Integer, Set<Plan>> solutionMap,
                         ArrayList<PlanningAgent> planningAgents) throws IOException {
        this.name = name.toLowerCase();
        this.monitor = monitor;
        if (isAnytime) {
            this.timeout = timeout;
        } else {
            this.timeout = -1;
        }
        alg = new PlanningAlgorithm(name, domainFile, problemFile, agList, waitSynch,
                sameObjects, traceOn, h, searchPerformance, negotiation, isAnytime,
                algorithmType, goalIndex, removeAgents, solutionMap, planningAgents);
    }

    /**
     * Execution code for the planning agent
     */
    @Override
    public void run() {
        alg.execute(timeout);

        synchronized (monitor) {
            monitor.notify();
        }
    }

    /**
     * Retrieves the agent name (without the suffix)
     *
     * @return Agent name
     */
    @Override
    public String getShortName() {
        return this.name;
    }

    /**
     * Sets the agent status listener
     *
     * @param paListener Planning agent listener
     */
    @Override
    public void setAgentListener(PlanningAgentListener paListener) {
        alg.paListener = paListener;
    }

    @Override
    public void selectPlan(String planName) {
        if (alg.paListener != null) {
            alg.paListener.selectPlan(planName);
        }
    }

    /**
     * Returns the grounded planning task
     */
    @Override
    public GroundedTask getGroundedTask() {
        return alg.groundedTask;
    }

    /**
     * Returns the agent communication utility
     */
    @Override
    public AgentCommunication getCommunication() {
        return alg.comm;
    }

    public boolean isFinished() {
        return alg.status == PlanningAlgorithm.STATUS_IDLE
                || alg.status == PlanningAlgorithm.STATUS_ERROR;
    }

    public boolean isError() {
        return alg.status == PlanningAlgorithm.STATUS_ERROR;
    }

    public void setTimeout(int t) {    // Time in seconds
        timeout = t;
    }

    @Override
    public Landmarks getLandmarks() {
        return alg.landmarks;
    }

    public PlanningAlgorithm getAlg() {
        return alg;
    }

    void shutdown() {
        try {
            if (alg.comm != null)
                alg.comm.close();
        } catch (Exception e) {
        }
    }
}
