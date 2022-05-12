package com.checkers.GUI;

import com.checkers.core.Board;
import com.checkers.core.exceptions.InValidMove;
import com.checkers.core.move.Move;
import com.checkers.core.move.Position;
import com.checkers.core.piece.Piece;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class Checkers extends JFrame {
    private Board board;
    private BoardSquare[][] boardSquares;
    private Position origin;

    public Checkers(boolean loadSavedGame){
        this.board = new Board(loadSavedGame);
        this.boardSquares = new BoardSquare[Board.BOARD_SIZE][Board.BOARD_SIZE];
        this.initBoardSquares();

        setSize(Board.BOARD_SIZE *  BoardSquare.SIZE + BoardSquare.SIZE,Board.BOARD_SIZE *  BoardSquare.SIZE);
        setTitle("Checkers");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel gamePanel = new JPanel();
        gamePanel.setLayout(new GridLayout(Board.BOARD_SIZE,Board.BOARD_SIZE));
        gamePanel.setSize(Board.BOARD_SIZE *  BoardSquare.SIZE,Board.BOARD_SIZE *  BoardSquare.SIZE);
        for (BoardSquare[] boardSquare : this.boardSquares){
            for (BoardSquare bs :
                    boardSquare) {
                gamePanel.add(bs);
            }
        }

        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new GridLayout(2,1));
        sidePanel.setSize( BoardSquare.SIZE,Board.BOARD_SIZE *  BoardSquare.SIZE);

        JButton undoButton = new JButton("undo");
        undoButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                turnOffAllHighlights();
                board.undo();
                updateBoard();

            }
        });
        undoButton.setSize(BoardSquare.SIZE,Board.BOARD_SIZE *  BoardSquare.SIZE/2);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    board.saveGame();
                } catch (FileNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        saveButton.setSize(BoardSquare.SIZE,Board.BOARD_SIZE *  BoardSquare.SIZE/2);


        sidePanel.add(undoButton);
        sidePanel.add(saveButton);

        gamePanel.setVisible(true);
        sidePanel.setVisible(true);


        add(gamePanel, BorderLayout.CENTER);
        add(sidePanel,BorderLayout.LINE_END);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);
    }

    public void initBoardSquares(){
        for (int i = 0; i < Board.BOARD_SIZE; i++) {
            for (int j = 0; j < Board.BOARD_SIZE; j++) {
                BoardSquare square = new BoardSquare(i,j);
                if(this.board.getPieceAt(i,j)==null)
                    square.setPiece();
                else
                    square.setPiece(this.board.getPieceAt(i,j).toString());

                square.setHighlight(false);
                square.addActionListener(new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        boardClicked(square.getCoordinate());
                    }
                });
                this.boardSquares[i][j] = square;
            }
        }
    }

    public void updateBoard(){
        for (int i = 0; i < Board.BOARD_SIZE; i++) {
            for (int j = (1 - i % 2); j < Board.BOARD_SIZE; j += 2) {

                if(this.board.getPieceAt(i,j)==null)
                    this.boardSquares[i][j].setPiece();
                else
                    this.boardSquares[i][j].setPiece(this.board.getPieceAt(i,j).toString());

            }
        }
    }

    public void boardClicked(Position clickedPos){

        if(this.origin==null){
            Piece p = board.getPieceAt(clickedPos);
            if(p==null)
                return;

            if(p.owner==board.getTurn()){
                this.origin = clickedPos;
                this.setHighlight(this.board.reachablePositions(clickedPos),true);
            }
        }else{
            Piece p = board.getPieceAt(clickedPos);
            if(p!=null && p.owner==board.getTurn()){
                this.origin = clickedPos;
                this.turnOffAllHighlights();
                this.setHighlight(this.board.reachablePositions(this.origin),true);
                return;
            }

            Move move = new Move(this.origin,clickedPos);
            try {
                this.board.makeMove(move);
                updateBoard();
                Piece.PieceOwner winner = this.board.isGameOver();
                if(winner!=null){
                    JOptionPane.showMessageDialog(this, winner + " won.", "WINNER ALERT " , JOptionPane.INFORMATION_MESSAGE);
                    this.board = new Board(false); // initializing new game
                    this.updateBoard();
                }

            } catch (InValidMove e) {
                System.out.println("Error occurred.");
            }finally {
                this.origin = null;
                this.turnOffAllHighlights();
            }
            // create a move and send it to board
            // inside board check for a move that has the same origin and destination, if so execute
        }
    }

    private void setHighlight(ArrayList<Move> moves, boolean light){
        for (Move mv :
                moves) {
            this.boardSquares[mv.getDestination().getRow()][mv.getDestination().getColumn()].setHighlight(light);
        }
    }

    private void turnOffAllHighlights(){
        for (BoardSquare[] boardSquare : this.boardSquares){
            for (BoardSquare bs :
                    boardSquare) {
                bs.setHighlight(false);
            }
        }
    }
}
