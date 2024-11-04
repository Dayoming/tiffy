package com.tiffy.service;

import com.tiffy.constant.ItemSellStatus;
import com.tiffy.dto.ItemDto;
import com.tiffy.entity.Item;
import com.tiffy.repository.ItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ItemService {

    @Autowired
    private final ItemMapper itemMapper;

    public ResponseEntity<Object> findByItemId(Long id) {
        Item item = itemMapper.findByItemId(id);
        if (item == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(item);
    }

    @Transactional
    public ResponseEntity<Map<String, String>> createItem(ItemDto itemDto) {
        itemDto.setItemSellStatus(ItemSellStatus.SELL);

        LocalDateTime localDateTime = LocalDateTime.now();
        itemDto.setRegTime(localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        itemDto.setUpdateTime(localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        itemDto.setSellerNm("Admin");
        itemDto.setSellerId("Admin");

        // price와 stockNumber가 null이면 기본값 설정
        if (itemDto.getPrice() == null) {
            itemDto.setPrice(0); // 기본값 설정
        }
        if (itemDto.getStockNumber() == null) {
            itemDto.setStockNumber(0); // 기본값 설정
        }
        Item item = itemDto.toEntity();
        itemMapper.saveItem(item);

        Map<String, String> response = new HashMap<>();
        response.put("message", "정상적으로 등록되었습니다.");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Transactional
    public Map<String, String> deleteItem(Long id) {
        Map<String, String> response = new HashMap<>();
        if (itemMapper.findByItemId(id) == null) {
            response.put("error", "상품을 찾을 수 없습니다.");
            return response;
        }

        itemMapper.deleteItemById(id);
        response.put("message", "상품을 정상적으로 삭제했습니다.");

        return response;
    }

    @Transactional
    public Item editItem(Long id, ItemDto itemDto) {
        Item target = itemMapper.findByItemId(id);
        LocalDateTime now = LocalDateTime.now();
        itemDto.setUpdateTime(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        itemDto.setSellerNm("Admin");
        itemDto.setSellerId("Admin");

        Item updatedItem = itemDto.toEntity();
        updatedItem.setId(target.getId());  // 기존 아이템 ID 유지
        itemMapper.updateItem(updatedItem);
        return updatedItem;
    }

    public Map<String, Object> getItemsByPage(int page, int size) {
            int offset = (page - 1) * size;
            List<Item> items = itemMapper.findItemsByPage(size, offset);
            int totalItems = itemMapper.countItems();
            int totalPages = (int) Math.ceil((double) totalItems / size);

            Map<String, Object> response = new HashMap<>();
            response.put("items", items);
            response.put("totalItems", totalItems);
            response.put("totalPages", totalPages);
            return response;
        }

}
