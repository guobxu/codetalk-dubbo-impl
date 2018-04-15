package me.codetalk.flowapp.main.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import me.codetalk.annotation.EnableProfile;
import me.codetalk.annotation.EventSupport;
import me.codetalk.auth.model.AuthApiModel;
import me.codetalk.flowapp.AppConstants;
import me.codetalk.flowapp.R;
import me.codetalk.flowapp.auth.activity.AuthActivity;
import me.codetalk.flowapp.activity.BaseActivity;
import me.codetalk.flowapp.post.activity.PostCreateActivity;
import me.codetalk.flowapp.fnd.activity.UserActivity;
import me.codetalk.flowapp.main.adapter.MainFragmentPagerAdapter;
import me.codetalk.event.Event;
import me.codetalk.auth.model.entity.UserInfo;
import me.codetalk.flowapp.fnd.activity.TagSearchActivity;
import me.codetalk.flowapp.fnd.model.UserTagApiModel;
import me.codetalk.flowapp.fnd.model.entity.Tag;
import me.codetalk.flowapp.search.activity.SearchActivity;

import static me.codetalk.flowapp.AppConstants.EVENT_LOGIN_SUCCESS;
import static me.codetalk.flowapp.AppConstants.EVENT_USERTAG_RT;

@EnableProfile
@EventSupport(unregisterWhenStop = false)
public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.main_drawer_layout)
    DrawerLayout drawerLayout;

    @BindView(R.id.main_toolbar)
    Toolbar toolbar;
    @BindView(R.id.main_toolbar_title)
    TextView toolbarTitle;

    @BindView(R.id.main_search_input)
    EditText inputSearch;

    @BindView(R.id.main_viewpager)
    ViewPager viewPager;

    @BindView(R.id.user_profile_img)
    CircleImageView profileImg;

    @BindView(R.id.main_nav_view)
    NavigationView navView;

    @BindView(R.id.main_btn_post)
    ImageButton btnCreatePost;

    // -- Drawer
    CircleImageView drawerProfileImg;

    TextView drawerUserName;

    TextView drawerUserLogin;

    // Models
    UserTagApiModel userTagApiModel;
    AuthApiModel authApiModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        // Drawer Nav
        View navHeaderView = navView.getHeaderView(0);
        drawerProfileImg = navHeaderView.findViewById(R.id.drawer_profile_img);
        drawerUserName = navHeaderView.findViewById(R.id.drawer_text_username);
        drawerUserLogin = navHeaderView.findViewById(R.id.drawer_text_userlogin);

        // toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        profileImg.setOnClickListener(v -> {
            if(isLoggedIn()) {
                drawerLayout.openDrawer(GravityCompat.START);
            } else {
                Intent intent = new Intent(MainActivity.this, AuthActivity.class);
                startActivity(intent);
            }
        });

        NavigationView navigationView = findViewById(R.id.main_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // tabs
        setupTabs();

        inputSearch.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent);
        });

        // Click
        btnCreatePost.setOnClickListener(view -> {
            Intent intent = new Intent(this, PostCreateActivity.class);
            startActivity(intent);
        });

        // Models
        Context appContext= getApplicationContext();
        userTagApiModel = UserTagApiModel.getInstance(appContext);
        authApiModel = AuthApiModel.getInstance(appContext);

        // user info
        if(isLoggedIn()) {
            authApiModel.userInfo(getUserId(), userInfo -> {
                flowApp.putUserInfo(userInfo);

                validateToolbar();

                userTagApiModel.userTag();
            });
        }
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        checkUserTags();
//    }

    private void validateToolbar() {
        showLoginProfile(profileImg);

        if(isLoggedIn()) {
            showUserProfile(drawerProfileImg, getUserProfile());

            UserInfo userInfo = getLoginUser();
            drawerUserName.setText(userInfo.getName());
            drawerUserLogin.setText(AppConstants.CHAR_MENTION + userInfo.getLogin());
        }
    }

    private void setupTabs() {
        MainFragmentPagerAdapter homePagerAdapter = new MainFragmentPagerAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(homePagerAdapter);

        TabLayout tabLayout = findViewById(R.id.main_tab);
        tabLayout.setupWithViewPager(viewPager);

        // tabs - icon
        for(int i = 0;i < tabLayout.getTabCount(); i++) {
            int iconId = -1;
            switch(i) {
                case 0:
                    iconId = R.drawable.sel_tab_home_32dp;
                    break;
                case 1:
                    iconId = R.drawable.sel_tab_search_32dp;
                    break;
                case 2:
                    iconId = R.drawable.sel_tab_mesg_32dp;
            }

            tabLayout.getTabAt(i).setIcon(iconId);
        }

        // select event
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch(tab.getPosition()) {
                    case 0:
                        showTitle(getString(R.string.title_home));
                        break;
                    case 1:
                        showSearch();
                        break;
                    case 2:
                        showTitle(getString(R.string.title_mesg));
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
    }

    private void showTitle(String title) {
        toolbarTitle.setText(title);
        toolbarTitle.setVisibility(View.VISIBLE);

        inputSearch.clearFocus();
        inputSearch.setVisibility(View.GONE);
    }

    private void showSearch() {
        toolbarTitle.setVisibility(View.GONE);
        inputSearch.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_item_account) {
            Intent intent = new Intent(this, UserActivity.class);
            intent.putExtra("user_id", getUserId());
            startActivity(intent);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    // Event Bus
    @Override
    protected void handleSuccess(Event event) {
        String name = event.name();
        if(EVENT_LOGIN_SUCCESS.equals(name)) {
            // handle login success
        } else if(EVENT_USERTAG_RT.equals(name)) {
            List<Tag> userTags = (List<Tag>)event.extra1();
            flowApp.setUserTags(userTags);

            checkUserTags();
        }
    }

    private void checkUserTags() {
        Set<String> userTags = flowApp.getTagSet();
        if(userTags != null && userTags.isEmpty()) {
            Intent intent = new Intent(MainActivity.this, TagSearchActivity.class);
            startActivity(intent);
        }
    }

}
