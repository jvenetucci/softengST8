package com.example.fragmenttest;


import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

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
                Toast.makeText(getActivity(), "Solo numbers", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(),ScoreActivity.class);
                startActivity(intent);
            }
        });
        btnComp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Numbers AI", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(),ScoreActivity.class);
                startActivity(intent);
            }
        });
        btnScore.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Go to Scores", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(),ScoreActivity.class);
                startActivity(intent);
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Go to Home", Toast.LENGTH_SHORT).show();
                ((MainActivity) getActivity()).setViewPager(0);
            }
        });
        return view;
    }
}
