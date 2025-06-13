package com.example.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class CategoryActivity extends AppCompatActivity {

    RelativeLayout notesLayout, quizLayout, tutorialLayout;
    TextView notesText, quizText, tutorialText;
    String selectedLanguage;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        db = FirebaseFirestore.getInstance();

        selectedLanguage = getIntent().getStringExtra("selectLanguage");

        notesLayout = findViewById(R.id.notesLayout);
        quizLayout = findViewById(R.id.quizLayout);
        tutorialLayout = findViewById(R.id.tutorialLayout);

        notesText = findViewById(R.id.notesText);
        quizText = findViewById(R.id.quizText);
        tutorialText = findViewById(R.id.tutorialText);

        if (selectedLanguage != null && !selectedLanguage.isEmpty()) {
            notesText.setText(selectedLanguage + " Notes");
            quizText.setText(selectedLanguage + " Quiz");
            tutorialText.setText(selectedLanguage + " Tutorial");
        } else {
            Toast.makeText(this, "No language selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Notes
        notesLayout.setOnClickListener(v -> {
            Intent intent = new Intent(CategoryActivity.this, TopicActivity.class);
            intent.putExtra("languageName", selectedLanguage);
            startActivity(intent);
        });

        // Quiz
        quizLayout.setOnClickListener(v -> {
            String languageId = getLanguageIdFromName(selectedLanguage);
            if (languageId != null) {
                Intent intent = new Intent(CategoryActivity.this, QuizActivity.class);
                intent.putExtra("languageId", languageId);
                intent.putExtra("languageName", selectedLanguage);
                startActivity(intent);
            } else {
                Toast.makeText(CategoryActivity.this, "Quiz not available for this language", Toast.LENGTH_SHORT).show();
            }
        });

        // Tutorial
        tutorialLayout.setOnClickListener(v -> {
            String languageId = getLanguageIdFromName(selectedLanguage);
            if (languageId == null) {
                Toast.makeText(CategoryActivity.this, "Tutorial not available", Toast.LENGTH_SHORT).show();
                return;
            }

            // ✅ Fetch from Firestore: languages/{languageId}/Tutorial/1
            db.collection("languages")
                    .document(languageId)
                    .collection("Tutorial")
                    .document(languageId)  // now uses "1", "2", "3", etc.
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String tutorialUrl = documentSnapshot.getString("Tutorial");
                            if (tutorialUrl != null && !tutorialUrl.isEmpty()) {

                                // ✅ Convert youtu.be to embeddable URL
                                if (tutorialUrl.contains("youtu.be/")) {
                                    String videoId = tutorialUrl.substring(tutorialUrl.lastIndexOf("/") + 1);
                                    tutorialUrl = "https://www.youtube.com/embed/" + videoId;
                                }

                                Intent intent = new Intent(CategoryActivity.this, TutorialActivity.class);
                                intent.putExtra("tutorialUrl", tutorialUrl);
                                startActivity(intent);
                            } else {
                                Toast.makeText(CategoryActivity.this, "Tutorial URL is missing", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(CategoryActivity.this, "Tutorial not found", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(CategoryActivity.this, "Failed to load tutorial", Toast.LENGTH_SHORT).show();
                    });
        });
    }

    private String getLanguageIdFromName(String languageName) {
        switch (languageName.toLowerCase()) {
            case "java": return "1";
            case "php": return "2";
            case "c++": return "3";
            case "python": return "4";
            case "html": return "5";
            case "javascript": return "6";
            case "react": return "7";
            case "ruby": return "8";
            default: return null;
        }
    }
}
