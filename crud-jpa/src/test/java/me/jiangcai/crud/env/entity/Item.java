package me.jiangcai.crud.env.entity;

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
import java.util.List;

/**
 * @author CJ
 */
@Entity
//@Setter
//@Getter
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
    @Column(scale = 2, precision = 4)
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
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> tags;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getInt1() {
        return int1;
    }

    public void setInt1(int int1) {
        this.int1 = int1;
    }

    public Integer getInt2() {
        return int2;
    }

    public void setInt2(Integer int2) {
        this.int2 = int2;
    }

    public byte getByte1() {
        return byte1;
    }

    public void setByte1(byte byte1) {
        this.byte1 = byte1;
    }

    public char getChar1() {
        return char1;
    }

    public void setChar1(char char1) {
        this.char1 = char1;
    }

    public short getShort1() {
        return short1;
    }

    public void setShort1(short short1) {
        this.short1 = short1;
    }

    public boolean isBoolean1() {
        return boolean1;
    }

    public void setBoolean1(boolean boolean1) {
        this.boolean1 = boolean1;
    }

    public float getFloat1() {
        return float1;
    }

    public void setFloat1(float float1) {
        this.float1 = float1;
    }

    public double getDouble1() {
        return double1;
    }

    public void setDouble1(double double1) {
        this.double1 = double1;
    }

    public long getLong1() {
        return long1;
    }

    public void setLong1(long long1) {
        this.long1 = long1;
    }

    public String getString1() {
        return string1;
    }

    public void setString1(String string1) {
        this.string1 = string1;
    }

    public BigInteger getBigInteger1() {
        return bigInteger1;
    }

    public void setBigInteger1(BigInteger bigInteger1) {
        this.bigInteger1 = bigInteger1;
    }

    public BigDecimal getBigDecimal1() {
        return bigDecimal1;
    }

    public void setBigDecimal1(BigDecimal bigDecimal1) {
        this.bigDecimal1 = bigDecimal1;
    }

    public Date getDate1() {
        return date1;
    }

    public void setDate1(Date date1) {
        this.date1 = date1;
    }

    public Calendar getCalendar1() {
        return calendar1;
    }

    public void setCalendar1(Calendar calendar1) {
        this.calendar1 = calendar1;
    }

    public java.sql.Date getDate2() {
        return date2;
    }

    public void setDate2(java.sql.Date date2) {
        this.date2 = date2;
    }

    public Time getTime1() {
        return time1;
    }

    public void setTime1(Time time1) {
        this.time1 = time1;
    }

    public Timestamp getTimestamp1() {
        return timestamp1;
    }

    public void setTimestamp1(Timestamp timestamp1) {
        this.timestamp1 = timestamp1;
    }

    public LocalDateTime getLocalDateTime1() {
        return localDateTime1;
    }

    public void setLocalDateTime1(LocalDateTime localDateTime1) {
        this.localDateTime1 = localDateTime1;
    }

    public LocalDate getLocalDate1() {
        return localDate1;
    }

    public void setLocalDate1(LocalDate localDate1) {
        this.localDate1 = localDate1;
    }

    public LocalTime getLocalTime1() {
        return localTime1;
    }

    public void setLocalTime1(LocalTime localTime1) {
        this.localTime1 = localTime1;
    }

    public Instant getInstant1() {
        return instant1;
    }

    public void setInstant1(Instant instant1) {
        this.instant1 = instant1;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Foo getFoo() {
        return foo;
    }

    public void setFoo(Foo foo) {
        this.foo = foo;
    }

    public BlueBlood getBlood() {
        return blood;
    }

    public void setBlood(BlueBlood blood) {
        this.blood = blood;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
