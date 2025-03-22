package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ModernButton extends JButton {
    private Color backgroundColor = new Color(41, 128, 185);
    private Color hoverColor = new Color(52, 152, 219);
    private boolean isHovered = false;

    public ModernButton(String text) {
        super(text);
        setupButton();
    }

    private void setupButton() {
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(true);
        setBackground(backgroundColor);
        setForeground(Color.WHITE);
        setFont(new Font("Segoe UI", Font.BOLD, 14));
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                setBackground(backgroundColor);
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (getModel().isPressed()) {
            g2.setColor(hoverColor.darker());
        } else if (isHovered) {
            g2.setColor(hoverColor);
        } else {
            g2.setColor(backgroundColor);
        }
        
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        g2.dispose();

        super.paintComponent(g);
    }
}
