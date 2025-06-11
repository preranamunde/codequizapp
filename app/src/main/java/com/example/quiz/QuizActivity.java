package com.example.quiz;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QuizActivity extends AppCompatActivity {

    TextView questionText, questionCountText, topicName;
    AppCompatButton option1, option2, option3, option4, nextBtn;

    FirebaseFirestore db;
    List<QuestionModel> questionList = new ArrayList<>();
    int currentQuestionIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        topicName = findViewById(R.id.topicName);
        questionText = findViewById(R.id.questionText);
        questionCountText = findViewById(R.id.questionCountText);
        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        option3 = findViewById(R.id.option3);
        option4 = findViewById(R.id.option4);
        nextBtn = findViewById(R.id.nextBtn);

        db = FirebaseFirestore.getInstance();

        // Get languageId (documentId) from Intent
        String languageId = getIntent().getStringExtra("languageId");
        topicName.setText("Quiz");

        loadQuizQuestions(languageId);

        nextBtn.setOnClickListener(v -> {
            if (currentQuestionIndex < questionList.size() - 1) {
                currentQuestionIndex++;
                showQuestion(currentQuestionIndex);
            } else {
                Toast.makeText(this, "Quiz Completed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadQuizQuestions(String languageId) {
        db.collection("languages").document(languageId)
                .collection("Quiz")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String question = doc.getString("1");
                        String answer = doc.getString("answer");
                        List<String> options = (List<String>) doc.get("options");

                        if (question != null && answer != null && options != null && options.size() == 4) {
                            questionList.add(new QuestionModel(question, answer, options));
                        }
                    }

                    if (!questionList.isEmpty()) {
                        showQuestion(0);
                    } else {
                        Toast.makeText(this, "No quiz questions found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load quiz: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void showQuestion(int index) {
        QuestionModel model = questionList.get(index);
        questionText.setText(model.getQuestion());
        questionCountText.setText((index + 1) + "/" + questionList.size());

        option1.setText(model.getOptions().get(0));
        option2.setText(model.getOptions().get(1));
        option3.setText(model.getOptions().get(2));
        option4.setText(model.getOptions().get(3));
    }

    // Optional: Add click listener for selecting an answer
}
