/**
 * 
 */
package com.zx.sms.codec.cmpp7F;

import com.zx.sms.codec.cmpp.msg.CmppDeliverRequestMessage;
import com.zx.sms.codec.cmpp.msg.CmppDeliverResponseMessage;
import com.zx.sms.codec.cmpp.msg.CmppReportRequestMessage;
import com.zx.sms.codec.cmpp.msg.LongMessageFrame;
import com.zx.sms.codec.cmpp.msg.Message;
import com.zx.sms.codec.cmpp.packet.CmppDeliverRequest;
import com.zx.sms.codec.cmpp.packet.CmppReportRequest;
import com.zx.sms.codec.cmpp.packet.PacketType;
import com.zx.sms.codec.cmpp.wap.LongMessageFrameHolder;
import com.zx.sms.codec.cmpp7F.packet.Cmpp7FPacketType;
import com.zx.sms.common.GlobalConstance;
import com.zx.sms.common.util.CMPPCommonUtil;
import com.zx.sms.common.util.DefaultMsgIdUtil;
import com.zx.sms.common.util.DefaultSequenceNumberUtil;
import com.zx.sms.common.util.FstObjectSerializeUtil;
import com.zx.sms.common.util.NettyByteBufUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.util.ReferenceCountUtil;
import org.marre.sms.SmsDcs;
import org.marre.sms.SmsMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author huzorro(huzorro@gmail.com)
 * @author Lihuanghe(18852780@qq.com)
 */
public class Cmpp7FDeliverRequestMessageCodec extends MessageToMessageCodec<Message, CmppDeliverRequestMessage> {
	private final Logger logger = LoggerFactory.getLogger(Cmpp7FDeliverRequestMessageCodec.class);
	private PacketType packetType;

	/**
	 * 
	 */
	public Cmpp7FDeliverRequestMessageCodec() {
		this(Cmpp7FPacketType.CMPPDELIVERREQUEST);
	}

