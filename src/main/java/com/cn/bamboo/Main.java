package com.cn.bamboo;

import com.cn.bamboo.client.BambooMqttClient;
import com.cn.bamboo.model.AuthData;
import com.cn.bamboo.client.BambooCloudClient;
import com.cn.bamboo.util.Const;

public class Main {
    public static void main(String[] args) {
        BambooCloudClient bamboo = new BambooCloudClient();
        bamboo.login("xxxxxxx","xxxxxxxx");
        AuthData userInfo = bamboo.getLoginUserData();

        BambooMqttClient bambooMqttClient = new BambooMqttClient(Const.BAMBOO_MQTT_URL, Const.BAMBOO_MQTT_PORT,
                userInfo.getUserName(), userInfo.getAccessToken(), userInfo.getProductInfos().getFirst().getDevId());

        bambooMqttClient.publishBambuToic(Const.MqttCommand.PUSH_ALL);
    }
}
