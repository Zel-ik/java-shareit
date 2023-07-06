package ru.practicum.shareit.user.model.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = {UserDto.class})
public class UserDtoTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testDeserializeValidDto() throws IOException {
        String json = "{\"id\":1,\"email\":\"user@example.com\",\"name\":\"John Doe\"}";

        UserDto userDto = objectMapper.readValue(json, UserDto.class);

        assertThat(userDto.getId()).isEqualTo(1L);
        assertThat(userDto.getEmail()).isEqualTo("user@example.com");
        assertThat(userDto.getName()).isEqualTo("John Doe");
    }
}