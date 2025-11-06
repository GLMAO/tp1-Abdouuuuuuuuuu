package org.emp.gl.clients;

import org.emp.gl.timer.service.TimerChangeListener;
import org.emp.gl.timer.service.TimerService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Modern GUI interface for displaying time using Swing with countdown timers
 * Redesigned with gradient background and glassmorphism effects
 */
public class HorlogeGUI extends JFrame implements TimerChangeListener {
    
    // Modern gradient color palette - Soft Teal Theme
    private static final Color PRIMARY_BG_START = new Color(204, 251, 241);
    private static final Color PRIMARY_BG_END = new Color(153, 246, 228);
    private static final Color GLASS_BG = new Color(255, 255, 255, 200);
    private static final Color GLASS_BORDER = new Color(20, 184, 166, 150);
    private static final Color ACCENT_START = new Color(6, 182, 212);
    private static final Color ACCENT_END = new Color(14, 165, 233);
    private static final Color TEXT_PRIMARY = new Color(17, 24, 39);
    private static final Color TEXT_SECONDARY = new Color(55, 65, 81);
    private static final Color SUCCESS_COLOR = new Color(5, 150, 105);
    private static final Color DANGER_COLOR = new Color(220, 38, 38);
    private static final Color WARNING_COLOR = new Color(217, 119, 6);
    
    private TimerService timerService;
    private JLabel timeLabel;
    private JLabel titleLabel;
    private Font timeFont;
    private JPanel countdownPanel;
    private List<CountdownTimer> countdownTimers;
    private JPanel mainPanel;
    
    public HorlogeGUI(String title, TimerService timerService) {
        this.timerService = timerService;
        this.countdownTimers = new ArrayList<>();
        
        if (timerService != null) {
            timerService.addTimeChangeListener(this);
        }
        
        initializeGUI(title);
        updateTime();
    }
    
