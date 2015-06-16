package com.ouwenjie.note.utils;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串公交类
 */
public class StringUtils {

	/**
	 * 验证字符串是否为空（null, ""）
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(CharSequence str) {

		return (null == str || str.toString().length() == 0);
	}

	/**
	 * 验证字符串是否为空（null, "", "null","NULL"）
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty2(CharSequence str) {

		return (isEmpty(str) || str.toString().trim().equalsIgnoreCase("null"));
	}

	/**
	 * 验证字符串是否为空（null, "", "null","NULL", "0"）
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty3(CharSequence str) {

		return (isEmpty2(str) || "0".equals(str.toString().trim()));
	}

	/**
	 * 邮箱地址格式校验
	 * 
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email) {

		if (null == email || "".equals(email.trim()))
			return false;

		// String strPattern =
		// "^[a-zA-Z][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
		Pattern p = Pattern.compile("^.*@{1}.+\\.{1}.*$");
		Matcher m = p.matcher(email);

		return m.matches();
	}

	/**
	 * 是否是合法的国内手机号 （移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
	 * 　联通：130、131、132、152、155、156、185、186 　　 电信：133、153、180、189、（1349卫通））
	 * 
	 * @param mobileNo
	 * @return
	 */
	public static boolean isMobileNO(String mobileNo) {

		if (null == mobileNo || mobileNo.length() != 11)
			return false;

		Pattern p = Pattern
				.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		Matcher m = p.matcher(mobileNo);

		return m.matches();
	}

	/**
	 * 是否为合法密码（不少于5位长度）
	 * 
	 * @param pwd
	 * @return
	 */
	public static boolean isPwd(String pwd) {

		return (null != pwd && pwd.trim().length() >= 5);
	}

	/**
	 * 是否为合法IMEI
	 * 
	 * @param imei
	 * @return
	 */
	public static boolean isIMEI(String imei) {

		return (!isEmpty(imei) && !"0".equals(imei));
	}

	/**
	 * 是否为合法的CVC
	 * 
	 * @param cvc
	 * @return
	 */
	public static boolean isCVC(String cvc) {

		return (null != cvc && (cvc.length() == 3 || cvc.length() == 4));
	}

	/**
	 * 信用卡号合法性校验
	 * 
	 * @param cardNumber
	 * @return
	 */
	public static boolean isCreditCard(String cardNumber) {

		int sum = 0;
		int digit = 0;
		int addend = 0;
		boolean timesTwo = false;

		for (int i = cardNumber.length() - 1; i >= 0; i--) {

			digit = Integer.parseInt(cardNumber.substring(i, i + 1));

			if (timesTwo) {

				addend = digit * 2;

				if (addend > 9) {

					addend -= 9;
				}
			} else {

				addend = digit;
			}

			sum += addend;
			timesTwo = !timesTwo;
		}

		int modulus = sum % 10;
		return modulus == 0;
	}

	/**
	 * 追加人民币货币符号（格式如： ¥ 200）
	 * 
	 * @param rmb
	 * @return
	 */
	public static String appendRMBSymbol(Object rmb) {

		if (null == rmb)
			return "";

		StringBuffer sb = new StringBuffer("¥ ");
		sb.append(new DecimalFormat("#").format(rmb));

		return sb.toString();
	}

	/**
	 * 追加百分号（格式如： 80%）
	 * 
	 * @param percent
	 * @return
	 */
	public static String appendPercentSymbol(int percent) {

		StringBuffer sb;

		sb = new StringBuffer();
		sb.append(percent);
		sb.append("%");

		return sb.toString();
	}

	/**
	 * 格式化成2为长度
	 * 
	 * @param num
	 * @return
	 */
	public static String formatTo2Bit(int num) {

		if (num < 10) {

			StringBuffer sb;

			sb = new StringBuffer("0");
			sb.append(num);

			return sb.toString();
		} else {

			return String.valueOf(num);
		}
	}

	/**
	 * 字符串限长（超出最大长度后面加省略号）
	 * 
	 * @param str
	 * @param maxLen
	 * @return
	 */
	public static String limitLenOfStr(String str, int maxLen) {

		if (null != str && str.length() > maxLen) {

			StringBuffer sb = new StringBuffer();

			sb.append(str.substring(0, maxLen));
			sb.append("...");

			return sb.toString();
		}

		return str;
	}
}
