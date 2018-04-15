package me.codetalk.flowapp.post.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import me.codetalk.flowapp.AppConstants;
import me.codetalk.flowapp.R;
import me.codetalk.flowapp.activity.BaseActivity;
import me.codetalk.flowapp.fnd.activity.UserActivity;
import me.codetalk.flowapp.post.adapter.CommentListAdapter;
import me.codetalk.flowapp.post.model.PostApiModel;
import me.codetalk.flowapp.post.model.entity.Comment;
import me.codetalk.flowapp.post.model.entity.CommentThread;
import me.codetalk.flowapp.post.model.entity.Mention;
import me.codetalk.flowapp.post.model.entity.Post;
import me.codetalk.flowapp.post.view.util.PostViewUtils;
import me.codetalk.util.DateUtils;
import me.codetalk.util.JsonUtils;

import static me.codetalk.flowapp.AppConstants.ACTION_LIKE;
import static me.codetalk.flowapp.AppConstants.ACTION_UNLIKE;

public class PostDetailActivity extends BaseActivity {

    @BindView(R.id.post_profile_img)
    CircleImageView profileImg;

    @BindView(R.id.post_text_username)
    TextView textUserName;

    @BindView(R.id.post_text_userlogin)
    TextView textUserLogin;

    @BindView(R.id.post_text_content)
    TextView textPostContent;

    @BindView(R.id.post_text_createdate)
    TextView textCreateDate;

    @BindView(R.id.post_btn_comment)
    ImageButton btnCmnt;

    @BindView(R.id.post_text_cmntcount)
    TextView textCmntCount;

    @BindView(R.id.post_btn_like)
    ImageButton btnLike;

    @BindView(R.id.post_text_likecount)
    TextView textLikeCount;

    @BindView(R.id.post_list_comment)
    RecyclerView commentListView;

    private CommentListAdapter commentListAdapter;

    // models
    PostApiModel postApiModel;

    // properties
    private Long postId;
    private Integer postType;
    private Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        ButterKnife.bind(this);

        // models
        Context appContext = getApplicationContext();
        postApiModel = PostApiModel.getInstance(appContext);

        // intent
        Intent intent = getIntent();
        postId = intent.getLongExtra("post_id", -1);
        postType = intent.getIntExtra("post_type", -1);

        postApiModel.postDetail(postId, postType, post -> {
            this.post = post;
            bindViews();
        });

        // views
        initViews();
    }

    private void initViews() {
        View.OnTouchListener postBtnTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        ((ViewGroup)view.getParent()).setAlpha(0.75f);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        ((ViewGroup)view.getParent()).setAlpha(1f);
                        break;
                    case MotionEvent.ACTION_UP:
                        ((ViewGroup)view.getParent()).setAlpha(1f);
                        break;
                }

                return false;
            }
        };
        btnCmnt.setOnTouchListener(postBtnTouchListener);
        btnLike.setOnTouchListener(postBtnTouchListener);

        // actions
        btnLike.setOnClickListener(view -> {
            postApiModel.postLike(post.getId(), post.getLiked() == 1 ? ACTION_UNLIKE : ACTION_LIKE, likes -> {
                textLikeCount.setText(String.valueOf(likes));
                post.setLiked(post.getLiked() == 1 ? 0 : 1);
                updateLikeButton();
            });
        });

        profileImg.setOnClickListener(view -> {
            Intent userIntent = new Intent(this, UserActivity.class);
            userIntent.putExtra("user_id", post.getUserId());

            startActivity(userIntent);
        });

        btnCmnt.setOnClickListener(view -> {
            Intent cmntIntent = new Intent(this, CommentCreateActivity.class);
            cmntIntent.putExtra("post_id", post.getId());

            Mention mention = new Mention(post.getUserId(), post.getUserLogin(), post.getUserProfile());
            cmntIntent.putExtra("mention_list", JsonUtils.toJson(Arrays.asList(mention)));

            startActivity(cmntIntent);
        });
    }

    private void bindViews() {
        showUserProfile(profileImg, post.getUserProfile());
        textUserName.setText(post.getUserName());
        textUserLogin.setText(AppConstants.CHAR_MENTION + post.getUserLogin());

        PostViewUtils.displayTaggedContent(post.getContent(), post.getTags(), this, textPostContent);

        textCreateDate.setText(DateUtils.format(post.getCreateDate(), "yyyy-MM-dd HH:mm"));

        textLikeCount.setText(post.getLikeCount().toString());
        textCmntCount.setText(post.getCommentCount().toString());

        updateLikeButton();

        // comment fragment
//        replaceFragment(R.id.post_comment_fragment_container,
//                CommentThreadListFragment.fromCommentThreads(post.getCommentThreads()));

        // comment list
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        commentListView.setLayoutManager(layoutManager);

        commentListAdapter = new CommentListAdapter(this);
        commentListAdapter.setCommentList(flatCommentThreadList(post.getCommentThreads()), null);
        commentListView.setAdapter(commentListAdapter);
    }

    private void updateLikeButton() {
        PostViewUtils.updateLikeAction(post.getLiked(), btnLike, textLikeCount);
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












