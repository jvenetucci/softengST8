package com.example.cody.slidingtiles;


// holds a username and associated score.
// Used for firebase stuff
// See DatabaseHandler.java
public class Record {
    private String name;
    private int score;

    public String getName() {return this.name;}
    public int getScore() {return this.score;}

    // Default Empty Constructor
    Record() {
    }

    // Constructor with Args
    Record(String name, int score) {
        this.name = name;
        this.score = score;
    }


}

