package com.sampoom.backend.common.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public enum ErrorStatus {

    // 400 BAD_REQUEST
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    MISSING_EMAIL_VERIFICATION_EXCEPTION(HttpStatus.BAD_REQUEST, "이메일 인증을 진행해주세요."),
    ALREADY_REGISTER_EMAIL_EXCEPTION(HttpStatus.BAD_REQUEST, "이미 가입된 이메일 입니다."),
    ALREADY_EXIST_BRANCH_NAME(HttpStatus.BAD_REQUEST, "이미 존재하는 지점명입니다"),

    NO_UPDATE_PARTS_LIST(HttpStatus.BAD_REQUEST, "업데이트 할 부품 리스트가 없습니다."),
    INVALID_PART_QUANTITY(HttpStatus.BAD_REQUEST, "부품의 재고 수량은 0 이상이어야 합니다."),
    DUPLICATED_PART(HttpStatus.BAD_REQUEST, "중복된 부품 아이디가 있습니다"),
    INVALID_ORDER_STATUS(HttpStatus.BAD_REQUEST, "잘못된 주문 상태입니다."),
    INVALID_QUANTITY_STATUS(HttpStatus.BAD_REQUEST, "잘못된 재고 수량 상태입니다."),
    INVALID_STATUS(HttpStatus.BAD_REQUEST, "잘못된 상태입니다."),
    DISTANCE_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "이미 존재하는 대리점+창고 조합입니다"),
    REQUEST_HAS_NULL(HttpStatus.BAD_REQUEST, "요청에 null이 존재합니다."),
    ROP_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "재고에 rop가 이미 존재합니다"),
    FAIL_SERIALIZE(HttpStatus.BAD_REQUEST, "직렬화에 실패하였습니다."),
    BAD_DELTA_REQUEST(HttpStatus.BAD_REQUEST, "부품이 중복되거나 재고를 찾을 수 없습니다."),

    POSITIVE_DELTA(HttpStatus.BAD_REQUEST, "변화량이 양수입니다."),
    NEGATIVE_DELTA(HttpStatus.BAD_REQUEST, "변화량이 음수입니다."),
    INVALID_PAYLOAD_TYPE(HttpStatus.BAD_REQUEST, "부정확한 이벤트 페이로드입니다."),
    PAYLOAD_NULL(HttpStatus.BAD_REQUEST, "null인 필드가 있습니다."),

    // 401 UNAUTHORIZED
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),

    // 403 FORBIDDEN
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),

    // 404 NOT_FOUND
    NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 리소스를 찾을 수 없습니다."),
    PART_NOT_FOUND(HttpStatus.NOT_FOUND, "창고에 존재하지 않는 부품입니다."),
    BRANCH_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 창고 지점입니다."),
    ROP_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 ROP Id입니다"),
    INVENTORY_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 창고, 부품으로 등록된 재고가 없습니다"),
    WAREHOUSE_NOT_FOUND(HttpStatus.NOT_FOUND, "창고가 존재하지 않습니다"),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "카테고리가 존재하지 않습니다."),
    GROUP_NOT_FOUND(HttpStatus.NOT_FOUND, "그룹이 존재하지 않습니다."),
    PO_NOT_FOUND(HttpStatus.NOT_FOUND, "발주 주문서를 찾을 수 없습니다."),


    // 409 CONFLICT
    CONFLICT(HttpStatus.CONFLICT, "충돌이 발생했습니다."),

    // 500 INTERNAL_SERVER_ERROR
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    public int getStatusCode() {
        return this.httpStatus.value();
    }

}
