package com.cn.bamboo.model;

import java.util.List;

import static com.cn.bamboo.util.Const.BAMBOO_MQTT_URL;

public class AuthData {
    private String account;
    private String password;
    private String apiError;
    private String verifyCode;
    private String accessToken;
    private String userName;
    private List<ProductInfo> productInfos;
    private String mqttHost;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getApiError() {
        return apiError;
    }

    public void setApiError(String apiError) {
        this.apiError = apiError;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<ProductInfo> getProductInfos() {
        return productInfos;
    }

    public void setProductInfos(List<ProductInfo> productInfos) {
        this.productInfos = productInfos;
    }

    public String getMqttHost() {
        return BAMBOO_MQTT_URL;
    }

}
