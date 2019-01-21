package me.jiangcai.common.logging.impl

import me.jiangcai.common.logging.LoggingConfig
import me.jiangcai.common.logging.RefreshLoggingEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.servlet.view.RedirectView
import java.util.*

/**
 * @author CJ
 */
@Controller
@PreAuthorize("hasAnyRole('ROOT','" + LoggingConfig.ROLE_MANAGER + "')")
open class LoggingController {

    private val manageableConfigs = Collections.synchronizedMap(HashMap<String, String>())
    @Autowired
    private val applicationEventPublisher: ApplicationEventPublisher? = null
//
//    public void setManageableConfigs(Map<String, String> manageableConfigs) {
//        this.manageableConfigs = manageableConfigs;
//    }

    fun getManageableConfigs(): Map<String, String> {
        return manageableConfigs
    }

    @RequestMapping(method = [RequestMethod.DELETE], value = ["/loggingConfig/{name}/"])
    open fun delete(@PathVariable("name") name: String): RedirectView {
        manageableConfigs.remove(name)
        applicationEventPublisher!!.publishEvent(RefreshLoggingEvent())
        //        System.out.println("after delete,there is " + manageableConfigs.size() + " configs");
        return RedirectView("/loggingConfig")
    }

    @RequestMapping(method = [RequestMethod.POST], value = ["/loggingConfig"])
    open fun add(name: String, level: String): RedirectView {
        manageableConfigs[name] = level
        applicationEventPublisher!!.publishEvent(RefreshLoggingEvent())
        return RedirectView("/loggingConfig")
    }

    @RequestMapping(method = [RequestMethod.GET], value = ["/loggingConfig"])
    open fun index(model: Model): String {
        //        System.out.println("there is " + manageableConfigs.size() + " configs");
        //        DefaultAnnotationHandlerMapping annotationHandlerMapping;
        //        org.springframework.web.servlet.PageNotFound pageNotFound;
        model.addAttribute("currentConfigs", manageableConfigs)
        return "thymeleaf:classpath:/logging/index"
    }
}