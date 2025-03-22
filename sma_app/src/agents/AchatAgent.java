package agents;

import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.*;

public class AchatAgent extends Agent {
    private static final long serialVersionUID = 1L;
    private Map<String, Double> fournisseurs;
    private boolean enNegociation = false;

    protected void setup() {
        System.out.println("Agent d'Achat " + getAID().getName() + " est prêt.");
        
        // Initialiser la liste des fournisseurs
        fournisseurs = new HashMap<>();
        fournisseurs.put("Fournisseur A", 100.0);
        fournisseurs.put("Fournisseur B", 95.0);
        fournisseurs.put("Fournisseur C", 105.0);

        // Comportement pour gérer les demandes d'achat
        addBehaviour(new CyclicBehaviour(this) {
            private static final long serialVersionUID = 1L;

            public void action() {
                MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
                ACLMessage msg = myAgent.receive(mt);
                
                if (msg != null) {
                    String content = msg.getContent();
                    if (content.startsWith("BESOIN_MATERIEL")) {
                        // Démarrer la négociation
                        startNegotiation(content.split(":")[1]);
                        
                        ACLMessage reply = msg.createReply();
                        reply.setPerformative(ACLMessage.INFORM);
                        reply.setContent("Négociation en cours");
                        send(reply);
                    }
                } else {
                    block();
                }
            }
        });

        // Comportement périodique pour mettre à jour les prix
        addBehaviour(new TickerBehaviour(this, 10000) {
            private static final long serialVersionUID = 1L;
            
            protected void onTick() {
                updatePrices();
            }
        });
    }

    private void startNegotiation(String materiel) {
        if (!enNegociation) {
            enNegociation = true;
            System.out.println("Début de la négociation pour: " + materiel);
            
            // Simuler le processus de négociation
            addBehaviour(new SequentialBehaviour() {
                private static final long serialVersionUID = 1L;
                
                public void onStart() {
                    super.onStart();
                    addSubBehaviour(new OneShotBehaviour() {
                        private static final long serialVersionUID = 1L;
                        
                        public void action() {
                            System.out.println("Analyse des prix du marché...");
                        }
                    });
                    
                    addSubBehaviour(new WakerBehaviour(myAgent, 2000) {
                        private static final long serialVersionUID = 1L;
                        
                        protected void onWake() {
                            System.out.println("Consultation des fournisseurs...");
                        }
                    });
                    
                    addSubBehaviour(new WakerBehaviour(myAgent, 4000) {
                        private static final long serialVersionUID = 1L;
                        
                        protected void onWake() {
                            String bestFournisseur = findBestSupplier();
                            System.out.println("Meilleure offre trouvée: " + bestFournisseur);
                            enNegociation = false;
                        }
                    });
                }
            });
        }
    }

    private void updatePrices() {
        Random rand = new Random();
        for (Map.Entry<String, Double> entry : fournisseurs.entrySet()) {
            // Ajuster les prix aléatoirement entre -5% et +5%
            double variation = (rand.nextDouble() - 0.5) * 10;
            double newPrice = entry.getValue() * (1 + variation/100);
            fournisseurs.put(entry.getKey(), newPrice);
        }
    }

    private String findBestSupplier() {
        return fournisseurs.entrySet()
            .stream()
            .min(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("Aucun fournisseur disponible");
    }

    protected void takeDown() {
        System.out.println("Agent d'Achat " + getAID().getName() + " terminé.");
    }
}
