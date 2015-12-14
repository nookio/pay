package pays;

import config.UnionpayConfig;
import models.Payment;
import pays.Strategy.Normal;
import play.Logger;
import services.unionpay.UnionPayHelper;
import status.PayStatus;
import utils.DateUtil;
import utils.StringUtil;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by nookio on 15/8/13.
 */
public class UnionPay extends MyBasePay<String> {

    private static Logger.ALogger logger = Logger.of(UnionPay.class);

    @Override
    protected String createNewPay(Integer payerId, String host, Object... all) {
        logger.info(payerId + "正在进行银联支付");
        Integer count = (Integer) all[0];
        Integer itemId = (Integer) all[1];
        Payment payment = createPayment(payerId, Payment.TYPE_UNIONPAY_WAP, PayStatus.PREPAY.name(), false, new Normal(count, itemId));
        prePayInfo = UnionPayHelper.genUnionOrderInfo(getSignData(payment.id, payment.money, host));
        return host;
    }

    @Override
    protected String infoNotify(Map<String, String> requestParams) {
        String respCode = requestParams.get("respCode");
        if (StringUtil.isNotBlank(respCode) && respCode.equals("00")){
            String outTradeNo = new String(requestParams.get("orderId").replaceFirst("^0*", ""));
            //获取交易金额 txnAmt
            String totalFee = String.valueOf(Double.valueOf(requestParams.get("txnAmt")) / 100);
            //获取付款时间
            String payedMill = requestParams.get("txnTime");
            //获取流水号
            String tradeNo = requestParams.get("queryId");

            String tradeStatus = "SUCCESS";
            Date payedAt = DateUtil.timeMillToDate(payedMill);
            Integer id = resultOfPayment(outTradeNo, tradeNo, tradeStatus, totalFee, payedAt);

            logger.info(id + "验证签名结果[成功].");
        }else{
            logger.error("银联支付返回，失败"+"\n以下是回掉信息" + requestParams.toString());
        }
        return notifySuccess();
    }

    @Override
    protected LinkedHashMap<String, String> getSignData(Object... all) {
        Integer id = (Integer) all[0];
        BigDecimal money = (BigDecimal) all[1];
        String host = (String) all[2];

        LinkedHashMap<String, String> data = new LinkedHashMap<>();
        // 版本号
        data.put("version", "5.0.0");
        // 字符集编码 默认"UTF-8"
        data.put("encoding", "UTF-8");
        // 签名方法 01 RSA
        data.put("signMethod", "01");
        // 交易类型 01-消费
        data.put("txnType", "01");
        // 交易子类型 01:自助消费 02:订购 03:分期付款
        data.put("txnSubType", "01");
        // 业务类型
        data.put("bizType", "000201");
        // 渠道类型，07-PC，08-手机
        data.put("channelType", "08");
        // 后台通知地址
        data.put("backUrl", host + UnionpayConfig.UNIONPAY_APP_NOTIFY_URL);
        // 接入类型，商户接入填0 0- 商户 ， 1： 收单， 2：平台商户
        data.put("accessType", "0");
        // 商户号码，请改成自己的商户号
        data.put("merId", UnionpayConfig.CUSTOM_ID);
        // 商户订单号，8-40位数字字母
        String orderId = id < 1000000000 ? String.format("%08d", id):id.toString();
        data.put("orderId", orderId);
        // 订单发送时间，取系统时间
        data.put("txnTime", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        // 交易金额，单位分
        String monenyCent = money.multiply(new BigDecimal(100)).setScale(0,BigDecimal.ROUND_HALF_DOWN).toString();
        data.put("txnAmt", monenyCent);
        // 交易币种
        data.put("currencyCode", "156");
        return data;
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
