package com.arulvakku.ui.model;

public class Notification {

    private String mFromTime;
    private String mAlertTitle;
    private String mAlertMessage;
    private int mNotificationId;
    private String bigPictureUrl;
    private String iconUrl;

    public String getmFromTime() {
        return mFromTime;
    }

    public void setmFromTime(String mFromTime) {
        this.mFromTime = mFromTime;
    }

    public String getmAlertTitle() {
        return mAlertTitle;
    }

    public void setmAlertTitle(String mAlertTitle) {
        this.mAlertTitle = mAlertTitle;
    }

    public String getmAlertMessage() {
        return mAlertMessage;
    }

    public void setmAlertMessage(String mAlertMessage) {
        this.mAlertMessage = mAlertMessage;
    }

    public int getmNotificationId() {
        return mNotificationId;
    }

    public void setmNotificationId(int mNotificationId) {
        this.mNotificationId = mNotificationId;
    }

    public String getBigPictureUrl() {
        return bigPictureUrl;
    }

    public void setBigPictureUrl(String bigPictureUrl) {
        this.bigPictureUrl = bigPictureUrl;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
}
