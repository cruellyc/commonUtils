package cn.seedtec.smclxd.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xmehome.util.Conf;
import com.xmehome.util.JaxbUtil;
import com.xmehome.util.RandUtil;
import com.xmehome.wx.WeiXin;

import cn.seedtec.smclxd.BaseLst;
import cn.seedtec.smclxd.BizException;
import cn.seedtec.smclxd.OrdStatus;
import cn.seedtec.smclxd.bean.CustAddr;
import cn.seedtec.smclxd.bean.Customer;
import cn.seedtec.smclxd.bean.Eva2Goods;
import cn.seedtec.smclxd.bean.Eva2Mch;
import cn.seedtec.smclxd.bean.Goods;
import cn.seedtec.smclxd.bean.MchUser;
import cn.seedtec.smclxd.bean.Merchant;
import cn.seedtec.smclxd.bean.Order;
import cn.seedtec.smclxd.bean.OrderItem;
import cn.seedtec.smclxd.bean.OrderLog;
import cn.seedtec.smclxd.bean.PayOrdr;
import cn.seedtec.smclxd.bean.SmcUser;
import cn.seedtec.smclxd.dao.CustAddrDao;
import cn.seedtec.smclxd.dao.CustDao;
import cn.seedtec.smclxd.dao.Eva2GodDao;
import cn.seedtec.smclxd.dao.Eva2MchDao;
import cn.seedtec.smclxd.dao.GoodsDao;
import cn.seedtec.smclxd.dao.MchDao;
import cn.seedtec.smclxd.dao.MchUserDao;
import cn.seedtec.smclxd.dao.OrdItemDao;
import cn.seedtec.smclxd.dao.OrdLogDao;
import cn.seedtec.smclxd.dao.OrderDao;
import cn.seedtec.smclxd.dao.PayOrdrDao;
import cn.seedtec.smclxd.dto.CartGoods;
import cn.seedtec.smclxd.dto.CreaEvaReq;
import cn.seedtec.smclxd.dto.GetCartResp;
import cn.seedtec.smclxd.dto.GetWaitOrdResp;
import cn.seedtec.smclxd.dto.GrandOrder;
import cn.seedtec.smclxd.dto.MchCustAnaResp;
import cn.seedtec.smclxd.dto.MchSplayResp;
import cn.seedtec.smclxd.dto.OrdItemMsg;
import cn.seedtec.smclxd.dto.OrdLstBody;
import cn.seedtec.smclxd.dto.OrderOut;
import cn.seedtec.smclxd.dto.TplGoodsStChngMsg;
import cn.seedtec.smclxd.dto.TplNewOrdrMsg;
import cn.seedtec.smclxd.dto.TplOrdrStChngMsg;
import cn.seedtec.smclxd.dto.TplPaySuccMsg;

