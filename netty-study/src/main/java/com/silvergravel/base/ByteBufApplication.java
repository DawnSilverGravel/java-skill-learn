package com.silvergravel.base;

import io.netty.buffer.*;
import io.netty.util.ReferenceCountUtil;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * @author DawnStar
 * @since : 2023/10/11
 */
public class ByteBufApplication {

    public static void main(String[] args) {
        generateByteBuf();
        byteBufApi();
        referenceRelease();
    }

    /**
     * 创建 ByteBuf 几种方式
     */
    private static void generateByteBuf() {
        // 通过 Unpooled
        // Unpooled#buffer() 默认返回 heapBuffer
        ByteBuf heapBuffer = Unpooled.buffer();
        ByteBuf directBuffer = Unpooled.directBuffer();
        ByteBuf wrappedBuffer = Unpooled.wrappedBuffer(new byte[128], new byte[256]);
        ByteBuf copiedBuffer = Unpooled.copiedBuffer(ByteBuffer.allocate(128));
        ByteBuf copiedBuffer1 = Unpooled.copiedBuffer(new byte[256],new byte[256]);


        // 通过实现ByteBufAllocator相关类
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        ByteBuf abstractBuf = AbstractByteBufAllocator.DEFAULT.buffer();
        ByteBuf poolBuf = PooledByteBufAllocator.DEFAULT.buffer();
        ByteBuf unPooledBuf = UnpooledByteBufAllocator.DEFAULT.buffer();

        System.out.println("heapBuffer："+heapBuffer);
        System.out.println("directBuffer："+directBuffer);
        System.out.println("wrappedBuffer："+wrappedBuffer);
        System.out.println(wrappedBuffer.maxCapacity());
        System.out.println("copiedBuffer："+copiedBuffer);
        System.out.println("copiedBuffer maxCapacity："+copiedBuffer.maxCapacity());
        System.out.println("copiedBuffer1："+copiedBuffer1);
        System.out.println("copiedBuffer1 maxCapacity："+copiedBuffer1.maxCapacity());
        System.out.println("buffer："+buffer);
        System.out.println("abstractBuf："+abstractBuf);
        System.out.println("poolBuf："+poolBuf);
        System.out.println("unPooledBuf："+unPooledBuf);

    }

    private static void referenceRelease() {
        ByteBuf buffer = Unpooled.buffer();
        // 增加引用计数
        buffer.retain();
        buffer.retain(2);
        ReferenceCountUtil.retain(buffer);
        ReferenceCountUtil.retain(buffer,2);
        // 释放引用计数
        ReferenceCountUtil.release(buffer,3);
        ReferenceCountUtil.safeRelease(buffer,3);

        buffer.release();

        ReferenceCountUtil.safeRelease(buffer);
        System.out.println("safeRelease 执行通过");

        boolean accessible = ByteBufUtil.isAccessible(buffer);
        if (accessible) {
            System.out.println("buffer 可释放");
            ReferenceCountUtil.release(buffer);
        }else {
            System.out.println("buffer 没有可释放");
        }

        ReferenceCountUtil.release(buffer);
        System.out.println("不会执行该代码");

    }


