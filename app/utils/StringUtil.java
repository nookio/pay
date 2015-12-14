package utils;


import org.jboss.logging.Logger;
import org.w3c.dom.Document;
import play.libs.XPath;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yuzhen on 15/4/11.
 */
public class StringUtil {

    private static Logger logger = Logger.getLogger(StringUtil.class);

    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static boolean isBlank(CharSequence cs) {
        int strLen;
        if(null != cs && (strLen = cs.length()) != 0) {
            for(int i = 0; i < strLen; ++i) {
                if(!Character.isWhitespace(cs.charAt(i))) {
                    return false;
                }
            }

            return true;
        } else {
            return true;
        }
    }

    public static boolean isNotBlank(CharSequence cs) {
        return !isBlank(cs);
    }

    public static String formatTime(Date date){
        return format.format(date);
    }

    public static boolean isMobile(String mobile) {
        String regex = "^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$";
        return match(regex, mobile);
    }

    public static boolean isEmail(String email) {
        String regex = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";
        return match(regex, email);
    }

    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    /**
     * 验证数字输入
     * @param str 待验证的字符串
     * @return 如果是符合格式的字符串,返回 <b>true </b>,否则为 <b>false </b>
     */
    public static boolean isNumber(String str)
    {
        String regex = "^[0-9]*$";
        return match(regex, str);
    }

    /**
     * 验证非零的正整数
     * @param str 待验证的字符串
     * @return 如果是符合格式的字符串,返回 <b>true </b>,否则为 <b>false </b>
     */
    public static boolean isIntNumber(String str)
    {
        String regex = "^\\+?[1-9][0-9]*$";
        return match(regex, str);
    }

    public static boolean isPwdValid(String password) {
        return isPwdPatternValid(password) && isPwdLengthValid(password);
    }

    /**
     * 验证输入密码条件(字符与数据同时出现)
     * @param str 待验证的字符串
     * @return 如果是符合格式的字符串,返回 <b>true </b>,否则为 <b>false </b>
     */
    public static boolean isPwdPatternValid(String str)
    {
        String regex = "[A-Za-z0-9]*";
        return match(regex, str);
    }

    /**
     * 验证输入密码长度 (6-18位)
     * @param str 待验证的字符串
     * @return 如果是符合格式的字符串,返回 <b>true </b>,否则为 <b>false </b>
     */
    public static boolean isPwdLengthValid(String str)
    {
        String regex = "^\\d{6,18}$";
        return match(regex, str);
    }

    /**
     * 验证验证输入字母
     * @param str 待验证的字符串
     * @return 如果是符合格式的字符串,返回 <b>true </b>,否则为 <b>false </b>
     */
    public static boolean isLetter(String str)
    {
        String regex = "^[A-Za-z]+$";
        return match(regex, str);
    }

    /**
     * @param regex 正则表达式字符串
     * @param str 要匹配的字符串
     * @return 如果str 符合 regex的正则表达式格式,返回true, 否则返回 false;
     */
    private static boolean match(String regex, String str)
    {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    public static Map<String,String> decodeXml(Document dom) {
        int i = XPath.selectNodes("/xml", dom).getLength();
        Map<String,String > result = new HashMap<>();
        result.put("appid", XPath.selectText("//appid", dom));
        result.put("bank_type", XPath.selectText("//bank_type", dom));
        result.put("fee_type", XPath.selectText("//fee_type", dom));
        result.put("is_subscribe", XPath.selectText("//is_subscribe", dom));
        result.put("mch_id", XPath.selectText("//mch_id", dom));
        result.put("cash_fee", XPath.selectText("//cash_fee", dom));
        result.put("nonce_str", XPath.selectText("//nonce_str", dom));
        result.put("openid", XPath.selectText("//openid", dom));
        result.put("out_trade_no", XPath.selectText("//out_trade_no", dom));
        result.put("result_code", XPath.selectText("//result_code", dom));
        result.put("return_code", XPath.selectText("//return_code", dom));
        result.put("sign", XPath.selectText("//sign", dom));
        result.put("time_end", XPath.selectText("//time_end", dom));
        result.put("total_fee", XPath.selectText("//total_fee", dom));
        result.put("trade_type", XPath.selectText("//trade_type", dom));
        result.put("transaction_id", XPath.selectText("//transaction_id", dom));
        return result;
    }
//
//    public static Map<String,String> decodeXml(Document dom) {
//        List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
//        packageParams.add(new BasicNameValuePair("appid", XPath.selectText("//appid", dom)));
//        packageParams.add(new BasicNameValuePair("bank_type", XPath.selectText("//bank_type", dom)));
//        packageParams.add(new BasicNameValuePair("cash_fee", XPath.selectText("//cash_fee", dom)));
//        packageParams.add(new BasicNameValuePair("fee_type", XPath.selectText("//fee_type", dom)));
//        packageParams.add(new BasicNameValuePair("is_subscribe", XPath.selectText("//is_subscribe", dom)));
//        packageParams.add(new BasicNameValuePair("mch_id", XPath.selectText("//mch_id", dom)));
//        packageParams.add(new BasicNameValuePair("nonce_str", XPath.selectText("//nonce_str", dom)));
//        packageParams.add(new BasicNameValuePair("openid", XPath.selectText("//openid", dom)));
//        packageParams.add(new BasicNameValuePair("out_trade_no", XPath.selectText("//out_trade_no", dom)));
//        packageParams.add(new BasicNameValuePair("result_code", XPath.selectText("//result_code", dom)));
//        packageParams.add(new BasicNameValuePair("return_code", XPath.selectText("//return_code", dom)));
//        packageParams.add(new BasicNameValuePair("sign", XPath.selectText("//sign", dom)));
//        packageParams.add(new BasicNameValuePair("time_end", XPath.selectText("//time_end", dom)));
//        packageParams.add(new BasicNameValuePair("total_fee", XPath.selectText("//total_fee", dom)));
//        packageParams.add(new BasicNameValuePair("trade_type", XPath.selectText("//trade_type", dom)));
//        packageParams.add(new BasicNameValuePair("transaction_id", XPath.selectText("//transaction_id", dom)));
//
//        return result;
//    }

}
