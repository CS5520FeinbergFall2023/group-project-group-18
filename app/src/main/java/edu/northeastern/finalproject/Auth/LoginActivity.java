package edu.northeastern.finalproject.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.NotNull;

import edu.northeastern.finalproject.MainActivity;
import edu.northeastern.finalproject.MoodFragment.AddMoodFragment;
import edu.northeastern.finalproject.R;

public class LoginActivity extends AppCompatActivity {
    private EditText editTextEmail, editTextPassword;
    private FirebaseAuth mAuth;
    private TextView registerGuide;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.editTextEmail_Login);
        editTextPassword = findViewById(R.id.editTextPassword_Login);
        registerGuide = findViewById(R.id.registerGuide_Login);
        Button buttonLogin = findViewById(R.id.buttonLogin_Login);

        buttonLogin.setOnClickListener(v -> loginUser());
        makeRegisterTextClickable(registerGuide);
    }

    private void makeRegisterTextClickable(TextView textView){
        String fullText = "Haven't registered? Register here.";
        SpannableString ss = new SpannableString(fullText);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(@NotNull TextPaint ds){
                super.updateDrawState(ds);
                ds.setColor(ds.linkColor);
            }
        };

        int start = fullText.indexOf("here");
        int end = start + "here".length();
        ss.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView.setText(ss);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void loginUser(){

        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            }else{
                if (task.getException() instanceof FirebaseAuthInvalidUserException){
                    Toast.makeText(LoginActivity.this,  "Email address does not exist.", Toast.LENGTH_SHORT).show();
                }else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                    Toast.makeText(LoginActivity.this, "Password is incorrect.", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(LoginActivity.this, "Login error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
