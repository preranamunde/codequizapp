package com.example.quiz;

import java.util.List;

public class QuestionModel {
    private String question;
    private String answer;
    private List<String> options;

    public QuestionModel() {
        // Empty constructor required for Firestore
    }

    public QuestionModel(String question, String answer, List<String> options) {
        this.question = question;
        this.answer = answer;
        this.options = options;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }
}