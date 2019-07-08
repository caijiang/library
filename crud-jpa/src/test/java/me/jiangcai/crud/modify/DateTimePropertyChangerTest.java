package me.jiangcai.crud.modify;

import org.junit.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
public class DateTimePropertyChangerTest {

    @Test
    public void go() {
        DateTimePropertyChanger c = new DateTimePropertyChanger();

        LocalDateTime x = (LocalDateTime) c.nonNullChange(LocalDateTime.class, "2019-07-06T16:00:00.000Z");
        assertThat(x)
                .isNotNull();
        System.out.println(x);
//        aa
//        System.out.println(String.class.getCanonicalName());
//        System.out.println(String.class.getSimpleName());
//        System.out.println(String.class.getName());
//        System.out.println(String.class.getTypeName());
    }

}