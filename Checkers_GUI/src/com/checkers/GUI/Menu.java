package com.checkers.GUI;

import com.checkers.core.Board;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This a JFrame class that allows users to choose from staring a new game or a saved game.
 * */
public class Menu extends JFrame {

    public Menu(){
        int height = 2 *  BoardSquare.SIZE;
        int width = 5*(Board.BOARD_SIZE + BoardSquare.SIZE);

        setSize(width,height);
        setTitle("Menu");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel jPanel = new JPanel();
        jPanel.setSize(width,height);
        jPanel.setLayout(new FlowLayout());
        jPanel.setBorder(new EmptyBorder(40, 10, 0, 10));

        JButton startNewGame = new JButton("Start New Game");
        startNewGame.setSize(200,100);
        startNewGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new Checkers(false);
            }
        });

        JButton loadFromFile = new JButton("Resume Saved Game");
        loadFromFile.setSize(200,100);
        loadFromFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new Checkers(true);
            }
        });

        jPanel.add(startNewGame);
        jPanel.add(loadFromFile);
        jPanel.setVisible(true);

        add(jPanel, BorderLayout.CENTER);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);
    }
}
