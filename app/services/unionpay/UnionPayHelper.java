package services.unionpay;

import config.UnionpayConfig;
import models.Payment;
import play.Logger;
import utils.HttpUtil;
import utils.MapUtil;
import utils.StringUtil;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by nookio on 15/8/4.
 */
public class UnionPayHelper {

    private static Logger.ALogger logger = Logger.of(UnionPayHelper.class);

    private static String ENCODING = "UTF-8";

    public static Map<String, String> genUnionOrderInfo(LinkedHashMap<String, String> data){
        Map<String, String> submitData = signData(data);
        // 交易请求url 从配置文件读取
        String requestAppUrl = UnionpayConfig.APP_TRANS_URL;
        logger.info("请求前数据"+data.toString());
        Map<String, String> resmap = submitUrl(submitData, requestAppUrl);
        if (null == resmap ) throw new RuntimeException("创建失败");
        logger.debug("数据已经返回，现在是返回值" + resmap.toString());
        logger.info("数据已经返回，现在是返回值"+resmap.toString());
        String tn = resmap.get("tn");
        resmap.clear();
        resmap.put("tn", tn);
        resmap.put("paymentType", String.valueOf(Payment.TYPE_UNIONPAY_WAP));
        return resmap;
    }


    private static Map<String, String> signData(Map<String, String > data){
        data = MapUtil.paraFilter(data);
        data = sign(data);
        return data;
    }

    /**
     * 签名数组
     * @param data 待签名map
     * @return
     */
    private static Map<String, String> sign(Map<String, String > data){
        String encoding = "UTF-8";
        data.put("certId", CertUtil.getSignCertId());
        String stringData = MapUtil.getUrlFromMap(MapUtil.sortMapByKey(data), false, false);
        logger.info("报文签名之前的字符串(不含signature域)=[" + stringData + "]");
        Object byteSign = null;
        String stringSign = null;

        try {
            byte[] e = SecureUtil.sha1X16(stringData, encoding);
            logger.info("SHA1->16进制转换后的摘要=[" + new String(e) + "]");
            byte[] byteSign1 = SecureUtil.base64Encode(SecureUtil.signBySoft(CertUtil.getSignCertPrivateKey(), e));
            stringSign = new String(byteSign1);
            logger.info("报文签名之后的字符串=[" + stringSign + "]");
            data.put("signature", stringSign);
            logger.info("签名处理结束.");
            return data;
        } catch (Exception var6) {
            logger.info("签名异常", var6);
            throw new RuntimeException("");
        }
    }


    /**
     * 向银联服务器提交申请
     * @param submitFromData
     * @param requestUrl
     * @return
     */
    private static Map<String, String> submitUrl(Map<String, String> submitFromData,String requestUrl) {
        String data = MapUtil.getUrlFromMap(submitFromData, false, true);
        //String resultFromUnionService = HttpUtil.doPost(requestUrl, data, GlobalConfig.HTTP_TIME_OUT);
        Map<String, String> header = new HashMap<>();
        header.put("Content-type", "application/x-www-form-urlencoded;charset=" + ENCODING);
        String resultFromUnionService = HttpUtil.doPostOnSSL(requestUrl, data, header);
        Map<String, String> result = null;
        if(StringUtil.isNotBlank(resultFromUnionService)){
            try {
                result = MapUtil.getMapFromUrl(resultFromUnionService);
                // 打印返回报文
                System.out.println("打印返回报文：" + resultFromUnionService);
                if (validate(result)){
                    System.out.println("验证签名成功");
                    if (!result.get("respCode").equals("00")){
                        return null;
                    }
                    return result;
                }else{
                    System.out.println("验证签名失败");
                    throw new RuntimeException("验证签名失败");
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                throw new RuntimeException("转换fail");
            }
        }
        return null;
    }

    /**
     * 对数据进行验证，判断是不是银联返回
     * @param resData
     * @return
     */
    public static boolean validate(Map<String, String> resData) {
        String encoding = "UTF-8";
        logger.info("验签处理开始.");
        String stringSign = (String)resData.get("signature");
        logger.info("返回报文中signature=[" + stringSign + "]");
        String certId = (String)resData.get("certId");
        logger.info("返回报文中certId=[" + certId + "]");
        String stringData = MapUtil.getUrlFromMap(MapUtil.sortMapByKey(resData), true, false);
        logger.info("返回报文中(不含signature域)的stringData=[" + stringData + "]");

        try {
            return SecureUtil.validateSignBySoft(CertUtil.getValidateKey(certId), SecureUtil.base64Decode(stringSign.getBytes(encoding)), SecureUtil.sha1X16(stringData, encoding));
        } catch (UnsupportedEncodingException var6) {
            logger.error(var6.getMessage(), var6);
        } catch (Exception var7) {
            logger.error(var7.getMessage(), var7);
        }

        return false;
    }
}
