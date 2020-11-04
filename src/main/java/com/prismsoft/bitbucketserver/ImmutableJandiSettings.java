package com.prismsoft.bitbucketserver;

public class ImmutableJandiSettings implements JandiSettings {

    private final boolean jandiNotificationsOverrideEnabled;
    private final boolean jandiNotificationsEnabled;
    private final boolean jandiNotificationsOpenedEnabled;
    private final boolean jandiNotificationsReopenedEnabled;
    private final boolean jandiNotificationsUpdatedEnabled;
    private final boolean jandiNotificationsApprovedEnabled;
    private final boolean jandiNotificationsUnapprovedEnabled;
    private final boolean jandiNotificationsDeclinedEnabled;
    private final boolean jandiNotificationsMergedEnabled;
    private final boolean jandiNotificationsCommentedEnabled;
    private final boolean jandiNotificationsEnabledForPush;
    private final boolean jandiNotificationsEnabledForPersonal;
    private final boolean jandiNotificationsNeedsWorkEnabled;
    private final NotificationLevel notificationLevel;
    private final NotificationLevel notificationPrLevel;
    private final String jandiWebHookUrl;


    public ImmutableJandiSettings(boolean jandiNotificationsOverrideEnabled,
                                  boolean jandiNotificationsEnabled,
                                  boolean jandiNotificationsOpenedEnabled,
                                  boolean jandiNotificationsReopenedEnabled,
                                  boolean jandiNotificationsUpdatedEnabled,
                                  boolean jandiNotificationsApprovedEnabled,
                                  boolean jandiNotificationsUnapprovedEnabled,
                                  boolean jandiNotificationsDeclinedEnabled,
                                  boolean jandiNotificationsMergedEnabled,
                                  boolean jandiNotificationsCommentedEnabled,
                                  boolean jandiNotificationsEnabledForPush,
                                  boolean jandiNotificationsEnabledForPersonal,
                                  boolean jandiNotificationsNeedsWorkEnabled,
                                  NotificationLevel notificationLevel,
                                  NotificationLevel notificationPrLevel,
                                  String jandiWebHookUrl) {
        this.jandiNotificationsOverrideEnabled = jandiNotificationsOverrideEnabled;
        this.jandiNotificationsEnabled = jandiNotificationsEnabled;
        this.jandiNotificationsOpenedEnabled = jandiNotificationsOpenedEnabled;
        this.jandiNotificationsReopenedEnabled = jandiNotificationsReopenedEnabled;
        this.jandiNotificationsUpdatedEnabled = jandiNotificationsUpdatedEnabled;
        this.jandiNotificationsApprovedEnabled = jandiNotificationsApprovedEnabled;
        this.jandiNotificationsUnapprovedEnabled = jandiNotificationsUnapprovedEnabled;
        this.jandiNotificationsDeclinedEnabled = jandiNotificationsDeclinedEnabled;
        this.jandiNotificationsMergedEnabled = jandiNotificationsMergedEnabled;
        this.jandiNotificationsCommentedEnabled = jandiNotificationsCommentedEnabled;
        this.jandiNotificationsEnabledForPush = jandiNotificationsEnabledForPush;
        this.jandiNotificationsEnabledForPersonal = jandiNotificationsEnabledForPersonal;
        this.jandiNotificationsNeedsWorkEnabled = jandiNotificationsNeedsWorkEnabled;
        this.notificationLevel = notificationLevel;
        this.notificationPrLevel = notificationPrLevel;
        this.jandiWebHookUrl = jandiWebHookUrl;

    }

    public boolean isJandiNotificationsOverrideEnabled() {
        return jandiNotificationsOverrideEnabled;
    }

    public boolean isJandiNotificationsEnabled() {
        return jandiNotificationsEnabled;
    }

    public boolean isJandiNotificationsOpenedEnabled() {
        return jandiNotificationsOpenedEnabled;
    }

    public boolean isJandiNotificationsReopenedEnabled() {
        return jandiNotificationsReopenedEnabled;
    }

    public boolean isJandiNotificationsUpdatedEnabled() {
        return jandiNotificationsUpdatedEnabled;
    }

    public boolean isJandiNotificationsApprovedEnabled() {
        return jandiNotificationsApprovedEnabled;
    }

    public boolean isJandiNotificationsUnapprovedEnabled() {
        return jandiNotificationsUnapprovedEnabled;
    }

    public boolean isJandiNotificationsDeclinedEnabled() {
        return jandiNotificationsDeclinedEnabled;
    }

    public boolean isJandiNotificationsMergedEnabled() {
        return jandiNotificationsMergedEnabled;
    }

    public boolean isJandiNotificationsCommentedEnabled() {
        return jandiNotificationsCommentedEnabled;
    }

    public boolean isJandiNotificationsEnabledForPush() {
        return jandiNotificationsEnabledForPush;
    }

    public boolean isJandiNotificationsEnabledForPersonal() {
        return jandiNotificationsEnabledForPersonal;
    }

    public boolean isJandiNotificationsNeedsWorkEnabled() {
        return jandiNotificationsNeedsWorkEnabled;
    }

    public NotificationLevel getNotificationLevel() {
        return notificationLevel;
    }

    public NotificationLevel getNotificationPrLevel() {
        return notificationPrLevel;
    }


    public String getJandiWebHookUrl() {
        return jandiWebHookUrl;
    }


    @Override
    public String toString() {
        return "ImmutableJandiSettings {" + "jandiNotificationsOverrideEnabled=" + jandiNotificationsOverrideEnabled +
                ", jandiNotificationsEnabled=" + jandiNotificationsEnabled +
                ", jandiNotificationsOpenedEnabled=" + jandiNotificationsOpenedEnabled +
                ", jandiNotificationsReopenedEnabled=" + jandiNotificationsReopenedEnabled +
                ", jandiNotificationsUpdatedEnabled=" + jandiNotificationsUpdatedEnabled +
                ", jandiNotificationsApprovedEnabled=" + jandiNotificationsApprovedEnabled +
                ", jandiNotificationsUnapprovedEnabled=" + jandiNotificationsUnapprovedEnabled +
                ", jandiNotificationsDeclinedEnabled=" + jandiNotificationsDeclinedEnabled +
                ", jandiNotificationsMergedEnabled=" + jandiNotificationsMergedEnabled +
                ", jandiNotificationsCommentedEnabled=" + jandiNotificationsCommentedEnabled +
                ", jandiNotificationsEnabledForPush=" + jandiNotificationsEnabledForPush +
                ", jandiNotificationsEnabledForPersonal=" + jandiNotificationsEnabledForPersonal +
                ", notificationLevel=" + notificationLevel +
                ", notificationPrLevel=" + notificationPrLevel +
                ", jandiWebHookUrl=" + jandiWebHookUrl +
                "}";
    }

}
