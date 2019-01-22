package me.jiangcai.crud.env.controller;

import me.jiangcai.crud.controller.AbstractCrudController;
import me.jiangcai.crud.env.entity.Item;
import me.jiangcai.crud.row.FieldDefinition;
import me.jiangcai.crud.row.RowCustom;
import me.jiangcai.crud.row.field.Fields;
import me.jiangcai.crud.row.supplier.AntDesignPaginationDramatizer;
import me.jiangcai.crud.row.supplier.JQueryDataTableDramatizer;
import me.jiangcai.crud.row.supplier.Select2Dramatizer;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author CJ
 */
@Controller
@RequestMapping("/items")
public class ItemController extends AbstractCrudController<Item, Long, Item> {
    @Override
    protected Object describeEntity(Item origin) {
        return super.describeEntity(origin);
    }

    @GetMapping("/ant-d")
    @RowCustom(distinct = true, dramatizer = AntDesignPaginationDramatizer.class)
    public Object antDesignStyle(HttpServletRequest request) {
        return list(request);
    }

    @GetMapping("/jQuery")
    @RowCustom(distinct = true, dramatizer = JQueryDataTableDramatizer.class)
    public Object jQueryStyle(HttpServletRequest request) {
        return list(request);
    }

    @GetMapping("/select2")
    @RowCustom(distinct = true, dramatizer = Select2Dramatizer.class)
    public Object select2Style(HttpServletRequest request) {
        return list(request);
    }

    @Override
    protected List<FieldDefinition<Item>> listFields() {
        return Arrays.asList(
                Fields.asBasic("id"),
                Fields.asBasic("name")
        );
    }

    @Override
    protected Specification<Item> listSpecification(Map<String, Object> queryData) {
        return null;
    }

    @Override
    protected Item preparePersist(Item data, WebRequest otherData) {
        return super.preparePersist(data, otherData);
    }
}
