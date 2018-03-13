package com.example.cody.slidingtiles;

import android.widget.Button;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the MathSolutionHandler class
 */
@RunWith(MockitoJUnitRunner.class)
public class MathSolutionHandlerTest {

    @Mock
    private
    Button mockButton;

    @Test
    public void tilesSetEmptyAfterConstructor() {
        MathSolutionHandler solutionHandler = new MathSolutionHandler();
        assertTrue(solutionHandler.tiles.isEmpty());
    }

    @Test
    public void buttonInSetWhenAdded(){
        Button someButton = new Button(null);
        MathSolutionHandler solutionHandler = new MathSolutionHandler();
        solutionHandler.tiles.add(someButton);

        assertTrue(solutionHandler.tiles.contains(someButton));
    }

    @Test
    public void buttonNotInSetWhenNotAdded(){
        Button someButton = new Button(null);
        MathSolutionHandler solutionHandler = new MathSolutionHandler();

        assertFalse(solutionHandler.tiles.contains(someButton));
    }

    @Test
    public void tilesReturnsSize1With1Buttons(){
        Button someButton = new Button(null);
        MathSolutionHandler solutionHandler = new MathSolutionHandler();
        solutionHandler.tiles.add(someButton);

        assertEquals(solutionHandler.tiles.size(), 1);
    }

    @Test
    public void tilesReturnsSize2With2Buttons(){
        Button someButton = new Button(null);
        Button anotherButton = new Button(null);
        MathSolutionHandler solutionHandler = new MathSolutionHandler();
        solutionHandler.tiles.add(someButton);
        solutionHandler.tiles.add(anotherButton);

        assertEquals(solutionHandler.tiles.size(), 2);
    }

    @Test
    public void tilesWontContainDuplicate() {
        Button someButton = new Button(null);
        MathSolutionHandler solutionHandler = new MathSolutionHandler();
        solutionHandler.tiles.add(someButton);

        assertFalse(solutionHandler.tiles.add(someButton));
    }

    @Test
    public void tilesIsEmptyAfterClear() {
        Button someButton = new Button(null);
        MathSolutionHandler solutionHandler = new MathSolutionHandler();
        solutionHandler.tiles.add(someButton);

        assertTrue(solutionHandler.tiles.contains(someButton));

        solutionHandler.tiles.clear();
        assertEquals(solutionHandler.tiles.size(), 0);
    }

    @Test
    public void convertToIntValueReturns10ForEquals() {
        when(mockButton.getText()).thenReturn("=");
        Button someButton = mockButton;

        MathSolutionHandler solutionHandler = new MathSolutionHandler();

        assertEquals(solutionHandler.convertToIntValue(someButton), 10);
    }

    @Test
    public void convertToIntValueReturnsMinus1ForBlank() {
        when(mockButton.getText()).thenReturn(" ");
        Button someButton = mockButton;

        MathSolutionHandler solutionHandler = new MathSolutionHandler();

        assertEquals(solutionHandler.convertToIntValue(someButton), -1);
    }

    @Test
    public void convertToIntValueReturns2For2() {
        when(mockButton.getText()).thenReturn("2");
        Button someButton = mockButton;

        MathSolutionHandler solutionHandler = new MathSolutionHandler();

        assertEquals(solutionHandler.convertToIntValue(someButton), 2);
    }

    @Test
    public void whenLessThan5TilesSubmittedReturnMinus1() {
        Button someButton = new Button(null);
        MathSolutionHandler solutionHandler = new MathSolutionHandler();
        solutionHandler.tiles.add(someButton);

        assertEquals(solutionHandler.solve(), -1);
    }

    @Test
    public void getEquationNumberArrayReturnsCorrectString() {
        MathSolutionHandler solutionHandler = new MathSolutionHandler();
        int [] equationArray = {1, 11, 3, 10, 4}; // 1 + 3 = 4

        assertEquals(solutionHandler.getEquationString(equationArray), "1 + 3 = 4 ");
    }

    @Test
    public void getEquationNumberArrayReturnsCorrectStringReversed() {
        MathSolutionHandler solutionHandler = new MathSolutionHandler();
        int [] equationArray = {5, 10, 2, 11, 3}; // 5 = 2 + 3

        assertEquals(solutionHandler.getEquationString(equationArray), "5 = 2 + 3 ");
    }

    @Test
    public void solveReturns2WhenEquationEquals2() {
        int equation [] = new int [] {1, 11, 1, 10, 2}; //1 + 1 = 2
        MathSolutionHandler solutionHandler = new MathSolutionHandler();
        assertEquals(2,solutionHandler.solveEquation(equation));
    }

    @Test
    public void solveReversesAdditionEquation() {
        int equation [] = new int [] {2, 10, 1, 11, 1}; // 2 = 1 + 1
        MathSolutionHandler solutionHandler = new MathSolutionHandler();
        assertEquals(2,solutionHandler.solveEquation(equation));
    }

