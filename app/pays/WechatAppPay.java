package pays;

import config.WeChatConfig;
import models.Payment;
import pays.Strategy.Normal;
import play.Logger;
import services.wechat.WechatPayHelper;
import status.PayStatus;
import utils.DateUtil;
import utils.MapUtil;
import utils.XmlUtil;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by nookio on 15/8/13.
 */
public class WechatAppPay extends MyBasePay<String> {

    private static String payType = "微信app支付";

    private static Logger.ALogger logger = Logger.of(WechatAppPay.class);

    @Override
    public String createNewPay(Integer payerId, String host, Object... all) {
        Integer count = (Integer) all[0];
        Integer itemId = (Integer) all[1];
        Payment payment = createPayment(payerId, Payment.TYPE_ALIPAY_APP, PayStatus.PREPAY.name(), false, new Normal(count, itemId));
        String xmlData = WechatPayHelper.getNewWechatOrderInfo(getSignData(payment.id, payment.money, host));
        logger.info("用户" + payerId + "支付信息为" + xmlData);
        prePayInfo =  WechatPayHelper.genPayReq(xmlData, WeChatConfig.APP_ID, WeChatConfig.MCH_ID, WeChatConfig.API_KEY, payType);
        return xmlData;
    }

    @Override
    public String infoNotify(Map<String, String> requestParams) {
        //订单号
        String outTradeNo =  requestParams.get("out_trade_no");
        //微信交易号
        String tradeNo = requestParams.get("transaction_id");
        //交易状态
        String tradeStatus =requestParams.get("result_code");
        //交易金额
        String totalFee = String.valueOf(Double.valueOf(requestParams.get("total_fee")) / 100);
        //交易时间
        String gmtPayment = requestParams.get("time_end");
        if (requestParams.get("result_code").equals("SUCCESS") && requestParams.get("return_code").equals("SUCCESS")) {
            Date payedAt = DateUtil.timeMillToDate(gmtPayment);
            Integer userId = resultOfPayment(outTradeNo, tradeNo, totalFee, payedAt);
            logger.info("用户"+userId+"微信支付成功");
            logger.info("支付信息为"+requestParams.toString());
            return notifySuccess();
        }else{
            logger.error("回掉失败，+"+requestParams.toString());
            return notifySuccess();
        }
    }

    @Override
    protected LinkedHashMap<String, String> getSignData(Object... all) {
        String id = (String) all[0];
        BigDecimal money = (BigDecimal) all[1];
        String host = (String) all[2];

        String nonceStr = WechatPayHelper.genNonceStr();
        LinkedHashMap<String,String> params = new LinkedHashMap<>();
        String appId = WeChatConfig.APP_ID;
        String partnerid = WeChatConfig.MCH_ID;
        String notifyUrl = host + WeChatConfig.WechatPay_APP_NOTIFY_URL;
        String tradeType = "App";
        params.put("appid", appId);
        params.put("body", "weixin");
        params.put("mch_id",  partnerid);
        params.put("nonce_str", nonceStr);
        params.put("notify_url",  notifyUrl);
        params.put("out_trade_no", id);
        params.put("spbill_create_ip", "127.0.0.1");
        params.put("total_fee", money.multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_HALF_DOWN).toString());
        params.put("trade_type", tradeType);
        String sign = WechatPayHelper.genPackageSign(MapUtil.sortMapByKey(params), WeChatConfig.API_KEY);
        params.put("sign", sign);
        return params;
    }

    @Override
    public String notifySuccess() {
        Map<String,String> notifiyMap = new HashMap<>();
        notifiyMap.put("return_code", "SUCCESS");
        notifiyMap.put("return_msg", "OK");
        String result = XmlUtil.mapToXmlString(notifiyMap, "xml");
        return result;
    }

    @Override
    public String notifyFail() {
        Map<String,String> notifiyMap = new HashMap<>();
        notifiyMap.put("return_code", "FAIL");
        notifiyMap.put("return_msg", "验证open_id和product_id失败。");
        String result = XmlUtil.mapToXmlString(notifiyMap, "xml");
        return result;
    }
}
