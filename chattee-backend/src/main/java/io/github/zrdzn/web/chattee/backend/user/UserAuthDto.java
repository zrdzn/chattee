package io.github.zrdzn.web.chattee.backend.user;

public class UserAuthDto {

    private String email;
    private String password;

    public UserAuthDto() {
    }

    public UserAuthDto(String email, String password) {
        this.email = email;
        this.password = password;
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

}
