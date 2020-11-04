package com.prismsoft.bitbucketserver.tools;

public class JandiAttachment {

  private String title;
  private String description;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public static JandiAttachment createJandiAttachment(String title, String description) {
    JandiAttachment jandiAttachment = new JandiAttachment();
    jandiAttachment.setTitle(title);
    jandiAttachment.setDescription(description);

    return jandiAttachment;
  }
}
