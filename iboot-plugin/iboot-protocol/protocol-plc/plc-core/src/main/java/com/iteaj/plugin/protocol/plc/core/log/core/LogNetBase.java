package com.iteaj.plugin.protocol.plc.core.log.core;

import com.iteaj.plugin.protocol.plc.core.thread.SimpleHybirdLock;
import com.iteaj.plugin.protocol.plc.core.types.Environment;
import com.iteaj.plugin.protocol.plc.core.types.List;
import com.iteaj.plugin.protocol.plc.core.Utilities;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Queue;

public class LogNetBase implements  ILogNet{

    // region Constructor

    /// <summary>
    /// 实例化一个日志对象<br />
    /// Instantiate a log object
    /// </summary>
    public LogNetBase( )
    {
        m_fileSaveLock     = new SimpleHybirdLock( );
        m_simpleHybirdLock = new SimpleHybirdLock( );
        m_WaitForSave      = new LinkedList<MessageItem>( );
        filtrateKeyword    = new List<String>( );
        filtrateLock       = new Object( );
    }

    // endregion

    // region Private Member

    /// <summary>
    /// 文件存储的锁
    /// </summary>
    protected SimpleHybirdLock m_fileSaveLock;                                             // 文件的锁
    private MessageDegree m_messageDegree = MessageDegree.DEBUG;                     // 默认的存储规则
    private Queue<MessageItem> m_WaitForSave;                                           // 待存储数据的缓存
    private SimpleHybirdLock m_simpleHybirdLock;                                           // 缓存列表的锁
    private int m_SaveStatus = 0;                                                          // 存储状态
    private List<String> filtrateKeyword;                                                  // 需要过滤的存储对象
    private final Object filtrateLock;                                                           // 过滤列表的锁
    private String lastLogSaveFileName = "";                                               // 上一次存储的文件的名字

    // endregion

    // region Event Handle

    /**
     * 在日志存储到文件之前调用的方法，只需要重写本方法，就可以实现任意的信息输出<br />
     * The method that is called before the log is stored in a file only needs to be overridden to achieve arbitrary information output
     *
     * @param message 等待记录的消息
     */
    public void BeforeSaveToFile( MessageItem message ){

    }

    public boolean LogThreadID = true;

    public boolean LogStxAsciiCode = true;

    /// <inheritdoc cref="ILogNet.HourDeviation"/>
    public int HourDeviation  = 0;

    public void WriteDebug( String text ) {
        WriteDebug( "", text );
    }

    public void WriteDebug( String keyWord, String text ) {
        RecordMessage(MessageDegree.DEBUG, keyWord, text);
    }

    /**
     * 写入一条解释性的信息
     *
     * @param description 描述信息
     */
    @Override
    public void WriteDescription(String description) {

    }

    public void WriteInfo( String text ) {
        WriteInfo("", text);
    }

    public void WriteInfo( String keyWord, String text ) {
        RecordMessage(MessageDegree.INFO, keyWord, text);
    }

    public void WriteWarn( String text ) {
        WriteWarn( "", text );
    }

    public void WriteWarn( String keyWord, String text ) {
        RecordMessage( MessageDegree.WARN, keyWord, text );
    }

    public void WriteError( String text ) {
        WriteError( "", text );
    }

    public void WriteError( String keyWord, String text ) {
        RecordMessage( MessageDegree.ERROR, keyWord, text );
    }

    public void WriteFatal( String text ) {
        WriteFatal( "", text );
    }

    public void WriteFatal( String keyWord, String text ) {
        RecordMessage( MessageDegree.FATAL, keyWord, text );
    }

    public void WriteException( String keyWord, Exception ex ) {
        WriteException( keyWord, "", ex );
    }

    public void WriteException( String keyWord, String text, Exception ex ) {
        RecordMessage( MessageDegree.FATAL, keyWord, LogNetManagment.GetSaveStringFromException( text, ex ) );
    }

    /**
     * 文件存储模式，1:单文件，2:根据大小，3:根据时间
     *
     * @return 存储模式的值
     */
    @Override
    public int LogSaveMode() {
        return 0;
    }

    public void RecordMessage(MessageDegree degree, String keyWord, String text ) {
        WriteToFile( degree, keyWord, text );
    }

    public void WriteDescrition( String description ) {
        if (Utilities.IsStringNullOrEmpty(description)) return;

        // 和上面的文本之间追加一行空行
        StringBuilder stringBuilder = new StringBuilder("\u0002");
        stringBuilder.append(Environment.NewLine);
        stringBuilder.append("\u0002/");

        int count = 118 - CalculateStringOccupyLength(description);
        if (count >= 8) {
            int count_1 = (count - 8) / 2;
            AppendCharToStringBuilder(stringBuilder, '*', count_1);
            stringBuilder.append("   ");
            stringBuilder.append(description);
            stringBuilder.append("   ");
            if (count % 2 == 0) {
                AppendCharToStringBuilder(stringBuilder, '*', count_1);
            } else {
                AppendCharToStringBuilder(stringBuilder, '*', count_1 + 1);
            }
        } else if (count >= 2) {
            int count_1 = (count - 2) / 2;
            AppendCharToStringBuilder(stringBuilder, '*', count_1);
            stringBuilder.append(description);
            if (count % 2 == 0) {
                AppendCharToStringBuilder(stringBuilder, '*', count_1);
            } else {
                AppendCharToStringBuilder(stringBuilder, '*', count_1 + 1);
            }
        } else {
            stringBuilder.append(description);
        }

        stringBuilder.append("/");
        stringBuilder.append(Environment.NewLine);
        RecordMessage(MessageDegree.None, "", stringBuilder.toString());
    }

