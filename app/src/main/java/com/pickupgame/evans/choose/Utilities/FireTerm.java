package com.pickupgame.evans.choose.Utilities;

import java.util.ArrayList;

public class FireTerm {

    ArrayList<Term> termList = new ArrayList<>();
    int currentIndex;
    long score;

    public FireTerm() {
    }

    public FireTerm(ArrayList<Term> termList) {
        this.termList = termList;
    }

    public ArrayList<Term> getTermList() {
        return termList;
    }

    public void setTermList(ArrayList<Term> termList) {
        this.termList = termList;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }
}
