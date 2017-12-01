package cn.seedtec.smclxd.ctl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xmehome.util.Conf;
import com.xmehome.util.JaxbUtil;
import com.xmehome.util.StringUtil;
import com.xmehome.util.XmlUtils;
import com.xmehome.ws.RespMsg;
import com.xmehome.wx.MsgEvtServ;
import com.xmehome.wx.WeiXin;
import com.xmehome.wx.WxMsg;
import com.xmehome.wx.dto.Event;
import com.xmehome.wx.dto.JsConfig;
import com.xmehome.wx.dto.LocationEvent;
import com.xmehome.wx.dto.QrcEvent;
import com.xmehome.wx.dto.TxtMsgEvent;
import com.xmehome.wx.dto.UDM;
import com.xmehome.wx.dto.UDMBtn;
import com.xmehome.wx.dto.UDMEvent;
import com.xmehome.wx.dto.UDMMatchRule;

import cn.seedtec.smclxd.UnionImageUtil;

/**
 * 微信操作所需
 */
@Controller
@RequestMapping("wx")
public class WxCtl {
    private Logger logger = Logger.getLogger(WxCtl.class);
    private static String[] apis = {
        "onMenuShareTimeline", "onMenuShareAppMessage", "onMenuShareQQ",
        "onMenuShareWeibo", "onMenuShareQZone", "startRecord", "stopRecord",
        "onVoiceRecordEnd", "playVoice", "pauseVoice", "stopVoice", "onVoicePlayEnd",
        "uploadVoice", "downloadVoice", "chooseImage", "previewImage", "uploadImage",
        "downloadImage", "translateVoice", "getNetworkType", "openLocation", "getLocation",
        "hideOptionMenu", "showOptionMenu", "hideMenuItems", "showMenuItems",
        "hideAllNonBaseMenuItem", "showAllNonBaseMenuItem", "closeWindow", "scanQRCode",
        "chooseWXPay", "openProductSpecificView", "addCard", "chooseCard", "openCard"};
    @Autowired
    private Conf conf;
    @Autowired
    private WeiXin weiXin;
    @Autowired
    private MsgEvtServ msgEvtServ;
    @ResponseBody
    @RequestMapping("lstmedia")
    public String listMedia(
            @RequestParam(value="type", required=true) String type,
            @RequestParam(value="offset", required=true) int offset,
            @RequestParam(value="count", required=true) int count){
        logger.info("listMedia");
        String pd = "{"
            + "\"type\":\"" + type + "\","
            + "\"offset\":" + offset + ","
            + "\"count\":" + count
            + "}";
        weiXin.post(conf.getStr("wxaddr"), conf.getStr("wxaccesstoken"), "material/batchget_material", pd);
        return "DONE";
    }
    @ResponseBody
    @RequestMapping("updatemnu")
    public String updateMnu(@RequestParam(value="doit", required=false) String doit){
        logger.info("updatemnu " + doit);
        //定义菜单项
        //
        UDMBtn btnMain = new UDMBtn();
        btnMain.setType("view");
        btnMain.setName("楼下商圈");
        btnMain.setUrl("http://smc2.e3322.com/smclxd/index.html");
        //
        UDMBtn btnMchApply = new UDMBtn();
        btnMchApply.setType("view");
        btnMchApply.setName("我要开店");
        btnMchApply.setUrl("http://smc2.e3322.com/smclxd/biz.html#b_apply");
        //
        UDMBtn btnApp = new UDMBtn();
        btnApp.setType("view");
        btnApp.setName("APP下载");
        btnApp.setUrl("http://a.app.qq.com/o/simple.jsp?pkgname=com.seed.smc.cmmy.yunju");
        //
        UDMBtn btnMyOrdr = new UDMBtn();
        btnMyOrdr.setType("view");
        btnMyOrdr.setName("我的订单");
        btnMyOrdr.setUrl("http://smc2.e3322.com/smclxd/index.html#c_myordr");
        //
        UDMBtn btnMchIdx = new UDMBtn();
        btnMchIdx.setType("view");
        btnMchIdx.setName("商家入口");
        btnMchIdx.setUrl("http://smc2.e3322.com/smclxd/biz.html");
        //
        UDMBtn btnSmcIdx = new UDMBtn();
        btnSmcIdx.setType("view");
        btnSmcIdx.setName("平台入口");
        btnSmcIdx.setUrl("http://smc2.e3322.com/smclxd/smc.html");
        //
        UDMBtn btnMchApplyGuide = new UDMBtn();
        btnMchApplyGuide.setType("view_limited");
        btnMchApplyGuide.setName("入驻说明");
        btnMchApplyGuide.setMedia_id("IBttjB6-YmOClEqjcCUI1pJxt81V-x9CmzwM03fQ0X4");
        //定义菜单组
        //
        UDMBtn btnOrdr = new UDMBtn();
        List<UDMBtn> mmuOrdr = new ArrayList<UDMBtn>();
        mmuOrdr.add(btnApp);
        mmuOrdr.add(btnMyOrdr);
        btnOrdr.setName("订单");
        btnOrdr.setSub_button(mmuOrdr);
        //
        UDMBtn btnMch = new UDMBtn();
        List<UDMBtn> mmuMch = new ArrayList<UDMBtn>();
        mmuMch.add(btnMchApplyGuide);
        mmuMch.add(btnMchApply);
        mmuMch.add(btnApp);
        btnMch.setName("商圈指引");
        btnMch.setSub_button(mmuMch);
        //
        UDMBtn btnMchApplyWithGuide = new UDMBtn();
        List<UDMBtn> mmuMchApplyWithGuid = new ArrayList<UDMBtn>();
        mmuMchApplyWithGuid.add(btnMchApply);
        mmuMchApplyWithGuid.add(btnMchApplyGuide);
        btnMchApplyWithGuide.setName("商家");
        btnMchApplyWithGuide.setSub_button(mmuMchApplyWithGuid);
        //删除原菜单
        weiXin.post(conf.getStr("wxaddr"), conf.getStr("wxaccesstoken"), "menu/delete", null);
        //开始组装菜单并发送
        UDM udm = new UDM();
        UDMMatchRule rule = new UDMMatchRule();
        List<UDMBtn> btns = new ArrayList<UDMBtn>();
        String mmu;
        //
        btns.clear();
        btns.add(btnMain);
        btns.add(btnOrdr);
        btns.add(btnMchApplyWithGuide);
        udm.setButton(btns);
        mmu = JaxbUtil.bean2Json(udm);
        logger.info("user menu " + mmu);
        if("yes".equals(doit))
            weiXin.putUDM(conf.getStr("wxaddr"), conf.getStr("wxaccesstoken"), mmu);
        else
            logger.info("no really");
        //
        btns.clear();
        btns.add(btnMain);
        btns.add(btnMchIdx);
        btns.add(btnMch);
        rule.setTag_id("100");
        udm.setButton(btns);
        udm.setMatchrule(rule);
        mmu = JaxbUtil.bean2Json(udm);
        logger.info("biz menu " + mmu);
        if("yes".equals(doit))
            weiXin.putAddUDM(conf.getStr("wxaddr"), conf.getStr("wxaccesstoken"), mmu);
        else
            logger.info("no really");
        //
        btns.clear();
        btns.add(btnMain);
        btns.add(btnMch);
        btns.add(btnSmcIdx);
        rule.setTag_id("101");
        udm.setButton(btns);
        udm.setMatchrule(rule);
        mmu = JaxbUtil.bean2Json(udm);
        logger.info("smc menu " + mmu);
        if("yes".equals(doit))
            weiXin.putAddUDM(conf.getStr("wxaddr"), conf.getStr("wxaccesstoken"), mmu);
        else
            logger.info("no really");
        //
        return "DONE";
    }
    /**
     * 返回jsconfig用于微信前端页面配置
     */
    @ResponseBody
    @RequestMapping("getJsConfig")
    public JsConfig getJsConfig(@RequestParam(value="url2", required=true) String url2){
        logger.info("getting JsConfig " + url2);
        String ticket = conf.getStr("wxjsapiticket");
        logger.info("jsapiticket is " + ticket);
        JsConfig config = weiXin.getJsConfig(ticket, url2, conf.getStr("wxappid"), apis);
        logger.info("JsConfig made");
        return config;
    }
    @ResponseBody
    @RequestMapping("dlMedia")
    public RespMsg<String> dlMedia(@RequestParam(value="mediaId", required=true) String mediaId){
        logger.info("dlMedia(" + mediaId + ")");
        int bytesum = 0;
        int byteread = 0;
        String httpUrl = conf.getStr("wxaddr") + "media/get?access_token=" + conf.getStr("wxaccesstoken") + "&media_id=" + mediaId;
        logger.info(httpUrl);
        String saveFile = conf.getStr("imgpath") + mediaId + ".jpg";
        logger.info(saveFile);
        String small = conf.getStr("imgpath") + "s/" + mediaId + ".jpg";
        logger.info(small);
        String big = conf.getStr("imgpath") + "b/" + mediaId + ".jpg";
        logger.info(big);
        String med = conf.getStr("imgpath") + "m/" + mediaId + ".jpg";
        logger.info(med);
        //
        File file = new File(saveFile);
        if(file.exists()){
            logger.info("file exists.");
            return new RespMsg<String>("文件已存在");
        }
        //
        URL url = null;
        try{
            url = new URL(httpUrl);
        }catch(MalformedURLException e1){
            return new RespMsg<String>("下载地址出错", false);
        }
        try{
            URLConnection conn = url.openConnection();
            InputStream inStream = conn.getInputStream();
            FileOutputStream fs = new FileOutputStream(saveFile);
            byte[] buffer = new byte[1204];
            while ((byteread = inStream.read(buffer)) != -1) {
                bytesum += byteread;
                fs.write(buffer, 0, byteread);
            }
            logger.debug("bytesum=" + bytesum);
            fs.close();
            inStream.close();
        }catch(FileNotFoundException e){
            return new RespMsg<String>("文件操作失败", false);
        }catch(IOException e){
            return new RespMsg<String>("文件IO出错", false);
        }
        UnionImageUtil.box(saveFile, small, 100);
        UnionImageUtil.box(saveFile, big,0);
        UnionImageUtil.box(saveFile, med, 300);
        return new RespMsg<String>("下载成功");
    }
    /**
     * 事件分发
     * 根据msgType分发事件
     * @return
     */
    @RequestMapping(value="", method={RequestMethod.POST})
    public void eventDisp(@RequestBody String pd,
            @RequestParam(value="signature", required=true) String signature,
            @RequestParam(value="timestamp", required=true) String timestamp,
            @RequestParam(value="nonce", required=true) String nonce,
            HttpServletResponse response
            ) throws IOException {
        logger.info(String.format("eventDisp(%s, %s, %s)", signature, timestamp, nonce));
        response.setContentType("text/xml");
        response.setCharacterEncoding("utf8");
        PrintWriter out = response.getWriter();
        //检查输入
        if(!checkSignature(signature, timestamp, nonce)){
            logger.info("未知来源的消息");
            return;
        }
        //取得消息类型
        String msgType = "";
        logger.debug("pd=" + pd);
        Document doc = XmlUtils.genDoc(pd);
        if(doc==null){
            logger.error("invalid Xml");
            return;
        }
        Element e = doc.getRootElement();
        msgType = e.getChildText("MsgType");
        logger.debug("msgType=" + msgType);
        if(msgType.equals("event")){
            String evtType = e.getChildText("Event");
            if(evtType.equals("LOCATION")){
                LocationEvent evt = JaxbUtil.xml2Bean(pd, LocationEvent.class);
                if(evt != null){
                    WxMsg msg = msgEvtServ.process(evt);
                    if(msg!=null) out.print(msg.toXmlStr());
                }else{
                    logger.error("make LocationEvent error");
                }
            }else if(evtType.equals("CLICK")){
                UDMEvent evt = JaxbUtil.xml2Bean(pd, UDMEvent.class);
                if(evt != null){
                    WxMsg msg = msgEvtServ.process(evt);
                    if(msg!=null) out.print(msg.toXmlStr());
                }else{
                    logger.error("make UDMEvent error");
                }
            }else if(evtType.equals("subscribe") || evtType.equals("SCAN")){
            	QrcEvent evt = JaxbUtil.xml2Bean(pd, QrcEvent.class);
                if(evt != null){
                    List<WxMsg> list = msgEvtServ.process(evt);
                    if(null!=list.get(0)) out.print(list.get(0).toXmlStr());
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    if(list.size()>1) weiXin.sendTplMsg(conf.getStr("wxaddr"), conf.getStr("wxaccesstoken"), list.get(1));
                }else{
                    logger.error("make QrcEvent error");
                }
            }else{
                Event evt = JaxbUtil.xml2Bean(pd, Event.class);
                if(evt != null){
                    WxMsg msg = msgEvtServ.process(evt);
                    if(msg!=null) out.print(msg.toXmlStr());
                }else{
                    logger.error("make Event error");
                }
            }
        }else if(msgType.equals("text")){
            TxtMsgEvent evt = JaxbUtil.xml2Bean(pd, TxtMsgEvent.class);
            if(evt != null){
                WxMsg msg = msgEvtServ.process(evt);
                if(msg!=null) out.print(msg.toXmlStr());
            }else{
                logger.error("make TxtMsgEvent error");
            }
        }else{
            logger.error("unsupport msgType");
        }
        out.close();
    }
    /**
     * 微信接入验证
     */
    @RequestMapping(value="", method={RequestMethod.GET})
    @ResponseBody
    public String accessPerm(
            @RequestParam(value="signature", required=true) String signature,
            @RequestParam(value="timestamp", required=true) String timestamp,
            @RequestParam(value="nonce", required=true) String nonce,
            @RequestParam(value="echostr", required=true) String echostr){
        logger.info(String.format("accessPerm(%s, %s, %s, %s)", signature, timestamp, nonce, echostr));
        if(checkSignature(signature, timestamp, nonce)){
            logger.info("check pass");
            return echostr;
        }
        return "ERROR";
    }
    /**
     * 签名验证
     */
    private boolean checkSignature(String signature, String timestamp, String nonce){
        String token = conf.getStr("wxtoken");
        String[] tmpArr = {token, timestamp, nonce};
        Arrays.sort(tmpArr);
        String tmpStr = "";
        for(int i=0; i<tmpArr.length; i++) tmpStr = tmpStr + tmpArr[i];
        logger.debug("tmpStr=" + tmpStr);
        tmpStr = StringUtil.sha1(tmpStr);
        logger.info("sha1=" + tmpStr);
        return tmpStr.equals(signature);
    }
}
