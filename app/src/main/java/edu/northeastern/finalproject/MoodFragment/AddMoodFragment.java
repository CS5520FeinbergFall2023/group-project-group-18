package edu.northeastern.finalproject.MoodFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import android.os.Bundle;

import androidx.core.net.ParseException;
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
import edu.northeastern.finalproject.FirebaseUtil;
import edu.northeastern.finalproject.R;
import edu.northeastern.finalproject.data.UserDailyRecord;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class AddMoodFragment extends Fragment {

    private SeekBar moodSeekBar;
    private ImageView moodImageGood, moodImageAverage, moodImageBad;
    private ImageView sideBar;
    private ImageView icStatusSignal;
    private TextView moodValueText;
    private Button enterDailyQuoteBtn;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private FirebaseUser currentUser = mAuth.getCurrentUser();



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
        enterDailyQuoteBtn = view.findViewById(R.id.enterDailyQuoteBtn);

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

        String userId = FirebaseUtil.getAuth().getCurrentUser().getUid();

        String currentDate = getCurrentDate();
        DocumentReference dailyRecordRef = FirebaseUtil.getFirestore()
                .collection("users")
                .document(userId)
                .collection("dailyRecords")
                .document(currentDate);

        getTodayMood(dailyRecordRef);

        enterDailyQuoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Enter Today's Quote");
                final EditText input = new EditText(getContext());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String quote = input.getText().toString().trim();
                        if (quote.length() <= 25) {
                            saveQuote(quote);
                        } else {
                            Toast.makeText(getContext(), "Please limit the quote to 25 characters.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog dialog = builder.create();

                // Show the keyboard when the dialog is displayed
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

                dialog.show();

                // Request focus on the EditText when the dialog shows up
                input.requestFocus();
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
//    private void checkLoginStatus() {
//        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        if (currentUser == null) {
//            // User is not logged in, start LoginActivity
//            Context context = getContext();
//            if (context != null) {
//                Intent intent = new Intent(context, LoginActivity.class);
//                context.startActivity(intent);
//                if (getActivity() != null) {
//                    getActivity().finish(); // Close the current Fragment's hosting Activity
//                }
//            }
//        }
//    }

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

//    private void saveMoodValue(int moodValue) {
//
//        Calendar calendar = Calendar.getInstance();
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
//        String currentDate = dateFormat.format(calendar.getTime());
//        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
//        String dayOfWeekStr = new DateFormatSymbols().getWeekdays()[dayOfWeek];
//
//        SharedPreferences preferences = this.getActivity().getSharedPreferences("MoodPreferences", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = preferences.edit();
//
//
//        editor.putInt("MoodValue", moodValue);
//        editor.putString("Date", currentDate);
//        editor.putString("DayOfWeek", dayOfWeekStr);
//        editor.apply();
//
//
//        showSavedMoodData(currentDate, dayOfWeekStr, moodValue);
//    }
    private void saveMoodValue(int moodValue) {
        firebaseFirestore = FirebaseFirestore.getInstance();
        String currentDate = getCurrentDate();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            DocumentReference dailyRecordRef = firebaseFirestore
                    .collection("users")
                    .document(userId)
                    .collection("dailyRecords")
                    .document(getCurrentDate());

            // Check if a record already exists
            dailyRecordRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        // Record exists, update only the mood
                        dailyRecordRef.update("mood", moodValue)
                                .addOnSuccessListener(aVoid -> {
                                    try {
                                        showSavedMoodData(currentDate, moodValue);
                                    } catch (java.text.ParseException e) {
                                        e.printStackTrace();
                                    }
                                })
                                .addOnFailureListener(e -> Log.w("Firestore", "Error when updating mood data", e));
                    } else {
                        // No record for this day, create a new one
                        UserDailyRecord record = new UserDailyRecord();
                        record.setUserId(userId);
                        record.setDate(new Date()); // Set the current date
                        record.setMood(moodValue);

                        dailyRecordRef.set(record)
                                .addOnSuccessListener(aVoid -> {
                                    try {
                                        showSavedMoodData(currentDate, moodValue);
                                    } catch (java.text.ParseException e) {
                                        e.printStackTrace();
                                    }
                                })
                                .addOnFailureListener(e -> Log.w("Firestore", "Error when storing new mood data", e));
                    }
                } else {
                    Log.e("Firestore", "Error getting document", task.getException());
                }
            });
        }
    }

    private void showSavedMoodData(String date, int moodValue) throws java.text.ParseException {
        String dayOfWeekStr = getDayOfWeekStr(date);
        String message = String.format(Locale.getDefault(), "%s, %s, %d", date, dayOfWeekStr, moodValue);
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(new Date());
    }

    private void getTodayMood(DocumentReference dailyRecordRef){
        dailyRecordRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Check if the mood field is not null
                    if (document.contains("mood") && document.get("mood") != null) {
                        int mood = document.getLong("mood").intValue(); // Cast to int if you're sure it's an integer
                        // Now you can use this mood value to update your UI
                        moodSeekBar.setProgress(mood);
                        moodValueText.setText(String.valueOf(mood));
                    } else {
                        // Handle the case where mood is null or not present
                        moodSeekBar.setProgress(0); // Default value
                        moodValueText.setText("0");
                        Toast.makeText(getContext(),"You have not track your mood today~", Toast.LENGTH_LONG).show();
                    }
                } else {
                    // Document does not exist
                    moodSeekBar.setProgress(0); // Default value
                    moodValueText.setText("0");
                }
            } else {
                // Handle the error
                Log.e("Firestore", "Error getting documents: ", task.getException());
            }
        });
    }
    private String getDayOfWeekStr(String dateStr) throws java.text.ParseException {
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return new DateFormatSymbols().getWeekdays()[calendar.get(Calendar.DAY_OF_WEEK)];
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    private void saveQuote(String quote) {
        if (currentUser != null) {
            String userId = currentUser.getUid();
            String currentDate = getCurrentDate();
            DocumentReference dailyRecordRef = firebaseFirestore
                    .collection("users")
                    .document(userId)
                    .collection("dailyRecords")
                    .document(currentDate);

            // Check if a record already exists
            dailyRecordRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        // Record exists, update only the mood
                        dailyRecordRef.update("dailyQuote", quote)
                                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Quote updated successfully!", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error updating quote.", Toast.LENGTH_SHORT).show());

                    } else {
                        // No record for this day, create a new one
                        UserDailyRecord record = new UserDailyRecord();
                        record.setUserId(userId);
                        record.setDate(new Date()); // Set the current date
                        record.setDailyQuote(quote);

                        dailyRecordRef.set(record)
                                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Quote saved successfully!", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error saving quote.", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    Log.e("Firestore", "Error getting document", task.getException());
                }
            });
        }else{
            Toast.makeText(getContext(), "Please log in", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        if(getActivity() != null) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        if(getActivity() != null) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
    }

}