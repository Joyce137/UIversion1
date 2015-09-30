package com.joyce.login;

import java.io.UnsupportedEncodingException;

import com.joyce.login.PackHead;
import com.joyce.login.Utils;

import android.graphics.Color;
import android.util.Log;

//定义静态类型常量
	//#define
class Types {
	public static final String AES_KEY = "Wsn406";
	public static final int center_Port = 1001;
	public static final int version_Port = 1002;
	public static final String version_IP = "114.214.166.134";
	public static final int FileFree = 500000; 				//文件指针定时器 释放时间 一秒为1000
	public static final int MAX_PACKBUFFER_SIZE = 8*1024;	//包体大小
	public static final int PACK_START_FLAG = 0x9202; 		//包头标志
	public static final int FILE_MAX_BAG = 1000;			//当发送文件的时候 每个包发送多少个字符
	public static final int HEADPACK = 8;
	public static final int LoginUP = 0x0800;  				//登陆信息 密码和账号
	public static final int MY_MSG = 0x0805; 	//postthread
	public static final int Reg_is = 0x0708; 	//注册 服务器返回的消息
	public static final int INFONOYES = 0x0700; // 一些验证消息之类的标志	
	public static final int HeartBeat = 0x0705; //心跳包
	public static final int Login_is = 0x0706;  //登陆后  服务器返回的消息
	public static final int REQ_USER_INFO = 0x0728;
	public static final int RECONNTECT = 0x0729;
	public static final int ForwardInfo = 0x0730;
	public static final int Mod_Pass = 0x0731;
	public static final int Mod_Info = 0x0732;
	public static final int USER_TYPE_STORE = 0x0001;
	public static final int USER_LOGIN_FLAG = 0x0739;		//初始页面登录
	public static final int USER_ONLINE_FLAG = 0x0740;		//主页面内上线
	public static final int USER_RECONNECT_FLAG = 0x0741;	//断线重连
	public static final int BACKCOLOR = Color.rgb(241,243,243);
	public static final int BangDing = 0x750;
	public static final int To_User = 0x0712;		//请求链接
	public static final int Off_Link = 0x0709;		// 断开会话
};

//sockets
class Sockets{
	public static final TCPSocket socket_center = new TCPSocket();
}

//ERRORNO错误标志
class ErrorNo{
	public static final int SOCKET_NULL = 0X0301;
	public static final int WSAEWOULDBLOCK = 0x0302;
	public static final int DATA_NULL = 0x0303;
	public static final int MINUSVALUE = 0x0304;
}

//struct BangDingPha
class BangDingPha{
	private String username;		//char[15];
	private String pha; 			//char[15];
	private boolean yesno;
	public BangDingPha(){
		username = "";
		pha = "";
		yesno = false;
	}
	
	//与byte数组间的转换
	public static int size = 15+15+1;
	private byte buf[] = new byte[size];
	//构造并转换为byte数组
	public BangDingPha(String username,String pha, boolean yesno){
		this.username = username;
		this.pha = pha;
		this.yesno = yesno;
			
		byte[] temp;
		try {
			//username
			temp = username.getBytes("GBK");
			System.arraycopy(temp, 0, buf, 0, temp.length);
			//pha
			temp = username.getBytes();
			System.arraycopy(temp, 0, buf, 0, temp.length);
			//yesno
			byte bool_byte = (byte) (yesno==true? 0x01:0x00);
		    buf[20] = bool_byte;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}	
	}
	//通过byte数组获取相应的对象参数
	public static BangDingPha getBangDingPhaInfo(byte[] buf){
		String username = "";
		String pha = "";
		boolean yesno = false;
		
		try {
			//username
			byte[] tempStr = new byte[15];
			System.arraycopy(buf, 0, tempStr, 0, 15);
			username = new String(tempStr, "GBK");
			
			//pha
			System.arraycopy(buf, 15, tempStr, 0, 15);
			pha = new String(tempStr, "GBK");
			
			//yesno
			byte yesnoByte = buf[30];
			yesno = (yesnoByte == 0x00) ? false : true;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return new BangDingPha(username, pha, yesno);
	}
	
	//返回要发送的byte数组
	public byte[] getBuf(){
		return buf;
	}
};

//struct PackHead
	//WORD m_Start
class PackHead{
	public int getM_Start() {
		return m_Start;
	}

	public void setM_Start(int m_Start) {
		this.m_Start = m_Start;
	}

