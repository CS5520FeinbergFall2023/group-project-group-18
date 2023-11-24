package edu.northeastern.finalproject;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import edu.northeastern.finalproject.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());


        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();


            if(item.getItemId() == R.id.mood) {
                transaction.replace(R.id.fragment_container, new MoodFragment());
            } else if (item.getItemId() == R.id.record) {
                // Replace with RecordFragment
                // transaction.replace(R.id.fragment_container, new RecordFragment());
            } else if (item.getItemId() == R.id.community) {
                // Replace with CommunityFragment
                // transaction.replace(R.id.fragment_container, new CommunityFragment());
            } else if (item.getItemId() == R.id.photo){
                // Replace with PhotoFragment
                // transaction.replace(R.id.fragment_container, new PhotoFragment());
            }

            transaction.commit();
            return true;
        });


//        if (savedInstanceState == null) {
//            binding.bottomNavigationView.setSelectedItemId(R.id.mood);
//        }
    }
}
