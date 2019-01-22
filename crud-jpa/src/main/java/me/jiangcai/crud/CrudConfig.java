package me.jiangcai.crud;

import me.jiangcai.crud.row.bean.IndefiniteRowDefinitionHandler;
import me.jiangcai.crud.row.bean.RowDefinitionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

/**
 * 增删改查的Spring配置
 *
 * @author CJ
 */
@Configuration
@ComponentScan({
        "me.jiangcai.crud.row.bean",
        "me.jiangcai.crud.modify",
        "me.jiangcai.crud.controller"
})
public class CrudConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private RowDefinitionHandler rowDefinitionHandler;
    @Autowired
    private IndefiniteRowDefinitionHandler indefiniteRowDefinitionHandler;

    @Override
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
        super.addReturnValueHandlers(returnValueHandlers);
        returnValueHandlers.add(rowDefinitionHandler);
        returnValueHandlers.add(indefiniteRowDefinitionHandler);
    }
}
