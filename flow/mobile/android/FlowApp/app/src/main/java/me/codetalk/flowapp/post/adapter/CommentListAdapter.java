package me.codetalk.flowapp.post.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import me.codetalk.flowapp.R;
import me.codetalk.flowapp.fnd.activity.UserActivity;
import me.codetalk.flowapp.post.activity.CommentCreateActivity;
import me.codetalk.flowapp.post.activity.CommentDetailActivity;
import me.codetalk.flowapp.post.model.CommentApiModel;
import me.codetalk.flowapp.post.model.entity.Comment;
import me.codetalk.flowapp.post.view.util.PostViewUtils;
import me.codetalk.util.DateUtils;
import me.codetalk.util.ViewUtils;

import static me.codetalk.flowapp.AppConstants.ACTION_LIKE;
import static me.codetalk.flowapp.AppConstants.ACTION_UNLIKE;
import static me.codetalk.flowapp.AppConstants.USER_DEFAULT_PROFIE;

/**
 * Created by guobxu on 2018/1/27.
 */

public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.ViewHolder>{

    private Context context;

    private List<Comment> commentList;

    private Long cmntHighlight;

    private CommentApiModel cmntApiModel;

    public CommentListAdapter(Context context) {
        this.context = context;

        // models
        Context appContext = context.getApplicationContext();
        cmntApiModel = CommentApiModel.getInstance(appContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder.comment = comment;

        String profileUrl = comment.getUserProfile();
        if(profileUrl != null) {
            ViewUtils.loadImageUrl(context, holder.profileImg, profileUrl);
        } else {
            holder.profileImg.setImageResource(USER_DEFAULT_PROFIE);
        }

        holder.textUserName.setText(comment.getUserName());
        holder.textCmntDate.setText(DateUtils.readable(comment.getCreateDate()));

        // post content
        PostViewUtils.displayTaggedContent(comment.getContent(), comment.getTags(), context, holder.textCmntContent);

        holder.textCmntCount.setText(comment.getCommentCount().toString());
        holder.textLikeCount.setText(comment.getLikeCount().toString());

        holder.updateLikeButton();

        // thread line
        Comment nextComment = commentList.size() > position + 1 ? commentList.get(position + 1) : null;
        if(nextComment != null && nextComment.getThread().equals(comment.getThread())) {
            holder.layoutThreadline.setVisibility(View.VISIBLE);
        } else {
            holder.layoutThreadline.setVisibility(View.GONE);
        }

        // border
//        Comment preComment = position == 0 ? null : commentList.get(position - 1);
//        if(preComment == null || !preComment.getThread().equals(comment.getThread())) {
//            holder.itemView.setBackgroundResource(R.drawable.bg_rect_default_border_top_gray_1dp);
//        }
        if(nextComment == null || !nextComment.getThread().equals(comment.getThread())) {
            holder.itemView.setBackgroundResource(R.drawable.bg_rect_default_border_bottom_gray_1dp);
        }

        // highlight
        if(comment.getId().equals(cmntHighlight)) {
            holder.itemView.setBackgroundColor(ViewUtils.getColor(R.color.colorGray));
        }
    }

    public void setCommentList(List<Comment> commentList, Long cmntHighlight) {
        this.commentList = commentList;

        this.cmntHighlight = cmntHighlight;

        notifyDataSetChanged();
    }

    public Integer getHighlightPos() {
        if(cmntHighlight == null) return -1;

        for(int i = 0; i < commentList.size(); i++) {
            Comment cmnt = commentList.get(i);

            if(cmnt.getId().equals(cmntHighlight)) return i;
        }

        return -1;
    }

    @Override
    public int getItemCount() {
        return commentList == null ? 0 : commentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        Comment comment;

        CircleImageView profileImg;

        TextView textUserName;
        TextView textCmntDate;

        TextView textCmntContent;

        TextView textCmntCount;
        TextView textLikeCount;

        ImageButton btnCmnt;
        ImageButton btnLike;

        LinearLayout layoutThreadline;

        public ViewHolder(View itemView) {
            super(itemView);

            profileImg = itemView.findViewById(R.id.comment_profile_img);

            textUserName = itemView.findViewById(R.id.comment_text_username);
            textCmntDate = itemView.findViewById(R.id.comment_text_createdate);

            textCmntContent = itemView.findViewById(R.id.comment_text_content);

            textCmntCount = itemView.findViewById(R.id.comment_text_cmntcount);
            textLikeCount = itemView.findViewById(R.id.comment_text_likecount);

            btnCmnt = itemView.findViewById(R.id.comment_btn_comment);
            btnLike = itemView.findViewById(R.id.comment_btn_like);

            layoutThreadline = itemView.findViewById(R.id.comment_threadline_container);

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
                cmntApiModel.commentLike(comment.getId(), comment.getLiked() == 1 ? ACTION_UNLIKE : ACTION_LIKE, likes -> {
                    textLikeCount.setText(String.valueOf(likes));
                    comment.setLiked(comment.getLiked() == 1 ? 0 : 1);
                    updateLikeButton();
                });
            });

            profileImg.setOnClickListener(view -> {
                Intent intent = new Intent(context, UserActivity.class);
                intent.putExtra("user_id", comment.getUserId());

                context.startActivity(intent);
            });

            btnCmnt.setOnClickListener(view -> {
                Intent intent = new Intent(context, CommentCreateActivity.class);
                intent.putExtra("comment_id", comment.getId());

                intent.putExtra("mention_list", comment.getMentions());

                context.startActivity(intent);
            });

            itemView.setOnClickListener(view -> {
                if(comment.getCommentCount() <= 1) return;

                Intent intent = new Intent(context, CommentDetailActivity.class);
                intent.putExtra("comment_id", comment.getId());

                context.startActivity(intent);
            });
        }

        private void updateLikeButton() {
            PostViewUtils.updateLikeAction(comment.getLiked(), btnLike, textLikeCount);
        }


    }


}
