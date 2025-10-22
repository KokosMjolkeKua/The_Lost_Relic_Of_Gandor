package com.lostrelic;

public class PuzzleRoom extends GenericRoom {
    private final Riddle riddle;

    public PuzzleRoom(String description, Riddle riddle) {
        super(description);
        this.riddle = riddle;
    }

    public Riddle getRiddle() { return riddle; }
    public boolean solveRiddle(String attempt) { return riddle.solve(attempt); }
}
