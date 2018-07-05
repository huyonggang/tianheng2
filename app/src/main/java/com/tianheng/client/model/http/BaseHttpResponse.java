package com.tianheng.client.model.http;

/**
 * Created by huyg on 2017/1/17.
 */

public class BaseHttpResponse {
    private int code;
    private String message;
    public static int SUCCESS = 1;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return SUCCESS == code;
    }


}
