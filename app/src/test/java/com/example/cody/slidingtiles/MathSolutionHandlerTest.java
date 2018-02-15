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

    @Mock
    MathSolutionHandler mockSolutionHandler;

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
    public void fakeTest1() {

    }

    @Test
    public void fakeTest2() {

    }
    @Test
    public void fakeTest3() {

    }
    @Test
    public void fakeTest4() {

    }
    @Test
    public void fakeTest5() {

    }

//    @Test
//    public void solveReturns2WhenEquationEquals2() {
//        when(mockSolutionHandler.getEquationNumberArray()).thenReturn(new int[]{1, 11, 1, 10, 2});
//        MathSolutionHandler solutionHandler = mockSolutionHandler;
//
//        assertEquals(2,solutionHandler.solve());
//    }

//    @Test
//    public void solveReturnsMinus1WhenEquationHasBadFormat() {
//        when(mockSolutionHandler.getEquationNumberArray()).thenReturn(new int[]{1, 1, 1, 10, 1});
//        MathSolutionHandler solutionHandler = mockSolutionHandler;
//
//        assertEquals(-2,solutionHandler.solve());
//    }
}