package me.jiangcai.crud.env.entity;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.crud.CrudFriendly;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

/**
 * @author CJ
 */
@Entity
@Setter
@Getter
public class Item implements CrudFriendly<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int int1;
    private Integer int2;
    private byte byte1;
    private char char1;
    private short short1;
    private boolean boolean1;
    private float float1;
    private double double1;
    private long long1;
    private String string1;
    private BigInteger bigInteger1;
    private BigDecimal bigDecimal1;
    private Date date1;
    private Calendar calendar1;
    private java.sql.Date date2;
    private Time time1;
    private Timestamp timestamp1;
    @Column(columnDefinition = "datetime")
    private LocalDateTime localDateTime1;
    @Column(columnDefinition = "date")
    private LocalDate localDate1;
    @Column(columnDefinition = "time")
    private LocalTime localTime1;
    @Column(columnDefinition = "timestamp")
    private Instant instant1;
    private String name;
    private int amount;
    @ManyToOne
    private Foo foo;
    @ManyToOne
    private BlueBlood blood;
}
