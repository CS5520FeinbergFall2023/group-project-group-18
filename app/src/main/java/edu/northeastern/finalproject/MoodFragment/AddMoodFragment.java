package edu.northeastern.finalproject.MoodFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.DateFormatSymbols;

import edu.northeastern.finalproject.Auth.LoginActivity;
import edu.northeastern.finalproject.Auth.RegisterActivity;
import edu.northeastern.finalproject.R;


public class AddMoodFragment extends Fragment {

    private SeekBar moodSeekBar;
    private ImageView moodImageGood, moodImageAverage, moodImageBad;
    private ImageView sideBar;
    private ImageView icStatusSignal;
    private TextView moodValueText;

    public AddMoodFragment() {
        // Required empty public constructor
    }

    public static AddMoodFragment newInstance() {
        return new AddMoodFragment();
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
        icStatusSignal = view.findViewById(R.id.ic_status_signal);

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

        sideBar = view.findViewById(R.id.ic_calender);
        sideBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        icStatusSignal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });


        return view;
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
    private void checkLoginStatus() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // User is not logged in, start LoginActivity
            Context context = getContext();
            if (context != null) {
                Intent intent = new Intent(context, LoginActivity.class);
                context.startActivity(intent);
                if (getActivity() != null) {
                    getActivity().finish(); // Close the current Fragment's hosting Activity
                }
            }
        }
    }

    private void showDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_sidebar);

        LinearLayout weekLayout = dialog.findViewById(R.id.layoutWeek);
        LinearLayout monthLayout = dialog.findViewById(R.id.layoutMonth);

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