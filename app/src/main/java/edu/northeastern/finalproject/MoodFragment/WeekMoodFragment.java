package edu.northeastern.finalproject.MoodFragment;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import edu.northeastern.finalproject.R;

public class WeekMoodFragment extends Fragment {

    private FirebaseFirestore firebaseFirestore;
    private String userId;
    private View mRootView;

    public WeekMoodFragment() {
        // Required empty public constructor
    }

    public static WeekMoodFragment newInstance() {
        return new WeekMoodFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseFirestore = FirebaseFirestore.getInstance();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_mood_week, container, false);
            fetchMoodData();
        }
        return mRootView;
    }
    private void fetchMoodData() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        for (int i = 0; i < 7; i++) {
            // Use a separate calendar instance for each query
            Calendar queryCalendar = (Calendar) calendar.clone();
            queryCalendar.add(Calendar.DAY_OF_YEAR, -i); // Go back i days from today
            Date date = queryCalendar.getTime();
            String formattedDate = dateFormat.format(date);

            DocumentReference dailyRecordRef = firebaseFirestore
                    .collection("users")
                    .document(userId)
                    .collection("dailyRecords")
                    .document(formattedDate);

            final int dayIndex = i;
            dailyRecordRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists() && document.contains("mood")) {
                        Integer moodValue = document.getLong("mood").intValue();
                        updateMoodColorForDay(dayIndex, moodValue);
                    }
                }
            });
        }
    }

    private void updateMoodColorForDay(int dayIndex, int moodValue) {
        View colorDot;
        switch (dayIndex) {
            case 0:
                colorDot = mRootView.findViewById(R.id.calendarViewWeek_colorSunday);
                break;
            case 1:
                colorDot = mRootView.findViewById(R.id.calendarViewWeek_colorMonday);
                break;
            case 2:
                colorDot = mRootView.findViewById(R.id.calendarViewWeek_colorTuesday);
                break;
            case 3:
                colorDot = mRootView.findViewById(R.id.calendarViewWeek_colorWednesday);
                break;
            case 4:
                colorDot = mRootView.findViewById(R.id.calendarViewWeek_colorThursday);
                break;
            case 5:
                colorDot = mRootView.findViewById(R.id.calendarViewWeek_colorFriday);
                break;
            case 6:
                colorDot = mRootView.findViewById(R.id.calendarViewWeek_colorSaturday);
                break;
            default:
                return;
        }
        setMoodColor(colorDot, moodValue);
    }

    private void setMoodColor(View colorDot, int moodValue) {
        if (moodValue >= 0 && moodValue <= 4) {
            colorDot.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.mood_blue)));
        } else if (moodValue > 4 && moodValue <= 7) {
            colorDot.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.mood_green))); // Green
        } else if (moodValue > 7 && moodValue <= 10) {
            colorDot.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.mood_yellow))); // Yellow
        }
    }

}