package models;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.CreatedTimestamp;
import com.avaje.ebean.annotation.UpdatedTimestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nookio on 15/5/12.
 */
@Entity
@Table(name = "payments")
public class Payment extends Model {

    public static final int TYPE_ALIPAY = 0;
    public static final short TYPE_ALIPAY_APP = 1;
    public static final int TYPE_ALIPAY_WAP = 2;
    public static final short TYPE_UNIONPAY_WAP = 3;
    public static final short TYPE_IOS_IN_APP_PURCHASE = 4;
    public static final short TYPE_WECHAT_APP = 5;
    public static final short TYPE_WECHAT_SCAN = 6;


    public static final Integer ITEM_BOSS_VERSION_1_YEAR = 1;
    public static final Integer ITEM_BOSS_VERSION_3_YEAR = 2;
    public static final Integer ITEM_BOSS_VERSION_5_YEAR = 3;

    //年限
    public static final Integer ITEM_YEAR[] = {0, 1, 3, 5};

    @Id
    public Integer id;
    public Integer userId;
    //@Column(name="payment_type")
    public Short paymentType;
    //@Column(name="item_id")
    public Integer itemId;
    public Integer count;
    public Integer year;
    public BigDecimal money;
    public String tradeNo;
   // @Column(name="trade_status")
    public String tradeStatus;
    //@Column(name="total_fee")
    public BigDecimal totalFee;

    public Short payed;

    //@Column(name="payed_at")
    public Date payedAt;

    //@Column(name="created_at")
    @CreatedTimestamp
    public Date createdAt;

    //@Column(name="updated_at")
    @UpdatedTimestamp
    public Date updatedAt;

    public Map<String, String> toMap(){
        Map<String, String> result = new HashMap<>();
        result.put("id", this.id.toString());
        result.put("userId", this.userId.toString());
        result.put("paymentType", this.paymentType.toString());
        result.put("itemId", this.itemId.toString());
        result.put("count", this.count.toString());
        result.put("year", this.year.toString());
        result.put("money", this.money.toString());
//        result.put("tradeNo", this.tradeNo);
        result.put("tradeStatus", this.tradeStatus);
        result.put("totalFee", this.totalFee.toString());
        result.put("payed", this.payed.toString());
//        result.put("payedAt", this.payedAt.toString());
        result.put("createdAt", this.createdAt.toString());
        result.put("updatedAt", this.updatedAt.toString());
        return result;
    }

}
