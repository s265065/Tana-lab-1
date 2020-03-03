package com.company;

import java.io.Serializable;

public class Command implements Serializable { //Класс для передачи команды между сервером и клиентом

    private String command;
    private Object data;
    private String username;
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public Command() {
    }

    public Command(String command, Object data) {
        this.command = command;
        this.data = data;
    }

    public Command(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Command{" +
                "command='" + command + '\'' +
                ", data=" + data +
                ", key='" + '\'' +
                '}';
    }
}