    @Test
    public void solveReturns8WhenEquationEquals8() {
        int equation [] = new int [] {9, 12, 1, 10, 8}; // 9 - 1 = 8
        MathSolutionHandler solutionHandler = new MathSolutionHandler();
        assertEquals(8,solutionHandler.solveEquation(equation));
    }

    @Test
    public void solveReversesSubtractionEquation() {
        int equation [] = new int [] {8, 10, 9, 12, 1}; // 8 = 1 - 9
        MathSolutionHandler solutionHandler = new MathSolutionHandler();
        assertEquals(8,solutionHandler.solveEquation(equation));
    }

    @Test
    public void solveReturns4WhenEquationEquals4() {
        int equation [] = new int [] {2, 13, 2, 10, 4}; // 2 * 2 = 4
        MathSolutionHandler solutionHandler = new MathSolutionHandler();
        assertEquals(4,solutionHandler.solveEquation(equation));
    }

    @Test
    public void solveReversesMultiplicationEquation() {
        int equation [] = new int [] {4, 10, 2, 13, 2}; // 4 = 2 * 2
        MathSolutionHandler solutionHandler = new MathSolutionHandler();
        assertEquals(4,solutionHandler.solveEquation(equation));
    }

    @Test
    public void solveReturns3WhenEquationEquals3() {
        int equation [] = new int [] {6, 14, 2, 10, 3}; // 6 / 2 = 3
        MathSolutionHandler solutionHandler = new MathSolutionHandler();
        assertEquals(3,solutionHandler.solveEquation(equation));
    }

    // REVERSE DOES NOT WORK FOR DIVISION
    @Test
    public void solveReversesDivisionEquation() {
        int equation [] = new int [] {3, 10, 6, 14, 2}; // 3 = 6 / 2
        MathSolutionHandler solutionHandler = new MathSolutionHandler();
        assertEquals(3,solutionHandler.solveEquation(equation));
    }

    @Test
    public void solveReturnsMinus2WhenBadFormat() {
        int equation [] = new int [] {9, 9, 9, 14, 12}; // 9 9 9 / -
        MathSolutionHandler solutionHandler = new MathSolutionHandler();
        assertEquals(-2,solutionHandler.solveEquation(equation));
    }

    @Test
    public void solveReturnsMinus2WhenEquationContainsBlankTile() {
        int equation [] = new int [] {1, 11, -1, 10, 2}; // 1 + " " = 2
        MathSolutionHandler solutionHandler = new MathSolutionHandler();
        assertEquals(-2,solutionHandler.solveEquation(equation));
    }

    @Test
    public void solveReturnsMinus2WhenEquationContainsAllNumbers() {
        int equation [] = new int [] {1, 2, 3, 4, 5}; // 1 2 3 4 5
        MathSolutionHandler solutionHandler = new MathSolutionHandler();
        assertEquals(-2,solutionHandler.solveEquation(equation));
    }

    @Test
    public void solveReturnsMinus2WhenEquationHasOperatorInNumberPosition() {
        int equation [] = new int [] {1, 12, 12, 10, 2}; // 1 + + = 2
        MathSolutionHandler solutionHandler = new MathSolutionHandler();
        assertEquals(-2,solutionHandler.solveEquation(equation));
    }

    @Test
    public void solveReturnsMinus2WhenEquationHasNumberInOperatorPosition() {
        int equation [] = new int [] {1, 3, 1, 10, 2}; // 1 3 1 = 2
        MathSolutionHandler solutionHandler = new MathSolutionHandler();
        assertEquals(-2,solutionHandler.solveEquation(equation));
    }

    @Test
    public void solveReturnsMinus2WhenEquationMissingEqualsOperator() {
        int equation [] = new int [] {1, 12, 1, 12, 0}; // 1 - 1 - 2
        MathSolutionHandler solutionHandler = new MathSolutionHandler();
        assertEquals(-2,solutionHandler.solveEquation(equation));
    }

    @Test
    public void solveReturnsMinus1WhenEquationSubtractsFrom0() {
        int equation [] = new int [] {0, 12, 1, 10, 1}; // 0 - 1 = 1
        MathSolutionHandler solutionHandler = new MathSolutionHandler();
        assertEquals(-1,solutionHandler.solveEquation(equation));
    }

    @Test
    public void solveReturns0WhenAdditionEquationIsWrong() {
        int equation [] = new int [] {2, 11, 6, 10, 5}; // 2 + 6 = 5
        MathSolutionHandler solutionHandler = new MathSolutionHandler();
        assertEquals(-1,solutionHandler.solveEquation(equation));
    }

    @Test
    public void solveReturnsMinus1WhenDividingBy0() {
        int equation [] = new int [] {2, 14, 0, 10, 1}; // 2 / 0 = 1
        MathSolutionHandler solutionHandler = new MathSolutionHandler();
        assertEquals(-1,solutionHandler.solveEquation(equation));
    }
}