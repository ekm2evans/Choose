package com.pickupgame.evans.choose.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.BuildConfig;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.pickupgame.evans.choose.Adapters.CategoryAdapter;
import com.pickupgame.evans.choose.Adapters.CategoryList;
import com.pickupgame.evans.choose.R;
import com.pickupgame.evans.choose.Utilities.var;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class CategoryActivity extends musicAppCompatActivity {

    private static final int DEFAULT_MSG_LENGTH_LIMIT = 100;
    public static final int RC_SIGN_IN = 1001;
    private static final String ANONYMOUS = "ANONYMOUS";
    private static final String FRIENDLY_MSG_LENGHT_KEY = "com.google.firebase:firebase-config:16.3.0" ;
    private static final String TAG = "MESSAGE_APP" ;
    private static final int RC_PHOTO_PICKER = 1002;
    private RecyclerView mRecyclerView;
    ArrayList<CategoryList> mCategories;
    private RecyclerView.LayoutManager mLayoutManager;
    private CategoryAdapter mCategoryAdapter;


    //Firebase stuff
    private FirebaseDatabase mDatabase;
    private DatabaseReference mCategoryDatabaseReference;
    private ChildEventListener mchildEventListener;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    private String mUsername= ANONYMOUS;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Iniatilizing references to views
        mCategories = new ArrayList<>();
        mRecyclerView =  findViewById(R.id.mview);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        mCategoryAdapter = new CategoryAdapter(mCategories, R.layout.category_item);

        //init firebase Components
        mDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        mCategoryDatabaseReference = mDatabase.getReference().child("Categories");

     //   mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mRecyclerView.setAdapter(mCategoryAdapter);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user !=null ) {
                    //user is signin
                    onSignedInInitialized(user.getDisplayName());
                    Toast.makeText(CategoryActivity.this, "good", Toast.LENGTH_LONG);
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

        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG).build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);

        Map<String, Object> defaultConfigMap = new HashMap<>();
        defaultConfigMap.put(FRIENDLY_MSG_LENGHT_KEY, DEFAULT_MSG_LENGTH_LIMIT);
        mFirebaseRemoteConfig.setDefaults(defaultConfigMap);

        fetchConfig();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (requestCode == RESULT_OK) {
                Toast.makeText(this, "Signed in", Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                finish();
                Toast.makeText(this, "Signed in cancelled", Toast.LENGTH_LONG).show();
            }
        }
        }


    private void onSignedOutCleanup() {
        mUsername = ANONYMOUS;
        mCategories.clear();
        mCategoryAdapter.notifyDataSetChanged();
        dettachDatabaseListener();
    }

    private void dettachDatabaseListener() {
        if (mchildEventListener !=null) {
            mCategoryDatabaseReference.removeEventListener(mchildEventListener);
        mchildEventListener = null;
        }
    }

    private void onSignedInInitialized(String displayName) {
        mUsername = displayName;
        attachDatabaseReadListener();
    }

    private void attachDatabaseReadListener() {
        if (mchildEventListener == null) {
        mchildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //Load all the keys as Category title from
                // CATEGORIES DATABASE SET and feed those keys into recyclerVIew dataset;
                CategoryList mCategory = new CategoryList(dataSnapshot.getKey());
                mCategories.add(mCategory);
                mCategoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //UPDATE RECYCLER VIEW
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                //UPDATE RECYCLER VIEW
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

            mCategoryDatabaseReference.addChildEventListener(mchildEventListener);
    }}

    @Override
    protected void onResume() {
        super.onResume();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(uiOptions);
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        dettachDatabaseListener();
        mCategories.clear();
        mCategoryAdapter.notifyDataSetChanged();
    }

    public void fetchConfig() {
        long cacheExpiration = 3600;

        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }

        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mFirebaseRemoteConfig.activateFetched();
                        applyRetrievedLengthLimit();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "ERROR GETTING CONFIG");
                        // applyRetrievedLengthLimit();
                    }
                });

    }

    private void applyRetrievedLengthLimit() {
    Long friendly_msg_lenght = mFirebaseRemoteConfig.getLong(FRIENDLY_MSG_LENGHT_KEY);
    Log.d(TAG, FRIENDLY_MSG_LENGHT_KEY + " = " + friendly_msg_lenght);
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

    private void startActivityWithCategory(String category) {
        Intent myIntent = new Intent(CategoryActivity.this, GameActivity.class);
        myIntent.putExtra(var.CATEGORY, category); //Optional parameters

        CategoryActivity.this.startActivity(myIntent);
    }
}
