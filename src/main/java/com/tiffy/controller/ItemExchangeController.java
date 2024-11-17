package com.tiffy.controller;

import com.tiffy.dto.ItemDto;
import com.tiffy.entity.Item;
import com.tiffy.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/exchange")
public class ItemExchangeController {

    @Autowired
    ItemService itemService;

    @PostMapping("/createItem")
    public ResponseEntity<Map<String, String>> createExchangeItem(@AuthenticationPrincipal String username
            , @Valid @RequestBody ItemDto itemDto) {
        return itemService.createItem(username, itemDto);
    }

    @GetMapping("/")
    public Map<String, Object> exchangeList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String searchQuery,
            @RequestParam(defaultValue = "all") String searchType,
            @RequestParam(defaultValue = "latest") String sortOption) {
        return itemService.getItemsByPage(page, size, searchQuery, searchType, sortOption);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> exchangeListDetail(@PathVariable Long id) {
        return itemService.findByItemId(id);
    }

    @DeleteMapping("/delete/{id}")
    public Map<String, String> exchangeListDelete(@PathVariable Long id) {
        return itemService.deleteItem(id);
    }

    @PutMapping("/edit/{id}")
    public Item exchangeListEdit(@AuthenticationPrincipal String username, @PathVariable Long id, @RequestBody ItemDto itemDto) {
        return itemService.editItem(username, id, itemDto);
    }
}
