package com.dasd412.remake.api.config.oauth.provider;

import java.util.Map;

public class GitHubUserInfo implements OAuth2UserInfo {

    private Map<String, Object> attributes;

    public GitHubUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProvider() {
        int id= (int) attributes.get("id");
        return String.valueOf(id);
    }

    @Override
    public String getProviderId() {
        return "github";
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getName() {
        return (String) attributes.get("login");
    }
}
