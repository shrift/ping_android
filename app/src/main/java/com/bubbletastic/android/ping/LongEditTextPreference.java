package com.bubbletastic.android.ping;

import android.content.Context;
import android.preference.EditTextPreference;
import android.util.AttributeSet;

/**
 * Created by brendanmartens on 12/18/15.
 */
public class LongEditTextPreference extends EditTextPreference {
    public LongEditTextPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public LongEditTextPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public LongEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LongEditTextPreference(Context context) {
        super(context);
    }

    @Override
    protected String getPersistedString(String defaultReturnValue) {
        return String.valueOf(getPersistedLong(-1));
    }

    @Override
    protected boolean persistString(String value) {
        //Save the value as a long instead.
        return persistLong(Long.valueOf(value));
    }
}
