package com.example.cody.slidingtiles;

import android.graphics.Color;
import android.widget.Button;

import java.util.ArrayList;
import java.util.LinkedHashSet;

/**
 * A class that handles the player submissions from Math Mode
 * The MathMode Activity should send the submitted tiles to this class during a touchEvent
 * When the touchEvent registers an ACTION_UP (The user has lifted their finger off the screen)
 *  the activity should call the solve() method of this class.
 * This class will also change the color of the tiles as the user is submitting them.
 */
class MathSolutionHandler {
    LinkedHashSet<Button> tiles; //Used over HashSet because it keeps the insertion order
    ArrayList<String> solutionBlackList; //Keep a list of valid submitted solutions


    MathSolutionHandler() {
        tiles = new LinkedHashSet<>();
        solutionBlackList = new ArrayList<>();
    }

    // Adds a tile to the tiles collection.
    // Returns false if the tile already exists in the collection
    // or if there are already 5 tiles submitted
    boolean addTile(Button tile) {
        if (tiles.size() >= 5) {
            return false;
        } else if (!tiles.add(tile)) {
            return false;
        } else {
            tile.setTextColor(Color.RED);
            return true;
        }
    }

    // Solves the player submitted solution
    // Returns the value of the equation which should be added to players score
    // Returns -1 for invalid solutions
    public int solve() {
        if (tiles.size() != 5) {
            return -1;
        }
        return solveEquation(getEquationNumberArray());
    }

    // Verify if the equation passed in as an array is valid
    // Checks for format, correctness, and if its already been used.
    // Returns:
    //  -3 For already used solution
    //  -2 For incorrect format
    //  -1 For invalid equation
    //  Any other number for the value of the equation
    int solveEquation(int[] equation) {
        int score = 0;
        int equalsIndex;
        int operator;

        // format check
        for (int i = 0; i <5; i++ ){
            if(equation[i] == -1){
                return -2;
            }
            else if (i % 2 == 0){
                if (equation[i] >= 10){
                    return -2;
                }
            }
            else{
                if (equation [i] <= 9){
                    return -2;
                }
            }
        }

        //Are there two equals?
        if (equation[1] == 10 && equation[3] == 10) {
            return -2;
        }
        //Are there no equals?
        if (equation[1] != 10 && equation[3] != 10) {
            return -2;
        }

        if (equation[1] == 10) {
            equalsIndex = 1;
            operator = equation[3];
        } else {
            equalsIndex = 3;
            operator = equation[1];
        }

        // If a operator is addition (11) or multiplication (13) and is reversed
        // put the equation in the form Num Sym Num EQ Num
        if ((operator == 11 || operator == 13) && equalsIndex == 1) {
            int temp[]= new int[5];
            for(int i = 0; i <5; i++){
                temp[i] = equation[4-i];
            }
            System.arraycopy(temp, 0, equation, 0, 5);
            equalsIndex = 3;
        }

        // If a operator is subtraction (12) or division (14) and the numbers are the same
        // The equation is the same reversed.
        if ((operator == 12 || operator == 14)) {
            if ((equalsIndex == 1) && (equation[2] == equation[4])) {   // NumA = NumB / NumB
                int temp[]= new int[5];
                for(int i = 0; i <5; i++){
                    temp[i] = equation[4-i];
                }
                System.arraycopy(temp, 0, equation, 0, 5);
                equalsIndex = 3;
            }
        }

        // Check to see if we already used this equation
        String equaString;
        equaString = getEquationString(equation);
        if(solutionBlackList.contains(equaString)) {
            return -3;
        }

        // equation check.
        if (equalsIndex == 1) {
            score = solveReversedEquation(equation);
        } else {
            score = solveNormalEquation(equation);
        }

        if (score >= 0) {
            solutionBlackList.add(equaString);
        }
        return score;
    }

    private int solveNormalEquation(int[] equation) {
        switch (equation[1]) {
            case 11://add
                if (equation[4] == equation[0] + equation[2]) {
                    return equation[4];
                } else {
                    return -1;
                }
            case 12://subtract
                if (equation[4] == equation[0] - equation[2]) {
                    return equation[4];
                } else {
                    return -1;
                }
            case 13://multiply
                if (equation[4] == equation[0] * equation[2]) {
                    return equation[4];
                } else {
                    return -1;
                }
            case 14://divide
                if (equation[2] == 0 ){ // Never divide by zero!
                    return -1;
                } else if (equation[4] == (equation[0] / equation[2])) {
                    return equation[4];
                } else {
                    return -1;
                }
            default:
                return -1;
        }
    }

    private int solveReversedEquation(int[] equation) {
        switch (equation[3]) {
            case 11://add
                if (equation[0] == equation[2] + equation[4]) {
                    return equation[0];
                } else {
                    return -1;
                }
            case 12://subtract
                if (equation[0] == equation[2] - equation[4]) {
                    return equation[0];
                } else {
                    return -1;
                }
            case 13://multiply
                if (equation[0] == equation[2] * equation[4]) {
                    return equation[0];
                } else {
                    return -1;
                }
            case 14://divide
                if (equation[4] == 0 ){ // Never divide by zero!
                    return -1;
                } else if (equation[0] == (equation[2] / equation[4])) {
                    return equation[0];
                } else {
                    return -1;
                }
            default:
                return -1;
        }
}


    // Resets the handler by clearing the tiles from the collection
    void resetHandler() {
        for (Button tile : tiles) {
            tile.setTextColor(Color.BLACK);
        }
        tiles.clear();
    }

    // Get a string representing the equation from the current tiles in the collection
    String getEquationString() {
        StringBuilder equation = new StringBuilder();
        for (Button tile : tiles) {
            equation.append(tile.getText().toString()).append(" ");
        }
        return equation.toString();
    }

    // Get a string representing the equation from an integer array that represents the equation.
    String getEquationString(int equationNumberArray []) {
        StringBuilder equation = new StringBuilder();
        for (int num : equationNumberArray) {
            equation.append(convertToStringValue(num)).append(" ");
        }
        return equation.toString();
    }

    // Get a 5 element array of integers that represent the equation
    // Should only be used when the collection has 5 tiles.
    protected int[] getEquationNumberArray() {
        int equationNumberArray[] = new int [5];
        int i = 0;
        for (Button tile: tiles) {
            equationNumberArray[i] = convertToIntValue(tile);
            i++;
        }
        return equationNumberArray;
    }

    // Given a tile, take the text on that tile and convert it to its integer representation
    protected int convertToIntValue(Button tile) {
        String buttonText = tile.getText().toString();
        switch(buttonText) {
            case " ":
                return -1;
            case "=":
                return 10;
            case "+":
                return 11;
            case "-":
                return 12;
            case "*":
                return 13;
            case "/":
                return 14;
            default:
                return Integer.parseInt(buttonText);
        }
    }

    // Given an integer, convert it to its string representation
    protected String convertToStringValue(int value) {
        String stringValue;

        switch(value) {
            case -1:
                stringValue = " ";
                return stringValue;
            case 10:
                stringValue = "=";
                return stringValue;
            case 11:
                stringValue = "+";
                return stringValue;
            case 12:
                stringValue = "-";
                return stringValue;
            case 13:
                stringValue = "*";
                return stringValue;
            case 14:
                stringValue = "/";
                return stringValue;
            default:
                stringValue = Integer.toString(value);
                return stringValue;
        }
    }

    int getCountOfSubmittedTiles() {
        return tiles.size();
    }

    //Player2's solutions
    public void addToSolutionBlackList(String string){
        solutionBlackList.add(string);
    }
}
