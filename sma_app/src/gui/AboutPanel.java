package gui;

import javax.swing.*;
import java.awt.*;

public class AboutPanel extends JPanel {
    private Color primaryColor = new Color(41, 128, 185);

    public AboutPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        // Panel principal avec GridBagLayout pour un meilleur contrôle
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Logo ou titre
        JLabel titleLabel = new JLabel("Système Multi-Agents SONATRACH");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(primaryColor);
        mainPanel.add(titleLabel, gbc);

        // Version
        JLabel versionLabel = new JLabel("Version 1.0.0");
        versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        versionLabel.setForeground(Color.BLACK);
        mainPanel.add(versionLabel, gbc);

        // Description du projet
        String description = "<html><div style='text-align: center; width: 400px;'>" +
                "<p>Cette application utilise un système multi-agents (SMA) pour optimiser " +
                "les processus dans l'industrie pétrolière de Sonatrach.</p>" +
                "<p>Le système comprend cinq agents spécialisés qui collaborent pour :</p>" +
                "<ul>" +
                "<li>Surveiller les installations en temps réel</li>" +
                "<li>Détecter et gérer les anomalies</li>" +
                "<li>Optimiser la production</li>" +
                "<li>Gérer la logistique</li>" +
                "<li>Optimiser les achats</li>" +
                "</ul></div></html>";
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLabel.setForeground(Color.BLACK);

        mainPanel.add(descLabel, gbc);

        // Technologies utilisées
        String tech = "<html><div style='text-align: center; width: 400px;'>" +
                "<p><b>Technologies utilisées :</b></p>" +
                "<ul>" +
                "<li>JADE (Java Agent DEvelopment Framework)</li>" +
                "<li>Java Swing pour l'interface graphique</li>" +
                "<li>Algorithmes d'optimisation avancés</li>" +
                "</ul></div></html>";
        JLabel techLabel = new JLabel(tech);
        techLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        techLabel.setForeground(Color.BLACK);

        mainPanel.add(techLabel, gbc);

        // Guide d'utilisation rapide
        String guide = "<html><div style='text-align: center; width: 400px;'>" +
                "<p><b>Guide d'utilisation rapide :</b></p>" +
                "<ol>" +
                "<li>Utilisez le <b>Tableau de Bord</b> pour surveiller les métriques en temps réel</li>" +
                "<li>Accédez aux <b>Scénarios</b> pour simuler différentes situations</li>" +
                "<li>Observez les interactions entre les agents dans la visualisation</li>" +
                "<li>Consultez les logs pour suivre les événements détaillés</li>" +
                "</ol></div></html>";
        JLabel guideLabel = new JLabel(guide);
        guideLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        guideLabel.setForeground(Color.BLACK);

        mainPanel.add(guideLabel, gbc);

        // Crédits
        String credits = "<html><div style='text-align: center; width: 400px;'>" +
                "<p><b>Développé par :</b></p>" +
                "<p>Ahmed Azzi & Ilyes Boutalbi</p>" +
                "<p>Master 2 IA4IOT</p>" +
                "<p>Université de Mostaganem</p>" +
                "</div></html>";
        JLabel creditsLabel = new JLabel(credits);
        creditsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        creditsLabel.setForeground(Color.BLACK);

        mainPanel.add(creditsLabel, gbc);

        // Ajouter le panel principal avec scroll
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }
}
