package com.cn.bamboo.client;

import com.cn.bamboo.util.Const;
import com.cn.bamboo.util.Utils;
import org.eclipse.paho.client.mqttv3.*;

import javax.net.ssl.*;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.MessageFormat;
import java.util.Objects;

public class BambooMqttClient {

    private MqttClient mqttClient = null;
    private String mqttUrl;
    private Integer port;
    private String userName;
    private String password;
    private String deviceId;

    private static final String TLS_VERSION = "TLSv1.2";
    private static final String CLIENT_ID = "A1-Test";

    public BambooMqttClient(String mqttUrl, Integer port, String userName,
                            String password, String deviceId){
        this.mqttUrl = mqttUrl;
        this.port = port;
        this.userName = userName;
        this.password = password;
        this.deviceId = deviceId.replace("\"","");
        initMqttClient();
    }

    private void initMqttClient(){
        if(Objects.isNull(mqttClient)){
            try {
                mqttClient = new MqttClient(this.mqttUrl +":" + String.valueOf(this.port),
                        CLIENT_ID);

                MqttConnectOptions options = new MqttConnectOptions();
                try{
                    options.setCleanSession(true);
                    // 如果需要认证（根据实际情况添加）
                    options.setUserName("u_" + userName);
                    options.setPassword(password.toCharArray());
                    options.setSocketFactory(configureTls(null,null,null,null).getSocketFactory());
                    options.setHttpsHostnameVerificationEnabled(false); // 相当于tls_insecure_set(true)
                    options.setAutomaticReconnect(true);

                    mqttClient.setCallback(new MqttCallbackExtended() {
                        @Override
                        public void connectComplete(boolean b, String s) {
                            try {
                                subscribeBambuTopic();
                            } catch (Exception e) {
                                e.printStackTrace();
                                throw new IllegalStateException("发布Mqtt消息失败！");
                            }
                        }

                        @Override
                        public void connectionLost(Throwable throwable) {

                        }

                        @Override
                        public void messageArrived(String topic, MqttMessage mqttMessage){
                            System.out.println("receive " + topic + " message:");
                            System.out.println(new String(mqttMessage.getPayload()));
                        }

                        @Override
                        public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                        }
                    });
                    mqttClient.connect(options);
                } catch (MqttException e) {
                    e.printStackTrace();
                    throw new MqttException(MqttException.REASON_CODE_SERVER_CONNECT_ERROR);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new IllegalStateException("初始化Mqtt客户端失败!");
            }
        }
    }

    public void publishBambuToic(String command) {
        if (mqttClient == null || !mqttClient.isConnected()) {
            throw new IllegalStateException("Mqtt客户端未连接！");
        }

        try {
            publishCommand(MessageFormat.format(Const.BAMBOO_MQTT_PUBLISH,deviceId), command);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("Mqtt消息发布失败!");
        }
    }

    public void publishCommand(String topic, String command) throws MqttException {
        MqttMessage message = new MqttMessage(command.getBytes());
        message.setQos(1); // QoS level 1
        message.setRetained(false);
        mqttClient.publish(topic, message);
        System.out.println("Published command: " + command);
    }

    private void subscribeBambuTopic() throws MqttException {
        if (mqttClient == null || !mqttClient.isConnected()) {
            throw new IllegalStateException("Mqtt客户端未连接！");
        }

        subcribeCommand(MessageFormat.format(Const.BAMBOO_MQTT_SUBCRIBE,deviceId));
    }

    public void subcribeCommand(String topic) throws MqttException {
        mqttClient.subscribe(topic, 1);
    }

    private SSLContext configureTls(
                                    String caCertPath, String clientCertPath,
                                    String clientKeyPath, String keyPassword) throws Exception {
        TrustManagerFactory tmf;
        KeyManagerFactory kmf = null;

        // 1. 初始化TrustManagerFactory
        if (!Utils.isEmpty(caCertPath)) {
            // 自定义CA证书逻辑
            try (FileInputStream fis = new FileInputStream(caCertPath)) {
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                X509Certificate caCert = (X509Certificate) cf.generateCertificate(fis);

                KeyStore caKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                caKeyStore.load(null, null);
                caKeyStore.setCertificateEntry("caCert", caCert);

                tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                tmf.init(caKeyStore);
            }
        } else {
            // 使用系统默认证书
            tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init((KeyStore) null);
        }

        // 2. 初始化KeyManagerFactory（如果需要客户端证书）
        if (!Utils.isEmpty(clientCertPath)) {
            try (FileInputStream fis = new FileInputStream(clientCertPath)) {
                KeyStore clientKeyStore = KeyStore.getInstance("PKCS12");
                clientKeyStore.load(fis, keyPassword.toCharArray());

                kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                kmf.init(clientKeyStore, keyPassword.toCharArray());
            }
        }

        // 3. 创建SSLContext
        SSLContext sslContext = SSLContext.getInstance(TLS_VERSION);
        sslContext.init(kmf != null ? kmf.getKeyManagers() : null,
                tmf.getTrustManagers(),
                null);

        return  sslContext;
    }

    public boolean isConnected() {
        return mqttClient != null && mqttClient.isConnected();
    }
}
