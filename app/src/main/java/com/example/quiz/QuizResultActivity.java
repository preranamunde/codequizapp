package com.example.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class QuizResultActivity extends AppCompatActivity {

    TextView scoreText, correctAnswersText, messageText;
    AppCompatButton retakeBtn, homeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result);

        scoreText = findViewById(R.id.scoreText);
        correctAnswersText = findViewById(R.id.correctAnswersText);
        messageText = findViewById(R.id.messageText);
        retakeBtn = findViewById(R.id.retakeBtn);
        homeBtn = findViewById(R.id.homeBtn);

        // Get results from intent
        int correctAnswers = getIntent().getIntExtra("correctAnswers", 0);
        int totalQuestions = getIntent().getIntExtra("totalQuestions", 0);
        double scorePercentage = getIntent().getDoubleExtra("scorePercentage", 0.0);

        // Display results
        scoreText.setText(String.format("%.1f%%", scorePercentage));
        correctAnswersText.setText(correctAnswers + "/" + totalQuestions + " Correct");

        // Set message based on score
        if (scorePercentage >= 80) {
            messageText.setText("Excellent! Great job!");
        } else if (scorePercentage >= 60) {
            messageText.setText("Good work! Keep it up!");
        } else {
            messageText.setText("Better luck next time!");
        }

        retakeBtn.setOnClickListener(v -> {
            finish(); // Go back to previous activity
        });

        homeBtn.setOnClickListener(v -> {
            Intent homeIntent = new Intent(this, MainActivity.class);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(homeIntent);
            finish();
        });
    }
}