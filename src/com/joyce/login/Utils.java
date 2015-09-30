package com.joyce.login;

public class Utils {
    //大小端转化问题
    //short转化成byte数组
    public static byte[] shortToLH(int n){
        byte[] b = new byte[2];
        b[0] = (byte) (n & 0xff);
        b[1] = (byte) (n >> 8 & 0xff);
        return b;
    }
    //intװ转化成byte数组
    public static byte[] toLH(int n) {
        byte[] b = new byte[4];
        b[0] = (byte) (n & 0xff);
        b[1] = (byte) (n >> 8 & 0xff);
        b[2] = (byte) (n >> 16 & 0xff);
        b[3] = (byte) (n >> 24 & 0xff);
        return b;
    }
    
    //byte数组转化为int
    public static int vtolh(byte[] bArr) {
    	byte begin = bArr[0];
    	if(begin<0){
    		short n = 0;
            for(int i=0;i<bArr.length&&i<4;i++){
                int left = i*8;
                n+= (bArr[i] << left);
            }
            return n+256;
    	}
    	else{
    		short n = 0;
            for(int i=0;i<bArr.length&&i<4;i++){
                int left = i*8;
                n+= (bArr[i] << left);
            }
            return n;
    	}    
    }
    
    //交换对象
    public static void SWAP(Object a, Object b){
    	Object c = a;
    	a = b;
    	b = c;
    }
}
