package com.dasd412.remake.api.config.security.oauth.provider;

import java.util.Map;

public class GitHubUserInfo implements OAuth2UserInfo {

    private Map<String, Object> attributes;

    public GitHubUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProvider() {
        return "github";
    }

    @Override
    public String getProviderId() {
        int id = (int) attributes.get("id");
        return String.valueOf(id);
    }

    @Override
    public String getEmail() {
        int id = (int) attributes.get("id");
        return attributes.get("email") == null ? id + "@github.com" : (String) attributes.get("email");
    }

    @Override
    public String getName() {
        return (String) attributes.get("login");
    }
}