        /**
         * ByteBuf、ByteBufUtil、Unpooled
         * 一些相关API
         */
        private static void byteBufApi() {
            // Unpooled 申请 ByteBuf 并存入指定类型的数据int
            // 类似 API long,float,boolean,short,double
            ByteBuf intBuf = Unpooled.copyInt(1, 5, 5, 2134, 5454, 2345);
            // Unpooled 申请 ByteBuf 并存入指定类型的数据,byte,char,可多个数组
            // 这样的类型会限制 maxCapacity
            ByteBuf charBuffer = Unpooled.copiedBuffer(new char[]{'a', 'b', 'c', 'd'}, StandardCharsets.UTF_8);
            ByteBuf byteBuffer = Unpooled.copiedBuffer("SilverGravel".getBytes(StandardCharsets.UTF_8));
            charBuffer.release();
            /*数据复制*/
            // 复制是全新的ByteBuf
            ByteBuf copyBuf = byteBuffer.copy();

            /*数据读写*/
            // 读取整型
            // 可读字节，整型4位字节 4*6= 24
            System.out.print("\n************数据读写************\n");
            System.out.println("intBuf可读字节：" + intBuf.readableBytes());
            System.out.print("读取intBuf数据：" + intBuf.readInt());
            while (intBuf.isReadable()) {
                System.out.print("," + intBuf.readInt());
            }
            byte[] bytes = new byte[byteBuffer.readableBytes()];
            byteBuffer.readBytes(bytes);
            System.out.println("\nbyteBuffer数据：" + new String(bytes, StandardCharsets.UTF_8));
            // 此时readerIndex已经变更，同时writerIndex也会变更
            System.out.printf("byteBuffer readerIndex: %d,writerIndex: %d\n",
                    byteBuffer.readerIndex(), byteBuffer.writerIndex());


            byte[] copyBytes = ByteBufUtil.getBytes(copyBuf);
            System.out.println("copyBuf：" + new String(copyBytes, StandardCharsets.UTF_8));

            // 写数据
            // 可以字节、InputStream、FileChannel
            // ByteBuf、ScatteringByteChannel
            ByteBuf operationBuf = Unpooled.buffer();
            operationBuf.writeBytes("Silver".getBytes(StandardCharsets.UTF_8));
            byte[] bytes2 = ByteBufUtil.getBytes(operationBuf);
            System.out.println("\noperationBuf：" + new String(bytes2, StandardCharsets.UTF_8));
            System.out.printf("operationBuf readerIndex: %d,writerIndex: %d\n",
                    operationBuf.readerIndex(), operationBuf.writerIndex());


            operationBuf.writeBytes("Gravel".getBytes(StandardCharsets.UTF_8));
            byte[] bytes3 = new byte[operationBuf.readableBytes()];
            operationBuf.readBytes(bytes3);
            System.out.println("operationBuf：" + new String(bytes3, StandardCharsets.UTF_8));
            System.out.printf("operationBuf readerIndex: %d,writerIndex: %d\n",
                    operationBuf.readerIndex(), operationBuf.writerIndex());
            operationBuf.resetReaderIndex();

            operationBuf.writeBytes("Operation".getBytes(StandardCharsets.UTF_8));
            byte[] bytes4 = new byte[operationBuf.readableBytes()];
            operationBuf.readBytes(bytes4);
            System.out.println("operationBuf：" + new String(bytes4, StandardCharsets.UTF_8));
            System.out.printf("operationBuf readerIndex: %d,writerIndex: %d\n"
                    , operationBuf.readerIndex(), operationBuf.writerIndex());

            operationBuf.writeBytes("ByteBuf".getBytes(StandardCharsets.UTF_8));
            byte[] bytes5 = ByteBufUtil.getBytes(operationBuf);
            System.out.println("operationBuf：" + new String(bytes5, StandardCharsets.UTF_8));
            System.out.printf("operationBuf readerIndex: %d,writerIndex: %d\n",
                    operationBuf.readerIndex(), operationBuf.writerIndex());
            System.out.println("由上述可知，ByteBuf#readBytes()会修改ReaderIndex和WriterIndex" +
                    "，而ByteBufUtil#getBytes()则不会");

            /*重置 ReaderIndex WriterIndex*/
            System.out.print("\n************重置 ReaderIndex WriterIndex************\n");
            ByteBuf resetBuf = Unpooled.buffer();
            resetBuf.writeBytes("1".getBytes(StandardCharsets.UTF_8));
            System.out.printf("\nresetBuf当前 readerIndex：%d，writerIndex：%d\n",
                    resetBuf.readerIndex(), resetBuf.writerIndex());
            // 标记当前读位置
            resetBuf.markReaderIndex();
            System.out.println("resetBuf当前标记readerIndex：" + resetBuf.readerIndex());
            resetBuf.writeBytes("2".getBytes(StandardCharsets.UTF_8));
            System.out.printf("resetBuf当前 readerIndex：%d，writerIndex：%d\n",
                    resetBuf.readerIndex(), resetBuf.writerIndex());

            // 读取数据
            byte[] resetBytes = new byte[resetBuf.readableBytes()];
            resetBuf.readBytes(resetBytes);
            System.out.printf("resetBuf读取数据：%s, 当前 readerIndex：%d，writerIndex：%d\n",
                    new String(resetBytes), resetBuf.readerIndex(), resetBuf.writerIndex());

            resetBuf.writeBytes("3".getBytes(StandardCharsets.UTF_8));
            System.out.printf("resetBuf 当前 readerIndex：%d，writerIndex：%d\n",
                    resetBuf.readerIndex(), resetBuf.writerIndex());
            System.out.printf("resetBuf当前readerIndex：%d，可读字节：%d\n"
                    , resetBuf.readerIndex(), resetBuf.readableBytes());
            // 重置readerIndex到上个 markReaderIndex
            resetBuf.resetReaderIndex();
            System.out.printf("resetBuf重置readerIndex之后,readerIndex：%d，可读字节：%d\n"
                    , resetBuf.readerIndex(), resetBuf.readableBytes());

            // 标记当前 WriterIndex
            resetBuf.markWriterIndex();
            System.out.printf("resetBuf标记之后 readerIndex：%d， writerIndex：%d\n"
                    , resetBuf.writerIndex(), resetBuf.writerIndex());

            resetBuf.writeBytes("4".getBytes(StandardCharsets.UTF_8));
            resetBytes = new byte[resetBuf.readableBytes()];
            resetBuf.readBytes(resetBytes);
            System.out.printf("resetBuf读取数据：%s, 当前 readerIndex：%d，writerIndex：%d\n",
                    new String(resetBytes), resetBuf.readerIndex(), resetBuf.writerIndex());

            // 如果 WriterIndex > ReaderIndex 则抛出异常
            // 这里再重置一下 readerIndex
            resetBuf.resetReaderIndex();
            resetBuf.resetWriterIndex();
            System.out.printf("resetBuf重置读写索引之后,readerIndex：%d，writerIndex：%d\n"
                    , resetBuf.readerIndex(), resetBuf.writerIndex());

            // 等于 ByteBuf#setIndex(0,0)
            resetBuf.clear();
            System.out.printf("resetBuf#clear之后,readerIndex：%d，writerIndex：%d\n"
                    , resetBuf.readerIndex(), resetBuf.writerIndex());


            // 0 <= readerIndex <= writerIndex <= capacity
            resetBuf.setIndex(1, 3);
            System.out.printf("resetBuf设置读写索引之后,readerIndex：%d，writerIndex：%d\n"
                    , resetBuf.readerIndex(), resetBuf.writerIndex());



            /* 增加或释放 ByteBuf refCnt引用计数*/
            System.out.println("\n************增加或释放 ByteBuf refCnt引用计数************");
            ByteBuf refBuf = resetBuf.copy();
            refBuf.retain();
            refBuf.retain(3);
            // 1+1+3 = 5
            System.out.println("\nrefBuf增加引用计数后：" + refBuf.refCnt());
            refBuf.release();
            refBuf.release(3);
            System.out.println("refBuf释放引用计数后：" + refBuf.refCnt());

            /*转成Java NIO ByteBuffer*/
            System.out.println("\n************转成Java NIO ByteBuffer");
            ByteBuffer nioBuffer = resetBuf.nioBuffer();

            int remaining = nioBuffer.remaining();
            byte[] nioBytes = new byte[remaining];
            nioBuffer.get(nioBytes);
            System.out.println(new String(nioBytes));


            /*slice ByteBuf*/
            System.out.println("\ns************lice、duplicate ByteBuf************");
            ByteBuf sliceBuf = Unpooled.buffer();
            sliceBuf.writeBytes("SliceBuf".getBytes(StandardCharsets.UTF_8));
            ByteBuf slice1 = sliceBuf.slice();
            // 进行数据读取
            sliceBuf.readBytes(new byte[3]);
            System.out.printf("sliceBuf writerIndex：%d，readerIndex：%d，maxCapacity：%d\n"
                    ,sliceBuf.writerIndex(),sliceBuf.readerIndex(),sliceBuf.maxCapacity());
            System.out.printf("slice1 writerIndex：%d，readerIndex：%d，maxCapacity：%d\n"
                    ,slice1.writerIndex(),slice1.readerIndex(),slice1.maxCapacity());
            ByteBuf slice2 = sliceBuf.slice(1, 3);
            System.out.printf("slice2 writerIndex：%d，readerIndex：%d，maxCapacity：%d\n"
                    ,slice2.writerIndex(),slice2.readerIndex(),slice2.maxCapacity());

            ByteBuf duplicate = sliceBuf.duplicate();
            System.out.printf("duplicate writerIndex：%d，readerIndex：%d，maxCapacity：%d\n"
                    ,duplicate.writerIndex(),duplicate.readerIndex(),duplicate.maxCapacity());

        }




}
