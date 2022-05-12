package com.checkers.GUI;

import com.checkers.core.move.Position;

import javax.swing.*;
import java.awt.*;

public class BoardSquare extends JButton {

    private final Color BLACK = Color.GRAY;
    private final Color WHITE = Color.WHITE;
    private final Color RED = Color.RED;
    public static final int SIZE = 80;
   private final int r;
   private final int c;
   private final boolean light;
    public BoardSquare(int r, int c){
        this.light = (r+c)%2==0;
        this.r = r;
        this.c = c;

        setEnabled(!this.light);
        setSize(SIZE,SIZE);
    }

    public Position  getCoordinate(){
        return new Position(this.r,this.c);
    }

    public void setPiece(String piece){
        String[] pieces = piece.split(" ");
        String type = pieces[0];
        String owner = pieces[1];
        if(type.equalsIgnoreCase("King"))
            if (owner.equalsIgnoreCase("player1"))
                setIcon(new ImageIcon("src/resources/player1King.png"));
            else
                setIcon(new ImageIcon("src/resources/player2King.png"));

        else
            if(owner.equalsIgnoreCase("player1"))
                setIcon(new ImageIcon("src/resources/player1.png"));
            else
                setIcon(new ImageIcon("src/resources/player2.png"));

    }
    public void setPiece(){
        setIcon(null);
    }
    public void setHighlight(boolean highlight){
        if(highlight)
            setBackground(this.RED);
        else if (this.light)
            setBackground(this.WHITE);
        else
            setBackground(this.BLACK);

    }

}
