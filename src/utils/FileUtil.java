package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtil {
	public static void MatrixToCSV(float m[][],int x,int y,String name){
		name="e:/"+name;
		File file=new File(name);
		String ans="";
		FileOutputStream out=null;
		try {
			out=new FileOutputStream(file);
			for(int i=0;i<x;i++){
				for(int j=0;j<y;j++){
					if(j!=y-1)
						ans+=m[i][j]+",";
					else
						ans+=m[i][j]+"\n";
				}
			}
			out.write(ans.getBytes());
			out.flush();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
