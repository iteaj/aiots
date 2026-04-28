
'use strict';
importScripts(
	'libplayAPI.js',
	'libplay.js'
);

addEventListener('message', receiveMessage, false);	

Module.onRuntimeInitialized = function(){
	m_bLoadSuccess = true;
	var msgType = 'LoadSuccess';
	sendMessage(m_nPlayPort, msgType, null);
}

var m_bPlayback = 0;
var m_bSupportH264MSE = false;
var m_bSupportH265MSE = false;
var m_nCanvasWidth = 1920;
var m_nCanvasHeight = 1080;
var m_nPlayPort = 0;

var jsInputData = null;
var jsInputDataAry = null;

var jsFrameBuf = null;
var jsFrameInfo = null;
let dataView = null;

var jsBuf = null;
var jsFrameBodyData = null;

var jsBufY = null;
var jsBufU = null;
var jsBufV = null;
var jsYuvDataY = null;
var jsYuvDataU = null;
var jsYuvDataV = null;

var jsAudioBuf = null;
var jsAudioData = null;
var jsI16AudioData = null;
var jsF32AudioData = null;

var jsRecordFrameBuf = null;
var	jsRecordFrameInfo = null;
var	dataViewRecord = null;

var jsIvsBuf = null;
var jsIvsData = null;

var m_nWidth = 0;
var m_nHeight = 0;
var m_nPreWidth = 0;
var m_nPreHeight = 0;

var m_bSmartEncode = 0;
var m_nVideoEncodeType = 0;

var m_bLoadSuccess = false;

var m_bDecryptionResult = false;

var m_nTotalStreamLength = 0;

function receiveMessage(event) 
{
	if (!m_bLoadSuccess)
	{
		return;
	}
	var message = event.data;
	switch (message.nType) 
	{
		//初始化
        case 'Init':
			m_bPlayback = message.option.bPlayback;
			m_bSupportH264MSE = message.option.bSupportH264MSE;
			m_bSupportH265MSE = message.option.bSupportH265MSE;
			m_nCanvasWidth = message.option.nCanvasWidth;
			m_nCanvasHeight = message.option.nCanvasHeight;
			Init();
            break;
        //送流
		case 'InputData':
			InputData(message.pData);
            break;
		//打开智能绘制
		case 'OpenIVSDraw':
			OpenIVSDraw();
            break;
		//关闭智能绘制
		case 'CloseIVSDraw':
			CloseIVSDraw();
            break;
		//开始码流录制
		case 'StartRecord':
			StartRecord(message.nRecordType);
            break;
		//停止码流录制
		case 'StopRecord':
			StopRecord();
            break;
		//设置播放速度
		case 'SetPlaySpeed':
			SetPlaySpeed(message.nSpeed);
            break;
		case 'GetOriginalKeyCallBack':
			GetOriginalKey(message.playToken, message.playTokenKey, message.deviceID);
            break;
		case 'SetWebSecurityKey':
			SetWebSecurityKey(message.nDecryptType, message.nFrameType, message.strKey, message.stStreamInfo);
            break;
		//设置解密秘钥
		case 'SetSecurityKey':
			SetSecurityKey(message.nEncryptType, message.szKey, message.nKeyLen, message.szKeyId, message.nKeyIdLen);
            break;
		//设置是否支持硬解码标记
		case 'SetSupportWebMSE':
			m_bSupportH264MSE = message.bSupportH264MSE;
			m_bSupportH265MSE = message.bSupportH265MSE;
			SetSupportWebMSE(m_bSupportH264MSE, m_bSupportH265MSE);
		//暂停播放
		case 'Pause':
			Pause(message.bPause);
            break;
		//抓图
		case 'CatchPic':
			CatchPic();
            break;
		//停止播放
		case 'Stop':
			Stop();
            break;
        default:
            break;
    }
}

