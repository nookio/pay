package pays.Strategy;

import java.math.BigDecimal;

/**
 * Created by nookio on 15/12/14.
 */
public class Normal extends Strategy {

    //价格
    private static final Integer ITEM_MONEYS[] = {0, 1980, 4800, 6800};
    //正常折扣
    private static final Double ITEM_DISCOUNT[] = {1.0, 0.8};
    //活动折扣
    private static final Double  DISCOUNT = 1.0;

    private Integer count;
    private Integer itemId;

    public Normal(Integer count, Integer itemId) {
        this.count = count;
        this.itemId = itemId;
    }

    //todo 这个地方可以写的更加完美。这是一种比较笨的方式实现
    @Override
    public BigDecimal algorithm() {
        Double discount = count < 5 ? ITEM_DISCOUNT[0] : ITEM_DISCOUNT[1];
        BigDecimal money = new BigDecimal(count * ITEM_MONEYS[itemId] * discount * DISCOUNT).setScale(2, BigDecimal.ROUND_HALF_DOWN);
        return money;
    }
}
