package com.iteaj.plugin.protocol.iec104.network;

import org.openmuc.j60870.ie.IeBinaryCounterReading;
import org.openmuc.j60870.ie.InformationElement;

import java.util.Date;
import java.util.Set;

public class IEC104CounterTime2aValue extends IEC104CounterValue implements Time2aResolver{

    private final InformationElement time2a;

    public IEC104CounterTime2aValue(int ioa, int counterReading, int sequenceNumber, Set<IeBinaryCounterReading.Flag> flags, InformationElement time2a) {
        super(ioa, counterReading, sequenceNumber, flags);
        this.time2a = time2a;
    }

    public InformationElement getTime2a() {
        return time2a;
    }

    public long getTimestamp() {
        return resolverTimestamp(time2a);
    }

    public Date getDate() {
        return resolverDate(time2a);
    }
}
