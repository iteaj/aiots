package com.iteaj.plugin.protocol.iec104.network;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import org.openmuc.j60870.ie.IeTime16;
import org.openmuc.j60870.ie.IeTime24;
import org.openmuc.j60870.ie.IeTime56;
import org.openmuc.j60870.ie.InformationElement;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public interface Time2aResolver {

    default long resolverTimestamp(InformationElement time24) {
        if(time24 instanceof IeTime24) {
            // 到分钟的毫秒数
            return ((IeTime24) time24).getTimeInMs();
        } else if(time24 instanceof IeTime56){
            return ((IeTime56) time24).getTimestamp();
        } else {
            return ((IeTime16) time24).getTimeInMs();
        }
    }

    default Date resolverDate(InformationElement time) {
        if(time instanceof IeTime24) {
            final long m = TimeUnit.MILLISECONDS.toMinutes(((IeTime24) time).getTimeInMs());
            final DateTime date = new DateTime();
            date.setField(DateField.MINUTE, (int) m);
            date.setField(DateField.SECOND, 0);
            date.setField(DateField.MILLISECOND, 0);
            return date;
        } else if(time instanceof IeTime56) {
            return new Date(((IeTime56) time).getTimestamp());
        } else {
            final int timeInMs = ((IeTime16) time).getTimeInMs();
            final long seconds = TimeUnit.MILLISECONDS.toSeconds(timeInMs);
            final DateTime date = new DateTime();
            date.setField(DateField.MINUTE, 0);
            date.setField(DateField.SECOND, (int) seconds);
            date.setField(DateField.MILLISECOND, (int) (timeInMs - (seconds * 1000)));
            return date;
        }
    }

    default Date resolverDate(InformationElement time2a, InformationElement time2a_1) {
        final int timeInMs = ((IeTime16) time2a).getTimeInMs();
        if(time2a_1 instanceof IeTime24) {
            final long m = TimeUnit.MILLISECONDS.toMinutes(((IeTime24) time2a_1).getTimeInMs() + timeInMs);
            final DateTime date = new DateTime();
            date.setField(DateField.MINUTE, (int) m);
//            date.setField(DateField.SECOND, (int) s);
//            date.setField(DateField.MILLISECOND, (int) (timeInMs - (s * 1000)));
            return date;
        } else {
            return new DateTime(((IeTime56)time2a_1).getTimestamp() + timeInMs);
        }
    }
}
