package com.company;


import java.io.Serializable;

public class Response  implements Serializable {//Класс для передачи данных

    private Object response; //Объекты для хранения данных
    private String[] strings;

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }
}
