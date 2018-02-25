package com.example.cody.slidingtiles;


import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class NumberFragment extends Fragment{
    private Button btnSolo;
    private Button btnComp;
    private Button btnScore;
    private Button btnBack;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.number_fragment_layout, container, false);
        btnSolo = (Button) view.findViewById(R.id.btnSolo);
        btnComp = (Button) view.findViewById(R.id.btnComp);
        btnScore = (Button) view.findViewById(R.id.btnScore);
        btnBack = (Button) view.findViewById(R.id.btnBack);

        btnSolo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),NumberMode.class);
                startActivity(intent);
            }
        });
        btnComp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),NumberModeAI.class);
                startActivity(intent);
            }
        });
        btnScore.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),HighScores.class);
                startActivity(intent);
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).setViewPager(0);
            }
        });
        return view;
    }
}
