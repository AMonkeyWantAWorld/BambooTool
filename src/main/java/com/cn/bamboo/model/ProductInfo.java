package com.cn.bamboo.model;

public class ProductInfo {
    private String devId;
    private String name;
    private String onLine;
    private String printStatus;
    private String devProductName;
    private String devAccessCode;

    public String getDevId() {
        return devId;
    }

    public void setDevId(String devId) {
        this.devId = devId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOnLine() {
        return onLine;
    }

    public void setOnLine(String onLine) {
        this.onLine = onLine;
    }

    public String getPrintStatus() {
        return printStatus;
    }

    public void setPrintStatus(String printStatus) {
        this.printStatus = printStatus;
    }

    public String getDevProductName() {
        return devProductName;
    }

    public void setDevProductName(String devProductName) {
        this.devProductName = devProductName;
    }

    public String getDevAccessCode() {
        return devAccessCode;
    }

    public void setDevAccessCode(String devAccessCode) {
        this.devAccessCode = devAccessCode;
    }
}
