package com.example.boobleproject;

import com.google.gson.annotations.SerializedName;

public class ErrorResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("field")  // Опционально, если есть поле с ошибкой
    private String field;

    // Геттеры
    public String getMessage() { return message; }
    public String getField() { return field; }
}
