package com.tiffy.repository;

import com.tiffy.constant.ItemCategory;
import com.tiffy.constant.ItemSellStatus;
import com.tiffy.entity.Item;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Test
    @DisplayName("상품 저장 테스트")
    public void createItemTest() {
        Item item = new Item();
        item.setItemNm("테스트 상품");
        item.setItemCategory(ItemCategory.ETC);
        item.setSellerId("dayomi");
        item.setSellerNm("다영");
        item.setPrice(10000);
        item.setItemDetail("테스트 상품 상세 설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);

        LocalDateTime localDateTime = LocalDateTime.now();
        item.setRegTime(localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        item.setUpdateTime(localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        Item savedItem = itemRepository.save(item);
        System.out.println(savedItem.toString());
    }

    public void createItemList() {
        for (int i = 1; i <= 10; i++) {
            Item item = new Item();
            item.setItemNm("테스트 상품" + i);
            item.setItemCategory(ItemCategory.ETC);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setSellerId("dayomi" + i);
            item.setSellerNm("다영" + i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100);

            LocalDateTime localDateTime = LocalDateTime.now();
            item.setRegTime(localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            item.setUpdateTime(localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            Item savedItem = itemRepository.save(item);
        }
    }

    @Test
    @DisplayName("상품 전체 조회 테스트")
    public void findAllItemTest() {
        this.createItemList();
        List<Item> itemList = itemRepository.findAll();
        for (Item item : itemList) {
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("상품명 조회 테스트")
    public void findByItemNmTest() {
        this.createItemList();
        List<Item> itemList = itemRepository.findByItemNm("테스트 상품1");
        for (Item item : itemList) {
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("상품명, 상품상세설명 or 테스트")
    public void findByItemNmOrItemDetailTest() {
        this.createItemList();
        List<Item> itemList = itemRepository.findByItemNmOrItemDetail("테스트 상품1", "테스트 상품 상세 설명5");
        for (Item item : itemList) {
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("상품 가격 높은 순 정렬")
    public void findByPriceOrderByPriceDescTest() {
        this.createItemList();
        List<Item> itemList = itemRepository.findAllByOrderByPriceDesc();
        for(Item item : itemList) {
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("상품 가격 낮은 순 정렬")
    public void findByPriceOrderByPriceAscTest() {
        this.createItemList();
        List<Item> itemList = itemRepository.findAllByOrderByPriceAsc();
        for(Item item : itemList) {
            System.out.println(item.toString());
        }
    }
}
