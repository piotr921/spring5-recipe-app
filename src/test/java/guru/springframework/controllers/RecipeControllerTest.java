package guru.springframework.controllers;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.domain.Recipe;
import guru.springframework.exceptions.NotFoundException;
import guru.springframework.services.RecipeService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class RecipeControllerTest {

    @Mock
    private RecipeService service;

    private RecipeController recipeController;
    private MockMvc mockMvc;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        recipeController = new RecipeController(service);
        mockMvc = MockMvcBuilders
                .standaloneSetup(recipeController)
                .setControllerAdvice(new ControllerExceptionHandler())
                .build();
    }

    @Test
    public void shouldGetNewRecipe() throws Exception {

        mockMvc.perform(get("/recipe/new"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("recipe"))
                .andExpect(view().name("recipe/recipeform"));
    }

    @Test
    public void testGetRecipe() throws Exception {
        Recipe recipe = new Recipe();
        recipe.setId(12L);

        when(service.findById(anyLong())).thenReturn(recipe);

        mockMvc.perform(get("/recipe/12/show"))
                .andExpect(status().isOk())
                .andExpect(view().name("recipe/show"))
                .andExpect(model().attributeExists("recipe"));
    }

    @Test
    public void shouldReturnNotFoundStatusWhenRecipeWasNotFound() throws Exception {
        Recipe recipe = new Recipe();
        recipe.setId(1L);

        when(service.findById(anyLong())).thenThrow(NotFoundException.class);

        mockMvc.perform(get("/recipe/1/show"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("404error"));
    }

    @Test
    public void shouldReturnBadRequestStatusForInvalidRecipeId() throws Exception {
        mockMvc.perform(get("/recipe/invalid_id/show"))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("400error"));
    }

    @Test
    public void shouldGetNewRecipeForm() throws Exception {
        Recipe recipe = new Recipe();
        recipe.setId(12L);

        when(service.findById(anyLong())).thenReturn(recipe);

        mockMvc.perform(get("/recipe/12/show"))
                .andExpect(status().isOk())
                .andExpect(view().name("recipe/show"))
                .andExpect(model().attributeExists("recipe"));
    }

    @Test
    public void shouldPostNewRecipeForm() throws Exception {
        RecipeCommand command = new RecipeCommand();
        command.setId(19L);

        when(service.saveRecipeCommand(any())).thenReturn(command);

        mockMvc.perform(post("/recipe")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id", "")
                .param("cookTime", "3000")
                .param("description", "desc")
                .param("directions", "Cook!"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/recipe/19/show"));
    }

    @Test
    public void shouldFailWhenValidationConstraintsNotMet() throws Exception {
        RecipeCommand command = new RecipeCommand();
        command.setId(2L);

        when(service.saveRecipeCommand(any())).thenReturn(command);

        mockMvc.perform(post("/recipe")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id", ""))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("recipe"))
                .andExpect(view().name("recipe/recipeform"));
    }

    @Test
    public void shouldUpdateView() throws Exception {
        RecipeCommand command = new RecipeCommand();
        command.setId(25L);

        when(service.findCommandById(anyLong())).thenReturn(command);

        mockMvc.perform(get("/recipe/12/update"))
                .andExpect(status().isOk())
                .andExpect(view().name("recipe/recipeform"))
                .andExpect(model().attributeExists("recipe"));
    }

    @Test
    public void shouldDeleteRecipe() throws Exception {
        mockMvc.perform(get("/recipe/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));

        verify(service, times(1)).deleteById(anyLong());
    }
}