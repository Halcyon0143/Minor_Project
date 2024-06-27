package com.example.minor_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.minor_project.databinding.ActivityLogin2Binding;
import com.example.minor_project.databinding.ActivityUserBinding;

public class login2 extends AppCompatActivity {

    private ActivityLogin2Binding binding;

    private String userName = "<Admin>";
    private String password = "<password>";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLogin2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(binding.usernameEditText.getText().toString().equals(userName) && binding.passwordEditText.getText().toString().equals(password)) {
                        Intent intent = new Intent(login2.this, Admin_main.class);
                        startActivity(intent);
                        Toast.makeText(login2.this, "Logged-in Successfully", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        showToast();
                    }
                }
            });



    }

    private void showToast() {
            Toast.makeText(this, "Wrong Username or Password", Toast.LENGTH_SHORT).show();

    }
}
