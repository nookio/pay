package pays;

import models.Payment;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nookio on 15/8/13.
 */
public class NotifyPay {

    private MyBasePay basePay = null;

    Map<String, String> requestParams = new HashMap<>();

    public NotifyPay(Short type, Map<String, String> requestParams){
        this.requestParams = requestParams;

        switch (type) {
            case Payment.TYPE_ALIPAY_APP:
                basePay = new AliPay();
                break;
            case Payment.TYPE_UNIONPAY_WAP:
                basePay = new UnionPay();
                break;
            case Payment.TYPE_WECHAT_APP:
                basePay = new WechatAppPay();
                break;
            case Payment.TYPE_IOS_IN_APP_PURCHASE:
                basePay = new IosInAppPurchasePay();
                break;
            default:
                basePay = null;
        }
    }

    public String getSuccessResult(){
        return basePay.infoNotify(requestParams);
    }
}
