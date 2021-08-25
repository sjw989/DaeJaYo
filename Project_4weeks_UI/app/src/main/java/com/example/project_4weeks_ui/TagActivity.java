package com.example.project_4weeks_ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class TagActivity extends AppCompatActivity {
    MyApp myapp;
    RadioGroup radioGroupMount;
    RadioGroup radioGroupTime;
    RadioGroup radioGroupRecipe;
    RadioGroup radioGroupIngre;
    EditText recipe_et;
    EditText ingre_et;
    Button button;
    static String mount= "default";
    static String time = "default";
    static String recipeNum = "default";
    static String ingreNum = "default";
    static Boolean ingreBoolean = false;
    static Boolean recipeBoolean = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myapp = ((MyApp)getApplicationContext());
        setContentView(R.layout.activity_tag);

        radioGroupMount = findViewById(R.id.rbg_tag_mount);
        radioGroupTime = findViewById(R.id.rbg_tag_time);
        radioGroupIngre= findViewById(R.id.rbg_tag_ingre);
        radioGroupRecipe = findViewById(R.id.rbg_tag_recipe);
        recipe_et = findViewById(R.id.et_number_recipe);
        ingre_et = findViewById(R.id.et_number_ingre);

        radioGroupMount.setOnCheckedChangeListener(radioGroupButtonChangeListener);
        radioGroupTime.setOnCheckedChangeListener(radioGroupButtonChangeListener1);
        radioGroupIngre.setOnCheckedChangeListener(radioGroupButtonChangeListener2);
        radioGroupRecipe.setOnCheckedChangeListener(radioGroupButtonChangeListener3);
        button = findViewById(R.id.bt_setTag);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recipeNum = recipe_et.getText().toString();
                ingreNum = ingre_et.getText().toString();
                Intent intent = new Intent(getApplicationContext(),SelectMenuActivity.class);
                myapp.setTagCheck(true);
                startActivity(intent);
                finish();

            }
        });

        ImageButton btn_back5 = (ImageButton)findViewById(R.id.btn_back_5);
        btn_back5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    RadioGroup.OnCheckedChangeListener radioGroupButtonChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
            if (i == R.id.rb_tag_1mount) {
                mount="1인분";
            } else if (i == R.id.rb_tag_2mount) {
                mount="2인분";
            }
            else if ( i==R.id.rb_tag_3mount){
                mount="3인분";
            }
            else if( i==R.id.rb_tag_above3mount){
                mount="default";
            }
        }
    };
    RadioGroup.OnCheckedChangeListener radioGroupButtonChangeListener1 = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
            if (i == R.id.rb_tag_15min) {
                time="15분 이내";
            } else if (i == R.id.rb_tag_30min) {
                time="30분 이내";
            }
            else if ( i == R.id.rb_tag_60min){
                time="60분 이내";
            }
            else if ( i== R.id.rb_tag_defaultMin){
                time ="default";
            }
        }
    };
    RadioGroup.OnCheckedChangeListener radioGroupButtonChangeListener2 = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
            if(i==R.id.rb_tag_defaultIngre){
                ingreBoolean = false;
            }
            else{
                ingreBoolean =true;
            }
        }
    };
    RadioGroup.OnCheckedChangeListener radioGroupButtonChangeListener3 = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
            if(i==R.id.rb_tag_defaultRecipe){
                recipeBoolean =false;
            }
            else{
                recipeBoolean = true;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
