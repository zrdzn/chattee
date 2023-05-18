package io.github.zrdzn.web.chattee.backend.web;

public class WebOption {

    private long id;
    private String name;
    private String value;

    public WebOption() {
    }

    public WebOption(long id, String name, String value) {
        this.id = id;
        this.name = name;
        this.value = value;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
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
