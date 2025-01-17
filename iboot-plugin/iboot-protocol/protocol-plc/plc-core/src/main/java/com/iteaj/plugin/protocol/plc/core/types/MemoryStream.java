package com.iteaj.plugin.protocol.plc.core.types;

import java.io.ByteArrayOutputStream;

public class MemoryStream {
    public MemoryStream(){
        this.ms = new ByteArrayOutputStream();
    }


    public void WriteByte(int value){
        this.ms.write(value);
    }

    public void Write( byte[] buffer, int offset, int count ){
        this.ms.write(buffer,offset,count);
    }

    public void Write( byte[] buffer){
        if (buffer!= null){
            Write(buffer,0,buffer.length);
        }
    }

    public byte[] ToArray(){
        return this.ms.toByteArray();
    }

    private ByteArrayOutputStream ms;
}
