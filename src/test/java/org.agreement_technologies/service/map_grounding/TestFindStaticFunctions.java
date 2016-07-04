package org.agreement_technologies.service.map_grounding;

import org.agreement_technologies.common.map_grounding.GroundedTask;
import org.agreement_technologies.common.map_parser.PDDLParser;
import org.agreement_technologies.common.map_parser.Task;
import org.agreement_technologies.service.map_parser.MAPDDLParserImp;
import org.agreement_technologies.service.map_parser.ParserImp;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;

import static org.mockito.Mockito.mock;

/**
 * Created by rachel on 7/1/2016.
 */


@RunWith(Parameterized.class)
public class TestFindStaticFunctions {

    private String filePath;
    private String [] staticFunctions;

    public TestFindStaticFunctions(String filePath, String[] staticFunctions) {
        this.filePath = filePath;
        this.staticFunctions = staticFunctions;
    }

    @Test
    public void testGetStaticFunctions() throws IOException, ParseException {
        File domainFile = new File(TestFindStaticFunctions.class.getClassLoader().
                getResource(filePath).getFile());

        String domainFilePath = domainFile.getCanonicalPath();
        boolean isMAPDDL = new ParserImp().isMAPDDL(domainFilePath);

        PDDLParser parser = isMAPDDL ? new MAPDDLParserImp() : new ParserImp();
        Task planningTask = parser.parseDomain(domainFilePath);

        GroundingImp g = new GroundingImp(GroundedTask.SAME_OBJECTS_DISABLED);

        //AgentCommunication comm = mock(AgentCommunication.class);

        //function considered static if it is not in effect of any action
        String[] staticFunctions = g.getStaticFunctions(planningTask);

        Assert.assertArrayEquals(staticFunctions, this.staticFunctions);

    }




    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { "Problems/depots/Pfile2/DomainDepotsDepot.pddl",
                        new String[]{"myAgent", "located", "at", "placed","pos"} },
                { "Problems/depots/Pfile2/DomainDepotsTruck.pddl",
                        new String[]{"myAgent", "located", "placed"} }

        });
    }
}
