package edu.northeastern.finalproject.MoodFragment;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.northeastern.finalproject.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WeekMoodFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeekMoodFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View mRootView;

    public WeekMoodFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WeekMoodFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WeekMoodFragment newInstance(String param1, String param2) {
        WeekMoodFragment fragment = new WeekMoodFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView != null) {
            ViewGroup parent = (ViewGroup) mRootView.getParent();
            if (parent != null)
                parent.removeView(mRootView);
        } else {
            mRootView = inflater.inflate(R.layout.fragment_mood_week, container, false);

            View colorDot = mRootView.findViewById(R.id.calendarViewWeek_colorSunday);
            colorDot.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#2196F3")));
            colorDot = mRootView.findViewById(R.id.calendarViewWeek_colorMonday);
            colorDot.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
            colorDot = mRootView.findViewById(R.id.calendarViewWeek_colorTuesday);
            colorDot.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFEB3B")));
            colorDot = mRootView.findViewById(R.id.calendarViewWeek_colorWednesday);
            colorDot.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#2196F3")));
            colorDot = mRootView.findViewById(R.id.calendarViewWeek_colorThursday);
            colorDot.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFEB3B")));
            colorDot = mRootView.findViewById(R.id.calendarViewWeek_colorFriday);
            colorDot.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
            colorDot = mRootView.findViewById(R.id.calendarViewWeek_colorSaturday);
            colorDot.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFEB3B")));
        }
        return mRootView;
    }
}