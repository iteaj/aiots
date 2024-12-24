package com.iteaj.plugin.protocol.plc.core.log.core;

import com.iteaj.plugin.protocol.plc.core.types.Environment;
import com.iteaj.plugin.protocol.plc.core.StringResources;
import com.iteaj.plugin.protocol.plc.core.Utilities;

public class LogNetManagment {

    public static String GetDegreeDescription(MessageDegree degree) {
        switch (degree) {
            case DEBUG:
                return StringResources.Language.LogNetDebug();
            case INFO:
                return StringResources.Language.LogNetInfo();
            case WARN:
                return StringResources.Language.LogNetWarn();
            case ERROR:
                return StringResources.Language.LogNetError();
            case FATAL:
                return StringResources.Language.LogNetFatal();
            case None:
                return StringResources.Language.LogNetAbandon();
            default:
                return StringResources.Language.LogNetAbandon();
        }
    }

    /**
     * 通过异常文本格式化成字符串用于保存或发送<br />
     * Formatted as a string with exception text for saving or sending
     * @param text 文本消息
     * @param ex 异常
     * @return 异常最终信息
     */
    public static String GetSaveStringFromException(String text, Exception ex) {
        StringBuilder builder = new StringBuilder(text);

        if (ex != null) {
            if (!Utilities.IsStringNullOrEmpty(text)) {
                builder.append(" : ");
            }

            try {
                builder.append(StringResources.Language.ExceptionMessage());
                builder.append(ex.getMessage());
                builder.append(Environment.NewLine);
            } catch (Exception ex2) {

            }
            builder.append(Environment.NewLine);
            builder.append("\u0002/=================================================[    Exception    ]================================================/");
        }

        try {
            return builder.toString();
        } catch (Exception ex1) {
            return Utilities.IsStringNullOrEmpty(text) ? ex1.getMessage() : (text + ":" + ex.getMessage());
        }
    }
}
