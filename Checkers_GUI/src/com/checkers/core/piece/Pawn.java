package com.checkers.core.piece;

import com.checkers.core.Board;
import com.checkers.core.move.Jump;
import com.checkers.core.move.Move;
import com.checkers.core.move.Position;

import java.util.ArrayList;



public class Pawn extends Piece{
    public Pawn(PieceOwner owner) {
        super(owner);
    }

    @Override
    protected ArrayList<Move> generateSingleSteps(Board board, Position position) {
        int[][] moveDir;
        ArrayList<Move> tempMoveList = new ArrayList<>();


        if (this.owner == PieceOwner.PLAYER1)
            moveDir = new int[][]{{-1, -1}, {-1, 1}}; // allows player1's pawns to move towards the top of the board only
        else
            moveDir = new int[][]{{1, -1}, {1, 1}};  // allows player2's pawns to move towards the bottom of the board only

        int r = position.getRow();
        int c = position.getColumn();
        for (int i = 0; i < moveDir.length; i++) {
            int newR = r + moveDir[i][0];
            int newC = c + moveDir[i][1];

            //Check if the new row and column are inside the board
            if (newR < 0 || newR >= Board.BOARD_SIZE || newC < 0 || newC >= Board.BOARD_SIZE)
                continue;


            Piece newPos = board.getPieceAt(newR,newC);

            // check if the adjacent square is empty
            if (newPos == null) {
                Move mv = new Move(new Position(r,c), new Position(newR,newC));
                tempMoveList.add(mv);

            }
            // check if the adjacent square is occupied by our piece, if so continue
            else if (newPos.owner == this.owner)
                continue;
                // check if the second adjacent square is occupied or not
            else {
                int nextNewR = newR + moveDir[i][0];
                int nextNewC = newC + moveDir[i][1];

                if (nextNewR < 0 || nextNewR >= Board.BOARD_SIZE || nextNewC < 0 || nextNewC >= Board.BOARD_SIZE)
                    continue;
                Piece nextNewPos = board.getPieceAt(nextNewR,nextNewC);

                // if the second adjacent square isn't occupied  this is a valid removing move
                if (nextNewPos == null) {
                    Jump rm = new Jump(position,new Position(nextNewR,nextNewC));
                    rm.addToBeRemovedSquare(new Position(newR,newC));
                    tempMoveList.add(rm);
                }

            }
        }
        return tempMoveList;
    }

    @Override
    protected ArrayList<Jump> generateJumps(Board board, Position position) {
        ArrayList<Jump> jumpList = new ArrayList<>();
        ArrayList<int[]> tempPieces = new ArrayList<>();  // remove tempPiece and send the clone of board

        for (Move mv : this.generateSingleSteps(board,position)) {
            if (!(mv instanceof Jump))
                continue;
            ArrayList<Jump> jumps = new ArrayList<>();
            jumps.add((Jump) mv);
    
            while (jumps.size() > 0) {
                Jump x = jumps.remove(0);
                boolean addToJumpList = true;

                for (Move nMove : this.generateSingleSteps(board, x.getDestination())) {
                    if (!(nMove instanceof Jump))
                        continue;
                    new Jump(new Position(x.getOrigin()),nMove.getDestination());
                    Jump rm = new Jump(new Position(x.getOrigin()),nMove.getDestination());
                    rm.toBeRemoved.addAll(x.toBeRemoved);
                    rm.toBeRemoved.addAll(((Jump) nMove).toBeRemoved);
                    board.setPiece(x.getDestination(), new Pawn(this.owner)); // place temporary pieces to avoid infinite recursion with Kings
                    tempPieces.add(new int[]{x.getDestination().getRow(),x.getDestination().getColumn()});
                    jumps.add(rm);
                    addToJumpList = false;
                }
                if (addToJumpList) // if the move doesn't have any more jumps add it to jumpList
                    jumpList.add(x);
            }
        }

        for (int[] tempPiece :
                tempPieces) {
            board.setPiece(tempPiece[0],tempPiece[1], null);  // Removes the temporary pieces
        }

        return jumpList;
    }

    public boolean equals(Object obj) {
        if(this==obj)
            return true;

        if(!(obj instanceof Pawn))
            return false;

        Pawn p = (Pawn) obj;
        return this.owner.equals(p.owner);
    }

    @Override
    public String toString() {
        return "Pawn " + super.toString();
    }
}
