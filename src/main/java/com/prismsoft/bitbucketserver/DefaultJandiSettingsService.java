package com.prismsoft.bitbucketserver;

import static com.google.common.base.Preconditions.checkNotNull;

import com.atlassian.bitbucket.permission.Permission;
import com.atlassian.bitbucket.permission.PermissionValidationService;
import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import javax.annotation.Nonnull;

public class DefaultJandiSettingsService implements JandiSettingsService {

  static final ImmutableJandiSettings DEFAULT_CONFIG = new ImmutableJandiSettings(
      false,  // pr settings override enabled
      false,  // pull requests enabled
      true,   // opened
      true,   // reopened
      true,   // updated
      true,   // approved
      true,   // unapproved
      true,   // declined
      true,   // merged
      true,   // commented
      false,  // push enabled
      false,  // personal (forks) enabled
      false,  // needs work
      NotificationLevel.VERBOSE,
      NotificationLevel.VERBOSE,
      ""         // webhook override

  );

  static final String KEY_SLACK_OVERRIDE_NOTIFICATION = "jandiNotificationsOverrideEnabled";
  static final String KEY_SLACK_NOTIFICATION = "jandiNotificationsEnabled";
  static final String KEY_SLACK_OPENED_NOTIFICATION = "jandiNotificationsOpenedEnabled";
  static final String KEY_SLACK_REOPENED_NOTIFICATION = "jandiNotificationsReopenedEnabled";
  static final String KEY_SLACK_UPDATED_NOTIFICATION = "jandiNotificationsUpdatedEnabled";
  static final String KEY_SLACK_APPROVED_NOTIFICATION = "jandiNotificationsApprovedEnabled";
  static final String KEY_SLACK_UNAPPROVED_NOTIFICATION = "jandiNotificationsUnapprovedEnabled";
  static final String KEY_SLACK_DECLINED_NOTIFICATION = "jandiNotificationsDeclinedEnabled";
  static final String KEY_SLACK_MERGED_NOTIFICATION = "jandiNotificationsMergedEnabled";
  static final String KEY_SLACK_COMMENTED_NOTIFICATION = "jandiNotificationsCommentedEnabled";
  static final String KEY_SLACK_NOTIFICATION_PUSH = "jandiNotificationsEnabledForPush";
  static final String KEY_SLACK_NOTIFICATION_PERSONAL = "jandiNotificationsEnabledForPersonal";
  static final String KEY_SLACK_NOTIFICATION_NEEDS_WORK = "jandiNotificationsNeedsWorkEnabled";
  static final String KEY_SLACK_NOTIFICATION_LEVEL = "jandiNotificationLevel";
  static final String KEY_SLACK_NOTIFICATION_PR_LEVEL = "jandiNotificationPrLevel";
  static final String KEY_SLACK_CHANNEL_NAME = "jandiChannelName";
  static final String KEY_SLACK_WEBHOOK_URL = "jandiWebHookUrl";
  static final String KEY_SLACK_USER_NAME = "jandiUsername";
  static final String KEY_SLACK_ICON_URL = "jandiIconUrl";
  static final String KEY_SLACK_ICON_EMOJI = "jandiIconEmojil";

  private final PluginSettings pluginSettings;
  private final PermissionValidationService validationService;

  private final LoadingCache<Integer, JandiSettings> cache = CacheBuilder.newBuilder().build(
      new CacheLoader<Integer, JandiSettings>() {
        @Override
        public JandiSettings load(@Nonnull Integer repositoryId) {
          @SuppressWarnings("unchecked")
          Map<String, String> data = (Map) pluginSettings.get(repositoryId.toString());
          return data == null ? DEFAULT_CONFIG : deserialize(data);
        }
      }
  );

  public DefaultJandiSettingsService(PluginSettingsFactory pluginSettingsFactory,
      PermissionValidationService validationService) {
    this.validationService = validationService;
    this.pluginSettings = pluginSettingsFactory.createSettingsForKey(PluginMetadata.getPluginKey());
  }

  @Nonnull
  @Override
  public JandiSettings getJandiSettings(@Nonnull Repository repository) {
    validationService.validateForRepository(checkNotNull(repository, "repository"), Permission.REPO_READ);

    try {
      //noinspection ConstantConditions
      return cache.get(repository.getId());
    } catch (ExecutionException e) {
      throw Throwables.propagate(e.getCause());
    }
  }

  @Nonnull
  @Override
  public JandiSettings setJandiSettings(@Nonnull Repository repository, @Nonnull JandiSettings settings) {
    validationService.validateForRepository(checkNotNull(repository, "repository"), Permission.REPO_ADMIN);
    Map<String, String> data = serialize(checkNotNull(settings, "settings"));
    pluginSettings.put(Integer.toString(repository.getId()), data);
    cache.invalidate(repository.getId());

    return deserialize(data);
  }

