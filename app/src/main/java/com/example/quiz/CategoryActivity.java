package com.example.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CategoryActivity extends AppCompatActivity {

    RelativeLayout notesLayout, quizLayout, tutorialLayout;
    TextView notesText, quizText, tutorialText;
    String selectedLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        selectedLanguage = getIntent().getStringExtra("selectLanguage"); // ✅ changed key

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

        notesLayout.setOnClickListener(v -> {
            Intent intent = new Intent(CategoryActivity.this, TopicActivity.class);
            intent.putExtra("languageName", selectedLanguage); // ✅ send to TopicActivity
            startActivity(intent);
        });
        quizLayout.setOnClickListener(v -> {
            Intent intent = new Intent(CategoryActivity.this, QuizActivity.class);

            intent.putExtra("languageId", "1");
            startActivity(intent);
        });

        tutorialLayout.setOnClickListener(v ->
                Toast.makeText(CategoryActivity.this, "Open " + selectedLanguage + " Tutorial", Toast.LENGTH_SHORT).show()
        );
    }
}
