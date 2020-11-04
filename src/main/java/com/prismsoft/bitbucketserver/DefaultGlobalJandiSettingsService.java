package com.prismsoft.bitbucketserver;


import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.google.common.base.Strings;

public class DefaultGlobalJandiSettingsService implements JandiGlobalSettingsService {

  private static final String KEY_GLOBAL_SETTING_HOOK_URL = "stash2jandi.globalsettings.hookurl";
  private static final String KEY_GLOBAL_SETTING_NOTIFICATIONS_ENABLED = "stash2jandi.globalsettings.jandinotificationsenabled";
  private static final String KEY_GLOBAL_SETTING_NOTIFICATIONS_OPENED_ENABLED = "stash2jandi.globalsettings.jandinotificationsopenedenabled";
  private static final String KEY_GLOBAL_SETTING_NOTIFICATIONS_REOPENED_ENABLED = "stash2jandi.globalsettings.jandinotificationsreopenedenabled";
  private static final String KEY_GLOBAL_SETTING_NOTIFICATIONS_UPDATED_ENABLED = "stash2jandi.globalsettings.jandinotificationsupdatedenabled";
  private static final String KEY_GLOBAL_SETTING_NOTIFICATIONS_APPROVED_ENABLED = "stash2jandi.globalsettings.jandinotificationsapprovedenabled";
  private static final String KEY_GLOBAL_SETTING_NOTIFICATIONS_UNAPPROVED_ENABLED = "stash2jandi.globalsettings.jandinotificationsunapprovedenabled";
  private static final String KEY_GLOBAL_SETTING_NOTIFICATIONS_DECLINED_ENABLED = "stash2jandi.globalsettings.jandinotificationsdeclinedenabled";
  private static final String KEY_GLOBAL_SETTING_NOTIFICATIONS_MERGED_ENABLED = "stash2jandi.globalsettings.jandinotificationsmergedenabled";
  private static final String KEY_GLOBAL_SETTING_NOTIFICATIONS_COMMENTED_ENABLED = "stash2jandi.globalsettings.jandinotificationscommentedenabled";
  private static final String KEY_GLOBAL_SETTING_NOTIFICATIONS_LEVEL = "stash2jandi.globalsettings.jandinotificationslevel";
  private static final String KEY_GLOBAL_SETTING_NOTIFICATIONS_PR_LEVEL = "stash2jandi.globalsettings.jandinotificationsprlevel";
  private static final String KEY_GLOBAL_SETTING_NOTIFICATIONS_PUSH_ENABLED = "stash2jandi.globalsettings.jandinotificationspushenabled";
  private static final String KEY_GLOBAL_SETTING_NOTIFICATIONS_PERSONAL_ENABLED = "stash2jandi.globalsettings.jandinotificationspersonalenabled";
  private static final String KEY_GLOBAL_SETTING_NOTIFICATIONS_NEEDS_WORK_ENABLED = "stash2jandi.globalsettings.jandinotificationsneedsworkenabled";


  private final PluginSettings pluginSettings;

  public DefaultGlobalJandiSettingsService(PluginSettingsFactory pluginSettingsFactory) {
    this.pluginSettings = pluginSettingsFactory.createGlobalSettings();
  }

  @Override
  public String getWebHookUrl() {
    return getString(KEY_GLOBAL_SETTING_HOOK_URL);
  }

  @Override
  public void setWebHookUrl(String value) {
    if (!Strings.isNullOrEmpty(value)) {
      pluginSettings.put(KEY_GLOBAL_SETTING_HOOK_URL, value);
    } else {
      pluginSettings.put(KEY_GLOBAL_SETTING_HOOK_URL, null);
    }
  }

  @Override
  public boolean getJandiNotificationsEnabled() {
    return getBoolean(KEY_GLOBAL_SETTING_NOTIFICATIONS_ENABLED);
  }

  @Override
  public void setJandiNotificationsEnabled(boolean value) {
    setBoolean(KEY_GLOBAL_SETTING_NOTIFICATIONS_ENABLED, value);
  }

  @Override
  public boolean getJandiNotificationsOpenedEnabled() {
    return getBoolean(KEY_GLOBAL_SETTING_NOTIFICATIONS_OPENED_ENABLED);
  }

  @Override
  public void setJandiNotificationsOpenedEnabled(boolean value) {
    setBoolean(KEY_GLOBAL_SETTING_NOTIFICATIONS_OPENED_ENABLED, value);
  }

  @Override
  public boolean getJandiNotificationsReopenedEnabled() {
    return getBoolean(KEY_GLOBAL_SETTING_NOTIFICATIONS_REOPENED_ENABLED);
  }

  @Override
  public void setJandiNotificationsReopenedEnabled(boolean value) {
    setBoolean(KEY_GLOBAL_SETTING_NOTIFICATIONS_REOPENED_ENABLED, value);
  }

  @Override
  public boolean getJandiNotificationsUpdatedEnabled() {
    return getBoolean(KEY_GLOBAL_SETTING_NOTIFICATIONS_UPDATED_ENABLED);
  }

  @Override
  public void setJandiNotificationsUpdatedEnabled(boolean value) {
    setBoolean(KEY_GLOBAL_SETTING_NOTIFICATIONS_UPDATED_ENABLED, value);
  }

