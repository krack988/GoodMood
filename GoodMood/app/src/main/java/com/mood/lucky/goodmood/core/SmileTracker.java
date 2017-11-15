package com.mood.lucky.goodmood.core;

import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;

import static com.mood.lucky.goodmood.utils.Const.TEST_TAG;

/**
 * Created by lucky on 15.11.2017.
 */

public class SmileTracker extends Tracker<Face> {


    @Override
    public void onUpdate(Detector.Detections<Face> detections, Face face) {
        super.onUpdate(detections, face);
        if (face.getIsSmilingProbability() > 0.4){
            Log.i(TEST_TAG , "Yuo are smile!");
        }else{
            Log.i(TEST_TAG , "Yuo are sad(");
        }
    }
}
