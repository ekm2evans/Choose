package com.pickupgame.evans.choose.Activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mattyork.colours.Colour;
import com.pickupgame.evans.choose.BuildConfig;
import com.pickupgame.evans.choose.R;
import com.pickupgame.evans.choose.Utilities.FireTerm;
import com.pickupgame.evans.choose.Utilities.Quiz;
import com.pickupgame.evans.choose.Utilities.Term;
import com.pickupgame.evans.choose.Utilities.var;
import com.plattysoft.leonids.ParticleSystem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import tyrantgit.explosionfield.ExplosionField;

public class GameActivity extends AppCompatActivity {

    public static final int RC_SIGN_IN = 1001;
    private static final String ANONYMOUS = "ANONYMOUS";
    private static final String TAG = "AnimationStarter";
    static Random randomGenerator;
    private static int counter = 0;

    static {
        randomGenerator = new Random();
    }

    final int MiddleMiddle = 0, TopRight = 1, TopLeft = 2, BottomLeft = 3, BottomRight = 4;
    public Quiz mQuiz = null;
    public long score = 0;
    TextView top_left, top_right, middle_middle, bottom_left, bottom_right, scoreBox, answer;
    ImageView mImage;
    TableLayout mTable;
    boolean isFirstQuestion = true;
    boolean isPlaying = false;
    ExplosionField explosionField;
    SoundPool soundPool;
    MediaPlayer mp;
    HashMap<Integer, Integer> soundPoolMap;
    int soundClap, soundLongClap, soundLaughter;
    ArrayList<AnimatorSet> animatorSetDown = new ArrayList<>();
    ArrayList<AnimatorSet> animatorSetUp = new ArrayList<>();
    Handler time;
    int timeCounter;
    ParticleSystem emitter;
    int scaledowncounter;
    private String CURRENTCATEGORY = "";
    //Firebase stuff
    private FirebaseDatabase mDatabase;
    private DatabaseReference mTermDatabaseReference;
    private DatabaseReference mProgressTermDatabaseReference;
    private ChildEventListener mchildEventListener;
    private ValueEventListener mQuizValueEventListener;
    private ValueEventListener mQuizValueEventListenerProgress;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mPhotoStorageReference;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private String mUsername = ANONYMOUS;
    private long timeOutScore = 20000000;
    private long pauseAnswerDelay = 3000;
    private int attemptCounter;
    private boolean isPaused;
    private View.OnClickListener checkAnswerAndContinue = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TextView mChoice = (TextView) v;
            System.out.println(" ");
            System.out.println(" ");
            if (mChoice.getText().toString().equals(mQuiz.getMyQuizItems().get(counter).term)) {
                isPlaying = false;
                System.out.println("---------------------------------------         CLICK   >>>>>>> CORRECT");
                //System.out.println("Correct");
                switch (attemptCounter) {
                    case 0:
                        //Increase score by 10 points if correct on first try
                        score += 10;
                        break;
                    case 1:
                        //Increase score by 10 points if correct on second try
                        score += 5;
                        break;
                    case 2:
                        //Increase score by 10 points if correct on third try
                        score += 2;
                        break;
                }
                if (score % 100 == 0) {
                    playSoundWin(soundLongClap);
                } else {
                    playSoundWin(soundClap);
                }
                // mergeAllTextColorExcept((TextView) v);
                stopAnimation(v.getId());
                updateScoreBoard(score + "");
                setClickable(false);
                goToNextQuestion();

            } else {
                System.out.println("---------------------------------------         CLICK   >>>>>>> INCORRECT");


                contrastTextColor(mChoice);
                Animation animShake;
                animShake = AnimationUtils.loadAnimation(GameActivity.this, R.anim.shakeextra);
                mChoice.startAnimation(animShake);
                switch (attemptCounter) {
                    case 0:
                        //Increase score by 10 points if correct on first try
                        attemptCounter += 1;
                        shakeAll(R.anim.shake);
                        break;
                    case 1:
                        //Increase score by 10 points if correct on second try
                        attemptCounter += 1;
                        shakeAll(R.anim.shake2);
                        break;
                    case 2:
                        //Increase score by 10 points if correct on third try
                        if ((score - 2) > 0)
                            score -= 2;
                        updateScoreBoard(score + "");
                        stopAnimation(0);
                        mergeAllTextColorExcept(mChoice);
                        goToNextQuestion();
                        break;
                }

                playSoundWin(soundLaughter);
            }
            System.out.println("---------------------------------------         ATTEMPT   >>>>>>> " + attemptCounter);

        }


    };

    private long timeOnScreen = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_game);

        Intent intent = getIntent();

        CURRENTCATEGORY = intent.getStringExtra(var.CATEGORY);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //Iniatilizing references to views
        mImage = findViewById(R.id.image);
        top_left = findViewById(R.id.view_top_left);
        top_right = findViewById(R.id.view_top_right);
        middle_middle = findViewById(R.id.view_middle_middle);
        bottom_left = findViewById(R.id.view_bottom_left);
        bottom_right = findViewById(R.id.view_bottom_right);
        scoreBox = findViewById(R.id.score);
        answer = findViewById(R.id.answer);
        mTable = findViewById(R.id.table);

        for (int i = 0; i < 5; i++) {
            AnimatorSet mAnim = new AnimatorSet();
            animatorSetUp.add(mAnim);
        }

        for (int i = 0; i < 5; i++) {
            AnimatorSet mAnim = new AnimatorSet();
            animatorSetDown.add(mAnim);
        }
        explosionField = ExplosionField.attach2Window(this);

