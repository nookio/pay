package services;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

/**
 * Created by nookio on 15/12/11.
 */
public abstract class BaseService<E,T> {

    protected abstract E getInfo(String outTradeNo);

    protected abstract E updateInfo(E e, String tradeNo, String tradeStatus, Date payedAt, BigDecimal totalFee);

    protected abstract E createNewInfo(Integer payerId, Short paymentType, BigDecimal money, String tradeStatus, T ... all);
}
