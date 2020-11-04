package com.prismsoft.bitbucketserver.components;

import com.atlassian.bitbucket.avatar.AvatarRequest;
import com.atlassian.bitbucket.avatar.AvatarService;
import com.atlassian.bitbucket.comment.Comment;
import com.atlassian.bitbucket.event.pull.PullRequestActivityEvent;
import com.atlassian.bitbucket.event.pull.PullRequestCommentActivityEvent;
import com.atlassian.bitbucket.event.pull.PullRequestRescopeActivityEvent;
import com.atlassian.bitbucket.nav.NavBuilder;
import com.atlassian.bitbucket.project.Project;
import com.atlassian.bitbucket.pull.PullRequestParticipant;
import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.event.api.EventListener;
import com.google.gson.Gson;
import com.prismsoft.bitbucketserver.ColorCode;
import com.prismsoft.bitbucketserver.JandiGlobalSettingsService;
import com.prismsoft.bitbucketserver.JandiSettings;
import com.prismsoft.bitbucketserver.JandiSettingsService;
import com.prismsoft.bitbucketserver.NotificationLevel;
import com.prismsoft.bitbucketserver.tools.ChannelSelector;
import com.prismsoft.bitbucketserver.tools.JandiAttachment;
import com.prismsoft.bitbucketserver.tools.JandiNotifier;
import com.prismsoft.bitbucketserver.tools.JandiPayload;
import com.prismsoft.bitbucketserver.tools.SettingsSelector;
import com.prismsoft.bitbucketserver.tools.WebHookSelector;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PullRequestActivityListener {
    private static final Logger log = LoggerFactory.getLogger(PullRequestActivityListener.class);

    private final JandiGlobalSettingsService jandiGlobalSettingsService;
    private final JandiSettingsService jandiSettingsService;
    private final NavBuilder navBuilder;
    private final JandiNotifier jandiNotifier;
    private final AvatarService avatarService;
    private final AvatarRequest avatarRequest = new AvatarRequest(true, 16, true);
    private final Gson gson = new Gson();

    public PullRequestActivityListener(JandiGlobalSettingsService jandiGlobalSettingsService,
                                             JandiSettingsService jandiSettingsService,
                                             NavBuilder navBuilder,
                                             JandiNotifier jandiNotifier,
                                             AvatarService avatarService) {
        this.jandiGlobalSettingsService = jandiGlobalSettingsService;
        this.jandiSettingsService = jandiSettingsService;
        this.navBuilder = navBuilder;
        this.jandiNotifier = jandiNotifier;
        this.avatarService = avatarService;
    }

    @EventListener
    public void NotifyJandiChannel(PullRequestActivityEvent event) {
        // find out if notification is enabled for this repo
        Repository repository = event.getPullRequest().getToRef().getRepository();
        JandiSettings jandiSettings = jandiSettingsService.getJandiSettings(repository);
        String globalHookUrl = jandiGlobalSettingsService.getWebHookUrl();


        SettingsSelector settingsSelector = new SettingsSelector(jandiSettingsService,  jandiGlobalSettingsService, repository);
        JandiSettings resolvedJandiSettings = settingsSelector.getResolvedJandiSettings();

        if (resolvedJandiSettings.isJandiNotificationsEnabled()) {

            String localHookUrl = resolvedJandiSettings.getJandiWebHookUrl();
            WebHookSelector hookSelector = new WebHookSelector(globalHookUrl, localHookUrl);

            log.debug("SelectedHook: " + hookSelector.getSelectedHook());

            if (!hookSelector.isHookValid()) {
                log.error("There is no valid configured Web hook url! Reason: " + hookSelector.getProblem());
                return;
            }

            if (repository.isFork() && !resolvedJandiSettings.isJandiNotificationsEnabledForPersonal()) {
                // simply return silently when we don't want forks to get notifications unless they're explicitly enabled
                return;
            }

            String repoName = repository.getSlug();
            String projectName = repository.getProject().getKey();
            long pullRequestId = event.getPullRequest().getId();
            String userName = event.getUser() != null ? event.getUser().getDisplayName() : "unknown user";
            String activity = event.getActivity().getAction().name();
            String avatar = event.getUser() != null ? avatarService.getUrlForPerson(event.getUser(), avatarRequest) : "";

            NotificationLevel resolvedLevel = resolvedJandiSettings.getNotificationPrLevel();

            // Ignore RESCOPED PR events
            if (activity.equalsIgnoreCase("RESCOPED") && event instanceof PullRequestRescopeActivityEvent) {
                return;
            }

            if (activity.equalsIgnoreCase("OPENED") && !resolvedJandiSettings.isJandiNotificationsOpenedEnabled()) {
                return;
            }

            if (activity.equalsIgnoreCase("REOPENED") && !resolvedJandiSettings.isJandiNotificationsReopenedEnabled()) {
                return;
            }

            if (activity.equalsIgnoreCase("UPDATED") && !resolvedJandiSettings.isJandiNotificationsUpdatedEnabled()) {
                return;
            }

            if (activity.equalsIgnoreCase("APPROVED") && !resolvedJandiSettings.isJandiNotificationsApprovedEnabled()) {
                return;
            }

            if (activity.equalsIgnoreCase("UNAPPROVED") && !resolvedJandiSettings.isJandiNotificationsUnapprovedEnabled()) {
                return;
            }

            if (activity.equalsIgnoreCase("DECLINED") && !resolvedJandiSettings.isJandiNotificationsDeclinedEnabled()) {
                return;
            }

            if (activity.equalsIgnoreCase("MERGED") && !resolvedJandiSettings.isJandiNotificationsMergedEnabled()) {
                return;
            }

            if (activity.equalsIgnoreCase("COMMENTED") && !resolvedJandiSettings.isJandiNotificationsCommentedEnabled()) {
                return;
            }

            if (activity.equalsIgnoreCase("REVIEWED") && !resolvedJandiSettings.isJandiNotificationsNeedsWorkEnabled()) {
                return;
            }

            NavBuilder.PullRequest pullRequestUrlBuilder = navBuilder
                    .project(projectName)
                    .repo(repoName)
                    .pullRequest(pullRequestId);

            String url = pullRequestUrlBuilder
                    .overview()
                    .buildAbsolute();

            JandiPayload payload = new JandiPayload();

            switch (event.getActivity().getAction()) {
                case OPENED:
                    payload.setConnectColor(ColorCode.BLUE.getCode());
                    payload.setBody(String.format("opened pull request [%s](%s) by %s",
                        event.getPullRequest().getTitle(),
                        url,
                        userName));

                    if (resolvedLevel == NotificationLevel.COMPACT) {
                        payload.addConnectInfo(JandiAttachment.createJandiAttachment("Description", event.getPullRequest().getDescription()));
                    }

                    if (resolvedLevel == NotificationLevel.VERBOSE) {
                        this.addTargetMap(payload, event);
                        this.addReviewers(payload, event.getPullRequest().getReviewers());
                    }

                    break;

                case REOPENED:
                    payload.setConnectColor(ColorCode.BLUE.getCode());
                    payload.setBody(String.format("reopened pull request [%s](%s) by %s",
                        event.getPullRequest().getTitle(),
                        url,
                        userName));

                    if (resolvedLevel == NotificationLevel.COMPACT) {
                        payload.addConnectInfo(JandiAttachment.createJandiAttachment("Description", event.getPullRequest().getDescription()));
                    }

                    if (resolvedLevel == NotificationLevel.VERBOSE) {
                        this.addTargetMap(payload, event);
                        this.addReviewers(payload, event.getPullRequest().getReviewers());
                    }

                    break;

                case UPDATED:
                    payload.setConnectColor(ColorCode.PURPLE.getCode());
                    payload.setBody(String.format("updated pull request [%s](%s) by %s",
                        event.getPullRequest().getTitle(),
                        url,
                        userName));

                    if (resolvedLevel == NotificationLevel.COMPACT) {
                        payload.addConnectInfo(JandiAttachment.createJandiAttachment("Description", event.getPullRequest().getDescription()));
                    }

                    if (resolvedLevel == NotificationLevel.VERBOSE) {
                        this.addReviewers(payload, event.getPullRequest().getReviewers());
                    }
                    break;

                case APPROVED:
                    payload.setConnectColor(ColorCode.GREEN.getCode());
                    payload.setBody(String.format("approved pull request [%s](%s) by %s",
                        event.getPullRequest().getTitle(),
                        url,
                        userName));

                case UNAPPROVED:
                    payload.setConnectColor(ColorCode.RED.getCode());
                    payload.setBody(String.format("%s unapproved pull request [%s](%s) by %s",
                        event.getPullRequest().getTitle(),
                        url,
                        userName));

                    break;

                case DECLINED:
                    payload.setConnectColor(ColorCode.DARK_RED.getCode());
                    payload.setBody(String.format("declined pull request [%s](%s) by %s",
                        event.getPullRequest().getTitle(),
                        url,
                        userName));
                    break;

                case MERGED:
                    payload.setConnectColor(ColorCode.DARK_GREEN.getCode());
                    payload.setBody(String.format("merged pull request [%s](%s) by %s",
                        event.getPullRequest().getTitle(),
                        url,
                        userName));

                    if (resolvedLevel == NotificationLevel.VERBOSE) {
                        this.addTargetMap(payload, event);
                    }
                    break;

                case RESCOPED:
                    payload.setConnectColor(ColorCode.PURPLE.getCode());
                    payload.setBody(String.format("rescoped on pull request [%s](%s) by %s",
                        event.getPullRequest().getTitle(),
                        url,
                        userName));

                    break;

                case COMMENTED:
                    Comment comment = ((PullRequestCommentActivityEvent) event).getActivity().getComment();
                    String commentUrl = pullRequestUrlBuilder
                            .comment(comment.getId())
                            .buildAbsolute();

                    payload.setConnectColor(ColorCode.PALE_BLUE.getCode());

                    if (resolvedLevel == NotificationLevel.MINIMAL) {
                        payload.setBody(String.format("commented on pull request [%s](%s)",
                            event.getPullRequest().getTitle(),
                            commentUrl));
                    } else {
                        payload.setBody(String.format("commented on pull request [%s](%s) by %s",
                            event.getPullRequest().getTitle(),
                            commentUrl,
                            comment.getAuthor().getDisplayName()));
                    }

                    if (resolvedLevel == NotificationLevel.COMPACT || resolvedLevel == NotificationLevel.VERBOSE) {
                        this.addReviewers(payload, event.getPullRequest().getReviewers());
                        this.addComment(payload, ((PullRequestCommentActivityEvent) event).getActivity().getComment());
                    }
                    break;
                case REVIEWED:
                    payload.setConnectColor(ColorCode.ORANGE.getCode());
                    payload.setBody(String.format("reviewed pull request [%s](%s) by %s",
                        event.getPullRequest().getTitle(),
                        url,
                        userName));

                    break;
            }

            log.info("#sending message to: " + payload.getBody());
            jandiNotifier.SendJandiNotification(hookSelector.getSelectedHook(), gson.toJson(payload));
        }

    }

    private void addTargetMap(JandiPayload payload, PullRequestActivityEvent event) {
        Project fromProject = event.getPullRequest().getFromRef().getRepository().getProject();
        Repository fromRepository = event.getPullRequest().getFromRef().getRepository();

        NavBuilder.Project fromProjectBuilder = navBuilder
            .project(fromProject.getKey());
        NavBuilder.Repo fromRepoBuilder = fromProjectBuilder
            .repo(fromRepository.getSlug());

        payload.addConnectInfo(JandiAttachment.createJandiAttachment("Source",String.format("Project : [%s](%s)\nRepository : [%s](%s)\nBranch : %s",
            fromProject.getName(),
            fromProjectBuilder.buildAbsolute(),
            fromRepository.getName(),
            fromRepoBuilder.buildAbsolute(),
            event.getPullRequest().getFromRef().getDisplayId())));

        Project toProject = event.getPullRequest().getToRef().getRepository().getProject();
        Repository toRepository = event.getPullRequest().getToRef().getRepository();

        NavBuilder.Project toProjectBuilder = navBuilder
            .project(toProject.getKey());
        NavBuilder.Repo toRepoBuilder = fromProjectBuilder
            .repo(toRepository.getSlug());

        payload.addConnectInfo(JandiAttachment.createJandiAttachment("Destination", String.format("Project : [%s](%s)\nRepository : [%s](%s)\nBranch : %s",
            toProject.getName(),
            toProjectBuilder.buildAbsolute(),
            toRepository.getName(),
            toRepoBuilder.buildAbsolute(),
            event.getPullRequest().getToRef().getDisplayId())));
    }


    private void addReviewers(JandiPayload payload, Set<PullRequestParticipant> reviewers) {
        if (reviewers.isEmpty()) {
            return;
        }

        String names = "";
        for(PullRequestParticipant p : reviewers) {
            names += String.format("@%s ", p.getUser().getSlug());
        }
        payload.addConnectInfo(JandiAttachment.createJandiAttachment("Reviewers", names));
    }

    private void addComment(JandiPayload payload, Comment comment) {
        if (comment == null) {
            return;
        }

        payload.addConnectInfo(JandiAttachment.createJandiAttachment("Comment", comment.getText()));
    }
}
