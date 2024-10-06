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

    //department 관련
    DEPARTMENT_NOT_FOUND(404,"Department Not Found"),

    //resource 관련
    CAR_UNAUTHORIZED_ACTION(403, "Car Unauthorized Action"),
    CAR_NOT_FOUND(404, "Car Not Found"),
    ROOM_UNAUTHORIZED_ACTION(403, "Room UnAuthorized Action"),
    ROOM_NOT_FOUND(404, "Room Not Found"),

    //book 관련
    CAR_BOOK_UNAUTHORIZED_ACTION(403, "Car Book Unauthorized Action"),
    CAR_BOOK_NOT_FOUND(404, "Car Book Not Found"),
    ROOM_BOOK_UNAUTHORIZED_ACTION(403, "Room Book Unauthorized Action"),
    ROOM_BOOK_NOT_FOUND(404, "Room Book Not Found"),
    BOOK_CONFLICT_BOOKING(409, "Book Conflict Booking"),

    //calendar 관련
    CALENDAR_UNAUTHORIZED_ACTION(403, "Calendar Unauthorized Action"),
    CALENDAR_NOT_FOUND(404, "Calendar Not Found");


    @Getter
    private int statusCode;

    @Getter
    private String statusDescription;

}