package com.battleship.monitor;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Punto de entrada para la aplicación Monitor (ADMIN).
 * Muestra primero el login de administrador y luego el monitor.
 */
public class MonitorMain {

    public static void main(String[] args) {
        // Configurar Look and Feel del sistema operativo
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Si falla, usar el L&F por defecto
            e.printStackTrace();
        }

        // Lanzar la ventana de Login en el hilo de Swing
        SwingUtilities.invokeLater(() -> {
            MonitorLoginGUI loginGUI = new MonitorLoginGUI();
            loginGUI.setVisible(true);
        });
    }
}