function Init() 
{
	//获取PlaySDK空闲端口号
	var jsPort = Module._malloc(1);
	var jsPortAry = new Uint8Array(Module.HEAPU8.buffer, jsPort, 1);
	Module._PLAY_GetFreePort(jsPortAry.byteOffset);
	m_nPlayPort = jsPortAry[0];
	jsPortAry = null;
	Module._free(jsPort);
	
	//设置播放窗口宽高属性
	Module._PLAY_ViewResolutionChanged(m_nPlayPort, m_nCanvasWidth, m_nCanvasHeight, 0);
	
	//设置实时流模式
	var nRet = Module._PLAY_SetStreamOpenMode(m_nPlayPort, m_bPlayback);
	nRet = Module._PLAY_OpenStream(m_nPlayPort, 0, 0, 10*1024*1024);
	
	nRet = Module._PLAY_SetSupportWebMSE(m_nPlayPort, m_bSupportH264MSE, m_bSupportH265MSE);
	
	nRet = Module._PLAY_Play(m_nPlayPort, 1);
	if (nRet)
	{
		//向C++层申请一块wasm内存，用于接收码流
		jsInputData = Module._malloc(5*1024*1024);
		jsInputDataAry = new Uint8Array(Module.HEAPU8.buffer, jsInputData, 5*1024*1024);
		
		var msgType = 'InitSuccess';
		sendMessage(m_nPlayPort, msgType, null);
	}
}

function InputData(data) 
{
	m_nTotalStreamLength += data.length;
	
	if(jsInputDataAry) 
	{
		jsInputDataAry.set(data);
		var nRet = Module._PLAY_InputData(m_nPlayPort, jsInputDataAry.byteOffset, data.length); 
		var i = 0;
	}
}

function OpenIVSDraw() 
{
	Module._PLAY_RenderPrivateData(m_nPlayPort, 1, 0);
}

function CloseIVSDraw() 
{
	Module._PLAY_RenderPrivateData(m_nPlayPort, 0, 0);
}

function StartRecord(nRecordType) 
{
	Module._PLAY_StartDataRecord(m_nPlayPort, 0, nRecordType);
}

function StopRecord() 
{
	Module._PLAY_StopDataRecord(m_nPlayPort);
}

function SetPlaySpeed(nSpeed)
{
	Module._PLAY_SetPlaySpeed(m_nPlayPort, nSpeed);
}

function GetOriginalKey(playToken, playTokenKey, deviceID)
{
	var arrPlayToken = Module.intArrayFromString(playToken).concat(0);//add '\0'
	var playTokenPtr = Module._malloc(arrPlayToken.length);//采用声明的c函数 _malloc
	Module.HEAPU8.set(arrPlayToken, playTokenPtr);//复制字符串内容
	
	var arrPlayTokenKey = Module.intArrayFromString(playTokenKey).concat(0);
	var playTokenKeyPtr = Module._malloc(arrPlayTokenKey.length);
	Module.HEAPU8.set(arrPlayTokenKey, playTokenKeyPtr);
	
	var arrDeviceID = Module.intArrayFromString(deviceID).concat(0); 
	var deviceIDPtr = Module._malloc(arrDeviceID.length);  
	Module.HEAPU8.set(arrDeviceID, deviceIDPtr); 

	var outKeyPtr = Module._malloc(256);
	var outKeyLengthPtr = Module._malloc(4);	

	var nRet = Module._PLAY_GetOriginalKey(m_nPlayPort, playTokenPtr, playTokenKeyPtr, deviceIDPtr, outKeyPtr, outKeyLengthPtr);
	
	var outKeyLength = Module.HEAP32[outKeyLengthPtr >>2];
	var outKeyTmp = "";
	
	if ((1 == nRet) && (outKeyLength <= 256))
	{
		var jsKeyBuf = new ArrayBuffer(outKeyLength);
		var jsKeyData = new Uint8Array(jsKeyBuf);
		jsKeyData.set(Module.HEAPU8.subarray(outKeyPtr, outKeyPtr + outKeyLength));
		outKeyTmp = ArrayBufferToString(jsKeyBuf);
	}
	
	Module._free(playTokenPtr);//释放内存
	Module._free(playTokenKeyPtr);
	Module._free(deviceIDPtr);
	Module._free(outKeyPtr);
	Module._free(outKeyLengthPtr);
	
	var outKeyParam = 
	{
		nRet: nRet, 
		outKey: outKeyTmp,
	};
	
	var msgType = 'GetOriginalKeyCallBack';
	var msgData =  
	{	
		nRet: nRet,
		outKey: outKeyTmp,
	}
	
	sendMessage(m_nPlayPort, msgType, msgData);
}

