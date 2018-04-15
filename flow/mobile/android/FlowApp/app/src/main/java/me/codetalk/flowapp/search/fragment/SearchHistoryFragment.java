package me.codetalk.flowapp.search.fragment;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import me.codetalk.flowapp.R;
import me.codetalk.flowapp.fragment.BaseFragment;
import me.codetalk.flowapp.search.activity.SearchActivity;
import me.codetalk.flowapp.search.adapter.SearchItemAdapter;
import me.codetalk.flowapp.search.model.entity.SearchItem;

/**
 * Created by guobxu on 2018/1/21.
 */

public class SearchHistoryFragment extends BaseFragment {

    private TextView textSearchTip;
    private TextView textSearchHistory;

    private RecyclerView listSearchItems;
    private SearchItemAdapter searchItemAdapter;

    public SearchHistoryFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search_history, container, false);

        textSearchTip = rootView.findViewById(R.id.text_search_tip);
        textSearchHistory = rootView.findViewById(R.id.title_search_history);

        listSearchItems = rootView.findViewById(R.id.list_search_history);
        setDefaultLayout(listSearchItems, true);

        // adapter
        searchItemAdapter = new SearchItemAdapter();
        listSearchItems.setAdapter(searchItemAdapter);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        List<SearchItem> recentSearches = getRecentSearches();
        if(recentSearches.size() == 0) {
            showSearchTip();
        } else {
            showSearchHistory();

            searchItemAdapter.setSearchItems(recentSearches);
        }
    }

    private void showSearchTip() {
        textSearchTip.setVisibility(View.VISIBLE);
        textSearchHistory.setVisibility(View.GONE);
        listSearchItems.setVisibility(View.GONE);
    }

    private void showSearchHistory() {
        textSearchTip.setVisibility(View.GONE);
        textSearchHistory.setVisibility(View.VISIBLE);
        listSearchItems.setVisibility(View.VISIBLE);
    }

    private List<SearchItem> getRecentSearches() {
        SearchActivity searchActivity = (SearchActivity)getActivity();

        return searchActivity.getRecentSearches();
    }

}
