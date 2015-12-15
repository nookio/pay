package controllers;

import actions.annoations.EbeanTransactional;
import config.BaseConfig;
import config.WeChatConfig;
import controllers.forms.PaymentForm;
import helper.ResponseHelper;
import models.Payment;
import org.w3c.dom.Document;
import pays.CreatePay;
import pays.NotifyPay;
import pays.WechatAppPay;
import pays.WechatScanPay;
import play.Configuration;
import play.Logger;
import play.data.Form;
import play.mvc.BodyParser;
import play.mvc.Result;
import services.alipay.AliPayHelper;
import services.unionpay.UnionPayHelper;
import services.wechat.WechatPayHelper;
import utils.StringUtil;
import utils.XmlUtil;

import java.util.Map;

/**
 * Created by nookio on 15/6/9.
 */
public class Payments extends Application {

    private static Logger.ALogger logger = play.Logger.of(Payments.class);


    /**
     * 这里是根据客户端传进来的paymentType来进行不同的支付类型的跳转
     * 1：支付宝
     * 3：银联支付
     * 5：微信支付
     *
     * @return
     */
    @EbeanTransactional
    public Result createAppPays(){
        Form<PaymentForm> form = Form.form(PaymentForm.class).bindFromRequest();
        if (validateForm(form)) {
            PaymentForm paymentForm = form.get();
            checkToken(paymentForm.getLoginUserId(), paymentForm.getLoginToken());
            //logger.info(request().host() + "   " + request().uri() + "    " + request().remoteAddress() + "     " + request().path());
            logger.info(paymentForm.getLoginUserId() + "这里是用户端传过来的id");
            setHost();
            return ResponseHelper.wrapPay(new CreatePay(paymentForm.getPaymentType(),
                    paymentForm.getCount(), paymentForm.getItemId(), paymentForm.getLoginUserId(), BaseConfig.HOST));
        }else
            logger.error("创建支付信息出错" + form.get().toMap().toString());
        return null;
    }

    @EbeanTransactional
    public Result aliPayAppNotify() {
        Map<String, String> requestParams = Form.form().bindFromRequest().data();
        logger.info(requestParams.toString());
        if (AliPayHelper.verify(requestParams)) {
            return ResponseHelper.wrapPayNotify(new NotifyPay(Payment.TYPE_ALIPAY_APP, requestParams));
        } else {
            logger.error("创建支付信息出错" + requestParams.toString());
            return ResponseHelper.wrapAjax("success");
        }
    }

    @EbeanTransactional
    @BodyParser.Of(BodyParser.Xml.class)
    public Result wechatPayAppNotify() {
        //logger.info(request().body().toString());
        Document dom = request().body().asXml();
        if (dom ==null){
            logger.error("信息为空"+request().body().asFormUrlEncoded().toString());
            return ResponseHelper.wrapAjax(WechatAppPay.notifySuccess());
        }else{
            try {
                Map<String, String> requestParams = XmlUtil.decodeDocumentToMap(dom);
                logger.info(requestParams.toString());
                if (WechatPayHelper.verify(requestParams, WeChatConfig.API_KEY)) {
                    return ResponseHelper.wrapPayNotify(new NotifyPay(Payment.TYPE_WECHAT_APP, requestParams));
                } else {
                    logger.error("创建支付信息出错" + requestParams.toString());
                    return ResponseHelper.wrapAjax(WechatAppPay.notifySuccess());
                }
            } catch (Exception e){
                logger.error("转换失败"+e.getMessage()+request().body().asXml().toString());
                return ResponseHelper.wrapAjax(WechatAppPay.notifySuccess());
            }
        }
    }

    @EbeanTransactional
    @BodyParser.Of(BodyParser.Xml.class)
    public Result wechatPayScanNotify() {
        //logger.info(request().body().toString());
        Document dom = request().body().asXml();
        if (dom ==null){
            logger.error("信息为空" + request().body().asFormUrlEncoded().toString());
            return ResponseHelper.wrapAjax(WechatScanPay.notifySuccess());
        }else{
            try {
                Map<String, String> requestParams = XmlUtil.decodeDocumentToMap(dom);
                logger.info(requestParams.toString());
                if (WechatPayHelper.verify(requestParams, WeChatConfig.MP_API_KEY)) {
                    return ok(new NotifyPay(Payment.TYPE_WECHAT_SCAN, requestParams);
                } else {
                    logger.error("创建支付信息出错" + requestParams.toString());
                    return ResponseHelper.wrapAjax(WechatScanPay.notifySuccess());
                }
            } catch (Exception e){
                logger.error("转换失败"+e.getMessage()+request().body().asXml().toString());
                return ResponseHelper.wrapAjax(WechatScanPay.notifySuccess());
            }
        }
    }

    @EbeanTransactional
    public Result unionPayAppNotify() {
        Map<String, String> requestParams = Form.form().bindFromRequest().data();
        if (UnionPayHelper.validate(requestParams)) {
            logger.info("银联支付回掉信息" + requestParams.toString());
            return ResponseHelper.wrapPayNotify(new NotifyPay(Payment.TYPE_UNIONPAY_WAP, requestParams));
        } else {
            logger.error("创建支付信息出错" + requestParams.toString());
            return ResponseHelper.wrapAjax("success");
        }
    }

    @EbeanTransactional
    public Result iosInAppPurchaseNotify() {
        Map<String, String> requestParams = Form.form().bindFromRequest().data();
        logger.info("ios内购信息" + requestParams.toString());
        return ResponseHelper.wrapPayNotify(new NotifyPay(Payment.TYPE_IOS_IN_APP_PURCHASE, requestParams));
    }
}