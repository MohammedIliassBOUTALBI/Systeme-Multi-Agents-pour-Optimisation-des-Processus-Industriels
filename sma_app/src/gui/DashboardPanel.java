package gui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class DashboardPanel extends JPanel {
    private JPanel statsPanel;
    private JPanel chartPanel;
    private javax.swing.Timer updateTimer;
    private Random random = new Random();
    private Color primaryColor = new Color(41, 128, 185);
    private Color warningColor = new Color(231, 76, 60);
    private Color successColor = new Color(46, 204, 113);
    private Color purpleColor = new Color(155, 89, 182);
    
    // Variables pour simuler les données en temps réel
    private double productionLevel = 75.0;
    private int maintenanceAlerts = 0;
    private double stockLevel = 85.0;
    private int activeAgents = 5;
    private java.util.List<Double> productionHistory = new java.util.ArrayList<>();

    public DashboardPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setupComponents();
        startDataSimulation();
        
        // Initialiser l'historique
        for (int i = 0; i < 24; i++) {
            productionHistory.add(75.0 + random.nextDouble() * 10);
        }
    }

    private void setupComponents() {
        // Panel des statistiques
        statsPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        statsPanel.setOpaque(false);
        
        // Création des cartes de statistiques
        updateStatCards();
        
        // Panel du graphique
        chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawChart(g);
            }
        };
        chartPanel.setPreferredSize(new Dimension(0, 300));
        chartPanel.setBackground(Color.WHITE);
        chartPanel.setBorder(createStyledTitledBorder("Production en Temps Réel"));
        
        // Ajout des composants
        add(statsPanel, BorderLayout.NORTH);
        add(chartPanel, BorderLayout.CENTER);
        
        // Ajouter un panel d'alertes en bas
        JPanel alertPanel = createAlertPanel();
        add(alertPanel, BorderLayout.SOUTH);
    }

    private JPanel createAlertPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(createStyledTitledBorder("Dernières Alertes"));
        panel.setPreferredSize(new Dimension(0, 150));
        
        JTextArea alertArea = new JTextArea();
        alertArea.setEditable(false);
        alertArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        alertArea.setText("10:30 - Alerte de maintenance dans le secteur A-7\n" +
                         "10:45 - Niveau de stock critique pour matière première X\n" +
                         "11:00 - Optimisation de la production en cours\n");
        
        panel.add(new JScrollPane(alertArea), BorderLayout.CENTER);
        return panel;
    }

    private void updateStatCards() {
        statsPanel.removeAll();
        
        // Production
        addStatCard("Production", String.format("%.1f%%", productionLevel), primaryColor);
        
        // Alertes
        addStatCard("Alertes", String.valueOf(maintenanceAlerts), warningColor);
        
        // Niveau de Stock
        addStatCard("Stock", String.format("%.1f%%", stockLevel), successColor);
        
        // Agents Actifs
        addStatCard("Agents Actifs", String.valueOf(activeAgents), purpleColor);
        
        statsPanel.revalidate();
        statsPanel.repaint();
    }

    private void addStatCard(String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        statsPanel.add(card);
    }

    private TitledBorder createStyledTitledBorder(String title) {
        TitledBorder border = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(primaryColor, 1),
            title
        );
        border.setTitleFont(new Font("Segoe UI", Font.BOLD, 14));
        border.setTitleColor(primaryColor);
        return border;
    }

    private void drawChart(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int width = chartPanel.getWidth();
        int height = chartPanel.getHeight();
        int padding = 40;
        
        // Dessiner les axes
        g2.setColor(Color.BLACK);
        g2.drawLine(padding, height - padding, width - padding, height - padding); // axe X
        g2.drawLine(padding, padding, padding, height - padding); // axe Y
        
        // Dessiner les graduations Y
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        for (int i = 0; i <= 100; i += 20) {
            int y = height - padding - (i * (height - 2 * padding) / 100);
            g2.drawLine(padding - 5, y, padding, y);
            g2.drawString(i + "%", 5, y + 5);
        }
        
        // Dessiner la courbe
        g2.setColor(primaryColor);
        g2.setStroke(new BasicStroke(2f));
        
        int xStep = (width - 2 * padding) / (productionHistory.size() - 1);
        int[] xPoints = new int[productionHistory.size()];
        int[] yPoints = new int[productionHistory.size()];
        
        for (int i = 0; i < productionHistory.size(); i++) {
            xPoints[i] = padding + (i * xStep);
            double value = productionHistory.get(i);
            yPoints[i] = height - padding - (int)((value * (height - 2 * padding)) / 100);
        }
        
        // Dessiner la ligne
        g2.drawPolyline(xPoints, yPoints, xPoints.length);
        
        // Dessiner les points
        for (int i = 0; i < xPoints.length; i++) {
            g2.setColor(Color.WHITE);
            g2.fillOval(xPoints[i] - 4, yPoints[i] - 4, 8, 8);
            g2.setColor(primaryColor);
            g2.drawOval(xPoints[i] - 4, yPoints[i] - 4, 8, 8);
        }
    }

    private void startDataSimulation() {
        updateTimer = new javax.swing.Timer(2000, e -> {
            // Simuler des changements de données
            productionLevel += (random.nextDouble() - 0.5) * 5;
            productionLevel = Math.min(100, Math.max(0, productionLevel));
            
            if (random.nextDouble() < 0.1) {
                maintenanceAlerts++;
            }
            
            stockLevel += (random.nextDouble() - 0.5) * 3;
            stockLevel = Math.min(100, Math.max(0, stockLevel));
            
            // Mettre à jour l'historique
            productionHistory.remove(0);
            productionHistory.add(productionLevel);
            
            // Mettre à jour l'interface
            updateStatCards();
            chartPanel.repaint();
        });
        updateTimer.start();
    }
}
