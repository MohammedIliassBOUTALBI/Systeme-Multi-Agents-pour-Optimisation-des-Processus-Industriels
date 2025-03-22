package agents;

import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class MaintenanceAgent extends Agent {
    private static final long serialVersionUID = 1L;

    protected void setup() {
        System.out.println("Agent de Maintenance " + getAID().getName() + " est prêt.");
        
        // Ajouter le comportement pour gérer les alertes de maintenance
        addBehaviour(new CyclicBehaviour(this) {
            private static final long serialVersionUID = 1L;

            public void action() {
                MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
                ACLMessage msg = myAgent.receive(mt);
                
                if (msg != null) {
                    // Message reçu. Traiter ici
                    String content = msg.getContent();
                    System.out.println("Agent de Maintenance: Nouvelle alerte reçue: " + content);
                    
                    // Évaluer la criticité
                    evaluateCriticality(content);
                    
                    // Envoyer une confirmation
                    ACLMessage reply = msg.createReply();
                    reply.setPerformative(ACLMessage.CONFIRM);
                    reply.setContent("Maintenance planifiée");
                    send(reply);
                } else {
                    block();
                }
            }
        });
    }

    private void evaluateCriticality(String alert) {
        // Implémenter l'algorithme d'évaluation de la criticité
        System.out.println("Évaluation de la criticité de l'alerte: " + alert);
    }

    protected void takeDown() {
        System.out.println("Agent de Maintenance " + getAID().getName() + " terminé.");
    }
}
