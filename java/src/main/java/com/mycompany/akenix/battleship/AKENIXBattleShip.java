/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.akenix.battleship;

import com.battleship.server.ServerMain;
import com.battleship.ui.login.LoginGUI;

/**
 *
 * @author Adrian
 */
public class AKENIXBattleShip {

    public static void main(String[] args) {
        // Modo servidor (headless) si se pasa --server o -server como argumento
        if (args.length > 0 && (args[0].equals("--server") || args[0].equals("-server"))) {
            ServerMain.main(args);
        } else {
            // Modo cliente con GUI
            java.awt.EventQueue.invokeLater(() -> {
                new LoginGUI().setVisible(true);
            });
        }
    }
}
