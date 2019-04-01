package com.pickupgame.evans.choose.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class QuizPhases {
    OkHttpClient client = new OkHttpClient();
    Quiz mQuiz;
    private QuizPhasesListener listener;

    public QuizPhases() {
        this.listener = null;
        try {
            doGetRequest("https://api.quizlet.com/2.0/sets/211562410?" + var.clientID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Assign the listener implementing events interface that will receive the events
    public void setQuizPhasesListener(QuizPhasesListener listener) {
        this.listener = listener;
    }

    void doGetRequest(String url) throws IOException {
        System.out.println(url);
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(final Call call, IOException e) {
                        // Error

//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                // For the example, you can show an error dialog or a toast
//                                // on the main UI thread
//                            }
//                        });

                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        String body = response.body().string();

                        try {
                            JSONObject bodyObject = new JSONObject(body);

                            JSONArray terms = bodyObject.getJSONArray("terms");
                            ArrayList<Term> myTerms = new ArrayList<>();
                            for (int i = 0; i < terms.length(); i++) {
                                JSONObject termObject = terms.getJSONObject(i);
                                if (termObject.has("image") && !termObject.isNull("image")) {
                                    JSONObject imageObject = termObject.getJSONObject("image");

                                    String term = termObject.getString("term");
                                    String imageUrl = imageObject.getString("url");

                                    myTerms.add(new Term(imageUrl, term));
                                }
                            }

                            mQuiz = new Quiz(myTerms);
                            if (listener != null)
                                listener.onDataLoaded(mQuiz); // <---- fire listener here

//                            System.out.println(mQuiz.getMyQuizItems().get(0).term);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
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