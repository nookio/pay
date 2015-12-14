package pays;

import models.Payment;
import pays.Strategy.Normal;
import play.Logger;
import status.PayStatus;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by nookio on 15/10/14.
 */
public class IosInAppPurchasePay extends MyBasePay<String> {

    private static String IOS_IN_APP_STORE_SUCCESS = "SUCCESS";

    private static Logger.ALogger logger = Logger.of(IosInAppPurchasePay.class);

    @Override
    protected String createNewPay(Integer payerId, String host, Object... all) {
        Integer count = (Integer) all[0];
        Integer itemId = (Integer) all[1];
        Payment payment = createPayment(payerId, Payment.TYPE_ALIPAY_APP, PayStatus.PREPAY.name(), false, new Normal(count, itemId));
        prePayInfo = payment.toMap();
        return host;
    }

    @Override
    protected String infoNotify(Map<String, String> requestParams) {
        //订单号
        String outTradeNo = requestParams.get("paymentId");
        //支付宝交易号
        String tradeNo = requestParams.get("tradeNo");
        //交易状态
        String tradeStatus =requestParams.get("tradeStatus");
        //交易金额
        String totalFee =requestParams.get("totalFee");
//        //付款时间
//        String gmtPayment = requestParams.get("gmt_payment");
        logger.debug("ios内购接口" + outTradeNo + "   " + tradeNo + " " + tradeStatus + " " + totalFee);
        if (tradeStatus.equals(IOS_IN_APP_STORE_SUCCESS)) {
            logger.debug("回掉接口" + outTradeNo + "   " + tradeNo + " " + tradeStatus + " " + totalFee);
            Date payedAt = new Date();
            Integer userId = resultOfPayment(outTradeNo,tradeNo, totalFee, payedAt);
            logger.info("用户"+userId+"ios内购成功");
            logger.info("支付信息为"+requestParams.toString());
        }else{
            logger.info("ios内购失败");
        }
        return notifySuccess();
    }

    @Override
    protected LinkedHashMap<String, String> getSignData(Object... all) {
        return null;
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
