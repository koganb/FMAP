package org.agreement_technologies.agents;

import com.google.common.collect.Sets;
import org.agreement_technologies.common.map_grounding.GroundedTask;
import org.agreement_technologies.common.map_heuristic.HeuristicFactory;
import org.agreement_technologies.common.map_negotiation.NegotiationFactory;
import org.agreement_technologies.common.map_parser.AgentList;
import org.agreement_technologies.common.map_parser.PDDLParser;
import org.agreement_technologies.common.map_parser.Task;
import org.agreement_technologies.common.map_planner.Plan;
import org.agreement_technologies.common.map_planner.PlannerFactory;
import org.agreement_technologies.service.map_parser.AgentListImp;
import org.agreement_technologies.service.map_parser.MAPDDLParserImp;
import org.agreement_technologies.service.map_parser.ParserImp;
import org.agreement_technologies.service.map_planner.IPlan;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static org.agreement_technologies.agents.PlanningUtils.filterDuplicatePlans;

/**
 * @author Oscar
 */
public class GUIBootMultiAlg extends JFrame {


    private static final long serialVersionUID = -5039304283931395812L;
    private static final String[] heuristics = {"Breadth", "FF", "DTG", "Landmarks", "Land.Inc."};
    private static final String[] negotiation = {"Cooperative", "Borda voting", "Runoff voting"};
    private static Logger logger = LoggerFactory.getLogger(GUIBootMultiAlg.class);
    //private static final String[] searchMethods = {"Speed", "Balanced", "Quality"};
    private final Object monitor = new Object();
    private final Map<String, Set<Plan>> solutionMap = new ConcurrentHashMap<>();
    private String startDir;    // Start folder for selecting files
    private String qpidHost;
    private int timeout;
    // Variables declaration
    private javax.swing.JButton jButtonAddAgent;
    private javax.swing.JButton jButtonClearAgents;
    private javax.swing.JButton jButtonLoadConfig;
    private javax.swing.JButton jButtonLoadDomain;
    private javax.swing.JButton jButtonLoadProblem;
    private javax.swing.JButton jButtonSaveConfig;
    private javax.swing.JButton jButtonStart;
    //private javax.swing.JButton jButtonBatch;
    private javax.swing.JLabel jLabelAlgorithmType;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    @SuppressWarnings("rawtypes")
    private javax.swing.JList jListAgents;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private JComboBox jComboBoxAlgorithmType;
    private javax.swing.JTextField jTextFieldAgent;
    private javax.swing.JTextField jTextFieldDomain;
    private javax.swing.JTextField jTextFieldProblem;
    @SuppressWarnings("rawtypes")
    private javax.swing.JComboBox heuristicType;
    private javax.swing.JComboBox negotiationType;
    //private javax.swing.JComboBox searchType;
    private javax.swing.JCheckBox sameObjects;
    private javax.swing.JCheckBox trace;
    private javax.swing.JCheckBox anytime;
    private JTextField jTextTimeout;
    private JTextField jTextQpid;

    /**
     * Constructs the GUI for launching agents
     */
    public GUIBootMultiAlg() {
        startDir = null;
        qpidHost = "localhost";
        try {
            Scanner f = new Scanner(new File("configuration/startDir.txt"));
            startDir = f.nextLine();
            if (f.hasNextLine()) {
                qpidHost = f.nextLine();
            }
            f.close();
        } catch (FileNotFoundException e) {
        }
        try {
            if (startDir == null) {
                startDir = new java.io.File(".").getCanonicalPath();
            }
        } catch (Exception e) {
            startDir = "";
        }
        initComponents();
        setSize(590, 405);

        setLocationRelativeTo(null);
    }

    static IntStream revRange(int from, int to) {
        return IntStream.range(from, to).map(i -> to - i + from - 1);
    }

    public void saveStartDir() {
        FileWriter outFile;
        try {
            outFile = new FileWriter("configuration/startDir.txt");
            PrintWriter out = new PrintWriter(outFile);
            out.println(startDir);
            out.println(qpidHost);
            out.close();
        } catch (IOException e) {
        }
    }

