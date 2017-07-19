package com.zx.sms.common.util;

import io.netty.buffer.ByteBuf;

/**
 * @author jtcui
 * @version 2017/7/19
 */
public class NettyByteBufUtil {

    /**
     * Read the bytes of length from the bytebuf
     * this operation increase the readerIndex by length
     * @param buffer
     * @param length
     * @return
     */
    public static byte[] readBytes(ByteBuf buffer, int length) {
        byte[] result = new byte[length];
        buffer.readBytes(result);
        return result;
    }
}
