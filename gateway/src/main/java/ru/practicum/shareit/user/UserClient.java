package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.model.dto.UserDto;

@Service
@Slf4j
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getById(int userId) {
        log.info("Запрос на просмотр пользователя с id={}", userId);
        return get("/" + userId, null, null);
    }

    public ResponseEntity<Object> getUsers() {
        log.info("Запрос на просмотр всех пользователей");
        return get("", null, null);
    }

    public ResponseEntity<Object> addUser(UserDto userDto) {
        log.info("Запрос на добавление пользователя {}", userDto);
        return post("", null, userDto);
    }

    public ResponseEntity<Object> updateUser(int userId, UserDto userDto) {
        log.info("Запрос на обновление пользователя с id={}", userId);
        return patch("/" + userId, null, null, userDto);
    }

    public ResponseEntity<Object> deleteUser(int userId) {
        log.info("Запрос на удаление пользователя с id={}", userId);
        return delete("/" + userId, null);
    }
}
