package agents;

import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ProductionAgent extends Agent {
    private static final long serialVersionUID = 1L;
    private double productionLevel = 75.0;
    private double targetProduction = 80.0;

    protected void setup() {
        System.out.println("Agent de Production " + getAID().getName() + " est prêt.");
        
        // Comportement pour ajuster la production
        addBehaviour(new TickerBehaviour(this, 5000) {
            private static final long serialVersionUID = 1L;
            
            protected void onTick() {
                adjustProduction();
            }
        });

        // Comportement pour recevoir les demandes
        addBehaviour(new CyclicBehaviour(this) {
            private static final long serialVersionUID = 1L;

            public void action() {
                MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
                ACLMessage msg = myAgent.receive(mt);
                
                if (msg != null) {
                    String content = msg.getContent();
                    if (content.startsWith("ADJUST_PRODUCTION")) {
                        try {
                            targetProduction = Double.parseDouble(content.split(":")[1]);
                            ACLMessage reply = msg.createReply();
                            reply.setPerformative(ACLMessage.INFORM);
                            reply.setContent("Production ajustée à " + targetProduction);
                            send(reply);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    block();
                }
            }
        });
    }

    private void adjustProduction() {
        // Simuler l'ajustement de la production
        if (Math.abs(productionLevel - targetProduction) > 0.1) {
            if (productionLevel < targetProduction) {
                productionLevel += 0.5;
            } else {
                productionLevel -= 0.5;
            }
            System.out.println("Production ajustée à " + String.format("%.2f", productionLevel) + "%");
        }
    }

    protected void takeDown() {
        System.out.println("Agent de Production " + getAID().getName() + " terminé.");
    }
}
