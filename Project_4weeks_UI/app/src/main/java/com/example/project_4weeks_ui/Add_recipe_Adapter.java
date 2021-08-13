package com.example.project_4weeks_ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class Add_recipe_Adapter extends RecyclerView.Adapter<Add_recipe_Adapter.AddViewHolder> {

    ArrayList<add_recipe> arrayList = new ArrayList<>(); // 레시피 정보를 담을 arrayList

    public Add_recipe_Adapter(ArrayList<add_recipe> arrayList) {
        this.arrayList = arrayList;
    } // 레시피 recycler view adapter 생성자

    @NonNull
    @Override
    public Add_recipe_Adapter.AddViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_menu_recipe,parent,false);
        AddViewHolder holder = new AddViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AddViewHolder holder, int position) {
        Glide.with(holder.itemView)
                .load(arrayList.get(position).getImg())
                .into(holder.iv_addRecipeImg);
        holder.tv_addRecipeNum.setText(arrayList.get(position).getNum());
        holder.tv_addRecipeMessage.setText(arrayList.get(position).getTxt());
    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
        // arrayList 사이즈가 0이 아니면 그 사이즈를 return, null이면 0을 return
    }

    public class AddViewHolder extends RecyclerView.ViewHolder {
        TextView tv_addRecipeNum;
        TextView tv_addRecipeMessage;
        ImageView iv_addRecipeImg;
        public AddViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tv_addRecipeNum = itemView.findViewById(R.id.tv_addRecipeNum);
            this.tv_addRecipeMessage = itemView.findViewById(R.id.tv_addRecipeMessage);
            this.iv_addRecipeImg = itemView.findViewById(R.id.iv_addRecipeImg);
        }
    }
}
