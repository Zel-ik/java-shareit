package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestDtoTest {
    @Autowired
    private JacksonTester<ItemRequestDto> jacksonTesterDto;

    @Test
    void testJsonItemRequestDto() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("test");
        itemDto.setDescription("test description");
        itemDto.setAvailable(true);

        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setId(1L);
        requestDto.setDescription("description");
        requestDto.setCreated(LocalDateTime.of(2023, 1, 10, 14, 0, 0));

        JsonContent<ItemRequestDto> result = jacksonTesterDto.write(requestDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2023-01-10T14:00:00");
    }
}
