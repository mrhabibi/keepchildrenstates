package com.mrhabibi.keepchildrenstates.sample;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.mrhabibi.keepchildrenstates.api.KeepChildrenStates;

import org.androidannotations.annotations.EViewGroup;

/**
 * Created by mrhabibi on 8/6/17.
 */
@KeepChildrenStates
@EViewGroup(R.layout.view_sample_view_group)
public class SampleViewGroup extends LinearLayout {
    public SampleViewGroup(Context context) {
        super(context);
    }

    public SampleViewGroup(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SampleViewGroup(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SampleViewGroup(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
