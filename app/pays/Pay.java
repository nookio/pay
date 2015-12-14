package pays;

import models.Payment;
import pays.Strategy.Strategy;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by nookio on 15/12/14.
 */
public abstract class Pay<E, T> {

    protected Map<String, String> prePayInfo = new LinkedHashMap<>();

    public Map<String, String> getPrePayInfo(){
        return prePayInfo;
    }

    protected abstract LinkedHashMap<String, String> getSignData(E... all);

    // 一般具体实现是在具体的支付方式中
    protected abstract T createNewPay(Integer payerId, String host, E... all);

    // 一般具体实现是在具体的支付方式中
    protected abstract String infoNotify(Map<String, String> requestParams);

    protected abstract Payment createPayment(Integer payerId, Short paymentType, String tradeStatus, boolean test, E... all);

    protected abstract Integer resultOfPayment(String outTradeNo, String tradeNo, String totalFee, Date payedAt);

//    protected abstract void successTodo();
//
//    protected abstract void failTodo();

    // 一般具体实现是在具体的支付方式中
    protected abstract String notifySuccess();

    // 一般具体实现是在具体的支付方式中
    protected abstract String notifyFail();

    // 使用策略模式 生产新的计算方法类
    protected BigDecimal calculateMoney(Strategy strategy){
        return strategy.algorithm();
    }

}
