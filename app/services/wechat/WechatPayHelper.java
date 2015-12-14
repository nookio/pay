package services.wechat;

import config.WeChatConfig;
import models.Payment;
import org.w3c.dom.Document;
import play.Logger;
import utils.DateUtil;
import utils.HttpUtil;
import utils.MapUtil;
import utils.XmlUtil;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by nookio on 15/8/4.
 */
public class WechatPayHelper {

    private static Logger.ALogger logger = Logger.of(WechatPayHelper.class);

    private static String UNIFIEDORDERURL="https://api.mch.weixin.qq.com/pay/unifiedorder";

    public static String getNewWechatOrderInfo(LinkedHashMap<String, String> data){
        return XmlUtil.mapToXmlString(data, "xml");
    }

    public static Map<String, String> genPayReq(String xmlData, String appId, String partnerid, String apiKey, String payType){
        String prepayId = getPrepayId(xmlData);
        String packageValue = "Sign=WXPay";
        Map<String, String> signMap = new LinkedHashMap<>();
        signMap.put("appid", appId);
        signMap.put("noncestr", genNonceStr());
        signMap.put("package", packageValue);
        signMap.put("partnerid", partnerid);
        signMap.put("prepayid", prepayId);
        signMap.put("timestamp", String.valueOf(genTimeStamp()));
        String sign = genPackageSign(signMap, apiKey);
        signMap.put("sign", sign);
        signMap.put("paymentType", payType);
        logger.info(signMap.toString());
        return signMap;
    }

    private static String getPrepayId(String xmlData){
        Document resultFromWeChat = HttpUtil.doPostAsXml(UNIFIEDORDERURL, xmlData);
        Map<String, String> resultMap = XmlUtil.decodeDocumentToMap(resultFromWeChat);
        logger.info("获取服务端返回信息"+resultMap.toString());
        if (resultMap.get("return_code").equals("SUCCESS") && resultMap.get("result_code").equals("SUCCESS")){
            return resultMap.get("prepay_id");
        }else throw new RuntimeException("申请prepay_id失败");
    }

    public static boolean verify(Map<String, String> requestParams, String apiKey) {
        if (!requestParams.containsKey("sign")) {
            return false;
        }
        Map<String,String> staySign = MapUtil.sortMapByKey(requestParams);
        staySign.remove("sign");
        String sign =genPackageSign(staySign, apiKey);
        return sign.equals(requestParams.get("sign"));
    }

    /**
     * 生成随机字符串，签名
     * @return
     */
    public static String genNonceStr() {
        Random random = new Random();
        return getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
    }

    private static long genTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }

    public static String genPackageSign(Map<String, String> params, String apiKey) {
        //params.put("key", WeChatConfig.API_KEY);
        String preSign = MapUtil.getUrlFromMap(params, false, false);
        String prepareSign = preSign + "&key=" + apiKey;
        String resultSign = getMessageDigest(prepareSign.getBytes()).toUpperCase();
        params.remove("key");
        return resultSign;
    }


    private final static String getMessageDigest(byte[] buffer) {
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(buffer);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean verifyScanPay(Map<String, String> requestParams){
        if (!requestParams.containsKey("openid")
                || !requestParams.containsKey("product_id")){
            return false;
        }
        return true;
    }

}
