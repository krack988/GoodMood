package com.mood.lucky.goodmood.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;
import com.google.android.gms.vision.face.LargestFaceFocusingProcessor;
import com.mood.lucky.goodmood.App;
import com.mood.lucky.goodmood.R;
import com.mood.lucky.goodmood.activity.cameraui.CameraSourcePreview;
import com.mood.lucky.goodmood.activity.cameraui.GraphicOverlay;
import com.mood.lucky.goodmood.core.SmileTracker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.mood.lucky.goodmood.utils.Const.TEST_TAG;

public class TestActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{

    private static final String TAG = "GooglyEyes";
    private static final int RC_HANDLE_GMS = 9001;
    private static final int RC_HANDLE_CAMERA_PERM = 2;
    private CameraSource mCameraSource = null;
    private CameraSourcePreview mPreview;
    private GraphicOverlay mGraphicOverlay;
    private boolean mIsFrontFacing = true;
    private Button moodBtn;
    private List<String> moodListDescriptionSad;
    private List <String> moodListPower;
    private List <String> moodListDescriptionGood;
    private List <Bitmap> goodBitmap;
    private List <Bitmap> sadBitmap;
    private TextToSpeech textToSpeech;
    private Tracker<Face> tracker;
    private double moodLevel;
    private Bitmap myBitmap;
    private Bitmap yraFace;
    private Map<Integer, PointF> mPreviousProportions = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        myBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        yraFace  =BitmapFactory.decodeResource(getResources(), R.drawable.ic_forever_alone);
        App.getInstance().setBitmap(yraFace);
        if (myBitmap != null) Log.i("tag" , "bitmap not null");

        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay) findViewById(R.id.faceOverlay);
        moodBtn = (Button) findViewById(R.id.moodBtn);

        addMoodListDescriptionSad();
        addMoodListDescritionGood();
        addMoodListPower();

        textToSpeech = new TextToSpeech(this,this);

        final Button button = (Button) findViewById(R.id.flipButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIsFrontFacing = !mIsFrontFacing;

                if (mCameraSource != null) {
                    mCameraSource.release();
                    mCameraSource = null;
                }

                createCameraSource();
                startCameraSource();
            }
        });

        moodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int randomMoodDescriptionSad = (int) (Math.random()* moodListDescriptionSad.size());
                int randomMoodDescriptionGood = (int) (Math.random() * moodListDescriptionGood.size());
                int randomMoodPower = (int) (Math.random()* moodListPower.size());

                if (tracker != null) {
                    moodLevel = ((SmileTracker)tracker).getMoodLevel();
//                    leftEye = ((SmileTracker) tracker).getLeftEye();
//                    rightEye = ((SmileTracker) tracker).getRightEye();
                    Log.i(TEST_TAG, "moodLevel: " + moodLevel);
                }

                if (moodLevel <= 0){
                    textToSpeech.speak("Я не вижу твоё лицо!", TextToSpeech.QUEUE_FLUSH, null);
                }else
                if (moodLevel > 0.2 & moodLevel < 0.4){
                    textToSpeech.speak("Похоже, ты сел на кактус.", TextToSpeech.QUEUE_FLUSH, null);
                }else
                if (moodLevel >= 0.4) {
//                        textToSpeech.speak("Ты улыбаешься как жопа!", TextToSpeech.QUEUE_FLUSH, null);
                    textToSpeech.speak("Твое настроение " + moodListPower.get(randomMoodPower) + " " + moodListDescriptionGood.get(randomMoodDescriptionGood), TextToSpeech.QUEUE_FLUSH, null);
                } else{
//                        textToSpeech.speak("Ты похож на унылое говно!", TextToSpeech.QUEUE_FLUSH, null);
                    textToSpeech.speak("Твое настроение " + moodListPower.get(randomMoodPower) + " " + moodListDescriptionSad.get(randomMoodDescriptionSad), TextToSpeech.QUEUE_FLUSH, null);
                }




            }
        });

        if (savedInstanceState != null) {
            mIsFrontFacing = savedInstanceState.getBoolean("IsFrontFacing");
        }

        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
        } else {
            requestCameraPermission();
        }
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

    public void addGoodBitmap(){
        goodBitmap = new ArrayList<>();
        goodBitmap.add(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        goodBitmap.add(BitmapFactory.decodeResource(getResources(), R.drawable.ic_forever_alone));
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

        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

//        Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
//                Snackbar.LENGTH_INDEFINITE)
//                .setAction(R.string.ok, listener)
//                .show();
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

                    Log.i("test","left eye: " + face.getIsLeftEyeOpenProbability());
                    return new SmileTracker(mGraphicOverlay);
                }
            };
            processor = new MultiProcessor.Builder<>(factory).build();
        }

        detector.setProcessor(processor);

        if (!detector.isOperational()) {

            Log.w(TAG, "Face detector dependencies are not yet available.");

            IntentFilter lowStorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowStorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(this, "error", Toast.LENGTH_LONG).show();
                Log.w(TAG, "error");
            }
        }
        return detector;
    }
    @Override
    protected void onResume() {
        super.onResume();

        startCameraSource();
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        mPreview.stop();
    }

    /**
     * Releases the resources associated with the camera source, the associated detector, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
        }
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean("IsFrontFacing", mIsFrontFacing);
    }

    /**
     * Toggles between front-facing and rear-facing modes.
     */
    private View.OnClickListener mFlipButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            mIsFrontFacing = !mIsFrontFacing;

            if (mCameraSource != null) {
                mCameraSource.release();
                mCameraSource = null;
            }

            createCameraSource();
            startCameraSource();
        }
    };

    @Override
    public void onInit(int i) {
        if (i == TextToSpeech.SUCCESS){
            Locale locale = new Locale("ru");
            int result = textToSpeech.setLanguage(locale);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                Log.i("ttt" , "not supported");
                Toast.makeText(TestActivity.this,"Language not supported", Toast.LENGTH_SHORT).show();
            }
        }else{
            Log.i("ttt","Error");
        }
    }



}
