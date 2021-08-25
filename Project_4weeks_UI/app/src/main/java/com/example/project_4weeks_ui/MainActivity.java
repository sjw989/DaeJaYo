package com.example.project_4weeks_ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    MyApp myapp;
    public static ArrayList<Menu> search_menu = new ArrayList<>(); // 키워드로 검색된 메뉴들
    public static String search_word; // 검색어
    public static String selected_category_KR; // 선택된카테고리 한글
    public static String selected_category_ENG; // 선택된카테고리 영어
    // 한글과 영어 구분한 이유는 db에서 한글로 받아올 수 없어서 영어 이름도 넘겨줘야함

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myapp = ((MyApp)getApplicationContext());

        // 검색기능
        EditText etv_searching_word = (EditText) findViewById(R.id.etv_enter_searchingWord);
        etv_searching_word.getText().clear();
        ImageButton btn_search = (ImageButton) findViewById(R.id.btn_search); // 검색버튼
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_word = etv_searching_word.getText().toString(); // 검색한 단어
                if(search_word.isEmpty()){
                    Toast.makeText(getApplicationContext(), "검색어를 입력해주세요 !!" , Toast.LENGTH_SHORT).show();
                }
                else {
                    database = FirebaseDatabase.getInstance();
                    databaseReference = database.getReference();
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            search_menu.clear();
                            for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                                for (DataSnapshot menuSnapshot : categorySnapshot.getChildren()) {
                                    String name = menuSnapshot.child("name").getValue().toString();
                                    if (name.contains(search_word)) {
                                        Menu menu = new Menu();
                                        menu.set_name(menuSnapshot.child("name").getValue().toString());
                                        menu.set_img_URL(menuSnapshot.child("img").getValue().toString());
                                        menu.set_info1(menuSnapshot.child("info").child("info1").getValue().toString());
                                        menu.set_info2(menuSnapshot.child("info").child("info2").getValue().toString());
                                        menu.set_info3(menuSnapshot.child("info").child("info3").getValue().toString());
                                        search_menu.add(menu);
                                    }
                                }
                            }
                            Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                            startActivity(intent);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getApplicationContext(), "검색 실패!!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        // 카테고리 선택 버튼
        ImageButton button_noodle = findViewById(R.id.ib_noodle);
        ImageButton button_bowl = findViewById(R.id.ib_bowl);
        ImageButton button_maindish = findViewById(R.id.ib_maindish);
        ImageButton button_rice = findViewById(R.id.ib_rice);
        ImageButton button_bread = findViewById(R.id.ib_bread);
        ImageButton button_alcohol = findViewById(R.id.ib_alcohol);
        TextView tv_noodle = findViewById(R.id.tv_noodle);
        TextView tv_bowl = findViewById(R.id.tv_bowl);
        TextView tv_maindish = findViewById(R.id.tv_maindish);
        TextView tv_rice = findViewById(R.id.tv_rice);
        TextView tv_bread = findViewById(R.id.tv_bread);
        TextView tv_alcohol = findViewById(R.id.tv_alcohol);

        button_noodle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myapp.setCategory_KR((String) tv_noodle.getText());;
                myapp.setCategory_ENG("noodle");
                selected_category_KR = (String) tv_noodle.getText();
                selected_category_ENG = "noodle";
                Intent intent = new Intent(getApplicationContext(), SelectMenuActivity.class);
                startActivity(intent);
            }
        });
        button_bowl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myapp.setCategory_KR((String) tv_bowl.getText());;
                myapp.setCategory_ENG("bowl");
                selected_category_KR = (String) tv_bowl.getText();
                selected_category_ENG = "bowl";
                Intent intent = new Intent(getApplicationContext(), SelectMenuActivity.class);
                startActivity(intent);
            }
        });
        button_maindish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myapp.setCategory_KR((String) tv_maindish.getText());;
                myapp.setCategory_ENG("maindish");
                selected_category_KR = (String) tv_maindish.getText();
                selected_category_ENG = "maindish";
                Intent intent = new Intent(getApplicationContext(), SelectMenuActivity.class);
                startActivity(intent);
            }
        });
        button_rice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myapp.setCategory_KR((String) tv_rice.getText());;
                myapp.setCategory_ENG("rice");
                selected_category_KR = (String) tv_rice.getText();
                selected_category_ENG = "rice";
                Intent intent = new Intent(getApplicationContext(), SelectMenuActivity.class);
                startActivity(intent);
            }
        });
        button_bread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myapp.setCategory_KR((String) tv_bread.getText());;
                myapp.setCategory_ENG("bread");
                selected_category_KR = (String) tv_bread.getText();
                selected_category_ENG = "bread";
                Intent intent = new Intent(getApplicationContext(), SelectMenuActivity.class);
                startActivity(intent);
            }
        });
        button_alcohol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myapp.setCategory_KR((String) tv_alcohol.getText());;
                myapp.setCategory_ENG("alcohol");
                selected_category_KR = (String) tv_alcohol.getText();
                selected_category_ENG = "alcohol";
                Intent intent = new Intent(getApplicationContext(), SelectMenuActivity.class);
                startActivity(intent);
            }
        });
    }

}
