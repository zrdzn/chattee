package io.github.zrdzn.web.chattee.backend.account;

public class AccountRegisterDto {

    private String email;
    private String rawPassword;
    private String username;

    public AccountRegisterDto() {
    }

    public AccountRegisterDto(String email, String rawPassword, String username) {
        this.email = email;
        this.rawPassword = rawPassword;
        this.username = username;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRawPassword() {
        return this.rawPassword;
    }

    public void setRawPassword(String rawPassword) {
        this.rawPassword = rawPassword;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
