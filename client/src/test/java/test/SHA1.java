package test;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.lang.StringUtils;
/**
 * 加密算法
 * @author a_Q
 *
 */
public class SHA1 {
	// 字节数组转换为16进制的字符串
		private static String byteArrayToHex(byte[] byteArray) {
			char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
			char[] resultCharArray = new char[byteArray.length * 2];
			int index = 0;
			for (byte b : byteArray) {
				resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
				resultCharArray[index++] = hexDigits[b & 0xf];
			}
			return new String(resultCharArray);
		}

		//计算消息摘要
		public static String getMessageDigest(String str, String encName){
			byte[] digest = null;
			if (StringUtils.isBlank(encName)) {
				encName = "SHA-1";
			}
			try {
				MessageDigest md = MessageDigest.getInstance(encName);
				md.update(str.getBytes("UTF-8"));
				digest = md.digest();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			return byteArrayToHex(digest);
		}
}
