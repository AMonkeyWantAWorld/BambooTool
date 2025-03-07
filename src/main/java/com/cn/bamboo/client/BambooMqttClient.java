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
    private SSLContext sslContext;
    private boolean tlsInsecure;

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
                        "A1-Test");

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
                            } catch (MqttException e) {
                                throw new RuntimeException("Connect complete subcribe topic error!");
                            }
                        }

                        @Override
                        public void connectionLost(Throwable throwable) {

                        }

                        @Override
                        public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                            System.out.println("receive " + topic + " message:");
                            System.out.println(new String(mqttMessage.getPayload()));
                        }

                        @Override
                        public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                        }
                    });
                    mqttClient.connect(options);
                } catch (MqttException e) {
                    throw new RuntimeException(e);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Init mqtt client error!");
            }
        }
    }

    public void publishBambuToic(String command) {
        if (mqttClient == null || !mqttClient.isConnected()) {
            throw new RuntimeException("Mqtt client is not connected!");
        }

        try {
            publishCommand(MessageFormat.format(Const.BAMBOO_MQTT_PUBLISH,deviceId), command);
        } catch (MqttException e) {
            throw new RuntimeException(e);
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
            throw new MqttException(MqttException.REASON_CODE_CLIENT_NOT_CONNECTED);
        }

        subcribeCommand(MessageFormat.format(Const.BAMBOO_MQTT_SUBCRIBE,deviceId));
    }

    public void subcribeCommand(String topic) throws MqttException {
        mqttClient.subscribe(topic, 1);
    }

    private SSLContext configureTls(
                                    String caCertPath, String clientCertPath,
                                    String clientKeyPath, String keyPassword) throws Exception {
        TrustManagerFactory tmf = null;
        X509Certificate caCert = null;
        KeyManagerFactory kmf = null;

        // 1. 加载CA证书
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        if(!Utils.isEmpty(caCertPath)){
            caCert = (X509Certificate) cf.generateCertificate(
                    new FileInputStream(caCertPath));
        }

        KeyStore clientKeyStore = KeyStore.getInstance("PKCS12");
        if(!Objects.isNull(caCert)){
            // 2. 创建KeyStore并加载CA证书
            KeyStore caKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            caKeyStore.setCertificateEntry("caCert", caCert);
            // 3. 创建TrustManagerFactory
            tmf = TrustManagerFactory.getInstance(
                    TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(caKeyStore);

            // 4. 加载客户端证书和私钥
            clientKeyStore.load(new FileInputStream(clientCertPath),
                        keyPassword.toCharArray());

            // 5. 创建KeyManagerFactory
            kmf = KeyManagerFactory.getInstance(
                    KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(clientKeyStore, Utils.isEmpty(keyPassword)?null:keyPassword.toCharArray());
        } else {
            clientKeyStore.load(null, null);
            tmf = TrustManagerFactory.getInstance(
                    TrustManagerFactory.getDefaultAlgorithm());
            tmf.init((KeyStore) null); // 关键变化：加载默认CA证书
        }

        // 6. 创建SSLContext
        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
        sslContext.init(Objects.isNull(kmf)?null:kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        return  sslContext;
    }
}
