package utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import play.Logger;

import java.util.Collections;
import java.util.List;

/**
 * Created by yuzhen on 15/4/27.
 */
public class FastJson {

    private static final Logger.ALogger logger = Logger.of(FastJson.class);

    static {
        JSON.DEFFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    }

    public static String toJsonString(final Object data) {
        try {
            return JSON.toJSONString(data, SerializerFeature.WriteDateUseDateFormat);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String toJsonString(final Object data, PropertyFilter filter) {
        try {
            return JSON.toJSONString(data, filter, SerializerFeature.WriteDateUseDateFormat);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJsonToObj(String jsonStr, Class<T> clazz) {
        logger.info("from-json: {}", jsonStr);
        try {
            return JSON.parseObject(jsonStr, clazz);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> List<T> fromJsonToList(String jsonStr, Class<T> clazz) {
        logger.info("from-json: {}", jsonStr);
        try {
            return JSON.parseObject(jsonStr, new TypeReference<List<T>>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }


}
