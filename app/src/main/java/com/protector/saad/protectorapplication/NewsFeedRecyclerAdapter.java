package com.protector.saad.protectorapplication;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class NewsFeedRecyclerAdapter extends RecyclerView.Adapter<NewsFeedRecyclerAdapter.RecyclerViewHolder> {
        private Context context;
        private ArrayList<NewsFeed> newsfeed;

    public NewsFeedRecyclerAdapter(Context context, ArrayList<NewsFeed> newsfeed) {
        this.context = context;
        this.newsfeed = newsfeed;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.newsfeed_layout,parent,false);
        RecyclerViewHolder recyclerViewHolder=new RecyclerViewHolder(v);
        return  recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        holder.text.setText(newsfeed.get(position).getText());
        holder.time.setText(newsfeed.get(position).getTime());
    }

    @Override
    public int getItemCount() {
        return newsfeed.size();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private TextView text,time;
        public RecyclerViewHolder(View itemView) {
            super(itemView);
            text=itemView.findViewById(R.id.post_text);
            time=itemView.findViewById(R.id.post_time);
        }
    }
}
