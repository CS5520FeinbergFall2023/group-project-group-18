package edu.northeastern.finalproject.RecordFragment;

import static android.content.Context.SENSOR_SERVICE;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.Manifest;

import edu.northeastern.finalproject.R;

public class StepCounter extends Fragment implements SensorEventListener {
    private static final int REQUEST_ACTIVITY_RECOGNITION_PERMISSION = 1;

    private SensorManager mSensorManager = null;
    private Sensor stepSensor;
    private int totalSteps = 0;
    private int previewTotalSteps = 0;
    private TextView steps;
    private TextView warning;
    private ImageView heartRate;

    private ActivityResultLauncher<String> requestPermissionLauncher;


    public StepCounter() {
        // Required empty public constructor
    }

    public static StepCounter newInstance() {
        return new StepCounter();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                initSensor();
            } else {
                warning.setText("Permission denied. Cannot count steps.");
            }
        });
    }

    private void initSensor() {
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        stepSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (stepSensor != null) {
            mSensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            warning.setText("Step Sensor not available.");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_step_counter,container,false);
        steps = view.findViewById(R.id.steps);
        warning = view.findViewById(R.id.warning);
        heartRate = view.findViewById(R.id.heartRateImage);

        loadData();

        mSensorManager = (SensorManager)getActivity().getSystemService(SENSOR_SERVICE);
        stepSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION);
        } else {
            initSensor();
        }

        heartRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container, new HeartRate());
                transaction.commit();
            }
        });

        return view;
    }

    public void onResume(){
        super.onResume();
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED) {
            if(stepSensor != null) {
                mSensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
            }else{
                mSensorManager.registerListener(this,stepSensor,SensorManager.SENSOR_DELAY_NORMAL);
            }
        } else {
        }
    }

    public void onPause(){
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_STEP_COUNTER){
            totalSteps =(int) event.values[0];
            int currentSteps = totalSteps - previewTotalSteps;
            steps.setText(String.valueOf(currentSteps));

            if(currentSteps < 500){
                warning.setText("It's time to get up!");
            }else if(currentSteps < 2000){
                warning.setText("You did great! Adding a bit work-out would make it perfect");
            } else if (currentSteps < 8000) {
                warning.setText("You did amazing today!");
            } else {
                warning.setText("You are beyond amazing!");
            }
        }
    }

    private void saveData() {
        SharedPreferences sharedPref = getActivity().getSharedPreferences("myPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("key1",String.valueOf(previewTotalSteps));
        editor.apply();
    }

    private  void loadData(){
        SharedPreferences sharedPref = getActivity().getSharedPreferences("myPref", Context.MODE_PRIVATE);
        int savedNumber = (int) sharedPref.getFloat("key1",0f);
        previewTotalSteps = savedNumber;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}