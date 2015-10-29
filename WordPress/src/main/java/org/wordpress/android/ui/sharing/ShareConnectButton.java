package org.wordpress.android.ui.sharing;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;

import org.wordpress.android.R;
import org.wordpress.android.widgets.WPTextView;

import java.util.Arrays;

/**
 * Connect/disconnect/reconnect button used in sharing list
 */
public class ShareConnectButton extends WPTextView {

    public enum ConnectState {
        CONNECT,
        DISCONNECT,
        RECONNECT
    }

    private ConnectState mConnectState = ConnectState.CONNECT;
    private int mPaddingHorz;
    private int mPaddingVert;

    public ShareConnectButton(Context context){
        super(context);
        initView(context);
    }

    public ShareConnectButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ShareConnectButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    private void initView(Context context) {
        mPaddingHorz = context.getResources().getDimensionPixelSize(R.dimen.margin_large);
        mPaddingVert = context.getResources().getDimensionPixelSize(R.dimen.margin_medium);
        setAllCaps(true);

        int sz = context.getResources().getDimensionPixelSize(R.dimen.text_sz_small);
        setTextSize(TypedValue.COMPLEX_UNIT_PX, sz);

        updateView();
    }

    private void updateView() {
        int normalColorResId;
        int pressedColorResId;
        int textColorResId;
        int captionResId;

        switch (mConnectState) {
            case CONNECT:
                normalColorResId = R.color.blue_medium;
                pressedColorResId = R.color.blue_light;
                textColorResId = R.color.white;
                captionResId = R.string.share_btn_connect;
                break;
            case DISCONNECT:
                normalColorResId = R.color.grey_lighten_20;
                pressedColorResId = R.color.grey_lighten_30;
                textColorResId = R.color.grey_darken_20;
                captionResId = R.string.share_btn_disconnect;
                break;
            case RECONNECT:
                normalColorResId = R.color.orange_jazzy;
                pressedColorResId = R.color.orange_fire;
                textColorResId = R.color.white;
                captionResId = R.string.share_btn_reconnect;
                break;
            default:
                return;
        }

        int normalColor = getContext().getResources().getColor(normalColorResId);
        int pressedColor = getContext().getResources().getColor(pressedColorResId);
        Drawable background;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            background = getRippleDrawable(normalColor, pressedColor);
        } else {
            background = getStateListDrawable(normalColor, pressedColor);
        }
        setBackgroundDrawable(background);

        int textColor = getContext().getResources().getColor(textColorResId);
        setTextColor(textColor);

        setText(captionResId);

        // specify padding here since setting the background removes it
        setPadding(mPaddingHorz, mPaddingVert, mPaddingHorz, mPaddingVert);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static Drawable getRippleDrawable(int normalColor, int pressedColor) {
        float[] outerRadii = new float[8];
        Arrays.fill(outerRadii, 3);

        RoundRectShape rc = new RoundRectShape(outerRadii, null, null);
        ShapeDrawable shape = new ShapeDrawable(rc);
        shape.getPaint().setColor(normalColor);
        return new RippleDrawable(
                ColorStateList.valueOf(pressedColor),
                shape,
                shape);
    }

    public static StateListDrawable getStateListDrawable(int normalColor, int pressedColor) {
        StateListDrawable states = new StateListDrawable();
        states.addState(new int[]{android.R.attr.state_pressed},   new ColorDrawable(pressedColor));
        states.addState(new int[]{android.R.attr.state_focused},   new ColorDrawable(pressedColor));
        states.addState(new int[]{android.R.attr.state_activated}, new ColorDrawable(pressedColor));
        states.addState(new int[]{}, new ColorDrawable(normalColor));
        return states;
    }

    public ConnectState getConnectState() {
        return mConnectState;
    }

    public void setConnectState(ConnectState newState) {
        if (newState != null && !newState.equals(mConnectState)) {
            mConnectState = newState;
            updateView();
        }
    }
}