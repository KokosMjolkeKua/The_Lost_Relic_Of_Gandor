package com.lostrelic;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Use system look and feel then apply dark tweaks in GUI
        SwingUtilities.invokeLater(() -> {
            Game game = new Game();
            new GameGUI(game);
        });
    }
}
