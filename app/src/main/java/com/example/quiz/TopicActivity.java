package com.example.quiz;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Typeface;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class TopicActivity extends AppCompatActivity {

    private static final String TAG = "TopicActivity";
    FirebaseFirestore db;
    LinearLayout languageList;
    private String currentLanguage;

    // Color palette for attractive UI cards
    private final String[] cardColors = {
            "#FF6B6B", "#4ECDC4", "#45B7D1", "#96CEB4", "#FFEAA7",
            "#DDA0DD", "#98D8C8", "#F7DC6F", "#BB8FCE", "#85C1E9"
    };
    private int colorIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);

        languageList = findViewById(R.id.languageList);
        db = FirebaseFirestore.getInstance();

        getWindow().getDecorView().setBackgroundColor(Color.parseColor("#F8F9FA"));

        // Get language name passed via Intent extras
        currentLanguage = getIntent().getStringExtra("languageName");
        if (currentLanguage == null) {
            currentLanguage = "Java"; // default fallback
        }

        Log.d(TAG, "Loading topics for language: " + currentLanguage);

        // Set activity title to show current language
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(currentLanguage + " Topics");
        }

        loadTopicsForLanguage(currentLanguage);
    }

    private void loadTopicsForLanguage(String languageName) {
        Log.d(TAG, "Querying for language: " + languageName);

        db.collection("languages")
                .whereEqualTo("name", languageName)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    Log.d(TAG, "Language query returned " + querySnapshot.size() + " documents");

                    if (!querySnapshot.isEmpty()) {
                        // Get the first matching document (assuming unique language names)
                        DocumentSnapshot languageDoc = querySnapshot.getDocuments().get(0);
                        String languageDocId = languageDoc.getId();

                        Log.d(TAG, "Found language document with ID: " + languageDocId);

                        // Fetch topics subcollection under that specific language document
                        db.collection("languages")
                                .document(languageDocId)
                                .collection("topics")
                                .get()
                                .addOnSuccessListener(topicsSnapshot -> {
                                    Log.d(TAG, "Topics query returned " + topicsSnapshot.size() + " documents");

                                    if (!topicsSnapshot.isEmpty()) {
                                        // Clear existing views first
                                        languageList.removeAllViews();

                                        for (DocumentSnapshot topicDoc : topicsSnapshot.getDocuments()) {
                                            String topicName = topicDoc.getString("name");
                                            String topicDesc = topicDoc.getString("desc");

                                            Log.d(TAG, "Processing topic: " + topicName + " for language: " + languageName);

                                            if (topicName != null) {
                                                // Pass both topic name and language name to the next activity
                                                addTopicToLayout(topicName, topicDesc != null ? topicDesc : "");
                                            }
                                        }
                                    } else {
                                        Log.w(TAG, "No topics found for language: " + languageName);
                                        showEmptyState();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Error loading topics for " + languageName, e);
                                    Toast.makeText(this, "Error loading topics: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    showEmptyState();
                                });
                    } else {
                        Log.w(TAG, "Language not found: " + languageName);
                        Toast.makeText(this, "Language '" + languageName + "' not found", Toast.LENGTH_SHORT).show();
                        showEmptyState();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error finding language: " + languageName, e);
                    Toast.makeText(this, "Error finding language: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    showEmptyState();
                });
    }

    private void addTopicToLayout(String name, String desc) {
        // Create main container for the topic card
        LinearLayout topicCard = new LinearLayout(this);
        topicCard.setOrientation(LinearLayout.VERTICAL);
        topicCard.setBackground(createCardBackground());
        topicCard.setElevation(dpToPx(6));
        topicCard.setPadding(dpToPx(24), dpToPx(20), dpToPx(24), dpToPx(20));

        // Topic name
        TextView topicText = new TextView(this);
        topicText.setText(name);
        topicText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        topicText.setTypeface(null, Typeface.BOLD);
        topicText.setTextColor(Color.WHITE);
        topicText.setGravity(Gravity.START);

        // Topic description (if available)
        TextView topicDescText = new TextView(this);
        if (desc != null && !desc.isEmpty()) {
            topicDescText.setText(desc);
            topicDescText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            topicDescText.setTextColor(Color.parseColor("#E0FFFFFF"));
            topicDescText.setGravity(Gravity.START);
            topicDescText.setPadding(0, dpToPx(4), 0, 0);
        }

        // Add views to card
        topicCard.addView(topicText);
        if (desc != null && !desc.isEmpty()) {
            topicCard.addView(topicDescText);
        }

        // Set layout parameters
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(dpToPx(16), dpToPx(8), dpToPx(16), dpToPx(8));
        topicCard.setLayoutParams(layoutParams);

        // Set click listener
        topicCard.setOnClickListener(v -> openCategoryScreen(name));

        languageList.addView(topicCard);
    }

    private GradientDrawable createCardBackground() {
        GradientDrawable gradient = new GradientDrawable();

        String primaryColor = cardColors[colorIndex % cardColors.length];
        colorIndex++;

        int startColor = Color.parseColor(primaryColor);
        int endColor = adjustBrightness(startColor, 0.8f);

        gradient.setColors(new int[]{startColor, endColor});
        gradient.setOrientation(GradientDrawable.Orientation.TL_BR);
        gradient.setCornerRadius(dpToPx(16));
        gradient.setStroke(dpToPx(1), Color.parseColor("#20FFFFFF"));

        return gradient;
    }

    private int adjustBrightness(int color, float factor) {
        int red = Math.round(Color.red(color) * factor);
        int green = Math.round(Color.green(color) * factor);
        int blue = Math.round(Color.blue(color) * factor);

        red = Math.max(0, Math.min(255, red));
        green = Math.max(0, Math.min(255, green));
        blue = Math.max(0, Math.min(255, blue));

        return Color.rgb(red, green, blue);
    }

    private void showEmptyState() {
        languageList.removeAllViews();

        LinearLayout emptyLayout = new LinearLayout(this);
        emptyLayout.setOrientation(LinearLayout.VERTICAL);
        emptyLayout.setGravity(Gravity.CENTER);
        emptyLayout.setPadding(dpToPx(32), dpToPx(64), dpToPx(32), dpToPx(64));

        TextView emptyIcon = new TextView(this);
        emptyIcon.setText("📚");
        emptyIcon.setTextSize(TypedValue.COMPLEX_UNIT_SP, 48);
        emptyIcon.setGravity(Gravity.CENTER);
        emptyIcon.setPadding(0, 0, 0, dpToPx(16));

        TextView emptyText = new TextView(this);
        emptyText.setText("No topics found for " + currentLanguage);
        emptyText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        emptyText.setTextColor(Color.parseColor("#666666"));
        emptyText.setGravity(Gravity.CENTER);
        emptyText.setTypeface(null, Typeface.BOLD);

        TextView emptySubtext = new TextView(this);
        emptySubtext.setText("Check back later for new " + currentLanguage + " topics!");
        emptySubtext.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        emptySubtext.setTextColor(Color.parseColor("#999999"));
        emptySubtext.setGravity(Gravity.CENTER);
        emptySubtext.setPadding(0, dpToPx(8), 0, 0);

        emptyLayout.addView(emptyIcon);
        emptyLayout.addView(emptyText);
        emptyLayout.addView(emptySubtext);

        languageList.addView(emptyLayout);
    }

    private void openCategoryScreen(String topicName) {
        Intent intent = new Intent(TopicActivity.this, CategoryActivity.class);
        intent.putExtra("SelectedTopic", topicName);
        intent.putExtra("languageName", currentLanguage); // Pass language to next activity
        startActivity(intent);

        // Optional transition animation
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    private int dpToPx(int dp) {
        return Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics()));
    }
}