	public int getM_Crc() {
		return m_Crc;
	}

	public void setM_Crc(int m_Crc) {
		this.m_Crc = m_Crc;
	}

	public int getnDataLen() {
		return nDataLen;
	}

	public void setnDataLen(int nDataLen) {
		this.nDataLen = nDataLen;
	}

	public int getM_nFlag() {
		return m_nFlag;
	}

	public void setM_nFlag(int m_nFlag) {
		this.m_nFlag = m_nFlag;
	}

	private int m_Start;		//包头标志
	private int m_Crc;
	private int nDataLen;
	private int m_nFlag;
	
	public PackHead(){
		m_Start = Types.PACK_START_FLAG;
		m_Crc = -1;
		m_nFlag = 0;
		nDataLen = 0;
	}
	
	public static int size = 2+2+2+2;
	private byte buf[] = new byte[size];
	    
	 //构造并转化
	public PackHead(int m_Start,int m_Crc,int nDataLen,int m_nFlag){
		 this.m_Start = m_Start;
		 this.m_Crc = m_Crc;
		 this.nDataLen = nDataLen;
		 this.m_nFlag = m_nFlag;
	        
		 //m_Start(2)
		 byte[] temp = Utils.shortToLH(m_Start);
		 System.arraycopy(temp, 0, buf, 0, temp.length);
	        
		 //m_Start(2)
		 temp = Utils.shortToLH(m_Crc);
		 System.arraycopy(temp, 0, buf, 2, temp.length);
	        
		 //nDataLen(2)
		 temp = Utils.shortToLH(nDataLen);
		 System.arraycopy(temp, 0, buf, 4, temp.length);
	        
		 //m_nFlag(2)
		 temp = Utils.shortToLH(m_nFlag);
		 System.arraycopy(temp, 0, buf, 6, temp.length);
	 }
	    
	//ͨbyte数组还原为对象
	public static PackHead getPackHeadInfo(byte[] buf){
		int m_Start = Types.PACK_START_FLAG;;
		int m_Crc = -1;
		int nDataLen = 0;
		int m_nFlag = 0;
	        
		byte[] temp = new byte[4];
		//m_Start
		System.arraycopy(buf, 0, temp, 0, 2);
		m_Start = Utils.vtolh(temp);
		
		//m_Crc
		System.arraycopy(buf, 2, temp, 0, 2);
		m_Crc = Utils.vtolh(temp);
		
		//nDatalen
		System.arraycopy(buf, 4, temp, 0, 2);
		nDataLen = Utils.vtolh(temp);
		
		//m_nFlag
		System.arraycopy(buf, 6, temp, 0, 2);
		m_nFlag = Utils.vtolh(temp);
		
		return new PackHead(m_Start, m_Crc, nDataLen, m_nFlag);
	}

	//返回要发送的byte数组
	public byte[] getBuf(){
		return buf;
	}
};

//struct NET_PACK						// 网络包
class NET_PACK{
	private int m_Start;		//包头标志
	private int m_Crc;
	private int nDataLen;
	private int m_nFlag;
	private byte[] m_buffer;

	public byte[] getM_buffer() {
		return m_buffer;
	}

	public void setM_buffer(byte[] m_buffer) {
		this.m_buffer = m_buffer;
	}

	public NET_PACK(){
		Reset();
	}

	public int getM_Start() {
		return m_Start;
	}

	public void setM_Start(int m_Start) {
		this.m_Start = m_Start;
	}

	public int getM_Crc() {
		return m_Crc;
	}

	public void setM_Crc(int m_Crc) {
		this.m_Crc = m_Crc;
	}

	public int getnDataLen() {
		return nDataLen;
	}

	public void setnDataLen(int nDataLen) {
		this.nDataLen = nDataLen;
	}

	public int getM_nFlag() {
		return m_nFlag;
	}

	public void setM_nFlag(int m_nFlag) {
		this.m_nFlag = m_nFlag;
	}

	public void Reset() {
		m_Start = Types.PACK_START_FLAG;
		m_Crc = -1;	
		nDataLen = 0;
		m_nFlag = 0;
	}
	
	//除了数据以外的信息size
	public static int infoSize = 2+2+2+2;
	//该pack总size
	public int size = infoSize;
	private byte buf[];
	
