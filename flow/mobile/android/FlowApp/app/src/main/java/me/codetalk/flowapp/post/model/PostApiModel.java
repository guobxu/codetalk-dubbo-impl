package me.codetalk.flowapp.post.model;

import android.content.Context;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import me.codetalk.api.ApiUtils;
import me.codetalk.api.FileUploadApi;
import me.codetalk.api.response.ObjectDataResponse;
import me.codetalk.event.Event;
import me.codetalk.event.EventBus;
import me.codetalk.flowapp.AppConstants;
import me.codetalk.flowapp.post.api.PostApi;
import me.codetalk.flowapp.post.api.request.PostCreateBody;
import me.codetalk.flowapp.post.api.request.PostLikeBody;
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

public class PostApiModel extends BaseApiModel {

    private PostApi postApi = ApiUtils.getRestApi(PostApi.class);

    private FileUploadApi fileUploadApi = ApiUtils.getRestApi(FileUploadApi.class);

    private FileUploadApiModel fileUploadApiModel = null;

    private static PostApiModel INSTANCE = null;

    private PostApiModel(Context appContext) {
        super(appContext);

        fileUploadApiModel = FileUploadApiModel.getInstance(appContext);
    }

    public static PostApiModel getInstance(Context appContext) {
        if(INSTANCE == null) {
            INSTANCE = new PostApiModel(appContext);
        }

        return INSTANCE;
    }

    public void timeline(Long beginDate, Long endDate, Integer page, Integer size) {
        search("", beginDate, endDate, page, size);
    }

    public void search(String searchText, Long beginDate, Long endDate, Integer page, Integer size) {
        Integer begin = null, count = null;
        if(page != null && size != null) {
            begin = page * size;
            count = size;
        }
        String[] keywordAndTags = processSearchText(searchText);
        String q = keywordAndTags[0], tags = keywordAndTags[1];

        postApi.search(q, tags, beginDate, endDate, begin, count).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(rt -> {
            if(ApiUtils.isSuccess(rt)) {
                Event e = Event.builder().name(AppConstants.EVENT_POST_SEARCH_RT)
                        .extra1(page).extra2(rt.getPosts()).extra3(searchText)
                        .extra4(beginDate).extra5(endDate)
                        .build();
                EventBus.publish(e);
            } else {
                handleApiFailure(rt.getRetMsg(), AppConstants.EVENT_POST_SEARCH_ERR, page);
            }
        }, throwable -> {
            handleApiErr(throwable, AppConstants.EVENT_POST_SEARCH_ERR, page);
        });
    }

    public void createPost(PostCreateBody postCreateBody) {
        postApi.createPost(postCreateBody).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(rt -> {
            if(ApiUtils.isSuccess(rt)) {
                Event e = Event.builder().name(AppConstants.EVENT_POST_CREATE_RT).extra1(getPostId(rt)).build();
                EventBus.publish(e);
            } else {
                handleApiFailure(rt.getRetMsg(), AppConstants.EVENT_POST_CREATE_ERR);
            }
        }, throwable -> {
            handleApiErr(throwable, AppConstants.EVENT_POST_CREATE_ERR);
        });
    }

