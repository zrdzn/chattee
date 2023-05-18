package io.github.zrdzn.web.chattee.backend.web;

public class WebOptionCreateRequest {

    private String name;
    private String value;

    public WebOptionCreateRequest() {
    }

    public WebOptionCreateRequest(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
