package com.example.cody.slidingtiles;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class MainFragment extends Fragment {
    private Button btnNum;
    private Button btnMath;
    private Button btnScore;
    private Button btnBluetooth;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment_layout, container, false);
        btnNum = (Button) view.findViewById(R.id.btnNum);
        btnMath = (Button) view.findViewById(R.id.btnMath);
        btnScore = (Button) view.findViewById(R.id.btnScore);
        btnBluetooth = (Button) view.findViewById(R.id.btnBluetooth);

        btnNum.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).setViewPager(1);
            }
        });
        btnMath.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).setViewPager(2);
            }
        });
        btnScore.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),HighScores.class);
                startActivity(intent);
            }
        });
        btnBluetooth.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),BluetoothActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }
}
