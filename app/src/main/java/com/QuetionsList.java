package com;

public class QuetionsList {
    private String quetion,option1,option2,option3,option4,answer;

    private String userSelectedAnswer;

    public QuetionsList(String quetion,String option1,String option2,String option3,String option4,String answer,String userSelectedAnswer){
        this.quetion = quetion;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
        this.answer = answer;
        this.userSelectedAnswer = userSelectedAnswer;
    }
    public String getQuetion(){
        return quetion;
    }
    public String getOption1(){
        return option1;
    }

    public void setUserSelectedAnswer(String userSelectedAnswer) {
        this.userSelectedAnswer = userSelectedAnswer;
    }

    public String getOption2(){
        return option2;
    }
    public String getOption3(){
        return option3;
    }
    public String getOption4(){
        return option4;
    }
    public String getAnswer(){
        return answer;
    }
    public String getUserSelectedAnswer(){
        return userSelectedAnswer;
    }
}
