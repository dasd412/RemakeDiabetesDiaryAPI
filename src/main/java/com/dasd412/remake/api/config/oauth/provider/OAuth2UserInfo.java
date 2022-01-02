package com.dasd412.remake.api.config.oauth.provider;

public interface OAuth2UserInfo {
    String getProvider();//Oauth 제공 회사 이름
    String getProviderId();//Oauth provider 가 제공한 "유일한" 식별자 값
    String getEmail();
    String getName();
}

