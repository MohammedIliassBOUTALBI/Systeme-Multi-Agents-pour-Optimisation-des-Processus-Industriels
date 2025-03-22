package agents;

import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.*;

public class LogistiqueAgent extends Agent {
    private static final long serialVersionUID = 1L;
    private Map<String, Integer> routes;

    protected void setup() {
        System.out.println("Agent Logistique " + getAID().getName() + " est prêt.");
        routes = new HashMap<>();
        
        // Initialiser quelques routes
        initializeRoutes();
        
        // Ajouter le comportement pour optimiser les routes
        addBehaviour(new CyclicBehaviour(this) {
            private static final long serialVersionUID = 1L;

            public void action() {
                MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
                ACLMessage msg = myAgent.receive(mt);
                
                if (msg != null) {
                    // Traiter la demande d'optimisation
                    String content = msg.getContent();
                    String optimizedRoute = optimizeRoute(content);
                    
                    // Répondre avec la route optimisée
                    ACLMessage reply = msg.createReply();
                    reply.setPerformative(ACLMessage.INFORM);
                    reply.setContent(optimizedRoute);
                    send(reply);
                } else {
                    block();
                }
            }
        });
    }

    private void initializeRoutes() {
        // Simuler un réseau de routes avec des distances
        routes.put("A-B", 10);
        routes.put("B-C", 15);
        routes.put("A-C", 30);
    }

    private String optimizeRoute(String request) {
        // Implémenter l'algorithme de Dijkstra ici
        return "Route optimisée: A -> B -> C";
    }

    protected void takeDown() {
        System.out.println("Agent Logistique " + getAID().getName() + " terminé.");
    }
}
