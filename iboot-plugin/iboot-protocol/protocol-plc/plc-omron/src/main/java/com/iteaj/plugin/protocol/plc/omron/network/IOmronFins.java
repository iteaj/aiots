package com.iteaj.plugin.protocol.plc.omron.network;

import com.iteaj.plugin.protocol.plc.core.net.IReadWriteDevice;
import com.iteaj.plugin.protocol.plc.core.types.OperateResult;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExOne;

import java.util.Date;

/**
 * 欧姆龙Fins协议的统一接口信息
 */
public interface IOmronFins extends IReadWriteDevice {

    /**
     * 获取当前的plc类型信息
     * @return plc类型
     */
    OmronPlcType getPlcType();

    /**
     * 设置当前的PLC类型
     * @param plcType plc类型
     */
    void setPlcType( OmronPlcType plcType );

    // region Advanced Api

    /**
     * 将CPU单元的操作模式更改为RUN，从而使PLC能够执行其程序。<br />
     * Changes the CPU Unit’s operating mode to RUN, enabling the PLC to execute its program.
     *
     * @apiNote 当执行RUN时，CPU单元将开始运行。 在执行RUN之前，您必须确认系统的安全性。 启用“禁止覆盖受保护程序”设置时，无法执行此命令。<br />
     * The CPU Unit will start operation when RUN is executed. You must confirm the safety of the system before executing RUN.
     * When the “prohibit overwriting of protected program” setting is enabled, this command cannot be executed.
     * @return 是否启动成功
     */
    OperateResult Run( );

    /**
     * 将CPU单元的操作模式更改为PROGRAM，停止程序执行。<br />
     * Changes the CPU Unit’s operating mode to PROGRAM, stopping program execution.
     * @apiNote 当执行STOP时，CPU单元将停止操作。 在执行STOP之前，您必须确认系统的安全性。<br />
     * The CPU Unit will stop operation when STOP is executed. You must confirm the safety of the system before executing STOP.
     * @return 是否停止成功
     */
    OperateResult Stop( );

    /**
     * <b>[商业授权]</b> 读取CPU的一些数据信息，主要包含型号，版本，一些数据块的大小<br />
     * <b>[Authorization]</b> Read some data information of the CPU, mainly including the model, version, and the size of some data blocks
     * @return 是否读取成功
     */
    OperateResultExOne<OmronCpuUnitData> ReadCpuUnitData( );

    /**
     * <b>[商业授权]</b> 读取CPU单元的一些操作状态数据，主要包含运行状态，工作模式，错误信息等。<br />
     * <b>[Authorization]</b> Read some operating status data of the CPU unit, mainly including operating status, working mode, error information, etc.
     * @return 是否读取成功
     */
    OperateResultExOne<OmronCpuUnitStatus> ReadCpuUnitStatus( );

    /**
     * <b>[商业授权]</b> 读取CPU的时间信息<br />
     * <b>[Authorization]</b> Read the time information of the CPU
     * @return 是否读取成功
     */
    OperateResultExOne<Date> ReadCpuTime( );

    // endregion
}
