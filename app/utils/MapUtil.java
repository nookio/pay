package utils;

import org.apache.commons.lang.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created by nookio on 15/8/1.
 */
public class MapUtil {

    private static String ENCODING = "UTF-8";

    /**
     * 从已经排序好的map中获取到拼接URl
     * @param requestParam
     * @return
     */
    public static String getUrlFromMap(Map<String, String> requestParam, boolean paraFilter, boolean encoder){
        //coder = coder == null? ENCODING:coder;
        if (paraFilter) requestParam = paraFilter(requestParam);
        StringBuffer result_temp = new StringBuffer("");
        String result = "";
        if(null != requestParam && 0 != requestParam.size()) {
            Iterator iterator = requestParam.entrySet().iterator();

            while(!encoder && iterator.hasNext()){
                Map.Entry entry = (Map.Entry)iterator.next();
                result_temp.append((String)entry.getKey() + "=" + (null != entry.getValue() && !"".equals(entry.getValue())? (String) entry.getValue():"") + "&");
            }

            while(encoder && iterator.hasNext()) {
                Map.Entry entry = (Map.Entry)iterator.next();
                try {
                    result_temp.append((String)entry.getKey() + "=" + (null != entry.getValue() && !"".equals(entry.getValue())? URLEncoder.encode((String) entry.getValue(), ENCODING):"") + "&");
                } catch (UnsupportedEncodingException exception) {
                    exception.printStackTrace();
                    return "";
                }
            }
            result = result_temp.substring(0, result_temp.length() - 1);
        }
        return result;
    }

    /**
     * 对String形式的url数据串解析，得到map
     * @param url
     * @return
     * @throws UnsupportedEncodingException
     */
    public static Map<String, String> getMapFromUrl(String url) throws UnsupportedEncodingException {
        Map<String, String> result = new LinkedHashMap<>();
        if(StringUtils.isNotBlank(url)) {
            if(url.startsWith("{") && url.endsWith("}")) {
                System.out.println(url.length());
                url = url.substring(1, url.length() - 1);
            }

            result = parseQString(url);
        }
        return result;
    }

    /**
     * 使用 Map按key进行排序
     * @param map
     * @return
     */
    public static Map<String, String> sortMapByKey(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        Map<String, String> sortMap = new TreeMap<String, String>(new Comparator<String>(){

            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        sortMap.putAll(map);
        return sortMap;
    }

    /**
     * 除去数组中的空值和签名参数
     * @param sArray 签名参数组
     * @return 去掉空值与签名参数后的新签名参数组
     */
    public static Map<String, String> paraFilter(Map<String, String> sArray) {
        Map<String, String> result = new LinkedHashMap<>();
        if (sArray == null || sArray.size() <= 0) {
            return result;
        }
        for (String key : sArray.keySet()) {
            String value = sArray.get(key);
            if (value == null || value.equals("") || key.equalsIgnoreCase("sign")
                    || key.equalsIgnoreCase("sign_type") || key.equalsIgnoreCase("signature")) {
                continue;
            }
            result.put(key, value);
        }
        return result;
    }


    private static Map<String, String> parseQString(String str) throws UnsupportedEncodingException {
        HashMap map = new LinkedHashMap<>();
        int len = str.length();
        StringBuilder temp = new StringBuilder();
        String key = null;
        boolean isKey = true;
        boolean isOpen = false;
        byte openName = 0;
        if(len > 0) {
            for(int i = 0; i < len; ++i) {
                char curChar = str.charAt(i);
                if(isKey) {
                    if(curChar == 61) {
                        key = temp.toString();
                        temp.setLength(0);
                        isKey = false;
                    } else {
                        temp.append(curChar);
                    }
                } else {
                    if(isOpen) {
                        if(curChar == openName) {
                            isOpen = false;
                        }
                    } else {
                        if(curChar == 123) {
                            isOpen = true;
                            openName = 125;
                        }

                        if(curChar == 91) {
                            isOpen = true;
                            openName = 93;
                        }
                    }

                    if(curChar == 38 && !isOpen) {
                        putKeyValueToMap(temp, isKey, key, map);
                        temp.setLength(0);
                        isKey = true;
                    } else {
                        temp.append(curChar);
                    }
                }
            }

            putKeyValueToMap(temp, isKey, key, map);
        }

        return map;
    }

    private static void putKeyValueToMap(StringBuilder temp, boolean isKey, String key, Map<String, String> map) throws UnsupportedEncodingException {
        if(isKey) {
            key = temp.toString();
            if(key.length() == 0) {
                throw new RuntimeException("QString format illegal");
            }

            map.put(key, "");
        } else {
            if(key.length() == 0) {
                throw new RuntimeException("QString format illegal");
            }

            map.put(key, temp.toString());
        }

    }
}
