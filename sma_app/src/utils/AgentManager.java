package utils;

import jade.core.Runtime;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import jade.core.Agent;

public class AgentManager {
    private static AgentManager instance;
    private AgentContainer mainContainer;
    private Map<String, AgentController> agents;
    private Map<String, List<ScenarioEventListener>> eventListeners;
    
    public interface ScenarioEventListener {
        void onScenarioEvent(String agentName, String eventType, String message);
    }

    public static AgentManager getInstance() {
        if (instance == null) {
            instance = new AgentManager();
        }
        return instance;
    }

    public AgentManager() {
        agents = new ConcurrentHashMap<>();
        eventListeners = new ConcurrentHashMap<>();
    }

    public void setMainContainer(AgentContainer container) {
        this.mainContainer = container;
    }

    public void registerAgent(String name, AgentController controller) {
        agents.put(name, controller);
    }

    public void stopAllAgents() {
        for (AgentController agent : agents.values()) {
            try {
                if (agent != null) {
                    agent.kill();
                }
            } catch (StaleProxyException e) {
                e.printStackTrace();
            }
        }
        agents.clear();
    }

    public void notifyScenarioEvent(String agentName, String eventType, String message) {
        System.out.println("[" + agentName + "] " + eventType + ": " + message);
        if (eventListeners.containsKey(agentName)) {
            for (ScenarioEventListener listener : eventListeners.get(agentName)) {
                listener.onScenarioEvent(agentName, eventType, message);
            }
        }
    }

    public void addScenarioEventListener(String agentName, ScenarioEventListener listener) {
        eventListeners.computeIfAbsent(agentName, k -> new ArrayList<>()).add(listener);
    }

    public void removeScenarioEventListener(String agentName, ScenarioEventListener listener) {
        if (eventListeners.containsKey(agentName)) {
            eventListeners.get(agentName).remove(listener);
        }
    }

    public void sendMessage(Agent sender, String receiverName, String content) {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(new AID(receiverName, AID.ISLOCALNAME));
        msg.setContent(content);
        sender.send(msg);
        notifyScenarioEvent(sender.getLocalName(), "MESSAGE_SENT", 
            "Message envoyé à " + receiverName + ": " + content);
    }

    public AgentController getAgent(String name) {
        return agents.get(name);
    }

    public boolean isAgentActive(String name) {
        AgentController agent = agents.get(name);
        if (agent == null) return false;
        try {
            agent.getState();
            return true;
        } catch (StaleProxyException e) {
            return false;
        }
    }

    public Collection<String> getActiveAgents() {
        List<String> activeAgents = new ArrayList<>();
        for (Map.Entry<String, AgentController> entry : agents.entrySet()) {
            if (isAgentActive(entry.getKey())) {
                activeAgents.add(entry.getKey());
            }
        }
        return activeAgents;
    }

    public void broadcastMessage(String content) {
        for (String agentName : getActiveAgents()) {
            try {
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.addReceiver(new AID(agentName, AID.ISLOCALNAME));
                msg.setContent(content);
                // Nous devons trouver un agent pour envoyer le message
                // Pour l'instant, nous allons juste logger
                System.out.println("Broadcasting to " + agentName + ": " + content);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