  // note: for unknown reason, pluginSettngs.get() is not getting back the key for an empty string value
  // probably I don't know someyhing here. Applying a hack
  private Map<String, String> serialize(JandiSettings settings) {
    ImmutableMap<String, String> immutableMap = ImmutableMap.<String, String>builder()
        .put(KEY_SLACK_OVERRIDE_NOTIFICATION, Boolean.toString(settings.isJandiNotificationsOverrideEnabled()))
        .put(KEY_SLACK_NOTIFICATION, Boolean.toString(settings.isJandiNotificationsEnabled()))
        .put(KEY_SLACK_OPENED_NOTIFICATION, Boolean.toString(settings.isJandiNotificationsOpenedEnabled()))
        .put(KEY_SLACK_REOPENED_NOTIFICATION, Boolean.toString(settings.isJandiNotificationsReopenedEnabled()))
        .put(KEY_SLACK_UPDATED_NOTIFICATION, Boolean.toString(settings.isJandiNotificationsUpdatedEnabled()))
        .put(KEY_SLACK_APPROVED_NOTIFICATION, Boolean.toString(settings.isJandiNotificationsApprovedEnabled()))
        .put(KEY_SLACK_UNAPPROVED_NOTIFICATION, Boolean.toString(settings.isJandiNotificationsUnapprovedEnabled()))
        .put(KEY_SLACK_DECLINED_NOTIFICATION, Boolean.toString(settings.isJandiNotificationsDeclinedEnabled()))
        .put(KEY_SLACK_MERGED_NOTIFICATION, Boolean.toString(settings.isJandiNotificationsMergedEnabled()))
        .put(KEY_SLACK_COMMENTED_NOTIFICATION, Boolean.toString(settings.isJandiNotificationsCommentedEnabled()))
        .put(KEY_SLACK_NOTIFICATION_PUSH, Boolean.toString(settings.isJandiNotificationsEnabledForPush()))
        .put(KEY_SLACK_NOTIFICATION_PERSONAL, Boolean.toString(settings.isJandiNotificationsEnabledForPersonal()))
        .put(KEY_SLACK_NOTIFICATION_NEEDS_WORK, Boolean.toString(settings.isJandiNotificationsNeedsWorkEnabled()))
        .put(KEY_SLACK_NOTIFICATION_LEVEL, settings.getNotificationLevel().toString())
        .put(KEY_SLACK_NOTIFICATION_PR_LEVEL, settings.getNotificationPrLevel().toString())
        .put(KEY_SLACK_WEBHOOK_URL, settings.getJandiWebHookUrl().isEmpty() ? " " : settings.getJandiWebHookUrl())
        .build();

    return immutableMap;
  }

  // note: for unknown reason, pluginSettngs.get() is not getting back the key for an empty string value
  // probably I don't know something here. Applying a hack
  private JandiSettings deserialize(Map<String, String> settings) {
    return new ImmutableJandiSettings(
        Boolean.parseBoolean(settings.get(KEY_SLACK_OVERRIDE_NOTIFICATION)),
        Boolean.parseBoolean(settings.get(KEY_SLACK_NOTIFICATION)),
        Boolean.parseBoolean(settings.get(KEY_SLACK_OPENED_NOTIFICATION)),
        Boolean.parseBoolean(settings.get(KEY_SLACK_REOPENED_NOTIFICATION)),
        Boolean.parseBoolean(settings.get(KEY_SLACK_UPDATED_NOTIFICATION)),
        Boolean.parseBoolean(settings.get(KEY_SLACK_APPROVED_NOTIFICATION)),
        Boolean.parseBoolean(settings.get(KEY_SLACK_UNAPPROVED_NOTIFICATION)),
        Boolean.parseBoolean(settings.get(KEY_SLACK_DECLINED_NOTIFICATION)),
        Boolean.parseBoolean(settings.get(KEY_SLACK_MERGED_NOTIFICATION)),
        Boolean.parseBoolean(settings.get(KEY_SLACK_COMMENTED_NOTIFICATION)),
        Boolean.parseBoolean(settings.get(KEY_SLACK_NOTIFICATION_PUSH)),
        Boolean.parseBoolean(settings.get(KEY_SLACK_NOTIFICATION_PERSONAL)),
        Boolean.parseBoolean(settings.get(KEY_SLACK_NOTIFICATION_NEEDS_WORK)),
        settings.containsKey(KEY_SLACK_NOTIFICATION_LEVEL) ? NotificationLevel
            .valueOf(settings.get(KEY_SLACK_NOTIFICATION_LEVEL)) : NotificationLevel.VERBOSE,
        settings.containsKey(KEY_SLACK_NOTIFICATION_PR_LEVEL) ? NotificationLevel
            .valueOf(settings.get(KEY_SLACK_NOTIFICATION_PR_LEVEL)) : NotificationLevel.VERBOSE,
        Objects.toString(settings.get(KEY_SLACK_WEBHOOK_URL), " ").equals(" ") ? "" : settings.get(KEY_SLACK_WEBHOOK_URL)
    );
  }

}
