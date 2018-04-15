package me.codetalk.flowapp.main.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import me.codetalk.annotation.EventSupport;
import me.codetalk.event.Event;
import me.codetalk.flowapp.AppConstants;
import me.codetalk.flowapp.R;
import me.codetalk.flowapp.fragment.BaseFragment;
import me.codetalk.flowapp.main.adapter.TopTagAdapter;
import me.codetalk.flowapp.fnd.model.TagApiModel;
import me.codetalk.flowapp.fnd.model.entity.Tag;

/**
 * Created by guobxu on 2017/12/24.
 */

@EventSupport
public class SearchFragment extends BaseFragment {

    private static final int TOP_TAG_COUNT = 5;

    private TagApiModel tagApiModel;

    private TextView textSearchTip;
    private TextView titleTopTags;

    private RecyclerView listTopTags;
    private TopTagAdapter topTagAdapter;

    public SearchFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        // views
        textSearchTip = rootView.findViewById(R.id.text_search_tip);
        titleTopTags = rootView.findViewById(R.id.title_top_tags);

        listTopTags = rootView.findViewById(R.id.list_tags_topbyday);
        topTagAdapter = new TopTagAdapter(getContext());
        listTopTags.setAdapter(topTagAdapter);

        setDefaultLayout(listTopTags, true);

        // models
        Context appContext = getAppContext();
        tagApiModel = TagApiModel.getInstance(appContext);

        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {   // fragment in ViewPager DOES NOT trigger onResume
        super.setUserVisibleHint(isVisibleToUser);              // https://stackoverflow.com/questions/10024739/how-to-determine-when-fragment-becomes-visible-in-viewpager

        if (isVisibleToUser) {
            tagApiModel.topByDay(TOP_TAG_COUNT);
        }
    }

    @Override
    public void handleSuccess(Event e) {
        String name = e.name();

        if(AppConstants.EVENT_TAG_TOPBYDAY_RT.equals(name)) {
            List<Tag> tagList = (List<Tag>)e.extra1();
            topTagAdapter.setTags(tagList);

            if(tagList.size() > 0) {
                showTopTags();
            } else {
                showSearchTip();
            }
        }
    }

    private void showSearchTip() {
        textSearchTip.setVisibility(View.VISIBLE);
        titleTopTags.setVisibility(View.GONE);
        listTopTags.setVisibility(View.GONE);
    }

    private void showTopTags() {
        textSearchTip.setVisibility(View.GONE);
        titleTopTags.setVisibility(View.VISIBLE);
        listTopTags.setVisibility(View.VISIBLE);
    }

}
