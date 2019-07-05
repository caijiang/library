package me.jiangcai.crud.modify;

import org.springframework.stereotype.Component;
import org.springframework.util.NumberUtils;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * 虽然提供了默认的格式，但其实并不重要，因为可以通过 {@link org.springframework.http.converter.HttpMessageConverter}强行转换成一个符合要求的实例。
 *
 * @author CJ
 */
@Component
public class DateTimePropertyChanger extends NullablePC {

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    protected Object nonNullChange(Class type, Object origin) {
        if (type == Date.class) {
            return toDate(origin);
        } else if (type == Calendar.class) {
            Calendar cd = Calendar.getInstance();
            cd.setTime(toDate(origin));
            return cd;
        } else if (type == java.sql.Date.class) {
            return new java.sql.Date(toDate(origin).getTime());
        } else if (type == Time.class) {
            if (origin instanceof Time)
                return origin;
            if (origin instanceof Number)
                return new Time(((Number) origin).longValue());
            return new Time(NumberUtils.parseNumber(origin.toString(), Long.class));
        } else if (type == Timestamp.class) {
            if (origin instanceof Timestamp)
                return origin;
            if (origin instanceof Number)
                return new Timestamp(((Number) origin).longValue());
            try {
                return new Timestamp(simpleDateFormat.parse(origin.toString()).getTime());
            } catch (ParseException ex) {
                return new Timestamp(NumberUtils.parseNumber(origin.toString(), Long.class));
            }
        } else if (type == Instant.class) {
            return toInstant(origin);
        } else if (type == LocalDateTime.class) {
            if (origin instanceof LocalDateTime)
                return origin;
            try {
                return LocalDateTime.ofInstant(toInstant(origin), ZoneId.systemDefault());
            } catch (DateTimeException ex) {
                try {
                    return LocalDateTime.from(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").parse(origin.toString()));
                } catch (DateTimeException ex2) {
//                    try {
                    return LocalDateTime.from(DateTimeFormatter.ISO_DATE_TIME.parse(origin.toString()));
//                    } catch (DateTimeParseException ex3) {
//                        return LocalDateTime.from(DateTimeFormatter.ISO_LOCAL_DATE_TIME.parse(origin.toString()));
//                    }
                }
            }
        } else if (type == LocalDate.class) {
            if (origin instanceof LocalDate)
                return origin;
            // 这个时间其实不太好弄的
            return LocalDate.from(
                    DateTimeFormatter.ofPattern("yyyy-MM-dd").parse(origin.toString())
            );
        } else if (type == LocalTime.class) {
            if (origin instanceof LocalTime)
                return origin;
            try {
                return LocalTime.from(
                        DateTimeFormatter.ofPattern("HH:mm:ss").parse(origin.toString())
                );
            } catch (DateTimeParseException ex) {
                return LocalTime.from(
                        DateTimeFormatter.ofPattern("HH:mm").parse(origin.toString())
                );
            }

        }
        throw new IllegalStateException("unknown of type:" + type);
    }

    private Instant toInstant(Object origin) {
        if (origin instanceof Instant)
            return (Instant) origin;
        if (origin instanceof Number)
            return Instant.ofEpochMilli(((Number) origin).longValue());
        // 时间？ 格式？
        try {
            return Instant.ofEpochMilli(NumberUtils.parseNumber(origin.toString(), Long.class));
        } catch (IllegalArgumentException ex) {
            // 文字格式吧
            return Instant.from(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").parse(origin.toString()));
        }
    }

    private Date toDate(Object origin) {
        if (origin instanceof Date)
            return (Date) origin;
        if (origin instanceof Number)
            return new Date(((Number) origin).longValue());
        // 默认格式吧
        try {
            return simpleDateFormat.parse(origin.toString());
        } catch (ParseException ex) {
            return new Date(NumberUtils.parseNumber(origin.toString(), Long.class));
        }
    }

    @Override
    public boolean support(Class<?> type) {
        return type == Date.class || type == Calendar.class || type == java.sql.Date.class || type == Time.class
                || type == Timestamp.class
                || type == LocalDateTime.class || type == LocalDate.class || type == LocalTime.class
                || type == Instant.class;
    }
}
