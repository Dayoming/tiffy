package com.tiffy.controller;

import com.tiffy.dto.RecipeDto;
import com.tiffy.service.RecipeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @PostMapping("/search")
    public Map<String, Object> getRecipesByIngredients(@RequestParam String ingredients) {
        List<RecipeDto> recipes = recipeService.getRecipes(ingredients);

        Map<String, Object> response = new HashMap<>();
        response.put("recipes", recipes);
        return response;
    }
}