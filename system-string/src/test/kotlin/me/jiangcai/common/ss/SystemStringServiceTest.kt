package me.jiangcai.common.ss

import me.jiangcai.common.jpa.EnableJpa
import me.jiangcai.common.jpa.JpaPackageScanner
import org.apache.commons.lang3.RandomStringUtils
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Offset
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

/**
 * @author CJ
 */
@ContextConfiguration(classes = [SystemStringConfig::class, SystemStringServiceTest.Config::class])
//@RunWith(SpringJUnit4ClassRunner::class)
//@WebAppConfiguration
@SpringBootTest
@AutoConfigureMockMvc
class SystemStringServiceTest {


    private val random = Random()

    @Autowired
    private lateinit var systemStringService: SystemStringService


    @Test
    @Throws(Exception::class)
    fun updateSystemString() {
        val key = UUID.randomUUID().toString()
        val decimal = BigDecimal(random.nextDouble())
        systemStringService.updateSystemString(key, decimal)
        assertThat(systemStringService.getSystemString(key, BigDecimal::class.java))
            .isCloseTo(decimal, Offset.offset(BigDecimal("0.000001")))
    }

    @Test
    @Throws(Exception::class)
    fun updateSystemString1() {
        val key = UUID.randomUUID().toString()
        val value = random.nextBoolean()
        systemStringService.updateSystemString(key, value)
        assertThat(systemStringService.getSystemString(key, Boolean::class.java))
            .isEqualTo(value)
    }

    @Test
    @Throws(Exception::class)
    fun updateSystemString2() {
        val key = UUID.randomUUID().toString()
        val bytes = ByteArray(1)
        random.nextBytes(bytes)
        val value = bytes[0]
        systemStringService.updateSystemString(key, value)
        assertThat(systemStringService.getSystemString(key, Byte::class.java))
            .isEqualTo(value)
    }

    @Test
    @Throws(Exception::class)
    fun updateSystemString3() {
        val key = UUID.randomUUID().toString()
        val value = random.nextInt(32767).toShort()
        systemStringService.updateSystemString(key, value)
        assertThat(systemStringService.getSystemString(key, Short::class.java))
            .isEqualTo(value)
    }

    @Test
    @Throws(Exception::class)
    fun updateSystemString4() {
        val key = UUID.randomUUID().toString()
        val value = RandomStringUtils.random(1)[0]
        systemStringService.updateSystemString(key, value)
        assertThat(systemStringService.getSystemString(key, Char::class.java))
            .isEqualTo(value)
    }

    @Test
    @Throws(Exception::class)
    fun updateSystemString5() {
        val key = UUID.randomUUID().toString()
        val value = random.nextInt()
        systemStringService.updateSystemString(key, value)
        assertThat(systemStringService.getSystemString(key, Int::class.java))
            .isEqualTo(value)
    }

    @Test
    @Throws(Exception::class)
    fun updateSystemString6() {
        val key = UUID.randomUUID().toString()
        val value = random.nextLong()
        systemStringService.updateSystemString(key, value)
        assertThat(systemStringService.getSystemString(key, Long::class.java))
            .isEqualTo(value)
    }

    @Test
    @Throws(Exception::class)
    fun updateSystemString7() {
        val key = UUID.randomUUID().toString()
        val value = random.nextFloat()
        systemStringService.updateSystemString(key, value)
        assertThat(systemStringService.getSystemString(key, Float::class.java))
            .isEqualTo(value)
    }

    @Test
    @Throws(Exception::class)
    fun updateSystemString8() {
        val key = UUID.randomUUID().toString()
        val value = random.nextDouble()
        systemStringService.updateSystemString(key, value)
        assertThat(systemStringService.getSystemString(key, Double::class.java))
            .isEqualTo(value)
        assertThat(systemStringService.getSystemString(key, java.lang.Double.TYPE))
            .isEqualTo(value)
    }

    @Test
    @Throws(Exception::class)
    fun updateSystemString9() {
        val key = UUID.randomUUID().toString()
        val value = RandomStringUtils.random(10)
        systemStringService.updateSystemString(key, value)
        assertThat(systemStringService.getSystemString(key, String::class.java))
            .isEqualTo(value)
        assertThat(systemStringService.getCustomSystemString(key, null, true, String::class.java, "????"))
            .isEqualTo(value)
    }

    @Test
    @Throws(Exception::class)
    fun updateSystemString10() {
        val key = UUID.randomUUID().toString()
        val value = LocalDateTime.now()
        systemStringService.updateSystemString(key, value)
        assertThat(systemStringService.getSystemString(key, LocalDateTime::class.java))
            .isEqualTo(value)
    }

    @Test
    @Throws(Exception::class)
    fun updateSystemString11() {
        val key = UUID.randomUUID().toString()
        val value = LocalDate.now()
        systemStringService.updateSystemString(key, value)
        assertThat(systemStringService.getSystemString(key, LocalDate::class.java))
            .isEqualTo(value)
    }

    @Test
    @Throws(Exception::class)
    fun updateSystemString12() {
        val key = UUID.randomUUID().toString()
        val value = LocalTime.now()
        systemStringService.updateSystemString(key, value)
        assertThat(systemStringService.getSystemString(key, LocalTime::class.java))
            .isEqualTo(value)
    }

    @Test
    @Throws(Exception::class)
    fun updateSystemString13() {
        val key = UUID.randomUUID().toString()
        val value = Date()
        systemStringService.updateSystemString(key, value)
        assertThat(systemStringService.getSystemString(key, Date::class.java))
            .isEqualTo(value)
    }

    @Test
    @Throws(Exception::class)
    fun updateSystemString14() {
        val key = UUID.randomUUID().toString()
        val value = Calendar.getInstance()
        systemStringService.updateSystemString(key, value)
        assertThat(systemStringService.getSystemString(key, Calendar::class.java))
            .isEqualTo(value)
    }


    @Configuration
    @EnableJpa(
        useH2TempDataSource = true
//    useMysqlDatabase = "library"
    )
//    @EnableTransactionManagement(mode = AdviceMode.PROXY)
//    @EnableAspectJAutoProxy
//    @ImportResource("classpath:/datasource_sys.xml")
    @EnableWebMvc
    open class Config : JpaPackageScanner {
        override fun addJpaPackage(prefix: String, set: MutableSet<String>) {
            set.add("me.jiangcai.common.ss.entity")
        }
    }
}