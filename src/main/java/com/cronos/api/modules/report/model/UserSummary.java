package com.cronos.api.modules.report.model;

public class UserSummary {
    private Integer userId;
    private String username;
    private Long totalSeconds;

    public UserSummary(Integer userId, String username, Long totalSeconds) {
        this.userId = userId;
        this.username = username;
        this.totalSeconds = totalSeconds != null ? totalSeconds : 0L;
    }

    public Integer getUserId() { return userId; }
    public String getUsername() { return username; }
    public Long getTotalSeconds() { return totalSeconds; }
}
