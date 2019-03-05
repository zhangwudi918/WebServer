package demo;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class UTFDemo {
	public static void main(String[] args) throws UnsupportedEncodingException  {
		String str = "·¶";
		byte[] data = str.getBytes("utf-8");
		System.out.println(Arrays.toString(data));
	}
}
