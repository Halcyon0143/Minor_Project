package com.example.minor_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.minor_project.databinding.ActivityPdfviewBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PDFVIEW extends AppCompatActivity implements MainAdapter.OnItemClickListener {

    private ActivityPdfviewBinding binding;
    private DatabaseReference databaseReference;
    private List<pdfClass> uploads;
    private MainAdapter adapter;
    private String searchQuery;
    boolean searchavailable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityPdfviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        NetworkUtils.checkInternetAndDisplayToast(this);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        int selectedIconColor = ContextCompat.getColor(this, R.color.purple_200);

// Create a ColorStateList from the XML resource
        ColorStateList colorStateList = ContextCompat.getColorStateList(this, R.color.bottom_nav_icon_color);

// Set the ColorStateList to the BottomNavigationView
        bottomNavigationView.setItemIconTintList(colorStateList);
        bottomNavigationView.setItemTextColor(colorStateList);
        BottomNavUtils.setupBottomNavigation(this);

        binding.imageViewSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = binding.editTextSearch.getText().toString().trim();
                viewSpecificFilesBySearchQuery(searchText);
                binding.editTextSearch.setText( searchText);

            }
        });

        binding.imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PDFVIEW.this,bookMarkView.class);
                startActivity(intent);
            }
        });

        Intent intent2 = getIntent();
        if (intent2 != null && intent2.hasExtra("searchQuery")) {
            searchQuery = intent2.getStringExtra("searchQuery");
            viewSpecificFilesBySearchQuery(searchQuery);
            binding.editTextSearch.setText( searchQuery);
            searchavailable =true;
        }

        if(!searchavailable) {
            Intent intent = getIntent();
            if (intent != null) {
                String selectedSubject = intent.getStringExtra("selectedSubject");
                String selectedTerm = intent.getStringExtra("selectedTerm");
            viewSpecificFiles(selectedSubject,selectedTerm);
//                Toast.makeText(this, "" + selectedSubject, Toast.LENGTH_SHORT).show();
                // Now you have the selected subject and term values.
                // You can use them as needed in your activity.
            }
        }

        FirebaseApp.initializeApp(this);


        uploads = new ArrayList<>();
        adapter = new MainAdapter(this, uploads, this);

        RecyclerView recyclerView = binding.rv;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

//        viewSampleFiles();

        //viewAllFiles();


        binding.backButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });




    }

    private void viewAllFiles() {
        Toast.makeText(this, "Getting your files please wait 😊", Toast.LENGTH_SHORT).show();
        databaseReference = FirebaseDatabase.getInstance().getReference("Uploads");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                uploads.clear(); // Clear existing data before adding new data
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    pdfClass pdfClass = postSnapshot.getValue(pdfClass.class);
                    uploads.add(pdfClass);
                }


                // Notify the adapter that the data has changed
                adapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled
            }
        });
    }

    private void viewSpecificFiles(String subject, String term) {
        Toast.makeText(this, "Getting your files please wait 😊", Toast.LENGTH_SHORT).show();

        databaseReference = FirebaseDatabase.getInstance().getReference("Uploads");
        databaseReference.orderByChild("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                uploads.clear(); // Clear existing data before adding new data

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    pdfClass pdfClass = postSnapshot.getValue(pdfClass.class);
                    if (pdfClass != null && pdfClass.getName().startsWith(subject + "_" + term)) {

                        boolean isBookmarked = loadBookmarkStatus(pdfClass);

                        // Set the loaded bookmark status to the pdfClass object
                        pdfClass.setBookmarked(isBookmarked);
                        uploads.add(pdfClass);
                    }
                }

                // Notify the adapter that the data has changed
                adapter.notifyDataSetChanged();
                if (uploads.isEmpty()) {
                    Toast.makeText(PDFVIEW.this, "No results found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled
            }
        });
    }

    private void viewSpecificFilesBySearchQuery(String searchQuery) {
        Toast.makeText(this, "Getting your files please wait 😊", Toast.LENGTH_SHORT).show();

        // Convert the search query to lowercase (or uppercase)
        final String queryLowerCase = searchQuery.toLowerCase();

        databaseReference = FirebaseDatabase.getInstance().getReference("Uploads");
        databaseReference.orderByChild("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                uploads.clear(); // Clear existing data before adding new data

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    pdfClass pdfClass = postSnapshot.getValue(pdfClass.class);
                    if (pdfClass != null && pdfClass.getName().toLowerCase().contains(queryLowerCase)) {

                        boolean isBookmarked = loadBookmarkStatus(pdfClass);

                        // Set the loaded bookmark status to the pdfClass object
                        pdfClass.setBookmarked(isBookmarked);
                        uploads.add(pdfClass);
                    }
                }

                // Notify the adapter that the data has changed
                adapter.notifyDataSetChanged();

                // Check if no results were found
                if (uploads.isEmpty()) {
                    Toast.makeText(PDFVIEW.this, "No results found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled
            }
        });
    }



    private boolean loadBookmarkStatus(pdfClass pdfFile) {
        SharedPreferences preferences = getSharedPreferences("BookmarkPrefs", Context.MODE_PRIVATE);
        // Load bookmark status from SharedPreferences
        return preferences.getBoolean(pdfFile.getName(), false);
    }


    private void viewSampleFiles() {
        // Sample data for testing
//        uploads.clear();
        uploads.add(new pdfClass());
        uploads.add(new pdfClass());

        // Notify the adapter that the data has changed
        adapter.notifyDataSetChanged();
    }


    // Implement the onItemClick method from OnItemClickListener interface
    @Override
    public void onItemClick(pdfClass pdfFile) {
        // Handle item click, open the PDF with an external PDF viewer
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(pdfFile.getUrl()), "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        try {
            startActivity(intent);
        } catch (Exception e) {
            // Handle the exception, e.g., show a message to install a PDF viewer
            Toast.makeText(this, "No PDF viewer installed", Toast.LENGTH_SHORT).show();
        }
    }
}
