package net.blacklab.lmmnx.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import littleMaidMobX.LMM_LittleMaidMobNX;

public class Version {
	
	public static class VersionData{
		public int code;
		public String name;
		public VersionData(int i, String s){
			code = i;
			name = s;
		}
	}
	
	/**
	 * バージョンチェック用。HTTPにアクセスし、現在最新のバージョンコードを返す。
	 * 基本的に別スレッドで呼ばれるので、なんか繋がらない時も待たされることはないはず
	 * @return バージョンコードの整数値
	 */
	
	public static VersionData getLatestVersion(){
		int latestcode = LMM_LittleMaidMobNX.VERSION_CODE;
		String latestversion = LMM_LittleMaidMobNX.VERSION;
		String address = "http://mc.el-blacklab.net/lmmnxversion.txt";
		
		URL url;
		HttpURLConnection connection;
		BufferedReader reader;
		
		try{
			url = new URL(address);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			
			String str;
			while((str = reader.readLine()) != null){
				if(str.startsWith("CODE=")){
					latestcode = Integer.valueOf(str.split("=")[1]);
				}
				if(str.startsWith("NAME=")){
					latestversion = str.split("=")[1];
				}
			}
			reader.close();
			connection.disconnect();
		}catch(IOException e){
			e.printStackTrace();
		}
		return new VersionData(latestcode,latestversion);
	}
	
}