    /**
     * This method is called from within the constructor to initialize the form
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void initComponents() {
        jLabelAlgorithmType = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jTextFieldAgent = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextFieldDomain = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextFieldProblem = new javax.swing.JTextField();
        jButtonLoadDomain = new javax.swing.JButton();
        jButtonLoadProblem = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jButtonClearAgents = new javax.swing.JButton();
        //jButtonBatch = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jListAgents = new javax.swing.JList();
        jButtonAddAgent = new javax.swing.JButton();
        jButtonLoadConfig = new javax.swing.JButton();
        jButtonSaveConfig = new javax.swing.JButton();
        jButtonStart = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("FMAP - Multi-Agent Planning");
        setResizable(false);
        getContentPane().setLayout(null);

        JPanel algorithmPanel = new JPanel();
        algorithmPanel.setLayout(null);
        getContentPane().add(algorithmPanel);
        algorithmPanel.setBounds(0, 0, 570, 25);

        jLabelAlgorithmType.setBounds(20, 11, 90, 14);
        jLabelAlgorithmType.setText("Algorithm:");
        algorithmPanel.add(jLabelAlgorithmType);

        jComboBoxAlgorithmType = new JComboBox(
                Arrays.stream(AlgorithmType.values()).map(Enum::name).toArray());

        jComboBoxAlgorithmType.setSelectedIndex(0);
        jComboBoxAlgorithmType.setBounds(130, 8, 100, 17);

        algorithmPanel.add(jComboBoxAlgorithmType);


        JPanel problemDataPanel = new JPanel();
        problemDataPanel.setLayout(null);
        getContentPane().add(problemDataPanel);
        problemDataPanel.setBounds(0, 20, 570, 130);


        jLabel1.setText("Agent's name:");
        problemDataPanel.add(jLabel1);
        jLabel1.setBounds(20, 20, 90, 14);

        problemDataPanel.add(jTextFieldAgent);
        jTextFieldAgent.setBounds(130, 20, 130, 20);

        jLabel2.setText("Domain file:");
        problemDataPanel.add(jLabel2);
        jLabel2.setBounds(20, 56, 110, 14);
        problemDataPanel.add(jTextFieldDomain);
        jTextFieldDomain.setBounds(130, 50, 360, 20);

        jLabel3.setText("Problem file:");
        problemDataPanel.add(jLabel3);
        jLabel3.setBounds(20, 76, 110, 14);
        problemDataPanel.add(jTextFieldProblem);
        jTextFieldProblem.setBounds(130, 76, 360, 20);

        JLabel jLabel4 = new JLabel("Qpid host:");
        problemDataPanel.add(jLabel4);
        jLabel4.setBounds(20, 105, 110, 16);
        /*
         searchType = new JComboBox(searchMethods);
         searchType.setSelectedIndex(1);
         getContentPane().add(searchType);
         searchType.setBounds(130, 105, 150, 18);
         searchType.setEnabled(false);
         */
        jTextQpid = new JTextField(qpidHost);
        problemDataPanel.add(jTextQpid);
        jTextQpid.setBounds(130, 105, 150, 18);
        JLabel jLabel5 = new JLabel("Heuristic function:");
        problemDataPanel.add(jLabel5);
        jLabel5.setBounds(300, 105, 110, 16);
        heuristicType = new JComboBox(heuristics);
        heuristicType.setSelectedIndex(HeuristicFactory.LAND_DTG_NORM);
        problemDataPanel.add(heuristicType);
        heuristicType.setBounds(418, 105, 150, 18);

        /**
         * ***************************************************
         */
        JLabel jLabel6 = new JLabel("Negotiation method:");
        problemDataPanel.add(jLabel6);
        jLabel6.setBounds(290, 20, 120, 14);
        negotiationType = new JComboBox(negotiation);
        negotiationType.setSelectedIndex(NegotiationFactory.COOPERATIVE);
        problemDataPanel.add(negotiationType);
        negotiationType.setBounds(418, 20, 150, 20);

