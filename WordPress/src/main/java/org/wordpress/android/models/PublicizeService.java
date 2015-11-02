package org.wordpress.android.models;

import org.wordpress.android.util.StringUtils;

public class PublicizeService {
    private String mName;
    private String mLabel;
    private String mDescription;
    private String mNoticon;
    private String mIconUrl;
    private String mConnectUrl;

    public String getName() {
        return StringUtils.notNullStr(mName);
    }
    public void setName(String name) {
        mName = StringUtils.notNullStr(name);
    }

    public String getLabel() {
        return StringUtils.notNullStr(mLabel);
    }
    public void setLabel(String label) {
        mLabel = StringUtils.notNullStr(label);
    }

    public String getDescription() {
        return StringUtils.notNullStr(mDescription);
    }
    public void setDescription(String description) {
        mDescription = StringUtils.notNullStr(description);
    }

    public String getNoticon() {
        return StringUtils.notNullStr(mNoticon);
    }
    public void setNoticon(String noticon) {
        mNoticon = StringUtils.notNullStr(noticon);
    }

    public String getIconUrl() {
        return StringUtils.notNullStr(mIconUrl);
    }
    public void setIconUrl(String url) {
        mIconUrl = StringUtils.notNullStr(url);
    }

    public String getConnectUrl() {
        return StringUtils.notNullStr(mConnectUrl);
    }
    public void setConnectUrl(String url) {
        mConnectUrl = StringUtils.notNullStr(url);
    }

    public boolean isSameAs(PublicizeService other) {
        return other != null
                && other.getName().equals(this.getName())
                && other.getLabel().equals(this.getLabel())
                && other.getDescription().equals(this.getDescription())
                && other.getNoticon().equals(this.getNoticon())
                && other.getIconUrl().equals(this.getIconUrl())
                && other.getConnectUrl().equals(this.getConnectUrl());
    }
}
