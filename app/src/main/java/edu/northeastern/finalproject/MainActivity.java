package edu.northeastern.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import edu.northeastern.finalproject.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if(item.getItemId() == R.id.mood){
                return true;
            } else if (item.getItemId() == R.id.record) {
                return true;
            } else if (item.getItemId() == R.id.community) {
                return true;
            } else if (item.getItemId() == R.id.photo){
                return true;
            }
            return true;
        });
    }
}