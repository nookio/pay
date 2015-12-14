package pays;

import com.google.inject.Inject;
import config.WeChatConfig;
import models.Payment;
import models.User;
import pays.Strategy.WechatScan;
import play.Logger;
import services.PaymentService;
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
 * Created by nookio on 15/12/14.
 */
public class WechatScanPay extends MyBasePay<String> {

    @Inject
    PaymentService paymentService;

    private static String payType = "微信scan支付";

    private static Logger.ALogger logger = Logger.of(WechatScanPay.class);

    @Override
    protected String createNewPay(Integer payerId, String host, Object... all) {
        Map<String, String> requestParams = (Map<String, String>) all[0];
        if (WechatPayHelper.verifyScanPay(requestParams)){
            logger.error("有人正在进行扫码支付验证open_id和product_id" +
                    "失败。以下是支付信息："+requestParams.toString());
            return notifyFail();
        }else{
            //String nonceStr = requestParams.get("nonce_str");
            Integer productId = Integer.valueOf(requestParams.get("product_id"));
            String openId = requestParams.get("openid");
            Payment payment = createScanPay(Payment.TYPE_WECHAT_SCAN, productId);

            String xmlData = WechatPayHelper.getNewWechatOrderInfo(getSignData());
            logger.info("扫码支付信息为" + xmlData);
            return xmlData;
        }

    }

    @Override
    protected String infoNotify(Map<String, String> requestParams) {
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
            resultOfScanPayment(outTradeNo, tradeNo, tradeStatus, totalFee, payedAt);
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
        String openId = (String) all[3];

        String nonceStr = WechatPayHelper.genNonceStr();
        LinkedHashMap<String,String> params = new LinkedHashMap<>();
        String appId = WeChatConfig.MP_APP_ID;
        String partnerid = WeChatConfig.MP_MCH_ID;
        String notifyUrl = host + WeChatConfig.WechatPay_SCAN_NOTIFY_URL;
        String tradeType = "";
        params.put("time_start", DateUtil.getNow());
        params.put("time_expire", "20200101010101");
        params.put("goods_tag", "扫码支付");
        params.put("openid", openId);
        params.put("product_id", money.toString());
        params.put("appid", appId);
        params.put("body", "weixin");
        params.put("mch_id",  partnerid);
        params.put("nonce_str", nonceStr);
        params.put("notify_url",  notifyUrl);
        params.put("out_trade_no", id.toString());
        params.put("spbill_create_ip", "127.0.0.1");
        params.put("total_fee", money.multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_HALF_DOWN).toString());
        params.put("trade_type", tradeType);
        String sign = WechatPayHelper.genPackageSign(MapUtil.sortMapByKey(params), WeChatConfig.MP_API_KEY);
        params.put("sign", sign);
        return params;
    }

    @Override
    protected String notifySuccess() {
        Map<String,String> notifiyMap = new HashMap<>();
        notifiyMap.put("return_code", "SUCCESS");
        notifiyMap.put("return_msg", "OK");
        String result = XmlUtil.mapToXmlString(notifiyMap, "xml");
        return result;
    }

    @Override
    protected String notifyFail() {
        Map<String,String> notifiyMap = new HashMap<>();
        notifiyMap.put("return_code", "FAIL");
        notifiyMap.put("return_msg", "验证open_id和product_id失败。");
        String result = XmlUtil.mapToXmlString(notifiyMap, "xml");
        return result;
    }

    public Payment createScanPay(Short paymentType, Integer productId){
        BigDecimal money = calculateMoney(new WechatScan(productId));
        Payment payment = createPayment(User.SCAN_PAY_USER, paymentType, PayStatus.PREPAY.name(), false, new WechatScan(productId));
        logger.info("用户" + User.SCAN_PAY_USER + "payment创建成功" + payment.id);
        return payment;
    }

    private void resultOfScanPayment(String outTradeNo, String tradeNo, String tradeStatus, String totalFee, Date payedAt){
        Payment payment = paymentService.getInfo(outTradeNo);
        BigDecimal fee = new BigDecimal(totalFee);
        int r = payment.money.compareTo(fee);
        if(r!=0) {
            logger.error("支付结果和记录信息不一致");
            logger.error("服务端传值"+totalFee+"记录值"+payment.money);
        }
        paymentService.updateInfo(payment, tradeNo, tradeStatus, payedAt, fee);
        logger.info("扫码支付成功，单号:" + tradeNo + "时间" + payedAt.toString());
    }

}
