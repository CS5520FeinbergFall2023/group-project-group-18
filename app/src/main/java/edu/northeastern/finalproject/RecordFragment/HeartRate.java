package edu.northeastern.finalproject.RecordFragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import edu.northeastern.finalproject.R;
public class HeartRate extends Fragment {
    private TextView rate;
    private ImageView stepCounter;

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
        View view = inflater.inflate(R.layout.fragment_heart_rate,container,false);

        rate = view.findViewById(R.id.rate);
        stepCounter = view.findViewById(R.id.stepCounterImage);

        stepCounter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container, new StepCounter());
                transaction.commit();
            }
        });
        return view;
    }
}