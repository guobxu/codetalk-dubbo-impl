package me.codetalk.flowapp.post.activity;

import android.animation.TimeInterpolator;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import me.codetalk.annotation.EnableProfile;
import me.codetalk.annotation.EventSupport;
import me.codetalk.flowapp.AppConstants;
import me.codetalk.flowapp.R;
import me.codetalk.flowapp.activity.BaseActivity;
import me.codetalk.flowapp.post.adapter.PostImageAdapter;
import me.codetalk.flowapp.post.api.request.PostCreateBody;
import me.codetalk.event.Event;
import me.codetalk.custom.view.ImageSpinnerButton;
import me.codetalk.model.FileUploadApiModel;
import me.codetalk.flowapp.post.model.PostApiModel;
import me.codetalk.util.FileUtils;
import me.codetalk.util.StringUtils;
import me.codetalk.util.ViewUtils;

import static me.codetalk.flowapp.AppConstants.UPLOAD_TYPE_POST_IMAGE;

@EnableProfile
@EventSupport
public class PostCreateActivity extends BaseActivity implements View.OnClickListener, NumberPicker.OnValueChangeListener {

    private FileUploadApiModel fileUploadApiModel = null;

    private PostApiModel postApiModel = null;

    @BindView(R.id.pc_btn_img)
    ImageButton btnAddImg;

    @BindView(R.id.pc_btn_poll)
    ImageButton btnPoll;

    @BindView(R.id.pc_btn_done)
    ImageSpinnerButton btnDone;

    @BindView(R.id.pc_post_img)
    RecyclerView listPostImg;

    @BindView(R.id.pc_text_content)
    EditText inputPostContent;

    @BindView(R.id.pc_progress_post_create)
    ProgressBar progressPostCreate;

    @BindView(R.id.pc_post_container)
    RelativeLayout layoutPost;

    @BindView(R.id.pc_poll_container)
    LinearLayout layoutPoll;

    @BindView(R.id.pc_option_container)
    LinearLayout layoutOption;

    @BindView(R.id.pc_btn_show_picker)
    ImageButton btnShowPicker;

    @BindView(R.id.pc_picker_container)
    LinearLayout layoutPickers;

    @BindView(R.id.picker_day)
    NumberPicker pickerDay;
    @BindView(R.id.picker_hour)
    NumberPicker pickerHour;
    @BindView(R.id.picker_min)
    NumberPicker pickerMin;

    @BindView(R.id.pc_text_poll_duration)
    TextView textPollDuration;

    String tagAddOption = null;
    String tagCancelPoll = null;
    String tagInputOption = null;

    PostTextWatcher textWatcher;

    PostImageAdapter postImageAdapter;

