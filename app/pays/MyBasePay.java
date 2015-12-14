package pays;

import com.google.inject.Inject;
import models.Payment;
import models.User;
import pays.Strategy.Debug;
import pays.Strategy.Normal;
import pays.Strategy.Strategy;
import play.Logger;
import services.PaymentService;
import services.UserService;
import status.PayStatus;
import utils.DateUtil;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by nookio on 15/12/14.
 */
public abstract class MyBasePay<T> extends Pay<Object, T> {

    @Inject
    private PaymentService paymentService;

    private static Short PAYED = 1;

    private static Logger.ALogger logger = Logger.of(MyBasePay.class);

    @Override
    protected abstract LinkedHashMap<String, String> getSignData(Object... all);

    @Override
    protected abstract T createNewPay(Integer payerId, String host, Object... all);

    @Override
    protected abstract String infoNotify(Map<String, String> requestParams);

    @Override
    protected Payment createPayment(Integer payerId, Short paymentType, String tradeStatus, boolean test, Object... all) {
        BigDecimal money = calculateMoney(new Debug());
        if (test){
            money = calculateMoney(new Debug());
        }else if (!test){
            Strategy strategy = (Strategy) all[0];
            money = calculateMoney(strategy);
        }
        Payment payment = paymentService.createNewInfo(payerId, paymentType, money, tradeStatus);
        logger.info("用户" + payerId + "payment创建成功" + payment.id);
        return payment;
    }


    @Override
    protected Integer resultOfPayment(String outTradeNo, String tradeNo, String totalFee, Date payedAt) {
        Payment payment = paymentService.getInfo(outTradeNo);
        BigDecimal fee = new BigDecimal(totalFee);
        int r = payment.money.compareTo(fee);
        if(r!=0) {
            logger.error("支付结果和记录信息不一致");
            logger.error("服务端传值"+totalFee+"记录值"+payment.money);
        }
        paymentService.updateInfo(payment, tradeNo, PayStatus.valueOf(payment.tradeStatus).getNext().name(), payedAt, fee);

        Date expiredDate = new Date();
        if (payment.itemId == Payment.ITEM_BOSS_VERSION_1_YEAR)
            expiredDate = DateUtil.addDate(new Date(), Calendar.YEAR, 1);
        else if  (payment.itemId == Payment.ITEM_BOSS_VERSION_3_YEAR)
            expiredDate = DateUtil.addDate(new Date(), Calendar.YEAR, 3);
        else if  (payment.itemId == Payment.ITEM_BOSS_VERSION_5_YEAR)
            expiredDate = DateUtil.addDate(new Date(), Calendar.YEAR, 5);

        Integer id = UserService.updatePaymentInfoOfUser(payment.userId, PAYED, User.TYPE_BOSS, User.PAY_TYPE_ONLINE, payedAt, expiredDate);
        logger.info(id + "支付成功");
        return id;
    }

    @Override
    protected abstract String notifySuccess();

    @Override
    protected abstract String notifyFail();
}
