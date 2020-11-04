package com.prismsoft.bitbucketserver;

public interface JandiGlobalSettingsService {

    // hook and channel name
    String getWebHookUrl();
    void setWebHookUrl(String value);

    // pull requests are enabled and pr events
    boolean getJandiNotificationsEnabled();
    void setJandiNotificationsEnabled(boolean value);

    boolean getJandiNotificationsOpenedEnabled();
    void setJandiNotificationsOpenedEnabled(boolean value);

    boolean getJandiNotificationsReopenedEnabled();
    void setJandiNotificationsReopenedEnabled(boolean value);

    boolean getJandiNotificationsUpdatedEnabled();
    void setJandiNotificationsUpdatedEnabled(boolean value);

    boolean getJandiNotificationsApprovedEnabled();
    void setJandiNotificationsApprovedEnabled(boolean value);

    boolean getJandiNotificationsUnapprovedEnabled();
    void setJandiNotificationsUnapprovedEnabled(boolean value);

    boolean getJandiNotificationsDeclinedEnabled();
    void setJandiNotificationsDeclinedEnabled(boolean value);

    boolean getJandiNotificationsMergedEnabled();
    void setJandiNotificationsMergedEnabled(boolean value);

    boolean getJandiNotificationsCommentedEnabled();
    void setJandiNotificationsCommentedEnabled(boolean value);

    // push notifications are enabled and push options
    boolean getJandiNotificationsEnabledForPush();
    void setJandiNotificationsEnabledForPush(boolean value);

    NotificationLevel getNotificationLevel();
    void setNotificationLevel(String value);

    NotificationLevel getNotificationPrLevel();
    void setNotificationPrLevel(String value);

    boolean getJandiNotificationsEnabledForPersonal();
    void setJandiNotificationsEnabledForPersonal(boolean value);

    boolean getJandiNotificationsNeedsWorkEnabled();
    void setJandiNotificationsNeedsWorkEnabled(boolean value);

}
