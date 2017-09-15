package com.liyc.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.util.StringUtils;

/**
 * byte操作工具
 */
public class ByteUtils {
	/** 掩码1 */
	private static final byte[] MASK_ONE = { (byte) 0x80, 0x40, 0x20, 0x10, 0x08, 0x04, 0x02, 0x01 };
	/** 掩码0 */
	private static final byte[] MASK_ZERO = { 0x7F, (byte) 0xBF, (byte) 0xDF, (byte) 0xEF, (byte) 0xF7, (byte) 0xFB,
			(byte) 0xFD, (byte) 0xFE };

	/**
	 * 合并数组 For example, {@code concat(new byte[] a, b}, new byte[] {}, new
	 * byte[] {c}} returns the array {@code a, b, c} .
	 * 
	 * @param arrays
	 *            zero or more {@code byte} arrays
	 * @return a single array containing all the values from the source arrays,
	 *         in order
	 */
	public static byte[] concat(byte[]... arrays) {
		int length = 0;
		for (byte[] array : arrays) {
			length += array.length;
		}
		byte[] result = new byte[length];
		int pos = 0;
		for (byte[] array : arrays) {
			System.arraycopy(array, 0, result, pos, array.length);
			pos += array.length;
		}
		return result;
	}

	/**
	 * 输入流转换为字节数组
	 * 
	 * @param inStream
	 * @return
	 * @throws IOException
	 */
	public static final byte[] input2byte(InputStream inStream) throws IOException {
		ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
		byte[] buff = new byte[100];
		int rc = 0;
		while ((rc = inStream.read(buff, 0, 100)) > 0) {
			swapStream.write(buff, 0, rc);
		}
		byte[] in2b = swapStream.toByteArray();
		return in2b;
	}

	/***
	 * 字节数组转换为整型(数组长度为2)
	 * 
	 * @param b
	 * @return
	 */
	public static int byteToInt2(byte[] b) {
		int mask = 0xff;
		int temp = 0;
		int n = 0;
		for (int i = 0; i < 2; i++) {
			n <<= 8;
			temp = b[i] & mask;
			n |= temp;
		}
		return n;
	}

	/***
	 * crc16算法
	 * 
	 * @param buf
	 * @param len
	 * @return
	 */
	public static byte[] modbus_crc16(byte[] buf, int len) {
		int i, j;
		int c, crc = 0xFFFF;
		for (i = 0; i < len; i++) {
			c = buf[i] & 0x00FF;
			crc ^= c;
			for (j = 0; j < 8; j++) {
				if ((crc & 0x0001) != 0) {
					crc >>= 1;
					crc ^= 0xA001;
				} else
					crc >>= 1;
			}
		}
		byte[] res = new byte[2];
		res[0] = (byte) crc;
		res[1] = (byte) (crc >> 8);
		return res;
	}

	/**
	 * 将byte数组转成十六进制字符串
	 * 
	 * @param bytes
	 * @return
	 */
	public static String asHex(byte[] bytes) {
		return asHex(bytes, null);
	}

	/**
	 * 将byte数组转成字符串
	 * 
	 * @param bytes
	 * @return
	 */
	public static String asStr(byte[] bytes) {
		return BinHexOctUtil.toStringHex(asHex(bytes));
	}

	/***
	 * 将byte数组转换为以空格分割的字符串
	 * 
	 * @param bytes
	 * @return
	 */
	public static String asHexWithWhiteSpace(byte[] bytes) {
		return asHex(bytes, " ");
	}

	/**
	 * convert integer to byte array 大端模式
	 * 
	 * @param v
	 * @return
	 */
	public static byte[] getByte2FromInt(int v) {
		if (v > 65535) {
			throw new IllegalArgumentException("超出范围");
		}
		byte[] b = new byte[2];
		b[0] = (byte) ((v >> 8) & 0xFF);
		b[1] = (byte) v;
		return b;
	}

