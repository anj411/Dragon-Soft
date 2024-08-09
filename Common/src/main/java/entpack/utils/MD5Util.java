package entpack.utils;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {
    /**
     * MD5方法
     *
     * @param text 明文
     * @return 密文
     * @throws Exception
     */
    public static String md5(String text) {
        //加密后的字符串
        String encodeStr = DigestUtils.md5Hex(text);
        return encodeStr;
    }

    /**
     * MD5验证方法
     *
     * @param text 明文
     * @param md5  密文
     * @return true/false
     * @throws Exception
     */
    public static boolean verify(String text, String md5) {
        //根据传入的密钥进行验证
        String md5Text = md5(text);
        if (md5Text.equalsIgnoreCase(md5)) {
            System.out.println("MD5验证通过");
            return true;
        }

        return false;
    }

    public static String SignKeyEncryption(String data) {
        try {
            java.security.MessageDigest h = java.security.MessageDigest.getInstance("MD5");
            byte[] cipherStr = h.digest(data.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < cipherStr.length; ++i) {
                sb.append(Integer.toHexString((cipherStr[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (Exception e) {
            System.out.println(e);
        }
        return "";
    }

}