	public Cmpp7FDeliverRequestMessageCodec(PacketType packetType) {
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

		CmppDeliverRequestMessage requestMessage = new CmppDeliverRequestMessage(msg.getHeader());

		ByteBuf bodyBuffer = Unpooled.wrappedBuffer(msg.getBodyBuffer());
		requestMessage.setMsgId(DefaultMsgIdUtil.bytes2MsgId(NettyByteBufUtil.readBytes(bodyBuffer, CmppDeliverRequest.MSGID.getLength())));
		requestMessage.setDestId(bodyBuffer.readBytes(CmppDeliverRequest.DESTID.getLength()).toString(GlobalConstance.defaultTransportCharset).trim());
		requestMessage.setServiceid(bodyBuffer.readBytes(CmppDeliverRequest.SERVICEID.getLength()).toString(GlobalConstance.defaultTransportCharset).trim());

		LongMessageFrame frame = new LongMessageFrame();
		frame.setTppid(bodyBuffer.readUnsignedByte());
		frame.setTpudhi(bodyBuffer.readUnsignedByte());
		frame.setMsgfmt(new SmsDcs((byte)bodyBuffer.readUnsignedByte()));

		requestMessage.setSrcterminalId(bodyBuffer.readBytes(CmppDeliverRequest.SRCTERMINALID.getLength()).toString(GlobalConstance.defaultTransportCharset)
				.trim());
		requestMessage.setSrcterminalType(bodyBuffer.readUnsignedByte());
		requestMessage.setRegisteredDelivery(bodyBuffer.readUnsignedByte());
		
		int frameLength = frame.getPayloadLength(bodyBuffer.readUnsignedByte());
		
		if (requestMessage.getRegisteredDelivery() == 0) {
			byte[] contentbytes = new byte[frameLength];
			bodyBuffer.readBytes(contentbytes);
			frame.setMsgContentBytes(contentbytes);
			frame.setMsgLength((short)frameLength);
		} else {
			requestMessage.setReportRequestMessage(new CmppReportRequestMessage());
			requestMessage.getReportRequestMessage().setMsgId(DefaultMsgIdUtil.bytes2MsgId(NettyByteBufUtil.readBytes(bodyBuffer, CmppReportRequest.MSGID.getLength())));
			requestMessage.getReportRequestMessage().setStat(
					bodyBuffer.readBytes(CmppReportRequest.STAT.getLength()).toString(GlobalConstance.defaultTransportCharset).trim());
			requestMessage.getReportRequestMessage().setSubmitTime(
					bodyBuffer.readBytes(CmppReportRequest.SUBMITTIME.getLength()).toString(GlobalConstance.defaultTransportCharset).trim());
			requestMessage.getReportRequestMessage().setDoneTime(
					bodyBuffer.readBytes(CmppReportRequest.DONETIME.getLength()).toString(GlobalConstance.defaultTransportCharset).trim());
			requestMessage.getReportRequestMessage().setDestterminalId(
					bodyBuffer.readBytes(CmppReportRequest.DESTTERMINALID.getLength()).toString(GlobalConstance.defaultTransportCharset).trim());
			requestMessage.getReportRequestMessage().setSmscSequence(bodyBuffer.readUnsignedInt());
		}

		requestMessage.setLinkid(bodyBuffer.readBytes(CmppDeliverRequest.LINKID.getLength()).toString(GlobalConstance.defaultTransportCharset).trim());
		//在线公司自定义的字段
		int attach = bodyBuffer.readInt();
		if(attach != 0 ){
			byte[] objbytes = new byte[attach];
			bodyBuffer.readBytes(objbytes);
			try{
				requestMessage.setAttachment(FstObjectSerializeUtil.read(objbytes));
			}catch(Exception ex){
				logger.warn("Attachment decode error",ex);
			}
		}
		
		if (requestMessage.getRegisteredDelivery() == 0) {
			try {
				SmsMessage content = LongMessageFrameHolder.INS.putAndget(requestMessage.getSrcterminalId(), frame);

				if (content != null) {
					requestMessage.setMsgContent(content);
					out.add(requestMessage);
				} else {
					//短信片断未接收完全，直接给网关回复resp，等待其它片断
					CmppDeliverResponseMessage responseMessage = new CmppDeliverResponseMessage(msg.getHeader());
					responseMessage.setMsgId(requestMessage.getMsgId());
					responseMessage.setResult(0);
					ctx.channel().writeAndFlush(responseMessage);
				}
			} catch (Exception ex){
				logger.error("",ex);
				//长短信解析失败，直接给网关回复 resp . 并丢弃这个短信
				logger.error("Decode CmppDeliverRequestMessage Error ,msg dump :{}" , ByteBufUtil.hexDump(msg.getBodyBuffer()));
				CmppDeliverResponseMessage responseMessage = new CmppDeliverResponseMessage(msg.getHeader());
				responseMessage.setMsgId(requestMessage.getMsgId());
				responseMessage.setResult(0);
				ctx.channel().writeAndFlush(responseMessage);
			}
		} else {
			out.add(requestMessage);
		}
		ReferenceCountUtil.release(bodyBuffer);
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, CmppDeliverRequestMessage oldMsg, List<Object> out) throws Exception {
		
		if(oldMsg.getBodyBuffer()!=null && oldMsg.getBodyBuffer().length == oldMsg.getHeader().getBodyLength()){
			//bodybuffer不为空，说明该消息是已经编码过的。可能其它连接断了，消息通过这个连接再次发送，不需要再次编码
			out.add(oldMsg);
			return;
		}
		
		//clone一个消息，防止业务层对msg进行修改
		CmppDeliverRequestMessage requestMessage = oldMsg.clone();
		
		List<LongMessageFrame> frameList = LongMessageFrameHolder.INS.splitmsgcontent(requestMessage.getMsg(), requestMessage.isSupportLongMsg());
		
		boolean first = true;
		for (LongMessageFrame frame : frameList) {
			// bodyBuffer 会在CmppHeaderCodec.encode里释放
			ByteBuf bodyBuffer = Unpooled.buffer(CmppDeliverRequest.DESTID.getBodyLength() + frame.getMsgLength());

			bodyBuffer.writeBytes(DefaultMsgIdUtil.msgId2Bytes(requestMessage.getMsgId()));
			bodyBuffer.writeBytes(CMPPCommonUtil.ensureLength(requestMessage.getDestId().getBytes(GlobalConstance.defaultTransportCharset),
					CmppDeliverRequest.DESTID.getLength(), 0));
			bodyBuffer.writeBytes(CMPPCommonUtil.ensureLength(requestMessage.getServiceid().getBytes(GlobalConstance.defaultTransportCharset),
					CmppDeliverRequest.SERVICEID.getLength(), 0));
			bodyBuffer.writeByte(frame.getTppid());
			bodyBuffer.writeByte(frame.getTpudhi());
			bodyBuffer.writeByte(frame.getMsgfmt().getValue());
			bodyBuffer.writeBytes(CMPPCommonUtil.ensureLength(requestMessage.getSrcterminalId().getBytes(GlobalConstance.defaultTransportCharset),
					CmppDeliverRequest.SRCTERMINALID.getLength(), 0));
			bodyBuffer.writeByte(requestMessage.getSrcterminalType());
			bodyBuffer.writeByte(requestMessage.getRegisteredDelivery());
			

			if (!requestMessage.isReport()) {
				bodyBuffer.writeByte(frame.getMsgLength());

				bodyBuffer.writeBytes(frame.getMsgContentBytes());
				//修改消息内容为分片以后的值
				requestMessage.setMsgContent(frame.getContentPart());
				
			} else {
				bodyBuffer.writeByte(CmppReportRequest.DESTTERMINALID.getBodyLength());
				
				bodyBuffer.writeBytes(DefaultMsgIdUtil.msgId2Bytes(requestMessage.getReportRequestMessage().getMsgId()));
				bodyBuffer.writeBytes(CMPPCommonUtil.ensureLength(
						requestMessage.getReportRequestMessage().getStat().getBytes(GlobalConstance.defaultTransportCharset),
						CmppReportRequest.STAT.getLength(), 0));
				bodyBuffer.writeBytes(CMPPCommonUtil.ensureLength(
						requestMessage.getReportRequestMessage().getSubmitTime().getBytes(GlobalConstance.defaultTransportCharset),
						CmppReportRequest.SUBMITTIME.getLength(), 0));
				bodyBuffer.writeBytes(CMPPCommonUtil.ensureLength(
						requestMessage.getReportRequestMessage().getDoneTime().getBytes(GlobalConstance.defaultTransportCharset),
						CmppReportRequest.DONETIME.getLength(), 0));
				bodyBuffer.writeBytes(CMPPCommonUtil.ensureLength(
						requestMessage.getReportRequestMessage().getDestterminalId().getBytes(GlobalConstance.defaultTransportCharset),
						CmppReportRequest.DESTTERMINALID.getLength(), 0));

				bodyBuffer.writeInt((int) requestMessage.getReportRequestMessage().getSmscSequence());
			}

			bodyBuffer.writeBytes(CMPPCommonUtil.ensureLength(requestMessage.getLinkid().getBytes(GlobalConstance.defaultTransportCharset),
					CmppDeliverRequest.LINKID.getLength(), 0));
			//在线公司自定义的字段
			if(requestMessage.getAttachment()!=null){
				try{
					byte[] attach =FstObjectSerializeUtil.write(requestMessage.getAttachment());
					bodyBuffer.writeInt(attach.length);
					bodyBuffer.writeBytes(attach);
				}catch(Exception ex){
					logger.warn("Attachment Serializa error",ex);
					bodyBuffer.writeInt(0);
				}
			}else{
				bodyBuffer.writeInt(0);
			}
			if (first) {

				requestMessage.setBodyBuffer(byteBufreadarray(bodyBuffer));
				requestMessage.getHeader().setBodyLength(requestMessage.getBodyBuffer().length);
				out.add(requestMessage);
				first = false;
			} else {

				CmppDeliverRequestMessage defaultMsg = requestMessage.clone();
				defaultMsg.getHeader().setSequenceId(DefaultSequenceNumberUtil.getSequenceNo());
				defaultMsg.setBodyBuffer(byteBufreadarray(bodyBuffer));
				defaultMsg.getHeader().setBodyLength(defaultMsg.getBodyBuffer().length);
				out.add(defaultMsg);
			}
			ReferenceCountUtil.release(bodyBuffer);
		}

	}
	private byte[] byteBufreadarray(ByteBuf buf){
		byte[] dst = new byte[ buf.readableBytes()];
		buf.readBytes(dst);
		return dst;
	}
}
