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
import ru.practicum.shareit.itemrequest.ItemRequestClient;
import ru.practicum.shareit.itemrequest.ItemRequestController;
import ru.practicum.shareit.itemrequest.model.dto.ItemRequestDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestControllerTest {
    @MockBean
    private ItemRequestClient itemRequestClient;
    private final ObjectMapper mapper;
    private final MockMvc mvc;

    private final ItemRequestDto itemRequestDto = new ItemRequestDto(
            1,
            "item request description",
            LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
            Collections.emptyList()
    );

    private final ResponseEntity<Object> response = new ResponseEntity<>(HttpStatus.OK);

    @Test
    public void shouldAddItemRequest() throws Exception {
        Mockito
                .when(itemRequestClient.addItemRequest(anyInt(), any(ItemRequestDto.class)))
                .thenReturn(response);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldNotAddItemRequestWhenBlankDescription() throws Exception {
        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(
                                new ItemRequestDto(
                                        1,
                                        "",
                                        LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
                                        Collections.emptyList()
                                )
                        ))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Описание запроса не может быть пустым"));
    }

    @Test
    public void shouldGetItemRequestsByUserId() throws Exception {
        Mockito
                .when(itemRequestClient.getItemRequestsByUserId(anyInt()))
                .thenReturn(response);

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldGetAllItemRequests() throws Exception {
        Mockito
                .when(itemRequestClient.getAllItemRequests(anyInt(), anyInt(), anyInt()))
                .thenReturn(response);

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldGetItemRequestById() throws Exception {
        Mockito
                .when(itemRequestClient.getById(anyInt(), anyInt()))
                .thenReturn(response);

        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}