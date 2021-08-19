package com.example.project_4weeks_ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SelectMenuActivity extends AppCompatActivity {
    public static String selected_menu; // 선택된 메뉴이름
    public static String selected_menu_num; // 선택된 메뉴 번호
    // MainActivity로 부터 선택된 카테고리 이름 받아오기
    public static int curCategory_size; // 현재 current 카테고리의 원소 개수
    public static Context context_select_menu; // curCategory_size를 넘겨주기 위한 context 선언
    public TextView tv_category; // 카테고리 이름을 출력할 textView
    public FloatingActionButton btn_addmenu; // 메뉴 추가 버튼
    private RecyclerView recyclerView;
    private Menu_Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Menu> arrayList;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_menu);

        tv_category = findViewById(R.id.tv_category);
        tv_category.setText(MainActivity.selected_category_KR);

        // 뒤로가기 버튼
        ImageButton btn_back = (ImageButton)findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        // 메뉴 추가 버튼
        btn_addmenu = findViewById(R.id.btn_addMenu);
        btn_addmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddMenuActivity.class);
                startActivity(intent);
            }
        });

        recyclerView = findViewById(R.id.rec_menu);
        recyclerView.setHasFixedSize(true); // recyclerView 성능강화
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>(); // Menu 를 담을 arrayList

        database = FirebaseDatabase.getInstance(); // 파이어베이스 db 연동
        databaseReference = database.getReference(MainActivity.selected_category_ENG); // 선택된 category db로 연결
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // 파이어베이스 db에서 데이터를 받아오는 함수
                arrayList.clear(); // arrayList 초기화
                curCategory_size = 0;
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){ // for문으로 List 추출
                    curCategory_size++;
                    Menu menu = new Menu();
                    menu.set_name(snapshot.child("name").getValue().toString());
                    menu.set_img_URL(snapshot.child("img").getValue().toString());
                    menu.set_info1(snapshot.child("info").child("info1").getValue().toString());
                    menu.set_info2(snapshot.child("info").child("info2").getValue().toString());
                    menu.set_info3(snapshot.child("info").child("info3").getValue().toString());
                    // Menu object를 담음

                    arrayList.add(menu); // 받아온 menu object를 arrayList에 추가
                }
                adapter.notifyDataSetChanged(); // arrayList 저장 및 갱신
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"DB를 가져오는데 실패했습니다",Toast.LENGTH_SHORT).show();
                // db가져오면서 error 발생시 Toast 출력
            }
        });
        adapter = new Menu_Adapter(arrayList,this);
        recyclerView.setAdapter(adapter); // recyclerView에 adapter 연결

        // 메뉴 선택 이벤트 처리
        adapter.setOnItemClickListener(new Menu_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                selected_menu = arrayList.get(position).get_name();
                selected_menu_num = Integer.toString(position);
                Intent intent = new Intent(getApplicationContext(), RecipeActivity.class);
                startActivity(intent);

            }
        });


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
            return true;
        }
        return true;
    }
}