package com.prismsoft.bitbucketserver;

public class PluginMetadata {

    public static String getPluginKey() {
        return "com.prismsoft.bitbucketserver.stash2jandi";
    }

    public static String getCompleteModuleKey(String moduleKey) {
        return getPluginKey() + ":" + moduleKey;
    }
}
