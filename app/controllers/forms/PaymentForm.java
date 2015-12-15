package controllers.forms;

import play.data.validation.Constraints;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nookio on 15/6/9.
 */
public class PaymentForm {

    public Short getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(Short paymentType) {
        this.paymentType = paymentType;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Integer getLoginUserId() {
        return loginUserId;
    }

    public void setLoginUserId(Integer loginUserId) {
        this.loginUserId = loginUserId;
    }

    public String getLoginToken() {
        return loginToken;
    }

    public void setLoginToken(String loginToken) {
        this.loginToken = loginToken;
    }

    @Constraints.Required
    private Short paymentType;

    @Constraints.Required
    private Integer count;

    @Constraints.Required
    private Integer itemId;

    @Constraints.Required
    private Integer loginUserId;

    @Constraints.Required
    private String loginToken;

    public Map<String,String> toMap(){
        Map<String,String> map = new HashMap<>();
        map.put("paymentType",paymentType.toString());
        map.put("count",count.toString());
        map.put("itemId",itemId.toString());
        map.put("loginUserId",loginUserId.toString());
        map.put("loginToken",loginToken);
        return map;
    }

}
