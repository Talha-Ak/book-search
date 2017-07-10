package com.example.talha.booksearch;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.NumberPicker;

/**
 * A {@link android.preference.Preference} that displays a number picker as a dialog.
 */
public class NumberPickerPreference extends DialogPreference {

    /**
     * These are the default values for the {@link NumberPicker} if they aren't specified in the settings_main.xml
     */
    public static final int DEFAULT_MAX_VALUE = 40;
    public static final int DEFAULT_MIN_VALUE = 1;
    public static final boolean DEFAULT_WRAP_SELECTOR_WHEEL = true;

    /**
     * These variables will store the values we specify in the settings_main.xml
     */
    private final int minValue;
    private final int maxValue;
    private final boolean wrapSelectorWheel;

    private NumberPicker picker;
    private int value;

    /**
     * This constructor creates an instance of our {@link NumberPickerPreference} by taking the
     * context and AttributeSet (the properties we specify in our xml file like minValue and maxValue)
     * and passing it along to the next constructor with a style.
     */
    public NumberPickerPreference(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.dialogPreferenceStyle);
    }

    /**
     * This constructor creates an instance of our {@link NumberPickerPreference}
     */
    public NumberPickerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        /* We call .obtainStyledAttributes on our context, and supply our attributes and our
           NumberPickerPreference attribute definitions. This gives us back a TypedArray, which
           allows us to get the information we set in our xml file. We call .getInteger on the
           TypedArray to get the value we set for min and maxValue, along with a default value. The
           default value is returned if we didn't set a custom min or max value in our xml layout.
           We call .getBoolean on the TypeArray to get the boolean for wrapSelectorWheel, along with
           our default value.*/
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NumberPickerPreference);
        minValue = a.getInteger(R.styleable.NumberPickerPreference_minValue, DEFAULT_MIN_VALUE);
        maxValue = a.getInteger(R.styleable.NumberPickerPreference_maxValue, DEFAULT_MAX_VALUE);
        wrapSelectorWheel = a.getBoolean(R.styleable.NumberPickerPreference_wrapSelectorWheel, DEFAULT_WRAP_SELECTOR_WHEEL);
        a.recycle();
    }

    /**
     * This method creates a layout that contains the NumberPicker. This layout is presented to the
     * user.
     */
    @Override
    protected View onCreateDialogView() {

        // A layout is created with layout_width and layout_height at MATCH_PARENT and CENTER
        // gravity.
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;

        // A NumberPicker is created and the FrameLayout this picker is in is set according to the
        // layout defined right above.
        picker = new NumberPicker(getContext());
        picker.setLayoutParams(layoutParams);

        // The picker is added to the FrameLayout, and the entire layout is returned.
        FrameLayout dialogView = new FrameLayout(getContext());
        dialogView.addView(picker);

        return dialogView;
    }

    /**
     * This method sets up the NumberPicker with the values we specified. */
    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        picker.setMinValue(minValue);
        picker.setMaxValue(maxValue);
        picker.setWrapSelectorWheel(wrapSelectorWheel);
        picker.setValue(getValue());
    }

    /**
     * This method runs when the user closes the dialog box. It retrieves the value the user chose,
     * it tells the OnPreferenceChangeListener that a value has been changed and it sets the value.
     */
    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            picker.clearFocus();
            int newValue = picker.getValue();
            if (callChangeListener(newValue)) {
                setValue(newValue);
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, minValue);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        setValue(restorePersistedValue ? getPersistedInt(minValue) : (Integer) defaultValue);
    }

    public void setValue(int value) {
        this.value = value;
        persistInt(this.value);
    }

    public int getValue() {
        return this.value;
    }
}