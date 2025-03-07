package com.cn.bamboo.client;

import com.cn.bamboo.api.BambuCloudHandler;
import com.cn.bamboo.model.AuthData;
import com.cn.bamboo.model.ProductInfo;
import com.cn.bamboo.util.Utils;
import com.google.gson.*;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.logging.Logger;

import static com.cn.bamboo.util.Utils.*;

public class BambooCloudClient {

    private AuthData user = new AuthData();
    private BambuCloudHandler handler = new BambuCloudHandler();

    public void login(String account, String password){

        String info = readLocalData();
        try {
            loginByLocal(info, password);
        } catch (Exception e){
            loginByCloudApi(account, password);
        }

        saveLocalData(encrypt( new Gson().toJson(user), generateAESKey(password)));
    }

    private void loginByLocal(String info, String password){
        SecretKey secretKey = generateAESKey(password);
        String decrypted = decrypt(info, secretKey);
        user = new Gson().fromJson(decrypted, AuthData.class);
        if(!Objects.isNull(user)) return;
    }

    private void loginByCloudApi(String account, String password){
        user.setAccount(account);
        user.setPassword(password);

        user.setVerifyCode(handler.getVerifyCode(user));
        user.setAccessToken(handler.getAccessTokenByVerifyCode(user));

        user.setUserName(handler.getUserNameByAccessToken(user.getAccessToken()));
        getDeviceList(handler.getDeviceList(user.getAccessToken()));
    }

    private void getDeviceList(String devices){
        if(!Utils.isEmpty(devices)){
            JsonObject jsonObject = new JsonParser().parse(devices).getAsJsonObject();
            if(jsonObject.has("devices") && !Utils.isEmpty(jsonObject.get("devices").toString())){
                JsonArray jsonElements = new Gson().fromJson(jsonObject.get("devices").toString(),JsonArray.class);
                if(jsonElements.size() != 0){
                    List<ProductInfo> productInfos = new ArrayList<>();
                    jsonElements.forEach(jsonElement -> {
                        JsonObject item = jsonElement.getAsJsonObject();
                        ProductInfo productInfo = new ProductInfo();
                        productInfo.setDevAccessCode(item.get("dev_access_code").toString());
                        productInfo.setDevProductName(item.get("dev_product_name").toString());
                        productInfo.setName(item.get("name").toString());
                        productInfo.setDevId(item.get("dev_id").toString());
                        productInfo.setOnLine(item.get("online").toString());
                        productInfo.setPrintStatus(item.get("print_status").toString());
                        productInfos.add(productInfo);
                    });
                    user.setProductInfos(productInfos);
                }
            }
        }
    }

    public AuthData getLoginUserData(){
        return user;
    }

    public String getMyTask(){
       return handler.getMyTasks(user.getAccessToken());
    }
}
