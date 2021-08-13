package com.example.project_4weeks_ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonArray;
import com.google.gson.annotations.SerializedName;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

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
        mDBReference = FirebaseDatabase.getInstance().getReference().child(MainActivity.selected_category_ENG).child(select_menu.selected_menu_num);
        mDBReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
//                iv_title.setImageDrawable();
                Glide.with(iv_title.getContext()).load(dataSnapshot.child("img").getValue().toString()).into(iv_title);
                tv_name.setText(dataSnapshot.child("name").getValue().toString());;
                for (DataSnapshot ingreData : dataSnapshot.child("ingre").getChildren()) {
                    if(ingreData.child("ingre_name").getValue().toString()!= null){
                        ingre += ingreData.child("ingre_name").getValue().toString()+" "+ingreData.child("ingre_count").getValue().toString()
                                +ingreData.child("ingre_unit").getValue().toString()+ "  ";
                    }
                }
                for (DataSnapshot listData : dataSnapshot.child("recipe").getChildren()) {
                    mData.add(new RecipeItem(listData.child("img").getValue().toString(),
                            listData.child("txt").getValue().toString()));
                }
                tv_ingre.setText("재료 : "+ ingre);
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

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ingre = null;
    }

}