package com.pickupgame.evans.choose.Activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pickupgame.evans.choose.R;
import com.pickupgame.evans.choose.Utilities.FireTerm;
import com.pickupgame.evans.choose.Utilities.Term;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import static com.pickupgame.evans.choose.Activity.CategoryActivity.RC_SIGN_IN;

public class UploadActivity extends AppCompatActivity {

    private static final String TAG = "MESSAGE_APP";

    Button button;
    ArrayList<Term> myTerms;
    //Firebase stuff
    private FirebaseDatabase mDatabase;
    private DatabaseReference mTermDatabaseReference;
    private ChildEventListener mchildEventListener;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private String mUsername = "ANONYMOUS";
    private ValueEventListener mQuizValueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        //Iniatilizing references to views
        button = findViewById(R.id.button);

        //init firebase Components
        mDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();

        mTermDatabaseReference = mDatabase.getReference().child("Categories");


        //Adding functionality to the send button
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                //GET TERMS AND UPLOAD AFTER IMAGE UPLOAD
                String mNames = "[ \n" +
                        "\"ARIANA_GRANDE\", \n" +
                        "\"QUEEN\", \n" +
                        "\"POST_MALONE\", \n" +
                        "\"KHALID\", \n" +
                        "\"JUICE_WRLD\", \n" +
                        "\"IMAGINE_DRAGONS\", \n" +
                        "\"CARDI_B\", \n" +
                        "\"BTS\", \n" +
                        "\"LADY_GAGA\", \n" +
                        "\"HALSEY\", \n" +
                        "\"DRAKE\", \n" +
                        "\"BILLIE_EILISH\", \n" +
                        "\"LUKE_COMBS\", \n" +
                        "\"PANIC_AT_THE_DISCO\", \n" +
                        "\"PINK\", \n" +
                        "\"BRUNO_MARS\", \n" +
                        "\"LAUREN_DAIGLE\", \n" +
                        "\"CHRIS_STAPLETON\", \n" +
                        "\"BRADLEY_COOPER\", \n" +
                        "\"TRAVIS_SCOTT\", \n" +
                        "\"DAN_PLUS_SHAY\", \n" +
                        "\"KANE_BROWN\", \n" +
                        "\"JONAS_BROTHERS\", \n" +
                        "\"ED_SHEERAN\", \n" +
                        "\"MARSHMELLO\", \n" +
                        "\"J_COLE\", \n" +
                        "\"EMINEM\", \n" +
                        "\"MAREN_MORRIS\", \n" +
                        "\"LIL_BABY\", \n" +
                        "\"HOZIER\", \n" +
                        "\"AVA_MAX\", \n" +
                        "\"ELLA_MAI\", \n" +
                        "\"MAROON_5\", \n" +
                        "\"SWAE_LEE\", \n" +
                        "\"YNW_MELLY\", \n" +
                        "\"FLORIDA_GEORGIA_LINE\", \n" +
                        "\"21_SAVAGE\", \n" +
                        "\"MEEK_MILL\", \n" +
                        "\"XXXTENTACION\", \n" +
                        "\"SHAWN_MENDES\", \n" +
                        "\"BLUEFACE\", \n" +
                        "\"METALLICA\", \n" +
                        "\"TWENTY_ONE_PILOTS\", \n" +
                        "\"DEAN_LEWIS\", \n" +
                        "\"BRETT_YOUNG\", \n" +
                        "\"THOMAS_RHETT\", \n" +
                        "\"A_BOOGIE_WIT_DA_HOODIE\", \n" +
                        "\"THE_CHAINSMOKERS\", \n" +
                        "\"KODAK_BLACK\", \n" +
                        "\"TAYLOR_SWIFT\", \n" +
                        "\"CASTING_CROWNS\", \n" +
                        "\"SAM_SMITH\", \n" +
                        "\"JASON_ALDEAN\", \n" +
                        "\"5_SECONDS_OF_SUMMER\", \n" +
                        "\"NORMANI\", \n" +
                        "\"PAUL_MCCARTNEY\", \n" +
                        "\"BAD_BUNNY\", \n" +
                        "\"KENDRICK_LAMAR\", \n" +
                        "\"PINKFONG\", \n" +
                        "\"EXO\", \n" +
                        "\"FLEETWOOD_MAC\", \n" +
                        "\"GEORGE_STRAIT\", \n" +
                        "\"BEBE_REXHA\", \n" +
                        "\"TOM_PETTY_AND_THE_HEARTBREAKERS\", \n" +
                        "\"LIL_NAS_X\", \n" +
                        "\"_WEEZER\", \n" +
                        "\"GUNNA\", \n" +
                        "\"KACEY_MUSGRAVES\", \n" +
                        "\"THE_BEATLES\", \n" +
                        "\"CITY_GIRLS\", \n" +
                        "\"BASTILLE\", \n" +
                        "\"MICHAEL_JACKSON\", \n" +
                        "\"CARRIE_UNDERWOOD\", \n" +
                        "\"KELSEA_BALLERINI\", \n" +
                        "\"MERCYME\", \n" +
                        "\"IGGY_AZALEA\", \n" +
                        "\"ADELE\", \n" +
                        "\"MICHAEL_BUBLE\", \n" +
                        "\"LYNYRD_SKYNYRD\", \n" +
                        "\"ELVIS_PRESLEY\", \n" +
                        "\"CAMILA_CABELLO\", \n" +
                        "\"KATY_PERRY\", \n" +
                        "\"OFFSET\", \n" +
                        "\"J_BALVIN\", \n" +
                        "\"JERRY_GARCIA\", \n" +
                        "\"DADDY_YANKEE\", \n" +
                        "\"EAGLES\", \n" +
                        "\"LAUV\", \n" +
                        "\"GRETA_VAN_FLEET\", \n" +
                        "\"LUKE_BRYAN\", \n" +
                        "\"YOUNGBOY_NEVER_BROKE_AGAIN\", \n" +
                        "\"BENNY_BLANCO\", \n" +
                        "\"AC-DC\", \n" +
                        "\"MIGOS\", \n" +
                        "\"JAKE_OWEN\", \n" +
                        "\"JOURNEY\", \n" +
                        "\"OLD_DOMINION\", \n" +
                        "\"MORGAN_WALLEN\", \n" +
                        "\"NEW_KIDS_ON_THE_BLOCK\", \n" +
                        "\"ERIC_CHURCH\" \n" +
                        "]";
                try {
                    JSONArray mNameArray = new JSONArray(mNames);
                    if (myTerms == null)
                        myTerms = new ArrayList<>();
                    for (int i = 0; i < mNameArray.length(); i++) {
                        Term mTerm = new Term("https://gridmapp.net/CAT/musicians_01/" + mNameArray.getString(i) + ".jpg", mNameArray.getString(i).replace("_", " "));
                        System.out.println(mTerm.getUrl());
                        myTerms.add(mTerm);
                    }
                    FireTerm mTermArray = new FireTerm();
                    mTermArray.setTermList(myTerms);
                    mTermDatabaseReference.child("MUSICIANS").setValue(mTermArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //bu

            }
        });

