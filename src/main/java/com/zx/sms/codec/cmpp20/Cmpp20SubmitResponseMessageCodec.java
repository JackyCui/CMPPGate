/**
 * 
 */
package com.zx.sms.codec.cmpp20;

import com.zx.sms.codec.cmpp.msg.CmppSubmitResponseMessage;
import com.zx.sms.codec.cmpp.msg.Message;
import com.zx.sms.codec.cmpp.packet.PacketType;
import com.zx.sms.codec.cmpp20.packet.Cmpp20PacketType;
import com.zx.sms.codec.cmpp20.packet.Cmpp20SubmitResponse;
import com.zx.sms.common.util.DefaultMsgIdUtil;
import com.zx.sms.common.util.NettyByteBufUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.util.ReferenceCountUtil;

import java.util.List;

/**
 * shifei(shifei@asiainfo.com)
 */
public class Cmpp20SubmitResponseMessageCodec extends MessageToMessageCodec<Message, CmppSubmitResponseMessage> {
	private PacketType packetType;

	/**
	 * 
	 */
	public Cmpp20SubmitResponseMessageCodec() {
		this(Cmpp20PacketType.CMPPSUBMITRESPONSE);
	}

	public Cmpp20SubmitResponseMessageCodec(PacketType packetType) {
		this.packetType = packetType;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, Message msg, List<Object> out) throws Exception {
		long commandId = ((Long) msg.getHeader().getCommandId()).longValue();
		if (packetType.getCommandId() != commandId) {
			// 不解析，交给下一个codec
			out.add(msg);
			return;
		}
		out.add(decode(msg));
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, CmppSubmitResponseMessage msg, List<Object> out) throws Exception {
		ByteBuf bodyBuffer = Unpooled.buffer(Cmpp20SubmitResponse.RESULT.getBodyLength());

		bodyBuffer.writeBytes(DefaultMsgIdUtil.msgId2Bytes(msg.getMsgId()));
		bodyBuffer.writeByte((int) msg.getResult());
		msg.setBodyBuffer(bodyBuffer.array());
		msg.getHeader().setBodyLength(msg.getBodyBuffer().length);
		ReferenceCountUtil.release(bodyBuffer);
		out.add(msg);
	}
	
	public static CmppSubmitResponseMessage decode(Message msg){
		CmppSubmitResponseMessage responseMessage = new CmppSubmitResponseMessage(msg.getHeader());

		ByteBuf bodyBuffer =Unpooled.wrappedBuffer(msg.getBodyBuffer());

		responseMessage.setMsgId(DefaultMsgIdUtil.bytes2MsgId(NettyByteBufUtil.readBytes(bodyBuffer, Cmpp20SubmitResponse.MSGID.getLength())));
		responseMessage.setResult(bodyBuffer.readUnsignedByte());
		ReferenceCountUtil.release(bodyBuffer);
		return responseMessage;
	}

}
