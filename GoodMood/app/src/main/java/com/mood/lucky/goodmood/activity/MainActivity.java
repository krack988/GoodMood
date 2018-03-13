package com.mood.lucky.goodmood.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.LargestFaceFocusingProcessor;
import com.mood.lucky.goodmood.R;
import com.mood.lucky.goodmood.activity.cameraui.CameraSourcePreview;
import com.mood.lucky.goodmood.activity.cameraui.GraphicOverlay;
import com.mood.lucky.goodmood.core.SmileTracker;
import com.mood.lucky.goodmood.dialog.DialogFragmentSharing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.mood.lucky.goodmood.utils.Const.TEST_TAG;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{

    private Button btnMyMood;
    private TextView textMood;
    private TextToSpeech textToSpeech;
    private List <Animation> animationList;
    private List <String> moodListDescriptionSad;
    private List <String> moodListPower;
    private List <String> moodListDescriptionGood;
    private Toolbar toolbar;
    private FragmentManager fragmentManager;
    private LayoutInflater inflater;
    private View dialogSharing;
    private ImageButton btnSharingVK;
    private ImageButton btnSharingFB;
    private DialogFragment dialogFragment;
    private boolean mIsFrontFacing = true;
    private CameraSource mCameraSource = null;
    private CameraSourcePreview mPreview;
    private GraphicOverlay mGraphicOverlay;
    private double moodLevel;
    private Tracker<Face> tracker;
    private float leftEye;
    private float rightEye;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
//camera
        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay) findViewById(R.id.faceOverlay);

        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
        } else {
            Toast.makeText(this,"Haven't permission for camera", Toast.LENGTH_SHORT).show();
        }

//end camera

        inflater = getLayoutInflater();
        dialogSharing = inflater.inflate(R.layout.dialog_sharing_change,(LinearLayout) findViewById(R.id.sharingLayout));
        btnSharingFB = (ImageButton) findViewById(R.id.btnFBSharing);
        btnSharingVK = (ImageButton) findViewById(R.id.btnVKSharing);
        dialogFragment = new DialogFragmentSharing();

        addAnimationList();
        addMoodListDescriptionSad();
        addMoodListDescritionGood();
        addMoodListPower();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(MainActivity.this,"Share",Toast.LENGTH_SHORT).show();
//                dialogFragment.show(getSupportFragmentManager(),"dialogSharing");


                startActivity(new Intent(MainActivity.this,TestActivity.class));

                return true;
            }
        });

        textToSpeech = new TextToSpeech(this,this);

        textMood = (TextView) findViewById(R.id.textMood);
        textMood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        btnMyMood = (Button) findViewById(R.id.btnChangeMood);
        btnMyMood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int randomAnim = (int) (Math.random()* animationList.size());
                int randomMoodDescriptionSad = (int) (Math.random()* moodListDescriptionSad.size());
                int randomMoodDescriptionGood = (int) (Math.random() * moodListDescriptionGood.size());
                int randomMoodPower = (int) (Math.random()* moodListPower.size());

//                textMood.setText(moodListPower.get(randomMoodPower) + " " + moodListDescriptionSad.get(randomMoodDescriptionSad));
//                textToSpeech.speak("Твое настроение " + textMood.getText().toString(),TextToSpeech.QUEUE_FLUSH,null);
                textMood.startAnimation(animationList.get(randomAnim));

                if (tracker != null) {
                    moodLevel = ((SmileTracker)tracker).getMoodLevel();
                    leftEye = ((SmileTracker) tracker).getLeftEye();
                    rightEye = ((SmileTracker) tracker).getRightEye();
                    Log.i(TEST_TAG, "moodLevel: " + moodLevel);
                }

                if (moodLevel <= 0){
                    textToSpeech.speak("Я не вижу твоё лицо!", TextToSpeech.QUEUE_FLUSH, null);
                }else
                    if (moodLevel > 0.2 & moodLevel < 0.4){
                        textToSpeech.speak("Похоже, ты сел на кактус.", TextToSpeech.QUEUE_FLUSH, null);
                    }else
                    if (moodLevel >= 0.4) {
                        textMood.setText(moodListPower.get(randomMoodPower) + " " + moodListDescriptionGood.get(randomMoodDescriptionGood));
//                        textToSpeech.speak("Ты улыбаешься как жопа!", TextToSpeech.QUEUE_FLUSH, null);
                    } else{
//                        textToSpeech.speak("Ты похож на унылое говно!", TextToSpeech.QUEUE_FLUSH, null);
                        textMood.setText(moodListPower.get(randomMoodPower) + " " + moodListDescriptionSad.get(randomMoodDescriptionSad));
                    }
                textToSpeech.speak("Твое настроение " + textMood.getText().toString(),TextToSpeech.QUEUE_FLUSH,null);

            }
        });


        //detect smile
