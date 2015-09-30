package com.joyce.login;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import com.liwei.uiversion1.MainFrame;
import com.liwei.uiversion1.R;
import com.liwei.uiversion1.Register;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private Button loginBtn;
	private TextView mLogin_wangjimima, mLogin_zhuce;
	public TCPSocket login_socket = Sockets.socket_center;
	private String m_default_pha_username;
	private String m_login_username;
	
	//声明一个Handler对象
	private Handler mHandler;
	
	//用户名和密码输入框
	private EditText mLogin_user, mLogin_password;
	//显示登录结果
	private TextView mLogin_result;
	
	public static Context context;

	//InitSocket
	public boolean InitSocket(int DefaultPort, String DefaultIP){
		Log.e("InitSocket", "0");
		InetAddress addr = null;
		try {
			Log.e("InitSocket", "1");
			addr = InetAddress.getByName(DefaultIP);
			login_socket.sockClient = new Socket(addr, DefaultPort);
			
			//启动接收线程
			ReceiveMessageThread recv = new ReceiveMessageThread("Recv_Thread");
			Thread th = new Thread(recv);
			th.start();
			
			return true;
		} catch (UnknownHostException e) {
			Log.e("InitSocket", "2");
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			Log.e("InitSocket", "3");
			e.printStackTrace();
			return false;
		}
	}
		
	public void initSocket() {
		 if (!InitSocket(Types.center_Port, Types.version_IP))
		        if (!InitSocket(Types.center_Port, Types.version_IP))
		            if (!InitSocket(Types.center_Port, Types.version_IP))
		            {
		                System.out.println("网络故障，请稍后重试");
		                return;
		            }
		 Log.e("initSocket", "initSocket成功！");
	}
	
	//RecvPack
	public void RecvPack(NET_PACK data){
		Log.e("RecvPack", "RecvPack------");
		//登录标志
		if(data.getM_nFlag() == Types.Login_is){
			Login_Back_Info y = Login_Back_Info.getLogin_Back_Info(data.getM_buffer());
			if(y.getRecon() == Types.USER_LOGIN_FLAG){
				onRecvLoginMessage(data);
			}
		}
	}
	
	//收到login登录结果信息
	public void onRecvLoginMessage(NET_PACK data){
		System.out.println("onRecvLoginMessage-----------");
		String login_result = "";
		Login_Back_Info y = Login_Back_Info.getLogin_Back_Info(data.getM_buffer());
		m_login_username = y.getUsername();
		m_default_pha_username = y.getPharmacist();
		login_socket.username = y.getUsername();
		boolean yesno = y.isYesno();
		if(yesno){
			//登录成功
			login_result = "yes";
			Log.e("onRecvLoginMessage: login_result", login_result);
			Looper.prepare();
			//Toast.makeText(MainActivity.this, login_result, Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(MainActivity.this, MainFrame.class);
			startActivity(intent);
	        Looper.loop();
		}
		else{
			//1、登陆的时候 type = 1 密码和账号错误；type = 2 已经在线; type = 3 使用错了客户端; type = 4 审核未过
	        int type = y.getType();
	        switch (type)
	        {
	            case 1:
	                login_result = "账号或者密码错误";
	                break;
	            case 2:
	            	login_result = "该账号已经在线";
	                break;
	            case 3:
	            	login_result = "账号与客户端不匹配";
	                break;
	            case 4:
	            	login_result = "审核未通过";
	                break;
	            default:
	                break;
	        }
	        Log.e("onRecvLoginMessage: login error", login_result);
	        Looper.prepare();
	        Toast.makeText(MainActivity.this, login_result, Toast.LENGTH_SHORT).show();
	        Looper.loop();
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题栏
		setContentView(R.layout.login);
		
		//安卓2.3以后访问网络增加内容
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads()
				.detectDiskWrites()
				.detectNetwork()
				.penaltyLog()
				.build()); 
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
				.detectLeakedSqlLiteObjects()
				.detectLeakedClosableObjects()
				.penaltyLog()
				.penaltyDeath()
				.build());

		//初始化socket
		initSocket();

		loginBtn = (Button) findViewById(R.id.Login_OK);
		mLogin_wangjimima = (TextView) findViewById(R.id.Login_wangjimima);
		mLogin_zhuce = (TextView) findViewById(R.id.Login_zhuce);
		
		//用户名和密码输入框
		mLogin_user = (EditText) findViewById(R.id.Login_user);
		mLogin_password = (EditText) findViewById(R.id.Login_password);
		//登录结果
		

		loginBtn.setOnClickListener(new View.OnClickListener() {	

			@Override
			public void onClick(View v) {
				String username = mLogin_user.getText().toString();
				String pwd = mLogin_password.getText().toString();

				//用户名和密码为空
				if(username.isEmpty() || pwd.isEmpty()){
					Toast.makeText(getApplicationContext(), "请输入用户名和密码",Toast.LENGTH_SHORT).show();
				}
				else{
					String str = pwd;
					byte cstr[] = str.getBytes();
					for(int i = 0;i<cstr.length;i++)
						cstr[i] = 0;
					byte strb[] = str.getBytes();
					for(int i = 0;i<str.length();i++){
						cstr[i] = strb[i];
					}
					CRC4 crc = new CRC4();
					byte b[] = Types.AES_KEY.getBytes();
					crc.Encrypt(cstr, b);
					Sockets.socket_center.SendUserinfo(username, cstr, Types.USER_TYPE_STORE, Types.USER_LOGIN_FLAG);
					
//					Intent intent = new Intent(MainActivity.this, MainFrame.class);
//					startActivity(intent);
				}
			}
		});

		mLogin_zhuce.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, Register.class);

				startActivity(intent);
			}
		});
		
		mLogin_wangjimima.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(MainActivity.this, "忘记密码", 1).show();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	//接收线程
	class ReceiveMessageThread extends Thread{
		public ReceiveMessageThread(String threadName){
			this.setName(threadName);
		}
		@Override
		public void run() {
			Log.e("ReceiveMessageThread", "run() ");
			//_tagThreadParams_WORKER x = new _tagThreadParams_WORKER();
			TCPSocket p = Sockets.socket_center; //从主线程中传过来
			byte recvBuf[] = new byte[10000];
			boolean pack_err = false;
			boolean isPackageHead = false;
				
			while(true){
				synchronized (this) {
					try {
						this.wait(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				try {
					p.sockClient.getInputStream().read(recvBuf);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				if(pack_err == false){
					if(isPackageHead == true){
						PackHead ph = PackHead.getPackHeadInfo(recvBuf);
						if(ph.getM_Start() == Types.PACK_START_FLAG){
							isPackageHead = false;
						}
						else{
							for(int i = 0;i<recvBuf.length;i++){
								recvBuf[i] = 0;
							}
							pack_err = true;
						}
					}
					else{
						NET_PACK data = NET_PACK.getNET_PACKInfo(recvBuf);
						if(data.VerifyCRC() == data.getM_Crc()){							
							RecvPack(data);		//交给RecvPack处理
							//将recvBuf清空
							for(int i = 0;i<recvBuf.length;i++){
								recvBuf[i] = 0;
							}
							pack_err = true;
						}
						else{
							//将recvBuf清空
							for(int i = 0;i<recvBuf.length;i++){
								recvBuf[i] = 0;
							}
							pack_err = true;
						}
					}
				}		
			}
		}
	};

	//心跳包设计
	public void HeartBeatTimeProc(){
		
	}
}


