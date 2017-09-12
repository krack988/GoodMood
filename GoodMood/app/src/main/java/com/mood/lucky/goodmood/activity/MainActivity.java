package com.mood.lucky.goodmood.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mood.lucky.goodmood.R;

public class MainActivity extends AppCompatActivity {

    private Button btnMyMood;
    private TextView textMood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textMood = (TextView) findViewById(R.id.textMood);
        btnMyMood = (Button) findViewById(R.id.btnChangeMood);
        btnMyMood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
