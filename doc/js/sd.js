//日期修改
Date.prototype.addDay = function(d){
    return (new Date(this.valueOf() + d * 86400000));
};
// 对Date的扩展，将 Date 转化为指定格式的String
// 月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符，
// 年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字)
// 例子：
// (new Date()).Format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423
// (new Date()).Format("yyyy-M-d h:m:s.S")      ==> 2006-7-2 8:9:4.18
Date.prototype.Format = function (fmt) { //author: meizz
    var wd = ['日', '一', '二', '三', '四', '五', '六'];
    var o = {
        "M+": this.getMonth() + 1, //月份
        "d+": this.getDate(), //日
        "h+": this.getHours(), //小时
        "m+": this.getMinutes(), //分
        "s+": this.getSeconds(), //秒
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度
        "S": this.getMilliseconds() //毫秒
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    if(/(D)/.test(fmt)) fmt = fmt.replace(RegExp.$1, wd[this.getDay()]);
    for (var k in o) {
        if(new RegExp("(" + k + ")").test(fmt))
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    }
    return fmt;
};
Date.prototype.FormatWeek = function (day) {
    var wd = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
    var week = wd[day.getDay()];
    return week;
};
Date.prototype.FormatWeek2 = function (day) {
    var wd = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'];
    var week = wd[day.getDay()];
    return week;
};
Date.prototype.FormatWeek3 = function (day) {
    var wd = ['星期日Sunday', '星期一Monday', '星期二Tuesday', '星期三Wednesday', '星期四Thursday', '星期五Friday', '星期六Saturday'];
    var week = wd[day.getDay()];
    return week;
};
/* 滚动事件 */
(function() {
	var special = jQuery.event.special, uid1 = 'D' + (+new Date()), uid2 = 'D'
			+ (+new Date() + 1);
	special.scrollstart = {
		setup : function() {
			var timer, handler = function(evt) {
				var _self = this, _args = arguments;
				if (timer) {
					clearTimeout(timer);
				} else {
					evt.type = 'scrollstart';
					jQuery.event.handle.apply(_self, _args);
				}
				timer = setTimeout(function() {
					timer = null;
				}, special.scrollstop.latency);
			};
			jQuery(this).bind('scroll', handler).data(uid1, handler);
		},
		teardown : function() {
			jQuery(this).unbind('scroll', jQuery(this).data(uid1));
		}
	};
	special.scrollstop = {
		latency : 300,
		setup : function() {
			var timer, handler = function(evt) {
				var _self = this, _args = arguments;
				if (timer) {
					clearTimeout(timer);
				}
				timer = setTimeout(function() {
					timer = null;
					evt.type = 'scrollstop';
					jQuery.event.handle.apply(_self, _args);
				}, special.scrollstop.latency);
			};
			jQuery(this).bind('scroll', handler).data(uid2, handler);
		},
		teardown : function() {
			jQuery(this).unbind('scroll', jQuery(this).data(uid2));
		}
	};
})();
if(!window.sessionStorage){
    alert('浏览器..不..支持sessionStorage，请换一个试试！');
}
$(document).bind("contextmenu",function(e){
	console.log(e.target.tagName);
	if(e.target.tagName=='INPUT' || e.target.tagName=='TEXTAREA') return true;  //Event
	else return false;
});

SD = {};
SD.ws = '#ws';
SD.loadingEl = '#loadingToast';

SD.genSID = function(){
    return (new Date()).valueOf();
};

//Hash page router engine
$(window).on('hashchange', function() {
    SD.ldPg(window.location.hash);
});

SD.go = function(hash, force){
    //console.log('go ' + hash);
    if(SD.beforeGo) SD.beforeGo();
    if(window.location.hash != hash) window.location.hash = hash;
    else if(force) SD.ldPg(hash);
};

//Load Page, Dialog
SD.ldPg = function(hash, isDialog){
    //console.log('hash=' + hash);
    $(SD.loadingEl).css('display', '');
    hash = hash.replace('#', '');
    hash = hash.replace('_', '/');
    //console.log('hash=' + hash);
    //load js
    var clsid = hash.replace('/', '');
    if($('#js' + clsid).length <= 0){
        $('body').append('<script id="js' + clsid + '" type="text/javascript" src="js/' + hash + '.js"></script>');
    }
    if($('#css' + clsid).length <= 0){
        $('head').append('<link id="css' + clsid + '" rel="stylesheet" type="text/css" href="css/' + hash + '.css">');
    }
    //load page
    var target = $(SD.ws);
    if(isDialog){
        target = $('<div class="dialog"></div>').appendTo('body');
    }
    target.load('html/' + hash + '.html', function(){
        $(SD.loadingEl).hide();
        var elInif = target.find('.sdpg');
        var inif = '';
        if(elInif.length > 0) inif = elInif.data('inif');
        if(inif && inif.length > 0){
            //console.log(inif);
            var fns = inif.split('.');
            var l = fns.length;
            var f = window;
            for(var i=0; i<l; i++){
                //console.log('fn' + i + '=' + fns[i]);
                f = f[fns[i]];
            }
            f();
        }
    });
};

SD.toast = function(s, t){
    var to = 2000;
    if(t) to = t;
    var r = SD.genSID();
    var ss = '<div class="ehtoast ehtoast_' + r + '" style="display:none;position:fixed;left:0;right:0;bottom:0;height:150px;text-align:center;"><div style="display:inline-block;padding:12px 12px;border-radius:3px;background-color:#555;color:white;font-size:11x;">' + s + '</div></div>';
    $(ss).appendTo('body').show();
    window.setTimeout(function(){
        $('.ehtoast_' + r).remove();
    }, to);
};

SD.getJSON = function(url, fsucc, ffail){
    $(SD.loadingEl).css('display', '');
    $.getJSON(url, function(jo){
        $(SD.loadingEl).hide();
        if(jo.success){
            if(fsucc) fsucc(jo);
        }else{
            if(ffail){
                ffail(jo);
            }else{
                SD.toast(jo.desc);
                if(jo.desc.match(/未登录/) && SD.loginFunc) SD.loginFunc();
            }
        }
    });
};

SD.postJSON = function(url, data, fsucc, ffail){
    $(SD.loadingEl).css('display', '');
    $.ajax({
        type: 'POST',
        url: url,
        data: JSON.stringify(data),
        dataType: 'json',
        contentType: 'application/json',
        success: function(jo){
            if(jo.success){
                if(fsucc) fsucc(jo);
            }else{
                if(ffail){
                    ffail(jo);
                }else{
                    SD.toast(jo.desc);
                    if(jo.desc.match(/未登录/) && SD.loginFunc) SD.loginFunc();
                }
            }
        },
        complete: function(){
            $(SD.loadingEl).hide();
        }
    });
};

SD.setPara = function(k, v){
    localStorage.setItem('sdpp_' + k, v);
};

SD.popPara = function(k, def){
    var r = localStorage.getItem('sdpp_' + k);
    localStorage.removeItem('sdpp_' + k);
    if(r===null && typeof(def)!='undefined') return def;
    return r;
};

SD.getPara = function(k, def){
    var r = localStorage.getItem('sdpp_' + k);
    if(r===null && typeof(def)!='undefined') return def;
    return r;
};

SD.inlineLoading = function(e){
    var s =
        '<div class="eh_inline_loading">' +
        '<div class="eh_ilp eh_ilp1"></div>' +
        '<div class="eh_ilp eh_ilp2"></div>' +
        '<div class="eh_ilp eh_ilp3"></div>' +
        '<div class="eh_ilp eh_ilp4"></div>' +
        '<div class="eh_ilp eh_ilp5"></div>' +
        '</div>';
    $(e).html(s);
};

SD.fillJson = function(jo, el){
    $.each(jo, function(k, v){
        //console.log('key=' + k + ', value=' + v);
        var cls = el + ' .edt' + k;
        if($(cls).attr('type')=='checkbox'){
            //console.log($(cls).prop("checked"));
            jo[k] = $(cls).prop("checked");
        }else{
            var kv = $(cls).val();
            //console.log(kv);
            if(kv && kv !== '') jo[k] = kv;
        }
    });
};

SD.isWeixin = function(){
    var ua = navigator.userAgent.toLowerCase();
    return ua.match(/MicroMessenger/i)=="micromessenger";
};

SD.getTomapp = function(){
    var r = window.location.pathname;
    var idx = r.lastIndexOf('/');
    return window.location.protocol + '//' + window.location.host + r.substr(0, idx);
};

SD.openAS = function(asName){
    was = $('.was' + asName);
    mask = $('.msk' + asName);
    cncl = $('.cncl' + asName);
    was.addClass('weui_actionsheet_toggle');
    mask.show().addClass('weui_fade_toggle').one('click', function(){
        hideAS(was, mask);
    });
    cncl.one('click', function () {
        hideAS(was, mask);
    });
    was.unbind('transitionend').unbind('webkitTransitionEnd');
    function hideAS(was, mask) {
        was.removeClass('weui_actionsheet_toggle');
        mask.removeClass('weui_fade_toggle');
        was.on('transitionend', function () {
            mask.hide();
        }).on('webkitTransitionEnd', function () {
            mask.hide();
        });
    }
};

SD.setPgHint = function(msg){
    SD.setPara('pgHint', msg);
};

SD.popPgHint = function(){
    var msg = SD.popPara('pgHint');
    if(msg) SD.toast(msg);
};

SD.qs2Para = function(){
    var qs = window.location.search.substr(1).split('&');
    if(qs !== ''){
        for(var i=0; i<qs.length; i++){
            ov = qs[i].split('=', 2);
            if(ov.length == 2) SD.setPara(ov[0], decodeURIComponent(ov[1].replace(/\+/g, " ")));
        }
    }
};
//判断提示对话框
SD.confirme = function(t, s, ff, good, cann, cf) {
	var str = '<div class="confirme">' + '<div class="confirmeWin">'
			+ '	<div class="tit">' + t + '</div>'
			+ '	<div class="contents">' + s + '</div>'
			+ '	<div class="bottom">'
			+ '		<div class="cancel"><div class="cann">'
			+ (cann ? cann : '取消') + '</div></div>'
			+ '		<div class="good">' + (good ? good : '确定') + '</div>'
			+ '	</div>' + '</div>' + '<div class="confirmeBg"></div>'
			+ '</div>';
	$('body').append(str);
	// 取消
	$('.cancel').on("click", function() {
		$('.confirme').remove();
		if (cf)
			cf();
	});
	// 好
	$('.good').on("click", function() {
		$('.confirme').remove();
		if (ff)
			ff();
	});
	$('.confirmeBg').click(function() {
		$('.confirme').remove();
	});
};
