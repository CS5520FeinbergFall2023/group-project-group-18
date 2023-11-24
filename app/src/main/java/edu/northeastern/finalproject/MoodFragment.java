package edu.northeastern.finalproject;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

public class MoodFragment extends Fragment {

    private SeekBar moodSeekBar;
    private TextView moodTextGood, moodTextAverage, moodTextBad;

    public MoodFragment() {
        // Required empty public constructor
    }

    public static MoodFragment newInstance() {
        return new MoodFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mood, container, false);
        moodSeekBar = view.findViewById(R.id.moodSeekBar);
        moodTextGood = view.findViewById(R.id.moodTextGood);
        moodTextAverage = view.findViewById(R.id.moodTextAverage);
        moodTextBad = view.findViewById(R.id.moodTextBad);

        moodSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // No action needed
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // No action needed
            }
        });

        return view;
    }
}
