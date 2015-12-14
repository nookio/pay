package pays;

import config.AlipayConfig;
import models.Payment;
import pays.Strategy.Normal;
import play.Logger;
import services.alipay.AliPayHelper;
import status.PayStatus;
import utils.DateUtil;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by nookio on 15/8/13.
 */
public class AliPay extends MyBasePay<String> {

    private static String getSignType() {
        return "sign_type=\"RSA\"";
    }

    private static Logger.ALogger logger = Logger.of(AliPay.class);

    protected static String ALI_TRADE_SUCCESS = "TRADE_SUCCESS";

    protected static String ALI_TRADE_FINISHED = "TRADE_FINISHED";

    @Override
    protected String createNewPay(Integer payerId, String host, Object... all) {
        Integer count = (Integer) all[0];
        Integer itemId = (Integer) all[1];
        Payment payment = createPayment(payerId, Payment.TYPE_ALIPAY_APP, PayStatus.PREPAY.name(), false, new Normal(count, itemId));
        LinkedHashMap<String, String> data = getSignData();
        String info = AliPayHelper.getNewAliOrderInfo(data);
        String sign = AliPayHelper.aliSign(info, AlipayConfig.PRIVATE);
        sign = URLEncoder.encode(sign);
        info += "&sign=\"" + sign + "\"&" + getSignType();
        prePayInfo.put("out_trade_no", payment.id.toString());
        prePayInfo.put("info", info);
        // result.put("userId",payment.userId);
        prePayInfo.put("money", payment.money.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        prePayInfo.put("paymentType", String.valueOf(Payment.TYPE_ALIPAY_APP));
        logger.info(info);
        return info;
    }

    @Override
    protected String infoNotify(Map<String, String> requestParams) {
        //订单号
        String outTradeNo = requestParams.get("out_trade_no");
        //支付宝交易号
        String tradeNo = requestParams.get("trade_no");
        //交易状态
        String tradeStatus =requestParams.get("trade_status");
        //交易金额
        String totalFee =requestParams.get("total_fee");
        //付款时间
        String gmtPayment = requestParams.get("gmt_payment");
        logger.debug("支付宝掉接口" + outTradeNo + "   " + tradeNo + " " + tradeStatus + " " + totalFee);
        if (tradeStatus.equals(ALI_TRADE_SUCCESS) || tradeStatus.equals(ALI_TRADE_FINISHED)) {
            logger.debug("回掉接口er" + outTradeNo + "   " + tradeNo + " " + tradeStatus + " " + totalFee);
            Date payedAt = DateUtil.formate(gmtPayment);
            Integer userId = resultOfPayment(outTradeNo,tradeNo, totalFee, payedAt);
            logger.info("用户"+userId+"支付宝支付成功");
            logger.info("支付信息为"+requestParams.toString());
        }else{
            logger.info("支付宝支付失败");
        }
        return notifySuccess();
    }

    @Override
    protected LinkedHashMap<String, String> getSignData(Object... all) {
        Payment payment = (Payment) all[0];
        String host = (String) all[1];
        String notifyUrl = host + AlipayConfig.ALIPAY_APP_NOTIFY_URL;

        LinkedHashMap<String,String> map = new LinkedHashMap<>();
        map.put("partner", "\"" + AlipayConfig.DEFAULT_PARTNER + "\"");
        map.put("seller_id", "\"" + AlipayConfig.DEFAULT_SELLER + "\"");
        map.put("out_trade_no", "\"" + payment.id.toString() + "\"");
        //map.put("out_trade_no", "\"" + 774 + "\"");
        map.put("subject", "\"" + "美业邦经理版" + "\"");
        map.put("body", "\"" + "美业邦管店经理版" + "\"");
        map.put("total_fee", "\"" + payment.money.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\"");
        map.put("notify_url", "\"" + URLEncoder.encode(notifyUrl) + "\"");
        map.put("service", "\"" + "mobile.securitypay.pay" + "\"");
        map.put("payment_type", "\"" + Payment.TYPE_ALIPAY_APP + "\"");
        map.put("_input_charset", "\"" + "utf-8" + "\"");
        map.put("it_b_pay", "\"" + "1m" + "\"");
        map.put("return_url", "\"" + URLEncoder.encode("http://m.alipay.com") + "\"");
        return map;
    }

    @Override
    protected String notifySuccess() {
        return "success";
    }

    @Override
    protected String notifyFail() {
        return "fail";
    }
}
