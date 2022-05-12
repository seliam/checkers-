package com.checkers.core;

import com.checkers.core.exceptions.InValidMove;
import com.checkers.core.exceptions.InvalidArrangement;
import com.checkers.core.move.Jump;
import com.checkers.core.move.Move;
import com.checkers.core.move.Position;
import com.checkers.core.piece.King;
import com.checkers.core.piece.Pawn;
import com.checkers.core.piece.Piece;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class Board {
    public static final int BOARD_SIZE = 8;
    private static final String GAMES_PATH = "src/games.txt";
    private Piece[][] board ;
    private Piece[][] prevBoard;
    private Piece.PieceOwner turn;

    public Board(boolean loadSavedGame) {
        if(loadSavedGame){
            try {
                Scanner input = new Scanner(new FileInputStream(GAMES_PATH));
                String board = input.nextLine();
                this.board = this.stringToBoard(board);
                String prevBoard = input.nextLine();
                this.prevBoard = this.stringToBoard(prevBoard);
                String turn = input.nextLine();
                this.turn = (turn.equals(Piece.PieceOwner.PLAYER1.toString()))? Piece.PieceOwner.PLAYER1: Piece.PieceOwner.PLAYER2;
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        else {
            this.board = new Piece[BOARD_SIZE][BOARD_SIZE];
            this.prevBoard = null;
            this.turn = Piece.PieceOwner.PLAYER1;
            this.resetBoard();
        }

    }

    public Board(Board b) {
        this.prevBoard = cloneBoardArray(b.prevBoard);
        this.board = cloneBoardArray(b.board);
        this.turn = b.turn;
    }

    public Piece[][] getBoard(){
        return this.cloneBoardArray(this.board);
    }

    public void setPiece(Position pos,Piece p){
        this.board[pos.getRow()][pos.getColumn()] = p;
    }
    public void setPiece(int r,int c,Piece p){
        this.board[r][c] = p;
    }

    /**
     * Moves a piece on the board
     * Move a piece to a new square on the board, and makes
     * its previous spot null. We also check if the piece is at
     * an edge row and make the piece a king if so. If the move is a jump,
     * we iterate through the toBeRemoved ArrayList and make sure all the pieces
     * in there are null so that they are removed.
     */
    public Move makeMove(Move mv) throws InValidMove {
        if (mv == null)
            throw new InValidMove("Move object can't be null.");

        if(this.getPieceAt(mv.getOrigin()).owner!= this.turn)
            throw new InValidMove("Not your turn.");

        for (Move move :
                this.reachablePositionsByPlayer(this.getPieceAt(mv.getOrigin()).owner)) {
            if (mv.equals(move)){
                prevBoard = cloneBoardArray(board);
                int initR = move.getOrigin().getRow();
                int initC = move.getOrigin().getColumn();
                int newR = move.getDestination().getRow();
                int newC = move.getDestination().getColumn();

                // Put the piece onto its new destination
                board[newR][newC] =  board[initR][initC].clone();
                board[initR][initC] = null;  // Make the previous position empty

                if (newR == 0 && this.turn == Piece.PieceOwner.PLAYER1)
                    board[newR][newC]= new King(this.turn);

                if (newR == BOARD_SIZE - 1 && this.turn == Piece.PieceOwner.PLAYER2)
                    board[newR][newC] = new King(this.turn);

                if (move instanceof Jump) {
                    for (Position remove : ((Jump) move).toBeRemoved) {
                        board[remove.getRow()][remove.getColumn()] = null;  // Remove all piece that are jumped over
                    }
                }

                this.turn = (this.turn==Piece.PieceOwner.PLAYER1)? Piece.PieceOwner.PLAYER2: Piece.PieceOwner.PLAYER1;
                return move ;
            }
        }
        throw new InValidMove("No such move.");

    }

    public ArrayList<Move> reachablePositions(Position position){
        if(position==null || this.getPieceAt(position)==null )
            return null;
        return this.getPieceAt(position).generateMoves(new Board(this),position);
    }

    /**
     * Returns all possible move a player can take. Iteratively calls <code>reachablePositions</code> to get
     * the moves a piece can make and adds to an ArrayList. If there are Jump type moves, those are the only moves returned.
     *
     * @param owner - the player we want to generate the list of moves for
     * @return the possible moves for a player
     *  */
    public ArrayList<Move> reachablePositionsByPlayer(Piece.PieceOwner owner){
        ArrayList<Move> jumps = new ArrayList<>();
        ArrayList<Move> moves = new ArrayList<>();

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = (1 - i % 2); j < BOARD_SIZE; j += 2) {
                Piece piece = this.getPieceAt(i,j);
                if(piece!=null && piece.owner==owner){
                    ArrayList<Move> tempMoves = this.reachablePositions(new Position(i,j));

                    if(tempMoves.size()==0)
                        continue;

                    else if(tempMoves.get(0) instanceof Jump)
                        jumps.addAll(tempMoves);
                    else
                        moves.addAll(tempMoves);
                }
            }}
        if(jumps.size()!=0)
            return jumps;
        return moves;
    }

    /**
     * Returns whose turn it is.
     *
     * @return the PieceOwner type whose turn it is.
     * */

    public Piece.PieceOwner getTurn(){
        return this.turn;
    }

    /**
     * Resets the board to its original state.
     * We iterate through the board skipping a square. The first 3 rows
     * are for player 2 and the last three row are for player 1 initially.
     * The rows in between are empty.
     */
    public void resetBoard() {

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = (1 - i % 2); j < BOARD_SIZE; j += 2) { // Makes sure the Pieces are placed on squares where i+j is odd
                if (i <= 2) {  //place player two's pawns in their starting place
                    this.board[i][j] = new Pawn( Piece.PieceOwner.PLAYER2);
                } else if (i <= 4) { // makes the middle two rows empty
                    this.board[i][j] = null;
                } else { //place player one's pawns in their starting place
                    this.board[i][j] = new Pawn( Piece.PieceOwner.PLAYER1);
                }
            }
        }

    }

    /**
     * Returns the Piece object at the give position.
     *
     * @param pos - the position of the Piece required.
     * @return Piece - returns the piece at the specified position.
     * */
    public Piece getPieceAt(Position pos) {
        int r = pos.getRow();
        int c = pos.getColumn();
        if (r < 0 || r >= BOARD_SIZE || c < 0 || c >= BOARD_SIZE)
            return null;

        return board[r][c];

    }

    /**
     * Returns the Piece object at the give row and column.
     *
     * @param r - the row of the Piece required.
     * @param c - the column of the Piece required.
     * @return Piece - returns the piece at the specified position.
     * */
    public Piece getPieceAt(int r, int c) {
        if (r < 0 || r >= BOARD_SIZE || c < 0 || c >= BOARD_SIZE)
            return null;
        return board[r][c];

    }

    /**
     * Restores the previous state of the board
     * prevBoard holds the board state just before the last move
     */
    public void undo() {
        if(prevBoard!=null){
            board = cloneBoardArray(prevBoard);
            prevBoard = null;
            this.turn = (this.turn==Piece.PieceOwner.PLAYER1)? Piece.PieceOwner.PLAYER2: Piece.PieceOwner.PLAYER1;
        }

    }


    /**
     * Iterates through the board looking at if each player has at least one possible movement.
     * If each player has at least one move, we return null symbolizing no-one is a winner here.
     * If one of players has possible moves and the other doesn't we return that player.
     *
     * @return PieceOwner type of the winning player or null if the game hasn't ended yet.
     */
    public Piece.PieceOwner isGameOver() { // TODO change the name to isThereWinner
        boolean p1HasMoves = false, p2HasMoves = false;

        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = (1 - r % 2); c < BOARD_SIZE; c += 2) { // Makes sure the Pieces are placed on squares where i+j is odd
                Piece piece = this.board[r][c];
                if (piece != null) {
                    if (piece.generateMoves(new Board(this),new Position(r,c)).size() != 0) {
                        if (piece.owner == Piece.PieceOwner.PLAYER1)
                            p1HasMoves = true;
                        else
                            p2HasMoves = true;

                        if (p1HasMoves && p2HasMoves)
                            return null;
                    }

                }
            }
        }
        if (p1HasMoves)
            return Piece.PieceOwner.PLAYER1;

        return Piece.PieceOwner.PLAYER2;
    }



    public Piece[][] cloneBoardArray(Piece[][] b){
        if(b==null)
            return b;
        Piece[][] newBoard = new Piece[BOARD_SIZE][BOARD_SIZE];

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = (1 - i % 2); j < BOARD_SIZE; j += 2) {
                if (b[i][j] != null)
                    newBoard[i][j] = b[i][j].clone();
                else
                    newBoard[i][j] = null;
            }
        }
        return newBoard;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Board))
            return false;

        Board b = (Board) obj;

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j ++) {
                {
                    Position pos = new Position(i,j);
                    if(b.getPieceAt(pos)==null && this.getPieceAt(pos)==null)
                        continue;
                    if(!b.getPieceAt(pos).equals(this.getPieceAt(pos)))
                        return false;
                }
            }
        }
        return true;
    }

    /**
     * Converts a given Multi-dimensional array of Piece to a string format.
     * <li> - means there is a null piece</li>
     * <li> o means there is a player one Pawn piece </li>
     * <li> O means there is a player one King piece </li>
     * <li> t means there is a player two Pawn piece </li>
     * <li> T means there is a player two King piece </li>
     *
     * @param b - the Piece array that is going to be converted to a string
     * @return String - the string representation of the array
     * */
    private String boardToString(Piece[][] b){
        if(b==null)
            return "";
        String str = "";
        for (Piece[] row :
                b) {
            for (Piece p :
                    row) {
                char c ;
             if(p==null){
                 str+="-";
                 continue;
             }
             if(p.owner== Piece.PieceOwner.PLAYER1)
                 c = 'o';
             else
                 c='t';

             if(p instanceof King)
                 c = Character.toUpperCase(c);

             str+=c;
            }
        }
        return str;
    }

    /**
     * Converts a string to a Multi-dimensional array of Piece.
     * <li> - means there is a null piece</li>
     * <li> o means there is a player one Pawn piece </li>
     * <li> O means there is a player one King piece </li>
     * <li> t means there is a player two Pawn piece </li>
     * <li> T means there is a player two King piece </li>
     *
     * @param str - a string representation of an array
     * @return Piece[][] -  the Piece array that the string represents
     * */
    public Piece[][] stringToBoard(String str){
        if(str.equals(""))
            return null;
        try {
            verifyArrangement(str);
            Piece[][] pieces = new Piece[BOARD_SIZE][BOARD_SIZE];
            int row=0,column=0;

            for (int i = 0; i < str.length(); i++) {

                int r = row/BOARD_SIZE;
                int c = column%BOARD_SIZE;

                Piece p ;
                switch (str.charAt(i)){
                    case 'o': p = new Pawn(Piece.PieceOwner.PLAYER1); break;
                    case 'O': p = new King(Piece.PieceOwner.PLAYER1); break;
                    case 't': p = new Pawn(Piece.PieceOwner.PLAYER2); break;
                    case 'T': p = new King(Piece.PieceOwner.PLAYER2); break;
                    default: p=null;break;
                }
                pieces[r][c]= p;
                row++;
                column++;
            }
            return pieces;

        } catch (InvalidArrangement e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Verifies that the string given is a correct arrangement for a game.
     * <li> Checks that the strings length is 64</li>
     * <li> Checks that each player has at least one piece</li>
     * @param str - a string representation of an array
     * @throws InvalidArrangement - thrown if the string is not in a correct state
     * */
    public void verifyArrangement(String str) throws InvalidArrangement {

        if(str.length()!=64)
            throw new InvalidArrangement("Invalid arrangement, length is less than 64.");

        boolean player1Exists=false, player2Exists=false;

        for (int i = 0; i < str.length(); i++) {
            char l = str.charAt(i);
            if(l=='t' || l=='T')
                player2Exists = true;
            else if(l=='o' || l=='O')
                player1Exists = true;
        }
        if(! (player2Exists && player1Exists))
            throw new InvalidArrangement("Invalid arrangement, game already over.");
    }


/**
 * Saves the current state of the board to a file.
 * <li>First, writes the current state of the board</li>
 * <li>Second, writes the current state of prevBoard</li>
 * <li>Finally, writes whose turn it is</li>
 *
 * @throws FileNotFoundException whenever there is an error accessing the file.
 * */
    public void saveGame() throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(new FileOutputStream(GAMES_PATH));
        writer.println(this.boardToString(this.board));
        writer.println(this.boardToString(this.prevBoard));
        writer.println(this.turn);
        writer.close();
    }
}
