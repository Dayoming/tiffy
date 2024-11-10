package com.tiffy.repository;

import com.tiffy.entity.Item;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ItemMapper {
    Item findByItemId(@Param("id") Long id);
    int countItems();
    List<Item> findItemsByPage(@Param("size") int size, @Param("offset") int offset);
    List<Item> findByItemNm(@Param("item") Item item);
    List<Item> findByItemNmOrItemDetail(@Param("item") Item item);
    List<Item> findAllByOrderByPriceDesc();
    List<Item> findAllByOrderByPriceAsc();
    Long distanceCalculate(@Param("longitude") Long longitude, @Param("latitude") Long latitude);
    void saveItem(Item item);
    void updateItem(Item item);
    void deleteItemById(@Param("id") Long id);
}
