package edu.northeastern.finalproject.MoodFragment;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import edu.northeastern.finalproject.Auth.LoginActivity;
import edu.northeastern.finalproject.R;

public class WeekMoodFragment extends Fragment {

    private FirebaseFirestore firebaseFirestore;
    private String userId;
    private View mRootView;
    private ImageView icStatusSignal;
    private ImageView sideBar;
    private TextView quoteTextView;

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
            fetchMoodAndQuote();
        }
        icStatusSignal = mRootView.findViewById(R.id.ic_status_signal);
        sideBar = mRootView.findViewById(R.id.ic_calender);

        icStatusSignal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });

        sideBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        return mRootView;
    }
    private void fetchMoodAndQuote() {
        Calendar today = Calendar.getInstance();
        int todayIndex = today.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY; // Sunday is 0, Monday is 1, etc.

        Calendar startOfWeek = (Calendar) today.clone();
        startOfWeek.add(Calendar.DAY_OF_YEAR, -todayIndex); // Set to the start of the current week

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        for (int i = 0; i < 7; i++) {
            Calendar queryCalendar = (Calendar) startOfWeek.clone();
            queryCalendar.add(Calendar.DAY_OF_YEAR, i);
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
                    if (document != null && document.exists()) {
                        if (document.contains("mood")) {
                            Long moodLong = document.getLong("mood");
                            if (moodLong != null) {
                                Integer moodValue = moodLong.intValue();
                                updateMoodColorForDay(dayIndex, moodValue);
                            }
                        }
                        if (document.contains("dailyQuote")) {
                            String quote = document.getString("dailyQuote");
                            updateQuoteForDay(dayIndex, quote);
                        }
                    }
                }
            });
        }
    }

    private void updateQuoteForDay(int dayIndex, String quote) {
        switch (dayIndex) {
            case 0:
                quoteTextView = mRootView.findViewById(R.id.calendarViewWeek_quoteSunday);
                break;
            case 1:
                quoteTextView = mRootView.findViewById(R.id.calendarViewWeek_quoteMonday);
                break;
            case 2:
                quoteTextView = mRootView.findViewById(R.id.calendarViewWeek_quoteTuesday);
                break;
            case 3:
                quoteTextView = mRootView.findViewById(R.id.calendarViewWeek_quoteWednesday);
                break;
            case 4:
                quoteTextView = mRootView.findViewById(R.id.calendarViewWeek_quoteThursday);
                break;
            case 5:
                quoteTextView = mRootView.findViewById(R.id.calendarViewWeek_quoteFriday);
                break;
            case 6:
                quoteTextView = mRootView.findViewById(R.id.calendarViewWeek_quoteSaturday);
                break;
            default:
                return;
        }
        quoteTextView.setText(quote != null ? quote : ""); // Set the quote or leave it blank
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
            colorDot.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.mood_blue))); // blue
        } else if (moodValue > 4 && moodValue <= 7) {
            colorDot.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.mood_green))); // Green
        } else if (moodValue > 7 && moodValue <= 10) {
            colorDot.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.mood_yellow))); // Yellow
        }
    }

    private void showPopupMenu(View view){
        PopupMenu popupMenu = new PopupMenu(getActivity(), view);
        popupMenu.getMenu().add(Menu.NONE, Menu.FIRST, Menu.NONE, "Log Out");
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == Menu.FIRST) {
                logoutUser();
                return true;
            }
            return false;
        });
        popupMenu.show();
    }

    private void logoutUser(){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
    private void showDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_sidebar);

        LinearLayout dailyLayout = dialog.findViewById(R.id.layoutDaily);
        LinearLayout weekLayout = dialog.findViewById(R.id.layoutWeek);
        LinearLayout monthLayout = dialog.findViewById(R.id.layoutMonth);

        dailyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container, new AddMoodFragment());
                transaction.commit();
                dialog.dismiss();
            }
        });

        weekLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container, new WeekMoodFragment());
                transaction.commit();
                dialog.dismiss();
            }
        });

        monthLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container, new MonthMoodFragment());
                transaction.commit();
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialoAnimation;
        dialog.getWindow().setGravity(Gravity.END);

    }
}