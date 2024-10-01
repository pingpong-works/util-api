package com.util.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ExceptionCode {


    //추가해야할 에러 코드
    NOT_MATCH_HOST_MEMBER(404,"NOT_MATCH_HOST_MEMBER"),
    REQUEST_ALREADY_RECEIVED_FROM_OTHER_PARTY(202,"NOT_MATCH_HOST_MEMBER"),
    ACCESS_DENIED(403,"ACCESS_DENIED"),
    CHATROOM_NOT_FOUND(404,"CHATROOM_NOT_FOUND"),


    //board 관련
    BOARD_UNAUTHORIZED_ACTION(403, "Board_Unauthorized_Action"),
    BOARD_NOT_FOUND(404,"Board Not Found"),
    BOARD_COMMENT_NOT_FOUND(404,"Board_Comment Not Found"),
    BOARD_IMAGE_NOT_FOUND(404,"Board_Image Not Found"),
    BOARD_EXISTS(409,"Board exists"),
    INVALID_BOARD_TYPE(400,"INVALID_BOARD_TYPE"),

    //notice 관련
    NOTICE_UNAUTHORIZED_ACTION(403, "Notice_Unauthorized_Action"),
    NOTICE_NOT_FOUND(404, "Notice Not Found"),
    NOTICE_IMAGE_NOT_FOUND(404,"Notice_Image Not Found"),

    //member 관련
    MEMBER_NOT_FOUND(404,"Member Not Found"),
    MEMBER_EXISTS(409,"Member exists"),
    PHONE_EXISTS(409, "Phone exists"),
    NICKNAME_EXISTS(409, "NickName exists"),

    // friend 관련
    REQUEST_NOT_FOUND(404, "Request Not Found"),
    FRIEND_REQUEST_ALREADY_EXISTS(409, "Friend Request Already Exists"),
    NOT_FRIEND_RECIPIENT(403, "Not a friend recipient"),
    FRIEND_REQUEST_NOT_FOUND(403, "친구요청을 찾을 수 없습니다"),
    INVALID_FRIEND_STATUS (404, "없는 친구 상태입니다"),
    // 친구 요청 관련
    UNAUTHORIZED_ACCESS(404, "친구요청을 없습니다."),
    BANNED_MEMBER_CANNOT_SEND_REQUEST( 404,"벤한 친구는 친구요청을 할 수 없습니다"),
    // 메일 전송 관련
    UNABLE_TO_SEND_EMAIL(404, "이메일 전송에 실패했습니다."),
    NO_SUCH_ALGORITHM(404, "No_Such_Algorithm"),

    // 토큰 인증 관련
    UNAUTHORIZED_MEMBER(401, "권한이 없는 멤버입니다."),
    TOKEN_INVALID(404, "토큰값이 유효하지 않습니다."),

    //룰렛 관련
    ROULETTE_EXISTS(409, "roulette exists"),
    ROULETTE_NOT_FOUND(404, "Roulette Not Found"),

    // 차단 관련
    BLOCKING_MEMBER_NOT_FOUND(404, "차단할 멤버를 찾을 수 없습니다."),
    BANNED_MEMBER_NOT_FOUND(404, "차단당할 멤버를 찾을 수 없습니다."),
    BAN_RECORD_NOT_FOUND(404, "차단 기록이 없습니다.");
    @Getter
    private int statusCode;

    @Getter
    private String statusDescription;

}