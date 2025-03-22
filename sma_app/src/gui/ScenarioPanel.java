package gui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.text.SimpleDateFormat;
import scenarios.ScenarioManager;
import scenarios.ScenarioManager.Scenario;
import utils.AgentManager;

public class ScenarioPanel extends JPanel implements AgentManager.ScenarioEventListener {
    private Color primaryColor = new Color(41, 128, 185);
    private Color successColor = new Color(46, 204, 113);
    private Color warningColor = new Color(230, 126, 34);
    private JTextArea logArea;
    private JPanel visualizationPanel;
    private JComboBox<String> scenarioComboBox;
    private JButton startButton;
    private JButton stopButton;
    private JPanel agentStatusPanel;
    private Map<String, JProgressBar> agentProgress;
    private AgentManager agentManager;
    private javax.swing.Timer simulationTimer;
    private boolean isRunning = false;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

    public ScenarioPanel(AgentManager agentManager) {
        this.agentManager = agentManager;
        this.agentProgress = new HashMap<>();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setupComponents();

        // S'abonner aux événements des agents
        String[] agents = {"surveillance", "maintenance", "production", "logistique", "achat"};
        for (String agent : agents) {
            agentManager.addScenarioEventListener(agent, this);
        }
    }

    private void setupComponents() {
        // Panel de contrôle (haut)
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        controlPanel.setBackground(Color.WHITE);
        controlPanel.setBorder(createStyledBorder("Contrôles"));

        // ComboBox des scénarios
        scenarioComboBox = new JComboBox<>();
        for (Scenario scenario : ScenarioManager.getAllScenarios()) {
            scenarioComboBox.addItem(scenario.getName());
        }
        scenarioComboBox.setPreferredSize(new Dimension(250, 30));
        controlPanel.add(scenarioComboBox);

        // Boutons
        startButton = createStyledButton("Démarrer", successColor);
        stopButton = createStyledButton("Arrêter", warningColor);
        stopButton.setEnabled(false);
        controlPanel.add(startButton);
        controlPanel.add(stopButton);

        // Panel principal (centre) avec GridBagLayout
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;

        // Visualisation (gauche)
        visualizationPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawVisualization(g);
            }
        };
        visualizationPanel.setBackground(Color.WHITE);
        visualizationPanel.setBorder(createStyledBorder("Visualisation"));
        visualizationPanel.setPreferredSize(new Dimension(400, 300));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        mainPanel.add(visualizationPanel, gbc);

        // Status des agents (droite)
        agentStatusPanel = new JPanel();
        agentStatusPanel.setLayout(new BoxLayout(agentStatusPanel, BoxLayout.Y_AXIS));
        agentStatusPanel.setBackground(Color.WHITE);
        agentStatusPanel.setBorder(createStyledBorder("Status des Agents"));
        
        // Créer les barres de progression pour chaque agent
        String[] agents = {"surveillance", "maintenance", "production", "logistique", "achat"};
        for (String agent : agents) {
            addAgentProgressBar(agent);
        }

        JScrollPane statusScrollPane = new JScrollPane(agentStatusPanel);
        statusScrollPane.setPreferredSize(new Dimension(300, 300));
        gbc.gridx = 1;
        gbc.weightx = 0.5;
        mainPanel.add(statusScrollPane, gbc);

        // Logs (bas)
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane logScrollPane = new JScrollPane(logArea);
        logScrollPane.setBorder(createStyledBorder("Logs"));
        logScrollPane.setPreferredSize(new Dimension(0, 200));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weighty = 0.5;
        mainPanel.add(logScrollPane, gbc);

        // Ajouter tous les panels
        add(controlPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);

        // Configurer les actions des boutons
        setupButtonActions();
    }

    private void setupButtonActions() {
        startButton.addActionListener(e -> startScenario());
        stopButton.addActionListener(e -> stopScenario());
    }

    private void startScenario() {
        String selectedScenario = (String) scenarioComboBox.getSelectedItem();
        Scenario scenario = null;
        for (Scenario s : ScenarioManager.getAllScenarios()) {
            if (s.getName().equals(selectedScenario)) {
                scenario = s;
                break;
            }
        }

        if (scenario == null) {
            addLog("Erreur: Scénario non trouvé");
            return;
        }

        isRunning = true;
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
        scenarioComboBox.setEnabled(false);

        // Réinitialiser les progress bars
        for (JProgressBar bar : agentProgress.values()) {
            bar.setValue(0);
        }

        // Démarrer la simulation
        final Scenario finalScenario = scenario;
        simulationTimer = new javax.swing.Timer(1000, new ActionListener() {
            private int step = 0;
            private Map<String, Integer> agentSteps = new HashMap<>();

            @Override
            public void actionPerformed(ActionEvent e) {
                if (step < finalScenario.getSteps().size()) {
                    // Mettre à jour les logs
                    addLog(finalScenario.getSteps().get(step));

                    // Mettre à jour les progress bars des agents
                    for (Map.Entry<String, java.util.List<String>> entry : finalScenario.getAgentActions().entrySet()) {
                        String agent = entry.getKey();
                        java.util.List<String> actions = entry.getValue();
                        
                        int currentStep = agentSteps.getOrDefault(agent, 0);
                        if (currentStep < actions.size()) {
                            addLog("[" + agent + "] " + actions.get(currentStep));
                            int progress = (currentStep + 1) * 100 / actions.size();
                            agentProgress.get(agent).setValue(progress);
                            agentSteps.put(agent, currentStep + 1);
                        }
                    }

                    step++;
                    visualizationPanel.repaint();
                } else {
                    stopScenario();
                }
            }
        });
        simulationTimer.start();
    }

    private void stopScenario() {
        if (simulationTimer != null) {
            simulationTimer.stop();
        }
        isRunning = false;
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        scenarioComboBox.setEnabled(true);
        addLog("Scénario terminé");
    }

    private void addAgentProgressBar(String agentName) {
        JPanel agentPanel = new JPanel();
        agentPanel.setLayout(new BoxLayout(agentPanel, BoxLayout.Y_AXIS));
        agentPanel.setBackground(Color.WHITE);
        agentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        agentPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JLabel nameLabel = new JLabel(capitalizeFirst(agentName));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(250, 20));
        progressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        progressBar.setAlignmentX(Component.LEFT_ALIGNMENT);

        agentPanel.add(nameLabel);
        agentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        agentPanel.add(progressBar);

        agentStatusPanel.add(agentPanel);
        agentProgress.put(agentName, progressBar);
    }

    private void addLog(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(timeFormat.format(new Date()) + " - " + message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    private void drawVisualization(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = visualizationPanel.getWidth();
        int height = visualizationPanel.getHeight();
        int centerX = width / 2;
        int centerY = height / 2;
        int radius = Math.min(width, height) / 4;

        // Dessiner les connexions entre les agents
        g2d.setStroke(new BasicStroke(2));
        g2d.setColor(new Color(200, 200, 200));

        // Calculer les positions des agents
        Map<String, Point> agentPositions = new HashMap<>();
        String[] agents = {"surveillance", "maintenance", "production", "logistique", "achat"};
        for (int i = 0; i < agents.length; i++) {
            double angle = 2 * Math.PI * i / agents.length - Math.PI / 2;
            int x = centerX + (int) (radius * Math.cos(angle));
            int y = centerY + (int) (radius * Math.sin(angle));
            agentPositions.put(agents[i], new Point(x, y));

            // Dessiner les connexions
            for (int j = 0; j < i; j++) {
                Point p1 = agentPositions.get(agents[j]);
                g2d.drawLine(x, y, p1.x, p1.y);
            }
        }

        // Dessiner les agents
        for (Map.Entry<String, Point> entry : agentPositions.entrySet()) {
            String agent = entry.getKey();
            Point pos = entry.getValue();
            
            // Cercle de l'agent
            int progress = agentProgress.get(agent).getValue();
            Color agentColor = new Color(
                primaryColor.getRed(),
                primaryColor.getGreen(),
                primaryColor.getBlue(),
                128 + (127 * progress / 100)
            );
            
            g2d.setColor(agentColor);
            g2d.fillOval(pos.x - 30, pos.y - 30, 60, 60);
            g2d.setColor(primaryColor);
            g2d.drawOval(pos.x - 30, pos.y - 30, 60, 60);

            // Nom de l'agent
            g2d.setColor(Color.BLACK);
            String name = capitalizeFirst(agent);
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(name);
            g2d.drawString(name, pos.x - textWidth/2, pos.y + 5);
        }
    }

    private String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(100, 30));
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.darker());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });
        return button;
    }

    private TitledBorder createStyledBorder(String title) {
        TitledBorder border = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(primaryColor, 1),
            title
        );
        border.setTitleFont(new Font("Segoe UI", Font.BOLD, 14));
        border.setTitleColor(primaryColor);
        return border;
    }

    public void loadScenario(String scenarioName) {
        scenarioComboBox.setSelectedItem(scenarioName);
    }

    @Override
    public void onScenarioEvent(String agentName, String eventType, String message) {
        addLog("[" + agentName + "] " + eventType + ": " + message);
        
        // Mettre à jour la visualisation si nécessaire
        if (isRunning) {
            visualizationPanel.repaint();
        }
    }
}
