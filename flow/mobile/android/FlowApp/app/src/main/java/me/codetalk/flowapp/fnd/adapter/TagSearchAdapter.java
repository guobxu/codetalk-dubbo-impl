package me.codetalk.flowapp.fnd.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import me.codetalk.event.Event;
import me.codetalk.event.EventBus;
import me.codetalk.flowapp.AppConstants;
import me.codetalk.flowapp.R;
import me.codetalk.flowapp.fnd.model.entity.Tag;

import static me.codetalk.flowapp.AppConstants.EVENT_TAG_SELECT_CHANGE;

/**
 * Created by guobxu on 2018/1/17.
 */

public class TagSearchAdapter extends RecyclerView.Adapter<TagSearchAdapter.ViewHolder>{

    private List<Tag> tags = null;

    private Set<String> selected = null;

    private Context context;

    public TagSearchAdapter(Context context) {
        this(context, null);
    }

    public TagSearchAdapter(Context context, Set<String> selected) {
        this.context = context;

        this.selected = selected == null ? new HashSet<>() : selected;
    }

    @Override
    public TagSearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tag_search, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TagSearchAdapter.ViewHolder holder, int position) {
        Tag tag = tags.get(position);

        String tagText = tag.getText();
        holder.textTag.setText(tagText);
        holder.textHits.setText(String.valueOf(tag.getHits()));

        if(selected.contains(tagText)) {
            holder.itemView.setBackground(context.getDrawable(R.drawable.bg_rect_accent_radius_8dp));
        } else {
            holder.itemView.setBackground(context.getDrawable(R.drawable.bg_rect_default_radius_8dp));
        }
    }

    @Override
    public int getItemCount() {
        return tags == null ? 0 : tags.size();
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;

        notifyDataSetChanged();
    }

    public Set<String> getSelectedTags() {
        return selected;
    }

    public int getSelectCount() {
        return selected.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textTag;
        TextView textHits;

        public ViewHolder(View itemView) {
            super(itemView);

            textTag = itemView.findViewById(R.id.tag_text);
            textHits = itemView.findViewById(R.id.tag_hits);

            itemView.setOnClickListener(view -> {
                String tagText = textTag.getText().toString();
                if(selected.contains(tagText)) {
                    view.setBackground(context.getDrawable(R.drawable.bg_rect_default_radius_8dp));
                    selected.remove(tagText);

                    EventBus.publish(Event.name(EVENT_TAG_SELECT_CHANGE));
                } else {
                    if(selected.size() >= AppConstants.MAX_USER_TAG) {
                        String mesg = context.getResources().getString(R.string.err_max_usertag_reached);
                        Toast.makeText(context, String.format(mesg, AppConstants.MAX_USER_TAG), Toast.LENGTH_SHORT).show();
                    } else {
                        view.setBackground(context.getDrawable(R.drawable.bg_rect_accent_radius_8dp));
                        selected.add(tagText);

                        EventBus.publish(Event.name(EVENT_TAG_SELECT_CHANGE));
                    }
                }
            });
        }
    }

}
