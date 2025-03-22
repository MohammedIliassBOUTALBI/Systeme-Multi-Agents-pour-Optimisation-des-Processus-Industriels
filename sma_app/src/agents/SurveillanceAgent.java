package agents;

import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import utils.AgentManager;
import java.util.Random;

public class SurveillanceAgent extends Agent {
    private static final long serialVersionUID = 1L;
    private boolean isMonitoring = false;
    private Random random = new Random();
    private AgentManager agentManager;

    protected void setup() {
        agentManager = AgentManager.getInstance();
        agentManager.notifyScenarioEvent(getLocalName(), "SETUP", "Agent de Surveillance initialisé");

        // Comportement pour la surveillance continue
        addBehaviour(new TickerBehaviour(this, 5000) {
            private static final long serialVersionUID = 1L;

            protected void onTick() {
                if (isMonitoring) {
                    checkSensors();
                }
            }
        });

        // Comportement pour recevoir les commandes de l'interface
        addBehaviour(new CyclicBehaviour(this) {
            private static final long serialVersionUID = 1L;

            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    String content = msg.getContent();
                    processCommand(content);
                } else {
                    block();
                }
            }
        });
    }

    private void processCommand(String command) {
        switch (command) {
            case "START_MONITORING":
                startMonitoring();
                break;
            case "STOP_MONITORING":
                stopMonitoring();
                break;
            case "START_LEAK_SCENARIO":
                startLeakScenario();
                break;
            default:
                agentManager.notifyScenarioEvent(getLocalName(), "UNKNOWN_COMMAND", "Commande inconnue: " + command);
        }
    }

    private void startMonitoring() {
        isMonitoring = true;
        agentManager.notifyScenarioEvent(getLocalName(), "START", "Démarrage de la surveillance");
    }

    private void stopMonitoring() {
        isMonitoring = false;
        agentManager.notifyScenarioEvent(getLocalName(), "STOP", "Arrêt de la surveillance");
    }

    private void checkSensors() {
        // Simuler la lecture des capteurs
        if (random.nextDouble() < 0.3) { // 30% de chance de détecter une anomalie
            String sector = "Secteur-" + (char)('A' + random.nextInt(5));
            double pressure = 50 + random.nextDouble() * 50;
            String anomaly = String.format("Pression anormale détectée dans %s (%.2f bar)", sector, pressure);
            
            agentManager.notifyScenarioEvent(getLocalName(), "ANOMALY", anomaly);
            
            if (pressure > 85) {
                reportCriticalAnomaly(sector, pressure);
            }
        }
    }

    private void startLeakScenario() {
        addBehaviour(new SequentialBehaviour() {
            private static final long serialVersionUID = 1L;

            public void onStart() {
                agentManager.notifyScenarioEvent(getLocalName(), "SCENARIO_START", "Démarrage du scénario de fuite");
            }

            protected void addFirst() {
                // Étape 1: Détection initiale
                addSubBehaviour(new OneShotBehaviour() {
                    private static final long serialVersionUID = 1L;
                    public void action() {
                        agentManager.notifyScenarioEvent(getLocalName(), "DETECTION", 
                            "Détection d'une variation de pression dans le Secteur-A");
                    }
                });

                // Étape 2: Analyse des données
                addSubBehaviour(new WakerBehaviour(myAgent, 2000) {
                    private static final long serialVersionUID = 1L;
                    protected void onWake() {
                        agentManager.notifyScenarioEvent(getLocalName(), "ANALYSIS", 
                            "Analyse des données des capteurs du Secteur-A");
                        reportCriticalAnomaly("Secteur-A", 92.5);
                    }
                });

                // Étape 3: Confirmation de la fuite
                addSubBehaviour(new WakerBehaviour(myAgent, 4000) {
                    private static final long serialVersionUID = 1L;
                    protected void onWake() {
                        agentManager.notifyScenarioEvent(getLocalName(), "CONFIRMATION", 
                            "Fuite confirmée dans le Secteur-A - Niveau critique");
                    }
                });
            }
        });
    }

    private void reportCriticalAnomaly(String sector, double pressure) {
        String alert = String.format("ALERT:CRITICAL:%s:%.2f", sector, pressure);
        agentManager.sendMessage(this, "maintenance", alert);
        agentManager.notifyScenarioEvent(getLocalName(), "ALERT", 
            "Alerte critique envoyée à l'agent de maintenance pour " + sector);
    }

    protected void takeDown() {
        agentManager.notifyScenarioEvent(getLocalName(), "SHUTDOWN", "Agent de Surveillance arrêté");
    }
}
