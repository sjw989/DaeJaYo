package com.example.project_4weeks_ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class SelectMenuActivity extends AppCompatActivity {
    MyApp myapp;
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
    private Button tagBt;
    TextView mountText;
    TextView ingreText;
    TextView recipeText;
    TextView timeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_menu);
        if(TagActivity.ingreBoolean==true){
            ingreText = findViewById(R.id.tv_ingreCount);
            ingreText.setVisibility(View.VISIBLE);
            ingreText.setText(TagActivity.ingreNum+"개");
        }
        if(TagActivity.recipeBoolean==true){
            recipeText = findViewById(R.id.tv_recipeCount);
            recipeText.setVisibility(View.VISIBLE);
            recipeText.setText(TagActivity.recipeNum + "개");
        }
        if(!TagActivity.mount.equals("default")){
            mountText = findViewById(R.id.tv_mount);
            mountText.setVisibility(View.VISIBLE);
            mountText.setText(TagActivity.mount);
        }
        if(!TagActivity.time.equals("default")){
            timeText = findViewById(R.id.tv_time);
            timeText.setVisibility(View.VISIBLE);
            timeText.setText(TagActivity.time);
        }
        myapp = ((MyApp)getApplicationContext());
        tagBt = findViewById(R.id.bt_tag);
        initTagBt();
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
                finish();
            }
        });

        recyclerView = findViewById(R.id.rec_menu);
        recyclerView.setHasFixedSize(true); // recyclerView 성능강화
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>(); // Menu 를 담을 arrayList

        database = FirebaseDatabase.getInstance(); // 파이어베이스 db 연동
        Log.e("mytTag", String.valueOf(myapp.getTagCheck()));
        if (myapp.getTagCheck()==false) {
            databaseReference = database.getReference(MainActivity.selected_category_ENG); // 선택된 category db로 연결
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // 파이어베이스 db에서 데이터를 받아오는 함수
                    arrayList.clear(); // arrayList 초기화
                    curCategory_size = 0;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) { // for문으로 List 추출
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
                    Toast.makeText(getApplicationContext(), "DB를 가져오는데 실패했습니다", Toast.LENGTH_SHORT).show();
                    // db가져오면서 error 발생시 Toast 출력
                }
            });
        }

        else{
            callWithTag();
        }

        adapter = new Menu_Adapter(arrayList,this);
        adapter.notifyDataSetChanged();
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

    private void initTagBt(){
        tagBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),TagActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void callWithTag(){
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1 : snapshot.getChildren()){ // for문으로 List 추출
                    Menu menu = new Menu();
                    Log.d("menuCount",String.valueOf(snapshot1.child("ingre").getChildrenCount()));
                    if(TagActivity.time.equals("default")){ // 시간 = default
                        if(TagActivity.ingreBoolean==true){ // 재료 Tag true
                            if (snapshot1.child("ingre").getChildrenCount() <= Integer.parseInt(TagActivity.ingreNum)){
                                Log.e("MyTag", String.valueOf(Integer.parseInt(TagActivity.ingreNum)));
                                // 재료 수 <= Tag
                                if (TagActivity.recipeBoolean==true){ // 레시피 Tag true
                                    if (snapshot1.child("recipe").getChildrenCount() <= Integer.parseInt(TagActivity.recipeNum)){
                                        // 레시피 단계 수 <= Tag
                                        menu.set_name(snapshot1.child("name").getValue().toString());
                                        menu.set_img_URL(snapshot1.child("img").getValue().toString());
                                            menu.set_info1(snapshot1.child("info").child("info1").getValue().toString());
                                            menu.set_info2(snapshot1.child("info").child("info2").getValue().toString());
                                        menu.set_info3(snapshot1.child("info").child("info3").getValue().toString());
                                        arrayList.add(menu);
                                    }
                                    else{

                                    }
                                }
                                else{ //
                                    menu.set_name(snapshot1.child("name").getValue().toString());
                                    menu.set_img_URL(snapshot1.child("img").getValue().toString());
                                    menu.set_info1(snapshot1.child("info").child("info1").getValue().toString());
                                    menu.set_info2(snapshot1.child("info").child("info2").getValue().toString());
                                    menu.set_info3(snapshot1.child("info").child("info3").getValue().toString());
                                    arrayList.add(menu);
                                }

                            }
                            else{

                            }

                        }
                        else{
                            if (TagActivity.recipeBoolean==true){
                                if (snapshot1.child("recipe").getChildrenCount() <= Integer.parseInt(TagActivity.recipeNum)){
                                    menu.set_name(snapshot1.child("name").getValue().toString());
                                    menu.set_img_URL(snapshot1.child("img").getValue().toString());
                                    menu.set_info1(snapshot1.child("info").child("info1").getValue().toString());
                                    menu.set_info2(snapshot1.child("info").child("info2").getValue().toString());
                                    menu.set_info3(snapshot1.child("info").child("info3").getValue().toString());
                                    arrayList.add(menu);
                                }
                                else{

                                }
                            }
                            else{
                                menu.set_name(snapshot1.child("name").getValue().toString());
                                menu.set_img_URL(snapshot1.child("img").getValue().toString());
                                menu.set_info1(snapshot1.child("info").child("info1").getValue().toString());
                                menu.set_info2(snapshot1.child("info").child("info2").getValue().toString());
                                menu.set_info3(snapshot1.child("info").child("info3").getValue().toString());
                                arrayList.add(menu);
                            }
                        }

                    }
                    else{
                        String strInfoTime = snapshot1.child("info").child("info2").getValue().toString(); // ex) 10분 이내
                        int intInfoTime = 0;
                        int intTagTime = 0;
                        for(int i = 0 ; i< 2 ; i++){
                            if((int)strInfoTime.charAt(i) >= 48 && (int)strInfoTime.charAt(i) <= 57){ // i번째가 숫자인 경우
                                intInfoTime = intInfoTime*10 + Integer.parseInt(String.valueOf(strInfoTime.charAt(i)));
                            }
                            if((int)TagActivity.time.charAt(i) >= 48 && (int)TagActivity.time.charAt(i) <= 57){ // i번째가 숫자인 경우
                                intTagTime = intTagTime*10 + Integer.parseInt(String.valueOf(TagActivity.time.charAt(i)));
                            }
                        }
                        //Log.e("Mytag", Integer.toString(intInfoTime));
                        if(intInfoTime <= intTagTime){
                            if(TagActivity.ingreBoolean==true){
                                if (snapshot1.child("ingre").getChildrenCount() <= Integer.parseInt(TagActivity.ingreNum)){
                                    Log.d("checkTagIngre","true");
                                    if (TagActivity.recipeBoolean==true){
                                        if (snapshot1.child("recipe").getChildrenCount() <= Integer.parseInt(TagActivity.recipeNum)){
                                            Log.d("menuRecipeBoolean","true");
                                            menu.set_name(snapshot1.child("name").getValue().toString());
                                            menu.set_img_URL(snapshot1.child("img").getValue().toString());
                                            menu.set_info1(snapshot1.child("info").child("info1").getValue().toString());
                                            menu.set_info2(snapshot1.child("info").child("info2").getValue().toString());
                                            menu.set_info3(snapshot1.child("info").child("info3").getValue().toString());
                                            arrayList.add(menu);
                                        }
                                        else{

                                        }
                                    }
                                    else{
                                        Log.d("menuRecipeBoolean","false");
                                        menu.set_name(snapshot1.child("name").getValue().toString());
                                        menu.set_img_URL(snapshot1.child("img").getValue().toString());
                                        menu.set_info1(snapshot1.child("info").child("info1").getValue().toString());
                                        menu.set_info2(snapshot1.child("info").child("info2").getValue().toString());
                                        menu.set_info3(snapshot1.child("info").child("info3").getValue().toString());
                                        arrayList.add(menu);
                                    }

                                }
                                else{

                                }

                            }
                            else{
                                if (TagActivity.recipeBoolean==true){
                                    if (snapshot1.child("recipe").getChildrenCount() <= Integer.parseInt(TagActivity.recipeNum) ){
                                        menu.set_name(snapshot1.child("name").getValue().toString());
                                        menu.set_img_URL(snapshot1.child("img").getValue().toString());
                                        menu.set_info1(snapshot1.child("info").child("info1").getValue().toString());
                                        menu.set_info2(snapshot1.child("info").child("info2").getValue().toString());
                                        menu.set_info3(snapshot1.child("info").child("info3").getValue().toString());
                                        arrayList.add(menu);
                                    }
                                    else{

                                    }
                                }
                                else{
                                    menu.set_name(snapshot1.child("name").getValue().toString());
                                    menu.set_img_URL(snapshot1.child("img").getValue().toString());
                                    menu.set_info1(snapshot1.child("info").child("info1").getValue().toString());
                                    menu.set_info2(snapshot1.child("info").child("info2").getValue().toString());
                                    menu.set_info3(snapshot1.child("info").child("info3").getValue().toString());
                                    arrayList.add(menu);
                                }
                            }
                        }
                    }

                    // Menu object를 담음

                    ; // 받아온 menu object를 arrayList에 추가
                }
                adapter.notifyDataSetChanged(); // arrayList 저장 및 갱신
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("menu",error.toString());
            }

        };

        if (TagActivity.mount.equals("default")){
            Query query =database.getReference().child(myapp.getCategory_ENG()).orderByChild("info/info1");
            query.addListenerForSingleValueEvent(postListener);
        }
        else {
            Query query =database.getReference().child(myapp.getCategory_ENG()).orderByChild("info/info1").equalTo(TagActivity.mount);
            query.addListenerForSingleValueEvent(postListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}