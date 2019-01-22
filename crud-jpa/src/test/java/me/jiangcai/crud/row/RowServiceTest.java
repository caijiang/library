package me.jiangcai.crud.row;

import me.jiangcai.crud.env.entity.Bar;
import me.jiangcai.crud.env.entity.Foo;
import me.jiangcai.crud.env.entity.Item;
import me.jiangcai.crud.row.field.FieldBuilder;
import me.jiangcai.crud.row.field.Fields;
import me.jiangcai.crud.row.field.IndefiniteFieldBuilder;
import org.junit.Test;

import javax.persistence.criteria.JoinType;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
public class RowServiceTest {

    @Test
    public void forIndefiniteFieldBuilder() {
        final List<IndefiniteFieldDefinition> fields = Arrays.asList(
                IndefiniteFieldBuilder.asName("id").build(),
                IndefiniteFieldBuilder.asName("name").build(),
                IndefiniteFieldBuilder.asName("amount").build()
        );

        testFieldDefinition(fields);

    }

    @Test
    public void forFieldsAsBasic() {
        testFieldDefinition(
                Arrays.asList(
                        Fields.asBasic("id"),
                        Fields.asBasic("name"),
                        Fields.asBasic("amount")
                )
        );
    }

    @Test
    public void testFieldsAsFunction() {
        testFieldDefinition(
                Arrays.asList(
                        Fields.asFunction("bar", root -> root.get("foo").get("bar").get("value")),
                        Fields.asFunction("blood", root -> root.join("blood", JoinType.LEFT).get("bar").get("value")),
                        Fields.asFunction("id", root -> root.get("id")),
                        Fields.asFunction("name", root -> root.get("name")),
                        Fields.asFunction("amount", root -> root.get("amount"))
                )
        );
    }

    @Test
    public void testFieldsAsFunction2() {
        testFieldDefinition(
                Arrays.asList(
                        Fields.asBiFunction("id", null, Item::getId),
                        Fields.asBiFunction("name", null, Item::getName),
                        Fields.asBiFunction("amount", null, Item::getAmount)
                )
        );
    }

    @Test
    public void testFieldBuilder() {
        testFieldDefinition(
                Arrays.asList(
                        FieldBuilder.asName(Item.class, "id")
                                .build(),
                        FieldBuilder.asName(Item.class, "name")
                                .build(),
                        FieldBuilder.asName(Item.class, "amount")
                                .build()
                )
        );
    }

    @Test
    public void testFieldBuilder2() {
        testFieldDefinition(
                Arrays.asList(
                        FieldBuilder.asName(Item.class, "id")
                                .addSelect(itemRoot -> itemRoot.get("id"))
                                .build(),
                        FieldBuilder.asName(Item.class, "name")
                                .addSelect(itemRoot -> itemRoot.get("name"))
                                .build(),
                        FieldBuilder.asName(Item.class, "amount")
                                .addSelect(itemRoot -> itemRoot.get("amount"))
                                .build(),
                        FieldBuilder.asName(Item.class, "bar")
                                .addSelect(itemRoot -> itemRoot.get("foo").get("bar").get("value"))
                                .build()
                )
        );
    }


    private void testFieldDefinition(List<IndefiniteFieldDefinition> fields) {
        Item example = new Item();
        example.setId(1L);
        example.setName(UUID.randomUUID().toString());
        example.setAmount(100);

        Bar bar = new Bar();
        bar.setValue(UUID.randomUUID().toString());

        Foo foo = new Foo();
        foo.setBar(bar);

        example.setFoo(foo);

        @SuppressWarnings("unchecked")
        Map<String, Object> result = (Map<String, Object>) RowService
                .drawEntityToRow(example, fields
                        , null);

        assertThat(result.get("id"))
                .isEqualTo(example.getId());
        assertThat(result.get("name"))
                .isEqualTo(example.getName());
        assertThat(result.get("amount"))
                .isEqualTo(example.getAmount());

        Object actualBar = result.get("bar");
        if (actualBar != null) {
            assertThat(actualBar)
                    .isEqualTo(bar.getValue());
        }
        assertThat(result.get("blood"))
                .isNull();

    }

}