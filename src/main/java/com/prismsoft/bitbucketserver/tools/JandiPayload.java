package com.prismsoft.bitbucketserver.tools;

import java.util.LinkedList;
import java.util.List;

public class JandiPayload {

  private String body;

  private String connectColor;

  private List<JandiAttachment> connectInfo = new LinkedList<JandiAttachment>();

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getConnectColor() {
        return connectColor;
    }

    public void setConnectColor(String connectColor) {
        this.connectColor = connectColor;
    }

    public List<JandiAttachment> getConnectInfo() {
        return connectInfo;
    }

    public void setConnectInfo(List<JandiAttachment> connectInfo) {
        this.connectInfo = connectInfo;
    }

    public void addConnectInfo(JandiAttachment jandiAttachment) {
        this.connectInfo.add(jandiAttachment);
    }
}