  @Override
  public boolean getJandiNotificationsApprovedEnabled() {
    return getBoolean(KEY_GLOBAL_SETTING_NOTIFICATIONS_APPROVED_ENABLED);
  }

  @Override
  public void setJandiNotificationsApprovedEnabled(boolean value) {
    setBoolean(KEY_GLOBAL_SETTING_NOTIFICATIONS_APPROVED_ENABLED, value);
  }

  @Override
  public boolean getJandiNotificationsUnapprovedEnabled() {
    return getBoolean(KEY_GLOBAL_SETTING_NOTIFICATIONS_UNAPPROVED_ENABLED);
  }

  @Override
  public void setJandiNotificationsUnapprovedEnabled(boolean value) {
    setBoolean(KEY_GLOBAL_SETTING_NOTIFICATIONS_UNAPPROVED_ENABLED, value);
  }

  @Override
  public boolean getJandiNotificationsDeclinedEnabled() {
    return getBoolean(KEY_GLOBAL_SETTING_NOTIFICATIONS_DECLINED_ENABLED);
  }

  @Override
  public void setJandiNotificationsDeclinedEnabled(boolean value) {
    setBoolean(KEY_GLOBAL_SETTING_NOTIFICATIONS_DECLINED_ENABLED, value);
  }

  @Override
  public boolean getJandiNotificationsMergedEnabled() {
    return getBoolean(KEY_GLOBAL_SETTING_NOTIFICATIONS_MERGED_ENABLED);
  }

  @Override
  public void setJandiNotificationsMergedEnabled(boolean value) {
    setBoolean(KEY_GLOBAL_SETTING_NOTIFICATIONS_MERGED_ENABLED, value);
  }

  @Override
  public boolean getJandiNotificationsCommentedEnabled() {
    return getBoolean(KEY_GLOBAL_SETTING_NOTIFICATIONS_COMMENTED_ENABLED);
  }

  @Override
  public void setJandiNotificationsCommentedEnabled(boolean value) {
    setBoolean(KEY_GLOBAL_SETTING_NOTIFICATIONS_COMMENTED_ENABLED, value);
  }

  @Override
  public boolean getJandiNotificationsEnabledForPush() {
    return getBoolean(KEY_GLOBAL_SETTING_NOTIFICATIONS_PUSH_ENABLED);
  }

  @Override
  public void setJandiNotificationsEnabledForPush(boolean value) {
    setBoolean(KEY_GLOBAL_SETTING_NOTIFICATIONS_PUSH_ENABLED, value);
  }

  @Override
  public boolean getJandiNotificationsEnabledForPersonal() {
    return getBoolean(KEY_GLOBAL_SETTING_NOTIFICATIONS_PERSONAL_ENABLED);
  }

  @Override
  public void setJandiNotificationsEnabledForPersonal(boolean value) {
    setBoolean(KEY_GLOBAL_SETTING_NOTIFICATIONS_PERSONAL_ENABLED, value);
  }

  @Override
  public boolean getJandiNotificationsNeedsWorkEnabled() {
    return getBoolean(KEY_GLOBAL_SETTING_NOTIFICATIONS_NEEDS_WORK_ENABLED);
  }

  @Override
  public void setJandiNotificationsNeedsWorkEnabled(boolean value) {
    setBoolean(KEY_GLOBAL_SETTING_NOTIFICATIONS_NEEDS_WORK_ENABLED, value);
  }

  @Override
  public NotificationLevel getNotificationLevel() {
    String value = getString(KEY_GLOBAL_SETTING_NOTIFICATIONS_LEVEL);
    if (value.isEmpty()) {
      return NotificationLevel.VERBOSE;
    } else {
      return NotificationLevel.valueOf(value);
    }
  }

  @Override
  public void setNotificationLevel(String value) {
    if (!Strings.isNullOrEmpty(value)) {
      pluginSettings.put(KEY_GLOBAL_SETTING_NOTIFICATIONS_LEVEL, value);
    } else {
      pluginSettings.put(KEY_GLOBAL_SETTING_NOTIFICATIONS_LEVEL, null);
    }
  }

  @Override
  public NotificationLevel getNotificationPrLevel() {
    String value = getString(KEY_GLOBAL_SETTING_NOTIFICATIONS_PR_LEVEL);
    if (value.isEmpty()) {
      return NotificationLevel.VERBOSE;
    } else {
      return NotificationLevel.valueOf(value);
    }
  }

  @Override
  public void setNotificationPrLevel(String value) {
    if (!Strings.isNullOrEmpty(value)) {
      pluginSettings.put(KEY_GLOBAL_SETTING_NOTIFICATIONS_PR_LEVEL, value);
    } else {
      pluginSettings.put(KEY_GLOBAL_SETTING_NOTIFICATIONS_PR_LEVEL, null);
    }
  }

  private String getString(String key) {
    Object value = pluginSettings.get(key);
    return null == value ? "" : value.toString();
  }

  private boolean getBoolean(String key) {
    return Boolean.parseBoolean((String) pluginSettings.get(key));
  }

  private void setBoolean(String key, Boolean value) {
    pluginSettings.put(key, value.toString());
  }
}
