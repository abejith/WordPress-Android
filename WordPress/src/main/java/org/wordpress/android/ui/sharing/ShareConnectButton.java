package org.wordpress.android.ui.sharing;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;

import org.wordpress.android.R;
import org.wordpress.android.widgets.WPTextView;

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

        updateView(context);
    }

    private void updateView(Context context) {
        int backColorResId;
        int foreColorResId;
        int captionResId;

        switch (mConnectState) {
            case CONNECT:
                backColorResId = R.color.blue_medium;
                foreColorResId = R.color.white;
                captionResId = R.string.share_btn_connect;
                break;
            case DISCONNECT:
                backColorResId = R.color.grey_lighten_20;
                foreColorResId = R.color.grey_darken_20;
                captionResId = R.string.share_btn_disconnect;
                break;
            case RECONNECT:
                backColorResId = R.color.orange_jazzy;
                foreColorResId = R.color.white;
                captionResId = R.string.share_btn_reconnect;
                break;
            default:
                return;
        }

        int backColor = context.getResources().getColor(backColorResId);
        int foreColor = context.getResources().getColor(foreColorResId);
        setBackgroundColor(backColor);
        setTextColor(foreColor);
        setText(captionResId);
        setPadding(mPaddingHorz, mPaddingVert, mPaddingHorz, mPaddingVert);
    }

    public ConnectState getConnectState() {
        return mConnectState;
    }

    public void setConnectState(ConnectState newState) {
        if (newState != null && !newState.equals(mConnectState)) {
            mConnectState = newState;
            updateView(getContext());
        }
    }
}