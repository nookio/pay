package services.alipay;

import config.AlipayConfig;
import models.Payment;
import play.Logger;
import utils.HttpUtil;
import utils.MapUtil;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by nookio on 15/8/4.
 */
public class AliPayHelper {

    private static Logger.ALogger logger = play.Logger.of(AliPayHelper.class);

    //支付宝签名方法
    public static final String  ALI_SIGN_ALGORITHMS = "SHA1WithRSA";

    // 字符编码格式 目前支持 gbk 或 utf-8
    public static String input_charset = "utf-8";

    // 回调签名方式 不需修改
    public static String SIGN_TYPE = "RSA";


    /**
     * 支付宝消息验证地址
     */
    private static final String HTTPS_VERIFY_URL = "https://mapi.alipay.com/gateway.do?service=notify_verify&";


    /**
     * RSA签名
     * @param content 待签名数据
     * @param privateKey 商户私钥
     * @return 签名值
     */
    public static String aliSign(String content, String privateKey)
    {
        String input_charset = "UTF-8";
        try {
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(
                    AliPayBase64.decode(privateKey));
            KeyFactory keyf = KeyFactory.getInstance("RSA");
            PrivateKey priKey = keyf.generatePrivate(priPKCS8);

            java.security.Signature signature = java.security.Signature
                    .getInstance("SHA1WithRSA");

            signature.initSign(priKey);
            signature.update(content.getBytes(input_charset));

            byte[] signed = signature.sign();

            return AliPayBase64.encode(signed);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * RSA验签名检查
     * @param content 待签名数据
     * @param sign 签名值
     * @param ali_public_key 支付宝公钥
     * @param input_charset 编码格式
     * @return 布尔值
     */
    public static boolean aliVerify(String content, String sign, String ali_public_key, String input_charset)
    {
        try
        {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] encodedKey = AliPayBase64.decode(ali_public_key);
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));


            java.security.Signature signature = java.security.Signature
                    .getInstance(ALI_SIGN_ALGORITHMS);

            signature.initVerify(pubKey);
            signature.update( content.getBytes(input_charset) );

            boolean bverify = signature.verify(AliPayBase64.decode(sign));
            return bverify;

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 验证消息是否是支付宝发出的合法消息
     * @param params 通知返回来的参数数组
     * @return 验证结果
     */
    public static boolean verify(Map<String, String> params) {

        //判断responsetTxt是否为true，isSign是否为true
        //responsetTxt的结果不是true，与服务器设置问题、合作身份者ID、notify_id一分钟失效有关
        //isSign不是true，与安全校验码、请求时的参数格式（如：带自定义参数等）、编码格式有关
        boolean responseFromAliService = true;
        if(params.get("notify_id") != null) {
            String notify_id = params.get("notify_id");
            responseFromAliService = verifyResponse(notify_id);
        }
        String sign = "";
        if(params.get("sign") != null) {sign = params.get("sign");}
        boolean isSign = getSignVeryfy(params, sign);
        logger.error(isSign + "  " + responseFromAliService);
        if (isSign && responseFromAliService) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取远程服务器ATN结果,验证返回URL
     * @param notify_id 通知校验ID
     * @return 服务器ATN结果
     * 验证结果集：
     * invalid命令参数不对 出现这个错误，请检测返回处理中partner和key是否为空
     * true 返回正确信息
     * false 请检查防火墙或者是服务器阻止端口问题以及验证时间是否超过一分钟
     */
    private static boolean verifyResponse(String notify_id) {
        //获取远程服务器ATN结果，验证是否是支付宝服务器发来的请求
        String partner = AlipayConfig.DEFAULT_PARTNER;
        String veryfy_url = HTTPS_VERIFY_URL + "partner=" + partner + "&notify_id=" + notify_id;
        String result = HttpUtil.doGet(veryfy_url);
        return "true".equals(result)? true : false;
    }

    /**
     * 根据反馈回来的信息，生成签名结果
     * @param params 通知返回来的参数数组
     * @param sign 比对的签名结果
     * @return 生成的签名结果
     */
    private static boolean getSignVeryfy(Map<String, String> params, String sign) {
        Map<String, String> sParams = MapUtil.sortMapByKey(params);
        //获取待签名字符串
        String preSignStr = MapUtil.getUrlFromMap(sParams, true, false);
        //获得签名验证结果
        boolean isSign = false;
        if(SIGN_TYPE.equals("RSA")){
            isSign = aliVerify(preSignStr, sign, AlipayConfig.PUBLIC, input_charset);
        }
        return isSign;
    }

    public static String getNewAliOrderInfo(LinkedHashMap<String, String> data){
        return MapUtil.getUrlFromMap(data, false, false);
    }

}
