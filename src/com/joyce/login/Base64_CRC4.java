package com.joyce.login;

import java.io.UnsupportedEncodingException;
import sun.misc.BASE64Encoder;
import sun.misc.BASE64Decoder;

public class Base64_CRC4 {
	public static final char B64_offset[] = { 		//char[256]
		64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64,
		64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64,
		64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 62, 64, 64, 64, 63,
		52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 64, 64, 64, 64, 64, 64,
		64, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,
		15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 64, 64, 64, 64, 64,
		64, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
		41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 64, 64, 64, 64, 64,
		64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64,
		64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64,
		64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64,
		64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64,
		64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64,
		64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64,
		64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64,
		64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64
	};
	
	public static final String base64_map = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
};

//Base64编码
class Base64 {
	// 加密
	public static String getBase64(String str) {
		byte[] b = null;
		String s = null;
		try {
			b = str.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (b != null) {
			s = new BASE64Encoder().encode(b);
		}
		return s;
	}

	// 解密
	public static String getFromBase64(String s) {
		byte[] b = null;
		String result = null;
		if (s != null) {
			BASE64Decoder decoder = new BASE64Decoder();
			try {
				b = decoder.decodeBuffer(s);
				result = new String(b, "utf-8");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}
}

//CRC4编码
class CRC4{
	private byte sbox[] = new byte[256];	//待编码的字符数组
	private byte key[] = new byte[256];		//数字键值
	private byte k;							//键值指针
	private int m,n,i,j,ilen;
	
	public CRC4(){
		for(int x = 0;x<256;x++){
			sbox[x] = 0;
		}
		for(int x = 0;x<256;x++){
			sbox[x] = 0;
		}
	}
	
	//Encrypt
	public byte[] Encrypt(byte[] pszText, byte[] pszKey){
		i = 0;
		j = 0;
		n = 0;
		ilen = pszKey.length;
		
		//初始化键值
		for(m = 0;m<256;m++){
			key[m] = pszKey[m%ilen];
			sbox[m] = (byte) m;
		}
		
		for(m = 0;m<256;m++){
			n = (n+sbox[m]+key[m]) &0xff;
//			Utils.SWAP(sbox[m], sbox[n]);
			byte c = sbox[m];
			sbox[m] = sbox[n];
			sbox[n] = c;
		}
		
		ilen = pszText.length;
		for(m = 0;m<ilen;m++){
			i = (i+1)&0xff;
			j = (j+sbox[i])&0xff;
//			Utils.SWAP(sbox[i], sbox[j]);
			byte c = sbox[i];
			sbox[i] = sbox[j];
			sbox[j] = c;
			k = sbox[(sbox[i]+sbox[j])&0xff];
			if(k == pszText[m]){
				k = 0;
			}
			pszText[m] ^= k;
		}
		return pszText;
	}
	
	//Decrypt
	public byte[] Decrypt(byte[] pszText, byte[] pszKey){
		return Encrypt(pszText, pszKey);
	}
}
