package com.sampoom.backend.api.order.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.sampoom.backend.api.order.entity.OrderStatus;
import com.sampoom.backend.api.order.entity.POStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class POResDto {
    private String orderNumber;
    private String categoryName;
    private String groupName;
    private String partName;
    private String partCode;
    private Integer currQuantity;
    private Integer rop;
    private String unit;
    private Integer orderQuantity;
    private Integer inboundQuantity;
    private Integer restQuantity;
    private Integer price;
    private LocalDateTime createdAt;
    private POStatus orderStatus;

    @QueryProjection
    public POResDto(String orderNumber,
                    String partName,
                    String partCode,
                    Integer currQuantity,
                    Integer rop,
                    String unit,
                    Integer orderQuantity,
                    Integer inboundQuantity,
                    Integer restQuantity,
                    Integer price,
                    LocalDateTime createdAt,
                    POStatus orderStatus) {

        this.orderNumber = orderNumber;
        this.partName = partName;
        this.partCode = partCode;
        this.currQuantity = currQuantity;
        this.rop = rop;
        this.unit = unit;
        this.orderQuantity = orderQuantity;
        this.inboundQuantity = inboundQuantity;
        this.restQuantity = restQuantity;
        this.price = price;
        this.createdAt = createdAt;
        this.orderStatus = orderStatus;
    }

    public String getOrderStatus() {
        if (orderStatus == null)
            return null;
        return this.orderStatus.getKorean();
    }
}
