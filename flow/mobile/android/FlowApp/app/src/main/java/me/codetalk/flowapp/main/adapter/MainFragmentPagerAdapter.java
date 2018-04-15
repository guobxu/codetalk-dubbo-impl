package me.codetalk.flowapp.main.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import me.codetalk.flowapp.main.fragment.MesgThreadListFragment;
import me.codetalk.flowapp.post.fragment.PostListFragment;
import me.codetalk.flowapp.main.fragment.SearchFragment;

/**
 * Created by guobxu on 2017/12/24.
 */

public class MainFragmentPagerAdapter extends FragmentPagerAdapter {

    static final int TAB_COUNT_HOME = 3;

    private Context mContext;

    public MainFragmentPagerAdapter(Context context, FragmentManager fm) {
        super(fm);

        this.mContext = context;
    }

    @Override
    public CharSequence getPageTitle(int position) {
//        switch(position) {
//            case 0:
//                return mContext.getString(R.string.tab_home_posts);
//            case 1:
//                return mContext.getString(R.string.tab_home_search);
//            case 2:
//                return mContext.getString(R.string.tab_home_prvm);
//        }
//
//        return null;
        return "";
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                return PostListFragment.timeline();
            case 1:
                return new SearchFragment();
            case 2:
                return new MesgThreadListFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        return TAB_COUNT_HOME;
    }
}
