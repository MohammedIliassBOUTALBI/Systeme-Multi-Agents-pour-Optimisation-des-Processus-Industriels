package gui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import utils.AgentManager;

public class MainGUI extends JFrame {
    private JPanel sidebarPanel;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private Color primaryColor = new Color(0, 108, 105);
    private Color activeButtonColor = new Color(52, 152, 219);
    private Color hoverButtonColor = new Color(41, 128, 185);
    private Color backgroundColor = new Color(236, 240, 241);

    private DashboardPanel dashboardPanel;
    private ScenarioPanel scenarioPanel;
    private AboutPanel aboutPanel;
    private AgentManager agentManager;

    public MainGUI() {
        setTitle("SONATRACH - Système Multi-Agents");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 600));

        // Initialiser l'AgentManager via le singleton
        agentManager = AgentManager.getInstance();

        // Configurer le look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Créer les composants principaux
        setupComponents();

        // Ajouter un listener pour la fermeture propre
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Arrêter les agents avant de fermer
                agentManager.stopAllAgents();
            }
        });
    }

    private void setupComponents() {
        // Panel principal avec BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(backgroundColor);

        // Créer la barre latérale
        createSidebar();
        mainPanel.add(sidebarPanel, BorderLayout.WEST);

        // Créer le panel de contenu avec CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(backgroundColor);

        // Créer et ajouter les panels
        dashboardPanel = new DashboardPanel();
        scenarioPanel = new ScenarioPanel(agentManager);
        aboutPanel = new AboutPanel();

        contentPanel.add(dashboardPanel, "DASHBOARD");
        contentPanel.add(scenarioPanel, "SCENARIOS");
        contentPanel.add(aboutPanel, "ABOUT");

        // Ajouter un padding autour du contenu
        JPanel paddedContent = new JPanel(new BorderLayout());
        paddedContent.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        paddedContent.setBackground(backgroundColor);
        paddedContent.add(contentPanel, BorderLayout.CENTER);

        mainPanel.add(paddedContent, BorderLayout.CENTER);
        add(mainPanel);

        // Afficher le dashboard par défaut
        cardLayout.show(contentPanel, "DASHBOARD");
    }

    private void createSidebar() {
        sidebarPanel = new JPanel();
        sidebarPanel.setPreferredSize(new Dimension(200, 0));
        sidebarPanel.setBackground(primaryColor);
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Logo ou titre
        JLabel titleLabel = new JLabel("SONATRACH SMA");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        sidebarPanel.add(titleLabel);

        // Boutons de navigation
        addSidebarButton("Tableau de Bord", "DASHBOARD", "📊");
        addSidebarButton("Scénarios", "SCENARIOS", "🔄");
        addSidebarButton("À Propos", "ABOUT", "ℹ️");

        // Ajouter un espace flexible
        sidebarPanel.add(Box.createVerticalGlue());

        // Status des agents
        JLabel statusLabel = new JLabel("Agents Actifs: " + agentManager.getActiveAgents().size());
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebarPanel.add(statusLabel);

        // Mettre à jour le statut périodiquement
        new javax.swing.Timer(2000, e -> {
            statusLabel.setText("Agents Actifs: " + agentManager.getActiveAgents().size());
        }).start();
    }

    private void addSidebarButton(String text, String cardName, String icon) {
        JButton button = new JButton(icon + " " + text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(primaryColor);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setMaximumSize(new Dimension(200, 40));
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Style du bouton
        button.setContentAreaFilled(false);
        button.setOpaque(true);

        // Effets de survol
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverButtonColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(primaryColor);
            }
        });

        // Action du bouton
        button.addActionListener(e -> {
            cardLayout.show(contentPanel, cardName);
            // Mettre à jour l'apparence des boutons
            for (Component c : sidebarPanel.getComponents()) {
                if (c instanceof JButton) {
                    c.setBackground(primaryColor);
                }
            }
            button.setBackground(activeButtonColor);
        });

        sidebarPanel.add(button);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 5)));
    }

    public void showScenario(String scenarioName) {
        cardLayout.show(contentPanel, "SCENARIOS");
        scenarioPanel.loadScenario(scenarioName);
    }
}