    private void initializeGUI(String title) {
        setTitle(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(true);
        
        // Create gradient background panel
        GradientPanel backgroundPanel = new GradientPanel(PRIMARY_BG_START, PRIMARY_BG_END);
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);
        
        // Create modern title with gradient effect
        titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setBorder(new EmptyBorder(30, 20, 20, 20));
        titleLabel.setOpaque(false);
        
        // Create main panel with padding
        mainPanel = new JPanel(new BorderLayout(0, 25));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(new EmptyBorder(0, 40, 40, 40));
        
        // Create glassmorphic time display card
        GlassPanel timeCard = new GlassPanel();
        timeCard.setLayout(new BorderLayout());
        timeCard.setBorder(new EmptyBorder(40, 50, 40, 50));
        
        timeFont = new Font("SF Mono", Font.BOLD, 64);
        timeLabel = new JLabel("00:00:00.0", SwingConstants.CENTER);
        timeLabel.setFont(timeFont);
        timeLabel.setForeground(TEXT_PRIMARY);
        timeLabel.setOpaque(false);
        
        // Add glow effect
        timeLabel.setForeground(new Color(167, 139, 250));
        
        timeCard.add(timeLabel, BorderLayout.CENTER);
        
        // Create countdown section with glass effect
        JPanel countdownSection = new JPanel(new BorderLayout(0, 20));
        countdownSection.setOpaque(false);
        
        JLabel countdownTitle = new JLabel("⏱ Compteurs à Rebours");
        countdownTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        countdownTitle.setForeground(TEXT_PRIMARY);
        countdownTitle.setBorder(new EmptyBorder(15, 5, 10, 5));
        
        countdownPanel = new JPanel();
        countdownPanel.setLayout(new BoxLayout(countdownPanel, BoxLayout.Y_AXIS));
        countdownPanel.setOpaque(false);
        
        JScrollPane scrollPane = new JScrollPane(countdownPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(600, 250));
        
        // Modern scrollbar
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        
        countdownSection.add(countdownTitle, BorderLayout.NORTH);
        countdownSection.add(scrollPane, BorderLayout.CENTER);
        
        // Create modern control panel
        JPanel controlPanel = createCountdownControlPanel();
        
        // Assemble layout
        backgroundPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(timeCard, BorderLayout.NORTH);
        mainPanel.add(countdownSection, BorderLayout.CENTER);
        mainPanel.add(controlPanel, BorderLayout.SOUTH);
        backgroundPanel.add(mainPanel, BorderLayout.CENTER);
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                disconnect();
                System.exit(0);
            }
        });
        
        setSize(750, 800);
        setLocationRelativeTo(null);
    }
    
    private JPanel createCountdownControlPanel() {
        GlassPanel panel = new GlassPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 20));
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));
        
        JLabel hLabel = new JLabel("H:");
        hLabel.setForeground(TEXT_SECONDARY);
        hLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        
        JSpinner hoursSpinner = createModernSpinner(0, 0, 23, 1);
        
        JLabel mLabel = new JLabel("M:");
        mLabel.setForeground(TEXT_SECONDARY);
        mLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        
        JSpinner minutesSpinner = createModernSpinner(0, 0, 59, 1);
        
        JLabel sLabel = new JLabel("S:");
        sLabel.setForeground(TEXT_SECONDARY);
        sLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        
        JSpinner secondsSpinner = createModernSpinner(0, 0, 59, 1);
        
        GradientButton addButton = new GradientButton("➕ Ajouter", ACCENT_START, ACCENT_END);
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int hours = (Integer) hoursSpinner.getValue();
                int minutes = (Integer) minutesSpinner.getValue();
                int seconds = (Integer) secondsSpinner.getValue();
                
                if (hours > 0 || minutes > 0 || seconds > 0) {
                    addCountdownTimer(hours, minutes, seconds);
                } else {
                    JOptionPane.showMessageDialog(HorlogeGUI.this, 
                        "Veuillez entrer une durée valide!", 
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        panel.add(hLabel);
        panel.add(hoursSpinner);
        panel.add(mLabel);
        panel.add(minutesSpinner);
        panel.add(sLabel);
        panel.add(secondsSpinner);
        panel.add(addButton);
        
        return panel;
    }
    
    private JSpinner createModernSpinner(int value, int min, int max, int step) {
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(value, min, max, step));
        spinner.setPreferredSize(new Dimension(75, 40));
        spinner.setFont(new Font("Segoe UI", Font.BOLD, 15));
        
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JSpinner.DefaultEditor spinnerEditor = (JSpinner.DefaultEditor) editor;
            spinnerEditor.getTextField().setBackground(new Color(71, 85, 105, 150));
            spinnerEditor.getTextField().setForeground(TEXT_PRIMARY);
            spinnerEditor.getTextField().setCaretColor(TEXT_PRIMARY);
            spinnerEditor.getTextField().setBorder(new EmptyBorder(5, 12, 5, 12));
        }
        
        spinner.setBorder(new RoundedBorder(10, GLASS_BORDER));
        spinner.setOpaque(false);
        return spinner;
    }
    
    private void addCountdownTimer(int hours, int minutes, int seconds) {
        CountdownTimer countdown = new CountdownTimer(hours, minutes, seconds);
        countdownTimers.add(countdown);
        
        GlassPanel timerPanel = new GlassPanel();
        timerPanel.setLayout(new BorderLayout(20, 0));
        timerPanel.setBorder(new EmptyBorder(25, 30, 25, 30));
        timerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 85));
        
        JLabel timerLabel = new JLabel();
        timerLabel.setFont(new Font("SF Mono", Font.BOLD, 32));
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timerLabel.setForeground(SUCCESS_COLOR);
        countdown.setDisplayLabel(timerLabel);
        updateCountdownDisplay(countdown);
        
        GradientButton removeButton = new GradientButton("🗑 Supprimer", 
            new Color(220, 38, 38), new Color(239, 68, 68));
        removeButton.setPreferredSize(new Dimension(130, 40));
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                countdownTimers.remove(countdown);
                countdownPanel.remove(timerPanel);
                countdownPanel.revalidate();
                countdownPanel.repaint();
            }
        });
        
        timerPanel.add(timerLabel, BorderLayout.CENTER);
        timerPanel.add(removeButton, BorderLayout.EAST);
        
        countdownPanel.add(timerPanel);
        countdownPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        countdownPanel.revalidate();
        countdownPanel.repaint();
    }
    
    private void updateCountdownDisplay(CountdownTimer countdown) {
        if (countdown.isFinished()) {
            countdown.getDisplayLabel().setText("⏰ TERMINÉ!");
            countdown.getDisplayLabel().setForeground(DANGER_COLOR);
        } else {
            int remaining = countdown.getRemainingTenths();
            int hours = remaining / 36000;
            int minutes = (remaining % 36000) / 600;
            int seconds = (remaining % 600) / 10;
            int tenths = remaining % 10;
            
            String timeStr = String.format("%02d:%02d:%02d.%d", hours, minutes, seconds, tenths);
            countdown.getDisplayLabel().setText(timeStr);
            
            if (remaining < 100) {
                countdown.getDisplayLabel().setForeground(WARNING_COLOR);
            } else {
                countdown.getDisplayLabel().setForeground(SUCCESS_COLOR);
            }
        }
    }
    
    private void updateTime() {
        if (timerService != null && timeLabel != null) {
            SwingUtilities.invokeLater(() -> {
                String timeString = String.format("%02d:%02d:%02d.%d",
                        timerService.getHeures(),
                        timerService.getMinutes(),
                        timerService.getSecondes(),
                        timerService.getDixiemeDeSeconde());
                timeLabel.setText(timeString);
            });
        }
    }
    
    @Override
    public void propertyChange(String prop, Object oldValue, Object newValue) {
        updateTime();
        
        SwingUtilities.invokeLater(() -> {
            for (CountdownTimer countdown : countdownTimers) {
                if (!countdown.isFinished() && 
                    TimerChangeListener.DIXEME_DE_SECONDE_PROP.equals(prop)) {
                    countdown.decrement();
                    updateCountdownDisplay(countdown);
                }
            }
        });
    }
    
    public void disconnect() {
        if (timerService != null) {
            timerService.removeTimeChangeListener(this);
        }
    }
    
    public void showWindow() {
        setVisible(true);
    }
    
    /**
     * Gradient background panel
     */
    private static class GradientPanel extends JPanel {
        private Color startColor;
        private Color endColor;
        
        public GradientPanel(Color start, Color end) {
            this.startColor = start;
            this.endColor = end;
            setOpaque(true);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            
            int width = getWidth();
            int height = getHeight();
            
            GradientPaint gp = new GradientPaint(0, 0, startColor, 0, height, endColor);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, width, height);
        }
    }
    
    /**
     * Glassmorphic panel with blur effect
     */
    private static class GlassPanel extends JPanel {
        public GlassPanel() {
            setOpaque(false);
            setBorder(new RoundedBorder(20, GLASS_BORDER));
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Glass background
            g2.setColor(GLASS_BG);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            
            // Subtle border
            g2.setColor(GLASS_BORDER);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
            
            g2.dispose();
            super.paintComponent(g);
        }
    }
    
    /**
     * Gradient button with hover effects
     */
    private static class GradientButton extends JButton {
        private Color startColor;
        private Color endColor;
        private boolean isHovered = false;
        
        public GradientButton(String text, Color start, Color end) {
            super(text);
            this.startColor = start;
            this.endColor = end;
            
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setPreferredSize(new Dimension(120, 40));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    isHovered = true;
                    repaint();
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    isHovered = false;
                    repaint();
                }
            });
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            Color start = isHovered ? startColor.brighter() : startColor;
            Color end = isHovered ? endColor.brighter() : endColor;
            
            GradientPaint gp = new GradientPaint(0, 0, start, 0, getHeight(), end);
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            
            g2.dispose();
            super.paintComponent(g);
        }
    }
    
    /**
     * Rounded border
     */
    private static class RoundedBorder extends EmptyBorder {
        private Color borderColor;
        private int radius;
        
        public RoundedBorder(int radius, Color color) {
            super(2, 2, 2, 2);
            this.radius = radius;
            this.borderColor = color;
        }
        
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }
    }
    
    /**
     * Modern scrollbar UI
     */
    private static class ModernScrollBarUI extends javax.swing.plaf.basic.BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = new Color(139, 92, 246, 150);
            this.trackColor = new Color(71, 85, 105, 50);
        }
        
        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }
        
        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }
        
        private JButton createZeroButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            return button;
        }
        
        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(thumbColor);
            g2.fillRoundRect(thumbBounds.x + 3, thumbBounds.y + 3, 
                thumbBounds.width - 6, thumbBounds.height - 6, 12, 12);
            g2.dispose();
        }
    }
    
    /**
     * Inner class to represent a countdown timer
     */
    private static class CountdownTimer {
        private int remainingTenths;
        private JLabel displayLabel;
        
        public CountdownTimer(int hours, int minutes, int seconds) {
            this.remainingTenths = (hours * 3600 + minutes * 60 + seconds) * 10;
        }
        
        public void decrement() {
            if (remainingTenths > 0) {
                remainingTenths--;
            }
        }
        
        public boolean isFinished() {
            return remainingTenths <= 0;
        }
        
        public int getRemainingTenths() {
            return remainingTenths;
        }
        
        public JLabel getDisplayLabel() {
            return displayLabel;
        }
        
        public void setDisplayLabel(JLabel displayLabel) {
            this.displayLabel = displayLabel;
        }
    }
}