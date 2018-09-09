package guru.springframework.services;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.converters.CategoryCommandToCategory;
import guru.springframework.converters.CategoryToCategoryCommand;
import guru.springframework.converters.IngredientCommandToIngredient;
import guru.springframework.converters.IngredientToIngredientCommand;
import guru.springframework.converters.NotesCommandToNotes;
import guru.springframework.converters.NotesToNotesCommand;
import guru.springframework.converters.RecipeCommandToRecipe;
import guru.springframework.converters.RecipeToRecipeCommand;
import guru.springframework.converters.UnitOfMeasureCommandToUnitOfMeasure;
import guru.springframework.converters.UnitOfMeasureToUnitOfMeasureCommand;
import guru.springframework.domain.Recipe;
import guru.springframework.repositories.RecipeRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class RecipeServiceImplTest {

    RecipeServiceImpl recipeService;

    @Mock
    RecipeRepository recipeRepository;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        recipeService = new RecipeServiceImpl(recipeRepository,
            new RecipeCommandToRecipe(new CategoryCommandToCategory(), new IngredientCommandToIngredient(new UnitOfMeasureCommandToUnitOfMeasure()),
                new NotesCommandToNotes()),
            new RecipeToRecipeCommand(new IngredientToIngredientCommand(new UnitOfMeasureToUnitOfMeasureCommand()),
                new NotesToNotesCommand(), new CategoryToCategoryCommand()));
    }

    @Test
    public void getRecipes() {

        Recipe recipe = new Recipe();
        HashSet recipiesData = new HashSet();
        recipiesData.add(recipe);

        when(recipeService.getRecipes()).thenReturn(recipiesData);

        Set<Recipe> recipes = recipeService.getRecipes();

        assertEquals(recipes.size(), 1);
        verify(recipeRepository, times(1)).findAll();
    }

    @Test
    public void getRecipesById() {
        // given
        Recipe recipe = new Recipe();
        final long testId = 23L;
        recipe.setId(testId);
        recipe.setDescription("Test recipe");
        Optional<Recipe> recipeOptional = Optional.of(recipe);

        when(recipeRepository.findById(anyLong())).thenReturn(recipeOptional);

        // when
        Recipe recipeById = recipeService.findById(testId);

        // then
        assertNotNull("Null recipe returned", recipeById);
        assertEquals("Test recipe", recipeById.getDescription());
        verify(recipeRepository, times(1)).findById(anyLong());
        verify(recipeRepository, never()).findAll();
    }

    @Test
    public void shouldDeleteRecipe() {
        // given
        Long idToDelete = 2L;
        recipeService.deleteById(idToDelete);

        // when

        // then
        verify(recipeRepository, times(1)).deleteById(anyLong());
    }

    @Test
    public void shouldFindCommandById() {
        // given
        Recipe recipe = new Recipe();
        final long testId = 23L;
        recipe.setId(testId);
        recipe.setDescription("Test recipe");
        Optional<Recipe> recipeOptional = Optional.of(recipe);

        when(recipeRepository.findById(anyLong())).thenReturn(recipeOptional);

        // when
        RecipeCommand foundRecipe = recipeService.findCommandById(23L);

        // then
        assertEquals(new Long("23"), foundRecipe.getId());
        verify(recipeRepository, times(1)).findById(anyLong());
    }
}