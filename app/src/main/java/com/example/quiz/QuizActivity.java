package com.example.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class QuizActivity extends AppCompatActivity {

    TextView questionText, questionCountText, topicName, timer;
    AppCompatButton option1, option2, option3, option4, nextBtn;
    AppCompatButton selectedButton = null;

    FirebaseFirestore db;
    List<QuestionModel> questionList = new ArrayList<>();
    int currentQuestionIndex = 0;
    int correctAnswers = 0;
    String selectedAnswer = "";

    // Store these for passing to result activity
    String languageId;
    String languageName;

    // Timer variables
    CountDownTimer countDownTimer;
    private static final long TOTAL_TIME = 60000; // 60 seconds per question
    private static final long COUNT_DOWN_INTERVAL = 1000; // 1 second
    boolean isTimerRunning = false;
    boolean isAnswerChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        initializeViews();

        db = FirebaseFirestore.getInstance();
        languageId = getIntent().getStringExtra("languageId");
        languageName = getIntent().getStringExtra("languageName");

        // Add debugging
        Log.d("QuizDebug", "Language ID: " + languageId);
        Log.d("QuizDebug", "Language Name: " + languageName);

        if (languageId == null || languageId.isEmpty()) {
            Log.e("QuizDebug", "Language ID is null or empty!");
            Toast.makeText(this, "Invalid language selection", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (languageName != null) {
            topicName.setText(languageName + " Quiz");
        } else {
            topicName.setText("Quiz");
        }

        loadQuizQuestions(languageId);
        setupClickListeners();
    }

    private void initializeViews() {
        topicName = findViewById(R.id.topicName);
        questionText = findViewById(R.id.questionText);
        questionCountText = findViewById(R.id.questionCountText);
        timer = findViewById(R.id.timer);
        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        option3 = findViewById(R.id.option3);
        option4 = findViewById(R.id.option4);
        nextBtn = findViewById(R.id.nextBtn);
    }

    private void setupClickListeners() {
        // Back button click listener
        findViewById(R.id.back_icon).setOnClickListener(v -> {
            stopTimer();
            finish();
        });

        option1.setOnClickListener(v -> selectOption(option1));
        option2.setOnClickListener(v -> selectOption(option2));
        option3.setOnClickListener(v -> selectOption(option3));
        option4.setOnClickListener(v -> selectOption(option4));

        nextBtn.setOnClickListener(v -> {
            Log.d("QuizDebug", "Next button clicked. isAnswerChecked: " + isAnswerChecked);

            if (!isAnswerChecked) {
                if (selectedAnswer.isEmpty()) {
                    Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show();
                    return;
                }
                checkAnswer();
            } else {
                handleNextQuestion();
            }
        });
    }

    private void loadQuizQuestions(String languageId) {
        Log.d("QuizDebug", "Loading questions for language: " + languageId);

        db.collection("languages").document(languageId)
                .collection("Quiz")
                .get()
                .addOnSuccessListener(querySnapshots -> {
                    Log.d("QuizDebug", "Total documents found: " + querySnapshots.size());

                    for (QueryDocumentSnapshot doc : querySnapshots) {
                        String question = doc.getString("question");
                        String answer = doc.getString("answer");
                        List<String> options = (List<String>) doc.get("options");

                        Log.d("QuizDebug", "Document ID: " + doc.getId());
                        Log.d("QuizDebug", "Question: " + question);
                        Log.d("QuizDebug", "Answer: " + answer);
                        Log.d("QuizDebug", "Options: " + options);

                        if (question != null && answer != null && options != null && options.size() == 4) {
                            questionList.add(new QuestionModel(question, answer, options));
                        } else {
                            Log.w("QuizDebug", "Incomplete question data found in document: " + doc.getId());
                        }
                    }

                    Log.d("QuizDebug", "Valid questions loaded: " + questionList.size());

                    if (!questionList.isEmpty()) {
                        // Shuffle questions for variety
                        Collections.shuffle(questionList);
                        // Limit to 10 questions if more are available
                        if (questionList.size() > 10) {
                            questionList = questionList.subList(0, 10);
                        }
                        showQuestion(0);
                    } else {
                        Toast.makeText(this, "No quiz questions found for " + languageId, Toast.LENGTH_LONG).show();
                        Log.e("QuizDebug", "No valid questions found in database for language: " + languageId);
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("QuizLoadError", "Error loading questions: ", e);
                    Toast.makeText(this, "Failed to load questions: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    finish();
                });
    }

    private void showQuestion(int index) {
        if (index >= questionList.size()) {
            Log.d("QuizDebug", "All questions completed, going to result");
            goToResult();
            return;
        }

        Log.d("QuizDebug", "Showing question " + (index + 1) + " of " + questionList.size());

        QuestionModel model = questionList.get(index);
        questionText.setText(model.getQuestion());
        questionCountText.setText((index + 1) + "/" + questionList.size());

        option1.setText(model.getOptions().get(0));
        option2.setText(model.getOptions().get(1));
        option3.setText(model.getOptions().get(2));
        option4.setText(model.getOptions().get(3));

        // Update next button text based on current question
        if (index == questionList.size() - 1) {
            nextBtn.setText("Submit");
        } else {
            nextBtn.setText("Next");
        }

        resetQuestionState();
        startTimer();
    }

    private void resetQuestionState() {
        selectedAnswer = "";
        selectedButton = null;
        isAnswerChecked = false;

        resetOptionColors();
        enableAllOptions(true);
        nextBtn.setEnabled(false);
    }

    private void selectOption(AppCompatButton selectedBtn) {
        if (isAnswerChecked || !isTimerRunning) {
            return; // Don't allow selection after answer is checked or time is up
        }

        selectedAnswer = selectedBtn.getText().toString();
        selectedButton = selectedBtn;

        resetOptionColors();
        selectedBtn.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));

        nextBtn.setEnabled(true);

        // Change button text to show next action
        if (currentQuestionIndex == questionList.size() - 1) {
            nextBtn.setText("Check & Submit");
        } else {
            nextBtn.setText("Check Answer");
        }
    }

    private void resetOptionColors() {
        option1.setBackgroundColor(getResources().getColor(android.R.color.white));
        option2.setBackgroundColor(getResources().getColor(android.R.color.white));
        option3.setBackgroundColor(getResources().getColor(android.R.color.white));
        option4.setBackgroundColor(getResources().getColor(android.R.color.white));
    }

    private void enableAllOptions(boolean enabled) {
        option1.setEnabled(enabled);
        option2.setEnabled(enabled);
        option3.setEnabled(enabled);
        option4.setEnabled(enabled);
    }

    private void checkAnswer() {
        if (isAnswerChecked) return;

        Log.d("QuizDebug", "Checking answer for question " + (currentQuestionIndex + 1));

        isAnswerChecked = true;
        stopTimer();

        String correctAnswer = questionList.get(currentQuestionIndex).getAnswer();

        if (selectedButton != null && selectedAnswer.equals(correctAnswer)) {
            selectedButton.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
            correctAnswers++;
            Toast.makeText(this, "Correct! ✓", Toast.LENGTH_SHORT).show();
        } else {
            if (selectedButton != null) {
                selectedButton.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
            }

            // Show correct answer in green
            showCorrectAnswer(correctAnswer);

            if (selectedAnswer.isEmpty()) {
                Toast.makeText(this, "Time's up! ⏰", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Incorrect! ✗", Toast.LENGTH_SHORT).show();
            }
        }

        enableAllOptions(false);

        // Update button text after checking answer
        if (currentQuestionIndex == questionList.size() - 1) {
            nextBtn.setText("Submit Quiz");
        } else {
            nextBtn.setText("Next Question");
        }

        nextBtn.setEnabled(true);
    }

    private void showCorrectAnswer(String correctAnswer) {
        if (option1.getText().toString().equals(correctAnswer)) {
            option1.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
        } else if (option2.getText().toString().equals(correctAnswer)) {
            option2.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
        } else if (option3.getText().toString().equals(correctAnswer)) {
            option3.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
        } else if (option4.getText().toString().equals(correctAnswer)) {
            option4.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
        }
    }

    private void handleNextQuestion() {
        Log.d("QuizDebug", "Handling next question. Current index: " + currentQuestionIndex + ", Total questions: " + questionList.size());

        if (currentQuestionIndex < questionList.size() - 1) {
            currentQuestionIndex++;
            showQuestion(currentQuestionIndex);
        } else {
            Log.d("QuizDebug", "Quiz completed, going to result");
            goToResult();
        }
    }

    private void startTimer() {
        stopTimer(); // Stop any existing timer

        countDownTimer = new CountDownTimer(TOTAL_TIME, COUNT_DOWN_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                isTimerRunning = true;
                long seconds = millisUntilFinished / 1000;
                timer.setText(String.format(Locale.getDefault(), "00:%02d", seconds));

                // Change timer color when time is running low
                if (seconds <= 10) {
                    timer.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                } else {
                    timer.setTextColor(getResources().getColor(android.R.color.black));
                }
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                timer.setText("00:00");
                timer.setTextColor(getResources().getColor(android.R.color.holo_red_light));

                if (!isAnswerChecked) {
                    selectedAnswer = ""; // No answer selected
                    checkAnswer(); // This will handle time-up scenario
                }
            }
        };

        countDownTimer.start();
    }

    private void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            isTimerRunning = false;
        }
    }

    private void goToResult() {
        Log.d("QuizDebug", "=== goToResult() method called ===");
        stopTimer();

        int totalQuestions = questionList.size();
        double scorePercentage = (correctAnswers * 100.0) / totalQuestions;

        Log.d("QuizDebug", "Quiz completed - Correct: " + correctAnswers + "/" + totalQuestions);
        Log.d("QuizDebug", "About to create Intent for QuizResultActivity");

        try {
            Intent intent = new Intent(QuizActivity.this, Quizresult.class);
            Log.d("QuizDebug", "Intent created successfully");

            intent.putExtra("correctAnswers", correctAnswers);
            intent.putExtra("totalQuestions", totalQuestions);
            intent.putExtra("scorePercentage", scorePercentage);
            intent.putExtra("languageName", languageName);
            intent.putExtra("languageId", languageId);

            Log.d("QuizDebug", "Intent extras added, about to start activity");
            startActivity(intent); // ✅ Only one call

            Log.d("QuizDebug", "startActivity() called successfully");
            finish();
            Log.d("QuizDebug", "finish() called");

        } catch (Exception e) {
            Log.e("QuizDebug", "Exception in goToResult(): " + e.getMessage(), e);
            e.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopTimer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (questionList.size() > 0 && currentQuestionIndex < questionList.size() && !isAnswerChecked) {
            startTimer();
        }
    }

    @Override
    public void onBackPressed() {
        stopTimer();
        super.onBackPressed();
    }
}