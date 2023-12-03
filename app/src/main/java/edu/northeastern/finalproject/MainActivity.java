package edu.northeastern.finalproject;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.google.firebase.auth.FirebaseAuth;

import edu.northeastern.finalproject.Auth.LoginActivity;
import edu.northeastern.finalproject.communityFragment.CommunityFragment;
import edu.northeastern.finalproject.databinding.ActivityMainBinding;
import edu.northeastern.finalproject.MoodFragment.AddMoodFragment;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkUserLogin();

        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());


        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();


            if(item.getItemId() == R.id.mood) {
                transaction.replace(R.id.fragment_container, new AddMoodFragment());
            } else if (item.getItemId() == R.id.record) {
                transaction.replace(R.id.fragment_container, new RecordFragment());
            } else if (item.getItemId() == R.id.community) {
                transaction.replace(R.id.fragment_container, new CommunityFragment());
            } else if (item.getItemId() == R.id.photo){
                transaction.replace(R.id.fragment_container, new PhotoFragment());
            }

            transaction.commit();
            return true;
        });


//        if (savedInstanceState == null) {
//            binding.bottomNavigationView.setSelectedItemId(R.id.mood);
//        }
    }
    private void checkUserLogin() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            // User is logged in, show AddMoodFragment
            showAddMoodFragment();
        } else {
            // User is not logged in, show LoginActivity
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish(); // Close MainActivity
        }
    }

    private void showAddMoodFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, new AddMoodFragment());
        transaction.commit();
    }
}