    public void WriteAnyString( String text ) {
        RecordMessage( MessageDegree.None, "", text );
    }

    public void WriteNewLine( ){
        RecordMessage( MessageDegree.None, "", "\u0002" + Environment.NewLine );
    }

    public void SetMessageDegree( MessageDegree degree ) {
        m_messageDegree = degree;
    }

    public void FiltrateKeyword( String keyword )
    {
        synchronized (filtrateLock) {
            if (!filtrateKeyword.contains(keyword)) {
                filtrateKeyword.Add(keyword);
            }
        }
    }

    /**
     * 获取已存在的日志文件名称
     *
     * @return
     */
    @Override
    public String[] GetExistLogFileNames() {
        return new String[0];
    }

    public void RemoveFiltrate( String keyword ) {
        synchronized (filtrateLock) {
            if (filtrateKeyword.contains(keyword)) {
                filtrateKeyword.remove(keyword);
            }
        }
    }

    private void WriteToFile(MessageDegree degree, String keyword, String text ) {
        // 过滤事件
        if (degree.ordinal() <= m_messageDegree.ordinal()) {
            // 需要记录数据
            MessageItem item = GetHslMessageItem(degree, keyword, text);

            BeforeSaveToFile(item);
            //AddItemToCache(item, true);
        }
    }

    private void AddItemToCache(MessageItem item, boolean start )
    {

    }

    private MessageItem GetAndRemoveLogItem( ) {
        MessageItem result = null;

        m_simpleHybirdLock.Enter();
        try {
            result = m_WaitForSave.size() > 0 ? m_WaitForSave.poll() : null;
        } catch (Exception ignored) {

        }
        m_simpleHybirdLock.Leave();

        return result;
    }


    private String HslMessageFormate(MessageItem hslMessage, boolean writeFile )
    {
        StringBuilder stringBuilder = new StringBuilder( );
        if (hslMessage.getDegree() != MessageDegree.None)
        {
            if (writeFile && LogStxAsciiCode) stringBuilder.append( "\u0002" );
            stringBuilder.append( "[" );
            stringBuilder.append( LogNetManagment.GetDegreeDescription( hslMessage.getDegree() ) );
            stringBuilder.append( "] " );

            stringBuilder.append( new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(hslMessage.getTime() ) );
            stringBuilder.append( " " );
            if (LogThreadID)
            {
                stringBuilder.append( "Thread:[" );
                stringBuilder.append( String.format( "%03d", hslMessage.getThreadId()) );
                stringBuilder.append( "] " );
            }

            if (!Utilities.IsStringNullOrEmpty( hslMessage.getKeyWord() ))
            {
                stringBuilder.append( hslMessage.getKeyWord() );
                stringBuilder.append( " : " );
            }
        }
        stringBuilder.append( hslMessage.getText() );

        return stringBuilder.toString( );
    }

    // endregion

    // region Object Override

    public String toString( ) {
        return "LogNetBase[" + LogSaveMode() + "]";
    }
		// endregion

    // region Helper Method

    protected String GetFileSaveName( ) {
        return "";
    }

    /**
     * 当写入文件完成的时候触发，这时候已经释放了文件的句柄了。<br />
     * Triggered when writing to the file is complete, and the file handle has been released.
     * @param createNewLogFile 是否创建了新的日志文件
     */
    protected void OnWriteCompleted( boolean createNewLogFile ) { }

    private MessageItem GetHslMessageItem(MessageDegree degree, String keyWord, String text ) {
        MessageItem messageItem = new MessageItem();
        messageItem.setDegree(degree);
        messageItem.setKeyWord(keyWord);
        messageItem.setText(text);
        messageItem.setThreadId((int) Thread.currentThread().getId());
        if (HourDeviation != 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(messageItem.getTime());
            calendar.add(Calendar.HOUR, HourDeviation); // 增加一个小时
            messageItem.setTime(calendar.getTime());
        }
        return messageItem;
    }

    private int CalculateStringOccupyLength( String str ) {
        if (Utilities.IsStringNullOrEmpty(str)) return 0;
        int result = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) >= 0x4e00 && str.charAt(i) <= 0x9fbb) {
                result += 2;
            } else {
                result += 1;
            }
        }
        return result;
    }

    private void AppendCharToStringBuilder( StringBuilder sb, char c, int count )
    {
        for (int i = 0; i < count; i++)
        {
            sb.append( c );
        }
    }

    // endregion
}
