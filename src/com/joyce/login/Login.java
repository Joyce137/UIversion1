package com.joyce.login;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.http.util.EncodingUtils;

public class Login {
	private String m_default_pha_username;
	private String m_login_username;
	public TCPSocket login_socket = Sockets.socket_center;
	
	//saveLocalInfo() 		//记录信息保存到本地
	public void saveLocalInfo() throws IOException{
		String localPath = login_socket.GetPath("login")+"localInfo.inf";
		if(localPath.isEmpty()){
			File f = new File(localPath);
			if(!f.canRead()){
				return;
			}
			
			FileInputStream fis = new FileInputStream(f);
			int length = fis.available();
			byte [] buffer = new byte[length];   
			fis.read(buffer);       	  
	        String content = EncodingUtils.getString(buffer, "UTF-8");   
	  
	        fis.close();  			
		}
	}
	
	public void Login(String username, String pwd) {
		String str = pwd;
		CRC4 crc = new CRC4();
//		crc.Encrypt(str, Types.AES_KEY);
//		login_socket.SendUserinfo(username, str, Types.USER_TYPE_STORE, Types.USER_LOGIN_FLAG);
	}	
	

}
