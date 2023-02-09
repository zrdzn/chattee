package io.github.zrdzn.web.chattee.backend;

public class ChatteeLauncher {

    public static void main(String[] args) {
        ChatteeLauncher chatteeLauncher = new ChatteeLauncher();
        chatteeLauncher.createWithDefaultOptions().launch();
    }

    public Chattee createWithDefaultOptions() {
        return ChatteeFactory.createDefault();
    }

}
