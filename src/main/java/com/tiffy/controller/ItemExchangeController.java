package com.tiffy.controller;

import com.tiffy.dto.ItemDto;
import com.tiffy.entity.Item;
import com.tiffy.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/exchange")
@CrossOrigin(origins = "http://localhost:8080")
public class ItemExchangeController {

    @Autowired
    ItemService itemService;

    @PostMapping("/createItem")
    public ResponseEntity<Map<String, String>> createExchangeItem(@Valid @RequestBody ItemDto itemDto) {
        return itemService.createItem(itemDto);
    }

    @GetMapping("/")
    public Map<String, Object> exchangeList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return itemService.getItemsByPage(page, size);
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
    public Item exchangeListEdit(@PathVariable Long id, @RequestBody ItemDto itemDto) {
        return itemService.editItem(id, itemDto);
    }
}
