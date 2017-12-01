package cn.seedtec.smclxd.ctl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.seedtec.smclxd.BizException;
import cn.seedtec.smclxd.Profile;
import cn.seedtec.smclxd.bean.CustAddr;
import cn.seedtec.smclxd.bean.Customer;
import cn.seedtec.smclxd.dto.CustAddrFiltered;
import cn.seedtec.smclxd.service.CmmyServ;
import cn.seedtec.smclxd.service.CustServ;

import com.xmehome.util.Conf;
import com.xmehome.util.JaxbUtil;
import com.xmehome.util.Mas;
import com.xmehome.util.RandUtil;
import com.xmehome.util.StringUtil;
import com.xmehome.ws.RespMsg;
import com.xmehome.wx.WeiXin;
import com.xmehome.wx.dto.H5Token;

/**
 * C端用户相关
 */
@Controller
@RequestMapping("cust")
public class CustCtl {
    private Logger logger = Logger.getLogger(CustCtl.class);
    @Autowired
    private Conf conf;
    @Autowired
    private Profile profile;
    @Autowired
    private WeiXin weiXin;
    @Autowired
    private CustServ custServ;
    @Autowired
    private CmmyServ cmmyServ;
    @ResponseBody
    @RequestMapping("sesn")
    public RespMsg<Customer> sesn(){
        logger.info("sesn()");
        Customer user = profile.copy2Cust();
        RespMsg<Customer> ret = new RespMsg<Customer>("done", profile.getUid()>0);
        ret.setCont(user);
        return ret;
    }
    @RequestMapping("wxo2")
    public String oauth2(
            @RequestParam(value="code", required=true) String code,
            @RequestParam(value="state", required=true) String state){
        logger.info(String.format("wxo2(%s, %s)", code, state));
        String page = "redirect:/" + (state.isEmpty()?"index.html":state);
        H5Token token = weiXin.getH5Token(conf.getStr("wxoauth2addr"), conf.getStr("wxappid"), conf.getStr("wxappsecret"), code);
        if(token != null){
            profile.setOpenid(token.getOpenid());
        }else{
            profile.setOpenid(null);
        }
        return page;
    }
    @ResponseBody
    @RequestMapping("wxlogin")
    public RespMsg<Customer> login(){
        logger.info("wxlogin()");
        String openid = profile.getOpenid();
        if(openid == null)
            return new RespMsg<Customer>("未登录", false);
        Customer usr = custServ.getByOpenid(openid);
        if(usr == null){
            usr = custServ.regByOpenid(openid);
            if(usr == null)
                return new RespMsg<Customer>("新增用户数据失败", false);
        }
        String key = StringUtil.sha1(String.format("%s%s", openid, usr.getPassword()));
        profile.copyFromUser(usr);
        usr.setPassword(null);
        RespMsg<Customer> ret = new RespMsg<Customer>(key);
        ret.setCont(usr);
        return ret;
    }
    @ResponseBody
    @RequestMapping("login")
    public RespMsg<Customer> login(
            @RequestParam(value="mobile", required=true) String mobile,
            @RequestParam(value="vcode", required=true) String vcode){
        logger.info(String.format("login(%s, %s)", mobile, vcode));
        logger.debug(profile.getVcode());
        if(profile.getVcode()==null || !profile.getVcode().equals(mobile + vcode))
            return new RespMsg<Customer>("验证码有误", false);
        Customer usr = custServ.getByMobile(mobile);
        if(usr == null){
            usr = custServ.regByMobile(mobile);
            if(usr == null)
                return new RespMsg<Customer>("创建用户失败", false);
        }
        String key = StringUtil.sha1(String.format("%s%s", mobile, usr.getPassword()));
        profile.copyFromUser(usr);
        usr.setPassword(null);
        RespMsg<Customer> ret = new RespMsg<Customer>(key);
        ret.setCont(usr);
        return ret;
    }
    @ResponseBody
    @RequestMapping("bindMobile")
    public RespMsg<String> bindMobile(
            @RequestParam(value="mobile", required=true) String mobile,
            @RequestParam(value="vcode", required=true) String vcode){
        logger.info(String.format("bindMobile(%s, %s)", mobile, vcode));
        logger.debug(profile.getVcode());
        if(profile.getVcode()==null || !profile.getVcode().equals(mobile + vcode))
            return new RespMsg<String>("验证码有误", false);
        Customer usr = custServ.getByMobile(mobile);
        if(usr != null){
            return new RespMsg<String>("该号码已绑定其他微信", false);
        }else{
            usr=custServ.bindMobile(profile.getOpenid(), mobile);
        }
        String key = StringUtil.sha1(String.format("%s%s", mobile, usr.getPassword()));
        RespMsg<String> ret = new RespMsg<String>(key);
        ret.setCont("绑定成功");
        return ret;
    }
    @ResponseBody
    @RequestMapping("getVcode")
    public RespMsg<String> getVcode(@RequestParam(value="mobile", required=true) String mobile){
        logger.info("getVcode(" + mobile + ")");
        String v = RandUtil.getRandNum(4);
        logger.info(v);
        String[] m = {mobile};
        Mas.sendSM(m, "验证码 " + v);
        profile.setVcode(mobile + v);
        return new RespMsg<String>("done");
    }
    @ResponseBody
    @RequestMapping("loginbg")
    public RespMsg<Customer> loginbg(
            @RequestParam(value="mobile", required=true) String mobile,
            @RequestParam(value="key", required=true) String key) {
        logger.info(String.format("loginbg(%s, %s)", mobile, key));
        Customer user = custServ.getByMobile(mobile);
        if(user==null){
            return new RespMsg<Customer>("登录失败", false);
        }
        String chk = StringUtil.sha1(String.format("%s%s", mobile, user.getPassword()));
        if(!key.equals(chk))
            return new RespMsg<Customer>("密钥不匹配", false);
        profile.copyFromUser(user);
        user.setPassword(null);
        RespMsg<Customer> ret = new RespMsg<Customer>("登录成功");
        ret.setCont(user);
        return ret;
    }
    @ResponseBody
    @RequestMapping("loginbgwx")
    public RespMsg<Customer> loginbgwx(
            @RequestParam(value="openid", required=true) String openid,
            @RequestParam(value="key", required=true) String key) {
        logger.info(String.format("loginbgwx(%s, %s)", openid, key));
        Customer user = custServ.getByOpenid(openid);
        if(user==null){
            return new RespMsg<Customer>("登录失败", false);
        }
        String chk = StringUtil.sha1(String.format("%s%s", openid, user.getPassword()));
        if(!key.equals(chk))
            return new RespMsg<Customer>("密钥不匹配", false);
        profile.copyFromUser(user);
        user.setPassword(null);
        RespMsg<Customer> ret = new RespMsg<Customer>("登录成功");
        ret.setCont(user);
        return ret;
    }
    @ResponseBody
    @RequestMapping("getaddr")
    public RespMsg<List<CustAddrFiltered>> getAddress(@RequestParam(value="mchId", required=true) long mchId){
        logger.info("getAddress(" + mchId + ")");
        if(profile.getUid()<=0) return new RespMsg<List<CustAddrFiltered>>("未登录", false);
        List<CustAddrFiltered> addr = custServ.listCustAddrFiltered(profile.getUid(), mchId);
        RespMsg<List<CustAddrFiltered>> ret = new RespMsg<List<CustAddrFiltered>>("done");
        ret.setCont(addr);
        return ret;
    }
    @ResponseBody
    @RequestMapping("saveaddr")
    public RespMsg<CustAddr> saveAddress(@RequestBody CustAddr custAddr){
        logger.info("saveAddress(" + JaxbUtil.bean2Json(custAddr) + ")");
        if(profile.getUid()<=0) return new RespMsg<CustAddr>("未登录", false);
        CustAddr ca = null;
        try{
            custAddr.getCustomer().setId(profile.getUid());
            ca = custServ.saveCustAddr(custAddr);
        }catch(BizException e){
            return new RespMsg<CustAddr>(e.getMessage(), false);
        }
        RespMsg<CustAddr> ret = new RespMsg<CustAddr>("done");
        ret.setCont(ca);
        return ret;
    }
    /**指定社区收货地址*/
    @ResponseBody
    @RequestMapping("addrByCid")
    public RespMsg<CustAddr> addressByCid(@RequestParam(value="cmmyId", required=true) long cmmyId){
        logger.info("addressByCid("+cmmyId+")");
        if(profile.getUid()<=0) 
            return new RespMsg<CustAddr>("未登录", false);
        CustAddr addr = custServ.addressByCid(profile.getUid(), cmmyId);
        if(addr==null)
            return new RespMsg<CustAddr>("请设置地址", false);
        RespMsg<CustAddr> ret = new RespMsg<CustAddr>("done");
        ret.setCont(addr);
        return ret;
    }
}