function SetWebSecurityKey(nDecryptType, nFrameType, strKey, stStreamInfo)
{
	var arrStrKey = Module.intArrayFromString(strKey).concat(0);
	var strKeyPtr = Module._malloc(arrStrKey.length);
	Module.HEAPU8.set(arrStrKey, strKeyPtr);	
	
	var arrSdpInfo = Module.intArrayFromString(stStreamInfo.sdpInfo).concat(0); 
	var sdpInfoPtr = Module._malloc(arrSdpInfo.length);  
	Module.HEAPU8.set(arrSdpInfo, sdpInfoPtr);  

	var arrUserName = Module.intArrayFromString(stStreamInfo.strUserName).concat(0); 
	var userNamePtr = Module._malloc(arrUserName.length);  
	Module.HEAPU8.set(arrUserName, userNamePtr); 		

	var arrPassWord = Module.intArrayFromString(stStreamInfo.strPassWord).concat(0); 
	var passWordPtr = Module._malloc(arrPassWord.length);  
	Module.HEAPU8.set(arrPassWord, passWordPtr);   

	const stStreamInfoPtr = Module._malloc(16);//4:uint32的字节大小  	
	Module.HEAP32[stStreamInfoPtr/4 + 0] = sdpInfoPtr; 
    Module.HEAP32[stStreamInfoPtr/4 + 1] = userNamePtr;  
    Module.HEAP32[stStreamInfoPtr/4 + 2] = passWordPtr; 
    Module.HEAP32[stStreamInfoPtr/4 + 3] = stStreamInfo.nSsrc; 
	
	Module._PLAY_SetWebSecurityKey(m_nPlayPort, nDecryptType, nFrameType, strKeyPtr, stStreamInfoPtr);
	
	Module._free(strKeyPtr);//释放内存
	Module._free(sdpInfoPtr);
	Module._free(userNamePtr);
	Module._free(passWordPtr);
	Module._free(stStreamInfoPtr);
}

function SetSecurityKey(nEncryptType, szKey, nKeyLen, szKeyId, nKeyIdLen)
{
	var nRet = 1;
	
	var strKey = Module._malloc(49);
	var arrayKey = new Uint8Array(Module.HEAPU8.buffer);
	var nSequence = 0;
	
	if (ENCRYPT_AES == nEncryptType)
	{
		//逐字节的形式去内存中设置值，直接修改wasm内存数据
		szKey.forEach((value, nIndex) => {
			arrayKey[(strKey + nSequence) >> 0] = value;
			nSequence++;
		});
	}
	else if(ENCRYPT_AES256 == nEncryptType)
	{
		var szKeyIdTmp = new Uint8Array(16); ;

		//协议规定ENCRYPT_AES256对应的枚举值为1
		arrayKey[(strKey + nSequence) >> 0] = 1;
		nSequence++;
		if (0 == nKeyIdLen)
		{
			for(var i = 0; i < 16; i++)
			{
				szKeyIdTmp[i] = 0x00;
			}
			nKeyIdLen = 16;
			szKeyId = szKeyIdTmp;
		}
		
		//拼接上key ID，逐字节的形式去内存中设置值，直接修改wasm内存数据
		szKeyId.forEach((value, nIndex) => {
			arrayKey[(strKey + nSequence) >> 0] = value;
			nSequence++;
		});
		
		//拼接上key，逐字节的形式去内存中设置值，直接修改wasm内存数据
		szKey.forEach((value, nIndex) => {
			arrayKey[strKey + nSequence] = value;
			nSequence++;
		});
		
		nKeyLen = 1 + nKeyLen + nKeyIdLen;
		szKeyIdTmp = null;
	}
	else if(ENCRYPT_AES_STRING_FORMAT == nEncryptType)
	{
		szKey.split('').forEach((char, nIndex) => {
			arrayKey[(strKey + nSequence) >> 0] = char.charCodeAt(0);
			nSequence++;
		});
	}
	nRet = Module._PLAY_SetSecurityKey(m_nPlayPort, strKey, nKeyLen);
	
	Module._free(strKey);
	
	return nRet;
}

function SetSupportWebMSE(bSupportH264MSE, bSupportH265MSE)
{
	Module._PLAY_SetSupportWebMSE(m_nPlayPort, bSupportH264MSE, bSupportH265MSE);
}

