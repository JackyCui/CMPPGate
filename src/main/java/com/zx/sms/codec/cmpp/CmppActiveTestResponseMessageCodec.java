/**
 * 
 */
package com.zx.sms.codec.cmpp;

import com.zx.sms.codec.cmpp.msg.CmppActiveTestResponseMessage;
import com.zx.sms.codec.cmpp.msg.Message;
import com.zx.sms.codec.cmpp.packet.CmppActiveTestResponse;
import com.zx.sms.codec.cmpp.packet.CmppPacketType;
import com.zx.sms.codec.cmpp.packet.PacketType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author huzorro(huzorro@gmail.com)
 * @author Lihuanghe(18852780@qq.com)
 *
 */
public class CmppActiveTestResponseMessageCodec extends MessageToMessageCodec<Message, CmppActiveTestResponseMessage> {
	private PacketType packetType;

	private Logger logger = LoggerFactory.getLogger(CmppActiveTestResponseMessageCodec.class);
	
	public CmppActiveTestResponseMessageCodec() {
		this(CmppPacketType.CMPPACTIVETESTRESPONSE);
	}

	public CmppActiveTestResponseMessageCodec(PacketType packetType) {
		this.packetType = packetType;
	}


	@Override
	protected void decode(ChannelHandlerContext ctx, Message msg, List<Object> out) throws Exception {
		long commandId = ((Long) msg.getHeader().getCommandId()).longValue();
		if (packetType.getCommandId() != commandId)
		{
			//不解析，交给下一个codec
			out.add(msg);
			return;
		}

		CmppActiveTestResponseMessage responseMessage = new CmppActiveTestResponseMessage(msg.getHeader());
		ByteBuf  bodyBuffer = Unpooled.wrappedBuffer(msg.getBodyBuffer());
		responseMessage.setReserved(bodyBuffer.readByte());
		ReferenceCountUtil.release(bodyBuffer);
		logger.info(responseMessage.toString());
		out.add(responseMessage);
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, CmppActiveTestResponseMessage msg, List<Object> out) throws Exception {
		
		ByteBuf bodyBuffer = Unpooled.buffer(CmppActiveTestResponse.RESERVED.getLength());
		bodyBuffer.writeByte(msg.getReserved());
		msg.setBodyBuffer(bodyBuffer.array());
		msg.getHeader().setBodyLength(msg.getBodyBuffer().length);
		ReferenceCountUtil.release(bodyBuffer);
		out.add(msg);
	}

}
