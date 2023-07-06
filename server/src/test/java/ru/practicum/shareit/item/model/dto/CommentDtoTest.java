package ru.practicum.shareit.item.model.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.shareit.validateInterfaces.Create;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CommentDtoTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private Validator validator;

    @Test
    void testSerialize() throws JsonProcessingException {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Some text");
        commentDto.setAuthorName("John");
        commentDto.setCreated(LocalDateTime.parse("2023-06-22T12:05:09.700"));

        String json = objectMapper.writeValueAsString(commentDto);

        assertThat(json).isEqualTo("{\"id\":1,\"text\":\"Some text\",\"authorName\":\"John\"," +
                "\"created\":\"2023-06-22T12:05:09.7\"}");
    }

    @Test
    void testDeserialize() throws JsonProcessingException {
        String json = "{\"id\":1,\"text\":\"Some text\",\"authorName\":\"John\"," +
                "\"created\":\"2023-06-22T12:05:09.700\"}";

        CommentDto commentDto = objectMapper.readValue(json, CommentDto.class);

        assertThat(commentDto.getId()).isEqualTo(1L);
        assertThat(commentDto.getText()).isEqualTo("Some text");
        assertThat(commentDto.getAuthorName()).isEqualTo("John");
        assertThat(commentDto.getCreated()).isEqualTo(LocalDateTime.parse("2023-06-22T12:05:09.700"));
    }

    @Test
    void testValidationSuccess() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Some text");

        Set<ConstraintViolation<CommentDto>> violations = validator.validate(commentDto, Create.class);

        assertThat(violations).isEmpty();
    }

    @Configuration
    static class CommentDtoTestConfig {
        @Bean
        public Validator validator() {
            return Validation.buildDefaultValidatorFactory().getValidator();
        }
    }
}