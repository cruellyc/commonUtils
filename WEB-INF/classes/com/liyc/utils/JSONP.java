package com.liyc.utils;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.util.JSONPObject;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 将请求结果封装成JSONP，绝大多数的请求都应该返回这个类，达到跨域访问的目的。
 * 如果没有指定回调函数名，使用defaultCallback。
 * @author .
 * @param <T>
 */
public class JSONP<T> extends JSONPObject {
	private Logger logger = Logger.getLogger(this.getClass());
	private static String defaultCallback = "foo";

	public JSONP(String function, String msg){
		super(function==null?defaultCallback:function, new RespMsg<T>(msg));
	}

	public JSONP(String function, String msg, boolean success){
		super(function==null?defaultCallback:function, new RespMsg<T>(msg, success));
	}
	public JSONP(String fn, Object msg){
		super(fn==null?defaultCallback:fn, msg);
	}
	public void setCont(T cont){
		@SuppressWarnings("unchecked")
		RespMsg<T> msg = (RespMsg<T>) super.getValue();
		msg.setCont(cont);
	}

	public void setMsg(String desc){
		@SuppressWarnings("unchecked")
		RespMsg<T> msg = (RespMsg<T>) super.getValue();
		msg.setDesc(desc);
	}

	public void setSuccess(boolean success){
		@SuppressWarnings("unchecked")
		RespMsg<T> msg = (RespMsg<T>) super.getValue();
		msg.setSuccess(success);
	}

	@SuppressWarnings("unchecked")
	public void setCode(int code) {
		RespMsg<T> msg = (RespMsg<T>) super.getValue();
		msg.setCode(code);
	}
	public boolean chkEtag(HttpServletRequest request, HttpServletResponse response,String obj){
		String etag = request.getHeader("If-None-Match");
		logger.debug("fn=" + etag);
		String t=MD5Util.Md5(obj.toString());
		logger.debug("lst="+obj.toString());
		logger.debug("md5="+t);
		if(t.equals(etag)){
			logger.debug("return 304");
			response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
			return false;
		}else{
			response.setHeader("ETag", t);
			return true;
		}
	}
	public void chkEtags(HttpServletRequest request, HttpServletResponse response,T t){
		if(t!=null){
			JSONObject obj=(JSONObject) JSONObject.toJSON(t);
			logger.debug(obj);
			String etag = request.getHeader("If-None-Match");
			logger.debug("fn=" + etag);
			String m=MD5Util.Md5(obj.toString());
			logger.debug("lst="+obj.toString());
			logger.debug("md5="+m);
			if(m.equals(etag)){
				logger.debug("return 304");
				response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
			}else{
				this.setCont(t);
				response.setHeader("ETag", m);
			}
		}
	}
	public void chkEtagLists(HttpServletRequest request, HttpServletResponse response,T t){
		if(t!=null){
			JSONArray obj=(JSONArray) JSONArray.toJSON(t);
			logger.debug(obj);
			String etag = request.getHeader("If-None-Match");
			logger.debug("fn=" + etag);
			String m=MD5Util.Md5(obj.toString());
			logger.debug("lst="+obj.toString());
			logger.debug("md5="+m);
			if(m.equals(etag)){
				logger.debug("return 304");
				response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
			}else{
				this.setCont(t);
				response.setHeader("ETag", m);
			}
		}
	}
}