	/**
	 * 将int转换为长度为4的byte数组
	 * 
	 * @param v
	 * @return
	 */
	public static byte[] intToByte4(int v) {
		return ByteBuffer.allocate(4).putInt(v).array();
	}

	/**
	 * 打印byte数组
	 * 
	 * @param bytes
	 * @param separator
	 */
	public static void print(byte[] bytes, String separator) {
		System.out.println(asHex(bytes, separator).toUpperCase());
		System.out.println();
	}

	/**
	 * 将byte数组转为带分隔符的字符串
	 * 
	 * @param bytes
	 * @param separator
	 * @return
	 */
	public static String asHex(byte[] bytes, String separator) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			String code = Integer.toHexString(bytes[i] & 0xFF);
			if ((bytes[i] & 0xFF) < 16) {
				sb.append('0');
			}

			sb.append(code);

			if (separator != null && i < bytes.length - 1) {
				sb.append(separator);
			}
		}
		return sb.toString().toUpperCase();
	}

	/**
	 * 将byte数组转为带分隔符的字符串
	 * 
	 * @param bytes
	 * @param separator
	 * @return
	 */
	public static String asDecimal(byte[] bytes, String separator) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			String code = Integer.toString(bytes[i]);
			if ((bytes[i]) < 10) {
				sb.append('0');
			}
			sb.append(code);
			if (separator != null && i < bytes.length - 1) {
				sb.append(separator);
			}
		}
		return sb.toString();
	}

	/**
	 * 将byte数组转成十进制字符串
	 * 
	 * @param bytes
	 * @return
	 */
	public static String asDecimal(byte[] bytes) {
		return asDecimal(bytes, null);
	}

	/**
	 * 将ip分割为byte数组
	 * 
	 * @param ip
	 * @return
	 */
	public static byte[] ipToByteArray(String ip) {
		String[] ips = ip.split("\\.");
		byte[] ipb = new byte[4];
		for (int i = 0; i < ips.length; i++) {
			ipb[i] = (byte) Integer.parseInt(ips[i]);
		}
		return ipb;
	}

	/**
	 * 将十六进制字符串转为byte数组
	 * 
	 * @param hex
	 * @return
	 */
	public static byte[] asByteArray(String hex) {
		byte[] bts = new byte[hex.length() / 2];
		for (int i = 0; i < bts.length; i++) {
			bts[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
		}
		return bts;
	}

	/**
	 * 将字符串转为byte数组
	 * 
	 * @param hex
	 * @return
	 */
	public static byte[] asByteArrayFromStr(String str) {
		String hex = BinHexOctUtil.toHexString(str);
		byte[] bts = new byte[hex.length() / 2];
		for (int i = 0; i < bts.length; i++) {
			bts[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
		}
		return bts;
	}

	/**
	 * 将字符串转为指定编码的byte数组
	 * 
	 * @param s
	 * @param charset
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static byte[] encodeString(String s, Charset charset) throws UnsupportedEncodingException {
		s += "\0";
		return s.getBytes(charset);
	}

	/**
	 * 将byte数组还原为字符串
	 * 
	 * @param bytes
	 * @param charset
	 * @return
	 */
	public static String getEncodeString(byte[] bytes, Charset charset) {
		String s = "";
		s = new String(bytes, charset);
		return s;
	}

	/**
	 * 从给定的偏移量处的给定数组的 4 字节读取一个 int。
	 * 
	 * @param b
	 * @param offset
	 * @return
	 */
	public static final int makeIntFromByte4(byte[] b, int offset) {
		return b[offset] << 24 | (b[offset + 1] & 0xff) << 16 | (b[offset + 2] & 0xff) << 8 | (b[offset + 3] & 0xff);
	}

	/**
	 * 从给定的偏移量处的给定数组的 2 字节读取一个 int。
	 * 
	 * @param b
	 * @param offset
	 * @return
	 */
	public static final int makeIntFromByte2(byte[] b, int offset) {
		return (b[offset] & 0xff) << 8 | (b[offset + 1] & 0xff);
	}

	/**
	 * 从 2 个字节的偏移 0 处的给定数组中读取 int
	 * 
	 * @param b
	 * @return
	 */
	public static final int makeIntFromByte2(byte[] b) {
		return makeIntFromByte2(b, 0);
	}

	/**
	 * 从 4 个字节的偏移 0 处的给定数组中读取 int
	 * 
	 * @param b
	 * @return
	 */
	public static final int makeIntFromByte4(byte[] b) {
		return makeIntFromByte4(b, 0);
	}

	/**
	 * byte大小端转换
	 * 
	 * @param b
	 * @param offset
	 * @param length
	 */
	public final static void changeByteEndianess(byte[] b, int offset, int length) {
		byte tmp;
		for (int i = offset; i < offset + length; i += 2) {
			tmp = b[i];
			b[i] = b[i + 1];
			b[i + 1] = tmp;
		}
	}

	/**
	 * word大小端转换
	 * 
	 * @param b
	 * @param offset
	 * @param length
	 */
	public static byte[] changeWordEndianess(byte[] b, int offset, int length) {
		byte tmp;

		for (int i = offset; i < offset + length; i += 4) {
			tmp = b[i];
			b[i] = b[i + 3];
			b[i + 3] = tmp;
			tmp = b[i + 1];
			b[i + 1] = b[i + 2];
			b[i + 2] = tmp;
		}
		return b;
	}

	/**
	 * 转义
	 * 
	 * @param bytes
	 * @param target
	 * @param tar
	 * @return
	 */
	public static byte[] esc(byte[] bytes, byte[] target, byte[] replacement) {
		String hex = asHex(bytes, " ");
		String sc = asHex(target, " ");
		String ta = asHex(replacement, " ");
		hex = hex.replaceAll(sc, ta);
		hex = hex.replaceAll(" ", "");
		return asByteArray(hex);
	}

	/**
	 * 组合数组
	 * 
	 * @param bytes
	 * @param value
	 * @return
	 */
	public static byte[] append(byte[] bytes, byte[] value) {
		if (value != null) {
			int len = bytes.length + value.length;
			byte[] b = new byte[len];
			for (int i = 0; i < bytes.length; i++) {
				b[i] = bytes[i];
			}
			for (int j = 0; j < value.length; j++) {
				b[j + bytes.length] = value[j];
			}
			return b;
		}
		return bytes;
	}

	/**
	 * 数组前添加一个字节
	 */
	public static byte[] append(byte b, byte[] bytes) {
		return append(new byte[] { b }, bytes);
	}

	/**
	 * 数组后添加一个字节
	 */
	public static byte[] append(byte[] bytes, byte b) {
		return append(bytes, new byte[] { b });
	}

	public static byte[] append(byte[] bytes, int v) {
		byte[] value = new byte[] { (byte) v };
		int len = bytes.length + value.length;
		byte[] b = new byte[len];
		for (int i = 0; i < bytes.length; i++) {
			b[i] = bytes[i];
		}
		for (int j = 0; j < value.length; j++) {
			b[j + bytes.length] = value[j];
		}
		return b;
	}

	/**
	 * 取byte中左起指定位的值
	 * 
	 * @param v
	 * @param index
	 *            偏移量，从1开始
	 * @return 0/1
	 */
	public static final int getBitValueFromByte(byte v, int index) {
		return Math.abs((v & MASK_ONE[index - 1]) >> (8 - index));
	}

	/**
	 * 从 经掩码2个字节的偏移 0 处的给定数组中读取 int
	 * 
	 * @param v
	 * @param mask
	 * @return
	 */
	public static int getIntFromMaskByte2(byte[] v, byte mask) {
		v[0] &= mask;
		return makeIntFromByte2(v);
	}

	/**
	 * 遍历数组，连续异或
	 * 
	 * @param v
	 * @return
	 */
	public static byte doXor(byte[] v) {
		byte res = 0x00;
		for (int i = 0; i < v.length; i++) {
			res ^= v[i];
		}
		return res;
	}

	/**
	 * 求和
	 */
	public static byte sum(byte[] content) {
		short temp = 0x0000;
		for (int i = 0; i < content.length; i++) {
			temp += content[i];
		}
		return (byte) temp;
	}

	/**
	 * 对数组中某区间进行连续异或
	 * 
	 * @param v
	 * @param offset
	 * @param count
	 * @return
	 */
	public static byte doXor(byte[] v, int offset, int count) {
		byte res = 0x00;
		for (int i = offset; i < offset + count; i++) {
			res ^= v[i];
		}
		return res;
	}

	/**
	 * 截取数组
	 * 
	 * @param v
	 * @param offset
	 *            偏移量，从0开始
	 * @param count
	 *            长度
	 * @return
	 */
	public static byte[] subArray(byte[] v, int offset, int count) {
		if (v.length < offset + count) {
			throw new IllegalArgumentException("参数错误");
		}
		byte[] sub = new byte[count];
		for (int i = 0; i < count; i++) {
			sub[i] = v[offset + i];
		}
		return sub;
	}

	/**
	 * 从偏移量出截取数组
	 * 
	 * @param v
	 * @param offset
	 *            偏移量，从0开始
	 * @return
	 */
	public static byte[] subArray(byte[] v, int offset) {
		if (v.length < offset) {
			throw new IllegalArgumentException("参数错误");
		}
		byte[] sub = new byte[v.length - offset];
		for (int i = 0; i < sub.length; i++) {
			sub[i] = v[offset + i];
		}
		return sub;
	}

	/**
	 * 掩码
	 * 
	 * @param v
	 * @param maskOne
	 * @param offset
	 * @return
	 */
	public static byte doAndMask(byte v, boolean maskOne, int[] offset) {
		for (int i : offset) {
			if (maskOne) {
				v &= MASK_ONE[i];
			} else {
				v &= MASK_ZERO[i];
			}
		}
		return v;
	}

	/**
	 * CRC16返回byte[]
	 * 
	 * @param data
	 * @return
	 */
	public static byte[] CRC_16_UP(byte[] data) {
		byte[] result = new byte[2];
		short x, crc = 0x0000;
		for (int i = 0; i < data.length; i++) {
			crc = (short) (crc ^ (data[i] << 8));
			for (int j = 8; j > 0; j--) {
				x = (short) (crc & 0x8000);
				crc <<= 1;
				if (x != 0)
					crc ^= 0x3213;
			}
		}
		result[0] = (byte) (crc >> 8);
		result[1] = (byte) crc;
		return result;
	}

	/**
	 * CRC校验返回short
	 */
	public static short CRC_16_UP(byte[] data, short polynomial) {
		short x, crc = 0x0000;
		for (int i = 0; i < data.length; i++) {
			crc = (short) (crc ^ (data[i] << 8));
			for (int j = 8; j > 0; j--) {
				x = (short) (crc & 0x8000);
				crc <<= 1;
				if (x != 0) {
					crc ^= polynomial;
				}
			}
		}
		return crc;
	}

	/**
	 * 从指定偏移开始4个字节获取IP
	 */
	public static String makeIpFromByte4(byte[] data, int offset) {
		String[] ipArray = new String[4];
		for (int i = 0; i < 4; i++) {
			ipArray[i] = String.valueOf(data[offset + i] & 0xFF);
		}
		return StringUtils.arrayToDelimitedString(ipArray, ".");
	}

	/**
	 * 从4个字节获取IP
	 */
	public static String makeIpFromByte4(byte[] data) {
		return makeIpFromByte4(data, 0);
	}

	/**
	 * 整型转换为byte[2]
	 * 
	 * @param v
	 * @return
	 */
	public static byte[] intToByte2(int v) {
		byte[] result = new byte[2];
		result[0] = (byte) (0xff & v);
		result[1] = (byte) ((0xff00 & v) >> 8);
		return result;
	}

	/**
	 * 将byte[]转换为指定长度byte数组,不足的补指定字节
	 */
	public static byte[] fill(byte[] bytes, int size, byte b) {
		byte[] result = new byte[size];
		// int rawLen = bytes.length;
		// for(int i=0;i<size-rawLen;i++){
		// result[i] = b;
		// }
		// for(int j=0;j<rawLen;j++){
		// result[j+size-rawLen] = bytes[j];
		// }
		for (int i = 0; i < result.length; i++) {
			try {
				result[i] = bytes[i];
			} catch (IndexOutOfBoundsException e) {
				result[i] = b;
			}
		}

		return result;
	}

	/**
	 * 获得掩码位置
	 */
	public static List<Integer> getMaskLocation(byte b) {
		List<Integer> locations = new ArrayList<Integer>();
		for (int i = 0; i < 8; i++) {
			if ((b & MASK_ONE[i]) != 0x00) {
				locations.add(7 - i);
			}
		}
		return locations;
	}

	/**
	 * 字节转换为2进制字符串
	 */
	public static String toBinaryString(byte b) {
		StringBuilder sb = new StringBuilder();
		int src = b < 0 ? (int) b + 256 : b;
		sb.append(Integer.toBinaryString(src));
		int lack = 8 - sb.length();
		for (int i = 0; i < lack; i++) {
			sb.insert(0, "0");
		}
		return sb.toString();
	}

	/**
	 * 字节反转
	 * 
	 * @param src
	 * @return
	 */
	public static byte reverse(byte src) {
		byte res;
		res = (byte) Integer.parseInt(org.apache.commons.lang3.StringUtils.reverse(toBinaryString(src)), 2);
		return res;
	}

	public static byte[] reverse(byte[] src) {
		byte[] temp = src;
		for (int i = 0; i < src.length; i++) {
			temp[i] = reverse(src[i]);
		}
		return temp;
	}

	/**
	 * byte[]转换为List数组
	 * 
	 * @param array
	 * @return
	 */
	public static List<Byte> toList(byte[] array) {
		List<Byte> list = new ArrayList<Byte>();
		for (byte b : array) {
			list.add(b);
		}
		return list;
	}

	/**
	 * List数组转换为byte[]
	 * 
	 * @param array
	 * @return
	 */
	public static byte[] toArray(List<Byte> list) {
		Byte[] temp = new Byte[] {};
		list.toArray(temp);
		return ArrayUtils.toPrimitive(temp);
	}

	/**
	 * blob转byte[]
	 */
	@SuppressWarnings("unused")
	private byte[] blobToBytes(Blob blob) {
		BufferedInputStream is = null;
		try {
			is = new BufferedInputStream(blob.getBinaryStream());
			byte[] bytes = new byte[(int) blob.length()];
			int len = bytes.length;
			int offset = 0;
			int read = 0;
			while (offset < len && (read = is.read(bytes, offset, len - offset)) >= 0) {
				offset += read;
			}
			return bytes;
		} catch (Exception e) {
			return null;
		} finally {
			try {
				is.close();
				is = null;
			} catch (IOException e) {
				return null;
			}

		}
	}

	/**
	 * 获得指定文件的byte数组
	 */
	public static byte[] getBytes(String filePath) {
		byte[] buffer = null;
		try {
			File file = new File(filePath);
			FileInputStream fis = new FileInputStream(file);
			ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
			byte[] b = new byte[1000];
			int n;
			while ((n = fis.read(b)) != -1) {
				bos.write(b, 0, n);
			}
			fis.close();
			bos.close();
			buffer = bos.toByteArray();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer;
	}

	/**
	 * 根据byte数组，生成文件
	 */
	public static void getFile(byte[] bfile, String filePath, String fileName) {
		BufferedOutputStream bos = null;
		FileOutputStream fos = null;
		File file = null;
		try {
			File dir = new File(filePath);
			if (!dir.exists() && dir.isDirectory()) {// 判断文件目录是否存在
				dir.mkdirs();
			}
			file = new File(filePath + "\\" + fileName);
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			bos.write(bfile);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

}
