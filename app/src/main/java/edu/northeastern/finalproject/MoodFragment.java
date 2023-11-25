package edu.northeastern.finalproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import java.text.DateFormatSymbols;


public class MoodFragment extends Fragment {

    private SeekBar moodSeekBar;
    private ImageView moodImageGood, moodImageAverage, moodImageBad;

    private TextView moodValueText;

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
        moodImageGood = view.findViewById(R.id.moodImageGood);
        moodImageAverage = view.findViewById(R.id.moodImageAverage);
        moodImageBad = view.findViewById(R.id.moodImageBad);
        moodValueText = view.findViewById(R.id.moodValueText);

        moodImageGood.setImageResource(R.drawable.ic_mood_good);
        moodImageAverage.setImageResource(R.drawable.ic_mood_average);
        moodImageBad.setImageResource(R.drawable.ic_mood_bad);

        moodSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int moodValue = seekBar.getProgress();
                moodValueText.setText(String.valueOf(moodValue));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // No action needed
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int moodValue = seekBar.getProgress();
                moodValueText.setText(String.valueOf(moodValue));

                new AlertDialog.Builder(getContext())
                        .setMessage("Do you want to save mood？")
                        .setPositiveButton("save", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                saveMoodValue(moodValue);
                                Toast.makeText(getContext(), "save successfully!", Toast.LENGTH_SHORT).show(); //show saved info
                            }
                        })
                        .setNegativeButton("cancel", null)
                        .show();

            }


        });

        return view;
    }
    private void saveMoodValue(int moodValue) {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = dateFormat.format(calendar.getTime());
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        String dayOfWeekStr = new DateFormatSymbols().getWeekdays()[dayOfWeek];

        SharedPreferences preferences = this.getActivity().getSharedPreferences("MoodPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();


        editor.putInt("MoodValue", moodValue);
        editor.putString("Date", currentDate);
        editor.putString("DayOfWeek", dayOfWeekStr);
        editor.apply();


        showSavedMoodData(currentDate, dayOfWeekStr, moodValue);
    }

    private void showSavedMoodData(String date, String dayOfWeek, int moodValue) {

        String message = String.format(Locale.getDefault(), "%s, %s, %d", date, dayOfWeek, moodValue);


        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }


}

