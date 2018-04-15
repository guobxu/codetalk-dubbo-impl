package me.codetalk.flowapp.main.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.codetalk.flowapp.R;

/**
 *
 * by guobxu - 2017/12/24
 *
 */
public class MesgThreadListFragment extends Fragment {

    public MesgThreadListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mesg_thread_list, container, false);
    }


}
