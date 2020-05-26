package com.radcortez.flyway.test.junit;

public class DataSourceInfo {
    private String url;
    private String username;
    private String password;

    private DataSourceInfo() {
    }

    public DataSourceInfo(final String url, final String username, final String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public static DataSourceInfo empty() {
        return new DataSourceInfo();
    }

    public static DataSourceInfo config(final String url) {
        return new DataSourceInfo(url, null, null);
    }

    public static DataSourceInfo config(final String url, final String username, final String password) {
        return new DataSourceInfo(url, username, password);
    }
}
