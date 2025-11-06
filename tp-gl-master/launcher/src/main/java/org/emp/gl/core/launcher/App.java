package org.emp.gl.core.launcher;

import org.emp.gl.clients.Horloge;
import org.emp.gl.clients.HorlogeGUI;
import org.emp.gl.time.service.impl.DummyTimeServiceImpl;
import org.emp.gl.timer.service.TimerService;

import javax.swing.SwingUtilities;

/**
 * Main application launcher
 */
public class App {

    public static void main(String[] args) {
        // Create timer service instance (singleton-like)
        TimerService timerService = new DummyTimeServiceImpl();
        
        // Create console horloge
        Horloge consoleHorloge = new Horloge("Console Horloge", timerService);
        // Display initial time
        consoleHorloge.afficherHeure();
        
        // Create and show GUI horloge
        SwingUtilities.invokeLater(() -> {
            HorlogeGUI guiHorloge = new HorlogeGUI("Horloge GUI", timerService);
            guiHorloge.showWindow();
        });
        
        // Keep the application running - the GUI will keep it alive
        // This thread can sleep as GUI runs on EDT
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
