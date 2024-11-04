package com.tiffy.controller;

import com.tiffy.constant.ItemSellStatus;
import com.tiffy.dto.ItemDto;
import com.tiffy.entity.Item;
import com.tiffy.repository.ItemRepository;
import com.tiffy.repository.UserRepository;
import com.tiffy.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/exchange")
@CrossOrigin(origins = "http://localhost:8080")
public class ItemExchangeController {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemService itemService;

    @PostMapping("/createItem")
    public Item createExchangeItem(@Valid @RequestBody ItemDto itemDto) {
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
        return itemRepository.save(item);
    }

    @GetMapping("/")
    public Map<String, Object> exchangeList(@PageableDefault(page = 1) Pageable pageable, Model model) {
        Page<ItemDto> itemPages = itemService.paging(pageable);

        int blockLimit = 10; // page 개수 설정
        int currentPage = pageable.getPageNumber();
        int startPage = (((int) Math.ceil((double) currentPage / blockLimit)) - 1) * blockLimit + 1;
        int endPage = Math.min(startPage + blockLimit - 1, itemPages.getTotalPages());

        Map<String, Object> response = new HashMap<>();
        response.put("itemList", itemPages.getContent());
        response.put("startPage", startPage);
        response.put("endPage", endPage);
        response.put("totalPages", itemPages.getTotalPages());

        return response;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> exchangeListDetail(@PathVariable Long id) {
        Item item = itemRepository.findById(id).orElse(null);
        if (item == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(item);
    }

    @DeleteMapping("/delete/{id}")
    public Map<String, String> exchangeListDelete(@PathVariable Long id) {
        Map<String, String> response = new HashMap<>();
        if (itemRepository.existsById(id)) {
            itemRepository.deleteById(id);
            response.put("message", "상품을 정상적으로 삭제했습니다.");
        } else {
            response.put("error", "상품을 찾을 수 없습니다.");
        }
        return response;
    }

    @PutMapping("/edit/{id}")
    public Item exchangeListEdit(@PathVariable Long id, @RequestBody ItemDto itemDto) {
        Item target = itemRepository.findById(id).orElseThrow(() -> new RuntimeException("Item not found"));
        LocalDateTime now = LocalDateTime.now();
        itemDto.setUpdateTime(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        itemDto.setSellerNm("Admin");
        itemDto.setSellerId("Admin");

        Item updatedItem = itemDto.toEntity();
        updatedItem.setId(target.getId());  // 기존 아이템 ID 유지
        return itemRepository.save(updatedItem);
    }

    @PutMapping("/update")
    public Item exchangeListUpdate(@RequestBody ItemDto itemDto) {
        LocalDateTime now = LocalDateTime.now();
        itemDto.setUpdateTime(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        itemDto.setSellerNm("Admin");
        itemDto.setSellerId("Admin");

        Item item = itemDto.toEntity();
        Item target = itemRepository.findById(item.getId()).orElse(null);
        if (target != null) {
            return itemRepository.save(item);
        }
        throw new RuntimeException("Item not found");
    }


}
