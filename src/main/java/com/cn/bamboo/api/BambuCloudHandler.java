package com.cn.bamboo.api;

import com.cn.bamboo.model.AuthData;
import com.cn.bamboo.util.Const;
import com.cn.bamboo.util.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jdk.jshell.execution.Util;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.cn.bamboo.util.HttpRequest.*;

public class BambuCloudHandler implements BambuCloudApi {

    @Override
    public String getVerifyCode(AuthData user) {
        post(Const.BAMBOO_LOGIN_API, user);
        String verifyCode = java.lang.System.console().readLine("请输入短信验证码: "); // 读取一行文本
        if(Utils.isEmpty(verifyCode)){
            throw new IllegalStateException("短信验证码不能为空！");
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
        if(!jsonObject.has("accessToken")){
            throw new IllegalStateException("响应中缺少accessToken字段！");
        }

        String accessToken = jsonObject.get("accessToken").toString().replace("\"","");
        if(!Utils.isEmpty(accessToken)){
            throw new IllegalStateException("返回的访问令牌为空！");
        }
        return accessToken;
    }

    @Override
    public String getUserNameByAccessToken(String accessToken){
        String[] tokens = accessToken.split("\\.");
        JsonObject payload = parseJwtPayload(tokens);

        if(payload.has("user_id")){
            return payload.get("user_id").getAsString();
        }

        String projectsInfo = get(Const.BAMBOO_PROJECTS_API, accessToken);
        JsonObject projectsResponse = JsonParser.parseString(projectsInfo).getAsJsonObject();
        return extractUserIdFromProjects(projectsResponse);
    }

    private JsonObject parseJwtPayload(String[] tokens){
        if(tokens.length == 3){
            String base64String = tokens[1];
            base64String += "=".repeat((4 - base64String.length() % 4) % 4);
            byte[] decodedBytes = Base64.getDecoder().decode(base64String);
            return new JsonParser().parse(new String(decodedBytes)).getAsJsonObject();
        }

        return new JsonObject();
    }

    private String extractUserIdFromProjects(JsonObject projectsResponse){
        if(!projectsResponse.has("projects")){
            throw new IllegalStateException("响应缺少projects字段！");
        }

        JsonArray projects = projectsResponse.getAsJsonArray("projects");
        if (projects.size() == 0 || Objects.isNull(projects)) {
            throw new IllegalStateException("项目列表为空！");
        }

        JsonObject firstProject = projects.get(0).getAsJsonObject();
        if (!firstProject.has("user_id")) {
            throw new IllegalStateException("项目信息中缺少user_id字段！");
        }

        return firstProject.get("user_id").getAsString();
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
