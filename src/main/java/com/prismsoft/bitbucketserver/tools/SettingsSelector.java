package com.prismsoft.bitbucketserver.tools;

import com.atlassian.bitbucket.repository.Repository;
import com.prismsoft.bitbucketserver.ImmutableJandiSettings;
import com.prismsoft.bitbucketserver.JandiGlobalSettingsService;
import com.prismsoft.bitbucketserver.JandiSettings;
import com.prismsoft.bitbucketserver.JandiSettingsService;

public class SettingsSelector {

  private JandiGlobalSettingsService jandiGlobalSettingsService;
  private JandiSettings jandiSettings;
  private JandiSettings resolvedJandiSettings;

  public SettingsSelector(JandiSettingsService jandiSettingsService, JandiGlobalSettingsService jandiGlobalSettingsService,
      Repository repository) {
    this.jandiGlobalSettingsService = jandiGlobalSettingsService;
    this.jandiSettings = jandiSettingsService.getJandiSettings(repository);
    this.setResolvedJandiSettings();
  }

  public JandiSettings getResolvedJandiSettings() {
    return this.resolvedJandiSettings;
  }

  private void setResolvedJandiSettings() {
    resolvedJandiSettings = new ImmutableJandiSettings(
        jandiSettings.isJandiNotificationsOverrideEnabled(),
        jandiSettings.isJandiNotificationsOverrideEnabled() ? jandiSettings.isJandiNotificationsEnabled()
            : jandiGlobalSettingsService.getJandiNotificationsEnabled(),
        jandiSettings.isJandiNotificationsOverrideEnabled() ? jandiSettings.isJandiNotificationsOpenedEnabled()
            : jandiGlobalSettingsService.getJandiNotificationsOpenedEnabled(),
        jandiSettings.isJandiNotificationsOverrideEnabled() ? jandiSettings.isJandiNotificationsReopenedEnabled()
            : jandiGlobalSettingsService.getJandiNotificationsReopenedEnabled(),
        jandiSettings.isJandiNotificationsOverrideEnabled() ? jandiSettings.isJandiNotificationsUpdatedEnabled()
            : jandiGlobalSettingsService.getJandiNotificationsUpdatedEnabled(),
        jandiSettings.isJandiNotificationsOverrideEnabled() ? jandiSettings.isJandiNotificationsApprovedEnabled()
            : jandiGlobalSettingsService.getJandiNotificationsApprovedEnabled(),
        jandiSettings.isJandiNotificationsOverrideEnabled() ? jandiSettings.isJandiNotificationsUnapprovedEnabled()
            : jandiGlobalSettingsService.getJandiNotificationsUnapprovedEnabled(),
        jandiSettings.isJandiNotificationsOverrideEnabled() ? jandiSettings.isJandiNotificationsDeclinedEnabled()
            : jandiGlobalSettingsService.getJandiNotificationsDeclinedEnabled(),
        jandiSettings.isJandiNotificationsOverrideEnabled() ? jandiSettings.isJandiNotificationsMergedEnabled()
            : jandiGlobalSettingsService.getJandiNotificationsMergedEnabled(),
        jandiSettings.isJandiNotificationsOverrideEnabled() ? jandiSettings.isJandiNotificationsCommentedEnabled()
            : jandiGlobalSettingsService.getJandiNotificationsCommentedEnabled(),
        jandiSettings.isJandiNotificationsOverrideEnabled() ? jandiSettings.isJandiNotificationsEnabledForPush()
            : jandiGlobalSettingsService.getJandiNotificationsEnabledForPush(),
        jandiSettings.isJandiNotificationsOverrideEnabled() ? jandiSettings.isJandiNotificationsEnabledForPersonal()
            : jandiGlobalSettingsService.getJandiNotificationsEnabledForPersonal(),
        jandiSettings.isJandiNotificationsOverrideEnabled() ? jandiSettings.isJandiNotificationsNeedsWorkEnabled()
            : jandiGlobalSettingsService.getJandiNotificationsNeedsWorkEnabled(),
        jandiSettings.isJandiNotificationsOverrideEnabled() ? jandiSettings.getNotificationLevel()
            : jandiGlobalSettingsService.getNotificationLevel(),
        jandiSettings.isJandiNotificationsOverrideEnabled() ? jandiSettings.getNotificationPrLevel()
            : jandiGlobalSettingsService.getNotificationPrLevel(),
        jandiSettings.isJandiNotificationsOverrideEnabled() ? jandiSettings.getJandiWebHookUrl()
            : jandiGlobalSettingsService.getWebHookUrl()
    );
  }

}
