package edu.northeastern.finalproject;

import android.content.Intent;

import android.content.pm.PackageManager;

import android.content.pm.ActivityInfo;

import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.google.firebase.auth.FirebaseAuth;


import edu.northeastern.finalproject.Auth.LoginActivity;
import edu.northeastern.finalproject.RecordFragment.StepCounter;
import edu.northeastern.finalproject.communityFragment.CommunityFragment;
import edu.northeastern.finalproject.MoodFragment.AddMoodFragment;
import edu.northeastern.finalproject.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    final int PERMISSION_REQUEST_CODE =112;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkUserLogin();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);

        setContentView(binding.getRoot());


        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();


            if(item.getItemId() == R.id.mood) {
                transaction.replace(R.id.fragment_container, new AddMoodFragment());
            } else if (item.getItemId() == R.id.record) {
                transaction.replace(R.id.fragment_container, new StepCounter());
            } else if (item.getItemId() == R.id.community) {
                transaction.replace(R.id.fragment_container, new CommunityFragment());
            } else if (item.getItemId() == R.id.photo){
                transaction.replace(R.id.fragment_container, new PhotoFragment());
            }

            transaction.commit();
            return true;
        });

        if (Build.VERSION.SDK_INT > 32) {
            if (!shouldShowRequestPermissionRationale("112")){
                getNotificationPermission();
            }
        }
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // 处理横向方向
            setContentView(R.layout.test);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // 处理纵向方向
            setContentView(R.layout.activity_main);
        }

        transaction.commit();
    }

    public void getNotificationPermission(){
        try {
            if (Build.VERSION.SDK_INT > 32) {
                String permission = "android.permission.POST_NOTIFICATIONS";

                int permCode = 42; // some integer request code

                requestPermissions(new String[]{permission}, permCode);
                ActivityCompat.requestPermissions(this,
                        new String[]{permission},
                        PERMISSION_REQUEST_CODE);
            }
        }catch (Exception e){

        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // allow

                }  else {
                    //deny
                }
                return;

        }

    }

}
