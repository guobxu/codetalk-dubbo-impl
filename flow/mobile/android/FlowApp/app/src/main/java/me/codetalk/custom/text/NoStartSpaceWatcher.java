package me.codetalk.custom.text;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import me.codetalk.util.StringUtils;

/**
 * Created by guobxu on 2018/1/19.
 */

public class NoStartSpaceWatcher implements TextWatcher {

    private EditText editText;

    public NoStartSpaceWatcher(EditText editText) {
        this.editText = editText;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String text = editText.getText().toString();
        if (text.startsWith(" ")) {
            editText.setText(StringUtils.ltrim(text));
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}

