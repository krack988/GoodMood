package com.mood.lucky.goodmood.activity;

import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mood.lucky.goodmood.App;
import com.mood.lucky.goodmood.R;
import com.mood.lucky.goodmood.model.BashModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{

    private Button btnMyMood;
    private TextView textMood;
    private TextToSpeech textToSpeech;
    private List <Animation> animationList;
    private List <String> moodList;
    private List <BashModel> randomBash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textToSpeech = new TextToSpeech(this,this);
        randomBash = new ArrayList<>();

        addAnimationList();
        addMoodList();

        textMood = (TextView) findViewById(R.id.textMood);
        textMood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                App.getBashApi().getData("bash",1).enqueue(new Callback<List<BashModel>>() {
                    @Override
                    public void onResponse(Call<List<BashModel>> call, Response<List<BashModel>> response) {
//                        randomBash.addAll(response.body());
//                        Log.i("test_tag" , "response " + randomBash.toString());
                    }

                    @Override
                    public void onFailure(Call<List<BashModel>> call, Throwable t) {
                        Log.i("test_tag" , "Network error");
                    }
                });
            }
        });
        btnMyMood = (Button) findViewById(R.id.btnChangeMood);
        btnMyMood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int randomAnim = (int) (Math.random()* animationList.size());
                int randomMood = (int) (Math.random()* moodList.size());

                textMood.setText(moodList.get(randomMood));
//                textToSpeech.speak(textMood.getText().toString(),TextToSpeech.QUEUE_FLUSH,null);
                textMood.startAnimation(animationList.get(randomAnim));
                Log.i("ttt" , "random: " + randomAnim);
            }
        });
    }

    public void addAnimationList(){
        animationList = new ArrayList<>();
        animationList.add(AnimationUtils.loadAnimation(MainActivity.this,R.anim.mycombo));
        animationList.add(AnimationUtils.loadAnimation(MainActivity.this,R.anim.myalpha));
        animationList.add(AnimationUtils.loadAnimation(MainActivity.this,R.anim.myrotate));
        animationList.add(AnimationUtils.loadAnimation(MainActivity.this,R.anim.myscale));
        animationList.add(AnimationUtils.loadAnimation(MainActivity.this,R.anim.mytrans));
    }

    public void addMoodList(){
        moodList = new ArrayList<>();
        moodList.add("Хреново");
        moodList.add("Ужасно");
        moodList.add("Погано");
        moodList.add("Грустно");
        moodList.add("Отстойно");
        moodList.add("Херово");
        moodList.add("Плохо");
        moodList.add("Не хорошо");
        moodList.add("Плохонько");
        moodList.add("Дерьмово");
        moodList.add("Скорбно");
        moodList.add("Серо");
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
    }
}
