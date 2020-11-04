package com.prismsoft.bitbucketserver;

import com.atlassian.soy.renderer.SoyException;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import com.atlassian.bitbucket.AuthorisationException;
import com.atlassian.bitbucket.permission.Permission;
import com.atlassian.bitbucket.permission.PermissionValidationService;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import com.atlassian.bitbucket.i18n.I18nService;
import com.google.common.collect.ImmutableMap;
import com.prismsoft.bitbucketserver.soy.SelectFieldOptions;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class JandiGlobalSettingsServlet extends HttpServlet {
    private final PageBuilderService pageBuilderService;
    private final JandiGlobalSettingsService jandiGlobalSettingsService;
    private final SoyTemplateRenderer soyTemplateRenderer;
    private final PermissionValidationService validationService;
    private final I18nService i18nService;

    public JandiGlobalSettingsServlet(PageBuilderService pageBuilderService,
                                      JandiGlobalSettingsService jandiGlobalSettingsService,
                                      SoyTemplateRenderer soyTemplateRenderer,
                                      PermissionValidationService validationService,
                                      I18nService i18nService) {
        this.pageBuilderService = pageBuilderService;
        this.jandiGlobalSettingsService = jandiGlobalSettingsService;
        this.soyTemplateRenderer = soyTemplateRenderer;
        this.validationService = validationService;
        this.i18nService = i18nService;

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        try {
            validationService.validateForGlobal(Permission.SYS_ADMIN);
        } catch (AuthorisationException e) {
            // Skip form processing
            doGet(req, res);
            return;
        }

        jandiGlobalSettingsService.setWebHookUrl(req.getParameter("jandiGlobalWebHookUrl").trim());

        jandiGlobalSettingsService.setJandiNotificationsEnabled(bool(req, "jandiNotificationsEnabled"));
        jandiGlobalSettingsService.setJandiNotificationsOpenedEnabled(bool(req, "jandiNotificationsOpenedEnabled"));
        jandiGlobalSettingsService.setJandiNotificationsReopenedEnabled(bool(req, "jandiNotificationsReopenedEnabled"));
        jandiGlobalSettingsService.setJandiNotificationsUpdatedEnabled(bool(req, "jandiNotificationsUpdatedEnabled"));
        jandiGlobalSettingsService.setJandiNotificationsApprovedEnabled(bool(req, "jandiNotificationsApprovedEnabled"));
        jandiGlobalSettingsService.setJandiNotificationsUnapprovedEnabled(bool(req, "jandiNotificationsUnapprovedEnabled"));
        jandiGlobalSettingsService.setJandiNotificationsDeclinedEnabled(bool(req, "jandiNotificationsDeclinedEnabled"));
        jandiGlobalSettingsService.setJandiNotificationsMergedEnabled(bool(req, "jandiNotificationsMergedEnabled"));
        jandiGlobalSettingsService.setJandiNotificationsCommentedEnabled(bool(req, "jandiNotificationsCommentedEnabled"));

        NotificationLevel notificationLevel = NotificationLevel.VERBOSE;
        if (null != req.getParameter("jandiNotificationLevel")) {
            notificationLevel = NotificationLevel.valueOf(req.getParameter("jandiNotificationLevel"));
        }
        jandiGlobalSettingsService.setNotificationLevel(notificationLevel.toString());

        NotificationLevel notificationPrLevel = NotificationLevel.VERBOSE;
        if (null != req.getParameter("jandiNotificationPrLevel")) {
            notificationPrLevel = NotificationLevel.valueOf(req.getParameter("jandiNotificationPrLevel"));
        }
        jandiGlobalSettingsService.setNotificationPrLevel(notificationPrLevel.toString());

        jandiGlobalSettingsService.setJandiNotificationsEnabledForPush(bool(req, "jandiNotificationsEnabledForPush"));
        jandiGlobalSettingsService.setJandiNotificationsEnabledForPersonal(bool(req, "jandiNotificationsEnabledForPersonal"));
        jandiGlobalSettingsService.setJandiNotificationsNeedsWorkEnabled(bool(req, "jandiNotificationsNeedsWorkEnabled"));

        doGet(req, res);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doView(response);

    }

    private void doView(HttpServletResponse response)
            throws ServletException, IOException {

        validationService.validateForGlobal(Permission.ADMIN);

        String webHookUrl = jandiGlobalSettingsService.getWebHookUrl();
        Boolean jandiNotificationsEnabled = jandiGlobalSettingsService.getJandiNotificationsEnabled();
        Boolean jandiNotificationsOpenedEnabled = jandiGlobalSettingsService.getJandiNotificationsOpenedEnabled();
        Boolean jandiNotificationsReopenedEnabled = jandiGlobalSettingsService.getJandiNotificationsReopenedEnabled();
        Boolean jandiNotificationsUpdatedEnabled = jandiGlobalSettingsService.getJandiNotificationsUpdatedEnabled();
        Boolean jandiNotificationsApprovedEnabled = jandiGlobalSettingsService.getJandiNotificationsApprovedEnabled();
        Boolean jandiNotificationsUnapprovedEnabled = jandiGlobalSettingsService.getJandiNotificationsUnapprovedEnabled();
        Boolean jandiNotificationsDeclinedEnabled = jandiGlobalSettingsService.getJandiNotificationsDeclinedEnabled();
        Boolean jandiNotificationsMergedEnabled = jandiGlobalSettingsService.getJandiNotificationsMergedEnabled();
        Boolean jandiNotificationsCommentedEnabled = jandiGlobalSettingsService.getJandiNotificationsCommentedEnabled();
        Boolean jandiNotificationsEnabledForPush = jandiGlobalSettingsService.getJandiNotificationsEnabledForPush();
        Boolean jandiNotificationsEnabledForPersonal = jandiGlobalSettingsService.getJandiNotificationsEnabledForPersonal();
        Boolean jandiNotificationsNeedsWorkEnabled = jandiGlobalSettingsService.getJandiNotificationsNeedsWorkEnabled();
        String notificationLevel = jandiGlobalSettingsService.getNotificationLevel().toString();
        String notificationPrLevel = jandiGlobalSettingsService.getNotificationPrLevel().toString();

        render(response,
                "bitbucketserver.page.jandi.global.settings.viewGlobalJandiSettings",
                ImmutableMap.<String, Object>builder()
                        .put("jandiGlobalWebHookUrl", webHookUrl)
                        .put("jandiNotificationsEnabled", jandiNotificationsEnabled)
                        .put("jandiNotificationsOpenedEnabled", jandiNotificationsOpenedEnabled)
                        .put("jandiNotificationsReopenedEnabled", jandiNotificationsReopenedEnabled)
                        .put("jandiNotificationsUpdatedEnabled", jandiNotificationsUpdatedEnabled)
                        .put("jandiNotificationsApprovedEnabled", jandiNotificationsApprovedEnabled)
                        .put("jandiNotificationsUnapprovedEnabled", jandiNotificationsUnapprovedEnabled)
                        .put("jandiNotificationsDeclinedEnabled", jandiNotificationsDeclinedEnabled)
                        .put("jandiNotificationsMergedEnabled", jandiNotificationsMergedEnabled)
                        .put("jandiNotificationsCommentedEnabled", jandiNotificationsCommentedEnabled)
                        .put("jandiNotificationsEnabledForPush", jandiNotificationsEnabledForPush)
                        .put("jandiNotificationsEnabledForPersonal", jandiNotificationsEnabledForPersonal)
                        .put("jandiNotificationsNeedsWorkEnabled", jandiNotificationsNeedsWorkEnabled)
                        .put("notificationLevel", notificationLevel)
                        .put("notificationPrLevel", notificationPrLevel)
                        .put("notificationLevels", new SelectFieldOptions(NotificationLevel.values()).toSoyStructure())
                        .build()
        );
    }

    private void render(HttpServletResponse response, String templateName, Map<String, Object> data)
            throws IOException, ServletException {
        pageBuilderService.assembler().resources().requireContext("plugin.adminpage.jandi");
        response.setContentType("text/html;charset=UTF-8");
        try {
            soyTemplateRenderer.render(response.getWriter(), PluginMetadata.getCompleteModuleKey("soy-templates"), templateName, data);
        } catch (SoyException e) {
            Throwable cause = e.getCause();
            if (cause instanceof IOException) {
                throw (IOException) cause;
            }
            throw new ServletException(e);
        }
    }

    private boolean bool(HttpServletRequest req, String name) {
        return "on".equals(req.getParameter(name));
    }
}
