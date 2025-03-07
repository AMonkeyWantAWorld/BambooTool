package com.cn.bamboo.api;

import com.cn.bamboo.model.AuthData;

public interface BambuCloudApi {

    String getVerifyCode(AuthData user);

    String getAccessTokenByVerifyCode(AuthData user);

    String getUserNameByAccessToken(String accessToken);

    String getDeviceList(String accessToken);

    String getMyTasks(String accessToken);

}