	//构造并转化
	public NET_PACK(int m_Crc, int nDataLen, int m_nFlag, byte[] m_buffer){
		this.m_Start = Types.PACK_START_FLAG;
		this.m_Crc = m_Crc;
		this.nDataLen = nDataLen;
		this.m_nFlag = m_nFlag;
		this.m_buffer = m_buffer;
		
		int truesize = infoSize + m_buffer.length;
		buf = new byte[truesize];
		
		//m_Start(2)
		byte[] temp = Utils.shortToLH(m_Start);
		System.arraycopy(temp, 0, buf, 0, temp.length);
	        
		//m_Crc(2)
		temp = Utils.shortToLH(m_Crc);
		System.arraycopy(temp, 0, buf, 2, temp.length);
	        
		//nDataLen(2)
		temp = Utils.shortToLH(nDataLen);
		System.arraycopy(temp, 0, buf, 4, temp.length);
	        
		//m_nFlag(2)
		temp = Utils.shortToLH(m_nFlag);
		System.arraycopy(temp, 0, buf, 6, temp.length);
		 
		//m_buffer
		System.arraycopy(m_buffer, 0, buf, 8, m_buffer.length);
	}
	//byte数组转化为类对象
	public static NET_PACK getNET_PACKInfo(byte[] buf){
		int m_Start = Types.PACK_START_FLAG;
		int m_Crc = -1;	
		int nDataLen = 0;
		int m_nFlag = 0;
		byte[] m_buffer;
		
		byte[] temp = new byte[4];
		//m_Start
		System.arraycopy(buf, 0, temp, 0, 2);
		m_Start = Utils.vtolh(temp);
		
		//m_Crc
		System.arraycopy(buf, 2, temp, 0, 2);
		m_Crc = Utils.vtolh(temp);
		
		//nDatalen
		System.arraycopy(buf, 4, temp, 0, 2);
		nDataLen = Utils.vtolh(temp);
		
		//m_nFlag
		System.arraycopy(buf, 6, temp, 0, 2);
		m_nFlag = Utils.vtolh(temp);
	
		//m_buffer
		int len = buf.length - 8;
		m_buffer = new byte[len];
		System.arraycopy(buf, 8, m_buffer, 0, len);
	
		return new NET_PACK(m_Crc, nDataLen, m_nFlag, m_buffer);
	}
	
	//返回要发送的byte数组
	public byte[] getBuf(){
		return buf;
	}
	
	//按byte加在一起，模65536，返回一个0-65535的数据设置成m_Crc
	public void CalCRC(){
		int sum = 0;
		for(int i = 0;i<nDataLen;i++){
			sum += m_buffer[i];
		}
		this.m_Crc = sum % 65536;
		byte[] temp = Utils.shortToLH(m_Crc);
		Log.e("CalCRC", "temp"+temp.length);
		System.arraycopy(temp, 0, this.buf, 2, temp.length);
	}
	
	public int VerifyCRC(){
		int sum = 0;
		Log.e("VerifyCRC", "nDataLen"+nDataLen);
		for(int i = 0;i<nDataLen;i++){
			sum += m_buffer[i];
		}
		return sum % 65536;
	}
};

//struct UserLogin
class UserLogin{
	public byte[] getKey() {
		return key;
	}

	public void setKey(byte[] key) {
		this.key = key;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}


	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getRecon() {
		return recon;
	}

	public void setRecon(int recon) {
		this.recon = recon;
	}

	private String username;		//char[20];
	private byte[] key = new byte[20];			//char[20];
	private int type;
	private int recon;
	
	public UserLogin(){
		username = "";
		for(int i = 0;i<key.length;i++){
			key[i] = 0;
		}
		type = -1;
		recon = 0;
	}
	
	public static int size = 20+20+4+4;
	private byte[] buf= new byte[size];
	//构造并转化
	public UserLogin(String username, byte[] key, int type, int recon){
		this.username = username;
		this.key = key;
		this.type = type;
		this.recon = recon;
		
		byte temp[];
		try {
			//username
			temp = username.getBytes("GBK");
			System.arraycopy(temp, 0, buf, 0, temp.length);
			//key
			temp = key;
			System.arraycopy(temp, 0, buf, 20, temp.length);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}	
		
		//type
		temp = Utils.toLH(type);
		System.arraycopy(temp, 0, buf, 40, temp.length);
		
		//recon
		temp = Utils.toLH(recon);
		System.arraycopy(temp, 0, buf, 44, temp.length);
	}
	
