package me.codetalk.flowapp.main.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import me.codetalk.flowapp.AppConstants;
import me.codetalk.flowapp.R;
import me.codetalk.flowapp.fnd.model.entity.Tag;
import me.codetalk.flowapp.search.activity.SearchActivity;

/**
 * Created by guobxu on 2018/1/22.
 */

public class TopTagAdapter extends RecyclerView.Adapter<TopTagAdapter.ViewHolder> {

    private List<Tag> tagList;

    private Context context;

    public TopTagAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_top_tag, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Tag tag = tagList.get(position);

        holder.textTag.setText(AppConstants.CHAR_HASHTAG + tag.getText());
        holder.textHits.setText(String.valueOf(tag.getHits()));
    }

    @Override
    public int getItemCount() {
        return tagList == null ? 0 : tagList.size();
    }

    public void setTags(List<Tag> tagList) {
        this.tagList = tagList;

        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textTag;
        TextView textHits;

        public ViewHolder(View itemView) {
            super(itemView);

            textTag = itemView.findViewById(R.id.text_tag);
            textHits = itemView.findViewById(R.id.text_taghits);

            itemView.setOnClickListener(view -> {
                Intent intent = new Intent(context, SearchActivity.class);
                intent.putExtra("search_text", textTag.getText());

                context.startActivity(intent);
            });
        }
    }
}
