package com.prismsoft.bitbucketserver.components;

import com.atlassian.bitbucket.commit.*;
import com.atlassian.bitbucket.util.PageRequestImpl;
import com.atlassian.event.api.EventListener;
import com.atlassian.bitbucket.event.repository.RepositoryPushEvent;
import com.atlassian.bitbucket.nav.NavBuilder;
import com.atlassian.bitbucket.repository.RefChange;
import com.atlassian.bitbucket.repository.RefChangeType;
import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.bitbucket.util.Page;
import com.atlassian.bitbucket.util.PageRequest;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.prismsoft.bitbucketserver.ColorCode;
import com.prismsoft.bitbucketserver.JandiGlobalSettingsService;
import com.prismsoft.bitbucketserver.JandiSettings;
import com.prismsoft.bitbucketserver.JandiSettingsService;
import com.prismsoft.bitbucketserver.tools.ChannelSelector;
import com.prismsoft.bitbucketserver.tools.JandiAttachment;
import com.prismsoft.bitbucketserver.tools.JandiNotifier;
import com.prismsoft.bitbucketserver.tools.JandiPayload;
import com.prismsoft.bitbucketserver.tools.SettingsSelector;
import com.prismsoft.bitbucketserver.tools.WebHookSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RepositoryPushActivityListener {
    private static final Logger log = LoggerFactory.getLogger(RepositoryPushActivityListener.class);

    private final JandiGlobalSettingsService jandiGlobalSettingsService;
    private final JandiSettingsService jandiSettingsService;
    private final CommitService commitService;
    private final NavBuilder navBuilder;
    private final JandiNotifier jandiNotifier;
    private final Gson gson = new Gson();

    public RepositoryPushActivityListener(JandiGlobalSettingsService jandiGlobalSettingsService,
                                          JandiSettingsService jandiSettingsService,
                                          CommitService commitService,
                                          NavBuilder navBuilder,
                                          JandiNotifier jandiNotifier) {
        this.jandiGlobalSettingsService = jandiGlobalSettingsService;
        this.jandiSettingsService = jandiSettingsService;
        this.commitService = commitService;
        this.navBuilder = navBuilder;
        this.jandiNotifier = jandiNotifier;
    }

    @EventListener
    public void NotifyJandiChannel(RepositoryPushEvent event) {
        // find out if notification is enabled for this repo
        Repository repository = event.getRepository();
        JandiSettings jandiSettings = jandiSettingsService.getJandiSettings(repository);
        String globalHookUrl = jandiGlobalSettingsService.getWebHookUrl();

        SettingsSelector settingsSelector = new SettingsSelector(jandiSettingsService,  jandiGlobalSettingsService, repository);
        JandiSettings resolvedJandiSettings = settingsSelector.getResolvedJandiSettings();

        if (resolvedJandiSettings.isJandiNotificationsEnabledForPush()) {
            String localHookUrl = jandiSettings.getJandiWebHookUrl();

            //TODO: Do we need the WebHookSelector? Already resolved.
            WebHookSelector hookSelector = new WebHookSelector(globalHookUrl, localHookUrl);

            if (!hookSelector.isHookValid()) {
                log.error("There is no valid configured Web hook url! Reason: " + hookSelector.getProblem());
                return;
            }

            if (repository.isFork() && !resolvedJandiSettings.isJandiNotificationsEnabledForPersonal()) {
                // simply return silently when we don't want forks to get notifications unless they're explicitly enabled
                return;
            }

            String repoSlug = repository.getSlug();
            String projectName = repository.getProject().getKey();

            String repoPath = projectName + "/" + event.getRepository().getName();

            for (RefChange refChange : event.getRefChanges()) {
                String text;
                String ref = refChange.getRef().getId();
                NavBuilder.Repo repoUrlBuilder = navBuilder
                        .project(projectName)
                        .repo(repoSlug);

                String url = repoUrlBuilder
                        .commits()
                        .until(refChange.getRef().getId())
                        .buildAbsolute();

                List<Commit> myCommits = new LinkedList<Commit>();

                boolean isNewRef = refChange.getFromHash().equalsIgnoreCase("0000000000000000000000000000000000000000");
                boolean isDeleted = refChange.getToHash().equalsIgnoreCase("0000000000000000000000000000000000000000")
                    && refChange.getType() == RefChangeType.DELETE;
                if (isDeleted) {
                    // issue#4: if type is "DELETE" and toHash is all zero then this is a branch delete
                    if (ref.indexOf("refs/tags") >= 0) {
                        text = String.format("Tag `%s` deleted from repository <%s|`%s`>.",
                                ref.replace("refs/tags/", ""),
                                repoUrlBuilder.buildAbsolute(),
                                repoPath);
                    } else {
                        text = String.format("Branch `%s` deleted from repository <%s|`%s`>.",
                                ref.replace("refs/heads/", ""),
                                repoUrlBuilder.buildAbsolute(),
                                repoPath);
                    }
                } else if (isNewRef) {
                    // issue#3 if fromHash is all zero (meaning the beginning of everything, probably), then this push is probably
                    // a new branch or tag, and we want only to display the latest commit, not the entire history

                    if (ref.indexOf("refs/tags") >= 0) {
                        text = String.format("Tag <%s|`%s`> pushed on <%s|`%s`>. See <%s|commit list>.",
                                url,
                                ref.replace("refs/tags/", ""),
                                repoUrlBuilder.buildAbsolute(),
                                repoPath,
                                url
                                );
                    } else {
                        text = String.format("Branch <%s|`%s`> pushed on <%s|`%s`>. See <%s|commit list>.",
                                url,
                                ref.replace("refs/heads/", ""),
                                repoUrlBuilder.buildAbsolute(),
                                repoPath,
                                url);
                    }
                } else {
                    PageRequest pRequest = new PageRequestImpl(0, PageRequest.MAX_PAGE_LIMIT);
                    CommitsBetweenRequest commitsBetween = new CommitsBetweenRequest.Builder(repository).exclude(refChange.getFromHash()).include(refChange.getToHash()).build();
                    Page<Commit> commitList = commitService.getCommitsBetween(commitsBetween, pRequest);
                    myCommits.addAll(Lists.newArrayList(commitList.getValues()));

                    int commitCount = myCommits.size();
                    String commitStr = commitCount == 1 ? "commit" : "commits";

                    String branch = ref.replace("refs/heads/", "");
                    text = String.format("Push on <%s|`%s`> branch <%s|`%s`> by `%s <%s>` (%d %s). See <%s|commit list>.",
                            repoUrlBuilder.buildAbsolute(),
                            repoPath,
                            url,
                            branch,
                            event.getUser() != null ? event.getUser().getDisplayName() : "unknown user",
                            event.getUser() != null ? event.getUser().getEmailAddress() : "unknown email",
                            commitCount, commitStr,
                            url);
                }

                log.debug("text: " + text);

                // Figure out what type of change this is:

                JandiPayload payload = new JandiPayload();

                payload.setBody(text);
                payload.setConnectColor(ColorCode.GRAY.getCode());

                switch (resolvedJandiSettings.getNotificationLevel()) {
                    case COMPACT:
                        compactCommitLog(event, refChange, payload, repoUrlBuilder, myCommits);
                        break;
                    case VERBOSE:
                        verboseCommitLog(event, refChange, payload, repoUrlBuilder, text, myCommits);
                        break;
                    case MINIMAL:
                    default:
                        break;
                }

                // jandiSettings.getJandiChannelName might be:
                // - empty
                // - single channel value
                // - comma separated list of pairs (pattern, channel) eg: bugfix/.*->#test-bf,master->#test-master
                log.debug("#sending message to: " + payload.getBody());
                jandiNotifier.SendJandiNotification(hookSelector.getSelectedHook(), gson.toJson(payload));

            }
        }
    }

    private void compactCommitLog(RepositoryPushEvent event, RefChange refChange, JandiPayload payload, NavBuilder.Repo urlBuilder, List<Commit> myCommits) {
        if (myCommits.size() == 0) {
            // If there are no commits, no reason to add anything
        }
        JandiAttachment commits = new JandiAttachment();

        // Since the branch is now in the main commit line, title is not needed
        //commits.setTitle(String.format("[%s:%s]", event.getRepository().getName(), refChange.getRefId().replace("refs/heads", "")));
        StringBuilder attachmentFallback = new StringBuilder();
        StringBuilder commitListBlock = new StringBuilder();
        for (Commit c : myCommits) {
            String commitUrl = urlBuilder.commit(c.getId()).buildAbsolute();
            String firstCommitMessageLine = c.getMessage().split("\n")[0];

            // Note that we changed this to put everything in one attachment because otherwise it
            // doesn't get collapsed in jandi (the see more... doesn't appear)
            payload.addConnectInfo(JandiAttachment.createJandiAttachment(c.getDisplayId(), String.format("<%s|`%s`>: _%s_\n",
                commitUrl, firstCommitMessageLine, c.getAuthor().getName())));
        }

    }

    private void verboseCommitLog(RepositoryPushEvent event, RefChange refChange, JandiPayload payload, NavBuilder.Repo urlBuilder, String text, List<Commit> myCommits) {
        for (Commit c : myCommits) {
            payload.addConnectInfo(
                JandiAttachment.createJandiAttachment(c.getId(),
                    String.format("[%s:%s] - %s", event.getRepository().getName(), refChange.getRef().getId().replace("refs/heads", ""), c.getId())));
        }
    }
}
