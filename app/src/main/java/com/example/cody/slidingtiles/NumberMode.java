package com.example.cody.slidingtiles;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class NumberMode extends AppCompatActivity {

    private static GestureDetectGridView mGridView;

    private static final int COLUMNS = 5;
    private static final int DIMENSIONS = COLUMNS * COLUMNS;

    private static int mColumnWidth, mColumnHeight;

    public static final String up = "up";
    public static final String down = "down";
    public static final String left = "left";
    public static final String right = "right";

    private static String[] tileList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_mode);

        init();

        scramble();

        setDimensions();
    }

    private void init() {
        mGridView = (GestureDetectGridView) findViewById(R.id.grid);
        mGridView.setNumColumns(COLUMNS);

        tileList = new String[DIMENSIONS];
        for (int i = 0; i < DIMENSIONS; i++) {
            tileList[i] = String.valueOf(i);
        }
    }

    private void scramble() {
        int index;
        String temp;
        Random random = new Random();

        for (int i = tileList.length - 1; i > 0; i--) {
            index = random.nextInt(i + 1);
            temp = tileList[index];
            tileList[index] = tileList[i];
            tileList[i] = temp;
        }
    }

    private void setDimensions() {
        ViewTreeObserver vto = mGridView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mGridView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int displayWidth = mGridView.getMeasuredWidth();
                int displayHeight = mGridView.getMeasuredHeight();

                int statusbarHeight = getStatusBarHeight(getApplicationContext());
                int requiredHeight = displayHeight - statusbarHeight;

                mColumnWidth = displayWidth / COLUMNS;
                mColumnHeight = requiredHeight / COLUMNS;

                display(getApplicationContext());
            }
        });
    }

    private int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen",
                "android");

        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }

        return result;
    }

    private static void display(Context context) {
        ArrayList<Button> buttons = new ArrayList<>();
        Button button;

        for (int i = 0; i < tileList.length; i++) {
            button = new Button(context);

//            if (tileList[i].equals("0"))
//                button.setBackgroundResource(R.drawable.pigeon_piece1);
//            else if (tileList[i].equals("1"))
//                button.setBackgroundResource(R.drawable.pigeon_piece2);
//            else if (tileList[i].equals("2"))
//                button.setBackgroundResource(R.drawable.pigeon_piece3);
//            else if (tileList[i].equals("3"))
//                button.setBackgroundResource(R.drawable.pigeon_piece4);
//            else if (tileList[i].equals("4"))
//                button.setBackgroundResource(R.drawable.pigeon_piece5);
//            else if (tileList[i].equals("5"))
//                button.setBackgroundResource(R.drawable.pigeon_piece6);
//            else if (tileList[i].equals("6"))
//                button.setBackgroundResource(R.drawable.pigeon_piece7);
//            else if (tileList[i].equals("7"))
//                button.setBackgroundResource(R.drawable.pigeon_piece8);
//            else if (tileList[i].equals("8"))
//                button.setBackgroundResource(R.drawable.pigeon_piece9);
            if (tileList[i].equals("0"))
                button.setText("1");
            else if (tileList[i].equals("1"))
                button.setText("2");
            else if (tileList[i].equals("2"))
                button.setText("3");
            else if (tileList[i].equals("3"))
                button.setText("4");
            else if (tileList[i].equals("4"))
                button.setText("5");
            else if (tileList[i].equals("4"))
                button.setText("5");
            else if (tileList[i].equals("5"))
                button.setText("6");
            else if (tileList[i].equals("6"))
                button.setText("7");
            else if (tileList[i].equals("7"))
                button.setText("8");
            else if (tileList[i].equals("8"))
                button.setText("9");
            else if (tileList[i].equals("9"))
                button.setText("10");
            else if (tileList[i].equals("10"))
                button.setText("11");
            else if (tileList[i].equals("11"))
                button.setText("12");
            else if (tileList[i].equals("12"))
                button.setText("13");
            else if (tileList[i].equals("13"))
                button.setText("14");
            else if (tileList[i].equals("14"))
                button.setText("15");
            else if (tileList[i].equals("15"))
                button.setText("16");
            else if (tileList[i].equals("16"))
                button.setText("17");
            else if (tileList[i].equals("17"))
                button.setText("18");
            else if (tileList[i].equals("18"))
                button.setText("19");
            else if (tileList[i].equals("19"))
                button.setText("20");
            else if (tileList[i].equals("20"))
                button.setText("21");
            else if (tileList[i].equals("21"))
                button.setText("22");
            else if (tileList[i].equals("22"))
                button.setText("23");
            else if (tileList[i].equals("23"))
                button.setText("24");
            else if (tileList[i].equals("24"))
                button.setText(" ");

            buttons.add(button);
        }

        mGridView.setAdapter(new CustomAdapter(buttons, mColumnWidth, mColumnHeight));
    }

    private static void swap(Context context, int currentPosition, int swap) {
        String newPosition = tileList[currentPosition + swap];
        tileList[currentPosition + swap] = tileList[currentPosition];
        tileList[currentPosition] = newPosition;
        display(context);

        if (isSolved()) Toast.makeText(context, "YOU WIN!", Toast.LENGTH_SHORT).show();
    }

    public static void moveTiles(Context context, String direction, int position) {

        // Upper-left-corner tile
        if (position == 0) {

            if (direction.equals(right)) swap(context, position, 1);
            else if (direction.equals(down)) swap(context, position, COLUMNS);
            else Toast.makeText(context, "Invalid move", Toast.LENGTH_SHORT).show();

            // Upper-center tiles
        } else if (position > 0 && position < COLUMNS - 1) {
            if (direction.equals(left)) swap(context, position, -1);
            else if (direction.equals(down)) swap(context, position, COLUMNS);
            else if (direction.equals(right)) swap(context, position, 1);
            else Toast.makeText(context, "Invalid move", Toast.LENGTH_SHORT).show();

            // Upper-right-corner tile
        } else if (position == COLUMNS - 1) {
            if (direction.equals(left)) swap(context, position, -1);
            else if (direction.equals(down)) swap(context, position, COLUMNS);
            else Toast.makeText(context, "Invalid move", Toast.LENGTH_SHORT).show();

            // Left-side tiles
        } else if (position > COLUMNS - 1 && position < DIMENSIONS - COLUMNS &&
                position % COLUMNS == 0) {
            if (direction.equals(up)) swap(context, position, -COLUMNS);
            else if (direction.equals(right)) swap(context, position, 1);
            else if (direction.equals(down)) swap(context, position, COLUMNS);
            else Toast.makeText(context, "Invalid move", Toast.LENGTH_SHORT).show();

            // Right-side AND bottom-right-corner tiles
        } else if (position == COLUMNS * 2 - 1 || position == COLUMNS * 3 - 1) {
            if (direction.equals(up)) swap(context, position, -COLUMNS);
            else if (direction.equals(left)) swap(context, position, -1);
            else if (direction.equals(down)) {

                // Tolerates only the right-side tiles to swap downwards as opposed to the bottom-
                // right-corner tile.
                if (position <= DIMENSIONS - COLUMNS - 1) swap(context, position,
                        COLUMNS);
                else Toast.makeText(context, "Invalid move", Toast.LENGTH_SHORT).show();
            } else Toast.makeText(context, "Invalid move", Toast.LENGTH_SHORT).show();

            // Bottom-left corner tile
        } else if (position == DIMENSIONS - COLUMNS) {
            if (direction.equals(up)) swap(context, position, -COLUMNS);
            else if (direction.equals(right)) swap(context, position, 1);
            else Toast.makeText(context, "Invalid move", Toast.LENGTH_SHORT).show();

            // Bottom-center tiles
        } else if (position < DIMENSIONS - 1 && position > DIMENSIONS - COLUMNS) {
            if (direction.equals(up)) swap(context, position, -COLUMNS);
            else if (direction.equals(left)) swap(context, position, -1);
            else if (direction.equals(right)) swap(context, position, 1);
            else Toast.makeText(context, "Invalid move", Toast.LENGTH_SHORT).show();

            // Center tiles
        } else {
            if (direction.equals(up)) swap(context, position, -COLUMNS);
            else if (direction.equals(left)) swap(context, position, -1);
            else if (direction.equals(right)) swap(context, position, 1);
            else swap(context, position, COLUMNS);
        }
    }

    private static boolean isSolved() {
        boolean solved = false;

        for (int i = 0; i < tileList.length; i++) {
            if (tileList[i].equals(String.valueOf(i))) {
                solved = true;
            } else {
                solved = false;
                break;
            }
        }

        return solved;
    }
}
