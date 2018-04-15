package me.codetalk.flowapp.post.view;

import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * Created by guobxu on 2018/1/26.
 */

public class TagLinkMovementMethod extends LinkMovementMethod {

    private TagSearchSpan tagSpan;

    @Override
    public boolean onTouchEvent(TextView textView, Spannable spannable, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            tagSpan = getPressedSpan(textView, spannable, event);
            if (tagSpan != null) {
                tagSpan.setPressed(true);
                Selection.setSelection(spannable, spannable.getSpanStart(tagSpan),
                        spannable.getSpanEnd(tagSpan));
            }
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            TagSearchSpan tagSpan = getPressedSpan(textView, spannable, event);
            if (this.tagSpan != null && tagSpan != this.tagSpan) {
                this.tagSpan.setPressed(false);
                this.tagSpan = null;
                Selection.removeSelection(spannable);
            }
        } else {
            if (tagSpan != null) {
                tagSpan.setPressed(false);
                super.onTouchEvent(textView, spannable, event);
            }
            tagSpan = null;
            Selection.removeSelection(spannable);
        }
        return true;
    }

    private TagSearchSpan getPressedSpan(TextView textView, Spannable spannable, MotionEvent event) {

        int x = (int) event.getX() - textView.getTotalPaddingLeft() + textView.getScrollX();
        int y = (int) event.getY() - textView.getTotalPaddingTop() + textView.getScrollY();

        Layout layout = textView.getLayout();
        int position = layout.getOffsetForHorizontal(layout.getLineForVertical(y), x);

        TagSearchSpan[] link = spannable.getSpans(position, position, TagSearchSpan.class);
        TagSearchSpan tagSpan = null;
        if (link.length > 0 && positionWithinTag(position, spannable, link[0])) {
            tagSpan = link[0];
        }

        return tagSpan;
    }

    private boolean positionWithinTag(int position, Spannable spannable, Object tag) {
        return position >= spannable.getSpanStart(tag) && position <= spannable.getSpanEnd(tag);
    }
}
