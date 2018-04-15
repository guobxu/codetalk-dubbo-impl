package me.codetalk.flowapp.post.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import me.codetalk.flowapp.R;
import me.codetalk.flowapp.fragment.BaseFragment;
import me.codetalk.flowapp.post.adapter.CommentListAdapter;
import me.codetalk.flowapp.post.adapter.PostListAdapter;
import me.codetalk.flowapp.post.model.PostApiModel;
import me.codetalk.flowapp.post.model.entity.Comment;
import me.codetalk.flowapp.post.model.entity.CommentThread;
import me.codetalk.util.ViewUtils;

/**
 * Created by guobxu on 2018/1/27.
 */

public class CommentThreadListFragment extends BaseFragment {

    private RecyclerView commentListView;
    private List<CommentThread> threadList;

    private CommentListAdapter commentListAdapter;

    public CommentThreadListFragment() {}

    public static CommentThreadListFragment fromCommentThreads(List<CommentThread> threadList) {
        CommentThreadListFragment fragment = new CommentThreadListFragment();
        fragment.threadList = threadList;

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_comment_thread_list, container, false);

//        Context context = getContext();

        // post list view
        commentListView = rootView.findViewById(R.id.list_comment);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        commentListView.setLayoutManager(layoutManager);

        commentListAdapter = new CommentListAdapter(getContext());
        commentListAdapter.setCommentList(flatCommentThreadList(threadList), null);
        commentListView.setAdapter(commentListAdapter);

        return rootView;
    }

    private List<Comment> flatCommentThreadList(List<CommentThread> threadList) {
        List<Comment> cmntList = new ArrayList<>();

        if(threadList == null) return cmntList;

        for(CommentThread thread : threadList) {
            cmntList.addAll(thread.getCommentList());
        }

        return cmntList;
    }


}
