package com.cn.bamboo.api;

import com.cn.bamboo.model.AuthData;
import com.cn.bamboo.util.Const;
import com.cn.bamboo.util.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static com.cn.bamboo.util.HttpRequest.*;

public class BambuCloudHandler implements BambuCloudApi {

    @Override
    public String getVerifyCode(AuthData user) {
        post(Const.BAMBOO_LOGIN_API, user);
        String verifyCode = java.lang.System.console().readLine("请输入短信验证码: "); // 读取一行文本
        if(Utils.isEmpty(verifyCode)){
            throw new RuntimeException("短信验证码不能为空！");
        }
        return verifyCode.replace("\"","");
    }

    @Override
    public String getAccessTokenByVerifyCode(AuthData user){
        Map<String, String> userMap = new HashMap<>();
        userMap.put("account",user.getAccount());
        userMap.put("code",user.getVerifyCode());

        String result = post(Const.BAMBOO_LOGIN_API, userMap);
        JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
        if(!jsonObject.has("accessToken") || Utils.isEmpty(jsonObject.get("accessToken").toString()))
            throw new RuntimeException("Get access token error!");
        return jsonObject.get("accessToken").toString().replace("\"","");
    }

    @Override
    public String getUserNameByAccessToken(String accessToken){
        JsonObject item = new JsonObject();
        String[] tokens = accessToken.split("\\.");
        if(tokens.length != 3){
            String projectsInfo = get(Const.BAMBOO_PROJECTS_API, accessToken);
            if(!Utils.isEmpty(projectsInfo)){
                JsonObject jsonObject = new JsonParser().parse(projectsInfo).getAsJsonObject();
                if(jsonObject.has("projects") && !Utils.isEmpty(jsonObject.get("projects").toString())){
                    JsonArray jsonElements = new Gson().fromJson(jsonObject.get("projects").toString(),JsonArray.class);
                    if(jsonElements.size() != 0){
                        item = jsonElements.get(0).getAsJsonObject();
                    }
                }
            }
        } else {
            String base64String = tokens[1];
            base64String += "=".repeat((4 - base64String.length() % 4) % 4);
            byte[] decodedBytes = Base64.getDecoder().decode(base64String);
            item = new JsonParser().parse(new String(decodedBytes)).getAsJsonObject();
        }

        if(item.has("user_id") && !Utils.isEmpty(item.get("user_id").toString())){
            return item.get("user_id").toString().replace("\"","");
        } else {
            throw new RuntimeException("Get projects info error!");
        }
    }

    @Override
    public String getDeviceList(String accessToken) {
        return get(Const.BAMBOO_BIND_API,accessToken);
    }

    @Override
    public String getMyTasks(String accessToken) {
        return get(Const.BAMBOO_TASKS_API,accessToken);
    }


}
