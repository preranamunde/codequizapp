package com.example.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class Quizresult extends AppCompatActivity {

    TextView resultTitle, scoreText, percentageText, messageText, correctAnswersText, incorrectAnswersText;
    AppCompatButton homeBtn, retakeBtn;

    int correctAnswers, totalQuestions, incorrectAnswers;
    double scorePercentage;
    String languageName, languageId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result);

        Log.d("QuizResultDebug", "QuizResultActivity onCreate() called");

        initializeViews();
        getIntentData();
        displayResults();
        setupClickListeners();
    }

    private void initializeViews() {
        resultTitle = findViewById(R.id.resultTitle);
        scoreText = findViewById(R.id.scoreText);
        percentageText = findViewById(R.id.percentageText);
        messageText = findViewById(R.id.messageText);
        correctAnswersText = findViewById(R.id.correctAnswersText);
        incorrectAnswersText = findViewById(R.id.incorrectAnswersText);
        homeBtn = findViewById(R.id.homeBtn);
        retakeBtn = findViewById(R.id.retakeBtn);
    }

    private void getIntentData() {
        Intent intent = getIntent();
        correctAnswers = intent.getIntExtra("correctAnswers", 0);
        totalQuestions = intent.getIntExtra("totalQuestions", 0);
        scorePercentage = intent.getDoubleExtra("scorePercentage", 0.0);
        languageName = intent.getStringExtra("languageName");
        languageId = intent.getStringExtra("languageId");
        incorrectAnswers = totalQuestions - correctAnswers;

        Log.d("QuizResultDebug", "Intent data - Correct: " + correctAnswers + ", Total: " + totalQuestions +
                ", Percentage: " + scorePercentage + ", Language: " + languageName + ", LanguageId: " + languageId);
    }

    private void displayResults() {
        if (languageName != null) {
            resultTitle.setText(languageName + " Quiz Result");
        } else {
            resultTitle.setText("Quiz Result");
        }

        scoreText.setText(correctAnswers + "/" + totalQuestions);
        percentageText.setText(String.format("%.1f%%", scorePercentage));
        correctAnswersText.setText("Correct: " + correctAnswers);
        incorrectAnswersText.setText("Incorrect: " + incorrectAnswers);

        // Set message and colors based on performance
        if (scorePercentage >= 80) {
            messageText.setText("🎉 Excellent! Outstanding Performance!");
            messageText.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            percentageText.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else if (scorePercentage >= 60) {
            messageText.setText("👍 Good Job! Keep it up!");
            messageText.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
            percentageText.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
        } else if (scorePercentage >= 40) {
            messageText.setText("📚 Keep Learning! You can do better!");
            messageText.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
            percentageText.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
        } else {
            messageText.setText("💪 Don't Give Up! Practice More!");
            messageText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            percentageText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }
    }

    private void setupClickListeners() {
        homeBtn.setOnClickListener(v -> {
            Log.d("QuizResultDebug", "Home button clicked");
            // Go back to main activity or home screen
            Intent intent = new Intent(Quizresult.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        retakeBtn.setOnClickListener(v -> {
            Log.d("QuizResultDebug", "Retake button clicked. LanguageId: " + languageId + ", LanguageName: " + languageName);

            if (languageId != null && !languageId.isEmpty()) {
                // Restart the quiz with same language
                Intent intent = new Intent(Quizresult.this, QuizActivity.class);
                intent.putExtra("languageId", languageId);
                intent.putExtra("languageName", languageName);
                startActivity(intent);
                finish();
            } else {
                Log.e("QuizResultDebug", "LanguageId is null or empty, cannot retake quiz");
                // Fallback to home if languageId is missing
                Intent intent = new Intent(Quizresult.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        // Back button click listener
        findViewById(R.id.back_icon).setOnClickListener(v -> {
            Log.d("QuizResultDebug", "Back button clicked");
            Intent intent = new Intent(Quizresult.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d("QuizResultDebug", "Hardware back button pressed");
        Intent intent = new Intent(Quizresult.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}