package com.iteaj.plugin.protocol.plc.core.types;

/**
 * 一个数据范围管理的的类型对象，可以方便的管理一个数据值的最大值，最小值，平均值信息。<br />
 * A type object managed by a data range can conveniently manage the maximum, minimum, and average information of a data value.
 */
public class ValueLimit {

    /**
     * 当前的数据的最大值<br />
     * The maximum value of the current data
     */
    public double MaxValue = 0;

    /**
     * 当前的数据的最小值<br />
     * The minimum value of the current data
     */
    public double MinValue  = 0;

    /**
     * 当前的数据的平均值<br />
     * Average value of current data
     */
    public double Average  = 0;

    /**
     * 当前的数据的起始值<br />
     * The starting value of the current data
     */
    public double StartValue  = 0;

    /**
     * 当前的数据的当前值，也是最后一次更新的数据值<br />
     * The current value of the current data is also the last updated data value
     */
    public double Current = 0;

    /**
     * 当前的数据的更新总个数<br />
     * The total number of current data updates
     */
    public int Count = 0;

    /**
     * 重新设置当前的最新值，然后计算新的最大值，最小值，平均值等信息<br />
     * Reset the current latest value, and then calculate the new maximum, minimum, average and other information
     * @param value 新的数据值信息
     * @return 新的值对象
     */
    public ValueLimit SetNewValue( double value )
    {
        if (Double.isNaN( value ))
        {

        }
        else
        {
            if (Count == 0)
            {
                MaxValue = value;
                MinValue = value;
                Count = 1;
                Current = value;
                Average = value;
                StartValue = value;
            }
            else
            {
                if (value < MinValue) MinValue = value;
                if (value > MaxValue) MaxValue = value;
                Current = value;
                Average = (Count * Average + value) / (Count + 1);
                Count++;
            }
        }
        return this;
    }

    public String toString( ){
        return  "Avg[" + Current + "]";
    }
}
