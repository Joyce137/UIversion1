package com.joyce.login;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.content.Context;
import android.util.Log;


class _tagThreadParams_WORKER{
	TCPSocket p;	//类指针，用于调用类中的函数
	int nThreadNo;	//线程编号
}

class TCPSocket {
	public Socket sockClient;
	public String center_IP;
	String username;
	
	//构造函数
	public TCPSocket(){
		
	}
	
	//ReceiveMessageThread
	public static NET_PACK ReceiveMessageThread(Socket socket, int len, int timeout){
		if(socket==null || len <0)
		{
			System.out.println("Socket 为空 !");
			return null;
		}
		byte[] receive = new byte[len];
		try {
			socket.setSoTimeout(timeout);
			socket.getInputStream().read(receive);
		} catch (IOException e) {
			e.printStackTrace();
		}
		NET_PACK p = NET_PACK.getNET_PACKInfo(receive);
		return p;
	}
	

	//SendPack
	public boolean SendPack(NET_PACK data){
		int flag = data.getM_Start();
		//循环发送
		while(true){
			int retVal;
			retVal = sendMsg(sockClient,data.getBuf(),data.size,flag);
			if(retVal != 1){
				Log.e("SendPack","SendPack: "+retVal);
				return false;
			}
			Log.e("SendPack","Yes__SendPack: "+retVal);
			return true;
		}
	}
	
	//sendMsg
	//==C语言 int send( SOCKET s,   const char FAR *buf,   int len,   int flags );
	public int sendMsg(Socket socket, byte[] data, int len, int flags){
		int resultno = 1;
		if(socket == null){
			Log.e("SendMsg","SendMsg: +ErrorNo.SOCKET_NULL");
			return ErrorNo.SOCKET_NULL;
		}
		if(data == null){
			Log.e("SendMsg","SendMsg: +ErrorNo.DATA_NULL");
			return ErrorNo.DATA_NULL;
		}
		if(len < 0){
			Log.e("SendMsg","SendMsg: +ErrorNo.MINUSVALUE");
			return ErrorNo.MINUSVALUE;
		}
		
		int available=0;
		try {
			if((available=socket.getInputStream().available())>=0){
				socket.getInputStream().skip(available);
			}	
			
 			socket.getOutputStream().write(data);
			socket.getOutputStream().flush();		
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.e("SendMsg","SendMsg: "+resultno);
		return resultno;
	}
	
	
	//ShutSocket
	public void ShutSocket(){
		if(!sockClient.isClosed()){
			try {
				sockClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}	
		sockClient = null;// 终止对套接字库的使用
	}
	
	//ShutConnect
	public void ShutConnect(){
		if(!sockClient.isConnected()){
			try {
				sockClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	//SendHeartBeat
	public boolean SendHeartBeat(byte ack){
		if(username.length() == 0){
			return false;
		}
		
		HeartBeat heart = new HeartBeat(username, ack);
		NET_PACK p = new NET_PACK();
		p.size = NET_PACK.infoSize + HeartBeat.size;
		p.setM_nFlag(Types.HeartBeat);
		p.setnDataLen(HeartBeat.size);
		p.CalCRC();
		p.setM_buffer(heart.getBuf());
		
		if(SendPack(p)){
			SendPack(p);
			return true;
		}
		else{
			return false;
		}
	}
	
	//Send_ControlMsg
	public void Send_ControlMsg(Control_Msg msg){
		NET_PACK p = new NET_PACK();
		p.size = NET_PACK.infoSize + Control_Msg.size;
		p.setM_nFlag(Types.INFONOYES);
		p.setnDataLen(Control_Msg.size);
		p.CalCRC();
		p.setM_buffer(msg.getBuf());
		
		SendPack(p);		
	}
	
	//Req_Off_Link
		//请求链接 或者 断开链接  yes为ture为请求
	public void Req_Off_Link(String username, boolean yes){
		Control_Msg cmsg = new Control_Msg();
		if(yes == true){
			cmsg.setFlag(Types.To_User);
		}
		else{
			cmsg.setFlag(Types.Off_Link);
		}
		cmsg.setType(1);
		cmsg.setUsername(username);
		
		Send_ControlMsg(cmsg);
	}
	
	//ReLogin
	public void ReLogin(String username){
		UserLogin user = new UserLogin();
		user.setUsername(username);
		user.setRecon(Types.USER_RECONNECT_FLAG);
		user.setKey(new byte[20]);
		
		//NET_PACK
		NET_PACK p = new NET_PACK();
		p.size = NET_PACK.infoSize + UserLogin.size;
		p.setM_nFlag(Types.LoginUP);
		p.setnDataLen(UserLogin.size);
		p.setM_buffer(user.getBuf());
		p.CalCRC();
		SendPack(p);
	}
	
	//SendUserinfo - 发送用户名和密码
	public void SendUserinfo(String username, byte[] key, int type, int flag){
		UserLogin user = new UserLogin(username, key, type, flag);
//		user.setUsername(username);
//		user.setKey(key);
//		user.setType(type);
//		user.setRecon(flag);
		NET_PACK p = new NET_PACK(-1, UserLogin.size, Types.LoginUP, user.getBuf());	
		p.size = NET_PACK.infoSize + UserLogin.size;
//		p.setM_nFlag(Types.LoginUP);
//		p.setnDataLen(UserLogin.size);
//		p.setM_buffer(user.getBuf());
		p.CalCRC();
		SendPack(p);
	}
	
	//GetPath -- databases : 存放数据库
	public String GetPath(String type){
		Context c = MainActivity.context.getApplicationContext();
		String path = c.getDatabasePath(type).toString()+"/";
		return path;
	}
}