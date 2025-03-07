package com.cn.bamboo.util;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;

public class Utils {

    public static boolean isEmpty(String string){
        if(Objects.isNull(string) || string.isEmpty() || string.length() == 0){
            return true;
        }
        return false;
    }

    // 生成AES密钥（256位）
    public static SecretKey generateAESKey(String password) {
        // 填充固定值
        byte[] keyBytes = password.getBytes(StandardCharsets.UTF_8);
        keyBytes = Arrays.copyOf(keyBytes, 32); // 自动补0
        return new SecretKeySpec(keyBytes, "AES");
    }

    // 加密（返回Base64编码字符串）
    public static String encrypt(String plaintext, SecretKey key) {
        byte[] iv = new byte[16];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv); // 生成随机IV

        Cipher cipher = null;
        byte[] cipherText;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
            cipherText = cipher.doFinal(plaintext.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }

        // 合并IV和密文：IV(16) + cipherText
        byte[] encryptedData = new byte[iv.length + cipherText.length];
        System.arraycopy(iv, 0, encryptedData, 0, iv.length);
        System.arraycopy(cipherText, 0, encryptedData, iv.length, cipherText.length);

        return Base64.getEncoder().encodeToString(encryptedData);
    }

    // 解密（输入Base64编码字符串）
    public static String decrypt(String base64Ciphertext, SecretKey key) {
        byte[] encryptedData = Base64.getDecoder().decode(base64Ciphertext);

        // 分离IV和密文
        byte[] iv = Arrays.copyOfRange(encryptedData, 0, 16);
        byte[] cipherText = Arrays.copyOfRange(encryptedData, 16, encryptedData.length);

        Cipher cipher = null;
        String data;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
            data = new String(cipher.doFinal(cipherText), "UTF-8");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        return data;
    }

    public static void saveLocalData(String text){
        try {
            Files.write(Path.of("data.data"), Collections.singleton(text), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Save text error!");
        }
    }

    public static String readLocalData(){
        List<String> lines = null;
        try {
            lines = Files.readAllLines(Paths.get("data.data"));
        } catch (IOException e) {
            throw new RuntimeException("Read text error!");
        }
        return String.join("\n", lines);
    }

    public static void main(String[] args) throws Exception {
        // 生成密钥
        SecretKey secretKey = generateAESKey("trustno1_!");

        // 原始文本
        String originalText = "Hello, 对称加密! 2024-04-15";
        System.out.println("原始文本: " + originalText);

        // 加密
        String encrypted = encrypt(originalText, secretKey);
        System.out.println("加密结果: " + encrypted);
        saveLocalData(encrypted);
        String content = readLocalData();

        // 解密
        String decrypted = decrypt(content, secretKey);
        System.out.println("解密结果: " + decrypted);
        System.out.println("验证结果: " + originalText.equals(decrypted));
    }
}