//        //ClickListerner
//        middle_middle.setOnClickListener(checkAnswerAndContinue);
//        top_left.setOnClickListener(checkAnswerAndContinue);
//        top_right.setOnClickListener(checkAnswerAndContinue);
//        bottom_left.setOnClickListener(checkAnswerAndContinue);
//        bottom_right.setOnClickListener(checkAnswerAndContinue);

        //init firebase Components
        mDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        mTermDatabaseReference = mDatabase.getReference().child("Categories");
        mProgressTermDatabaseReference = mDatabase.getReference().child("USER");
        mPhotoStorageReference = mFirebaseStorage.getReference().child("chat_photos");

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    //user is signin
                    onSignedInInitialized(user.getUid());
                    Toast.makeText(GameActivity.this, "good", Toast.LENGTH_LONG);
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
        defaultConfigMap.put("key name", "data");
        mFirebaseRemoteConfig.setDefaults(defaultConfigMap);

        fetchConfig();

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_GAME)
                .build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool.Builder().setMaxStreams(1).setAudioAttributes(audioAttributes).build();
            soundClap = soundPool.load(this, R.raw.applause_8, 1);
            soundLongClap = soundPool.load(this, R.raw.applause_3, 1);
            soundLaughter = soundPool.load(this, R.raw.laughter_2, 1);
        } else {
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 1);

        }

        answer.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                playSoundWin(1);
            }

        });

        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playSoundWin(3);
            }
        });

    }

    private void playSoundWin(int id) {
        if (!isPaused)
            soundPool.play(id, 1.0f, 1.0f, 1, 0, 1);
    }

    public long getRandomDelay() {
        long x = 0L;
        long y = 5000;
        Random r = new Random();
        return x + ((long) (r.nextDouble() * (y - x)));
    }

    public long getRandomDelay(long rangeStart, long rangeEnd) {
        long x = rangeStart;
        long y = rangeEnd;
        Random r = new Random();
        return x + ((long) (r.nextDouble() * (y - x)));
    }

    private void bounce() {
        {
            TranslateAnimation transAnim = new TranslateAnimation(0, 0, 0,
                    getDisplayHeight() / 2);
            transAnim.setStartOffset(00);
            transAnim.setDuration(1000);
            transAnim.setFillAfter(true);
            transAnim.setInterpolator(new OvershootInterpolator());
            transAnim.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                    Log.i(TAG, "Starting button dropdown animation");
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    Log.i(TAG,
                            "Ending button dropdown animation. Clearing animation and setting layout");
                    middle_middle.clearAnimation();
                    //bounce();

                }
            });
            middle_middle.startAnimation(transAnim);
        }
    }

    private void scaledownOnce(final View v, int viewID) {

        long delay = 0;
        Animator.AnimatorListener mListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (!isPlaying && !isFirstQuestion) {
                    scaledowncounter += 1;
                }
                animator.removeAllListeners();
                System.out.println("-------------------> " + scaledowncounter);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        };
        if (v.getId() != viewID) {
            delay = 0;
        } else {
            delay = pauseAnswerDelay;
        }
        // Make the object width 50%
        ObjectAnimator anim = ObjectAnimator.ofFloat(v, "scaleX", 0.0f);

        // Make the object width 50%
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(v, "scaleY", 0.0f);

        AnimatorSet animatorSetDownOnce = new AnimatorSet();
        List<Animator> animations = new ArrayList<Animator>();
