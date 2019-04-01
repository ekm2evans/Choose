package com.pickupgame.evans.choose.Utilities;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class QuizPhasesFireBaseVersion {
    Quiz mQuiz;
    private QuizPhasesListener listener;


    private FirebaseDatabase mDatabase;
    private DatabaseReference mTermDatabaseReference;
    private ValueEventListener mQuizValueEventListener;

    public QuizPhasesFireBaseVersion() {
        mDatabase = FirebaseDatabase.getInstance();
        mTermDatabaseReference = mDatabase.getReference().child("Categories");
        attachDatabaseQuizReadListener();
        this.listener = null;
    }

    // Assign the listener implementing events interface that will receive the events
    public void setQuizPhasesListener(QuizPhasesListener listener) {
        this.listener = listener;
    }

    private void dettachDatabaseListener() {
        if (mQuizValueEventListener != null) {
            mTermDatabaseReference.removeEventListener(mQuizValueEventListener);
            mQuizValueEventListener = null;
        }
    }

    private void attachDatabaseQuizReadListener() {
        if (mQuizValueEventListener == null) {
            mQuizValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    System.out.println(dataSnapshot);
//                    TermArray mTermArray = dataSnapshot.getValue(TermArray.class);
//                    System.out.println("------------------------- + "+mTermArray.myTerms.size());
//                    mQuiz = new Quiz(mTermArray.getMyTerms());
//                    listener.onDataLoaded(mQuiz);
//                    setQuizViewOnScreen(mQuiz, counter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
        }

        mTermDatabaseReference.addListenerForSingleValueEvent(mQuizValueEventListener);
    }

    // Step 1 - This interface defines the type of messages I want to communicate to my owner
    public interface QuizPhasesListener {
        // These methods are the different events and
        // need to pass relevant arguments related to the event triggered
        void onQuizReady();

        // or when data has been loaded
        void onDataLoaded(Quiz quiz);

        void onDataFailed();


    }

}