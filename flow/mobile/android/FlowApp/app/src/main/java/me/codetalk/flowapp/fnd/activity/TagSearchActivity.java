package me.codetalk.flowapp.fnd.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.codetalk.annotation.EnableProfile;
import me.codetalk.annotation.EventSupport;
import me.codetalk.custom.view.SpinnerButton;
import me.codetalk.event.Event;
import me.codetalk.flowapp.AppConstants;
import me.codetalk.flowapp.R;
import me.codetalk.flowapp.activity.BaseActivity;
import me.codetalk.flowapp.fnd.adapter.TagSearchAdapter;
import me.codetalk.flowapp.fnd.model.TagApiModel;
import me.codetalk.flowapp.fnd.model.UserTagApiModel;
import me.codetalk.flowapp.fnd.model.entity.Tag;

import static me.codetalk.flowapp.AppConstants.EVENT_TAG_SELECT_CHANGE;
import static me.codetalk.flowapp.AppConstants.EVENT_USERTAG_UPDATE_RT;

@EnableProfile
@EventSupport
public class TagSearchActivity extends BaseActivity implements View.OnClickListener {

    public static final int TAG_FETCH_COUNT = 50;

    @BindView(R.id.tagsearch_search_input)
    EditText inputSearch;

    @BindView(R.id.tagsearch_tag_list)
    RecyclerView tagList;

    @BindView(R.id.tagsearch_save)
    SpinnerButton btnSave;

    @BindView(R.id.tagsearch_text_sel_tagcount)
    TextView textTagCount;

    @BindView(R.id.tagsearch_btn_clear)
    ImageButton btnClear;

    TagSearchAdapter tagSearchAdapter;

    TagApiModel tagApiModel;

    UserTagApiModel userTagApiModel;

    // API call
    static final long API_CALL_DELAY = 1000L;
    long lastInput = -1L;
    private String lastq = null;
    boolean inTagQuery = false;

    Timer tagSearchTimer = new Timer();
    TimerTask tagSearchTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_search);

        ButterKnife.bind(this);

        // tag list view
        setupTagList();

        refreshTagCountAndButton();

        // Views
        inputSearch.addTextChangedListener(new SearchTextWatcher());

        btnClear.setOnClickListener(this);
        btnSave.setOnClickListener(this);

        // Models
        Context appContext = getApplicationContext();
        tagApiModel = TagApiModel.getInstance(appContext);
        userTagApiModel = UserTagApiModel.getInstance(appContext);

        // fetch top tags
        scheduleTagSearch("", 0);
    }

    private void setupTagList() {
        // views
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setFlexWrap(FlexWrap.WRAP);

        tagList.setLayoutManager(layoutManager);

        // user tags
        Set<String> userTags = flowApp.getTagSet();
        tagSearchAdapter = new TagSearchAdapter(this, userTags == null ? new HashSet<>() : userTags);

        tagList.setAdapter(tagSearchAdapter);
    }

    private void refreshTagCountAndButton() {
        int count = tagSearchAdapter.getSelectCount();

        textTagCount.setText(String.valueOf(count));

        btnSave.setEnabled(count > 0);
    }

    @Override
    public void handleSuccess(Event e) {
        String name = e.name();
        if(AppConstants.EVENT_TAG_LIST_RT.equals(name)) {
            inTagQuery = false;

            String q = ((String)e.extra2()).trim(), cq = inputSearch.getText().toString().trim();
            lastq = q;
            tagSearchAdapter.setTags((List<Tag>)e.extra1());

            if(!q.equals(cq)) {
                long delta = System.currentTimeMillis() - lastInput;
                if(delta >= API_CALL_DELAY) {
                    scheduleTagSearch(cq, 0);
                } else {
                    scheduleTagSearch(cq, API_CALL_DELAY - delta);
                }
            }
        } else if(EVENT_TAG_SELECT_CHANGE.equals(name)) {
            refreshTagCountAndButton();
        } else if(EVENT_USERTAG_UPDATE_RT.equals(name)) {
            userTagApiModel.userTag();

            finish();
        }
    }

    @Override
    public void handleFail(Event e) {
        super.handleFail(e);

        String name = e.name();
        if(AppConstants.EVENT_TAG_LIST_ERR.equals(name)) {
            inTagQuery = false;

            long delta = System.currentTimeMillis() - lastInput;
            String q = inputSearch.getText().toString().trim();
            if(delta >= API_CALL_DELAY) {
                scheduleTagSearch(q, 0);
            } else {
                scheduleTagSearch(q, API_CALL_DELAY - delta);
            }
        }
    }

    private void scheduleTagSearch(String q, long delay) {
        if(q.equals(lastq)) return;

        if(tagSearchTask != null) tagSearchTask.cancel();

        tagSearchTask = new TagSearchTask(q);
        tagSearchTimer.schedule(tagSearchTask, delay);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.spinner_btn_action:
                Set<String> tags = tagSearchAdapter.getSelectedTags();
                userTagApiModel.userTagUpdate(tags);
                break;
            case R.id.tagsearch_btn_clear:
                inputSearch.setText("");

                if(!inTagQuery) {
                    scheduleTagSearch("", API_CALL_DELAY);
                }
                break;
        }

    }

    class SearchTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void afterTextChanged(Editable editable) {
            lastInput = System.currentTimeMillis();
            String input = editable.toString();

            btnClear.setVisibility(input.length() > 0 ? View.VISIBLE : View.GONE);

            if(!inTagQuery) {
                scheduleTagSearch(input.trim(), API_CALL_DELAY);
            }
        }

    }

    class TagSearchTask extends TimerTask {

        private String q;

        TagSearchTask(String q) {
            this.q = q;
        }

        @Override
        public void run() {
            inTagQuery = true;
            tagApiModel.tagList(q, TAG_FETCH_COUNT);
        }
    }

}
