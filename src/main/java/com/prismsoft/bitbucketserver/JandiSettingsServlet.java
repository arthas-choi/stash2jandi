package com.prismsoft.bitbucketserver;

import com.atlassian.bitbucket.AuthorisationException;
import com.atlassian.bitbucket.i18n.I18nService;
import com.atlassian.bitbucket.permission.Permission;
import com.atlassian.bitbucket.permission.PermissionValidationService;
import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.bitbucket.repository.RepositoryService;
import com.atlassian.soy.renderer.SoyException;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.prismsoft.bitbucketserver.soy.SelectFieldOptions;
import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JandiSettingsServlet extends HttpServlet {

  private final PageBuilderService pageBuilderService;
  private final JandiSettingsService jandiSettingsService;
  private final RepositoryService repositoryService;
  private final SoyTemplateRenderer soyTemplateRenderer;
  private final PermissionValidationService validationService;
  private final I18nService i18nService;

  private Repository repository = null;

  public JandiSettingsServlet(PageBuilderService pageBuilderService,
      JandiSettingsService jandiSettingsService,
      RepositoryService repositoryService,
      SoyTemplateRenderer soyTemplateRenderer,
      PermissionValidationService validationService,
      I18nService i18nService) {
    this.pageBuilderService = pageBuilderService;
    this.jandiSettingsService = jandiSettingsService;
    this.repositoryService = repositoryService;
    this.soyTemplateRenderer = soyTemplateRenderer;
    this.validationService = validationService;
    this.i18nService = i18nService;
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {

    try {
      validationService.validateForRepository(this.repository, Permission.REPO_ADMIN);
    } catch (AuthorisationException e) {
      // Skip form processing
      doGet(req, res);
      return;
    }

    NotificationLevel notificationLevel = NotificationLevel.VERBOSE;
    if (null != req.getParameter("jandiNotificationLevel")) {
      notificationLevel = NotificationLevel.valueOf(req.getParameter("jandiNotificationLevel"));
    }

    NotificationLevel notificationPrLevel = NotificationLevel.VERBOSE;
    if (null != req.getParameter("jandiNotificationPrLevel")) {
      notificationPrLevel = NotificationLevel.valueOf(req.getParameter("jandiNotificationPrLevel"));
    }

    jandiSettingsService.setJandiSettings(
        repository,
        new ImmutableJandiSettings(
            "on".equals(req.getParameter("jandiNotificationsOverrideEnabled")),
            "on".equals(req.getParameter("jandiNotificationsEnabled")),
            "on".equals(req.getParameter("jandiNotificationsOpenedEnabled")),
            "on".equals(req.getParameter("jandiNotificationsReopenedEnabled")),
            "on".equals(req.getParameter("jandiNotificationsUpdatedEnabled")),
            "on".equals(req.getParameter("jandiNotificationsApprovedEnabled")),
            "on".equals(req.getParameter("jandiNotificationsUnapprovedEnabled")),
            "on".equals(req.getParameter("jandiNotificationsDeclinedEnabled")),
            "on".equals(req.getParameter("jandiNotificationsMergedEnabled")),
            "on".equals(req.getParameter("jandiNotificationsCommentedEnabled")),
            "on".equals(req.getParameter("jandiNotificationsEnabledForPush")),
            "on".equals(req.getParameter("jandiNotificationsEnabledForPersonal")),
            "on".equals(req.getParameter("jandiNotificationsNeedsWorkEnabled")),
            notificationLevel,
            notificationPrLevel,
            req.getParameter("jandiWebHookUrl").trim()
        )
    );

    doGet(req, res);
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String pathInfo = request.getPathInfo();
    if (Strings.isNullOrEmpty(pathInfo) || pathInfo.equals("/")) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }
    String[] pathParts = pathInfo.substring(1).split("/");
    if (pathParts.length != 4) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }
    String projectKey = pathParts[1];
    String repoSlug = pathParts[3];

    this.repository = repositoryService.getBySlug(projectKey, repoSlug);
    if (repository == null) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }
    doView(repository, response);

  }

  private void doView(Repository repository, HttpServletResponse response)
      throws ServletException, IOException {
    validationService.validateForRepository(repository, Permission.REPO_ADMIN);
    JandiSettings jandiSettings = jandiSettingsService.getJandiSettings(repository);
    render(response,
        "bitbucketserver.page.jandi.settings.viewJandiSettings",
        ImmutableMap.<String, Object>builder()
            .put("repository", repository)
            .put("jandiSettings", jandiSettings)
            .put("notificationLevels", new SelectFieldOptions(NotificationLevel.values()).toSoyStructure())
            .build()
    );
  }

  private void render(HttpServletResponse response, String templateName, Map<String, Object> data)
      throws IOException, ServletException {
    pageBuilderService.assembler().resources().requireContext("plugin.page.jandi");
    response.setContentType("text/html;charset=UTF-8");
    try {
      soyTemplateRenderer
          .render(response.getWriter(), PluginMetadata.getCompleteModuleKey("soy-templates"), templateName, data);
    } catch (SoyException e) {
      Throwable cause = e.getCause();
      if (cause instanceof IOException) {
        throw (IOException) cause;
      }
      throw new ServletException(e);
    }
  }
}