    public void createPost(PostCreateBody postCreateBody, List<String> imgList) {

        List<MultipartBody.Part> parts = HttpUtils.createProgressMultiPartByPath(AppConstants.UPLOAD_TYPE_POST_IMAGE, imgList, AppConstants.FIELD_FILE);

        fileUploadApi.fileupload(parts).onErrorReturn(throwable -> { // network error
            handleApiErr(throwable, AppConstants.EVENT_FILEUPLOAD_ERR, AppConstants.UPLOAD_TYPE_POST_IMAGE);

            return ObjectDataResponse.ERROR;
        }).flatMap(rt -> {
            if(ApiUtils.isFailure(rt)) {
                handleApiFailure(rt.getRetMsg(), AppConstants.EVENT_FILEUPLOAD_ERR, AppConstants.UPLOAD_TYPE_POST_IMAGE);

                return Observable.empty();
            } else {
                List<String> postImgs = (List<String>)rt.getRetData();
                postCreateBody.setImages(JsonUtils.toJson(postImgs));

                return postApi.createPost(postCreateBody);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(rt -> {
            if(ApiUtils.isSuccess(rt)) {
                Event e = Event.builder().name(AppConstants.EVENT_POST_CREATE_RT).extra1(getPostId(rt)).build();
                EventBus.publish(e);
            } else {
                handleApiFailure(rt.getRetMsg(), AppConstants.EVENT_POST_CREATE_ERR);
            }
        }, throwable -> {
            handleApiErr(throwable, AppConstants.EVENT_POST_CREATE_ERR);
        });
    }

    private Long getPostId(ObjectDataResponse rt) {
        Map<String, Object> rtData = (Map<String, Object>)rt.getRetData();

        return Long.parseLong(rtData.get("post_id").toString());
    }

    /**
     *
     * @param text
     * @return 0 -> keyword 1 -> tags
     */
    private String[] processSearchText(String text) {
        StringBuffer qbuf = new StringBuffer();
        Set<String> tags = new HashSet<>();

        int ts = -1; // tag start
        for(int i = 0, len = text.length(); i < len; i++) {
            char c = text.charAt(i);

            if(ts == -1) {	// not in tag
                if(c == '#') {
                    if(i == len -1) continue;

                    if(!isTagChar(text.charAt(i + 1))) {
                        continue;
                    }

                    if(i == 0 || !isTagChar(text.charAt(i - 1))) {
                        ts = i; // mark tag start
                    }
                } else {
                    qbuf.append(StringUtils.isPuncChar(c) ? ' ' : c);
                }
            } else {
                if(!isTagChar(c)) {
                    tags.add(text.substring(ts + 1, i).toLowerCase()); // add tag
                    ts = -1; // mark no tag
                    qbuf.append(' ');
                } else if(i == len - 1) {
                    tags.add(text.substring(ts + 1).toLowerCase()); // add tag
                }
            }
        }

        return new String[] {qbuf.toString(), CollectionUtils.concat(tags, ",")};
    }

    private boolean isTagChar(char c) {
        return !Character.isWhitespace(c) && !StringUtils.isPuncChar(c);
    }

    public void postLike(Long postId, int actionType, Consumer<Integer> consumer) {
        PostLikeBody body = new PostLikeBody();
        body.setPostId(postId);
        body.setActionType(actionType);

        postApi.postLike(body).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(rt -> {
            if(ApiUtils.isSuccess(rt)) {
                Integer likes = (Integer)( (Map)rt.getRetData() ).get("likes");
                consumer.accept(likes);
            } else {
                handleApiFailure(rt.getRetMsg(), AppConstants.EVENT_POST_LIKE_ERR);
            }
        }, throwable -> {
            handleApiErr(throwable, AppConstants.EVENT_POST_LIKE_ERR);
        });
    }

    public void listByUser(Long userId, Integer page, Integer size) {
        Integer begin = page * size, count = size;

        postApi.listByUser(userId, begin, count).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(rt -> {
            if(ApiUtils.isSuccess(rt)) {
                Event e = Event.builder().name(AppConstants.EVENT_POST_USER_LIST_RT)
                        .extra1(page).extra2(rt.getPosts()).build();
                EventBus.publish(e);
            } else {
                handleApiFailure(rt.getRetMsg(), AppConstants.EVENT_POST_USER_LIST_ERR, page);
            }
        }, throwable -> {
            handleApiErr(throwable, AppConstants.EVENT_POST_USER_LIST_ERR, page);
        });
    }

    public void likeByUser(Long userId, Integer page, Integer size) {
        Integer begin = page * size, count = size;

        postApi.likeByUser(userId, begin, count).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(rt -> {
            if(ApiUtils.isSuccess(rt)) {
                Event e = Event.builder().name(AppConstants.EVENT_POST_LIKE_LIST_RT)
                        .extra1(page).extra2(rt.getPosts()).build();
                EventBus.publish(e);
            } else {
                handleApiFailure(rt.getRetMsg(), AppConstants.EVENT_POST_LIKE_LIST_ERR, page);
            }
        }, throwable -> {
            handleApiErr(throwable, AppConstants.EVENT_POST_LIKE_LIST_ERR, page);
        });
    }

    public void postDetail(Long postId, Integer postType, Consumer<Post> consumer) {
        postApi.postDetail(postId, postType).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(rt -> {
            if(ApiUtils.isSuccess(rt)) {
                consumer.accept(rt.getPost());
            } else {
                handleApiFailure(rt.getRetMsg(), AppConstants.EVENT_POST_DTL_ERR);
            }
        }, throwable -> {
            handleApiErr(throwable, AppConstants.EVENT_POST_DTL_ERR);
        });
    }

}














