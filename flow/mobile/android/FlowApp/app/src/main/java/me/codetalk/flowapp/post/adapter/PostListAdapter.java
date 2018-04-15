package me.codetalk.flowapp.post.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import me.codetalk.event.Event;
import me.codetalk.event.EventBus;
import me.codetalk.flowapp.AppConstants;
import me.codetalk.flowapp.R;
import me.codetalk.flowapp.fnd.activity.UserActivity;
import me.codetalk.flowapp.post.activity.CommentCreateActivity;
import me.codetalk.flowapp.post.activity.PostDetailActivity;
import me.codetalk.flowapp.post.model.PostApiModel;
import me.codetalk.flowapp.post.model.entity.Mention;
import me.codetalk.flowapp.post.model.entity.Post;
import me.codetalk.flowapp.post.view.util.PostViewUtils;
import me.codetalk.util.DateUtils;
import me.codetalk.util.JsonUtils;
import me.codetalk.util.ViewUtils;

import static me.codetalk.flowapp.AppConstants.ACTION_LIKE;
import static me.codetalk.flowapp.AppConstants.ACTION_UNLIKE;
import static me.codetalk.flowapp.AppConstants.USER_DEFAULT_PROFIE;

/**
 * Created by guobxu on 2018/1/18.
 */

public class PostListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_ITEM = 1;
    private static final int VIEW_TYPE_LOADING = 2;

    private Context context;
    private List<Post> postList;

    private LoadingViewHolder loadingViewHolder;

    private PostApiModel postApiModel;

    public PostListAdapter(Context context) {
        this.context = context;

        postApiModel = PostApiModel.getInstance(context.getApplicationContext());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);

            return new PostViewHolder(view);
        } else if(viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_list_loading, parent, false);
            loadingViewHolder = new LoadingViewHolder(view);

            return loadingViewHolder;
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof PostViewHolder) {
            bindPostViewHolder((PostViewHolder)holder, postList.get(position));
        } else if(holder instanceof LoadingViewHolder) {

        }
    }

    private void bindPostViewHolder(PostViewHolder holder, Post post) {
        String profileUrl = post.getUserProfile();
        if(profileUrl != null) {
            ViewUtils.loadImageUrl(context, holder.profileImg, profileUrl);
        } else {
            holder.profileImg.setImageResource(USER_DEFAULT_PROFIE);
        }

        holder.textUserName.setText(post.getUserName());
        holder.textPostDate.setText(DateUtils.readable(post.getCreateDate()));

        // post content
        PostViewUtils.displayTaggedContent(post.getContent(), post.getTags(), context, holder.textPostContent);

        holder.textCmntCount.setText(post.getCommentCount().toString());
        holder.textLikeCount.setText(post.getLikeCount().toString());

        holder.post = post;
        holder.updateLikeButton();
    }

    @Override
    public int getItemCount() {
        return postList == null ? 1 : postList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if(postList == null || position > postList.size() - 1) {
            return VIEW_TYPE_LOADING;
        } else {
            return VIEW_TYPE_ITEM;
        }
    }

    public void addPosts(List<Post> posts) {
        if(postList == null) postList = new ArrayList<>();

        int currentSize = postList.size();
        postList.addAll(posts);

        notifyItemRangeInserted(currentSize, posts.size());
    }

    public void prependPosts(List<Post> posts) {
        if(postList == null) {
            postList = posts;
        } else {
            postList.addAll(0, posts);
        }

        notifyItemRangeInserted(0, posts.size());
    }

    public void setPosts(List<Post> posts) {
        postList = posts;

        notifyDataSetChanged();
    }

    public void clearPosts() {
        postList = new ArrayList<>();

        notifyDataSetChanged();
    }

    public void showLoading() {
        loadingViewHolder.showLoading();
    }

    public void showLoaded() {
        loadingViewHolder.showLoaded();
    }

    public void hideLoading() {
        loadingViewHolder.hideLoading();
    }

    public void showReload() {
        loadingViewHolder.showReload();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {

        Post post;

        CircleImageView profileImg;

        TextView textUserName;
        TextView textPostDate;

        TextView textPostContent;

        TextView textCmntCount;
        TextView textLikeCount;

        ImageButton btnCmnt;
        ImageButton btnLike;
        ImageButton btnMesg;

        @SuppressLint("ClickableViewAccessibility")
        public PostViewHolder(View itemView) {
            super(itemView);

            profileImg = itemView.findViewById(R.id.post_profile_img);

            textUserName = itemView.findViewById(R.id.post_text_username);
            textPostDate = itemView.findViewById(R.id.post_text_createdate);

            textPostContent = itemView.findViewById(R.id.post_text_content);

            textCmntCount = itemView.findViewById(R.id.post_text_cmntcount);
            textLikeCount = itemView.findViewById(R.id.post_text_likecount);

            btnCmnt = itemView.findViewById(R.id.post_btn_comment);
            btnLike = itemView.findViewById(R.id.post_btn_like);
            btnMesg = itemView.findViewById(R.id.post_btn_mesg);

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
            btnMesg.setOnTouchListener(postBtnTouchListener);

            //

            // actions
            btnLike.setOnClickListener(view -> {
                postApiModel.postLike(post.getId(), post.getLiked() == 1 ? ACTION_UNLIKE : ACTION_LIKE, likes -> {
                    textLikeCount.setText(String.valueOf(likes));
                    post.setLiked(post.getLiked() == 1 ? 0 : 1);
                    updateLikeButton();
                });
            });

            profileImg.setOnClickListener(view -> {
                Intent intent = new Intent(context, UserActivity.class);
                intent.putExtra("user_id", post.getUserId());

                context.startActivity(intent);
            });

            btnCmnt.setOnClickListener(view -> {
                Intent intent = new Intent(context, CommentCreateActivity.class);
                intent.putExtra("post_id", post.getId());

                Mention mention = new Mention(post.getUserId(), post.getUserLogin(), post.getUserProfile());
                intent.putExtra("mention_list", JsonUtils.toJson(Arrays.asList(mention)));

                context.startActivity(intent);
            });

            itemView.setOnClickListener(view -> {
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra("post_id", post.getId());
                intent.putExtra("post_type", post.getType());

                context.startActivity(intent);
            });
        }

        private void updateLikeButton() {
            PostViewUtils.updateLikeAction(post.getLiked(), btnLike, textLikeCount);
        }

    }


    public class LoadingViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressLoading;
        TextView textLoaded;
        TextView actionReload;

        public LoadingViewHolder(View itemView) {
            super(itemView);

            progressLoading = itemView.findViewById(R.id.progress_list_loading);
            textLoaded = itemView.findViewById(R.id.text_list_loaded);
            actionReload = itemView.findViewById(R.id.action_reload_list);

            actionReload.setOnClickListener(view -> {
                EventBus.publish(Event.name(AppConstants.EVENT_POST_LIST_RELOAD));
            });
        }

        public void showLoading() {
            itemView.setVisibility(View.VISIBLE);

            actionReload.setVisibility(View.GONE);

            progressLoading.setVisibility(View.VISIBLE);
            textLoaded.setVisibility(View.GONE);
        }

        public void showLoaded() {
            itemView.setVisibility(View.VISIBLE);

            actionReload.setVisibility(View.GONE);

            progressLoading.setVisibility(View.GONE);
            textLoaded.setVisibility(View.VISIBLE);
        }

        public void hideLoading() {
            itemView.setVisibility(View.GONE);
        }

        public void showReload() {
            itemView.setVisibility(View.VISIBLE);

            progressLoading.setVisibility(View.GONE);
            textLoaded.setVisibility(View.GONE);
            actionReload.setVisibility(View.VISIBLE);
        }

    }


}
