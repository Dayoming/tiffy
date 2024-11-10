package com.tiffy.entity;

import com.tiffy.constant.ItemCategory;
import com.tiffy.constant.ItemSellStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Item {
    private Long id; // 상품 코드

    private String itemNm; // 상품명

    private ItemCategory itemCategory; // 상품 카테고리

    private String sellerId; // 파는 사람 아이디

    private String sellerNm; // 파는 사람 별명

    private int price; // 가격

    private int stockNumber; // 수량

    private String itemDetail; // 상품 상세 설명

    private String place; // 거래 희망 위치

    private BigDecimal lat; // 거래 희망 위치 위도

    private BigDecimal lng; // 거래 희망 위치 경도

    private ItemSellStatus itemSellStatus; // 상품 판매 상태

    private String regTime; // 등록 시간

    private String updateTime; // 수정 시간

    @Builder
    public Item(Long id, String itemNm, ItemCategory itemCategory, String sellerId, String sellerNm
            , int price, int stockNumber, String itemDetail, String place, BigDecimal lat, BigDecimal lng) {
        this.id = id;
        this.itemNm = itemNm;
        this.itemCategory = itemCategory;
        this.sellerId = sellerId;
        this.sellerNm = sellerNm;
        this.price = price;
        this.stockNumber = stockNumber;
        this.itemDetail = itemDetail;
        this.place = place;
        this.lat = lat;
        this.lng = lng;
        this.itemSellStatus = ItemSellStatus.SELL;

        LocalDateTime localDateTime = LocalDateTime.now();

        this.regTime = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.updateTime = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
