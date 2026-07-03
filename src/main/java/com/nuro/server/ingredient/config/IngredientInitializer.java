package com.nuro.server.ingredient.config;

import com.nuro.server.ingredient.entity.Ingredient;
import com.nuro.server.ingredient.enums.Ingredients;
import com.nuro.server.ingredient.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class IngredientInitializer implements ApplicationRunner {
    private final IngredientRepository ingredientRepository;

    @Override
    public void run(ApplicationArguments arguments){
        //이미 데이터 있으면 생성 x
        if(ingredientRepository.count()>0){
            return ;
        }
        List<Ingredient> ingredients = Arrays.stream(Ingredients.values())
                .map(i -> Ingredient.create(
                        i.getKorName(),
                        i.getEngName(),
                        i.getEwgGrade(),
                        i.getRiskLevel(),
                        i.getDataLevel(),
                        i.getDesc(),
                        i.getEffects(),
                        i.getHowToUse(),
                        i.getTip()
                ))
                .toList();

        ingredientRepository.saveAll(ingredients);
    }
}
