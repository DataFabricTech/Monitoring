package com.mobigen.monitoring.utils;

import com.fasterxml.jackson.core.JsonParseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {
    private final Utils utils = new Utils();
    private final String tmpString = "{\"name\":\"test\", \"emptyString\": \"\", \"nullValue\": null}";


    @DisplayName("getJsonNode - 성공 값 제공 - 성공")
    @Test
    void getJsonNodeTest() {
        assertDoesNotThrow(() -> {
            utils.getJsonNode(tmpString);
        });
    }

    @DisplayName("getJsonNode - null 값 제공 - 실패")
    @Test
    void getJsonNodeNullTest() {
        assertThrows(IllegalArgumentException.class, () -> utils.getJsonNode(null));
    }

    @DisplayName("getJsonNode - emptyString 값 제공 - 성공")
    @Test
    void getJsonNodeEmptyStringTest() {
        assertDoesNotThrow(() -> utils.getJsonNode(""));
    }

    @DisplayName("getJsonNode - Wrong Json Format 제공 - 실패")
    @Test
    void getJsonNodeWrongJsonFormatTest() {
        assertThrows(JsonParseException.class, () -> utils.getJsonNode("{\"node\":\"test\",}"));
    }

    @DisplayName("getAsTextOrNull - 성공 값 제공 - 성공")
    @Test
    void getAsTextOrNull() {
        assertDoesNotThrow(() -> {
            var jsonNode = utils.getJsonNode(tmpString);
            assertEquals("test", utils.getAsTextOrNull(jsonNode.get("name")));
        });
    }

    @DisplayName("getAsTextOrNull - null 값 제공 - 성공")
    @Test
    void getAsTextOrNull_NullTest() {
        assertDoesNotThrow(() -> {
            var jsonNode = utils.getJsonNode(tmpString);
            assertNull(utils.getAsTextOrNull(jsonNode.get("nothing")));
        });
    }

    @DisplayName("getAsTextOrNull - null value 값 제공 - 성공")
    @Test
    void getAsTextOrNull_emptyStringTest() {
        assertDoesNotThrow(() -> {
            var jsonNode = utils.getJsonNode(tmpString);
            assertEquals("", utils.getAsTextOrNull(jsonNode.get("emptyString")));
        });
    }

    @DisplayName("getAsTextOrNull - emptyString 값 제공 - 성공")
    @Test
    void getAsTextOrNull_NullValueTest() {
        assertDoesNotThrow(() -> {
            var jsonNode = utils.getJsonNode(tmpString);
            assertNull(utils.getAsTextOrNull(jsonNode.get("nullValue")));
        });
    }
}