package me.codetalk.flowapp.post.model;

import android.content.Context;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import me.codetalk.api.ApiUtils;
import me.codetalk.api.FileUploadApi;
import me.codetalk.api.response.ObjectDataResponse;
import me.codetalk.event.Event;
import me.codetalk.event.EventBus;
import me.codetalk.flowapp.AppConstants;
import me.codetalk.flowapp.post.api.CommentApi;
import me.codetalk.flowapp.post.api.PostApi;
import me.codetalk.flowapp.post.api.request.CommentCreateBody;
import me.codetalk.flowapp.post.api.request.CommentLikeBody;
import me.codetalk.flowapp.post.api.request.PostCreateBody;
import me.codetalk.flowapp.post.api.request.PostLikeBody;
import me.codetalk.flowapp.post.model.entity.Comment;
import me.codetalk.flowapp.post.model.entity.CommentThread;
import me.codetalk.flowapp.post.model.entity.Post;
import me.codetalk.model.BaseApiModel;
import me.codetalk.model.FileUploadApiModel;
import me.codetalk.util.CollectionUtils;
import me.codetalk.util.HttpUtils;
import me.codetalk.util.JsonUtils;
import me.codetalk.util.StringUtils;
import okhttp3.MultipartBody;

/**
 * Created by guobxu on 2017/12/28.
 */

public class CommentApiModel extends BaseApiModel {

    private CommentApi commentApi = ApiUtils.getRestApi(CommentApi.class);

    private static CommentApiModel INSTANCE = null;

    private CommentApiModel(Context appContext) {
        super(appContext);
    }

    public static CommentApiModel getInstance(Context appContext) {
        if(INSTANCE == null) {
            INSTANCE = new CommentApiModel(appContext);
        }

        return INSTANCE;
    }

    public void replyPost(Long postId, String content) {
        CommentCreateBody body = new CommentCreateBody();
        body.setPostId(postId);
        body.setContent(content);

        commentApi.createComment(body).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(rt -> {
            if(ApiUtils.isSuccess(rt)) {
                Long cmntId = Long.parseLong(rt.getRetData().get("comment_id").toString());

                Event e = Event.builder().name(AppConstants.EVENT_COMMENT_CREATE_RT)
                        .extra1(cmntId).extra2(content).extra3(postId).build();
                EventBus.publish(e);
            } else {
                handleApiFailure(rt.getRetMsg(), AppConstants.EVENT_COMMENT_CREATE_ERR);
            }
        }, throwable -> {
            handleApiErr(throwable, AppConstants.EVENT_COMMENT_CREATE_ERR);
        });
    }

    public void replyComment(Long cmntReply, String content) {
        CommentCreateBody body = new CommentCreateBody();
        body.setCommentId(cmntReply);
        body.setContent(content);

        commentApi.createComment(body).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(rt -> {
            if(ApiUtils.isSuccess(rt)) {
                Long cmntId = Long.parseLong(rt.getRetData().get("comment_id").toString());

                Event e = Event.builder().name(AppConstants.EVENT_COMMENT_CREATE_RT)
                        .extra1(cmntId).extra2(content).extra3(cmntReply).build();
                EventBus.publish(e);
            } else {
                handleApiFailure(rt.getRetMsg(), AppConstants.EVENT_COMMENT_CREATE_ERR);
            }
        }, throwable -> {
            handleApiErr(throwable, AppConstants.EVENT_COMMENT_CREATE_ERR);
        });
    }

    public void commentLike(Long cmntId, int actionType, Consumer<Integer> consumer) {
        CommentLikeBody body = new CommentLikeBody();
        body.setCommentId(cmntId);
        body.setActionType(actionType);

        commentApi.commentLike(body).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(rt -> {
            if(ApiUtils.isSuccess(rt)) {
                Integer likes = (Integer)( (Map)rt.getRetData() ).get("likes");
                consumer.accept(likes);
            } else {
                handleApiFailure(rt.getRetMsg(), AppConstants.EVENT_CMNT_LIKE_ERR);
            }
        }, throwable -> {
            handleApiErr(throwable, AppConstants.EVENT_CMNT_LIKE_ERR);
        });
    }

    public void commentDetail(Long cmntId, Consumer<List<CommentThread>> consumer) {
        commentApi.commentDetail(cmntId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(rt -> {
            if(ApiUtils.isSuccess(rt)) {
                consumer.accept(rt.getCommentThreads());
            } else {
                handleApiFailure(rt.getRetMsg(), AppConstants.EVENT_CMNT_DETAIL_ERR);
            }
        }, throwable -> {
            handleApiErr(throwable, AppConstants.EVENT_CMNT_DETAIL_ERR);
        });
    }

}














