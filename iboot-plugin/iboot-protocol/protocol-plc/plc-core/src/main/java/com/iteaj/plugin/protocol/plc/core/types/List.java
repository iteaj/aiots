package com.iteaj.plugin.protocol.plc.core.types;

import java.util.ArrayList;

public class List<T> extends ArrayList<T> {

    public void Add( T[] items ){
        for (int i =0 ; i < items.length; i ++){
            add(items[i]);
        }
    }
    public void Add( ArrayList<T> items ){
        for (int i =0 ; i < items.size(); i ++){
            add(items.get(i));
        }
    }
    public void  Add( T item ){
        add(item);
    }

    public double[] toDoubleArray(){
        double[] values = new double[this.size()];
        for(int i = 0; i < values.length; i++){
            values[i] = (double) (Double)get(i);
        }
        return values;
    }

    public String[] toStringArray(){
        String[] values = new String[this.size()];
        for(int i = 0; i < values.length; i++){
            values[i] = (String)get(i);
        }
        return values;
    }

}
