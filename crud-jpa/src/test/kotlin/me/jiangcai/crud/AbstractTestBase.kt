package me.jiangcai.crud

import me.jiangcai.common.jpa.JpaPackageScanner
import me.jiangcai.common.test.classic.ClassicMvcTest
import me.jiangcai.crud.event.EntityVariationEvent
import org.springframework.context.event.EventListener
import org.springframework.test.context.ContextConfiguration

/**
 * @author CJ
 */
@ContextConfiguration(classes = [AbstractTestBase.MyConfig::class, Test2Config::class])
abstract class AbstractTestBase : ClassicMvcTest() {

    open class MyConfig : JpaPackageScanner {
        override fun addJpaPackage(prefix: String, set: MutableSet<String>) {
            set.add("me.jiangcai.crud.env.entity2")
        }

        @EventListener(EntityVariationEvent::class)
        fun fire(event: EntityVariationEvent<*>) {
            println("" + event.variationType + event.target)
        }
    }

}