package me.jiangcai.crud.controller

import me.jiangcai.crud.BaseTest2
import me.jiangcai.crud.env.entity.Item
import me.jiangcai.crud.env.entity2.User
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.number.IsCloseTo
import org.junit.Test
import org.mockito.internal.matchers.StartsWith
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*
import javax.annotation.Resource
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import kotlin.collections.HashMap

/**
 * @author CJ
 */
class CrudControllerTest : BaseTest2() {

    @PersistenceContext
    private lateinit var entityManager: EntityManager
    @Resource
    private lateinit var transactionManager: PlatformTransactionManager

    @Test
    fun go() {
        TransactionTemplate(transactionManager)
            .execute {
                @Suppress("JpaQlInspection")
                entityManager.createQuery("delete from Item").executeUpdate()
            }

        // 这个应该还不存在
        mockMvc.perform(get("/items2/1", userAsRole("X")))
            .andExpect(status().isForbidden)
        mockMvc.perform(get("/items2/1", userAsRole("R")))
            .andExpect(status().isNotFound)


        // 继续
        // 现在新增
        val newData = HashMap<String, Any>()
        newData["name"] = "中文呢？"
        newData["other"] = "非标数据"

        mockMvc.perform(
            post("/items2", userAsRole("X"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(newData))
        )
            .andExpect(status().isForbidden)

        // 新增并且获得地址
        val newOneUri = mockMvc.perform(
            post("/items2", userAsRole("C"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(newData))
        )
            .andExpect(status().isCreated)
            .andExpect(header().string("Location", StartsWith("/items2/")))
            .andReturn().response.getHeader("Location")

        // 新增的URI可以打开正确的资源
        mockMvc.perform(get(newOneUri, userAsRole("R")))
            .andExpect(status().isOk)


        // 现在看看吧
        mockMvc.perform(
            get("/items2", userAsRole("R"))
        )
//            .andDo(print())
            .andExpect(status().isOk)

//        mockMvc.perform(
//            get("/items2/ant-d")
//        )
//            .andDo(print())
//            .andExpect(status().isOk())
//        mockMvc.perform(
//            get("/items2/ant-d")
//                .param("sorter", "name_descend")
//        )
//            .andDo(print())
//            .andExpect(status().isOk())
//
//        mockMvc.perform(
//            get("/items2/jQuery")
//        )
//            .andDo(print())
//            .andExpect(status().isOk())
//        mockMvc.perform(
//            get("/items2/select2")
//        )
//            .andDo(print())
//            .andExpect(status().isOk())
//
        // 4.2 修改测试
        val int1 = random.nextInt()
        mockMvc.perform(
            put("$newOneUri/int1", userAsRole("C"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(int1.toString())
        )
            .andExpect(status().isForbidden)
        mockMvc.perform(
            put("$newOneUri/int1", userAsRole("U"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(int1.toString())
        )
            .andExpect(status().isAccepted)
        mockMvc.perform(
            get(newOneUri, userAsRole("R"))
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.int1").value(int1))

        //
        val int2 = random.nextInt()
        mockMvc.perform(
            put("$newOneUri/int2", userAsRole("U"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(int2.toString())
        )
            .andExpect(status().isForbidden)
        mockMvc.perform(
            put("$newOneUri/int2", userAsRole("U2"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(int2.toString())
        )
            .andExpect(status().isAccepted)
        mockMvc.perform(
            get(newOneUri, userAsRole("R"))
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.int2").value(int2))


        val bytes = ByteArray(1)
        random.nextBytes(bytes)
        val byte1 = bytes[0].toInt()
        mockMvc.perform(
            put("$newOneUri/byte1", userAsRole("ANY"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(byte1.toString())
        )
            .andExpect(status().isAccepted)
        mockMvc.perform(
            get(newOneUri, userAsRole("R"))
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.byte1").value(byte1))

        val char1 = 'a'
        mockMvc.perform(
            put("$newOneUri/char1", userAsRole("U"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("\"" + char1 + "\"")
        )
            .andExpect(status().isAccepted)

        assertThat(testItem(newOneUri).char1)
            .isEqualTo(char1)

        mockMvc.perform(
            put("$newOneUri/short1", userAsRole("U"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(byte1.toString())
        )
            .andExpect(status().isAccepted)
        mockMvc.perform(
            get(newOneUri, userAsRole("R"))
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.short1").value(byte1))

        val boolean1 = random.nextBoolean()
        mockMvc.perform(
            put("$newOneUri/boolean1", userAsRole("U"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(boolean1.toString())
        )
            .andExpect(status().isAccepted)
        mockMvc.perform(
            get(newOneUri, userAsRole("R"))
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.boolean1").value(boolean1))

        val float1 = random.nextFloat()
        mockMvc.perform(
            put("$newOneUri/float1", userAsRole("U"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(float1.toString())
        )
            .andExpect(status().isAccepted)
        mockMvc.perform(
            get(newOneUri, userAsRole("R"))
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.float1").value(float1))

        val double1 = random.nextDouble()
        mockMvc.perform(
            put("$newOneUri/double1", userAsRole("U"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(double1.toString())
        )
            .andExpect(status().isAccepted)
        mockMvc.perform(
            get(newOneUri, userAsRole("R"))
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.double1").value(double1))

        val long1 = random.nextLong()
        mockMvc.perform(
            put("$newOneUri/long1", userAsRole("U"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(long1.toString())
        )
            .andExpect(status().isAccepted)
        mockMvc.perform(
            get(newOneUri, userAsRole("R"))
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.long1").value(long1))

        val string1 = randomString(6)
        mockMvc.perform(
            put("$newOneUri/string1", userAsRole("U"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("\"" + string1 + "\"")
        )
            .andExpect(status().isAccepted)
        mockMvc.perform(
            get(newOneUri, userAsRole("R"))
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.string1").value(string1))

        val int3 = random.nextInt()
        mockMvc.perform(
            put("$newOneUri/bigInteger1", userAsRole("U"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(int3.toString())
        )
            .andExpect(status().isAccepted)
        mockMvc.perform(
            get(newOneUri, userAsRole("R"))
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.bigInteger1").value(int3))

        val bigDecimal1 = BigDecimal.valueOf(random.nextDouble())
        mockMvc.perform(
            put("$newOneUri/bigDecimal1", userAsRole("U"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(bigDecimal1.toString())
        )
            .andExpect(status().isAccepted)
        mockMvc.perform(
            get(newOneUri, userAsRole("R"))
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.bigDecimal1").value(IsCloseTo(bigDecimal1.toDouble(), 0.01)))
//            .andExpect(jsonPath("$.bigDecimal1").value(bigDecimal1.setScale(0, RoundingMode.HALF_UP)))

        val date1 = Date()
        mockMvc.perform(
            put("$newOneUri/date1", userAsRole("U"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(SimpleDateFormat("\"yyyy-MM-dd HH:mm:ss\"").format(date1))
        )
            .andExpect(status().isAccepted)
        assertThat(testItem(newOneUri).date1)
            .isEqualToIgnoringMillis(date1)

        // 支持修改成空
        mockMvc.perform(
            put("$newOneUri/date1", userAsRole("U"))
        )
            .andExpect(status().isAccepted)
        assertThat(testItem(newOneUri).date1)
            .isNull()

        // 现在开始测试过滤器。
        // 首先给目标item 一个期望的name
        val name1 = randomMobile()
        mockMvc.perform(
            put("$newOneUri/name", userAsRole("U"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("\"$name1\"")
        )
            .andExpect(status().isAccepted)

        mockMvc.perform(
            get("/items2", userAsRole("R"))
                .param("filter", "name_$name1")
        )
            .andDo(print())
            .andExpect(status().isOk)

        mockMvc.perform(
            get("/items2", userAsRole("R"))
                .param("filter", "nameSize_0")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(0))

        mockMvc.perform(
            delete(newOneUri, userAsRole("HELLO"))
        )
            .andExpect(status().isForbidden)

        mockMvc.perform(
            delete(newOneUri, userAsRole("D"))
        )
            .andExpect(status().isNoContent)

        mockMvc.perform(
            get(newOneUri, userAsRole("R"))
        )
            .andExpect(status().isNotFound)

        // 聚合测试。

        val newOneUri2 = mockMvc.perform(
            post("/items3", userAsRole("C"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(newData))
        )
            .andExpect(status().isCreated)
            .andExpect(header().string("Location", StartsWith("/items3/")))
            .andReturn().response.getHeader("Location")

        mockMvc.perform(
            get("/items3", userAsRole("R"))
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.sumInt1").value(0))
            .andExpect(jsonPath("$.count").value(1))

        mockMvc.perform(
            put("$newOneUri2/int1", userAsRole("U"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(int1.toString())
        )
            .andExpect(status().isAccepted)

        mockMvc.perform(
            get("/items3", userAsRole("R"))
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.sumInt1").value(int1))
            .andExpect(jsonPath("$.count").value(1))

        mockMvc.perform(
            delete(newOneUri2, userAsRole("D"))
        )
            .andExpect(status().isNoContent)
    }

    private fun userAsRole(role: String): User {
        return TransactionTemplate(transactionManager)
            .execute {
                val user = User()
                user.authorities = setOf(role)
                entityManager.persist(user)
                entityManager.flush()
                user
            }
    }

    private fun testItem(uri: String): Item {
        return entityManager.find(Item::class.java, uri.removePrefix("/items2/").toLong())
    }

}