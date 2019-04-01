package com.pickupgame.evans.choose.Utilities;

import java.util.ArrayList;

public class QuizItem {

    public String url, term, wrongAnswer1, wrongAnswer2, wrongAnswer3, wrongAnswer4;
    public ArrayList<String> mAnswers;

    public QuizItem(String url, String term, String wrongAnswer1, String wrongAnswer2, String wrongAnswer3, String wrongAnswer4) {
        this.url = url;
        this.term = term;
        this.wrongAnswer1 = wrongAnswer1;
        this.wrongAnswer2 = wrongAnswer2;
        this.wrongAnswer3 = wrongAnswer3;
        this.wrongAnswer4 = wrongAnswer4;

    }

    public QuizItem(String url, String term) {
        this.url = url;
        this.term = term;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getWrongAnswer1() {
        return wrongAnswer1;
    }

    public void setWrongAnswer1(String wrongAnswer1) {
        this.wrongAnswer1 = wrongAnswer1;
    }

    public String getWrongAnswer2() {
        return wrongAnswer2;
    }

    public void setWrongAnswer2(String wrongAnswer2) {
        this.wrongAnswer2 = wrongAnswer2;
    }

    public String getWrongAnswer3() {
        return wrongAnswer3;
    }

    public void setWrongAnswer3(String wrongAnswer3) {
        this.wrongAnswer3 = wrongAnswer3;
    }

    public String getWrongAnswer4() {
        return wrongAnswer4;
    }

    public void setWrongAnswer4(String wrongAnswer4) {
        this.wrongAnswer4 = wrongAnswer4;
    }

    public int getNextAnswerIndex(String currentAnswer) {
        if (mAnswers == null)
            buildArray();
        int i = mAnswers.indexOf(currentAnswer);
        if (i == 4)
            return 0;
        else
            return i + 1;
    }

    private void buildArray() {

        mAnswers = new ArrayList<>();
        mAnswers.add(term);
        mAnswers.add(wrongAnswer1);
        mAnswers.add(wrongAnswer2);
        mAnswers.add(wrongAnswer3);
        mAnswers.add(wrongAnswer4);
    }
}
