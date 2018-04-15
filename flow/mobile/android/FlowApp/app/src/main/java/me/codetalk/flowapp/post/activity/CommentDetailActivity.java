package me.codetalk.flowapp.post.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import me.codetalk.annotation.EventSupport;
import me.codetalk.flowapp.R;
import me.codetalk.flowapp.activity.BaseActivity;
import me.codetalk.flowapp.post.adapter.CommentListAdapter;
import me.codetalk.flowapp.post.model.CommentApiModel;
import me.codetalk.flowapp.post.model.PostApiModel;
import me.codetalk.flowapp.post.model.entity.Comment;
import me.codetalk.flowapp.post.model.entity.CommentThread;
import me.codetalk.flowapp.post.view.util.PostViewUtils;

@EventSupport(unregisterWhenStop = true)
public class CommentDetailActivity extends BaseActivity {

    @BindView(R.id.comment_list)
    RecyclerView commentListView;

    private CommentListAdapter commentListAdapter;

    // models
    CommentApiModel commentApiModel;

    private Long commentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_detail);

        ButterKnife.bind(this);

        // models
        Context appContext = getApplicationContext();
        commentApiModel = CommentApiModel.getInstance(appContext);

        // intent
        Intent intent = getIntent();
        commentId = intent.getLongExtra("comment_id", -1);

        commentApiModel.commentDetail(commentId, cmntTheads -> {
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            commentListView.setLayoutManager(layoutManager);

            commentListAdapter = new CommentListAdapter(this);
            commentListAdapter.setCommentList(flatCommentThreadList(cmntTheads), commentId);
            commentListView.setAdapter(commentListAdapter);

            int pos = commentListAdapter.getHighlightPos();
            if(pos != -1) {
                commentListView.scrollToPosition(pos);
            }
        });

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