        /**
         * ***************************************************
         */

        jButtonLoadDomain.setText("Load");
        jButtonLoadDomain.setFocusable(false);
        jButtonLoadDomain.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLoadDomainActionPerformed(evt);
            }
        });
        problemDataPanel.add(jButtonLoadDomain);
        jButtonLoadDomain.setBounds(500, 50, 70, 20);

        jButtonLoadProblem.setText("Load");
        jButtonLoadProblem.setFocusable(false);
        jButtonLoadProblem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLoadProblemActionPerformed(evt);
            }
        });
        problemDataPanel.add(jButtonLoadProblem);
        jButtonLoadProblem.setBounds(500, 76, 70, 20);

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel1.setLayout(null);

        jButtonClearAgents.setText("Clear agents");
        jButtonClearAgents.setFocusable(false);
        jButtonClearAgents.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonClearAgentsActionPerformed(evt);
            }
        });
        jPanel1.add(jButtonClearAgents);
        jButtonClearAgents.setBounds(10, 40, 120, 23);

        jListAgents.setModel(new javax.swing.DefaultListModel());
        jListAgents.setFocusable(false);
        jScrollPane1.setViewportView(jListAgents);

        jPanel1.add(jScrollPane1);
        jScrollPane1.setBounds(150, 10, 400, 130);

        jButtonAddAgent.setText("Add agent");
        jButtonAddAgent.setFocusable(false);
        jButtonAddAgent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddAgentActionPerformed(evt);
            }
        });
        jPanel1.add(jButtonAddAgent);
        jButtonAddAgent.setBounds(10, 10, 120, 30);

        jButtonLoadConfig.setText("Load agents");
        jButtonLoadConfig.setFocusable(false);
        jButtonLoadConfig.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLoadConfigActionPerformed(evt);
            }
        });
        jPanel1.add(jButtonLoadConfig);
        jButtonLoadConfig.setBounds(10, 110, 120, 30);

        jButtonSaveConfig.setText("Save agents");
        jButtonSaveConfig.setFocusable(false);
        jButtonSaveConfig.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveConfigActionPerformed(evt);
            }
        });
        jPanel1.add(jButtonSaveConfig);
        jButtonSaveConfig.setBounds(10, 80, 120, 30);

        getContentPane().add(jPanel1);
        int posPanel = 160;
        jPanel1.setBounds(10, posPanel, 568, 150);

        jButtonStart.setText("Start agents");
        jButtonStart.setFocusable(false);
        jButtonStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStartActionPerformed(evt);
            }
        });
        /*
        jButtonBatch.setText("Batch");
        jButtonBatch.setFocusable(false);
        jButtonBatch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                batchTest();
            }
        });
        getContentPane().add(jButtonBatch);
        jButtonBatch.setBounds(16, posPanel + 158, 80, 20);
        */
        anytime = new JCheckBox("Anytime");
        getContentPane().add(anytime);
        anytime.setBounds(13, posPanel + 182, 75, 16);
        anytime.setSelected(false);

        final JLabel jLabel7 = new JLabel("Timeout");
        getContentPane().add(jLabel7);
        jLabel7.setBounds(88, posPanel + 182, 80, 16);
        final JLabel jLabel8 = new JLabel("sec.");
        getContentPane().add(jLabel8);
        jLabel8.setBounds(178, posPanel + 182, 80, 16);
        jTextTimeout = new JTextField(timeout);
        getContentPane().add(jTextTimeout);
        jTextTimeout.setHorizontalAlignment(JTextField.RIGHT);
        jTextTimeout.setText("1800");
        jTextTimeout.setBounds(138, posPanel + 182, 40, 16);
        jLabel7.setVisible(false);
        jTextTimeout.setVisible(false);
        jLabel8.setVisible(false);
        anytime.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent ce) {
                jLabel7.setVisible(anytime.isSelected());
                jTextTimeout.setVisible(anytime.isSelected());
                jLabel8.setVisible(anytime.isSelected());
            }
        });

        sameObjects = new JCheckBox("Same objects filtering");
        getContentPane().add(sameObjects);
        sameObjects.setBounds(420, posPanel + 160, 170, 16);
        sameObjects.setSelected(true);

        trace = new JCheckBox("Planning trace");
        getContentPane().add(trace);
        trace.setBounds(420, posPanel + 180, 170, 16);
        trace.setSelected(true);

        getContentPane().add(jButtonStart);
        jButtonStart.setBounds(220, posPanel + 160, 150, 40);

        pack();
    }

    /**
     * Selects the domain file
     *
     * @param evt Event information
     */
    private void jButtonLoadDomainActionPerformed(java.awt.event.ActionEvent evt) {
        javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser(startDir);
        if (fileChooser.showOpenDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION) {
            startDir = fileChooser.getCurrentDirectory().toString();
            saveStartDir();
            jTextFieldDomain.setText(fileChooser.getSelectedFile().toString());
        }
    }

    /**
     * Selects the problem file
     *
     * @param evt Event information
     */
    private void jButtonLoadProblemActionPerformed(java.awt.event.ActionEvent evt) {
        javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser(startDir);
        if (fileChooser.showOpenDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION) {
            startDir = fileChooser.getCurrentDirectory().toString();
            saveStartDir();
            jTextFieldProblem.setText(fileChooser.getSelectedFile().toString());
        }
    }

    /**
     * Clears the agents list
     *
     * @param evt Event information
     */
    @SuppressWarnings("rawtypes")
    private void jButtonClearAgentsActionPerformed(java.awt.event.ActionEvent evt) {
        javax.swing.DefaultListModel model
                = (javax.swing.DefaultListModel) jListAgents.getModel();
        model.clear();
    }

    /**
     * Adds an agent to the list
     *
     * @param evt Event information
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void jButtonAddAgentActionPerformed(java.awt.event.ActionEvent evt) {
        GUIBootMultiAlg.Agent a = new GUIBootMultiAlg.Agent(jTextFieldAgent.getText(),
                jTextFieldDomain.getText(), jTextFieldProblem.getText());
        if (a.name.equals("")) {
            javax.swing.JOptionPane.showMessageDialog(this, "The agent must have a name",
                    "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        } else {
            javax.swing.DefaultListModel model
                    = (javax.swing.DefaultListModel) jListAgents.getModel();
            model.addElement(a);
        }
    }

    /**
     * Loads the agents list from a file
     *
     * @param evt Event information
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void jButtonLoadConfigActionPerformed(java.awt.event.ActionEvent evt) {
        javax.swing.DefaultListModel model
                = (javax.swing.DefaultListModel) jListAgents.getModel();
        javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser(startDir);
        if (fileChooser.showOpenDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION) {
            startDir = fileChooser.getCurrentDirectory().toString();
            saveStartDir();
            String fileName = fileChooser.getSelectedFile().toString();
            try {
                java.util.Scanner s = new java.util.Scanner(new java.io.File(fileName));
                while (s.hasNext()) {
                    GUIBootMultiAlg.Agent a = new GUIBootMultiAlg.Agent(s.nextLine(), s.nextLine(), s.nextLine());
                    model.addElement(a);
                }
                s.close();
            } catch (Exception e) {
                javax.swing.JOptionPane.showMessageDialog(this, "The file could not be read",
                        "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Saves the list of agents to a file
     *
     * @param evt Event information
     */
    @SuppressWarnings("rawtypes")
    private void jButtonSaveConfigActionPerformed(java.awt.event.ActionEvent evt) {
        javax.swing.DefaultListModel model
                = (javax.swing.DefaultListModel) jListAgents.getModel();
        javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser(startDir);
        if (fileChooser.showSaveDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION) {
            startDir = fileChooser.getCurrentDirectory().toString();
            saveStartDir();
            String fileName = fileChooser.getSelectedFile().toString();
            try {
                java.io.PrintWriter w = new java.io.PrintWriter(fileName);
                for (int i = 0; i < model.size(); i++) {
                    GUIBootMultiAlg.Agent a = (GUIBootMultiAlg.Agent) model.elementAt(i);
                    w.println(a.name);
                    w.println(a.domain);
                    w.println(a.problem);
                }
                w.close();
            } catch (Exception e) {
                javax.swing.JOptionPane.showMessageDialog(this, "The file could not be saved",
                        "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Launches the agents to start the planning process
     *
     * @param evt Event information
     */
    @SuppressWarnings("rawtypes")
    private void jButtonStartActionPerformed(java.awt.event.ActionEvent evt) {
        qpidHost = jTextQpid.getText();
        saveStartDir();
        javax.swing.DefaultListModel model = (javax.swing.DefaultListModel) jListAgents.getModel();
        if (model.size() == 0) {
            logger.error("No agent defined");
            javax.swing.JOptionPane.showMessageDialog(this,
                    "No agents defined",
                    "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }


        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        int x = 0, y = 0;
        int h = heuristicType.getSelectedIndex();
        int n = negotiationType.getSelectedIndex();

        AlgorithmType selectedAlg = jComboBoxAlgorithmType.getSelectedIndex() == -1 ?
                AlgorithmType.FMAP :
                AlgorithmType.values()[jComboBoxAlgorithmType.getSelectedIndex()];

        int algorithmSelectedIndex = jComboBoxAlgorithmType.getSelectedIndex();
        timeout = -1;
        boolean isAnytime = anytime.isSelected();
        try {
            if (isAnytime) {
                timeout = Integer.parseInt(jTextTimeout.getText());
            }
        } catch (NumberFormatException e) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Timeout is not a number",
                    "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }
        int searchPerformance = 1; // Balanced: searchType.getSelectedIndex();
        if (h == HeuristicFactory.LAND_DTG_NORM || h == HeuristicFactory.LAND_DTG_INC) {
            searchPerformance = PlannerFactory.SEARCH_LANDMARKS;
        }
        int sameObjects = this.sameObjects.isSelected() ?
                GroundedTask.SAME_OBJECTS_REP_PARAMS + GroundedTask.SAME_OBJECTS_PREC_EQ_EFF :
                GroundedTask.SAME_OBJECTS_DISABLED;

        AgentList agList = new ParserImp().createEmptyAgentList();
        for (int i = 0; i < model.size(); i++) {
            agList.addAgent(((GUIBootMultiAlg.Agent) model.getElementAt(i)).name.toLowerCase(), "127.0.0.1");
        }
        //parse first agent problem to find global goals
        final Task planningTask;
        try {
            Agent firstAgent = (Agent) model.getElementAt(0);
            boolean isMAPDDL = new ParserImp().isMAPDDL(firstAgent.domain);
            PDDLParser parser = isMAPDDL ? new MAPDDLParserImp() : new ParserImp();
            planningTask = parser.parseDomain(firstAgent.domain);
            parser.parseProblem(firstAgent.problem, planningTask, agList, firstAgent.name);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return;
        }


//            agentIncludeArr.add(new boolean[]{true, true, true, true, false});
//            agentIncludeArr.add(new boolean[]{true, true, true, false, true});


        int finalSearchPerformance = searchPerformance;

        Stack<ImmutablePair<Set<Integer>, Set<Integer>>> unsolvedProblemsStack =
                PlanningPrepareUtils.assignGoalsToAgents(model.size(), planningTask.getAllGoals().length);

        Stack<ImmutablePair<Set<Integer>, Set<Integer>>> solvedProblemsStack = new Stack<>();
        Stack<IPlan> solutionStack = new Stack<>();

        while (!unsolvedProblemsStack.empty()) {
            ImmutablePair<Set<Integer>, Set<Integer>> problem = unsolvedProblemsStack.pop();
            int solutionStackStackPrev = solutionStack.size();

            MAPboot.planningAgents = new ArrayList<>();
            Collection<String> removeAgents = IntStream.range(0, model.size())
                    .filter(i -> !problem.getLeft().contains(i))
                    .mapToObj(i -> ((GUIBootMultiAlg.Agent) model.getElementAt(i)).name.toLowerCase())
                    .collect(Collectors.toSet());
            logger.info("Remove agents {}", removeAgents);

            AgentList agentList = new AgentListImp();
            problem.getLeft().forEach(i -> {
                Agent agent = ((GUIBootMultiAlg.Agent) model.getElementAt(i));
                agentList.addAgent(agent.name.toLowerCase(), "127.0.0.1");
            });
            logger.info("Agent list {}", agentList);

            problem.getLeft().forEach(i -> {
                try {
                    Agent agent = ((GUIBootMultiAlg.Agent) model.getElementAt(i));
                    PlanningAgent ag = new PlanningAgent(agent.name.toLowerCase(), agent.domain, agent.problem,
                            agentList, false, sameObjects, trace.isSelected(), h, finalSearchPerformance, n,
                            isAnytime, 60000, selectedAlg, problem.getRight(), monitor, removeAgents, solutionStack,
                            MAPboot.planningAgents);
                    MAPboot.planningAgents.add(ag);

                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    System.exit(1);
                }
            });




            for (PlanningAgent ag : MAPboot.planningAgents) {
                ag.start();
            }

            synchronized (monitor) {
                try {
                    monitor.wait(10000000);
                    Thread.sleep(500);  //sleep one second to finish commumication
                } catch (InterruptedException e) {
                    logger.info("Got interrupt on monitor");
                }
            }

            //shutdown agents
            for (PlanningAgent ag : MAPboot.planningAgents) {
                ag.interrupt();
                ag.shutdown();
            }
            logger.info("Active threads number: {}", Thread.activeCount());


            if (solutionStack.size() > solutionStackStackPrev) {
                logger.info("Solution found!");
                solvedProblemsStack.push(problem);
            } else if (!unsolvedProblemsStack.empty()) {
                logger.info("merge plan with next unsolved plan");

                ImmutablePair<Set<Integer>, Set<Integer>> nextUnsolvedProblem = unsolvedProblemsStack.pop();

                //merge the problems
                nextUnsolvedProblem.getLeft().addAll(problem.getLeft());
                nextUnsolvedProblem.getRight().addAll(problem.getRight());
                unsolvedProblemsStack.push(nextUnsolvedProblem);

            } else if (!solvedProblemsStack.empty()) {
                logger.info("merge plan with solved plan");

                ImmutablePair<Set<Integer>, Set<Integer>> nextUnsolvedProblem = solvedProblemsStack.pop();

                //merge the problems
                nextUnsolvedProblem.getLeft().addAll(problem.getLeft());
                nextUnsolvedProblem.getRight().addAll(problem.getRight());
                unsolvedProblemsStack.push(nextUnsolvedProblem);

                //remove solution
                solutionStack.pop();

            } else {
                logger.info("No solution found - EXIT");
                System.exit(1);
            }
        }


        if (!solutionStack.empty()) {
            System.out.println("Starting plan merging...");
            logger.info("Solution plan size {}", solutionStack.size());

            PlanningUtils.mergePlans(solutionStack);
        }



        System.out.println("Finishing plan merging...");


        System.exit(0);
//        jButtonStart.setEnabled(false);
//
//        setState(ICONIFIED);

    }

    public enum AlgorithmType {FMAP, MAFS}

    // Initial parameters for an agent
    private class Agent {

        String name;
        String domain, problem;

        Agent(String n, String d, String p) {
            name = n;
            domain = d;
            problem = p;
        }

        @Override
        public String toString() {
            return name + " (" + domain + "," + problem + ")";
        }
    }
}