@Service
public class OrderImpl implements OrderServ {
    private Logger logger = Logger.getLogger(OrderImpl.class);
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Autowired
    private Conf conf;
    @Autowired
    private WeiXin weiXin;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private OrdItemDao ordItemDao;
    @Autowired
    private GoodsDao goodsDao;
    @Autowired
    private MchDao mchDao;
    @Autowired
    private CustDao custDao;
    @Autowired
    private MchUserDao mchUserDao;
    @Autowired
    private CustAddrDao custAddrDao;
    @Autowired
    private Eva2MchDao eva2MchDao;
    @Autowired
    private Eva2GodDao eva2GodDao;
    @Autowired
    private OrdLogDao ordLogDao;
    @Autowired
    private PayOrdrDao payOrdrDao;
    @Autowired
    private PayServ payServ;
    @Autowired
    private CartServ cartServ;
    @Autowired
    private MchUserServ mchUserServ;
    @Autowired
    private SmcUserServ smcUserServ;
    @Autowired
    private OrdItemServ ordItemServ;
    @Override
    public Order get(long orderId){
        logger.info("get(" + orderId + ")");
        return orderDao.get(orderId);
    }
    /**
     * 获取订单详情
     */
    @Override
    public GrandOrder getGrand(long orderId) {
        logger.info("getGrand " + orderId);
        Order order = orderDao.get(orderId);
        List<OrdItemMsg> itemList = new ArrayList<OrdItemMsg>();
        Iterator<OrderItem> iOI = ordItemDao.getItemList(orderId).iterator();
        int num = 0;
        while(iOI.hasNext()){
            OrderItem oi = iOI.next();
            Goods goods = goodsDao.get(oi.getGoodsId());
            OrdItemMsg oim = new OrdItemMsg();
            oim.setOrderItem(oi);
            oim.setGoods(goods);
            itemList.add(oim);
            num = num + oi.getGoodsNum();
        }
        Merchant mch = mchDao.getMch(order.getMchId());
        Eva2Mch eva2Mch = eva2MchDao.getEva2Mch(orderId);
        List<OrderLog> ordLogList = new ArrayList<>();
        OrderLog ordLog = ordLogDao.get(orderId,OrdStatus.W2PAY);
        ordLogList.add(ordLog);
        CustAddr addr = custAddrDao.getById(order.getCustAddr());

        GrandOrder ordData = new GrandOrder();
        if(order.getStatus().equals(OrdStatus.W2PAY)){
            Calendar timeout = Calendar.getInstance();
            Calendar dealTime = Calendar.getInstance();
            try {
                dealTime.setTime(sdf.parse(ordLog.getTime()));
                dealTime.add(Calendar.MINUTE, 20);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            ordData.setInterval((dealTime.getTimeInMillis() - timeout.getTimeInMillis())/1000);
        }
        ordData.setOrder(order);
        ordData.setMch(mch);
        ordData.setItemList(itemList);
        ordData.setEva2Mch(eva2Mch);
        ordData.setOrdLogList(ordLogList);
        ordData.setAddr(addr);
        ordData.setGoodsNum(num);
        return ordData;
    }
    @Override
    public List<GrandOrder> listGrandByCust(long custId, String status){
        List<GrandOrder> ret = new ArrayList<GrandOrder>();
        String[] sts = {null};
        if(status!=null){
            sts = status.split(",");
        }
        for(String st:sts){
            Iterator<Order> itOrder = orderDao.listByCust(custId, st).iterator();
            while(itOrder.hasNext()){
                Order order = itOrder.next();
                GrandOrder go = this.getGrand(order.getId());
                ret.add(go);
            }
        }
        this.sortIntMethod(ret);
        return ret;
    }

    @Override
    public BaseLst<GrandOrder> listGrandByMch(long mchId, String status, String time, String keywrd){
        BaseLst<GrandOrder> list = new BaseLst<GrandOrder>();
        List<GrandOrder> ret = new ArrayList<GrandOrder>();
        String[] sts = {null};
        if(status!=null){
            sts = status.split(",");
        }
        for(String st:sts){
            Iterator<Order> itOrder = orderDao.listByMch(mchId, st, time, keywrd).iterator();
            while(itOrder.hasNext()){
                Order order = itOrder.next();
                GrandOrder go = this.getGrand(order.getId());
                ret.add(go);
            }
        }
        this.sortIntMethod(ret);
        list.setLst(ret);
        list.setW2accept(orderDao.waitNum(mchId, OrdStatus.W2ACCEPT, time, keywrd));
        list.setW2disp(orderDao.waitNum(mchId, OrdStatus.W2DISPATCH, time, keywrd));
        return list;
    }
    /**按订单id降序排序*/
    public void sortIntMethod(List<GrandOrder> list){  
        Collections.sort(list, new Comparator<Object>(){  
            @Override  
            public int compare(Object g1, Object g2) {
                GrandOrder o1 = (GrandOrder) g1;
                GrandOrder o2 = (GrandOrder) g2;
                if(o1.getOrder().getId()>o2.getOrder().getId()){  
                    return -1;  
                }else if(o1.getOrder().getId()==o2.getOrder().getId()){  
                    return 0;  
                }else{  
                    return 1;  
                }  
            }             
        });
    }
    /**PC获取订单列表*/
    @Override
    public BaseLst<GrandOrder> getOrdList(OrdLstBody lstBody) {
        logger.info(String.format("getOrdList(%s)", JaxbUtil.bean2Json(lstBody)));
        lstBody.setPageNum((lstBody.getPageNum() - 1)*lstBody.getTotal());
        BaseLst<GrandOrder> base = new BaseLst<GrandOrder>();
        String codes = "(";
        if(!lstBody.getCity().equals("0")){
            String st[] = lstBody.getCity().split(",");
            for(String s:st){
                if(codes.equals("(")){
                    codes = codes + "'" +s+"'";
                }else{
                    codes = codes + ", '"+ s+"'";
                }
            }
            codes = codes + ")";
            lstBody.setCity(codes);
        }
        base.setTotal(orderDao.numByBody(lstBody));
        if(base.getTotal()<=0){
            return null;
        }
        List<GrandOrder> grandLise = new ArrayList<GrandOrder>();
        List<Order> list = orderDao.getOrdList(lstBody);
        Iterator<Order> it = list.iterator();
        while(it.hasNext()){
            Order ord = it.next();
            GrandOrder go = this.getGrand(ord.getId());
            go.setOrder(ord);
            grandLise.add(go);
        }
        base.setLst(grandLise);
        return base;
    }

    /**C端待操作订单数*/
    @Override
    public GetWaitOrdResp getWaitOrd(long custid) {
        GetWaitOrdResp gwo = new GetWaitOrdResp();
        gwo.setW2pay(orderDao.getWaitOrd(custid, OrdStatus.W2PAY));
        gwo.setW2accept(orderDao.getWaitOrd(custid, OrdStatus.W2ACCEPT));
        gwo.setW2disp(orderDao.getWaitOrd(custid, OrdStatus.W2DISPATCH) + orderDao.getWaitOrd(custid, OrdStatus.W2CONFIRM));
        gwo.setW2eva(orderDao.getWaitOrd(custid, OrdStatus.W2EVALUATE));
        logger.info(String.format("%s", JaxbUtil.bean2Json(gwo)));
        return gwo;
    }

    /**添加订单*/
    @Transactional
    @Override
    public long create(long custId, long mchId, long addrId, String words, String time, int iscome) {
        logger.info(String.format("create(%d, %d, %d, %s, %s)", custId, mchId, addrId, words, time));
        GetCartResp cart = cartServ.getCart(custId, mchId);
        int gn = cart.getCartGoods().size();
        if(gn<=0)
            throw new BizException("购物车空，无法创建订单");
        String gname = cart.getCartGoods().get(0).getGoods().getName();
        boolean canDisp = cart.getD2dFee()>=0;
        Order order = new Order();
        order.setCode(RandUtil.genTimeSn());
        order.setMchId(mchId);
        order.setCustomerId(custId);
        order.setBody(gname + (gn>1?"等":""));
        order.setTotal(cart.getPrice());
        order.setTip(0);
        if(canDisp && iscome==1){
            order.setTotal(cart.getPrice() + cart.getD2dFee());
            order.setTip(cart.getD2dFee());
        }
        order.setArriveTime(time);
        order.setCustAddr(addrId);
        order.setLeaveWord(words);
        order.setStatus(OrdStatus.W2PAY);
        order.setPayed(false);
        if(orderDao.insert(order)!=1)
            throw new BizException("添加订单失败");
        long orderId = order.getId();
        logger.info("new order " + orderId);
        Iterator<CartGoods> icgs = cart.getCartGoods().iterator();
        while(icgs.hasNext()){
            CartGoods cg = icgs.next();
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(orderId);
            orderItem.setGoodsId(cg.getGoods().getId());
            orderItem.setGoodsPrice(cg.getGoods().getUnitPrice());
            orderItem.setGoodsNum(cg.getCart().getNum());
            orderItem.setItemPrice(cg.getCart().getPrice());
            if(ordItemDao.insert(orderItem) != 1)
                throw new BizException("添加订单明细出错");
            logger.info("new order item " + orderItem.getId());
        }
        OrderLog ordLog = new OrderLog();
        ordLog.setOrid(orderId);
        ordLog.setLog(OrdStatus.W2PAY);
        ordLog.setLogText(OrdStatus.A2CREATE);
        if(ordLogDao.createLog(ordLog) != 1)
            throw new BizException("添加订单日志出错");
        logger.info("new order log " + ordLog.getId());
        cartServ.cleanAll(custId, mchId);
        return order.getId();
    }
    /**订单进度表*/
    @Override
    public List<OrderLog> getOrdLog(long ordid) {
        logger.info("getOrdLog(" + ordid + ")");
        List<OrderLog> list = ordLogDao.getOrdLog(ordid);
        return list;
    }

    /**添加评价*/
    @Transactional
    @Override
    public void creEva(CreaEvaReq creEvaReq) {
        logger.info(String.format("creEva(%s)", JaxbUtil.bean2Json(creEvaReq)));
        if (eva2MchDao.creEvaMch(creEvaReq.getEvaMch()) != 1)
            throw new BizException("添加评价失败，请稍后再试");
        chngStatus(creEvaReq.getEvaMch().getOrderId(), OrdStatus.W2EVALUATE, OrdStatus.DONE, OrdStatus.A2EVALUATE, creEvaReq.getEvaMch().getUserId(), null);
        Iterator<Eva2Goods> it = creEvaReq.getList().iterator();
        while (it.hasNext()) {
            Eva2Goods evaGod = it.next();
            evaGod.setUserId(creEvaReq.getEvaMch().getUserId());
            if (eva2GodDao.creEvaGod(evaGod) != 1)
                throw new BizException("添加评价失败，请稍后再试");
        }
    }

    /**营业统计*/
    @Override
    public MchSplayResp mchSplay(String staDate, String endDate, long mchId) {
        logger.info(String.format("mchSplay(%s, %s, %d)", staDate, endDate, mchId));
        MchSplayResp msr = new MchSplayResp();
        msr.setTurnover(orderDao.getTurnover(staDate, endDate, mchId));
        msr.setOrdNum(orderDao.getOrdNum(staDate, endDate, mchId));
        if(msr.getOrdNum()==0 || msr.getTurnover()==0)
            msr.setOrdAve(0);
        else
            msr.setOrdAve(msr.getTurnover()/(msr.getOrdNum() * 1.00));
        return msr;
    }

    /**顾客分析*/
    @Override
    public MchCustAnaResp mchCustAna(String staDate, String endDate, long mchId) {
        logger.info("mchCustAna(" + staDate + ", " + endDate + ", " + mchId + ")");
        MchCustAnaResp mcar = new MchCustAnaResp();
        mcar.setNewCust(orderDao.newCustNum(staDate, endDate, mchId));
        mcar.setRegular(orderDao.regularNum(staDate, endDate, mchId));
        if(mcar.getRegular()==0)
            mcar.setRepurchase(0);
        else
            mcar.setRepurchase(mcar.getRegular()*100/(mcar.getNewCust()+mcar.getRegular()));
        return mcar;
    }
    /**订单状态修改*/
    @Transactional
    @Override
    public OrderLog chngStatus(long orderId, String oldStatus, String newStatus, String logText, long userId, String phone) {
        logger.info(String.format("chngStatus %d, %s, %s, %d, %s", orderId, oldStatus, newStatus, userId, phone));
        if(orderDao.chngStatus(orderId, oldStatus, newStatus)!=1)
            throw new BizException("数据更新失败");
        OrderLog ordLog = new OrderLog();
        ordLog.setOrid(orderId);
        ordLog.setLog(newStatus);
        ordLog.setLogText(logText);
        ordLog.setWho(userId);
        ordLog.setTime(sdf.format(new Date()));
        if(phone!=null)
            ordLog.setPhone(phone);
        if(ordLogDao.createLog(ordLog)!=1)
            throw new BizException("添加订单日志失败");
        return ordLog;
    }
    @Override
    public void payNotifyCust(Order order){
        logger.info("payNotifyUser " + order.getId());
        Merchant mch = mchDao.getMch(order.getMchId());
        if(mch==null)
            throw new BizException("无效的订单商家");
        Customer cust = custDao.getCust(order.getCustomerId());
        if(cust==null)
            throw new BizException("无效的买家");
        //
        TplPaySuccMsg msg = new TplPaySuccMsg();
        msg.setTemplate_id("vp7CruHxfICNhUIeMwCdy7KKqFMPFIj8pa3JoIVMipo");
        msg.setFirst("我们已经收到您的货款，正在等待商家接单，请耐心等候 ", "#ff7767");
        msg.setRemark("如有问题，请致电" + mch.getName() + "，" + mch.getPhone(), "#ff7767");
        msg.setOrderMoneySum(String.format("%.2f元", order.getTotal()), "#000000");
        msg.setOrderProductName(order.getCode() + " " + order.getBody(), "#000000");
        msg.setUrl(conf.getStr("smclxdaddr") + "index.html?orderid=" + order.getId() + "#c_orddet");
        String token = conf.getStr("wxaccesstoken");
        String wxaddr = conf.getStr("wxaddr");
        msg.setTouser(cust.getOpenid());
        msg.setTopcolor("#DDDDDD");
        weiXin.sendTplMsg(wxaddr, token, msg);
    }
    @Override
    public void payNotifyMch(Order order){
        logger.info("payNotifyMch " + order.getId());
        Merchant mch = mchDao.getMch(order.getMchId());
        if(mch==null)
            throw new BizException("无效的订单商家");
        Customer cust = custDao.getCust(order.getCustomerId());
        if(cust==null)
            throw new BizException("无效的买家");
        OrderLog log = ordLogDao.get(order.getId(), OrdStatus.W2PAY);
        CustAddr addr = custAddrDao.getById(order.getCustAddr());
        List<OrderItem> list = ordItemDao.getItemList(order.getId());
        Iterator<OrderItem> oiIt = list.iterator();
        String gname = "";
        while(oiIt.hasNext()){
            OrderItem oi = oiIt.next();
            if(gname.equals("")){
                gname = gname + oi.getName();
            }else{
                gname = gname + "、"+ oi.getName();
            }
        }
        //
        TplNewOrdrMsg msg = new TplNewOrdrMsg();
        msg.setTemplate_id("DaFMumRX8rOWRFS6i-SQKBLEuvWYDK3onsFH2Zl66mY");
        msg.setFirst("您有一条新的订单，配送时间:" + order.getArriveTime(), "#ff7767");
        msg.setRemark("请及时接单!", "#000000");
        msg.setTradeDateTime(log.getTime(), "#000000");
        msg.setOrderType("订单号" + order.getCode(), "#000000");
        msg.setCustomerInfo(addr.getNotifier() + " " + addr.getPhone(), "#000000");
        msg.setOrderItemName("商品名称", "#000000");
        msg.setOrderItemData(gname, "#000000");
        msg.setUrl(conf.getStr("smclxdaddr") + "biz.html?orderid=" + order.getId() + "#b_orddet");
        String token = conf.getStr("wxaccesstoken");
        String wxaddr = conf.getStr("wxaddr");
        Iterator<MchUser> it = mchUserServ.listByMch(mch.getId(), null).iterator();
        while(it.hasNext()){
            MchUser m = it.next();
            msg.setTouser(m.getOpenid());
            msg.setTopcolor("#DDDDDD");
            weiXin.sendTplMsg(wxaddr, token, msg);
        }
    }
    @Override
    @Transactional
    public void pay(long orderId, long userId){
        logger.info("pay " + orderId + " " + userId);
        Order order = orderDao.get(orderId);
        if(!order.getStatus().equals(OrdStatus.W2PAY))
            throw new BizException("订单不在待支付状态，无法支付");
        chngStatus(orderId, OrdStatus.W2PAY, OrdStatus.W2ACCEPT, OrdStatus.A2PAY, userId, null);
        if(orderDao.setPayed(orderId)!=1)
            throw new BizException("标记订单已支付失败");
        payNotifyMch(order);
        payNotifyCust(order);
    }
    @Override
    @Transactional
    public void cancel(long orderId, long userId){
        logger.info("cancel " + orderId + " " + userId);
        Order order = orderDao.get(orderId);
        if(!order.getStatus().equals(OrdStatus.W2PAY))
            throw new BizException("订单不在待支付状态，无法取消");
        OrderLog log = chngStatus(orderId, OrdStatus.W2PAY, OrdStatus.CANCEL,OrdStatus.A2CANCEL, userId, null);
        //
        //		Merchant mch = mchDao.getMch(order.getMchId());
        //		Customer cust = custDao.getCust(order.getCustomerId());
        //		TplOrdrStChngMsg msg = new TplOrdrStChngMsg();
        //		msg.setTemplate_id("qU4RccH0rM-c70oTengsig5k6Ucz4B9DsESNanoi-iI");
        //		msg.setFirst("您的订单已取消", "#ff7767");
        //		msg.setKeyword1(order.getCode() + " " + order.getBody(), "#000000");
        //		msg.setKeyword2(log.getTime(), "#000000");
        //		msg.setRemark("如有问题，请致电" + mch.getName() + "，" + mch.getPhone(), "#ff7767");
        //		msg.setUrl(conf.getStr("smclxdaddr") + "index.html?orderid=" + order.getId() + "#c_orddet");
        //		String token = conf.getStr("wxaccesstoken");
        //		String wxaddr = conf.getStr("wxaddr");
        //		msg.setTouser(cust.getOpenid());
        //		msg.setTopcolor("#DDDDDD");
        //		weiXin.sendTplMsg(wxaddr, token, msg);
    }
    @Override
    @Transactional
    public void accept(long orderId, long userId){
        logger.info("accept " + orderId + " " + userId);
        Order order = orderDao.get(orderId);
        MchUser user = mchUserDao.get(userId);
        if(!order.getStatus().equals(OrdStatus.W2ACCEPT))
            throw new BizException("订单不在待接单状态");
        OrderLog log = chngStatus(orderId, OrdStatus.W2ACCEPT, OrdStatus.W2DISPATCH,OrdStatus.A2ACCEPT, userId, user.getPhone());
        //
        List<OrderItem> oiList = ordItemDao.getItemList(orderId);
        Iterator<OrderItem> it = oiList.iterator();
        while(it.hasNext()){
            OrderItem ordItem = it.next();
            Goods gods = goodsDao.get(ordItem.getGoodsId());
            if(gods.getInventory() == 9999)
                continue;
            if(gods.getInventory() < ordItem.getGoodsNum())
                throw new BizException(gods.getName() + "库存不足");
            if(goodsDao.reduceInvt(gods.getId(), ordItem.getGoodsNum(), gods.getInventory())!=1)
                throw new BizException(gods.getName() + "库存更新失败");
            if(gods.getInventory() == ordItem.getGoodsNum()){
                gods.setStatus(false);
                gods.setInventory(0);
                if(goodsDao.update(gods)!=1)
                    throw new BizException(gods.getName() + "库存更新失败");
                //
                MchUser m = mchUserDao.primary(order.getMchId());
                TplGoodsStChngMsg msggod = new TplGoodsStChngMsg();
                msggod.setTemplate_id("DQwGmKikA92OdyCWKYWusoT_An_4jcBtoQ2iuCyDjEE");
                msggod.setFirst("尊敬的商家，您的店铺有商品已经下架。", "#ff7767");
                msggod.setKeyword1(gods.getName(), "#000000");
                msggod.setKeyword2(sdf.format(new Date()), "#000000");
                msggod.setKeyword3("商品已售完，库存量不足", "#000000");
                msggod.setRemark("如需继续销售，请编辑上架！", "#ff7767");
                msggod.setUrl(conf.getStr("smclxdaddr") + "biz.html?goodsid=" + gods.getId() + "#b_agd");
                String token = conf.getStr("wxaccesstoken");
                String wxaddr = conf.getStr("wxaddr");
                msggod.setTouser(m.getOpenid());
                msggod.setTopcolor("#DDDDDD");
                weiXin.sendTplMsg(wxaddr, token, msggod);
            }

        }
        //
        Merchant mch = mchDao.getMch(order.getMchId());
        Customer cust = custDao.getCust(order.getCustomerId());
        TplOrdrStChngMsg msg = new TplOrdrStChngMsg();
        msg.setTemplate_id("qU4RccH0rM-c70oTengsig5k6Ucz4B9DsESNanoi-iI");
        msg.setFirst("您的订单商家已经接单，请等待商家处理！", "#ff7767");
        msg.setKeyword1(mch.getName(), "#000000");
        msg.setKeyword2(order.getCode(), "#000000");
        msg.setKeyword3(String.format("%.2f元", order.getTotal()), "#000000");
        msg.setKeyword4(OrdStatus.W2DISPATCH, "#000000");
        msg.setRemark("如有问题，请致电" + mch.getName() + "，" + mch.getPhone(), "#ff7767");
        msg.setUrl(conf.getStr("smclxdaddr") + "index.html?orderid=" + order.getId() + "#c_orddet");
        String token = conf.getStr("wxaccesstoken");
        String wxaddr = conf.getStr("wxaddr");
        msg.setTouser(cust.getOpenid());
        msg.setTopcolor("#DDDDDD");
        weiXin.sendTplMsg(wxaddr, token, msg);
    }
    @Override
    @Transactional
    public void reject(long orderId, long userId){
        logger.info("reject " + orderId + " " + userId);
        Order order = orderDao.get(orderId);
        if(!order.getStatus().equals(OrdStatus.W2ACCEPT))
            throw new BizException("订单不在待接单状态");
        MchUser user = mchUserDao.get(userId);
        OrderLog log = chngStatus(orderId, OrdStatus.W2ACCEPT, OrdStatus.REJECT,OrdStatus.A2REJECT, userId, user.getPhone());
        //
        Merchant mch = mchDao.getMch(order.getMchId());
        Customer cust = custDao.getCust(order.getCustomerId());
        TplOrdrStChngMsg msg = new TplOrdrStChngMsg();
        msg.setTemplate_id("qU4RccH0rM-c70oTengsig5k6Ucz4B9DsESNanoi-iI");
        msg.setFirst("您的订单已被商家拒单！订单金额将退回至您的账户，请注意查收！", "#ff7767");
        msg.setKeyword1(mch.getName(), "#000000");
        msg.setKeyword2(order.getCode(), "#000000");
        msg.setKeyword3(String.format("%.2f元", order.getTotal()), "#000000");
        msg.setKeyword4(OrdStatus.REJECT, "#000000");
        msg.setRemark("如有问题，请致电" + mch.getName() + "，" + mch.getPhone(), "#ff7767");
        msg.setUrl(conf.getStr("smclxdaddr") + "index.html?orderid=" + order.getId() + "#c_orddet");
        String token = conf.getStr("wxaccesstoken");
        String wxaddr = conf.getStr("wxaddr");
        msg.setTouser(cust.getOpenid());
        msg.setTopcolor("#DDDDDD");
        logger.debug(JaxbUtil.bean2Json(msg));
        weiXin.sendTplMsg(wxaddr, token, msg);
    }
    @Override
    @Transactional
    public void dispatch(long orderId, long userId){
        logger.info("dispatch " + orderId + " " + userId);
        Order order = orderDao.get(orderId);
        if(!order.getStatus().equals(OrdStatus.W2DISPATCH))
            throw new BizException("订单不在待处理状态");
        MchUser user = mchUserDao.get(userId);
        OrderLog log = chngStatus(orderId, OrdStatus.W2DISPATCH, OrdStatus.W2CONFIRM,OrdStatus.A2DISPATCH, userId, user.getPhone());
        //
        Merchant mch = mchDao.getMch(order.getMchId());
        if(user.getMchId()!=mch.getId()){
            throw new BizException("该订单不属于本店,不可操作");
        }
        Customer cust = custDao.getCust(order.getCustomerId());
        TplOrdrStChngMsg msg = new TplOrdrStChngMsg();
        msg.setTemplate_id("qU4RccH0rM-c70oTengsig5k6Ucz4B9DsESNanoi-iI");
        msg.setFirst("您的订单已处理完成，请您尽快对订单进行确认！", "#ff7767");
        msg.setKeyword1(mch.getName(), "#000000");
        msg.setKeyword2(order.getCode(), "#000000");
        msg.setKeyword3(String.format("%.2f元", order.getTotal()), "#000000");
        msg.setKeyword4(OrdStatus.W2CONFIRM, "#000000");
        msg.setRemark("如有问题，请致电" + mch.getName() + "，" + mch.getPhone(), "#ff7767");
        msg.setUrl(conf.getStr("smclxdaddr") + "index.html?orderid=" + order.getId() + "#c_orddet");
        String token = conf.getStr("wxaccesstoken");
        String wxaddr = conf.getStr("wxaddr");
        msg.setTouser(cust.getOpenid());
        msg.setTopcolor("#DDDDDD");
        logger.debug(JaxbUtil.bean2Json(msg));
        weiXin.sendTplMsg(wxaddr, token, msg);
    }
    @Override
    @Transactional
    public void confirm(long orderId, long userId){
        logger.info("confirm " + orderId + " " + userId);
        Order order = orderDao.get(orderId);
        if(!order.getStatus().equals(OrdStatus.W2CONFIRM))
            throw new BizException("订单不在待确认状态");
        OrderLog log = chngStatus(orderId, OrdStatus.W2CONFIRM, OrdStatus.W2EVALUATE,OrdStatus.A2CONFIRM, userId, null);
        //
        Merchant mch = mchDao.getMch(order.getMchId());
        TplOrdrStChngMsg msg = new TplOrdrStChngMsg();
        msg.setTemplate_id("qU4RccH0rM-c70oTengsig5k6Ucz4B9DsESNanoi-iI");
        msg.setFirst("买家已经确认订单完成", "#ff7767");
        msg.setKeyword1(mch.getName(), "#000000");
        msg.setKeyword2(order.getCode(), "#000000");
        msg.setKeyword3(String.format("%.2f元", order.getTotal()), "#000000");
        msg.setKeyword4(OrdStatus.DONE, "#000000");
        msg.setRemark("", "#ff7767");
        msg.setUrl(conf.getStr("smclxdaddr") + "biz.html?orderid=" + order.getId() + "#b_orddet");
        String token = conf.getStr("wxaccesstoken");
        String wxaddr = conf.getStr("wxaddr");
        msg.setTopcolor("#DDDDDD");
        Iterator<MchUser> it = mchUserServ.listByMch(mch.getId(), null).iterator();
        while(it.hasNext()){
            MchUser m = it.next();
            msg.setTouser(m.getOpenid());
            weiXin.sendTplMsg(wxaddr, token, msg);
        }
    }
    /**
     * 检查最近1小时内的被拒订单，进行退款操作
     */
    @Override
    public void autoRefund(){
        logger.info("autoRefund");
        Date now = new Date();
        Date before10m = new Date(now.getTime() - 10 * 60 * 1000);
        Iterator<OrderLog> logs = ordLogDao.listByTimeAndLog(before10m, OrdStatus.REJECT).iterator();
        while(logs.hasNext()){
            OrderLog log = logs.next();
            //			Order order = orderDao.get(log.getOrid());
            //			Merchant mch = mchDao.getMch(order.getMchId());
            PayOrdr po = payOrdrDao.getByOrdr(log.getOrid(), "REFULWX");
            if(po==null){
                try{
                    po = payServ.refundUlWx(log.getOrid());
                }catch(Exception e){
                    logger.error(e.getMessage());
                    po = new PayOrdr();
                    po.setOrdrId(log.getOrid());
                    po.setType("REFULWX");
                    po.setData(e.getMessage());
                    po.setPayed(false);
                    payOrdrDao.insert(po);
                    //
                    String token = conf.getStr("wxaccesstoken");
                    String wxaddr = conf.getStr("wxaddr");
                    TplOrdrStChngMsg msg = new TplOrdrStChngMsg();
                    msg.setTemplate_id("d7mj6HqU5Ssc1F9JVfV8WHqtuznm9LfYBsbwNZbqSM4");
                    //msg.setTemplate_id("qU4RccH0rM-c70oTengsig5k6Ucz4B9DsESNanoi-iI");
                    msg.setFirst("商家拒单后自动退款失败", "#ff7767");
                    msg.setKeyword1("订单ID:" + log.getOrid() + " " + e.getMessage(), "#000000");
                    msg.setKeyword2(sdf.format(new Date()), "#000000");
                    //					msg.setKeyword1(mch.getName(), "#000000");
                    //					msg.setKeyword2(order.getCode(), "#000000");
                    //					msg.setKeyword3(String.format("%.2f元", order.getTotal()), "#000000");
                    //					msg.setKeyword4(OrdStatus.REJECT, "#000000");
                    msg.setRemark("", "#ff7767");
                    msg.setTopcolor("#DDDDDD");
                    Iterator<SmcUser> it = smcUserServ.list().iterator();
                    while(it.hasNext()){
                        SmcUser u = it.next();
                        if(u.getOpenid()!=null){
                            logger.info("send to " + u.getOpenid());
                            msg.setTouser(u.getOpenid());
                            msg.setTopcolor("#DDDDDD");
                            weiXin.sendTplMsg(wxaddr, token, msg);
                        }
                    }
                }
            }
        }
    }
    @Override
    @Transactional
    public void autoCancel(){
        logger.info("autoCancel()");
        Calendar timeout = Calendar.getInstance();
        timeout.add(Calendar.MINUTE, -20);
        Iterator<Order> orders = orderDao.listByStatus(OrdStatus.W2PAY).iterator();
        while(orders.hasNext()){
            Order order = orders.next();
            OrderLog orderLog = ordLogDao.get(order.getId(), OrdStatus.W2PAY);
            Calendar dealTime = Calendar.getInstance();
            try {
                dealTime.setTime(sdf.parse(orderLog.getTime()));
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if(dealTime.after(timeout)) continue;
            //
            OrderLog log = chngStatus(order.getId(), OrdStatus.W2PAY, OrdStatus.CANCEL, OrdStatus.A2CANCEL, 0, null);
            //
            Merchant mch = mchDao.getMch(order.getMchId());
            Customer cust = custDao.getCust(order.getCustomerId());
            TplOrdrStChngMsg msg = new TplOrdrStChngMsg();
            msg.setTemplate_id("qU4RccH0rM-c70oTengsig5k6Ucz4B9DsESNanoi-iI");
            msg.setFirst("您的订单已取消", "#ff7767");
            msg.setKeyword1(mch.getName(), "#000000");
            msg.setKeyword2(order.getCode(), "#000000");
            msg.setKeyword3(String.format("%.2f元", order.getTotal()), "#000000");
            msg.setKeyword4(OrdStatus.CANCEL, "#000000");
            msg.setRemark("", "#ff7767");
            msg.setUrl(conf.getStr("smclxdaddr") + "index.html?orderid=" + order.getId() + "#c_orddet");
            String token = conf.getStr("wxaccesstoken");
            String wxaddr = conf.getStr("wxaddr");
            msg.setTouser(cust.getOpenid());
            msg.setTopcolor("#DDDDDD");
            weiXin.sendTplMsg(wxaddr, token, msg);
        }
    }
    /**
     * 待确认订单在8小时后自动确认，并给交易双方发送消息
     */
    @Override
    @Transactional
    public void autoConfirm(){
        logger.info("autoConfirm");
        Calendar timeout = Calendar.getInstance();
        timeout.add(Calendar.HOUR, -8);
        Iterator<Order> orders = orderDao.listByStatus(OrdStatus.W2CONFIRM).iterator();
        while(orders.hasNext()){
            Order order = orders.next();
            OrderLog orderLog = ordLogDao.get(order.getId(), OrdStatus.W2CONFIRM);
            Calendar dealTime = Calendar.getInstance();
            try {
                dealTime.setTime(sdf.parse(orderLog.getTime()));
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if(dealTime.after(timeout)) continue;
            //
            OrderLog log = chngStatus(order.getId(), OrdStatus.W2CONFIRM, OrdStatus.W2EVALUATE,OrdStatus.A2CONFIRM, 0, null);
            //
            Merchant mch = mchDao.getMch(order.getMchId());
            Customer cust = custDao.getCust(order.getCustomerId());
            String token = conf.getStr("wxaccesstoken");
            String wxaddr = conf.getStr("wxaddr");
            //
            TplOrdrStChngMsg msg = new TplOrdrStChngMsg();
            msg.setTemplate_id("qU4RccH0rM-c70oTengsig5k6Ucz4B9DsESNanoi-iI");
            msg.setFirst("订单自动确认完成", "#ff7767");
            msg.setKeyword1(mch.getName(), "#000000");
            msg.setKeyword2(order.getCode(), "#000000");
            msg.setKeyword3(String.format("%.2f元", order.getTotal()), "#000000");
            msg.setKeyword4(OrdStatus.DONE, "#000000");
            msg.setRemark("", "#ff7767");
            msg.setUrl(conf.getStr("smclxdaddr") + "biz.html?orderid=" + order.getId() + "#b_orddet");
            msg.setTopcolor("#DDDDDD");
            Iterator<MchUser> it = mchUserServ.listByMch(mch.getId(), null).iterator();
            while(it.hasNext()){
                MchUser m = it.next();
                msg.setTouser(m.getOpenid());
                weiXin.sendTplMsg(wxaddr, token, msg);
            }
            //
            msg.setFirst("订单自动确认完成，欢迎再次光临", "#ff7767");
            msg.setKeyword4(OrdStatus.W2EVALUATE, "#000000");
            msg.setUrl(conf.getStr("smclxdaddr") + "index.html?orderid=" + order.getId() + "#c_orddet");
            msg.setTouser(cust.getOpenid());
            weiXin.sendTplMsg(wxaddr, token, msg);
        }
    }
    @Override
    public List<OrderOut> ordOut(OrdLstBody lstBody) {
        logger.info(String.format("OrdOut(%s)", JaxbUtil.bean2Json(lstBody)));
        lstBody.setPageNum((lstBody.getPageNum() - 1)*lstBody.getTotal());
        String codes = "(";
        if(!lstBody.getCity().equals("0")){
            String st[] = lstBody.getCity().split(",");
            for(String s:st){
                if(codes.equals("(")){
                    codes = codes + "'" +s+"'";
                }else{
                    codes = codes + ", '"+ s+"'";
                }
            }
            codes = codes + ")";
            lstBody.setCity(codes);
        }
        List<OrderOut> list = orderDao.ordOut(lstBody);
        Iterator<OrderOut> it = list.iterator();
        while(it.hasNext()){
            OrderOut oo = it.next();
            OrderLog ol = ordLogDao.get(oo.getId(), OrdStatus.W2ACCEPT);
            if(ol==null)
                oo.setPayTime("订单未付款");
            else
                oo.setPayTime(ol.getTime());
            List<OrderItem> oilist = ordItemDao.getItemList(oo.getId());
            Iterator<OrderItem> oiit = oilist.iterator();
            String goodsName = "";
            int goodsNum = 0;
            while(oiit.hasNext()){
                OrderItem oi = oiit.next();
                if(goodsName.equals("")){
                    goodsName = goodsName + oi.getName();
                }else{
                    goodsName = goodsName + "， " + oi.getName();
                }
                goodsNum = goodsNum + oi.getGoodsNum();
            }
            oo.setGoodsName(goodsName);
            oo.setGoodsNum(goodsNum);
        }
        return list;
    }
    /**B端订单管理数量提示*/
    @Override
    public Integer mchWaitNum(long mchId) {
        return orderDao.waitNum(mchId, OrdStatus.W2ACCEPT, null, null);
    }
}
