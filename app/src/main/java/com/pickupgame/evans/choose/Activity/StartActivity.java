package com.pickupgame.evans.choose.Activity;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
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
import com.pickupgame.evans.choose.R;
import com.pickupgame.evans.choose.Services.MusicService;
import com.pickupgame.evans.choose.Utilities.var;
import com.plattysoft.leonids.ParticleSystem;

public class StartActivity extends musicAppCompatActivity {


    public static final int RC_SIGN_IN = 1001;
    private static final String ANONYMOUS = "ANONYMOUS";
    Button mButton, upload;
    int soundClick;
    SoundPool soundPool;
    //Firebase stuff
    private FirebaseDatabase mDatabase;
    private DatabaseReference mMessageDatabaseReference;
    private ChildEventListener mchildEventListener;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    private String mUsername = ANONYMOUS;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_start);

        //Iniatilizing references to views


        //init firebase Components
        mDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        mMessageDatabaseReference = mDatabase.getReference().child("messages");

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    //user is signin
                    onSignedInInitialized(user.getDisplayName());
//                    Toast.makeText(StartActivity.this, "good", Toast.LENGTH_LONG);
                } else {
                    //user is signed out
                    onSignedOutCleanup();
                    signIn();
                }
            }
        };

        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG).build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);

//        Map<String, Object> defaultConfigMap = new HashMap<>();
//        defaultConfigMap.put(FRIENDLY_MSG_LENGHT_KEY, DEFAULT_MSG_LENGTH_LIMIT);
//        mFirebaseRemoteConfig.setDefaults(defaultConfigMap);

//        fetchConfig();


        setUpViews();
        startEmitting();
        setButtonListener();
        startMusic();

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_GAME)
                .build();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool.Builder().setMaxStreams(1).setAudioAttributes(audioAttributes).build();
            soundClick = soundPool.load(this, R.raw.footttt, 1);
        } else {
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 1);
        }
    }

    public void playSoundWin(int id) {
        soundPool.play(id, 1.0f, 1.0f, 1, 0, 1);
    }

    private void startMusic() {
        //If some other song is already playing, stop the service
        if (var.playing) {
            Intent i = new Intent(StartActivity.this, MusicService.class);
            stopService(i);
        }

        var.backgroundMusic = R.raw.bensound_hey;
        var.playing = true;

        //Play the selected song by starting the service
        Intent start = new Intent(StartActivity.this, MusicService.class);
        startService(start);
    }

    private void signIn() {
        //                    startActivityForResult(
//                            AuthUI.getInstance()
//                                    .createSignInIntentBuilder()
//                                    .setAvailableProviders(Arrays.asList(
//                                            new AuthUI.IdpConfig.GoogleBuilder().build(),
//                                            new AuthUI.IdpConfig.EmailBuilder().build()))
//                                    .build(),
//                            RC_SIGN_IN);
        mFirebaseAuth.signInAnonymously();
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
        dettachDatabaseListener();
    }

    private void dettachDatabaseListener() {
        if (mchildEventListener != null) {
            mMessageDatabaseReference.removeEventListener(mchildEventListener);
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
//                    //to see new messages
//                    FriendlyMessage friendlyMessage = dataSnapshot.getValue(FriendlyMessage.class);
//                    friendlyMessage.set_my_message(friendlyMessage.getName().equals(mUsername));
//                    myDataset.add(friendlyMessage);
//                    mMessageAdapter.notifyDataSetChanged();
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };

            mMessageDatabaseReference.addChildEventListener(mchildEventListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        doBindService();
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
        //myDataset.clear();
        //mMessageAdapter.notifyDataSetChanged();
    }


    private void setUpViews() {
        mButton = findViewById(R.id.startbutton);
        upload = findViewById(R.id.upload);
    }

    private void startEmitting() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                new ParticleSystem(StartActivity.this, 50, R.drawable.star_pink, 1000, R.id.background)
                        .setSpeedRange(0.1f, 0.25f)
                        .emit(mButton, 100);
            }
        }, 500);

    }

    private void setButtonListener() {

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(StartActivity.this, CategoryActivity.class);
//                myIntent.putExtra("key", value); //Optional parameters
                StartActivity.this.startActivity(myIntent);
                var.isTransitionning = true;
                playSoundWin(soundClick);
                finish();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(StartActivity.this, UploadActivity.class);
//                myIntent.putExtra("key", value); //Optional parameters
                StartActivity.this.startActivity(myIntent);
            }
        });

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
                        // Log.w(TAG, "ERROR GETTING CONFIG");
                        applyRetrievedLengthLimit();
                    }
                });

    }

    private void applyRetrievedLengthLimit() {
//        Long friendly_msg_lenght = mFirebaseRemoteConfig.getLong(FRIENDLY_MSG_LENGHT_KEY);
//        messageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(friendly_msg_lenght.intValue())});
//        Log.d(TAG, FRIENDLY_MSG_LENGHT_KEY + " = " + friendly_msg_lenght);
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
}