    // request codes
    private static final int REQ_CODE_PHOTO = 1;  // photo

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_create);

        ButterKnife.bind(this);

        // Click
        btnAddImg.setOnClickListener(view -> {
            if(!isExtStoragePermGranted()) {
                requestExtStoragePerm();
            } else {
                Intent photoPickIntent = new Intent(Intent.ACTION_PICK);
                photoPickIntent.setType("image/*");
                photoPickIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(photoPickIntent, REQ_CODE_PHOTO);
            }
        });

        // post imgs
        setupPostImgs();

        // views
        btnDone.setEnabled(false);
        textWatcher = new PostTextWatcher();
        inputPostContent.addTextChangedListener(textWatcher);

        btnDone.setOnClickListener(view -> {
            actionCreatePost();
        });

        btnPoll.setOnClickListener(view -> {
            actionShowPoll();
        });

        // btn show picker
        setupPicker();

        // tags
        tagAddOption = getString(R.string.tag_btn_add_option);
        tagCancelPoll = getString(R.string.tag_btn_cancel_poll);
        tagInputOption = getString(R.string.tag_input_poll_option);

        // models
        Context appContext = getApplicationContext();
        fileUploadApiModel = FileUploadApiModel.getInstance(appContext);
        postApiModel = PostApiModel.getInstance(appContext);

        // request perms
        requestExtStoragePerm();
    }

    private void setupPostImgs() {
        // views
        RecyclerView listImg = findViewById(R.id.pc_post_img);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        listImg.setLayoutManager(layoutManager);

        postImageAdapter = new PostImageAdapter(this);
        listImg.setAdapter(postImageAdapter);
    }

    private void setupPicker() {
        TimeInterpolator interpolator = new AccelerateDecelerateInterpolator();
        btnShowPicker.setOnClickListener(view -> {
            if(layoutPickers.getVisibility() == View.GONE) {
                btnShowPicker.animate().rotation(btnShowPicker.getRotation() + 180F).setInterpolator(interpolator);
                layoutPickers.setVisibility(View.VISIBLE);
            } else {
                btnShowPicker.animate().rotation(btnShowPicker.getRotation() - 180F).setInterpolator(interpolator);
                layoutPickers.setVisibility(View.GONE);
            }
        });

        pickerDay.setMinValue(0);
        pickerDay.setMaxValue(6);
        pickerDay.setValue(1);

        pickerHour.setMinValue(0);
        pickerHour.setMaxValue(23);

        pickerMin.setMinValue(0);
        pickerMin.setMaxValue(59);

        pickerDay.setOnValueChangedListener(this);
        pickerHour.setOnValueChangedListener(this);
        pickerMin.setOnValueChangedListener(this);

        setPollDuration();
    }

    private void actionCreatePost() {
        btnDone.setLoading(true);

        PostCreateBody body = new PostCreateBody();
        int postType = btnAddImg.isEnabled() ? AppConstants.POST_TYPE_BASIC : AppConstants.POST_TYPE_POLL;
        body.setType(postType);
        body.setContent(inputPostContent.getText().toString());

        if(postType == AppConstants.POST_TYPE_BASIC) {
            List<String> imgList = postImageAdapter.getPostImages();

            progressPostCreate.setProgress(0);
            // total size
            if (imgList.size() == 0) {
                progressPostCreate.setVisibility(View.GONE);

                postApiModel.createPost(body);
            } else {
                long total = 0;
                for (String filePath : imgList) {
                    total += new File(filePath).length();
                }
                addUpload(AppConstants.UPLOAD_TYPE_POST_IMAGE, total);

                // reset progress
                progressPostCreate.setVisibility(View.VISIBLE);

                postApiModel.createPost(body, imgList);
            }
        } else if(postType == AppConstants.POST_TYPE_POLL) {
            List<View> inputOptions = ViewUtils.getViewsByTag(layoutOption, getString(R.string.tag_input_poll_option));
            List<String> options = new ArrayList<>();
            for(View inputOption : inputOptions) {
                String optionText = ((EditText)inputOption).getText().toString().trim();

                if(StringUtils.isNotNull(optionText)) options.add(optionText);
            }

            long duration = ( pickerDay.getValue() * 24 * 60 + pickerHour.getValue() * 60 + pickerMin.getValue() ) * 60 * 1000L;

            Map<String, Object> pollData = new HashMap<>();
            pollData.put("poll_duration", duration);
            pollData.put("poll_options", options);
            body.setPollData(pollData);

            postApiModel.createPost(body);
        }
    }

    private void setPollDuration() {
        int day = pickerDay.getValue(), hour = pickerHour.getValue(), min = pickerMin.getValue();
        String text = String.format("%s天%s时%s分", day, hour, min);

        textPollDuration.setText(text);
    }

    @Override
    public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
        int day = pickerDay.getValue(), hour = pickerHour.getValue(), min = pickerMin.getValue();

        if(day == 0 && hour == 0) {
            if(min == 0) { // 全部置0, 设置为1小时
                pickerHour.setValue(1);
            } else if(min < 5) {
                pickerMin.setValue(5); // 最小5分钟
            }
        }

        setPollDuration();
    }

    private void actionShowPoll() {
        if(layoutPoll.getVisibility() == View.VISIBLE) return;

        layoutPoll.setVisibility(View.VISIBLE);

        LinearLayout layoutCancelPoll = inflatePollOptionView(true, false),
                layoutAddOption = inflatePollOptionView(false, true);
        layoutOption.addView(layoutCancelPoll);
        layoutOption.addView(layoutAddOption);

        ImageButton btnCancel = (ImageButton)ViewUtils.getFirstViewByTag(layoutOption, tagCancelPoll);
        // cancel poll
        btnCancel.setOnClickListener(view -> {
            layoutPoll.setVisibility(View.GONE);
            layoutOption.removeAllViews();
            btnAddImg.setEnabled(true);

            validateActionButton();
        });

        ImageButton addOptionBtn = (ImageButton)ViewUtils.getLastViewByTag(layoutOption, tagAddOption);
        addOptionBtn.setOnClickListener(this);

        btnAddImg.setEnabled(false);
        validateActionButton();
    }

    @Override
    public void onClick(View view) {
        String tagAddOption = getString(R.string.tag_btn_add_option);
        if(!tagAddOption.equals(view.getTag().toString())) return;

        view.setVisibility(View.GONE);

        List<View> optionViews = ViewUtils.getViewsByTag(layoutOption, tagAddOption);
        boolean allowAdd = (optionViews.size() == AppConstants.POLL_MAX_OPTS - 1 ? false : true);
        LinearLayout layoutOptionNew = inflatePollOptionView(false, allowAdd);

        layoutOption.addView(layoutOptionNew);
        layoutOptionNew.findViewWithTag(tagAddOption).setOnClickListener(this);
    }

    private LinearLayout inflatePollOptionView(boolean allowCancel, boolean allowAdd) {
        LinearLayout layout = (LinearLayout)LayoutInflater.from(this).inflate(R.layout.item_poll_option, layoutOption, false);
        ImageButton btnAddOption = (ImageButton)layout.findViewWithTag(tagAddOption),
                    btnPollCancel = (ImageButton)layout.findViewWithTag(tagCancelPoll);
        EditText inputOption = (EditText)layout.findViewWithTag(tagInputOption);

        if(allowCancel) {
            btnAddOption.setVisibility(View.GONE);
        } else if(allowAdd) {
            btnPollCancel.setVisibility(View.GONE);
        } else {
            btnAddOption.setVisibility(View.GONE);
            btnPollCancel.setVisibility(View.GONE);
        }

        inputOption.addTextChangedListener(textWatcher);

        return layout;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != RESULT_OK) return;

        if(requestCode == REQ_CODE_PHOTO) {
            List<String> imgList = new ArrayList<>();

            if (data.getClipData() != null) {
                ClipData mClipData = data.getClipData();
                for (int i = 0; i < mClipData.getItemCount(); i++) {
                    ClipData.Item item = mClipData.getItemAt(i);
                    Uri uri = item.getUri();

                    imgList.add(FileUtils.getRealPathFromURI(uri, this));
                }
            }

            postImageAdapter.addPostImages(imgList);

            // reset progress
            progressPostCreate.setProgress(0);
            progressPostCreate.setVisibility(View.GONE);

            // disable poll button
            if(imgList.size() > 0) btnPoll.setEnabled(false);
        }
    }

    class PostTextWatcher implements TextWatcher {

        @Override
        public void afterTextChanged(Editable editable) {
            validateActionButton();
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

    }

    private void validateActionButton() {
        String postContent = inputPostContent.getText().toString().trim();
        if(postContent.length() == 0) {
            btnDone.setEnabled(false);
            return;
        }

        // poll: at least two options
        if(!btnAddImg.isEnabled()) {
            List<View> optionViews = ViewUtils.getViewsByTag(layoutOption, tagInputOption);

            int i = 0;
            for(View optionView : optionViews) {
                if(!ViewUtils.isEmpty((EditText)optionView)) i++;
            }

            if(i < 2) {
                btnDone.setEnabled(false);
                return;
            }
        }

        btnDone.setEnabled(true);
    }

    // Event
    @Override
    protected void handleSuccess(Event event) {
        String name = event.name();
        if(AppConstants.EVENT_FILEUPLOAD_BYTES.equals(name)) {
            String type = (String)event.extra1();
            Integer bytes = (Integer)event.extra2();

            if(UPLOAD_TYPE_POST_IMAGE.equals(type)) {
                int percent = incrAndPercent(type, bytes);
                progressPostCreate.setProgress(percent);
            }
        } else if(AppConstants.EVENT_POST_CREATE_RT.equals(name)) {
            btnDone.setLoading(false);
            // TODO
        } else if(AppConstants.EVENT_POST_IMG_DELETE.equals(name)) {
            btnPoll.setEnabled(postImageAdapter.getItemCount() == 0);
        }
    }

    @Override
    protected void handleFail(Event event) {
        super.handleFail(event);

        String name = event.name();
        if(AppConstants.EVENT_POST_CREATE_ERR.equals(name)) {
            btnDone.setLoading(false);
            // TODO
        }
    }


}
