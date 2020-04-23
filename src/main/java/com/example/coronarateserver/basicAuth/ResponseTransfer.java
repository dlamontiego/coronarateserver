package com.example.coronarateserver.basicAuth;

public class ResponseTransfer {

    private String response;

    public ResponseTransfer(String response) {
        this.setResponse(response);
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
