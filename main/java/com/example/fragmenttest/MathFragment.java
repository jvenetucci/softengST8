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

public class MathFragment extends Fragment{
    private Button btnSolo;
    private Button btnMulti;
    private Button btnScore;
    private Button btnBack;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.math_fragment_layout, container, false);
        btnSolo = (Button) view.findViewById(R.id.btnSolo);
        btnMulti = (Button) view.findViewById(R.id.btnMulti);
        btnScore = (Button) view.findViewById(R.id.btnScore);
        btnBack = (Button) view.findViewById(R.id.btnBack);

        btnSolo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Solo Math", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(),ScoreActivity.class);
                startActivity(intent);
            }
        });
        btnMulti.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "2 Player Math", Toast.LENGTH_SHORT).show();
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
        btnBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Go to Home", Toast.LENGTH_SHORT).show();
                ((MainActivity)getActivity()).setViewPager(0);
            }
        });
        return view;
    }
}
