package com.example.project_4weeks_ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RecipeActivity extends AppCompatActivity {
    private DatabaseReference  mDBReference;
    String ingre = "";
    private ArrayList<RecipeItem> mData = new ArrayList<RecipeItem>() ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receipe);
        ImageView iv_title = findViewById(R.id.iv_image_recipe);
        RecyclerView recyclerView = findViewById(R.id.rc_recipe);
        RecipeRecyclerAdapter adapter = new RecipeRecyclerAdapter();
        TextView tv_name = findViewById(R.id.tv_title_recipe);
        TextView tv_ingre = findViewById(R.id.tv_ingredient_recipe);
        TextView tv_recipe_info1 = findViewById(R.id.tv_recipe_info1);
        TextView tv_recipe_info2 = findViewById(R.id.tv_recipe_info2);
        TextView tv_recipe_info3 = findViewById(R.id.tv_recipe_info3);
        mDBReference = FirebaseDatabase.getInstance().getReference().child(MainActivity.selected_category_ENG).child(SelectMenuActivity.selected_menu_num);
        mDBReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
//                iv_title.setImageDrawable();
                Glide.with(iv_title.getContext()).load(dataSnapshot.child("img").getValue().toString()).into(iv_title); // 메뉴 사진
                tv_name.setText(dataSnapshot.child("name").getValue().toString()); // 메뉴 이름

                // 메뉴 정보
                tv_recipe_info1.setText(dataSnapshot.child("info").child("info1").getValue().toString());
                tv_recipe_info2.setText(dataSnapshot.child("info").child("info2").getValue().toString());
                tv_recipe_info3.setText(dataSnapshot.child("info").child("info3").getValue().toString());


                // 재료 정보
                for (DataSnapshot ingreData : dataSnapshot.child("ingre").getChildren()) {
                    if(ingreData.child("ingre_name").getValue().toString()!= null){
                        ingre += ingreData.child("ingre_name").getValue().toString()+" "+ingreData.child("ingre_count").getValue().toString()
                                +ingreData.child("ingre_unit").getValue().toString()+ "  " + "\n";
                    }
                }
                tv_ingre.setText(ingre);

                // 레시피 정보
                for (DataSnapshot listData : dataSnapshot.child("recipe").getChildren()) {
                    mData.add(new RecipeItem(listData.child("img").getValue().toString(),
                            listData.child("txt").getValue().toString()));
                }
                adapter.setmData(mData);
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Database", "Failed to read value.", error.toException());
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));


        ImageButton btn_back4 = (ImageButton) findViewById(R.id.btn_back4);
        btn_back4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ingre = null;
    }

}