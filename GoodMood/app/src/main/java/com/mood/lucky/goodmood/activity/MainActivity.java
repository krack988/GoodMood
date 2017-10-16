package com.mood.lucky.goodmood.activity;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
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

import com.mood.lucky.goodmood.App;
import com.mood.lucky.goodmood.R;
import com.mood.lucky.goodmood.dialog.DialogFragmentSharing;
import com.mood.lucky.goodmood.dialog.DialogSharingActivity;
import com.mood.lucky.goodmood.model.BashModel;
import com.vk.sdk.api.VKError;
import com.vk.sdk.dialogs.VKShareDialogBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mood.lucky.goodmood.utils.Const.TEST_TAG;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{

    private Button btnMyMood;
    private TextView textMood;
    private TextToSpeech textToSpeech;
    private List <Animation> animationList;
    private List <String> moodList;
    private List <String> moodListTwo;
    private List <BashModel> randomBash;
    private Toolbar toolbar;
    private FragmentManager fragmentManager;
    private LayoutInflater inflater;
    private View dialogSharing;
    private ImageButton btnSharingVK;
    private ImageButton btnSharingFB;
    private DialogFragment dialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();

        inflater = getLayoutInflater();
        dialogSharing = inflater.inflate(R.layout.dialog_sharing_change,(LinearLayout) findViewById(R.id.sharingLayout));
        btnSharingFB = (ImageButton) findViewById(R.id.btnFBSharing);
        btnSharingVK = (ImageButton) findViewById(R.id.btnVKSharing);
        dialogFragment = new DialogFragmentSharing();

        addAnimationList();
        addMoodList();
        addMoodListTwo();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(MainActivity.this,"Share",Toast.LENGTH_SHORT).show();
                dialogFragment.show(getSupportFragmentManager(),"dialogSharing");

//                VKShareDialogBuilder builder = new VKShareDialogBuilder();
//                builder.setText(textMood.getText().toString());
//                builder.setShareDialogListener(new VKShareDialogBuilder.VKShareDialogListener() {
//                    @Override
//                    public void onVkShareComplete(int postId) {
//
//                    }
//
//                    @Override
//                    public void onVkShareCancel() {
//
//                    }
//
//                    @Override
//                    public void onVkShareError(VKError error) {
//
//                    }
//                });
//                builder.show(fragmentManager,"VK_SHARE_DIALOG");

                return true;
            }
        });

        textToSpeech = new TextToSpeech(this,this);
        randomBash = new ArrayList<>();

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
                        Log.i(TEST_TAG , "Network error");
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
                int randomMoodTwo = (int) (Math.random()*moodListTwo.size());

                textMood.setText(moodListTwo.get(randomMoodTwo) + " " + moodList.get(randomMood));
//                textToSpeech.speak("Твое настроение " + textMood.getText().toString(),TextToSpeech.QUEUE_FLUSH,null);
                textMood.startAnimation(animationList.get(randomAnim));
                Log.i(TEST_TAG , "random: " + randomAnim);
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

    public void addMoodListTwo(){
        moodListTwo = new ArrayList<>();
        moodListTwo.add("Очень");
        moodListTwo.add("Сильно");
        moodListTwo.add("Ужасно");
        moodListTwo.add("Колосально");
        moodListTwo.add("Крайне");
        moodListTwo.add("Заметно");
        moodListTwo.add("Невероятно");
        moodListTwo.add("Немного");
        moodListTwo.add("Страшно");

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
