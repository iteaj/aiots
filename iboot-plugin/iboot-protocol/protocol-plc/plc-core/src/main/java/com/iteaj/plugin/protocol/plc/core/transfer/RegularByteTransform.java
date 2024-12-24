package com.iteaj.plugin.protocol.plc.core.transfer;

public class RegularByteTransform extends ByteTransformBase
{
    public RegularByteTransform(){

    }

    public RegularByteTransform(DataFormat dataFormat){
        super(dataFormat);
    }

    public IByteTransform CreateByDateFormat( DataFormat dataFormat ) {
        RegularByteTransform transform = new RegularByteTransform(dataFormat);
        transform.setIsStringReverse(this.getIsStringReverse());
        return transform;
    }
}
