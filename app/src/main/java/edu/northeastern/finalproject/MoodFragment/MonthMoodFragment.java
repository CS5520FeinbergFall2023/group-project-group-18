package edu.northeastern.finalproject.MoodFragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import edu.northeastern.finalproject.Auth.LoginActivity;
import edu.northeastern.finalproject.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarLayout;
import com.haibin.calendarview.CalendarView;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class MonthMoodFragment extends Fragment {

    protected View mRootView;
    CalendarView mCalendarView;
    private ImageView icStatusSignal;
    private ImageView sideBar;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();


    public MonthMoodFragment() {
    }

    public static MonthMoodFragment newInstance() {
        return new MonthMoodFragment();
    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_mood_month, container, false);
            mCalendarView = mRootView.findViewById(R.id.calendarViewMonth);
            initData();
        }
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

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


    protected void initView () {
        mCalendarView = mRootView.findViewById(R.id.calendarViewMonth);
    }

    protected void initData () {

        int year = mCalendarView.getCurYear();
        int month = mCalendarView.getCurMonth();

        fetchMoodData(year, month, moodData -> {
            Map<String, Calendar> schemeMap = new HashMap<>();
            moodData.forEach((dateString, moodValue) -> {
                String[] parts = dateString.split("-");
                int dYear = Integer.parseInt(parts[0]);
                int dMonth = Integer.parseInt(parts[1]);
                int dDay = Integer.parseInt(parts[2]);

                int color = getColorForMood(moodValue);
                Calendar calendar = getSchemeCalendar(dYear, dMonth, dDay, color, "");
                schemeMap.put(calendar.toString(), calendar);
            });
            mCalendarView.setSchemeDate(schemeMap);
        });
    }

    private Calendar getSchemeCalendar ( int year, int month, int day, int color, String text){
        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setSchemeColor(color);
        calendar.setScheme(text);
        return calendar;
    }
    private void fetchMoodData ( int year, int month, FirestoreCallback callback){
        if (currentUser == null) return;

        String userId = currentUser.getUid();
        java.util.Calendar startOfMonth = java.util.Calendar.getInstance();
        startOfMonth.set(year, month - 1, 1);
        int daysInMonth = startOfMonth.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Map<String, Integer> moodData = new HashMap<>();
        CountDownLatch latch = new CountDownLatch(daysInMonth);

        for (int day = 1; day <= daysInMonth; day++) {
            String formattedDate = dateFormat.format(startOfMonth.getTime());

            DocumentReference dailyRecordRef = firebaseFirestore
                    .collection("users")
                    .document(userId)
                    .collection("dailyRecords")
                    .document(formattedDate);

            dailyRecordRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        Long moodLong = document.getLong("mood");
                        if (moodLong != null) {
                            moodData.put(formattedDate, moodLong.intValue());
                        }
                    }
                }
                latch.countDown();
            });

            startOfMonth.add(java.util.Calendar.DAY_OF_MONTH, 1);
        }

        new Thread(() -> {
            try {
                latch.await();
                callback.onCallback(moodData);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private int getColorForMood(int moodScore) {
        if (moodScore >= 0 && moodScore <= 4) return 0xFF2196F3; // Blue
        else if (moodScore > 4 && moodScore <= 7) return 0xFF4CAF50; // Green
        else return 0xFFFFEB3B; // Yellow
    }
    interface FirestoreCallback {
        void onCallback(Map<String, Integer> moodData);
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
