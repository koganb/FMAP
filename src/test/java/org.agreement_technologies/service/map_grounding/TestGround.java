package org.agreement_technologies.service.map_grounding;

import org.agreement_technologies.common.map_communication.AgentCommunication;
import org.agreement_technologies.common.map_grounding.GroundedTask;
import org.agreement_technologies.common.map_grounding.ReachedValue;
import org.agreement_technologies.common.map_parser.AgentList;
import org.agreement_technologies.common.map_parser.PDDLParser;
import org.agreement_technologies.common.map_parser.Task;
import org.agreement_technologies.service.map_parser.AgentListImp;
import org.agreement_technologies.service.map_parser.MAPDDLParserImp;
import org.agreement_technologies.service.map_parser.ParserImp;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by rachel on 7/4/2016.
 */
public class TestGround {

    private static final Logger logger = LoggerFactory.getLogger(TestGround.class);

    private static GroundingImp g;
    private static Task planningTask;
    private static boolean isMAPDDL;

    static {
        System.setProperty("log4j2.enable.threadlocals", "false");
    }

    @BeforeClass
    public static void init() throws IOException, ParseException {
        File domainFile = new File(TestFindStaticFunctions.class.getClassLoader().
                getResource("Problems/depots/Pfile1/DomainDepotsDepot.pddl").getFile());
        File problemFile = new File(TestFindStaticFunctions.class.getClassLoader().
                getResource("Problems/depots/Pfile1/ProblemDepotsdepot0.pddl").getFile());


        String domainFilePath = domainFile.getCanonicalPath();
        isMAPDDL = new ParserImp().isMAPDDL(domainFilePath);

        PDDLParser parser = isMAPDDL ? new MAPDDLParserImp() : new ParserImp();
        planningTask =  parser.parseDomain(domainFilePath);

        AgentList agentList = new AgentListImp();
        agentList.addAgent("depot0", "127.0.0.1");
        String problemFilePath = problemFile.getCanonicalPath();
        parser.parseProblem(problemFilePath, planningTask, agentList, "c");

        g = new GroundingImp(GroundedTask.SAME_OBJECTS_DISABLED);
        AgentCommunication comm = mock(AgentCommunication.class);
        when(comm.numAgents()).thenReturn(2);  //two agents
        when(comm.batonAgent()).thenReturn(true);
        when(comm.getOtherAgents()).thenReturn(
                new ArrayList<>(Collections.singletonList("OtherAgent")));
        when(comm.receiveMessage(eq("OtherAgent"), anyBoolean())).
                thenReturn(new String[]{"clear", "staticfun2"}).
                thenReturn("yes");

        g.computeStaticFunctions(planningTask, comm);

    }

    @Test
    public void testComputeStaticFunctions() {

        //get other agent static functions and check them
        Assert.assertArrayEquals(
                g.getStaticFunctions().toArray(new String[0]),
                new String[] {"staticfun2"});
    }


    @Test
    public void testGrounding() {
        AgentCommunication comm = mock(AgentCommunication.class);
        when(comm.getThisAgentName()).thenReturn("depot0");
        when(comm.numAgents()).thenReturn(2);
        GroundedTaskImp groundedTask = (GroundedTaskImp) g.ground(planningTask, "depot0", isMAPDDL);
        ReachedValue[] newValues = groundedTask.getNewValues();
        logger.debug("New values: {}", (Object) newValues);
        assertEquals(newValues.length, 26);

    }

}
