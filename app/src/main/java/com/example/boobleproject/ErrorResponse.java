package com.example.boobleproject;

import com.google.gson.annotations.SerializedName;

public class ErrorResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("field")
    private String field;


    public String getMessage() { return message; }
    public String getField() { return field; }
}
