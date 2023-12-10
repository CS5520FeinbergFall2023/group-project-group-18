package edu.northeastern.finalproject.RecordFragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import edu.northeastern.finalproject.R;
import edu.northeastern.finalproject.data.UserDailyRecord;

public class HeartRate extends Fragment {
    private TextView rate;
    private ImageView stepCounter;
    private Button confirmButton;
    private RecyclerView historyRecyclerView;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();

    public HeartRate() {

    }

    public static HeartRate newInstance() {
        return new HeartRate();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_heart_rate, container, false);

        rate = view.findViewById(R.id.rate);
        stepCounter = view.findViewById(R.id.stepCounterImage);
        confirmButton = view.findViewById(R.id.confirmButton);
        historyRecyclerView = view.findViewById(R.id.historyRecyclerView);

        // Firestore Initialization
        firebaseFirestore = FirebaseFirestore.getInstance();

        stepCounter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container, new StepCounter());
                transaction.commit();
            }
        });

        confirmButton.setOnClickListener(v -> saveHeartRateData());
        loadHistoryData();

        return view;
    }

    private void saveHeartRateData() {
        double heartRateValue = Double.parseDouble(rate.getText().toString());
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
                        dailyRecordRef.update("heartRate", heartRateValue)
                                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Heart rate updated successfully!", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error updating heart rate.", Toast.LENGTH_SHORT).show());

                    } else {
                        // No record for this day, create a new one
                        UserDailyRecord record = new UserDailyRecord();
                        record.setUserId(userId);
                        record.setDate(new Date()); // Set the current date
                        record.setHeartRate(heartRateValue);

                        dailyRecordRef.set(record)
                                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Heart rate saved successfully!", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error saving heart rate.", Toast.LENGTH_SHORT).show());

                    }
                    loadHistoryData();
                } else {
                    Log.e("Firestore", "Error getting document", task.getException());
                }
            });
        }else{
            Toast.makeText(getContext(), "Please log in", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadHistoryData() {
        String userId = currentUser.getUid();
        firebaseFirestore.collection("users")
                .document(userId)
                .collection("dailyRecords")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<UserDailyRecord> records = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            records.add(document.toObject(UserDailyRecord.class));
                        }
                        HeartRateHistoryAdapter adapter = new HeartRateHistoryAdapter(records);
                        historyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        historyRecyclerView.setAdapter(adapter);
                    } else {
                        Log.e("Firestore", "Error getting document", task.getException());
                    }
                });
    }
    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(new Date());
    }
}