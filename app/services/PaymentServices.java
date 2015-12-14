package services;

import daos.PaymentsDao;
import models.Payment;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by nookio on 15/6/10.
 */
public class PaymentServices {

    public static Payment getPayment(String outTradeNo){
        return PaymentsDao.find(outTradeNo);
    }


    public static void updatePayment(Payment payment, String tradeNo, String tradeStatus, Date payedAt, BigDecimal totalFee){
        payment.tradeNo = tradeNo;
        payment.tradeStatus = tradeStatus;
        payment.payed = 1;
        payment.totalFee = totalFee;
        payment.payedAt = payedAt;
        PaymentsDao.update(payment);
    }

    public static Payment createNewPayment(Integer count, Integer itemId, Integer loginUserId, Short paymentType, BigDecimal money, String tradeStatus) {
        Payment payment = new Payment();
        payment.paymentType = paymentType;
        payment.count =count;
        payment.itemId = itemId;
        payment.money = money;
        payment.year = Payment.ITEM_YEAR[itemId];
        payment.payed = 0;
        payment.tradeStatus = tradeStatus;
        payment.totalFee = new BigDecimal(0);
        payment.userId = loginUserId;
        PaymentsDao.save(payment);
        return payment;
    }
}
