package ru.practicum.shareit.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.model.dto.CommentDto;
import ru.practicum.shareit.item.model.dto.ItemDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemControllerTest {
    @MockBean
    private ItemClient itemClient;
    private final ObjectMapper mapper;
    private final MockMvc mvc;

    private final ItemDto itemDto = new ItemDto(
            1,
            "item1",
            "item1 description",
            true,
            null,
            null,
            null,
            Collections.emptyList()
    );

    private final ResponseEntity<Object> response = new ResponseEntity<>(HttpStatus.OK);

    @Test
    public void shouldGetItemById() throws Exception {
        Mockito
                .when(itemClient.getById(anyInt(), anyInt()))
                .thenReturn(response);

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldGetItemsByUserId() throws Exception {
        Mockito
                .when(itemClient.getItemsByUserId(anyInt(), anyInt(), anyInt()))
                .thenReturn(response);

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldAddItem() throws Exception {
        Mockito
                .when(itemClient.addItem(anyInt(), any(ItemDto.class)))
                .thenReturn(response);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldNotAddItemWhenBlankName() throws Exception {
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(
                                new ItemDto(
                                        1,
                                        "",
                                        "item1 description",
                                        true,
                                        null,
                                        null,
                                        null,
                                        Collections.emptyList()
                                )
                        ))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Имя вещи не может быть пустым"));
    }

    @Test
    public void shouldNotAddItemWhenBlankDescription() throws Exception {
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(
                                new ItemDto(
                                        1,
                                        "item1",
                                        "",
                                        true,
                                        null,
                                        null,
                                        null,
                                        Collections.emptyList()
                                )
                        ))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Описание вещи не может быть пустым"));
    }

    @Test
    public void shouldNotAddItemWhenIsAvailableNull() throws Exception {
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(
                                new ItemDto(
                                        1,
                                        "item1",
                                        "item1 description",
                                        null,
                                        null,
                                        null,
                                        null,
                                        Collections.emptyList()
                                )
                        ))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Должна быть указана доступность вещи"));
    }

    @Test
    public void shouldUpdateItem() throws Exception {
        Mockito
                .when(itemClient.updateItem(anyInt(), anyInt(), any(ItemDto.class)))
                .thenReturn(response);

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldDeleteItem() throws Exception {
        Mockito
                .when(itemClient.deleteItem(anyInt(), anyInt()))
                .thenReturn(response);
        mvc.perform(delete("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldFindItems() throws Exception {
        Mockito
                .when(itemClient.findItems(anyInt(), anyString(), anyInt(), anyInt()))
                .thenReturn(response);

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1)
                        .param("text", "item")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldAddComment() throws Exception {
        CommentDto commentDto = new CommentDto(
                1,
                "test comment text",
                "user1",
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        );
        Mockito
                .when(itemClient.addComment(anyInt(), anyInt(), any(CommentDto.class)))
                .thenReturn(response);

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldNotAddCommentWhenBlankText() throws Exception {
        CommentDto commentDto = new CommentDto(
                1,
                "",
                "user1",
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        );

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Текст комментария не может быть пустым"));
    }
}
