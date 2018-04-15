package me.codetalk.flowapp.post.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import me.codetalk.annotation.EventSupport;
import me.codetalk.event.Event;
import me.codetalk.flowapp.AppConstants;
import me.codetalk.flowapp.R;
import me.codetalk.flowapp.fragment.BaseFragment;
import me.codetalk.flowapp.post.adapter.PostListAdapter;
import me.codetalk.flowapp.post.model.PostApiModel;
import me.codetalk.flowapp.post.model.entity.Post;
import me.codetalk.util.StringUtils;
import me.codetalk.util.ViewUtils;

import static me.codetalk.flowapp.AppConstants.APP_ID;
import static me.codetalk.flowapp.AppConstants.EVENT_POST_LIKE_LIST_ERR;
import static me.codetalk.flowapp.AppConstants.EVENT_POST_LIKE_LIST_RT;
import static me.codetalk.flowapp.AppConstants.EVENT_POST_USER_LIST_ERR;
import static me.codetalk.flowapp.AppConstants.EVENT_POST_LIST_RELOAD;
import static me.codetalk.flowapp.AppConstants.EVENT_POST_USER_LIST_RT;
import static me.codetalk.flowapp.AppConstants.EVENT_POST_SEARCH_ERR;
import static me.codetalk.flowapp.AppConstants.EVENT_POST_SEARCH_RT;
import static me.codetalk.flowapp.AppConstants.EVENT_USERTAG_UPDATE_RT;

/**
 *
 * by guobxu - 2017/12/24
 *
 */
@EventSupport(unregisterWhenStop = true)
public class PostListFragment extends BaseFragment {

    public static final int MODE_TIMELINE = 1;          // user timeline
    public static final int MODE_BY_SEARCH = 2;         // search by keyword or tag
    public static final int MODE_LIST_BY_USER = 3;      // post list of user
    public static final int MODE_LIKE_BY_USER = 4;      // post liked by user

    private int mode;

    // Views
    private RecyclerView postListView;

    private SwipeRefreshLayout layoutRefresh;

    private PostListAdapter postListAdapter;

    // Models
    private PostApiModel postApiModel;

    // Variables
    private int page = 0;       // current page

    private boolean hasMore = true;

    private String initialSearch;   // initial search
    private String lastSearch;      // last search, set when successfully searched

    // Time Thresholds
    private Long refreshThreshold;      // set at initial search & each time refreshed
    private Long loadMoreThreshold;     // set at initial search

    private boolean inSearch = false;

    // 指定用户的列表
    private Long userId;

    // Constructors
    public PostListFragment() {}

    public static PostListFragment timeline() {
        PostListFragment fragment = new PostListFragment();
        fragment.mode = MODE_TIMELINE;

        return fragment;
    }

    public static PostListFragment search(String initialSearch) {
        PostListFragment fragment = new PostListFragment();
        fragment.mode = MODE_BY_SEARCH;
        fragment.initialSearch = initialSearch;

        return fragment;
    }

    public static PostListFragment listByUser(Long userId) {
        PostListFragment fragment = new PostListFragment();
        fragment.mode = MODE_LIST_BY_USER;
        fragment.userId = userId;

        return fragment;
    }

    public static PostListFragment likeByUser(Long userId) {
        PostListFragment fragment = new PostListFragment();
        fragment.mode = MODE_LIKE_BY_USER;
        fragment.userId = userId;

        return fragment;
    }

