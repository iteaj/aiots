package com.iteaj.plugin.protocol.iec104.network;

import org.openmuc.j60870.ie.*;

import java.util.Date;

public class IEC104TITime2aValue extends IEC104TIValue implements Time2aResolver{

    private InformationElement time2a;

    public IEC104TITime2aValue(int ioa, boolean singlePointState, IeQuality quality) {
        super(ioa, singlePointState, quality);
    }

    public IEC104TITime2aValue(int ioa, IeValueWithTransientState transientState, IeQuality quality) {
        super(ioa, transientState, quality);
    }

    public IEC104TITime2aValue(int ioa, IeDoublePointWithQuality.DoublePointInformation doublePointState, IeQuality quality) {
        super(ioa, doublePointState, quality);
    }

    public IEC104TITime2aValue(int ioa, IeBinaryStateInformation binaryState, IeQuality quality) {
        super(ioa, binaryState, quality);
    }

    public IEC104TITime2aValue build(InformationElement time2a) {
        this.time2a = time2a; return this;
    }

    public InformationElement getTime2a() {
        return time2a;
    }

    public long getTimestamp() {
        return resolverTimestamp(this.getTime2a());
    }

    public Date getDate() {
        return resolverDate(this.getTime2a());
    }
}