function Pause(bPause) 
{
	Module._PLAY_Pause(m_nPlayPort, bPause);
}

function CatchPic()
{
	var nSize = m_nWidth * m_nHeight * 3/2;
	var pJpegBuf = Module._malloc(nSize);
	var pJpegBufArr = new Uint8Array(Module.HEAPU8.buffer, pJpegBuf, nSize);
	
	var pJpegSize = Module._malloc(4);
	var pJpegSizeArr = new Uint8Array(Module.HEAPU8.buffer, pJpegSize, 4);
	
	//获取当前图像编码后的jpeg图片数据
	Module._PLAY_GetPicJPEG(m_nPlayPort, pJpegBufArr.byteOffset, nSize, pJpegSizeArr.byteOffset, 100);
	
	//C++内存数据拷贝至JS内存
	var nDataSize = (pJpegSizeArr[3] << 24) + (pJpegSizeArr[2] << 16) + (pJpegSizeArr[1] << 8) + pJpegSizeArr[0];
	var pOutJpegBuf = new ArrayBuffer(nDataSize);
	var pOutJpegBufArr = new Uint8Array(pOutJpegBuf);
	pOutJpegBufArr.set(Module.HEAPU8.subarray(pJpegBufArr.byteOffset, pJpegBufArr.byteOffset + nDataSize));
	
	//写入至文件
	var blob = new Blob([pOutJpegBufArr.buffer], {type: "image/jpg"});
	var msgType = 'CatchPicCallBack';
	var msgData =  
	{	
		blob: blob,
	}
	
	sendMessage(m_nPlayPort, msgType, msgData);
	
	blob = null;
	Module._free(pJpegBuf);
	Module._free(pJpegSize);
	pJpegBufArr = null;
	pJpegSizeArr = null;
	pOutJpegBuf= null;
	pOutJpegBufArr = null;
	
}

function Stop() 
{
	var nRet = Module._PLAY_Stop(m_nPlayPort);
	if (0 == nRet)
	{
		return;
	}
	nRet = Module._PLAY_CloseStream(m_nPlayPort); 
	
	jsInputDataAry = null;
	Module._free(jsInputData);
	
	jsFrameBuf = null;
	jsFrameInfo = null;
	dataView = null;
	
	jsBufY = null;
	jsBufU = null;
	jsBufV = null;
	jsYuvDataY = null;
	jsYuvDataU = null;
	jsYuvDataV = null;
	
	jsRecordFrameBuf = null;
	jsRecordFrameInfo = null;
	dataViewRecord = null;
	
	m_nTotalStreamLength = 0;
}

