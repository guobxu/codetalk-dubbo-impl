package me.codetalk.flowapp.post.activity;

import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.codetalk.annotation.EnableProfile;
import me.codetalk.annotation.EventSupport;
import me.codetalk.custom.view.ImageSpinnerButton;
import me.codetalk.event.Event;
import me.codetalk.flowapp.AppConstants;
import me.codetalk.flowapp.R;
import me.codetalk.flowapp.activity.BaseActivity;
import me.codetalk.flowapp.post.model.CommentApiModel;
import me.codetalk.flowapp.post.model.entity.Mention;
import me.codetalk.util.CollectionUtils;
import me.codetalk.util.JsonUtils;
import me.codetalk.util.ViewUtils;

import static me.codetalk.flowapp.AppConstants.EVENT_COMMENT_CREATE_ERR;
import static me.codetalk.flowapp.AppConstants.EVENT_COMMENT_CREATE_RT;

@EnableProfile
@EventSupport(unregisterWhenStop = true)
public class CommentCreateActivity extends BaseActivity {

    @BindView(R.id.cc_text_replylist)
    TextView textReplyList;

    @BindView(R.id.cc_comment_content)
    EditText inputComment;

    @BindView(R.id.cc_btn_done)
    ImageSpinnerButton btnDone;

    // models
    private CommentApiModel commentApiModel;

    private Long postReply;
    private Long commentReply;
    private List<Mention> mentionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_create);

        ButterKnife.bind(this);

        // models
        Context appContext = getApplicationContext();
        commentApiModel = CommentApiModel.getInstance(appContext);

        // Intent Data
        Intent intent = getIntent();
        postReply = intent.getLongExtra("post_id", -1);
        commentReply = intent.getLongExtra("comment_id", -1);
        mentionList = JsonUtils.fromJsonArray(intent.getStringExtra("mention_list"), Mention.class);
        removeFromMention(mentionList, getUserId());

        // Views
        textReplyList.setText(getMentionText(mentionList));
        inputComment.addTextChangedListener(new CommentTextWatcher());

        btnDone.setOnClickListener(view -> {
            actionCreateComment();
        });
    }

    private String getMentionText(List<Mention> mentionList) {
        StringBuffer buf = new StringBuffer();

        for(int i = 0, size = mentionList.size(); i < size; i++) {
            buf.append(AppConstants.CHAR_MENTION);
            buf.append(mentionList.get(i).getUserLogin());

            if(i != size - 1) {
                buf.append(" ");
            }
        }

        return buf.toString();
    }

    private void actionCreateComment() {
        btnDone.setLoading(true);

        String content = inputComment.getText().toString().trim();
        if(postReply != -1) {
            commentApiModel.replyPost(postReply, content);
        } else if(commentReply != -1) {
            commentApiModel.replyComment(commentReply, content);
        }

    }

    private void removeFromMention(List<Mention> mentionList, Long userId) {
        for(int i = 0; i < mentionList.size(); i++) {
            Mention m = mentionList.get(i);
            if(userId.equals(m.getUserId())) {
                mentionList.remove(i);

                return;
            }
        }
    }

    private void validateActionButton() {
        String content = inputComment.getText().toString().trim();
        if(content.length() == 0) {
            btnDone.setEnabled(false);
            return;
        }

        btnDone.setEnabled(true);
    }

    class CommentTextWatcher implements TextWatcher {

        @Override
        public void afterTextChanged(Editable editable) {
            validateActionButton();
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

    }

    @Override
    public void handleSuccess(Event e) {
        String name = e.name();

        if(EVENT_COMMENT_CREATE_RT.equals(name)) {
            btnDone.setLoading(false);

            Long cmntId = (Long)e.extra1();
            String content = (String)e.extra2();

            // TODO
            finish();
        }
    }

    @Override
    public void handleFail(Event e) {
        super.handleFail(e);

        String name = e.name();
        if(EVENT_COMMENT_CREATE_ERR.equals(name)) {
            btnDone.setLoading(false);
        }
    }

}














