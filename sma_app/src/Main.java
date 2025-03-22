import jade.core.Runtime;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import gui.MainGUI;
import utils.AgentManager;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;

public class Main {
    private static AgentContainer mainContainer;
    private static MainGUI gui;
    private static boolean jadeInitialized = false;

    public static void main(String[] args) {
        // Démarrer l'interface graphique
        SwingUtilities.invokeLater(() -> {
            try {
                // Initialiser JADE avant de créer l'interface
                if (initializeJADE()) {
                    gui = new MainGUI();
                    gui.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null,
                        "Erreur lors de l'initialisation de JADE",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                    System.exit(1);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                    "Erreur lors du démarrage: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }

    private static boolean initializeJADE() {
        try {
            Runtime rt = Runtime.instance();
            
            // Créer un profil avec un port différent
            Profile p = new ProfileImpl();
            p.setParameter(Profile.MAIN_HOST, "localhost");
            p.setParameter(Profile.MAIN_PORT, "1098");
            p.setParameter(Profile.GUI, "true");
            
            // Créer le conteneur principal
            mainContainer = rt.createMainContainer(p);
            
            if (mainContainer != null) {
                // Configurer l'AgentManager
                AgentManager.getInstance().setMainContainer(mainContainer);
                
                // Créer et démarrer les agents
                createAndStartAgents();
                jadeInitialized = true;
                return true;
            }
            
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void createAndStartAgents() {
        try {
            AgentManager agentManager = AgentManager.getInstance();

            // Créer les agents
            AgentController maintenanceAgent = mainContainer.createNewAgent(
                "maintenance",
                "agents.MaintenanceAgent",
                null);

            AgentController surveillanceAgent = mainContainer.createNewAgent(
                "surveillance",
                "agents.SurveillanceAgent",
                null);

            AgentController logistiqueAgent = mainContainer.createNewAgent(
                "logistique",
                "agents.LogistiqueAgent",
                null);

            AgentController productionAgent = mainContainer.createNewAgent(
                "production",
                "agents.ProductionAgent",
                null);

            AgentController achatAgent = mainContainer.createNewAgent(
                "achat",
                "agents.AchatAgent",
                null);

            // Enregistrer les agents dans l'AgentManager
            agentManager.registerAgent("maintenance", maintenanceAgent);
            agentManager.registerAgent("surveillance", surveillanceAgent);
            agentManager.registerAgent("logistique", logistiqueAgent);
            agentManager.registerAgent("production", productionAgent);
            agentManager.registerAgent("achat", achatAgent);

            // Démarrer les agents
            maintenanceAgent.start();
            surveillanceAgent.start();
            logistiqueAgent.start();
            productionAgent.start();
            achatAgent.start();

        } catch (StaleProxyException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "Erreur lors du démarrage des agents: " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public static boolean isJadeInitialized() {
        return jadeInitialized;
    }
}