	//byte数组转化为类对象
	public static UserLogin getUserLoginInfo(byte[] buf){
		String username = "";
		byte key[] = new byte[20];
		int type = 0;
		int recon = 0;
		
		try {
			//username
			byte[] tempStr20 = new byte[20];
			System.arraycopy(buf, 0, tempStr20, 0, 20);
			username = new String(tempStr20, "GBK");
			
			//key
			System.arraycopy(buf, 20, key, 0, 20);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		//type
		byte[] temp = new byte[4];
		System.arraycopy(buf, 40, temp, 0, 4);
		type = Utils.vtolh(temp);
		
		//recon
		System.arraycopy(buf, 44, temp, 0, 4);
		recon = Utils.vtolh(temp);
		
		return new UserLogin(username, key, type, recon);
	}
	
	//返回要发送的byte数组
	public byte[] getBuf(){
		return buf;
	}
};

//struct Control_Msg  //一些确认信息
class Control_Msg{
	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public boolean isYesno() {
		return yesno;
	}

	public void setYesno(boolean yesno) {
		this.yesno = yesno;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	private String filename;	//char[100]
	private String username;	//char[15]:请求链接的用户:当type为true是请求，type为false的时候是返回消息   yesno是同意否
	private int flag;			//WORD Flag:什么类型的确定消息
	private boolean yesno;		//成功还是失败
	private int type;			//错误类型
		//1.登陆的时候 type = 1 密码和账号错误；2 已经在线; 3 使用错了客户端; 4 审核未过
		//2.注册的时候 type = 1 用户名已经存在；2 相关图片传送失败
		//3.链接的时候 type = 0 代表返回消息; 1 代表请求消息; 2 代码服务器同意两者链接 
	
	public Control_Msg(){
		username = "";
		username = "";
		flag = 0;
		yesno = false;
		type = 0;
	}
	
	public static int size = 100+15+2+1+4;
	private byte[] buf= new byte[size];
	//构造并转化
	public Control_Msg(String filename, String username, int flag, boolean yesno, int type){
		this.filename = filename;
		this.username = username;
		this.flag = flag;
		this.yesno = yesno;
		this.type = type;
		
		byte temp[];
		try {
			//filename
			temp = filename.getBytes("GBK");
			System.arraycopy(temp, 0, buf, 0, temp.length);
			//username
			temp = username.getBytes("GBK");
			System.arraycopy(temp, 0, buf, 100, temp.length);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}	
		
		//flag
		temp = Utils.shortToLH(flag);
		System.arraycopy(temp, 0, buf, 115, temp.length);
		
		//yesno
		byte yesnobyte = (byte) (yesno==true? 0x01:0x00);
	    buf[117] = yesnobyte;
	    
		//type
		temp = Utils.toLH(type);
		System.arraycopy(temp, 0, buf, 118, temp.length);
	}
	
	//byte数组转化为类对象
	public static Control_Msg getControl_MsgInfo(byte[] buf){
		String filename = "";
		String username = "";
		int flag = 0;
		boolean yesno = false;
		int type = 0;
		
		try {
			//filename
			byte[] tempStr100 = new byte[100];
			System.arraycopy(buf, 0, tempStr100, 0, 100);
			filename = new String(tempStr100, "GBK");
			
			//username
			byte[] tempStr15 = new byte[15];
			System.arraycopy(buf, 100, tempStr15, 0, 15);
			username = new String(tempStr15, "GBK");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		byte[] temp = new byte[4];
		//flag
		System.arraycopy(buf, 115, temp, 0, 2);
		flag = Utils.vtolh(temp);
		
		//yesno
		byte yesnoByte = buf[117];
		yesno = (yesnoByte == 0x00) ? false : true;
		
		//type
		System.arraycopy(buf, 118, temp, 0, 4);
		type = Utils.vtolh(temp);
	
		return new Control_Msg(filename, username, flag, yesno, type);
	}
	
	//返回要发送的byte数组
	public byte[] getBuf(){
		return buf;
	}
};

//struct Login_Back_Info
class Login_Back_Info{
	public int getRecon() {
		return recon;
	}

	public void setRecon(int recon) {
		this.recon = recon;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPharmacist() {
		return pharmacist;
	}

	public void setPharmacist(String pharmacist) {
		this.pharmacist = pharmacist;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isYesno() {
		return yesno;
	}

	public void setYesno(boolean yesno) {
		this.yesno = yesno;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	private String username;	//char[15];
	private String pharmacist;	//char[15];
	private String title;		//char[100];
	private boolean yesno;		//成功还是失败
	private int type;			//登陆的时候 错误类型
		//type = 1 密码和账号错误；
		//type = 2 已经在线;
		//type = 3 使用错了客户端; 
		//type = 4 审核未过
	private int recon;
	
	public Login_Back_Info(){
		username = "";
		pharmacist = "";
		title = "";
		yesno = false;
		type = 0;
		recon = -1;
	}
	
	public static int size = 15+15+100+2+4+4;
	private byte[] buf= new byte[size];
	//构造并转化
	public Login_Back_Info(String username, String pharmacist, String title, boolean yesno, int type, int recon){
		this.username = username;
		this.pharmacist = pharmacist;
		this.title = title;
		this.yesno = yesno;
		this.type = type;
		this.recon = recon;
		
		byte temp[];
		try {
			//username
			temp = username.getBytes("GBK");
			System.arraycopy(temp, 0, buf, 0, temp.length);
			//pharmacist
			temp = pharmacist.getBytes("GBK");
			System.arraycopy(temp, 0, buf, 15, temp.length);
			//title
			temp = title.getBytes("GBK");
			System.arraycopy(temp, 0, buf, 30, temp.length);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}	
		
		//yesno
		byte yesnobyte = (byte) (yesno==true? 0x01:0x00);
	    buf[130] = yesnobyte;    
		//type
		temp = Utils.toLH(type);
		System.arraycopy(temp, 0, buf, 132, temp.length);
		//recon
		temp = Utils.toLH(recon);
		System.arraycopy(temp, 0, buf, 136, temp.length);
	}
	
	//byte数组转化为类对象
	public static Login_Back_Info getLogin_Back_Info(byte[] buf){
		String username = "";
		String pharmacist = "";
		String title = "";
		boolean yesno = false;
		int type = 0;
		int recon = 0;
		
		try {
			//username
			byte[] tempStr15 = new byte[15];
			System.arraycopy(buf, 0, tempStr15, 0, 15);
			username = new String(tempStr15, "GBK");
			
			//pharmacist
			System.arraycopy(buf, 15, tempStr15, 0, 15);
			pharmacist = new String(tempStr15, "GBK");
			
			//title
			byte[] tempStr100 = new byte[100];
			System.arraycopy(buf, 30, tempStr100, 0, 100);
			pharmacist = new String(tempStr100, "GBK");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		//yesno
		byte yesnoByte = buf[130];
		yesno = (yesnoByte == 0x00) ? false : true;
		
		byte[] temp = new byte[4];
		//type
		System.arraycopy(buf, 132, temp, 0, 4);
		type = Utils.vtolh(temp);
		
		//recon
		System.arraycopy(buf, 136, temp, 0, 4);
		recon = Utils.vtolh(temp);
	
		return new Login_Back_Info(username, pharmacist, title, yesno, type, recon);
	}
	
	//返回要发送的byte数组
	public byte[] getBuf(){
		return buf;
	}	
};

//Req_Info_Username
class Req_Info_Username{
	private String username;		//char[12]
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Req_Info_Username(){
		username = null;
	}
}
//HeartBeat
class HeartBeat{
	String username;		//char[15]
	byte ack;				//确认字符ACK
	public HeartBeat(){
		username = "";
		ack = 0;
	}
	
	public static int size = 15+1;
	byte[] buf = new byte[size];
	
	public HeartBeat(String username, byte ack){
		this.username = username;
		this.ack = ack;
		
		//username
		byte[] temp = new byte[15];
		try {
			temp = username.getBytes("GBK");
			System.arraycopy(temp, 0, buf, 0, temp.length);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		//ack
		System.arraycopy(ack, 0, buf, 15, 1);
	}
	
	public static HeartBeat getHeartBeatInfo(byte buf[]){
		String username = "";
		byte ack = 0;
		byte[] tempStr15 = new byte[15];
		System.arraycopy(buf, 0, tempStr15, 0, 15);
		try {
			username = new String(tempStr15, "GBK");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		System.arraycopy(buf, 15, ack, 0, 1);
		return new HeartBeat(username, ack);
	}
	//返回要发送的byte数组
	public byte[] getBuf(){
		return buf;
	}
};
