package me.codetalk.flowapp.search.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.codetalk.annotation.EnableProfile;
import me.codetalk.annotation.EventSupport;
import me.codetalk.custom.text.NoStartSpaceWatcher;
import me.codetalk.event.Event;
import me.codetalk.flowapp.R;
import me.codetalk.flowapp.activity.BaseActivity;
import me.codetalk.flowapp.post.fragment.PostListFragment;
import me.codetalk.flowapp.search.fragment.SearchHistoryFragment;
import me.codetalk.flowapp.search.model.entity.SearchItem;
import me.codetalk.util.JsonUtils;
import me.codetalk.util.StringUtils;

import static me.codetalk.flowapp.AppConstants.EVENT_ACTION_SEARCH;
import static me.codetalk.flowapp.AppConstants.MAX_SEARCH_ITEMS;
import static me.codetalk.flowapp.AppConstants.PREF_RECENT_RESEARCH;

@EnableProfile
@EventSupport
public class SearchActivity extends BaseActivity {

    @BindView(R.id.search_btn_clear)
    ImageButton btnClear;

    @BindView(R.id.search_input)
    EditText inputSearch;

    @BindView(R.id.search_create_post)
    ImageButton btnCreatePost;

    @BindView(R.id.search_fragment_container)
    FrameLayout fragmentContainer;

    private PostListFragment postListFragment;
    private SearchHistoryFragment searchHistoryFragment;

    private List<SearchItem> recentSearches;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ButterKnife.bind(this);

        // load recent searches
        String searchItemStr = prefModel.getString(PREF_RECENT_RESEARCH);
        if(searchItemStr == null) {
            recentSearches = new ArrayList<>();
        } else {
            recentSearches = JsonUtils.fromJsonArray(searchItemStr, SearchItem.class);
        }

        // search on create
        String searchText = getIntent().getStringExtra("search_text");
        if(searchText != null) {
            inputSearch.append(searchText + " ");

            search(searchText);
        } else {
            // async delay
            Completable.complete().delay(600, TimeUnit.MILLISECONDS)
                    .doOnComplete(() -> {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(inputSearch, InputMethodManager.SHOW_IMPLICIT);
                    }).subscribe();

            searchHistoryFragment =  new SearchHistoryFragment();
            replaceFragment(searchHistoryFragment);
        }

        // views
        inputSearch.addTextChangedListener(new NoStartSpaceWatcher(inputSearch));
        inputSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String searchText = inputSearch.getText().toString();

                    if(StringUtils.isNotBlank(searchText)) {
                        search(searchText.trim());
                    }
                }

                return false;
            }
        });

        inputSearch.addTextChangedListener(new SearchTextWatcher());

        btnClear.setOnClickListener(view -> {
            inputSearch.setText("");
        });

    }

    @Override
    public void onStop() {
        super.onStop();

        saveRecentSearches();
    }

    private void search(String searchText) {
        if(postListFragment == null) {
            postListFragment = PostListFragment.search(searchText);
            replaceFragment(postListFragment);
        } else {
            postListFragment.searchByText(searchText);
        }

        addNewSearch(searchText);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction txn = fragmentManager.beginTransaction();
        txn.replace(R.id.search_fragment_container, fragment);
//        txn.addToBackStack(null);

        txn.commit();
    }

    private void addNewSearch(String searchText) {
        SearchItem newItem = new SearchItem(searchText, System.currentTimeMillis());
        recentSearches.remove(newItem);

        recentSearches.add(0, newItem);

        if(recentSearches.size() > MAX_SEARCH_ITEMS) {
            recentSearches.remove(recentSearches.size() - 1);
        }
    }

    public void saveRecentSearches() {
        Observable.fromCallable((Callable) () -> {
            prefModel.putString(PREF_RECENT_RESEARCH, JsonUtils.toJson(recentSearches));

            return true; // DO NOT return null
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe();
    }

    public List<SearchItem> getRecentSearches() {
        return recentSearches;
    }

    class SearchTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void afterTextChanged(Editable editable) {
            String input = editable.toString();
            btnClear.setVisibility(input.length() > 0 ? View.VISIBLE : View.GONE);
        }

    }

    @Override
    public void handleSuccess(Event e) {
        String name = e.name();
        if(EVENT_ACTION_SEARCH.equals(name)) {
            String searchText = (String)e.extra1();
            if(StringUtils.isNotBlank(searchText)) {
                inputSearch.append(searchText + " ");

                search(searchText);
            }
        }
    }

}
