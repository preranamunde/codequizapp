package com.example.quiz;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DescriptionActivity extends AppCompatActivity {

    private static final String TAG = "DescriptionActivity";
    TextView topicTitle, topicDescription;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        topicTitle = findViewById(R.id.topicTitle);
        topicDescription = findViewById(R.id.topicDescription);
        db = FirebaseFirestore.getInstance();

        String topicName = getIntent().getStringExtra("SelectedTopic");
        String languageName = getIntent().getStringExtra("languageName");

        if (getSupportActionBar() != null && topicName != null) {
            getSupportActionBar().setTitle(topicName);
        }

        topicTitle.setText(topicName != null ? topicName : "Topic");

        if (topicName != null && languageName != null) {
            loadTopicDescription(languageName, topicName);
        } else {
            topicDescription.setText("No topic or language selected.");
        }
    }

    private void loadTopicDescription(String languageName, String topicName) {
        db.collection("languages")
                .whereEqualTo("name", languageName)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot languageDoc = querySnapshot.getDocuments().get(0);
                        String languageId = languageDoc.getId();

                        db.collection("languages")
                                .document(languageId)
                                .collection("topics")
                                .whereEqualTo("name", topicName)
                                .get()
                                .addOnSuccessListener(topicSnapshot -> {
                                    if (!topicSnapshot.isEmpty()) {
                                        DocumentSnapshot topicDoc = topicSnapshot.getDocuments().get(0);
                                        String desc = topicDoc.getString("desc");
                                        topicDescription.setText(desc != null ? desc : "No description found.");
                                    } else {
                                        topicDescription.setText("Topic not found.");
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Error loading topic: ", e);
                                    topicDescription.setText("Error loading topic.");
                                });
                    } else {
                        topicDescription.setText("Language not found.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading language: ", e);
                    topicDescription.setText("Error loading language.");
                });
    }
}
