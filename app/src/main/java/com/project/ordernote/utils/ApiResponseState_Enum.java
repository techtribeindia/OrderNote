package com.project.ordernote.utils;


public class ApiResponseState_Enum<T> {
    public enum Status {
        SUCCESS,
        ERROR,
        LOADING
    }

    public final Status status;
    public final T data;
    public final String message;

    private ApiResponseState_Enum(Status status, T data, String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public static <T> ApiResponseState_Enum<T> success(T data) {
        return new ApiResponseState_Enum<>(Status.SUCCESS, data, null);
    }

    public static <T> ApiResponseState_Enum<T> error(String message, T data) {
        return new ApiResponseState_Enum<>(Status.ERROR, data, message);
    }

    public static <T> ApiResponseState_Enum<T> loading(T data) {
        return new ApiResponseState_Enum<>(Status.LOADING, data, null);
    }
}
