package services;

import daos.PaymentsDao;
import models.Payment;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by nookio on 15/12/14.
 */
public class PaymentService extends BaseService<Payment, Object> {

    @Override
    public Payment getInfo(String outTradeNo) {
        return PaymentsDao.find(outTradeNo);
    }

    @Override
    public Payment updateInfo(Payment payment, String tradeNo, String tradeStatus, Date payedAt, BigDecimal totalFee) {
        payment.tradeNo = tradeNo;
        payment.tradeStatus = tradeStatus;
        payment.payed = 1;
        payment.totalFee = totalFee;
        payment.payedAt = payedAt;
        PaymentsDao.update(payment);
        return payment;
    }

    @Override
    public Payment createNewInfo(Integer payerId, Short paymentType, BigDecimal money, String tradeStatus, Object... all) {
        Integer count = (Integer) all[0];
        Integer itemId = (Integer) all[1];
        Payment payment = new Payment();
        payment.paymentType = paymentType;
        payment.count = count;
        payment.itemId = itemId;
        payment.money = money;
        payment.year = Payment.ITEM_YEAR[itemId];
        payment.payed = 0;
        payment.tradeStatus = tradeStatus;
        payment.totalFee = new BigDecimal(0);
        payment.userId = payerId;
        PaymentsDao.save(payment);
        return payment;
    }

}