function cPlusVisibleDecCallBack(nPort, pBufY, pBufU, pBufV, nSize, pFrameInfo)
{
	var stuFrameInfo = {};
	
	stuFrameInfo.nTotalStreamLength = m_nTotalStreamLength;
	
	if(!jsFrameInfo) 
	{
		jsFrameBuf = new ArrayBuffer(292);//通过二进制对象分配一块连续内存
		jsFrameInfo = new Uint8Array(jsFrameBuf);//二进制对象绑定到视图，通过视图对内存进行读写操作
		dataView = new DataView(jsFrameBuf);
	}
	jsFrameInfo.set(Module.HEAPU8.subarray(pFrameInfo, pFrameInfo + 292));//c中的内存拷贝到刚分配的js内存中
	//帧类型
	stuFrameInfo.nFrameType = dataView.getInt32(0, true);
	//帧序号
	stuFrameInfo.nFrameID = dataView.getInt32(4, true);
	//帧子类型
	stuFrameInfo.nFrameSubType = dataView.getInt32(56, true);
	
	//帧时间
	stuFrameInfo.nYear = dataView.getUint16(40, true);
	stuFrameInfo.nMonth = dataView.getUint16(42, true);
	stuFrameInfo.nDay = dataView.getUint16(46, true);
	stuFrameInfo.nHour = dataView.getUint16(48, true);
	stuFrameInfo.nMinute = dataView.getUint16(50, true);
	stuFrameInfo.nSecond = dataView.getUint16(52, true);
	
	var msgData =  {};
	//视频
	if (1 == stuFrameInfo.nFrameType)
	{	
		//剩余缓冲数据量
		stuFrameInfo.nRemainData = dataView.getInt32(36, true);
		//抽帧标记
		stuFrameInfo.bThrowFrame = dataView.getUint8(120, true);
		if (0 == stuFrameInfo.bThrowFrame)
		{
			//编码类型
			stuFrameInfo.nEncodeType = dataView.getInt32(108, true);
			//码流类型
			stuFrameInfo.nStreamType = dataView.getInt32(112, true);
			//时间戳
			stuFrameInfo.nTimeStamp = dataView.getUint32(8, true);
			//图像宽度
			stuFrameInfo.nWidth = dataView.getInt32(12, true);
			m_nWidth = stuFrameInfo.nWidth;
			//图像高度
			stuFrameInfo.nHeight = dataView.getInt32(16, true);
			m_nHeight = stuFrameInfo.nHeight;
			//视频帧率
			stuFrameInfo.nFrameRate = dataView.getInt32(20, true);
			//图像跨距
			stuFrameInfo.nStride = dataView.getInt32(116, true);
			
			if ((ENCODE_TYPE_VIDEO_HI_H264 == stuFrameInfo.nEncodeType) || (ENCODE_TYPE_VIDEO_MY_H264 == stuFrameInfo.nEncodeType) || (ENCODE_TYPE_VIDEO_STD_H264 == stuFrameInfo.nEncodeType))
			{
				//H.264编码类型
				m_nVideoEncodeType = 1;
			}
			else if(12 == stuFrameInfo.nEncodeType)
			{
				//H.265编码类型
				m_nVideoEncodeType = 2;
			}
			
			//智能I/P帧
			if ((VIDEO_FRAME_SUB_TYPE_SMART_I == stuFrameInfo.nFrameSubType) 
				|| (VIDEO_FRAME_SUB_TYPE_SMART_P == stuFrameInfo.nFrameSubType)
				|| (VIDEO_FRAME_SUB_TYPE_SMART_I_NORENDER == stuFrameInfo.nFrameSubType))
			{
				//Smart H.264或者Smart H.265
				m_bSmartEncode = 1;
			}
			else if(0 == stuFrameInfo.nFrameSubType)
			{
				m_bSmartEncode = 0;
			}
			
			//非Smart H.264/H.265码流，若浏览器支持MSE硬解码则采用硬解码
			//SVC码流不支持硬解码
			if ((((1 == m_nVideoEncodeType) && (true == m_bSupportH264MSE)) 
				|| ((2 == m_nVideoEncodeType) && (true == m_bSupportH265MSE))) 
				&& !m_bSmartEncode
				&& (STREAM_TYPE_SVC != stuFrameInfo.nStreamType))
			{
				//读取码流裸数据
				jsBuf = new ArrayBuffer(nSize);//通过二进制对象分配一块连续内存
				jsFrameBodyData = new Uint8Array(jsBuf);//二进制对象绑定到视图，通过视图对内存进行读写操作
				jsFrameBodyData.set(Module.HEAPU8.subarray(pBufY, pBufY + nSize));//c中的内存拷贝到刚分配的js内存中
				
				msgData =  
				{	
					pBufY: jsFrameBodyData, 
					pBufU: null,
					pBufV: null, 
					nSize: nSize,
					stuFrameInfo: stuFrameInfo,
				}
			}
			else
			{
				if((0 == pBufY) || (0 == pBufU) || (0 == pBufV))
				{
					return;
				}
				
				if (m_nWidth != m_nPreWidth || m_nHeight != m_nPreHeight)
				{
					m_nPreWidth = m_nWidth;
					m_nPreHeight = m_nHeight;
					
					jsBufY = null;
					jsBufU = null;
					jsBufV = null;
					jsYuvDataY = null;
					jsYuvDataU = null;
					jsYuvDataV = null;
					
					jsBufY = new ArrayBuffer(m_nWidth * m_nHeight);//通过二进制对象分配一块连续内存
					jsYuvDataY = new Uint8Array(jsBufY);//二进制对象绑定到视图，通过视图对内存进行读写操作
						
					jsBufU = new ArrayBuffer(m_nWidth * m_nHeight / 4);
					jsYuvDataU = new Uint8Array(jsBufU);
						
					jsBufV = new ArrayBuffer(m_nWidth * m_nHeight / 4);
					jsYuvDataV = new Uint8Array(jsBufV);
				}
				
				var h = 0;
				//将C++层YUV解码数据Y分量数据拷贝至JS层内存中	
				for(h = 0; h < stuFrameInfo.nHeight; h++)
				{
					jsYuvDataY.set(Module.HEAPU8.subarray((pBufY + h * stuFrameInfo.nStride), (pBufY + h * stuFrameInfo.nStride) + stuFrameInfo.nWidth), h * stuFrameInfo.nWidth);//c中的内存拷贝到刚分配的js内存中
				}
				//将C++层YUV解码数据U分量数据拷贝至JS层内存中		
				for(h = 0; h < stuFrameInfo.nHeight / 2; h++)
				{
					jsYuvDataU.set(Module.HEAPU8.subarray((pBufU + h * stuFrameInfo.nStride/2), (pBufU + h * stuFrameInfo.nStride/2) + stuFrameInfo.nWidth/2), h * stuFrameInfo.nWidth/2);//c中的内存拷贝到刚分配的js内存中
				}
				//将C++层YUV解码数据V分量数据拷贝至JS层内存中		
				for(h = 0; h < stuFrameInfo.nHeight / 2; h++)
				{
					jsYuvDataV.set(Module.HEAPU8.subarray((pBufV + h * stuFrameInfo.nStride/2), (pBufV + h * stuFrameInfo.nStride/2) + stuFrameInfo.nWidth/2), h * stuFrameInfo.nWidth/2);//c中的内存拷贝到刚分配的js内存中
				}
				
				msgData =  
				{	
					pBufY: jsYuvDataY,
					pBufU: jsYuvDataU,
					pBufV: jsYuvDataV, 
					nSize: nSize,
					stuFrameInfo: stuFrameInfo,
				}
			}
		}
		else
		{
			msgData =  
			{	
				pBufY: null,
				pBufU: null,
				pBufV: null, 
				nSize: 0,
				stuFrameInfo: stuFrameInfo,
			}
		}
	}
	else if(2 == stuFrameInfo.nFrameType)//音频帧
	{
		//总通道数
		stuFrameInfo.nTotalChannel = dataView.getInt32(68, true);
		//当前通道
		stuFrameInfo.nCurChannel = dataView.getInt32(72, true);
		//暂不支持双通道音频播放
		if (stuFrameInfo.nCurChannel > 0)
		{
			return;
		}
		//采样位数
		stuFrameInfo.nBits = dataView.getInt32(28, true);
		//采样率
		stuFrameInfo.nSamples = dataView.getInt32(32, true);
		//声道数
		stuFrameInfo.nAudioChnNum = dataView.getInt32(24, true);
		
		jsAudioBuf = new ArrayBuffer(nSize);
		jsAudioData = new Uint8Array(jsAudioBuf);
		//将C++层解码后的pcm音频数据拷贝至JS层内存
        jsAudioData.set(Module.HEAPU8.subarray(pBufY, pBufY + nSize));
		
		//Uint8Array转Int16Array
		jsI16AudioData = new Int16Array(jsAudioData.buffer, jsAudioData.byteOffset, jsAudioData.byteLength / Int16Array.BYTES_PER_ELEMENT);
		//Int16Array转Float32Array
		jsF32AudioData = new Float32Array(jsI16AudioData.length);
		for(var idx = 0; idx < jsI16AudioData.length; idx++) 
		{
			jsF32AudioData[idx] = jsI16AudioData[idx] / Math.pow(2, 15);
		}
		
		msgData =  
		{	
			pBufY: jsF32AudioData,
			pBufU: null,
			pBufV: null, 
			nSize: nSize,
			stuFrameInfo: stuFrameInfo,
		}
	}
	
	var msgType = 'VisibleDecCallBack';
	
	sendMessage(nPort, msgType, msgData);
	
	jsBuf = null;
	jsFrameBodyData = null;
	jsAudioBuf = null;
    jsAudioData = null;
	jsI16AudioData = null;
    jsF32AudioData = null;
}

