package me.codetalk.flowapp.fnd.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.codetalk.annotation.EnableProfile;
import me.codetalk.annotation.EventSupport;
import me.codetalk.auth.model.AuthApiModel;
import me.codetalk.flowapp.AppConstants;
import me.codetalk.flowapp.R;
import me.codetalk.flowapp.activity.BaseActivity;
import me.codetalk.annotation.ActivityFeature;
import me.codetalk.event.Event;
import me.codetalk.flowapp.auth.activity.AuthActivity;
import me.codetalk.flowapp.fnd.model.FollowApiModel;
import me.codetalk.flowapp.post.fragment.PostListFragment;
import me.codetalk.auth.model.entity.UserInfo;
import me.codetalk.util.StringUtils;

import static me.codetalk.annotation.ActivityFeature.HIDESTATUS;

@EnableProfile
@EventSupport
@ActivityFeature(value = HIDESTATUS)
public class UserActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.user_tab)
    TabLayout tabLayout;

    @BindView(R.id.user_viewpager)
    ViewPager viewPager;

    @BindView(R.id.user_bgcover)
    ImageView bgCoverImg;

    @BindView(R.id.user_name)
    TextView userNameText;

    @BindView(R.id.user_login)
    TextView userLoginText;

    @BindView(R.id.user_signature)
    TextView userSignText;

    @BindView(R.id.user_location)
    TextView userLocText;

    @BindView(R.id.user_site)
    TextView userSiteText;

    @BindView(R.id.user_edit_btn)
    Button userEditBtn;

    @BindView(R.id.user_btn_follow)
    Button btnFollow;

    @BindView(R.id.user_text_follow_count)
    TextView textFollowCount;

    @BindView(R.id.user_text_followed_count)
    TextView textFollowedCount;

    // models
    private AuthApiModel authApiModel;
    private FollowApiModel followApiModel;

    // properties
    private Long userId;
    private boolean followed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        ButterKnife.bind(this);

        // models
        Context appContext = flowApp.getApplicationContext();
        authApiModel = AuthApiModel.getInstance(appContext);
        followApiModel = FollowApiModel.getInstance(appContext);

        // init userInfo
        userId = getIntent().getLongExtra("user_id", -1);
        UserInfo userInfo = flowApp.getUserInfo(userId);
        if(userInfo != null) {
            showUserInfo(userInfo);
        } else {
            authApiModel.userInfo(userId, userRt -> {
                showUserInfo(userRt);

                flowApp.putUserInfo(userRt);
            });
        }

        // follow count
        followApiModel.count(userId, stat -> {
            textFollowCount.setText(String.valueOf(stat.getFollow()));
            textFollowedCount.setText(String.valueOf(stat.getFollowed()));
        });

        viewPager.setAdapter(new TabsAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);

        // Click
        if( isLoggedIn() && getUserId().equals(userId) ) {
            userEditBtn.setVisibility(View.VISIBLE);
            userEditBtn.setOnClickListener(this);
        } else {
            btnFollow.setVisibility(View.VISIBLE);
            btnFollow.setOnClickListener(this);

            if( isLoggedIn() ) {
                followApiModel.existFollow(userId, exists -> {
                    setFollowed(exists);
                });
            }
        }
    }

    private void showUserInfo(UserInfo userInfo) {
        if(userInfo == null) return;

        String bgCover = userInfo.getBgcover();
        if(bgCover != null) {
            loadImageUrl(bgCoverImg, bgCover, R.drawable.blank_user_bgcover);
        }

        userNameText.setText(userInfo.getName());
        userLoginText.setText(AppConstants.CHAR_MENTION + userInfo.getLogin());

        String signature = userInfo.getSignature();
        if(StringUtils.isNotNull(signature)) {
            userSignText.setText(signature);
        } else {
            userSignText.setVisibility(View.GONE);
        }

        String location = userInfo.getLocation(), site = userInfo.getSite();
        boolean showLocSite = false;
        if(StringUtils.isNotNull(location)) {
            userLocText.setText(location);
            showLocSite = true;
        } else {
            findViewById(R.id.user_location_group).setVisibility(View.GONE);
        }
        if(StringUtils.isNotNull(site)) {
            userSiteText.setText(site);
            showLocSite = true;
        } else {
            findViewById(R.id.user_site_group).setVisibility(View.GONE);
        }

        if(!showLocSite) {
            findViewById(R.id.user_location_site).setVisibility(View.GONE);
        }
    }

    private void setFollowed(boolean followed) {
        this.followed = followed;

        updateFollowButton();
    }

    private void updateFollowButton() {
        Resources resources = getResources();
        Drawable followDr = resources.getDrawable(R.drawable.sel_bg_accent_text_btn_8dp),
                unFollowDr = resources.getDrawable(R.drawable.sel_bg_default_text_btn_8dp);

        btnFollow.setBackground(followed ? followDr : unFollowDr);
        btnFollow.setText(followed ? R.string.unfollow : R.string.follow);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.user_edit_btn:
                Intent userEditIntent = new Intent(this, UserEditActivity.class);
                startActivity(userEditIntent);
                break;
            case R.id.user_btn_follow:
                if(!isLoggedIn()) {
                    Intent authIntent = new Intent(UserActivity.this, AuthActivity.class);
                    startActivity(authIntent);
                } else {
                    followApiModel.follow(userId, followed ? AppConstants.ACTION_UNFOLLOW : AppConstants.ACTION_FOLLOW, stat -> {
                        if(stat != null) {
                            textFollowCount.setText(String.valueOf(stat.getFollow()));
                            textFollowedCount.setText(String.valueOf(stat.getFollowed()));
                        }

                        setFollowed(!followed);
                    });
                }
        }
    }

    private class TabsAdapter extends FragmentPagerAdapter {

        int TAB_COUNT = 2;

        public TabsAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position) {
                case 0:
                    return PostListFragment.listByUser(userId);
                case 1:
                    return PostListFragment.likeByUser(userId);
            }

            return null;
        }

        @Override
        public int getCount() {
            return TAB_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch(position) {
                case 0:
                    return getString(R.string.tab_title_post);
                case 1:
                    return getString(R.string.tab_title_likes);
            }

            return null;
        }
    }

    @Override
    protected void handleSuccess(Event event) {
        String name = event.name();

    }

}
