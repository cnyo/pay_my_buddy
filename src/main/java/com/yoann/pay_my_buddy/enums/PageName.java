package com.yoann.pay_my_buddy.enums;

public enum PageName {
    TRANSACTION("transaction"),
    RELATION("relation"),
    PROFILE("profile");

    private final String pageName;

    PageName(String pageName) {
        this.pageName = pageName;
    }

    public String getPage() {
        return pageName;
    }
}
