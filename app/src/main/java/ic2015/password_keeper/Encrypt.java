package ic2015.password_keeper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;


//加密算法
public class Encrypt {

    //SHA-256
    //https://blog.csdn.net/chain_fei/article/details/77822830
    public static String SHA(String password){
        String result = "";

        MessageDigest messageDigest;

        try {

            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(password.getBytes("UTF-8"));
            result = byte2Hex(messageDigest.digest());

        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e){
            e.printStackTrace();
        }

        return result;
    }

    //将 byte 转为 16 进制
    private static String byte2Hex(byte[] bytes){

        StringBuilder string_buffer = new StringBuilder();
        String temp;
        for (int i = 0; i < bytes.length; i++){
            temp = Integer.toHexString(bytes[i] & 0xFF);
            if (temp.length() == 1){
                // 得到一位的进行补 0 操作
                string_buffer.append(0);
            }
            string_buffer.append(temp);
        }
        return string_buffer.toString();
    }

    //AES 加密
    //https://blog.csdn.net/u012103898/article/details/77807331
    public static String AESEncrypt(String password, String encryptRules){

        String result = "";

        try {

            //利用KeyGenerator构造密钥生成器，指定为AES算法，不区分大小写
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            //根据encryptEncodeRules规则初始化密钥生成器，生成一个128位的随机源,根据传入的字节数组，实现随机数算法
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "Crypto");
            random.setSeed(encryptRules.getBytes());
            keyGenerator.init(128, random);
            //产生原始对称密钥
            SecretKey originalKey = keyGenerator.generateKey();
            //获得原始对称密钥的字节数组
            byte[] rawByte = originalKey.getEncoded();
            //根据字节数组生成AES密钥
            SecretKey secretKey = new SecretKeySpec(rawByte, "AES");
            //根据指定算法AES自成密码器
            Cipher cipher = Cipher.getInstance("AES");
            //初始化密码器，第一个参数为加密(Encrypt_mode)或者解密解密(Decrypt_mode)操作，第二个参数为使用的KEY
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            //获取加密内容的字节数组(这里要设置为utf-8)不然内容中如果有中文和英文混合中文就会解密为乱码
            byte[] byteEncode = password.getBytes("utf-8");
            //根据密码器的初始化方式--加密：将数据加密
            byte[] bytesAes = cipher.doFinal(byteEncode);
            //将加密后的数据转换为字符串
            result = new BASE64Encoder().encode(bytesAes);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | UnsupportedEncodingException
                | NoSuchProviderException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        return result;

    }

    //AES 解密
    public static String AESDecrypt(String encrypt_password, String encryptRules){

        String result = "";

        try {

            //利用KeyGenerator构造密钥生成器，指定为AES算法，不区分大小写
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            //根据encryptEncodeRules规则初始化密钥生成器，生成一个128位的随机源,根据传入的字节数组，实现随机数算法
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "Crypto");
            random.setSeed(encryptRules.getBytes());
            keyGenerator.init(128, random);
            //产生原始对称密钥
            SecretKey originalKey = keyGenerator.generateKey();
            //获得原始对称密钥的字节数组
            byte[] byteArray = originalKey.getEncoded();
            //根据字节数组生成AES密钥
            SecretKey secretKey = new SecretKeySpec(byteArray, "AES");
            //根据指定算法AES自成密码器
            Cipher cipher = Cipher.getInstance("AES");
            //将加密并编码后的内容解码成字节数组
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            //解密密文
            byte[] bytePassword = new BASE64Decoder().decodeBuffer(encrypt_password);
            byte[] byteEncode = cipher.doFinal(bytePassword);

            result = new String(byteEncode, "UTF-8");


        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | NoSuchProviderException
                | BadPaddingException | IllegalBlockSizeException | IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    //生成随机密码
    public static String getRandomPassWord(int len){
        int i;  //生成的随机数
        int count = 0; //生成的密码的长度
        // 密码字典
        char[] str = {
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                '~', '!', '@', '#', '$', '%', '^', '-', '+'
        };
        StringBuilder sb = new StringBuilder("");
        Random r = new Random();
        while(count < len){
            //生成 0 ~ 密码字典-1 之间的随机数
            i = r.nextInt(str.length);
            sb.append(str[i]);
            count ++;
        }
        return sb.toString();
    }

}
