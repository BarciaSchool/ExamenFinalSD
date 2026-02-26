/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.akenix.battleship;

import com.battleship.ui.login.LoginGUI;

/**
 *
 * @author Adrian
 */
public class AKENIXBattleShip {

    public static void main(String[] args) {
        // Iniciar la interfaz de login
        java.awt.EventQueue.invokeLater(() -> {
            new LoginGUI().setVisible(true);
        });
    }
}
