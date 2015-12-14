package pays.Strategy;

import java.math.BigDecimal;

/**
 * Created by nookio on 15/12/14.
 */
public class WechatScan extends Strategy {

    //价格
    private static final Integer ITEM_MONEYS[] = {0, 1980, 4800, 6800};

    //活动折扣
    private static final Double  DISCOUNT = 1.0;

    private Integer productId = 0;

    public WechatScan(Integer productId){
        this.productId = productId;
    }
    @Override
    public BigDecimal algorithm() {
        BigDecimal money = new BigDecimal(ITEM_MONEYS[productId] * DISCOUNT).setScale(2, BigDecimal.ROUND_HALF_DOWN);
        return money;
    }
}
