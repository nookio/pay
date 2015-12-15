package pays;

import models.Payment;

import java.util.Map;

/**
 * Created by nookio on 15/8/13.
 */
public class CreatePay {

    private MyBasePay basePay = null;

    private Integer count;

    private Integer itemId;

    private Integer loginUserId;

    private String host;

    //这里可以根据需要定义不同的构造方法
    public CreatePay(Short payType, Integer count, Integer itemId, Integer loginUserId, String host){
        this.count = count;
        this.itemId = itemId;
        this.loginUserId = loginUserId;
        this.host = host;

        switch (payType) {
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
            case Payment.TYPE_WECHAT_SCAN:
                basePay = new WechatScanPay();
                break;
            default:
                basePay = null;
        }
        getPrePayInfo();
    }

    public Map<String, String> getPrePayInfo(){
        try {
            basePay.createNewPay(loginUserId, host, count, itemId);
            return basePay.getPrePayInfo();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
