package com.example.project_4weeks_ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private Menu_Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Menu> arrayList = MainActivity.search_menu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        TextView textView = findViewById(R.id.tv_searchWord);
        textView.setText(MainActivity.search_word);

        recyclerView = findViewById(R.id.rec_searchView);
        recyclerView.setHasFixedSize(true); // recyclerView 성능강화
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new Menu_Adapter(arrayList,this);
        recyclerView.setAdapter(adapter); // recyclerView에 adapter 연결
        adapter.notifyDataSetChanged(); // arrayList 저장 및 갱신

        adapter.setOnItemClickListener(new Menu_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                SelectMenuActivity.selected_menu = arrayList.get(position).get_name();
                SelectMenuActivity.selected_menu_num = Integer.toString(position);
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