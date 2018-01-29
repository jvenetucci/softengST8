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

public class MainFragment extends Fragment{
    private Button btnNum;
    private Button btnMath;
    private Button btnScore;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment_layout, container, false);
        btnNum = (Button) view.findViewById(R.id.btnNum);
        btnMath = (Button) view.findViewById(R.id.btnMath);
        btnScore = (Button) view.findViewById(R.id.btnScore);

        btnNum.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Go to Number mode", Toast.LENGTH_SHORT).show();
                ((MainActivity)getActivity()).setViewPager(1);
            }
        });
        btnMath.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Go to Math mode", Toast.LENGTH_SHORT).show();
                ((MainActivity)getActivity()).setViewPager(2);
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
        return view;
    }
}
