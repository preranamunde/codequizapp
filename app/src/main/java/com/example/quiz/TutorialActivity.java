package com.example.quiz;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class TutorialActivity extends AppCompatActivity {

    MaterialButton tutorialButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        tutorialButton = findViewById(R.id.tutorialButton);

        String tutorialUrl = getIntent().getStringExtra("tutorialUrl");

        if (tutorialUrl != null && !tutorialUrl.isEmpty()) {
            // Open the YouTube link when "Watch Tutorial" button is clicked
            tutorialButton.setOnClickListener(v -> {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(tutorialUrl));
                startActivity(browserIntent);
                // After opening tutorial, navigate to CategoryActivity
                Intent intent = new Intent(TutorialActivity.this, CategoryActivity.class);
                startActivity(intent);
                finish(); // Close current activity
            });
        } else {
            tutorialButton.setEnabled(false); // Disable button if no URL
        }
    }
}