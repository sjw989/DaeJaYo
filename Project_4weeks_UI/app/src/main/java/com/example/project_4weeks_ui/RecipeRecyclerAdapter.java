package com.example.project_4weeks_ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class RecipeRecyclerAdapter extends RecyclerView.Adapter<RecipeRecyclerAdapter.ViewHolder> {

    private ArrayList<RecipeItem> mData = new ArrayList<RecipeItem>();

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_recipe ;
        ImageView iv_recipe;

        ViewHolder(View itemView) {
            super(itemView);

            // 뷰 객체에 대한 참조. (hold strong reference)
            tv_recipe = itemView.findViewById(R.id.tv_recipe_rv) ;
            iv_recipe = itemView.findViewById(R.id.iv_recipe_rv);
        }
        void onBind(RecipeItem item){
            Glide.with(iv_recipe.getContext()).load(item.getResourceId()).into(iv_recipe);
            tv_recipe.setText(item.getName());

        }
    }
    public void setmData(ArrayList<RecipeItem> list) {
        mData = list ;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecipeRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.recycler_item_recipe, parent, false) ;
        RecipeRecyclerAdapter.ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeRecyclerAdapter.ViewHolder holder, int position) {
        holder.onBind(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


}

