package com.example.quiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    // Maps for TextViews and Layouts
    private Map<String, Integer> docIdToTextViewId;
    private Map<String, LinearLayout> docIdToLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();

        // Map Firestore document IDs to TextView IDs
        docIdToTextViewId = new HashMap<>();
        docIdToTextViewId.put("1", R.id.java);
        docIdToTextViewId.put("2", R.id.php);
        docIdToTextViewId.put("3", R.id.cpp);
        docIdToTextViewId.put("4", R.id.python);
        docIdToTextViewId.put("5", R.id.html);
        docIdToTextViewId.put("6", R.id.javascript);
        docIdToTextViewId.put("7", R.id.react);
        docIdToTextViewId.put("8", R.id.ruby);

        // Map Firestore document IDs to Layouts
        docIdToLayout = new HashMap<>();
        docIdToLayout.put("1", findViewById(R.id.javaLayout));
        docIdToLayout.put("2", findViewById(R.id.phpLayout));
        docIdToLayout.put("3", findViewById(R.id.cppLayout));
        docIdToLayout.put("4", findViewById(R.id.pythonLayout));
        docIdToLayout.put("5", findViewById(R.id.htmlLayout));
        docIdToLayout.put("6", findViewById(R.id.jsLayout));
        docIdToLayout.put("7", findViewById(R.id.reactLayout));
        docIdToLayout.put("8", findViewById(R.id.rubyLayout));

        // Load language names and set click listeners
        loadLanguageNames();
    }

    private void loadLanguageNames() {
        db.collection("languages")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String docId = doc.getId();
                        String languageName = doc.getString("name");

                        if (languageName != null && docIdToTextViewId.containsKey(docId)) {
                            TextView tv = findViewById(docIdToTextViewId.get(docId));
                            tv.setText(languageName);

                            LinearLayout layout = docIdToLayout.get(docId);
                            if (layout != null) {
                                layout.setOnClickListener(v -> {
                                    Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
                                    intent.putExtra("selectLanguage", languageName);  // ✅ pass language name
                                    startActivity(intent);
                                });
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MainActivity.this, "Failed to load languages: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