//        AnimatorSet animatorSetUp = new AnimatorSet();
        animations.add(anim);
        animations.add(anim2);

        animatorSetDownOnce.setDuration(500);
        animatorSetDownOnce.setStartDelay(delay);
        animatorSetDownOnce.playTogether(animations);

        if (!isFirstQuestion)
            animatorSetDownOnce.addListener(mListener);
        animatorSetDownOnce.start();
    }

    private void scaledown(View v, int viewLocation) {
        if (isFirstQuestion)
            contrastTextColor((TextView) v);
        long randAnimDuration = getRandomDelay(1000, 4000);
        // Make the object width 50%
        ObjectAnimator anim = ObjectAnimator.ofFloat(v, "scaleX", 0.0f);
        // Make the object width 50%
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(v, "scaleY", 0.0f);
        List<Animator> animations = new ArrayList<Animator>();
        animations.add(anim);
        animations.add(anim2);
        animatorSetDown.get(viewLocation).playTogether(animations);
        animatorSetDown.get(viewLocation).setStartDelay(timeOnScreen);
        animatorSetDown.get(viewLocation).setDuration(randAnimDuration);

        Animator.AnimatorListener animatorListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
//                if (!isPlaying) {
//                    animatorSetDown.get(viewLocation).cancel();
//                    System.out.println("------------------------------------- END DOWN ANIMATION  off >> ");
//                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (isPlaying) {
                    scaleup(v, viewLocation);
                    shuffleAlone((TextView) v);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        };
        if (animatorSetDown.get(viewLocation).getListeners() == null) {
            animatorSetDown.get(viewLocation).addListener(animatorListener);
        }
        animatorSetDown.get(viewLocation).start();

    }

    private void shuffleAlone(TextView v) {
        String currentAnswer = v.getText().toString();
        System.out.println(currentAnswer);
        int i = mQuiz.myQuizItems.get(counter).getNextAnswerIndex(currentAnswer);
        v.setText(mQuiz.myQuizItems.get(counter).mAnswers.get(i));
    }


    private void scaleup(View v, int viewLocation) {
        // Make the object width 50%
        contrastTextColor((TextView) v);
        long randAnimDuration = getRandomDelay(2000, 4000);
        long randDurationBeforeGoingDown = getRandomDelay(1000, 2000);

        ObjectAnimator anim = ObjectAnimator.ofFloat(v, "scaleX", 1.0f);
        // Make the object width 50%
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(v, "scaleY", 1.0f);

        List<Animator> animations = new ArrayList<Animator>();
        animations.add(anim);
        animations.add(anim2);

        animatorSetUp.get(viewLocation).playTogether(animations);
        animatorSetUp.get(viewLocation).setDuration(randAnimDuration);
        animatorSetUp.get(viewLocation).setStartDelay(1000);

        Animator.AnimatorListener mListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (isPlaying) {
                    scaledown(v, viewLocation);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                System.out.println("cancel");
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        };

        if (animatorSetUp.get(viewLocation).getListeners() == null) {
            animatorSetUp.get(viewLocation).addListener(mListener);
        }
        animatorSetUp.get(viewLocation).start();
    }


    private void scaleupScore(View v) {
        // Make the object width 50%
        ObjectAnimator anim = ObjectAnimator.ofFloat(v, "scaleX", 1.5f);
        // Make the object width 50%
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(v, "scaleY", 1.5f);

        List<Animator> animations = new ArrayList<Animator>();
        animations.add(anim);
        animations.add(anim2);


        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animations);
        animatorSet.setDuration(200);
        animatorSet.start();

        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                scaledownSore(v);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    private void scaledownSore(View v) {
        {
            // Make the object width 50%
            ObjectAnimator anim = ObjectAnimator.ofFloat(v, "scaleX", 1f);
            // Make the object width 50%
            ObjectAnimator anim2 = ObjectAnimator.ofFloat(v, "scaleY", 1f);

            List<Animator> animations = new ArrayList<Animator>();
            animations.add(anim);
            animations.add(anim2);


            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(animations);
            animatorSet.setDuration(200);
            animatorSet.start();

            animatorSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
        }
    }

    private int getDisplayHeight() {
        return this.getResources().getDisplayMetrics().heightPixels;
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
        if (mQuizValueEventListener != null) {
            mTermDatabaseReference.removeEventListener(mQuizValueEventListener);
            mQuizValueEventListener = null;
        }
        if (mQuizValueEventListenerProgress != null) {
            mTermDatabaseReference.removeEventListener(mQuizValueEventListenerProgress);
            mQuizValueEventListenerProgress = null;
        }
    }

    private void onSignedInInitialized(String displayName) {
        mUsername = displayName;
        System.out.println("MY USER NAME IS " + mUsername);
        if (mQuiz == null) {
//            attachDatabaseQuizReadListener();
            attachDatabaseQuizReadListenerProgress();
        }
    }

    private void attachDatabaseQuizReadListener() {
        System.out.println("----------------------------------------        ISPAUSED   >> " + isPaused);
        if (mQuizValueEventListener == null) {
            mQuizValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    System.out.println(dataSnapshot);
                    FireTerm mTermArray = dataSnapshot.getValue(FireTerm.class);

                    //    System.out.println("------------------------- + " + mTermArray.terms.get(3).url);
//                    System.out.println("------------------------- + "+mTermArray.mTerms.size());

                    ArrayList<Term> mTerms = mTermArray.getTermList();
                    Collections.shuffle(mTerms);
                    saveProgress(mTerms);
                    mQuiz = new Quiz(mTerms);
                    startNewQuestion();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
        }

        mTermDatabaseReference.child(CURRENTCATEGORY).addListenerForSingleValueEvent(mQuizValueEventListener);
    }

    private void attachDatabaseQuizReadListenerProgress() {
        System.out.println("----------------------------------------        ISPAUSED   >> " + isPaused);
        if (mQuizValueEventListenerProgress == null) {
            mQuizValueEventListenerProgress = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    System.out.println(dataSnapshot);
                    if (dataSnapshot.getValue() == null) {
                        System.out.println("nulllllllllllllllllllllll");
                        attachDatabaseQuizReadListener();
                    } else {
                        FireTerm mTermArray = dataSnapshot.getValue(FireTerm.class);
//
//                    //    System.out.println("------------------------- + " + mTermArray.terms.get(3).url);
////                    System.out.println("------------------------- + "+mTermArray.mTerms.size());

                        ArrayList<Term> mTerms = mTermArray.getTermList();
                        counter = mTermArray.getCurrentIndex();
                        score = mTermArray.getScore();
                        updateScoreBoard(score + "");
                        mQuiz = new Quiz(mTerms);
                        startNewQuestion();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
        }
        System.out.println(mUsername);
        mProgressTermDatabaseReference.child(var.progress).child(mUsername).child(CURRENTCATEGORY).addListenerForSingleValueEvent(mQuizValueEventListenerProgress);

    }

    private void saveProgress(ArrayList<Term> mTerms) {
        FireTerm mTermArray = new FireTerm();
        mTermArray.setTermList(mTerms);
        mTermArray.setCurrentIndex(counter);
        mTermArray.setScore(score);
        mProgressTermDatabaseReference.child("PROGRESS").child(mUsername).child(CURRENTCATEGORY).setValue(mTermArray);
    }

    private void saveProgressIndex() {

        mProgressTermDatabaseReference.child(var.progress).child(mUsername).child(CURRENTCATEGORY).child("currentIndex").setValue(counter);
        mProgressTermDatabaseReference.child(var.progress).child(mUsername).child(CURRENTCATEGORY).child("score").setValue(score);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);

        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(uiOptions);

        if (mp == null) {
            mp = MediaPlayer.create(this, R.raw.cute);
            mp.setLooping(true);
            mp.start();
        } else if (mp != null && isPaused) {
            mp.start();
        }

        if (isPaused) {
            isPaused = false;
            resumeQuestion();
            timeOut(score);
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        dettachDatabaseListener();
        if (time != null) {
            time.removeMessages(0);
        }
        stopAnimation(0);
        if (mp.isPlaying()) {
            mp.pause();
            isPaused = true;
        }
        saveProgressIndex();
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
                        applyRetrievedLengthLimit();
                    }
                });

    }

    private void applyRetrievedLengthLimit() {
//    Long friendly_msg_lenght = mFirebaseRemoteConfig.getLong(FRIENDLY_MSG_LENGHT_KEY);
//    messageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(friendly_msg_lenght.intValue())});
//    Log.d(TAG, FRIENDLY_MSG_LENGHT_KEY + " = " + friendly_msg_lenght);
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

    private String generateColor() {
        int newColor = randomGenerator.nextInt(0x1000000);
        return String.format("#%06X", newColor);
    }

    private void shuffleMiddle(int i) {
        middle_middle.setText(mQuiz.myQuizItems.get(i).getTerm());
        top_left.setText(mQuiz.myQuizItems.get(i).getWrongAnswer1());
        top_right.setText(mQuiz.myQuizItems.get(i).getWrongAnswer2());
        bottom_right.setText(mQuiz.myQuizItems.get(i).getWrongAnswer3());
        bottom_left.setText(mQuiz.myQuizItems.get(i).getWrongAnswer4());
    }

    private void shuffleTopRight(int i) {
        middle_middle.setText(mQuiz.myQuizItems.get(i).getWrongAnswer2());
        top_left.setText(mQuiz.myQuizItems.get(i).getWrongAnswer1());
        top_right.setText(mQuiz.myQuizItems.get(i).getTerm());
        bottom_right.setText(mQuiz.myQuizItems.get(i).getWrongAnswer3());
        bottom_left.setText(mQuiz.myQuizItems.get(i).getWrongAnswer4());
    }

    private void shuffleTopLeft(int i) {
        middle_middle.setText(mQuiz.myQuizItems.get(i).getWrongAnswer1());
        top_left.setText(mQuiz.myQuizItems.get(i).getTerm());
        top_right.setText(mQuiz.myQuizItems.get(i).getWrongAnswer2());
        bottom_right.setText(mQuiz.myQuizItems.get(i).getWrongAnswer3());
        bottom_left.setText(mQuiz.myQuizItems.get(i).getWrongAnswer4());
    }

    private void shuffleBottomLeft(int i) {
        middle_middle.setText(mQuiz.myQuizItems.get(i).getWrongAnswer4());
        top_left.setText(mQuiz.myQuizItems.get(i).getWrongAnswer1());
        top_right.setText(mQuiz.myQuizItems.get(i).getWrongAnswer2());
        bottom_right.setText(mQuiz.myQuizItems.get(i).getWrongAnswer3());
        bottom_left.setText(mQuiz.myQuizItems.get(i).getTerm());
    }

    private void shuffleBottomRight(int i) {
        middle_middle.setText(mQuiz.myQuizItems.get(i).getWrongAnswer4());
        top_left.setText(mQuiz.myQuizItems.get(i).getWrongAnswer1());
        top_right.setText(mQuiz.myQuizItems.get(i).getWrongAnswer2());
        bottom_right.setText(mQuiz.myQuizItems.get(i).getTerm());
        bottom_left.setText(mQuiz.myQuizItems.get(i).getWrongAnswer3());
    }

    private void displayNextQuestion() {
        if (mQuiz != null) {
            //scaledownOnce(mImage);
            Glide.with(this)
                    .load(mQuiz.myQuizItems.get(counter).getUrl())
                    .into(mImage);
            //scaleUpOnce(mImage, 3000);
            System.out.println("----------------------------------------     DISPLAY NEXT >> " + counter);
            shuffleIt(counter);
            //    timeOut(score);
            if (isFirstQuestion) {
                scaleDownAllCirclesNowWithContinuation();
                isFirstQuestion = false;
            } else
                scaleUpAllCirclesNowWithContinuation();
            setClickable(true);
        }
    }

    private void startNewQuestion() {
        System.out.println("startNewQuestion");
        isPlaying = true;
        showCircles();
        if (isFirstQuestion) {
            // isFirstQuestion = false;
//            scaleDownallCirclesNow(0);

        }
        displayNextQuestion();

    }

    private void showCircles() {
        mTable.setVisibility(View.VISIBLE);
    }

    private void scaleDownallCirclesNow(int viewID) {
        scaledownOnce(top_left, viewID);
        scaledownOnce(top_right, viewID);
        scaledownOnce(bottom_left, viewID);
        scaledownOnce(bottom_right, viewID);
        scaledownOnce(middle_middle, viewID);
    }

    private void expandCircle(TextView category_text_view) {
        ScaleAnimation transAnim = new ScaleAnimation(0, 1f, 0, 1f, Animation.RELATIVE_TO_SELF, (float) 0.5, Animation.RELATIVE_TO_SELF, (float) 0.5);
//            transAnim.setDuration(500);
        transAnim.setDuration(1000);
//            transAnim.setInterpolator(new Z);
        //  transAnim.setFillAfter(false);
        category_text_view.startAnimation(transAnim);
    }

    private void scaleUpAllCirclesNowWithContinuation() {
        scaleup(top_left, TopLeft);
        scaleup(top_right, TopRight);
        scaleup(bottom_right, BottomRight);
        scaleup(bottom_left, BottomLeft);
        scaleup(middle_middle, MiddleMiddle);
    }

    private void scaleDownAllCirclesNowWithContinuation() {
        scaledown(top_left, TopLeft);
        scaledown(top_right, TopRight);
        scaledown(bottom_right, BottomRight);
        scaledown(bottom_left, BottomLeft);
        scaledown(middle_middle, MiddleMiddle);

    }

    private void shakeAll(int shake) {
        final Animation animShake = AnimationUtils.loadAnimation(GameActivity.this, shake);
        top_left.startAnimation(animShake);
        top_right.startAnimation(animShake);
        bottom_left.startAnimation(animShake);
        bottom_right.startAnimation(animShake);
        middle_middle.startAnimation(animShake);
    }

    private void clearShakeAll(int shake) {
        // final Animation animShake = AnimationUtils.loadAnimation(GameActivity.this, shake);
        top_left.clearAnimation();
        top_right.clearAnimation();
        bottom_left.clearAnimation();
        bottom_right.clearAnimation();
        middle_middle.clearAnimation();
    }

    private void transitionBetweenQuestions() {
        explosionField.explode(middle_middle);
        explosionField.explode(top_left);
        explosionField.explode(top_right);
        explosionField.explode(bottom_left);
        explosionField.explode(bottom_right);
    }

    private void shuffleIt(int counter) {
//        setInvisible();
        Random random = new Random();
        int i = random.nextInt(5);
        //  System.out.println("ramdom "+i);
        switch (i) {
            case 0:
                shuffleTopLeft(counter);
                break;
            case 1:
                shuffleTopRight(counter);
                break;
            case 2:
                shuffleBottomLeft(counter);
                break;
            case 3:
                shuffleBottomRight(counter);
                break;
            case 4:
                shuffleMiddle(counter);
                break;
        }
//        System.out.println("----------------------------------------           SHUFLE >> " + counter);
//        setVisible();
    }

    public void handlerStep() {
        //System.out.println("HANDLER STEP LOOP");
        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (scaledowncounter == 5) {
                // System.out.println("HANDLER = 5");
                System.out.println("----------------------------------------   ANIMATION DONE >> " + (counter - 1));
                startNewQuestion();
                scaledowncounter = 0;
            } else {
                handlerStep();
                //System.out.println("HANDLER = 0");
            }
        }, 100);
    }

    private void timeOut(long score) {
        //  System.out.println("------------------------------------------>> TIME OUT START");
        time = new Handler();
        time.postDelayed(() -> {

            if (GameActivity.this.score == score && !isPaused) {
                playSoundWin(soundLaughter);
                if (GameActivity.this.score >= 5)
                    GameActivity.this.score -= 5;
                updateScoreBoard(GameActivity.this.score + "");
                goToNextQuestion();
                mergeAllTextColorTransparent();
                System.out.println("--------------------------------------- TIME OUT COMPLETE >> " + timeCounter);
                timeCounter += 1;
            }

        }, timeOutScore);

    }

    private void emit(View v) {
        new ParticleSystem(GameActivity.this, 50, R.drawable.star_pink, 1000, R.id.backgroundmain)
                .setSpeedRange(0.1f, 0.25f)
                .emit(v, 100);
    }

    private void mergeTextColor(TextView v) {
        int color = Color.TRANSPARENT;
        Drawable bg = v.getBackground();
        if (bg instanceof ColorDrawable) {
            color = ((ColorDrawable) bg).getColor();
        }
        v.setTextColor(color);
    }

    private void mergeAllTextColor() {
        mergeTextColor(middle_middle);
        mergeTextColor(top_left);
        mergeTextColor(top_right);
        mergeTextColor(bottom_left);
        mergeTextColor(bottom_right);
    }

    private void mergeAllTextColorTransparent() {
        mergeTextColorTransparent(middle_middle);
        mergeTextColorTransparent(top_left);
        mergeTextColorTransparent(top_right);
        mergeTextColorTransparent(bottom_left);
        mergeTextColorTransparent(bottom_right);
    }

    private void mergeAllTextColorExcept(TextView v) {
        mergeAllTextColorTransparent();
        contrastTextColor(v);
    }

    private void contrastTextColor(TextView v) {
        Drawable drawable = getDrawable(R.drawable.my_message);
        // prepare
//        int strokeWidth = 5; // 5px not dp
//        int roundRadius = 15; // 15px not dp
//        int strokeColor = Color.parseColor("#2E3135");
//        int fillColor = Color.parseColor("#DFDFE0");
        int fillColor = Color.parseColor(generateColor());

        GradientDrawable gd = (GradientDrawable) drawable;
        gd.setColor(fillColor);
//        gd.setCornerRadius(roundRadius);
//        gd.setStroke(strokeWidth, strokeColor);
//        int contrastingColor = ;
        v.setBackground(gd);
        v.setTextColor(Colour.blackOrWhiteContrastingColor(fillColor));
    }

    private void mergeTextColorTransparent(TextView v) {
        Drawable drawable = getDrawable(R.drawable.my_message);
        int fillColor = Color.parseColor("#00FFFFFF");
        GradientDrawable gd = (GradientDrawable) drawable;
        assert gd != null;
        gd.setColor(fillColor);
/*
gd.setCornerRadius(roundRadius);
gd.setStroke(strokeWidth, strokeColor);
int contrastingColor = ;
*/
        v.setBackground(gd);
        v.setTextColor(fillColor);
    }

    private void stopAnimation(int viewID) {
        isPlaying = false;
        clearShakeAll(0);
        for (int i = 0; i < animatorSetUp.size(); i++) {
            animatorSetUp.get(i).cancel();
            animatorSetUp.get(i).removeAllListeners();
            animatorSetDown.get(i).cancel();
            animatorSetDown.get(i).removeAllListeners();
        }

        scaleDownallCirclesNow(viewID);
    }

    private void goToNextQuestion() {

        attemptCounter = 0;
        if (counter == mQuiz.myQuizItems.size()) {
            //TODO END THE GAME
        } else {
            counter += 1;
            answer.setText(score + " -- " + mQuiz.getMyTerms().get(counter).term);
//            transitionBetweenQuestions();
//            explosionField.explode(mTable);
            handlerStep();
            System.out.println("---------------------------------------  START QUESTION   >> " + counter);
        }
    }

    private void resumeQuestion() {
//        attemptCounter = 0;
        answer.setText(score + " -- " + mQuiz.getMyTerms().get(counter).term);
//            transitionBetweenQuestions();
//            explosionField.explode(mTable);
        handlerStep();
        System.out.println("---------------------------------------  Resume QUESTION   >> " + counter);
    }


    private void updateScoreBoard(String score) {
        scoreBox.setText(score);
        scaleupScore(scoreBox);
    }

    private void setClickable(boolean clickable) {
        if (clickable) {
            middle_middle.setOnClickListener(checkAnswerAndContinue);
            top_left.setOnClickListener(checkAnswerAndContinue);
            top_right.setOnClickListener(checkAnswerAndContinue);
            bottom_left.setOnClickListener(checkAnswerAndContinue);
            bottom_right.setOnClickListener(checkAnswerAndContinue);
        } else {
            middle_middle.setOnClickListener(null);
            top_left.setOnClickListener(null);
            top_right.setOnClickListener(null);
            bottom_left.setOnClickListener(null);
            bottom_right.setOnClickListener(null);
        }
    }

    @Override
    protected void onDestroy() {
        counter = 0;
        super.onDestroy();
    }


}
