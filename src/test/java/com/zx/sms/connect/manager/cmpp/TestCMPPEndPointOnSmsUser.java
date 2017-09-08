package com.zx.sms.connect.manager.cmpp;

import com.zx.sms.connect.manager.CMPPEndpointManager;
import com.zx.sms.handler.api.BusinessHandlerInterface;
import com.zx.sms.handler.api.gate.SessionConnectedHandler;
import com.zx.sms.handler.api.smsbiz.MessageReceiveHandler;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.LockSupport;
/**
 *经测试，35个连接，每个连接每200/s条消息
 *lenovoX250能承担7000/s消息编码解析无压力。
 *10000/s的消息服务不稳定，开个网页，或者打开其它程序导致系统抖动，会有大量消息延迟 (超过500ms)
 *
 *低负载时消息编码解码可控制在10ms以内。
 *
 */


public class TestCMPPEndPointOnSmsUser {
	private static final Logger logger = LoggerFactory.getLogger(TestCMPPEndPointOnSmsUser.class);

	@Test
	public void testCMPPEndpoint() throws Exception {
	
		final CMPPEndpointManager manager = CMPPEndpointManager.INS;
	
		CMPPClientEndpointEntity client = new CMPPClientEndpointEntity();
		client.setId("client");
		client.setHost("139.196.80.180");
		client.setPort(17890);
		client.setChartset(Charset.forName("utf-8"));
		client.setGroupName("gmsy");
		client.setUserName("080180");
		client.setPassword("080180");
		client.setWindows((short)16);
		client.setVersion((short)48);
		client.setRetryWaitTimeSec((short)10);
		client.setMaxChannels((short)10);
		
		List<BusinessHandlerInterface> clienthandlers = new ArrayList<BusinessHandlerInterface>();
		clienthandlers.add(new MessageReceiveHandler());
		clienthandlers.add(new SessionConnectedHandler());
		client.setBusinessHandlerSet(clienthandlers);
		manager.addEndpointEntity(client);

		
		manager.openAll();
		LockSupport.park();
//		Thread.sleep(300000);
		CMPPEndpointManager.INS.close();
	}
}
