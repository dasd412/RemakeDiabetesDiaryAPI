package com.dasd412.remake.api.config.oauth.provider;

import java.util.Map;

public class FaceBookUserInfo implements OAuth2UserInfo {

    private Map<String, Object> attributes;

    public FaceBookUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProvider() {
        return (String) attributes.get("id");
    }

    @Override
    public String getProviderId() {
        return "facebook";
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }
}