//        FaceDetector detector = new FaceDetector.Builder(getApplicationContext())
//                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
//                .setProminentFaceOnly(true)
//                .setTrackingEnabled(true)
//                .build();
    }

    public void addAnimationList(){
        animationList = new ArrayList<>();
        animationList.add(AnimationUtils.loadAnimation(MainActivity.this,R.anim.mycombo));
        animationList.add(AnimationUtils.loadAnimation(MainActivity.this,R.anim.myalpha));
        animationList.add(AnimationUtils.loadAnimation(MainActivity.this,R.anim.myrotate));
        animationList.add(AnimationUtils.loadAnimation(MainActivity.this,R.anim.myscale));
        animationList.add(AnimationUtils.loadAnimation(MainActivity.this,R.anim.mytrans));
    }

    public void addMoodListDescriptionSad(){
        moodListDescriptionSad = new ArrayList<>();
        moodListDescriptionSad.add("Хреново");
        moodListDescriptionSad.add("Ужасно");
        moodListDescriptionSad.add("Погано");
        moodListDescriptionSad.add("Грустно");
        moodListDescriptionSad.add("Отстойно");
        moodListDescriptionSad.add("Херово");
        moodListDescriptionSad.add("Плохо");
        moodListDescriptionSad.add("Не хорошо");
        moodListDescriptionSad.add("Плохонько");
        moodListDescriptionSad.add("Дерьмово");
        moodListDescriptionSad.add("Скорбно");
        moodListDescriptionSad.add("Серо");
    }

    public void addMoodListPower(){
        moodListPower = new ArrayList<>();
        moodListPower.add("Очень");
        moodListPower.add("Сильно");
        moodListPower.add("Ужасно");
        moodListPower.add("Колосально");
        moodListPower.add("Крайне");
        moodListPower.add("Заметно");
        moodListPower.add("Невероятно");
        moodListPower.add("Немного");
        moodListPower.add("Страшно");

    }

    public void addMoodListDescritionGood(){
        moodListDescriptionGood = new ArrayList<>();
        moodListDescriptionGood.add("Замечательно");
        moodListDescriptionGood.add("Отлично");
        moodListDescriptionGood.add("Классно");
        moodListDescriptionGood.add("Превосходно");
        moodListDescriptionGood.add("Улыбчиво");
        moodListDescriptionGood.add("Распрекрасно");

    }

    @Override
    public void onInit(int i) {
        if (i == TextToSpeech.SUCCESS){
            Locale locale = new Locale("ru");
            int result = textToSpeech.setLanguage(locale);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                Log.i("ttt" , "not supported");
                Toast.makeText(MainActivity.this,"Language not supported", Toast.LENGTH_SHORT).show();
            }
        }else{
            Log.i("ttt","Error");
        }
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPreview.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();
    }

    @NonNull
    private FaceDetector createFaceDetector(Context context) {

        FaceDetector detector = new FaceDetector.Builder(context)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setTrackingEnabled(true)
                .setMode(FaceDetector.FAST_MODE)
                .setProminentFaceOnly(mIsFrontFacing)
                .setMinFaceSize(mIsFrontFacing ? 0.35f : 0.15f)
                .build();

        Detector.Processor<Face> processor;
        if (mIsFrontFacing) {

            tracker = new SmileTracker(mGraphicOverlay);
            processor = new LargestFaceFocusingProcessor.Builder(detector, tracker).build();
        } else {

            MultiProcessor.Factory<Face> factory = new MultiProcessor.Factory<Face>() {
                @Override
                public Tracker<Face> create(Face face) {

                    Log.i(TEST_TAG,"left eye: " + face.getIsLeftEyeOpenProbability());
                    return new SmileTracker(mGraphicOverlay);
                }
            };
            processor = new MultiProcessor.Builder<>(factory).build();
        }

        detector.setProcessor(processor);

        if (!detector.isOperational()) {

            Log.w(TEST_TAG, "Face detector dependencies are not yet available.");

            IntentFilter lowStorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowStorageFilter) != null;

        }

        return detector;
    }

    private void createCameraSource() {
        Context context = getApplicationContext();
        FaceDetector detector = createFaceDetector(context);

        int facing = CameraSource.CAMERA_FACING_FRONT;
        if (!mIsFrontFacing) {
            facing = CameraSource.CAMERA_FACING_BACK;
        }

        mCameraSource = new CameraSource.Builder(context, detector)
                .setFacing(facing)
                .setRequestedPreviewSize(320, 240)
                .setRequestedFps(60.0f)
                .setAutoFocusEnabled(true)
                .build();
    }

    private void startCameraSource() {
        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
//            Dialog dlg =
//                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
//            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TEST_TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }
}
