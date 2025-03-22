package scenarios;

import java.util.*;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

public class ScenarioManager {
    public static class Scenario {
        private String name;
        private String description;
        private List<String> steps;
        private Map<String, List<String>> agentActions;

        public Scenario(String name, String description) {
            this.name = name;
            this.description = description;
            this.steps = new ArrayList<>();
            this.agentActions = new HashMap<>();
        }

        public String getName() { return name; }
        public String getDescription() { return description; }
        public List<String> getSteps() { return steps; }
        public Map<String, List<String>> getAgentActions() { return agentActions; }
    }

    private static final Map<String, Scenario> scenarios = new HashMap<>();

    static {
        // Scénario 1: Détection et gestion d'une anomalie
        Scenario anomalyScenario = new Scenario(
            "Détection d'Anomalie",
            "Simulation d'une détection d'anomalie dans le système de production avec réponse coordonnée des agents"
        );
        anomalyScenario.steps.addAll(Arrays.asList(
            "Détection d'une variation anormale de température",
            "Analyse des données par l'agent de surveillance",
            "Notification à l'agent de maintenance",
            "Évaluation du risque",
            "Mise en place des mesures correctives"
        ));
        anomalyScenario.agentActions.put("surveillance", Arrays.asList(
            "Détection de l'anomalie",
            "Analyse des données historiques",
            "Envoi d'alerte prioritaire"
        ));
        anomalyScenario.agentActions.put("maintenance", Arrays.asList(
            "Réception de l'alerte",
            "Diagnostic du problème",
            "Planification de l'intervention",
            "Exécution des réparations"
        ));
        scenarios.put("anomaly", anomalyScenario);

        // Scénario 2: Optimisation de la production
        Scenario productionScenario = new Scenario(
            "Optimisation de Production",
            "Ajustement dynamique des niveaux de production basé sur la demande et les contraintes"
        );
        productionScenario.steps.addAll(Arrays.asList(
            "Analyse de la demande actuelle",
            "Vérification des stocks disponibles",
            "Calcul des capacités de production",
            "Ajustement des paramètres",
            "Surveillance des résultats"
        ));
        productionScenario.agentActions.put("production", Arrays.asList(
            "Analyse des données de production",
            "Calcul des optimisations possibles",
            "Ajustement des paramètres",
            "Surveillance des résultats"
        ));
        productionScenario.agentActions.put("logistique", Arrays.asList(
            "Vérification des stocks",
            "Planification des livraisons",
            "Optimisation des routes"
        ));
        scenarios.put("production", productionScenario);

        // Scénario 3: Gestion des approvisionnements
        Scenario supplyScenario = new Scenario(
            "Gestion des Approvisionnements",
            "Optimisation des achats et de la gestion des stocks avec négociation automatique"
        );
        supplyScenario.steps.addAll(Arrays.asList(
            "Analyse des niveaux de stock",
            "Prévision des besoins",
            "Consultation des fournisseurs",
            "Négociation des prix",
            "Passation des commandes"
        ));
        supplyScenario.agentActions.put("achat", Arrays.asList(
            "Analyse des besoins",
            "Consultation des fournisseurs",
            "Négociation des prix",
            "Passation des commandes"
        ));
        supplyScenario.agentActions.put("logistique", Arrays.asList(
            "Vérification des espaces de stockage",
            "Planification des réceptions",
            "Mise à jour des inventaires"
        ));
        scenarios.put("supply", supplyScenario);

        // Scénario 4: Maintenance Préventive
        Scenario preventiveScenario = new Scenario(
            "Maintenance Préventive",
            "Planification et exécution d'opérations de maintenance préventive basées sur l'analyse prédictive"
        );
        preventiveScenario.steps.addAll(Arrays.asList(
            "Analyse des données historiques",
            "Identification des risques potentiels",
            "Planification des interventions",
            "Allocation des ressources",
            "Exécution des maintenances"
        ));
        preventiveScenario.agentActions.put("maintenance", Arrays.asList(
            "Analyse prédictive",
            "Planification des interventions",
            "Coordination des équipes",
            "Suivi des opérations"
        ));
        preventiveScenario.agentActions.put("production", Arrays.asList(
            "Ajustement des plannings",
            "Adaptation de la production",
            "Validation des arrêts"
        ));
        scenarios.put("preventive", preventiveScenario);

        // Scénario 5: Gestion de Crise
        Scenario crisisScenario = new Scenario(
            "Gestion de Crise",
            "Réponse coordonnée à une situation de crise impliquant tous les agents"
        );
        crisisScenario.steps.addAll(Arrays.asList(
            "Détection de la situation de crise",
            "Évaluation des risques",
            "Activation du protocole d'urgence",
            "Coordination des interventions",
            "Retour à la normale"
        ));
        crisisScenario.agentActions.put("surveillance", Arrays.asList(
            "Détection de la crise",
            "Analyse de la situation",
            "Coordination générale"
        ));
        crisisScenario.agentActions.put("maintenance", Arrays.asList(
            "Intervention d'urgence",
            "Réparations critiques",
            "Sécurisation des installations"
        ));
        crisisScenario.agentActions.put("production", Arrays.asList(
            "Arrêt d'urgence",
            "Sécurisation des processus",
            "Reprise progressive"
        ));
        crisisScenario.agentActions.put("logistique", Arrays.asList(
            "Gestion des évacuations",
            "Support logistique",
            "Coordination des ressources"
        ));
        scenarios.put("crisis", crisisScenario);
    }

    public static Scenario getScenario(String scenarioId) {
        return scenarios.get(scenarioId);
    }

    public static Collection<Scenario> getAllScenarios() {
        return scenarios.values();
    }

    public static ACLMessage createScenarioMessage(String scenarioId, String agentName) {
        Scenario scenario = scenarios.get(scenarioId);
        if (scenario == null || !scenario.agentActions.containsKey(agentName)) {
            return null;
        }

        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.addReceiver(new AID(agentName, AID.ISLOCALNAME));
        msg.setContent("EXECUTE_SCENARIO:" + scenarioId);
        return msg;
    }
}
