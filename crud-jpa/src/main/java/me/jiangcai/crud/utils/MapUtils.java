package me.jiangcai.crud.utils;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author helloztt
 */
public class MapUtils {

    public static Map<String, Object> changeIt(Map<String, String[]> input) {
        return input.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, (Function<Map.Entry<String, String[]>, Object>) stringEntry -> {
                    String[] rs = stringEntry.getValue();
                    if (rs == null)
                        return null;
                    if (rs.length > 1)
                        return rs;
                    return rs[0];
                }));
    }

}
