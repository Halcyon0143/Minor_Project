package com.example.minor_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.minor_project.databinding.ActivityMainBinding;
import com.example.minor_project.databinding.ActivityPdfviewBinding;

public class MainActivity extends AppCompatActivity implements BottomNavHandler {

    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        BottomNavUtils.setupBottomNavigation(this);
        binding.imageViewSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSearch();
            }
        });

        String[] availabilityOptions1 = {"I", "II","III","IV","V","VI","VII","VIII"};

        ArrayAdapter<String> availabilityAdapter1 = new ArrayAdapter<>(
                this,
                R.layout.custom_spinner_dropdown_item,
                availabilityOptions1
        );

        binding.semesterSpinner.setAdapter(availabilityAdapter1);

        String[] availabilityOptions2 = {"t1", "t2","t3"};

        ArrayAdapter<String> availabilityAdapter2 = new ArrayAdapter<>(
                this,
                R.layout.custom_spinner_dropdown_item,
                availabilityOptions2
        );

        binding.termSpinner.setAdapter(availabilityAdapter2);

        String[] availabilityOptions3 = {"CSE", "IT","ECE","BIO-TECH"};

        ArrayAdapter<String> availabilityAdapter3 = new ArrayAdapter<>(
                this,
                R.layout.custom_spinner_dropdown_item,
                availabilityOptions3
        );

        binding.branchSpinner.setAdapter(availabilityAdapter3);

        binding.goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get selected values from spinners
                String selectedSemester = binding.semesterSpinner.getSelectedItem().toString();
                String selectedTerm = binding.termSpinner.getSelectedItem().toString();

                // Create an intent to start the Subject activity
                Intent intent = new Intent(MainActivity.this, Subject.class);

                // Pass the selected values to the Subject activity
                intent.putExtra("selectedSemester", selectedSemester);
                intent.putExtra("selectedTerm", selectedTerm);

                // Start the Subject activity
                startActivity(intent);
            }
        });





    }

    private void performSearch() {
        String searchText = binding.editTextSearch.getText().toString().trim();

        if (!searchText.isEmpty()) {
            // Create an intent to start PdfviewActivity
            Intent intent = new Intent(MainActivity.this, PDFVIEW.class);

            // Pass the search query to PdfviewActivity
            intent.putExtra("searchQuery", searchText);

            // Start PdfviewActivity
            startActivity(intent);
        } else {
            // Handle empty search query, show a message, etc.
            Toast.makeText(MainActivity.this, "Enter a search query", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void setupBottomNavigation() {

    }
}