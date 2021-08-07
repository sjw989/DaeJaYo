package com.example.project_4weeks_ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class Menu_Adapter extends RecyclerView.Adapter<Menu_Adapter.MenuViewHolder> {
    private ArrayList<Menu> arrayList; // 메뉴정보를 담을 ArrayList
    private Context context;

    public Menu_Adapter(ArrayList<Menu> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_list,parent,false);
        MenuViewHolder holder = new MenuViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        Glide.with(holder.itemView)
                .load(arrayList.get(position).get_img_URL())
                .into(holder.ib_image);
        holder.tv_name.setText(arrayList.get(position).get_name());
        holder.tv_info1.setText(arrayList.get(position).get_info1());
        holder.tv_info2.setText(arrayList.get(position).get_info2());
        holder.tv_info3.setText(arrayList.get(position).get_info3());
    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
        // arrayList 사이즈가 0이 아니면 그 사이즈를 return, null이면 0을 return
    }

    public class MenuViewHolder extends RecyclerView.ViewHolder {
        ImageButton ib_image;
        TextView tv_name;
        TextView tv_info1;
        TextView tv_info2;
        TextView tv_info3;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            this.ib_image = itemView.findViewById(R.id.ib_image);
            this.tv_name = itemView.findViewById(R.id.tv_name);
            this.tv_info1 = itemView.findViewById(R.id.tv_info1);
            this.tv_info2 = itemView.findViewById(R.id.tv_info2);
            this.tv_info3 = itemView.findViewById(R.id.tv_info3);
        }
    }
}
