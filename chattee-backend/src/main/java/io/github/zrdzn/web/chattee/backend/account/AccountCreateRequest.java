package io.github.zrdzn.web.chattee.backend.account;

public class AccountCreateRequest {

    private String email;
    private String password;
    private String username;
    private String avatarUrl;

    public AccountCreateRequest() {
    }

    public AccountCreateRequest(String email, String password, String username, String avatarUrl) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.avatarUrl = avatarUrl;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatarUrl() {
        return this.avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

}
