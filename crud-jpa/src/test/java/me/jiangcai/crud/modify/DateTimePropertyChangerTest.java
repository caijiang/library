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

        LocalDateTime x = (LocalDateTime) c.nonNullChange(LocalDateTime.class, "2019-07-04T19:01:32.580");
        assertThat(x)
                .isNotNull();
        System.out.println(x);
    }

}