/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.akenix.battleship;

import com.battleship.monitor.MonitorMain;
import com.battleship.server.ServerMain;
import com.battleship.ui.login.LoginGUI;

/**
 *
 * @author Adrian
 */
public class AKENIXBattleShip {

    public static void main(String[] args) {
        if (args.length > 0) {
            switch (args[0]) {
                case "--server":
                case "-server":
                    // Modo servidor (headless)
                    ServerMain.main(args);
                    return;
                case "--monitor":
                case "-monitor":
                    // Modo monitor de administración
                    MonitorMain.main(args);
                    return;
            }
        }

        // Modo cliente con GUI (default)
        java.awt.EventQueue.invokeLater(() -> {
            new LoginGUI().setVisible(true);
        });
    }
}
