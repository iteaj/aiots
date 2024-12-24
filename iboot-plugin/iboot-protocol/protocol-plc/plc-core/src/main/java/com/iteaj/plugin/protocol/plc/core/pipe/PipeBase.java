package com.iteaj.plugin.protocol.plc.core.pipe;
import com.iteaj.plugin.protocol.plc.core.thread.SimpleHybirdLock;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 管道的基础类对象
 */
public class PipeBase {

    /**
     * 实例化一个默认的对象
     */
    public PipeBase( )
    {
        this.hybirdLock = new SimpleHybirdLock( );
    }

    /**
     * 获取锁
     */
    public void PipeLockEnter( ){
        lockingTick.getAndIncrement();
        this.hybirdLock.Enter( );
    }

    /**
     * 离开锁
     */
    public void PipeLockLeave( ) {
        this.hybirdLock.Leave( );
        lockingTick.decrementAndGet();
    }

    /**
     * 获取当前进入锁的计数
     */
    public int getLockingTick() {
        return this.lockingTick.get();
    }


    private SimpleHybirdLock hybirdLock;                     // 通道交互的锁，通道里要求每个通信都是唯一的
    private AtomicInteger lockingTick = new AtomicInteger(0);
}
