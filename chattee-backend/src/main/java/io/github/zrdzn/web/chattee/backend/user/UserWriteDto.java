package io.github.zrdzn.web.chattee.backend.user;

import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class UserWriteDto {

    @NotBlank
    @Email
    @Length(min = 5, max = 50)
    private String email;

    @NotBlank
    @Length(min = 3, max = 100)
    private String password;

    public UserWriteDto() {
    }

    public UserWriteDto(String email, String password) {
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
