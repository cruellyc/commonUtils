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
/**时间控件*/
SD_DATE={};
SD_DATE.dateLoading = function(e,h,m,f){
	var s =
		'<div class="backBg">' +
		'		<div class="date_choicetime">' +
		'		<div class="date_titRow"><span class="float_left" id="bgcancle">取消</span><span class="float_right" id="bgok">完成</span></div>' +
		'		<div class="date_timeRow">' +
		'			<div class="date_row"><div class="float_left date_hour" id="date_hourSelect">' +
		'			<div></div>';
		for(var i=0;i<=h;i++){
			s+='			<div id="h'+i+'"><span>'+(i<9?('0'+i):i)+'</span></div>';
		}
		s+='			<div></div>' +
		'			</div><div class="float_right date_minutes" id="date_minuteSelect">' +
		'			<div></div>';
		
		for(var i=0;i<=m;i++){
			s+='			<div id="m'+i+'"><span>'+(i<9?('0'+i):i)+'</span></div>';
		}
		s+='			<div></div>' +
		'			</div></div>' +
		'		</div>' +
		'	</div>' +
		'	<div class="date_line1"></div>' +
		'	<div class="date_line2"></div>' +
		'</div>';
	$(e).html(s);
	
	$('#bgcancle').click(function(){
		$(e).html('');
	});
	$('#bgok').click(function(){
		var h=$('#date_hourSelect').find('.date_select span').text();
		var m=$('#date_minuteSelect').find('.date_select span').text();
		$(e).html('');
		if(f) f(h,m);
	});
	SD_DATE.setDate();
};
SD_DATE.setDate=function(){
	$('#date_hourSelect').bind({
		'touchend touchcancel mouseup scrollstop' : function(){
			var x=$(this).scrollTop()/30;
			if(($(this).scrollTop()%30) != 0){
				if($(this).scrollTop()%30>15){
					console.log("##"+($(this).scrollTop()+(30-$(this).scrollTop()%30)));
					x=($(this).scrollTop()+(30-$(this).scrollTop()%30))/30;
					SD_DATE.setClass('#date_hourSelect',($(this).scrollTop()+(30-$(this).scrollTop()%30)),"h"+x);
				}else{
					console.log("**"+($(this).scrollTop()-($(this).scrollTop()%30)));
					x=($(this).scrollTop()-($(this).scrollTop()%30))/30;
					SD_DATE.setClass('#date_hourSelect',($(this).scrollTop()-($(this).scrollTop()%30)),"h"+x);
				}
				SD_DATE.setnoClass('#date_hourSelect',"h",x);
			}else{
				$('#date_hourSelect').children().removeClass('date_select');
				$('#date_hourSelect').find('#h'+x).addClass('date_select');
				SD_DATE.setnoClass('#date_hourSelect',"h",x);
			}
		}
	});
	$('#date_minuteSelect').bind({
		'touchend touchcancel mouseup scrollstop' : function(){
			var x=$(this).scrollTop()/30;
			if(($(this).scrollTop()%30) != 0){
				if($(this).scrollTop()%30>15){
					console.log("##"+($(this).scrollTop()+(30-$(this).scrollTop()%30)));
					x=($(this).scrollTop()+(30-$(this).scrollTop()%30))/30;
					SD_DATE.setClass('#date_minuteSelect',($(this).scrollTop()+(30-$(this).scrollTop()%30)),"m"+x);
				}else{
					console.log("**"+($(this).scrollTop()-($(this).scrollTop()%30)));
					x=($(this).scrollTop()-($(this).scrollTop()%30))/30;
					SD_DATE.setClass('#date_minuteSelect',($(this).scrollTop()-($(this).scrollTop()%30)),"m"+x);
				}
				SD_DATE.setnoClass('#date_minuteSelect',"m",x);
			}else{
				$('#date_minuteSelect').children().removeClass('date_select');
				$('#date_minuteSelect').find('#m'+x).addClass('date_select');
				SD_DATE.setnoClass('#date_minuteSelect',"m",x);
			}
		}
	});
	SD_DATE.setClass('#date_minuteSelect',0,"m0");
	SD_DATE.setClass('#date_hourSelect',0,"h0");
	SD_DATE.setnoClass('#date_minuteSelect',"m",0);
	SD_DATE.setnoClass('#date_hourSelect',"h",0);
}
SD_DATE.setClass=function(e,h,x){
	$(e).animate({scrollTop:(h)});
	$(e).children().removeClass('date_select');
	$(e).find('#'+x).addClass('date_select');
}
SD_DATE.setnoClass=function(e,m,x){
	$(e).children().removeClass('date_noselect');
	$(e).find('#'+m+(x+1)).addClass('date_noselect');
	$(e).find('#'+m+(x-1)).addClass('date_noselect');
}