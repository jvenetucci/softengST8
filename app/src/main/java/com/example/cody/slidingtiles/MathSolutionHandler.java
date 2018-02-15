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
    ArrayList<int []> solutionBlackList; //Keep a list of valid submitted solutions


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
        int score = 0;
        if (tiles.size() != 5) {
            return -1;
        }
        int equation[] = getEquationNumberArray();
        // format check
        for (int i = 0; i <5; i++ ){
            if(equation[i] == -1){
                return -2;
            }
            if (i % 2 == 0){
                if (equation[i] > 11){
                    return -2;
                }
            }
            else{
                if (equation [i] < 9){
                    return -2;
                }
            }
        }

        // change to the form Num Sym Num EQ Num
        if (equation [1] == 10) {
            int temp[]= new int[5];
            for(int i = 0; i <5; i++){
                temp[i] = equation[4-i];
            }
            for(int j = 0; j <5; j++){
                equation[j] = temp[j];
            }
        }
        // equation check.
        switch(equation[1]){
            case 11://add
                if(equation[4] == equation[0] + equation[2]){
                    score = equation[4];
                }
                break;
            case 12://subtract
                if(equation[0] == 0){
                    score = 0;
                }else if(equation[4] == equation[0] - equation[2]){
                    score = equation[4];
                }
                break;
            case 13://multiply
                if(equation[4] == equation[0] * equation[2]){
                    score = equation[4];
                }
                break;
            case 14://divide
                if(equation[0] == 0 || equation[2] == 0 || equation[4] == 0){
                    return 0;
                }else if(equation[4] == equation[0] / equation[2]){
                    score = equation[4];
                }
                break;
            default:
                break;
        }
        return score;


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
    private int[] getEquationNumberArray() {
        int equationNumberArray[] = new int [5];
        int i = 0;
//        for (int i = 0; i < 5; i++) {
            for (Button tile: tiles) {
                equationNumberArray[i] = convertToIntValue(tile);
                i++;
            }
//        }
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
}
