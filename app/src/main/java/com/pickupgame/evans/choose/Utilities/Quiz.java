package com.pickupgame.evans.choose.Utilities;

import java.util.ArrayList;
import java.util.Random;

public class Quiz {

    public ArrayList<QuizItem> myQuizItems;
    Random rand = new Random();
    ArrayList<Term> myTerms;

    public Quiz() {
    }

    public Quiz(ArrayList<Term> myTerms) {
        this.myTerms = myTerms;
        myQuizItems = new ArrayList<>();
        for (int i = 0; i < myTerms.size(); i++) {
            myQuizItems.add(getQuizItem(i));
        }

    }

    public QuizItem getQuizItem(int i) {
        QuizItem quizItem = new QuizItem(myTerms.get(i).url, myTerms.get(i).term);
        quizItem.setWrongAnswer1(myTerms.get(getRandomInt(i)).term);
        quizItem.setWrongAnswer2(myTerms.get(getRandomInt(i)).term);
        quizItem.setWrongAnswer3(myTerms.get(getRandomInt(i)).term);
        quizItem.setWrongAnswer4(myTerms.get(getRandomInt(i)).term);
        return quizItem;
    }

    public ArrayList<QuizItem> getMyQuizItems() {
        return myQuizItems;
    }

    public void setMyQuizItems(ArrayList<QuizItem> myQuizItems) {
        this.myQuizItems = myQuizItems;
    }

    public ArrayList<Term> getMyTerms() {
        return myTerms;
    }

    public void setMyTerms(ArrayList<Term> myTerms) {
        this.myTerms = myTerms;
    }

    private int getRandomInt(int i) {

        int rand_int = rand.nextInt(myTerms.size());
        if (rand_int != i) {
            //  System.out.println(rand_int+ " i= "+i);
            return rand_int;
        } else {
            return getRandomInt(i);
        }
    }
}