/*
 * C++层AES解密回调。
 *
 * @param[in] nPort 端口号
 * @param[in] nFrameID 视频帧序号
 * @param[in] bSuccess 是否解密成功
 */
function cDigitalSignCallBack(nPort, nFrameID, bSuccess)
{
	m_bDecryptionResult = bSuccess;
	var msgType = 'DecryptionResultCallBack';
	var msgData =  
	{	
		bSuccess: bSuccess
	}
	
	sendMessage(nPort, msgType, msgData);
}

/*
 * C++层码流录制回调，回调至JS层进行数据存储
 *
 * @param[in] nPort 端口号
 * @param[in] pData 码流数据
 * @param[in] nDataLen 数据长度
 * @param[in] nOffset 偏移量
 * @param[in] pFrameInfo 码流信息
 */
function cRecordDataCallBack(nPort, pData, nDataLen, nOffset, pFrameInfo) 
{
	var stuFrameInfo = {};
	if (!jsRecordFrameInfo) 
	{
		jsRecordFrameBuf = new ArrayBuffer(292); //通过二进制对象分配一块连续内存
		jsRecordFrameInfo = new Uint8Array(jsRecordFrameBuf); //二进制对象绑定到视图，通过视图对内存进行读写操作
		dataViewRecord = new DataView(jsRecordFrameBuf);
	}
	jsRecordFrameInfo.set(Module.HEAPU8.subarray(pFrameInfo, pFrameInfo + 292)); //c中的内存拷贝到刚分配的js内存中
	
	//帧类型
	stuFrameInfo.nFrameType = dataViewRecord.getInt32(0, true);
	//帧序号
	stuFrameInfo.nFrameID = dataViewRecord.getInt32(4, true);
	//帧子类型
	stuFrameInfo.nFrameSubType = dataViewRecord.getInt32(56, true);
	
	//视频帧
	if (1 == stuFrameInfo.nFrameType) 
	{
		//编码类型
		stuFrameInfo.nEncodeType = dataViewRecord.getInt32(68, true);
		//码流类型
		stuFrameInfo.nStreamType = dataViewRecord.getInt32(72, true);
		
		//时间戳
		stuFrameInfo.nTimeStamp = dataViewRecord.getUint32(8, true);
		//帧时间
		stuFrameInfo.nYear = dataViewRecord.getUint16(40, true);
		stuFrameInfo.nMonth = dataViewRecord.getUint16(42, true);
		stuFrameInfo.nDay = dataViewRecord.getUint16(46, true);
		stuFrameInfo.nHour = dataViewRecord.getUint16(48, true);
		stuFrameInfo.nMinute = dataViewRecord.getUint16(50, true);
		stuFrameInfo.nSecond = dataViewRecord.getUint16(52, true);
	}
	
	var bufRecord = new ArrayBuffer(nDataLen);
	var arrayRecord = new Uint8Array(bufRecord);  
	arrayRecord.set(Module.HEAPU8.subarray(pData, pData + nDataLen));
	
	var msgType = 'RecordDataCallBack';
	var msgData =  
	{	
		pRecordData: arrayRecord,
		nLen: nDataLen,
		Offset: nOffset, 
		stuFrameInfo: stuFrameInfo,
	}
	sendMessage(nPort, msgType, msgData);
	
	bufRecord = null;
	arrayRecord = null;
}

function cIVSDrawDataCallBack(nPort, buf, type, len, reallen)
{
	jsIvsBuf = new ArrayBuffer(len);//通过二进制对象分配一块连续内存
	jsIvsData = new Uint8Array(jsIvsBuf);//二进制对象绑定到视图，通过视图对内存进行读写操作
	
	//将C++层解码后的pcm音频数据拷贝至JS层内存
	jsIvsData.set(Module.HEAPU8.subarray(buf, buf + len));
	
	var msgType = 'IVSDataCallBack';
	var msgData =  
	{	
		pBuf: jsIvsData,
		nType: type,
		nLen: len, 
		nReallen: reallen,
	}
	
	sendMessage(nPort, msgType, msgData);
}

function sendMessage(nPort, msgType, msgData) 
{
	var event = 
	{
		nPort: nPort,
		msgType: msgType,
		msgData: msgData,
	};

    postMessage(event);
}

function ArrayBufferToString(buffer, encoding = 'utf-8') 
{
	const decoder = new TextDecoder(encoding);
	return decoder.decode(buffer);
}

