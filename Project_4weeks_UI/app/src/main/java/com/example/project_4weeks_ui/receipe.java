package com.example.project_4weeks_ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.widget.TextView;

public class receipe extends AppCompatActivity {
    TextView textView1;
    TextView textView2;
    TextView textView3;
    TextView textView4;
    TextView textView5;
    TextView textView6;
    TextView textView7;
    TextView textView8;

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
            return true;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipe);
        textView1 = findViewById(R.id.textView1);
        textView1.setMovementMethod(new ScrollingMovementMethod());
        textView2 = findViewById(R.id.textView2);
        textView2.setMovementMethod(new ScrollingMovementMethod());
        textView3 = findViewById(R.id.textView3);
        textView3.setMovementMethod(new ScrollingMovementMethod());
        textView4 = findViewById(R.id.textView4);
        textView4.setMovementMethod(new ScrollingMovementMethod());
        textView5 = findViewById(R.id.textView5);
        textView5.setMovementMethod(new ScrollingMovementMethod());
        textView6 = findViewById(R.id.textView6);
        textView6.setMovementMethod(new ScrollingMovementMethod());
        textView7 = findViewById(R.id.textView7);
        textView7.setMovementMethod(new ScrollingMovementMethod());
        textView8 = findViewById(R.id.textView8);
        textView8.setMovementMethod(new ScrollingMovementMethod());
    }
}