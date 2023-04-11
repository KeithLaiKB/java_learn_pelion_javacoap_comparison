package com.learn.pleion_javacoap.server.totesttp;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.mbed.coap.exception.CoapException;
import com.mbed.coap.observe.SimpleObservableResource;
import com.mbed.coap.packet.BlockSize;
import com.mbed.coap.packet.Code;
import com.mbed.coap.server.CoapServer;
import com.mbed.coap.server.CoapServerBuilder;
import com.mbed.coap.transmission.SingleTimeout;
import com.mbed.coap.transport.InMemoryCoapTransport;

public class TestObserver_modified {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//
		//String 	myuri1_hostaddr   				= "localhost";
		int 	myuri1_port 	  				= 5683;
		String 	myuri1_path   					= "/Resource1";
		
		
		
		// ref:java-coap/coap-core/src/test/java/protocolTests/ObservationTest.java 
		CoapServer server = CoapServer.builder().transport(myuri1_port).build();
	    //
		MyObserverResource_Modified myobResc1 = new MyObserverResource_Modified(server);
		// 注意 这里的 hello 大小写是敏感的
		// 因为 client那边 是根据 coap://localhost:5656/hello 来发送请求的
		server.addRequestHandler(myuri1_path, myobResc1);
		//server.setObservationHandler(myobResc1);
		try {
			server.start();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		//----------------------------- give some time to run ------------------------
		// 因为它和main是不同线程的, 所以我要让我的main 等到 resource发布了我所需要测量的 数据报个数 
		// 才去stop resource
		// 然后
		// 才去destroy我们的server
		while(myobResc1.resourceFinished==false) {
			// 停留一段时间 让server继续运行, 这里用 sleep 是为了减少loop的时间
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//
		//
		// 因为我们的resource用了 timer,
		// 所以我们 destroy 了server以后 , resource还是在运行的
		// in my opinion, we should apply a standard process
		// so we need to stop the resource
		myobResc1.stopMyResource();
		//
		//
		// 再让Main函数 运行一段时间, 我们可以发现resource没有输出了, 也就意味着 确实结束了
		// 其实 这后面的可以不用, 只是用来判断resource是否结束了,
		// 如果resource 没关掉, 就可以 在这段时间内 发现有resource的输出
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// destroy server
		// because the resource use the timer
		server.stop();
		System.out.println("destroy the server and stop the resource timer finished!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
	}
}
