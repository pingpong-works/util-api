package com.util.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ExceptionCode {

    //board 관련
    BOARD_UNAUTHORIZED_ACTION(403, "Board_Unauthorized_Action"),
    BOARD_NOT_FOUND(404,"Board Not Found"),
    BOARD_COMMENT_NOT_FOUND(404,"Board_Comment Not Found"),
    BOARD_IMAGE_NOT_FOUND(404,"Board_Image Not Found"),
    BOARD_EXISTS(409,"Board exists"),
    INVALID_BOARD_TYPE(400,"INVALID_BOARD_TYPE"),

    //employee 관련
    EMPLOYEE_NOT_FOUND(404,"Employee Not Found"),

    // 토큰 인증 관련
    UNAUTHORIZED_MEMBER(401, "권한이 없는 멤버입니다."),
    TOKEN_INVALID(404, "토큰값이 유효하지 않습니다.");

    @Getter
    private int statusCode;

    @Getter
    private String statusDescription;

}