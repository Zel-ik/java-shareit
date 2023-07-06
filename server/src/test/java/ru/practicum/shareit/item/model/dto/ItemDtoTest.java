package ru.practicum.shareit.item.model.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private Validator validator;

    @Test
    public void testSerializeItemDto() throws JsonProcessingException {
        ItemDto item = new ItemDto();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("This is a test item description");
        item.setAvailable(true);

        String json = objectMapper.writeValueAsString(item);

        assertThat(json).isEqualTo("{\"id\":1,\"name\":\"Test Item\",\"description\":\"This is a test item " +
                "description\",\"available\":true,\"lastBooking\":null,\"nextBooking\":null,\"comments\":null," +
                "\"requestId\":null}");
    }

    @Test
    public void testDeserializeItemDto() throws JsonProcessingException {
        String json = "{\"id\":2,\"name\":\"Test Item 2\",\"description\":\"This is another test item description\"," +
                "\"available\":false}";

        ItemDto item = objectMapper.readValue(json, ItemDto.class);

        assertThat(item.getId()).isEqualTo(2L);
        assertThat(item.getName()).isEqualTo("Test Item 2");
        assertThat(item.getDescription()).isEqualTo("This is another test item description");
        assertThat(item.getAvailable()).isFalse();
    }

    @Test
    void givenValidItemDto_whenValidate_thenNoConstraintViolations() {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Name");
        itemDto.setDescription("Description");
        itemDto.setAvailable(true);

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto);

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