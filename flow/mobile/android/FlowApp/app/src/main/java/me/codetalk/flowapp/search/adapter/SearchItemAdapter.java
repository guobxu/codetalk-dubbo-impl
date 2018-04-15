package me.codetalk.flowapp.search.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import me.codetalk.event.Event;
import me.codetalk.event.EventBus;
import me.codetalk.flowapp.AppConstants;
import me.codetalk.flowapp.R;
import me.codetalk.flowapp.search.model.entity.SearchItem;

/**
 * Created by guobxu on 2018/1/21.
 */

public class SearchItemAdapter extends RecyclerView.Adapter<SearchItemAdapter.ViewHolder> {

    private List<SearchItem> searchItems;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_history, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String searchText = searchItems.get(position).getText();

        holder.textSearchItem.setText(searchText);
    }

    @Override
    public int getItemCount() {
        return searchItems == null ? 0 : searchItems.size();
    }

    public void setSearchItems(List<SearchItem> searchItems) {
        this.searchItems = searchItems;

        notifyDataSetChanged();
    }

    public List<SearchItem> getSearchItems() {
        return this.searchItems;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView textSearchItem;

        public ViewHolder(View itemView) {
            super(itemView);

            textSearchItem = itemView.findViewById(R.id.text_search_item);
            itemView.setOnClickListener(view -> {
                Event e = Event.builder().name(AppConstants.EVENT_ACTION_SEARCH).extra1(textSearchItem.getText()).build();
                EventBus.publish(e);
            });
        }

    }

}
