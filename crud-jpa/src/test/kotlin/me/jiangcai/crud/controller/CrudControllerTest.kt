package me.jiangcai.crud.controller

import me.jiangcai.crud.BaseTest2
import me.jiangcai.crud.env.entity.Item
import me.jiangcai.crud.env.entity2.User
import org.junit.Test
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate
import javax.annotation.Resource
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

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

//
//        // 继续
//        // 现在新增
//        val newData = HashMap<String, Any>()
//        newData["name"] = "中文呢？"
//        newData["other"] = "非标数据"
//
//        // 新增并且获得地址
//        val newOneUri = mockMvc.perform(
//            post("/items2")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsBytes(newData))
//        )
//            .andExpect(status().isCreated())
//            .andExpect(header().string("Location", StartsWith("/items2/")))
//            .andReturn().response.getHeader("Location")
//
//        // 新增的URI可以打开正确的资源
//        mockMvc.perform(get(newOneUri))
//            .andExpect(status().isOk())
//            .andDo(print())
//
//        mockMvc.perform(get("$newOneUri/detail"))
//            .andExpect(status().isOk())
//            .andDo(print())
//
//        // 现在看看吧
//        mockMvc.perform(
//            get("/items2")
//        )
//            .andDo(print())
//            .andExpect(status().isOk())
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
//        // 4.2 修改测试
//        val int1 = random.nextInt()
//        mockMvc.perform(
//            put("/items2/1/int1")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(int1.toString())
//        )
//            .andExpect(status().isAccepted())
//        mockMvc.perform(
//            get("/items2/1")
//                .accept(MediaType.APPLICATION_JSON)
//        )
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.int1").value(int1))
//
//        val int2 = random.nextInt()
//        mockMvc.perform(
//            put("/items2/1/int2")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(int2.toString())
//        )
//            .andExpect(status().isAccepted())
//        mockMvc.perform(
//            get("/items2/1")
//                .accept(MediaType.APPLICATION_JSON)
//        )
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.int2").value(int2))
//
//        val bytes = ByteArray(1)
//        random.nextBytes(bytes)
//        val byte1 = bytes[0].toInt()
//        mockMvc.perform(
//            put("/items2/1/byte1")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(byte1.toString())
//        )
//            .andExpect(status().isAccepted())
//        mockMvc.perform(
//            get("/items2/1")
//                .accept(MediaType.APPLICATION_JSON)
//        )
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.byte1").value(byte1))
//
//        val char1 = 'a'
//        mockMvc.perform(
//            put("/items2/1/char1")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("\"" + char1 + "\"")
//        )
//            .andExpect(status().isAccepted())
//        assertThat(testItem().char1)
//            .isEqualTo(char1)
//
//        mockMvc.perform(
//            put("/items2/1/short1")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(byte1.toString())
//        )
//            .andExpect(status().isAccepted())
//        mockMvc.perform(
//            get("/items2/1")
//                .accept(MediaType.APPLICATION_JSON)
//        )
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.short1").value(byte1))
//
//        val boolean1 = random.nextBoolean()
//        mockMvc.perform(
//            put("/items2/1/boolean1")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(boolean1.toString())
//        )
//            .andExpect(status().isAccepted())
//        mockMvc.perform(
//            get("/items2/1")
//                .accept(MediaType.APPLICATION_JSON)
//        )
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.boolean1").value(boolean1))
//
//        val float1 = random.nextFloat()
//        mockMvc.perform(
//            put("/items2/1/float1")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(float1.toString())
//        )
//            .andExpect(status().isAccepted())
//        mockMvc.perform(
//            get("/items2/1")
//                .accept(MediaType.APPLICATION_JSON)
//        )
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.float1").value(float1))
//
//        val double1 = random.nextDouble()
//        mockMvc.perform(
//            put("/items2/1/double1")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(double1.toString())
//        )
//            .andExpect(status().isAccepted())
//        mockMvc.perform(
//            get("/items2/1")
//                .accept(MediaType.APPLICATION_JSON)
//        )
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.double1").value(double1))
//
//        val long1 = random.nextLong()
//        mockMvc.perform(
//            put("/items2/1/long1")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(long1.toString())
//        )
//            .andExpect(status().isAccepted())
//        mockMvc.perform(
//            get("/items2/1")
//                .accept(MediaType.APPLICATION_JSON)
//        )
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.long1").value(long1))
//
//        val string1 = randomString(6)
//        mockMvc.perform(
//            put("/items2/1/string1")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("\"" + string1 + "\"")
//        )
//            .andExpect(status().isAccepted())
//        mockMvc.perform(
//            get("/items2/1")
//                .accept(MediaType.APPLICATION_JSON)
//        )
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.string1").value(string1))
//
//        val int3 = random.nextInt()
//        mockMvc.perform(
//            put("/items2/1/bigInteger1")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(int3.toString())
//        )
//            .andExpect(status().isAccepted())
//        mockMvc.perform(
//            get("/items2/1")
//                .accept(MediaType.APPLICATION_JSON)
//        )
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.bigInteger1").value(int3))
//
//        val bigDecimal1 = BigDecimal.valueOf(random.nextDouble())
//        mockMvc.perform(
//            put("/items2/1/bigDecimal1")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(bigDecimal1.toString())
//        )
//            .andExpect(status().isAccepted())
//        mockMvc.perform(
//            get("/items2/1")
//                .accept(MediaType.APPLICATION_JSON)
//        )
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.bigDecimal1").value(bigDecimal1))
//
//        val date1 = Date()
//        mockMvc.perform(
//            put("/items2/1/date1")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(SimpleDateFormat("\"yyyy-MM-dd HH:mm:ss\"").format(date1))
//        )
//            .andExpect(status().isAccepted())
//        assertThat(testItem().date1)
//            .isEqualToIgnoringMillis(date1)
//
//        // 支持修改成空
//        mockMvc.perform(
//            put("/items2/1/date1")
//            //                        .contentType(MediaType.APPLICATION_JSON)
//            //                        .content(new SimpleDateFormat("\"yyyy-MM-dd HH:mm:ss\"").format(date1))
//        )
//            .andExpect(status().isAccepted())
//        assertThat(testItem().date1)
//            .isNull()

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

    private fun testItem(): Item {
        return entityManager.find(Item::class.java, 1L)
    }

}