//        // ImagePickerButton shows an image picker to upload a image for a message
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                intent.setType("image/jpeg");
//                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
//                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
//            }
//        });

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        //   mRecyclerView.setHasFixedSize(true);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    //user is signin
                    onSignedInInitialized(user.getDisplayName());
                    Toast.makeText(UploadActivity.this, "good", Toast.LENGTH_LONG);
                } else {
                    //user is signed out
                    onSignedOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                                            new AuthUI.IdpConfig.EmailBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
    }

    private void onSignedOutCleanup() {
        mUsername = "ANONYMOUS";
//        mCategories.clear();
//        mMessageAdapter.notifyDataSetChanged();
        dettachDatabaseListener();
    }

    private void dettachDatabaseListener() {
        if (mchildEventListener != null) {
            mTermDatabaseReference.removeEventListener(mchildEventListener);
            mchildEventListener = null;
        }
    }

    private void onSignedInInitialized(String displayName) {
        mUsername = displayName;
        attachDatabaseReadListener();
    }

    private void attachDatabaseReadListener() {
        mQuizValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                System.out.println(dataSnapshot);
                FireTerm mTermArray = dataSnapshot.getValue(FireTerm.class);
                myTerms = new ArrayList<>();
                for (Term i : mTermArray.getTermList()) {

                    MyTask task = new MyTask();
                    task.execute(i.url);
                    // myTerms.add(i);
                }
//                //    System.out.println("------------------------- + " + mTermArray.terms.get(3).url);
////                    System.out.println("------------------------- + "+mTermArray.mTerms.size());
//                mQuiz = new Quiz(mTermArray.getTermList());
//                startNewQuestion();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mTermDatabaseReference.child("FLAGS").addListenerForSingleValueEvent(mQuizValueEventListener);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        dettachDatabaseListener();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.signout:
                //sign out
                AuthUI.getInstance().signOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.signout, menu);
        return true;
    }

    private class MyTask extends AsyncTask<String, Void, Boolean> {
        String url;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Boolean doInBackground(String... params) {
//            System.out.println(params[0]);
            url = params[0];
            try {
                HttpURLConnection.setFollowRedirects(false);
                HttpURLConnection con = (HttpURLConnection) new URL(params[0]).openConnection();
                con.setRequestMethod("HEAD");
//                System.out.println(con.getResponseCode());
                return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            boolean bResponse = result;
            if (bResponse == true) {
                System.out.println(".");
//                System.out.println(i.url);
//                Toast.makeText(UploadActivity.this, "File exists!", Toast.LENGTH_SHORT).show();
            } else {
                System.out.println(" FAILED " + url);
//                Toast.makeText(UploadActivity.this, "File does not exist!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

