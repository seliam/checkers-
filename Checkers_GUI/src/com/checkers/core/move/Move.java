package com.checkers.core.move;

public class Move {

    private Position origin;
    private Position destination;
    public  Move(Position origin, Position destination){
        this.origin = origin;
        this.destination = destination;
    }
    public Position getOrigin() {
        return origin;
    }

    public Position getDestination() {
        return destination;
    }
    @Override
    public String toString() {
        return String.format("Move from [%d,%d] to [%d,%d]",origin.getRow(),origin.getColumn(),destination.getRow(),destination.getColumn());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Move)) return false;
        Move move = (Move) o;
        return (this.origin.equals(move.origin) && this.destination.equals(move.destination));
    }

}
