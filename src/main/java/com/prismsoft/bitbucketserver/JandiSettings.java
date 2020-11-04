package com.prismsoft.bitbucketserver;

public interface JandiSettings {

    boolean isJandiNotificationsOverrideEnabled();
    boolean isJandiNotificationsEnabled();
    boolean isJandiNotificationsOpenedEnabled();
    boolean isJandiNotificationsReopenedEnabled();
    boolean isJandiNotificationsUpdatedEnabled();
    boolean isJandiNotificationsApprovedEnabled();
    boolean isJandiNotificationsUnapprovedEnabled();
    boolean isJandiNotificationsDeclinedEnabled();
    boolean isJandiNotificationsMergedEnabled();
    boolean isJandiNotificationsCommentedEnabled();
    boolean isJandiNotificationsEnabledForPush();
    boolean isJandiNotificationsEnabledForPersonal();
    boolean isJandiNotificationsNeedsWorkEnabled();
    NotificationLevel getNotificationLevel();
    NotificationLevel getNotificationPrLevel();
    String getJandiWebHookUrl();
}
