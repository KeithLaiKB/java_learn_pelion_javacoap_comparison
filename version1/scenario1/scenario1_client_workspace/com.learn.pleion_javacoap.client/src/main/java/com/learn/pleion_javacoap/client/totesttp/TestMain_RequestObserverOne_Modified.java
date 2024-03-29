package com.learn.pleion_javacoap.client.totesttp;


import java.io.IOException;

import java.net.InetSocketAddress;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


import com.mbed.coap.client.CoapClient;
import com.mbed.coap.client.CoapClientBuilder;
import com.mbed.coap.client.ObservationListener;
import com.mbed.coap.exception.CoapException;
import com.mbed.coap.packet.CoapPacket;


public class TestMain_RequestObserverOne_Modified {
	private static int receivedMessageNum 					= 0;
	private static int expectedReceivedMessageNum			= 30;
	
	public static void main(String[] args) {
		String port1 = "coap://localhost:5656/hello";
		String port2 = "coap://160.32.219.56:5656/hello";		//有线连接树莓派, 路由给的地址是192.168.50.178
																// 我把它的192.168.50.178:5656 映射成160.32.219.56:5656
		String port3 = "coap://160.32.219.56:5657/hello";		//无线连接树莓派, 路由给的地址是192.168.50.179
																// 我把它的192.168.50.179:5656 映射成160.32.219.56:5657
		
		//String 	myuri1_hostaddr   				= "135.0.237.84";
		String 	myuri1_hostaddr   				= "192.168.239.137";
		int 	myuri1_port 	  				= 5683;
		String 	myuri1_path   					= "/Resource1";
    	//

    	
		InetSocketAddress inetSocketAddr = new InetSocketAddress(myuri1_hostaddr,myuri1_port);
		CoapClient client=null;
		try {
			client = CoapClientBuilder.newBuilder(inetSocketAddr).build();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		CompletableFuture<CoapPacket> resp = null;
		try {
			//resp = client.resource(myuri1_path).maxAge(10L).get();																		//测试 get_con 单个get请求情况下  是否 包含 多个option
			//resp = client.resource(myuri1_path).maxAge(10L).payload("cs".getBytes()).get();												//测试 get_con 单个get请求情况下 是否 包含payload
			
			//resp = client.resource(myuri1_path).maxAge(10L).observe(new MyObservationListener()); 											//测试 get_con   observer的情况下   是否 包含option
			//resp = client.resource(myuri1_path).maxAge(10L).payload("test_obrq_payload".getBytes()).observe(new MyObservationListener()); 	//测试 get_con   observer的情况下   是否 包含payload
			
			resp = client.resource(myuri1_path).observe(new MyObservationListener());
			//
			if(resp != null) {
				//用来获取 第一次得到的数据
				if(resp.get().getPayloadString()!=null) {								//防止对面传空 对面传空 直接这么写里面的 会报错的, 所以它和californium 不太一样
					System.out.println(resp.get().getPayloadString().toString());		
				}
				else {
					System.out.println();	
				}
				// System.out.println(resp.get().getPayloadString().toString());
				receivedMessageNum = receivedMessageNum +1;
			}
			//
		} catch (CoapException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
        //---------------------------------------------
		// 停留一段时间 让server继续运行
        while(receivedMessageNum < expectedReceivedMessageNum) {
        	try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        //
		//-----
        System.out.println("kkk");
		client.close();
	
	}
	
	/**
	 * ObservationListener
	 * ref: java-coap/coap-core/src/test/java/protocolTests/ObservationTest.java
	 * 
	 * @author laipl
	 *
	 */
    public static class MyObservationListener implements ObservationListener {

        @Override
        public void onObservation(CoapPacket obsPacket) throws CoapException {
            System.out.println(obsPacket.getPayloadString());
            receivedMessageNum = receivedMessageNum +1;
        }

        @Override
        public void onTermination(CoapPacket obsPacket) throws CoapException {
        	System.out.println("term!!!!!!!"+obsPacket.getPayloadString());
        }
    }
}

