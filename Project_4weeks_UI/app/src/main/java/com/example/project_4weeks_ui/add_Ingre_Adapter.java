package com.example.project_4weeks_ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class add_Ingre_Adapter extends RecyclerView.Adapter<add_Ingre_Adapter.AddViewHolder> {

    ArrayList<Ingredient> arrayList = new ArrayList<>(); // 재료정보를 담을 ArrayList

    public add_Ingre_Adapter(ArrayList<Ingredient> arrayList) {
        this.arrayList = arrayList;
    } // 재료 recycler view adapter 생성자

    @NonNull
    @Override
    public AddViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_menu_ingredient,parent,false);
        AddViewHolder holder = new AddViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AddViewHolder holder, int position) {
        holder.tv_addingIngreNum.setText(arrayList.get(position).getIngre_num());
        holder.tv_addingIngreName.setText(arrayList.get(position).getIngre_name());
        holder.tv_addingIngreCount.setText(arrayList.get(position).getIngre_count());
        holder.tv_addingIngreUnit.setText(arrayList.get(position).getIngre_unit());
    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
        // arrayList 사이즈가 0이 아니면 그 사이즈를 return, null이면 0을 return
    }

    public class AddViewHolder extends RecyclerView.ViewHolder {
        TextView tv_addingIngreNum;
        TextView tv_addingIngreName;
        TextView tv_addingIngreCount;
        TextView tv_addingIngreUnit;

        public AddViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tv_addingIngreNum = itemView.findViewById(R.id.tv_addingIngreNum);
            this.tv_addingIngreName = itemView.findViewById(R.id.tv_addingIngreName);
            this.tv_addingIngreCount = itemView.findViewById(R.id.tv_addingIngreCount);
            this.tv_addingIngreUnit = itemView.findViewById(R.id.tv_addingIngreUnit);
        }
    }
}
