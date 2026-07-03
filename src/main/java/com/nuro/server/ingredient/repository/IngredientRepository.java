package com.nuro.server.ingredient.repository;

import com.nuro.server.ingredient.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    List<Ingredient> findAllByDeletedAtIsNullOrderById();
}