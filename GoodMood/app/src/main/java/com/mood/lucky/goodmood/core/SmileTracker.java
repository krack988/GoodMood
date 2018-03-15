package com.mood.lucky.goodmood.core;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;
import com.mood.lucky.goodmood.activity.TestActivity;
import com.mood.lucky.goodmood.activity.cameraui.GraphicOverlay;

import java.util.HashMap;
import java.util.Map;

import static com.mood.lucky.goodmood.utils.Const.TEST_TAG;

/**
 * Created by lucky on 15.11.2017.
 */

public class SmileTracker extends Tracker<Face> {
    private static final float EYE_CLOSED_THRESHOLD = 0.4f;

    private GraphicOverlay mOverlay;
    private GooglyEyesGraphic mEyesGraphic;

    private Map<Integer, PointF> mPreviousProportions = new HashMap<>();

    private boolean mPreviousIsLeftOpen = true;
    private boolean mPreviousIsRightOpen = true;

    private double moodLevel;
    private float leftEye;
    private float rightEye;

    public SmileTracker(){}
    public SmileTracker(GraphicOverlay overlay){
        mOverlay = overlay;
    }

    @Override
    public void onNewItem(int id, Face face) {
        mEyesGraphic = new GooglyEyesGraphic(mOverlay);
    }

    @Override
    public void onUpdate(Detector.Detections<Face> detections, Face face) {
        super.onUpdate(detections, face);
        //show eyes on activity
        mOverlay.add(mEyesGraphic);

        updatePreviousProportions(face);

                moodLevel = face.getIsSmilingProbability();
                leftEye = face.getIsLeftEyeOpenProbability();
                rightEye = face.getIsRightEyeOpenProbability();
//                Log.i(TEST_TAG, "moodlevel from tracker: " + moodLevel);
//                Log.i(TEST_TAG, "left eye: " + leftEye);
//                Log.i(TEST_TAG, "right eye: " + rightEye);

                if (face.getIsSmilingProbability() > 0.4) {
//            Log.i(TEST_TAG, "you are smile");
                } else {
//            Log.i(TEST_TAG,"you are sad");
                }

//        Log.i(TEST_TAG , "smile detect: " + face.getIsSmilingProbability());

        PointF leftPosition = getLandmarkPosition(face, Landmark.LEFT_EYE);
        PointF rightPosition = getLandmarkPosition(face, Landmark.RIGHT_EYE);

        float leftOpenScore = face.getIsLeftEyeOpenProbability();
        boolean isLeftOpen;
        if (leftOpenScore == Face.UNCOMPUTED_PROBABILITY) {
            isLeftOpen = mPreviousIsLeftOpen;
        } else {
            isLeftOpen = (leftOpenScore > EYE_CLOSED_THRESHOLD);
            mPreviousIsLeftOpen = isLeftOpen;
        }

        float rightOpenScore = face.getIsRightEyeOpenProbability();
        boolean isRightOpen;
        if (rightOpenScore == Face.UNCOMPUTED_PROBABILITY) {
            isRightOpen = mPreviousIsRightOpen;
        } else {
            isRightOpen = (rightOpenScore > EYE_CLOSED_THRESHOLD);
            mPreviousIsRightOpen = isRightOpen;
        }

        mEyesGraphic.updateEyes(leftPosition, isLeftOpen, rightPosition, isRightOpen);
    }

    /**
     * Hide the graphic when the corresponding face was not detected.  This can happen for
     * intermediate frames temporarily (e.g., if the face was momentarily blocked from
     * view).
     */
    @Override
    public void onMissing(FaceDetector.Detections<Face> detectionResults) {
        mOverlay.remove(mEyesGraphic);
    }

    /**
     * Called when the face is assumed to be gone for good. Remove the googly eyes graphic from
     * the overlay.
     */
    @Override
    public void onDone() {
        mOverlay.remove(mEyesGraphic);
    }

    //==============================================================================================
    // Private
    //==============================================================================================

    private void updatePreviousProportions(Face face) {
        for (Landmark landmark : face.getLandmarks()) {
            PointF position = landmark.getPosition();
            float xProp = (position.x - face.getPosition().x) / face.getWidth();
            float yProp = (position.y - face.getPosition().y) / face.getHeight();
            mPreviousProportions.put(landmark.getType(), new PointF(xProp, yProp));
        }
    }

    /**
     * Finds a specific landmark position, or approximates the position based on past observations
     * if it is not present.
     */
    private PointF getLandmarkPosition(Face face, int landmarkId) {
        for (Landmark landmark : face.getLandmarks()) {
            if (landmark.getType() == landmarkId) {
                return landmark.getPosition();
            }
        }

        PointF prop = mPreviousProportions.get(landmarkId);
        if (prop == null) {
            return null;
        }

        float x = face.getPosition().x + (prop.x * face.getWidth());
        float y = face.getPosition().y + (prop.y * face.getHeight());
        return new PointF(x, y);
    }

    public synchronized double getMoodLevel(){
        return moodLevel;
    }

    public synchronized float getLeftEye(){
        return leftEye;
    }

    public synchronized float getRightEye(){
        return rightEye;
    }
}
