package com.util.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ExceptionCode {

    //board 관련
    BOARD_UNAUTHORIZED_ACTION(403, "Board Unauthorized Action"),
    BOARD_NOT_FOUND(404,"Board Not Found"),
    BOARD_COMMENT_NOT_FOUND(404,"Board Comment Not Found"),

    //employee 관련
    EMPLOYEE_NOT_FOUND(404,"Employee Not Found"),

    //resource 관련
    CAR_UNAUTHORIZED_ACTION(403, "Car Unauthorized Action"),
    CAR_NOT_FOUND(404, "Car Not Found"),
    ROOM_UNAUTHORIZED_ACTION(403, "Room UnAuthorized Action"),
    ROOM_NOT_FOUND(404, "Room Not Found");


    @Getter
    private int statusCode;

    @Getter
    private String statusDescription;

}