    public void searchByText(String text) {
        if(inSearch || StringUtils.isBlank(text)) return;

        page = 0;
        hasMore = true;
        inSearch = true;

        postApiModel.search(text, null, System.currentTimeMillis(), page, AppConstants.POST_PAGE_SIZE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_post_list, container, false);

        Context context = getContext();

        // refresh layout
        layoutRefresh = rootView.findViewById(R.id.layout_refresh_post_list);
        layoutRefresh.setOnRefreshListener(() -> {
            refreshPostList();
        });
        layoutRefresh.setColorSchemeColors(ViewUtils.getColor(context, R.color.colorAccent), ViewUtils.getColor(context, R.color.colorAccentPressed));
        layoutRefresh.setEnabled(false); // enable only when first search success

        // post list view
        postListView = rootView.findViewById(R.id.list_post);
        LinearLayoutManager layoutManager = setDefaultLayout(postListView, true);

        postListAdapter = new PostListAdapter(getContext());
        postListView.setAdapter(postListAdapter);

        // post list - scroll
        postListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastVisibleItemPos = layoutManager.findLastVisibleItemPosition(),
                    lastCmpVisibleItemPost = layoutManager.findLastCompletelyVisibleItemPosition();

                int lastItemPos = postListAdapter.getItemCount() - 2;

                if(lastVisibleItemPos == lastItemPos && hasMore && !inSearch) {
                    loadMorePost();
                }
            }
        });

        // models
        Context appContext = getApplication().getApplicationContext();
        postApiModel = PostApiModel.getInstance(appContext);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initPostList();
    }

    private void initPostList() {
        if(mode == MODE_TIMELINE) {
            inSearch = true;
            postApiModel.timeline(null, System.currentTimeMillis(), page, AppConstants.POST_PAGE_SIZE);
        } else if(mode == MODE_BY_SEARCH) {
            searchByText(initialSearch);
        } else if(mode == MODE_LIST_BY_USER) {
            inSearch = true;
            postApiModel.listByUser(userId, page, AppConstants.POST_PAGE_SIZE);
        } else if(mode == MODE_LIKE_BY_USER) {
            inSearch = true;
            postApiModel.likeByUser(userId, page, AppConstants.POST_PAGE_SIZE);
        }
    }

    /**
     * 搜索最新帖子
     */
    private void refreshPostList() {
        if(inSearch || refreshThreshold == null) {
            layoutRefresh.setRefreshing(false);

            return;
        }

        inSearch = true;
        if(mode == MODE_TIMELINE) {
            postApiModel.timeline(refreshThreshold, System.currentTimeMillis(), null, null);
        } else if(mode == MODE_BY_SEARCH) {
            postApiModel.search(lastSearch, refreshThreshold, System.currentTimeMillis(), null, null);
        }
    }

    /**
     * 加载更多帖子
     */
    private void loadMorePost() {
        if(inSearch || !hasMore) return;

        inSearch = true;
        showLoading();
        if(mode == MODE_TIMELINE) {
            postApiModel.timeline(null, loadMoreThreshold, page, AppConstants.POST_PAGE_SIZE);
        } else if(mode == MODE_BY_SEARCH) {
            postApiModel.search(lastSearch, null, loadMoreThreshold, page, AppConstants.POST_PAGE_SIZE);
        }
    }

    private void reloadPost() {
        inSearch = true;
        showLoading();

        if(mode == MODE_TIMELINE) {
            postApiModel.timeline(null, System.currentTimeMillis(), page, AppConstants.POST_PAGE_SIZE);
        } else if(mode == MODE_BY_SEARCH) {
            postApiModel.search(initialSearch, null, System.currentTimeMillis(), page, AppConstants.POST_PAGE_SIZE);
        }
    }

    private void showLoaded() {
        postListAdapter.showLoaded();
    }

    private void showLoading() {
        postListAdapter.showLoading();
    }

    private void hideLoading() {
        postListAdapter.hideLoading();
    }

    private void showReload() {
        postListAdapter.showReload();
    }

    @Override
    public void handleSuccess(Event e) {
        String name = e.name();

        if(EVENT_POST_SEARCH_RT.equals(name)) {
            if(!inSearch) return; // only current fragment

            List<Post> posts = (List<Post>)e.extra2();
            Integer page = (Integer)e.extra1(), len = posts.size();

            if(page == null) {                      // load more
                if(posts.size() > 0) {
                    postListAdapter.prependPosts(posts);

                    postListView.scrollToPosition(0);
                }
                layoutRefresh.setRefreshing(false);
            } else if(page == 0) {
                postListAdapter.setPosts(posts);

                if(mode != MODE_LIST_BY_USER) layoutRefresh.setEnabled(true); // enable refresh
            } else if(len > 0) {
                postListAdapter.addPosts(posts);
            }

            // last search
            if(mode == MODE_BY_SEARCH) lastSearch = (String)e.extra3();

            // thresholds
            Long beginDate = (Long)e.extra4(), endDate = (Long)e.extra5();
            if(beginDate == null && endDate != null) { // load more: search before
                loadMoreThreshold = endDate;
                if(refreshThreshold == null || endDate > refreshThreshold) { // scenarior: user tag update
                    refreshThreshold = endDate;
                }
            }
            if(beginDate != null && endDate != null ) { // refresh: search between
                refreshThreshold = endDate;
            }

            // incr page
            if(page != null) { // load more: search before
                if (len == AppConstants.POST_PAGE_SIZE) {
                    this.page++;
                    hideLoading();
                } else {
                    hasMore = false;
                    showLoaded();
                }
            }

            inSearch = false;
        } else if(EVENT_POST_USER_LIST_RT.equals(name)) {
            processListSuccess(MODE_LIST_BY_USER, (List<Post>)e.extra2(), (Integer)e.extra1());
        } else if(EVENT_POST_LIKE_LIST_RT.equals(name)) {
            processListSuccess(MODE_LIKE_BY_USER, (List<Post>)e.extra2(), (Integer)e.extra1());
        } else if(EVENT_USERTAG_UPDATE_RT.equals(name)) {
            postListAdapter.clearPosts();

            if(mode == MODE_TIMELINE) {
                page = 0;

                postApiModel.timeline(null, System.currentTimeMillis(), page, AppConstants.POST_PAGE_SIZE);
            }
        } else if(EVENT_POST_LIST_RELOAD.equals(name)) {
            reloadPost();
        }
    }

    @Override
    public void handleFail(Event e) {
        super.handleFail(e);

        String name = e.name();
        if(EVENT_POST_SEARCH_ERR.equals(name)) {
            if(!inSearch) return; // only current fragment

            Integer page = (Integer)e.extra1();
            if(page == null) { // refresh
                layoutRefresh.setRefreshing(false);
            } else if(page == 0) {
                showReload();
            } else {
                hideLoading();
            }

            inSearch = false;
        } else if(EVENT_POST_USER_LIST_ERR.equals(name)) {
            processListFail(MODE_LIST_BY_USER, (Integer)e.extra1());
        } else if(EVENT_POST_LIKE_LIST_ERR.equals(name)) {
            processListFail(MODE_LIKE_BY_USER, (Integer)e.extra1());
        }
    }

    private void processListSuccess(int mode, List<Post> posts, Integer page) { // shared: list | like by user
        if(!inSearch || this.mode != mode) return; // only current fragment

        Integer len = posts.size();

        if(page == 0) {
            postListAdapter.setPosts(posts);
        } else if(len > 0) {
            postListAdapter.addPosts(posts);
        }

        // incr page
        if (len == AppConstants.POST_PAGE_SIZE) {
            this.page++;
            hideLoading();
        } else {
            hasMore = false;
            showLoaded();
        }

        inSearch = false;
    }

    private void processListFail(int mode, Integer page) {          // shared: list | like by user
        if(!inSearch || this.mode != mode) return; // only current fragment

        if(page == 0) {
            showReload();
        } else {
            hideLoading();
        }

        inSearch = false;
    }

}
























