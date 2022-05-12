package com.checkers.core.piece;

import com.checkers.core.Board;
import com.checkers.core.move.Jump;
import com.checkers.core.move.Move;
import com.checkers.core.move.Position;

import java.util.ArrayList;

public abstract class Piece implements Cloneable {
    public enum PieceOwner {PLAYER1, PLAYER2}

    public PieceOwner owner;

    public Piece( PieceOwner owner){
        this.owner= owner;
    }


    /**
     * Finds all possible moves and jumps from the position in one-step range.
     * First we set the proper movement direction for each piece. After that we iterate
     * through each movement direction and check if the new square is inside the board. If
     * that condition is satisfied we check if we have a legal movement from that square and return
     * a ArrayList of all possible moves from the square.
     *
     * @param board the object that represent the board
     * @param position  the origin of the piece we are generating the move for
     * @return ArrayList of all possible moves from that square
     */
    public  ArrayList<Move> generateMoves(Board board, Position position){
        ArrayList<Jump> jumps = generateJumps(new Board(board),position);
        if(jumps.size()!=0){
            ArrayList<Move> newJumps = new ArrayList<Move>();
            newJumps.addAll(jumps);
            return newJumps;
        }
        return generateSingleSteps(board,position);
    }
    /**
     * Finds all possible moves and jumps from the square position in one-step range.
     * First we set the proper movement direction for each piece. After that we iterate
     * through each movement direction and check if the new square is inside the board. If
     * that condition is satisfied we check if we have a legal movement from that square and return
     * a ArrayList of all possible moves from the square.
     *
     * @param board the object that represent the board
     * @param position  the origin of the piece we are generating the move for
     * @return ArrayList of all possible moves from that square
     */
    protected abstract ArrayList<Move> generateSingleSteps(Board board, Position position);

    /**
     * Find all possible jumps for a position.
     * We call the findMoves method to see all the possible moves from that square.
     * If there is any jump from the returned ArrayList, we use BreadthFirst search
     * to look for any other jumps from the new position.
     *
     * @param board the object that represent the board
     * @param position  the origin of the piece we are generating the move for
     * @return ArrayList of all possible Jumps from that square
     */
    protected abstract ArrayList<Jump> generateJumps(Board board,Position position);

    public Piece clone(){
        try {
            return (Piece) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public String toString() {
        return owner.toString();
    }
}
