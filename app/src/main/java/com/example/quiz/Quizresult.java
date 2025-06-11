package com.example.quiz;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Quizresult extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quizresult);

        final AppCompatButton startNewBtn = findViewById(R.id.startNewQuizBtn);
        final TextView correctAnswers = findViewById(R.id.correctAnswers);
        final TextView incorrectAnswers = findViewById(R.id.incorrectAnswers);

        // Retrieve values from the intent
        final int getCorrectAnswers = getIntent().getIntExtra("correct", 0);
        final int getIncorrectAnswers = getIntent().getIntExtra("incorrect", 0);



        // Set the retrieved values to the TextViews
        correctAnswers.setText("Correct Answer: " + getCorrectAnswers);
        incorrectAnswers.setText("Wrong Answer: " + getIncorrectAnswers);

        // Set onClickListener for the button
        startNewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Quizresult.this, MainActivity.class));
                finish();
            }
        });

        // Handle back press using OnBackPressedDispatcher
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                startActivity(new Intent(Quizresult.this, MainActivity.class));
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }
}
