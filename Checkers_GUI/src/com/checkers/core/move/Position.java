package com.checkers.core.move;

public class Position {
    private int row;
    private int column;

    public Position(int row, int column){
        this.row = row;
        this.column=column;
    }
    public Position(Position p){
        this.row = p.row;
        this.column = p.column;
    }

    public int getRow() {
        return row;
    }
    public  int getColumn(){
        return column;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;
        if(!(obj instanceof Position))
            return false;

        Position p = (Position) obj;
        return (this.column==p.column && this.row==p.row);
    }
}
