package com.cronos.api.modules.workspace.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MemberInviteRequest {
    private String username;
    private WorkspaceRole role;

    public MemberInviteRequest() {}

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public WorkspaceRole getRole() { return role; }
    public void setRole(WorkspaceRole role) { this.role = role; }
}
