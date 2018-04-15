package me.codetalk.flowapp.post.view.util;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import me.codetalk.flowapp.R;
import me.codetalk.flowapp.fnd.model.entity.TagPosition;
import me.codetalk.flowapp.post.view.TagLinkMovementMethod;
import me.codetalk.flowapp.post.view.TagSearchSpan;
import me.codetalk.util.JsonUtils;
import me.codetalk.util.ViewUtils;

/**
 * Created by guobxu on 2018/1/27.
 */

public class PostViewUtils {

    public static void displayTaggedContent(String content, String tagPosList, Context context, TextView textView) {
        textView.setText(content, TextView.BufferType.SPANNABLE);
        textView.setMovementMethod(new TagLinkMovementMethod());

        Spannable spannable = (Spannable)textView.getText();
        List<TagPosition> tags = JsonUtils.fromJsonArray(tagPosList, TagPosition.class);
        for(TagPosition tag : tags) {
            int normalColor = ViewUtils.getColor(context, R.color.colorAccent),
                    pressedColor = ViewUtils.getColor(context, R.color.colorAccentPressed),
                    bgColor = Color.TRANSPARENT;
            TagSearchSpan tagSpan = new TagSearchSpan(tag.getText(), normalColor, pressedColor, bgColor, context);
            spannable.setSpan(tagSpan, tag.getStart(), tag.getEnd(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    public static void updateLikeAction(Integer liked, ImageButton btnLike, TextView textLikeCount) {
        if(liked != null && liked == 1) {
            btnLike.setImageResource(R.drawable.ic_thumb_up_accent_18dp);
            textLikeCount.setTextColor(ViewUtils.getColor(R.color.colorAccent));
        } else {
            btnLike.setImageResource(R.drawable.ic_thumb_up_darkgray_18dp);
            textLikeCount.setTextColor(ViewUtils.getColor(R.color.colorDarkGray));
        }
    }

}
