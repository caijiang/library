package me.jiangcai.crud.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.jiangcai.crud.BaseTest;
import me.jiangcai.crud.env.entity.Item;
import org.junit.Test;
import org.mockito.internal.matchers.StartsWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author CJ
 */
public class AbstractCrudControllerTest extends BaseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private EntityManager entityManager;

    @Test
    public void go() throws Exception {
        // 这个应该还不存在
        mockMvc.perform(get("/items/1"))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/items/1/detail"))
                .andExpect(status().isNotFound());

        // 继续
        // 现在新增
        Map<String, Object> newData = new HashMap<>();
        newData.put("name", "中文呢？");
        newData.put("other", "非标数据");

        // 新增并且获得地址
        String newOneUri = mockMvc.perform(
                post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(newData))
        )
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", new StartsWith("/items/")))
                .andReturn().getResponse().getHeader("Location");

        // 新增的URI可以打开正确的资源
        mockMvc.perform(get(newOneUri))
                .andExpect(status().isOk())
                .andDo(print());

        mockMvc.perform(get(newOneUri + "/detail"))
                .andExpect(status().isOk())
                .andDo(print());

        // 现在看看吧
        mockMvc.perform(
                get("/items")
        )
                .andDo(print())
                .andExpect(status().isOk());
        mockMvc.perform(
                get("/items/ant-d")
        )
                .andDo(print())
                .andExpect(status().isOk());
        mockMvc.perform(
                get("/items/ant-d")
                        .param("sorter", "name_descend")
        )
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(
                get("/items/jQuery")
        )
                .andDo(print())
                .andExpect(status().isOk());
        mockMvc.perform(
                get("/items/select2")
        )
                .andDo(print())
                .andExpect(status().isOk());

        // 4.2 修改测试
        int int1 = getRandom().nextInt();
        mockMvc.perform(
                put("/items/1/int1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(int1))
        )
                .andExpect(status().isAccepted());
        mockMvc.perform(
                get("/items/1")
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.int1").value(int1));

        int int2 = getRandom().nextInt();
        mockMvc.perform(
                put("/items/1/int2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(int2))
        )
                .andExpect(status().isAccepted());
        mockMvc.perform(
                get("/items/1")
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.int2").value(int2));

        byte[] bytes = new byte[1];
        getRandom().nextBytes(bytes);
        int byte1 = bytes[0];
        mockMvc.perform(
                put("/items/1/byte1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(byte1))
        )
                .andExpect(status().isAccepted());
        mockMvc.perform(
                get("/items/1")
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.byte1").value(byte1));

        char char1 = 'a';
        mockMvc.perform(
                put("/items/1/char1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"" + char1 + "\"")
        )
                .andExpect(status().isAccepted());
        assertThat(testItem().getChar1())
                .isEqualTo(char1);

        int short1 = byte1;
        mockMvc.perform(
                put("/items/1/short1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(short1))
        )
                .andExpect(status().isAccepted());
        mockMvc.perform(
                get("/items/1")
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.short1").value(short1));

        boolean boolean1 = getRandom().nextBoolean();
        mockMvc.perform(
                put("/items/1/boolean1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(boolean1))
        )
                .andExpect(status().isAccepted());
        mockMvc.perform(
                get("/items/1")
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.boolean1").value(boolean1));

        float float1 = getRandom().nextFloat();
        mockMvc.perform(
                put("/items/1/float1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(float1))
        )
                .andExpect(status().isAccepted());
        mockMvc.perform(
                get("/items/1")
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.float1").value(float1));

        double double1 = getRandom().nextDouble();
        mockMvc.perform(
                put("/items/1/double1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(double1))
        )
                .andExpect(status().isAccepted());
        mockMvc.perform(
                get("/items/1")
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.double1").value(double1));

        long long1 = getRandom().nextLong();
        mockMvc.perform(
                put("/items/1/long1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(long1))
        )
                .andExpect(status().isAccepted());
        mockMvc.perform(
                get("/items/1")
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.long1").value(long1));

        String string1 = randomString(6);
        mockMvc.perform(
                put("/items/1/string1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"" + String.valueOf(string1) + "\"")
        )
                .andExpect(status().isAccepted());
        mockMvc.perform(
                get("/items/1")
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.string1").value(string1));

        int int3 = getRandom().nextInt();
        mockMvc.perform(
                put("/items/1/bigInteger1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(int3))
        )
                .andExpect(status().isAccepted());
        mockMvc.perform(
                get("/items/1")
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bigInteger1").value(int3));

        BigDecimal bigDecimal1 = BigDecimal.valueOf(getRandom().nextDouble());
        mockMvc.perform(
                put("/items/1/bigDecimal1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(bigDecimal1))
        )
                .andExpect(status().isAccepted());
        mockMvc.perform(
                get("/items/1")
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bigDecimal1").value(bigDecimal1));

        Date date1 = new Date();
        mockMvc.perform(
                put("/items/1/date1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new SimpleDateFormat("\"yyyy-MM-dd HH:mm:ss\"").format(date1))
        )
                .andExpect(status().isAccepted());
        assertThat(testItem().getDate1())
                .isEqualToIgnoringMillis(date1);

    }

    private Item testItem() {
        return entityManager.find(Item.class, 1L);
    }

}