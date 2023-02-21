package com.learn.pleion_javacoap.server;

import java.util.Timer;
import java.util.TimerTask;

import com.mbed.coap.CoapConstants;
import com.mbed.coap.exception.CoapCodeException;
import com.mbed.coap.exception.CoapException;
import com.mbed.coap.observe.AbstractObservableResource;
import com.mbed.coap.observe.NotificationDeliveryListener;
import com.mbed.coap.packet.Code;
import com.mbed.coap.packet.MediaTypes;
import com.mbed.coap.server.CoapExchange;
import com.mbed.coap.server.CoapServer;

public class MyObserverResource_Modified extends AbstractObservableResource{


	//private int int_connect_get_num=0;
	private int statusUpdate			=0;
	private int statusUpdateMaxTimes	=35;
	//
	private Timer timer = null;
	private MyTimerTaskForUpdate myUpdateTask1 	= null;
	//
	//private String content     	 				= "hello_my_world";
	private String content     	 				= "hello";
	//
	public boolean resourceFinished 			= false;
    
	
	public MyObserverResource_Modified(CoapServer coapServer) {
		super(coapServer);
		// TODO Auto-generated constructor stub
		//
		//
		this.setConNotifications(false);		// configure the notification type to NONs, 如果不写这个默认的是 CON
		//----------------------------------------
		//
		// schedule a periodic update task, otherwise let events call changed()
		//Timer timer = new Timer();
		timer = new Timer();
		// 每10000ms 则去 执行一次 里面那个run 的 changed 从而通知所有的client, 通知的时候调用handleGet
		myUpdateTask1 = new MyTimerTaskForUpdate();
		timer.schedule(myUpdateTask1,0, 1000);
	}
	
	
	@Override
	public void get(CoapExchange exchange) throws CoapCodeException {
		System.out.println("--------------------------------------------------------------------");
		System.out.println("--------- server side get method start -----------------------------");
		exchange.setResponseBody(content+":"+statusUpdate);
        exchange.getResponseHeaders().setContentFormat(MediaTypes.CT_TEXT_PLAIN);
        exchange.setResponseCode(Code.C205_CONTENT);
        exchange.sendResponse();
		System.out.println("--------- server side get method end -------------------------------");
		System.out.println("--------------------------------------------------------------------");
		/*
		// TODO Auto-generated method stub
		int_connect_get_num = int_connect_get_num +1;
		System.out.println("connect num: "+int_connect_get_num);
		System.out.println("task used num: "+int_mytask_used);
		
		
		//exchange.setResponseBody("helllo, i am server");
		//exchange.setResponseCode(Code.C205_CONTENT);
		//
		int_connect_get_num = int_connect_get_num +1;
		//exchange.respond(ResponseCode.CONTENT, "task used num:"+int_mytask_used);
		exchange.setResponseBody("task used num:"+int_mytask_used);
		exchange.setResponseCode(Code.C205_CONTENT);
		exchange.sendResponse();*/
	}
	
	
	
	
	/**
	 * 这里面 每一次changed 代表, 要去通知所有的client
	 * 则会调用handelGet
	 * 
	 * @author laipl
	 *
	 */
	private class MyTimerTaskForUpdate extends TimerTask {
		@Override
		public void run() {
			System.out.println("UpdateTask-------name:"+MyObserverResource_Modified.this.getClass().getName());
			//
			// .. periodic update of the resource
			// 为了保持 与Mqtt 测量的方式 相同, 当信息更新次数>statusUpdateMaxTimes-1时, 不再发送信息给 client
			if(statusUpdate<=statusUpdateMaxTimes-1) {
				//
				statusUpdate = statusUpdate+1;
				System.out.println(statusUpdate);
				try {
					// content format 在 notify change里面 java coap中是需要指定的 
					// 例如 MediaTypes.CT_TEXT_PLAIN 或 MediaTypes.CT_APPLICATION_JSON之类的
					// 记得 这里 如果修改了 content type 记得看一下 你需不需要把get也改了
					// 因为 client 去 observe, 它所获得的第一条信息 是来自于 get(CoapExchange exchange) 方法
					// 而不是这里这个方法
					notifyChange(new String(content+":"+statusUpdate).getBytes(CoapConstants.DEFAULT_CHARSET),MediaTypes.CT_TEXT_PLAIN);
				} catch (CoapException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} // notify all observers
			}
			else {
				resourceFinished = true;
			}
			// 类比于 mqtt 它每一次信息自己更新
		}
	}

    //---------------------------------------------------------------------
	//
	//把timer 停止了, 如果只是server.destory 是不会把这个 resource的 Timer结束的
	public int stopMyResource(){
		this.timer.cancel();
		return 1;
	}

}

