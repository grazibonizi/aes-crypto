package AESCrypto;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.SecretKeySpec;

public class AESCryptoService {
	private String password;
	private String mode;
	private int interations;
	private int keySize;
	private int ivLenght;
	private int saltLenght;

	public AESCryptoService() {
		password = "5ce9a2e2cd46";
		mode = "AES/CBC/PKCS5Padding";
		interations = 1000;
		keySize = 128;
		ivLenght = 16;
		saltLenght = 16;
	}

	private SecretKeySpec configureKey(byte[] salt)
			throws InvalidKeySpecException, NoSuchAlgorithmException {
		KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt,
				interations, keySize);
		SecretKeyFactory secretKeyFactory = SecretKeyFactory
				.getInstance("PBKDF2WithHmacSHA1");
		return new SecretKeySpec(secretKeyFactory.generateSecret(keySpec)
				.getEncoded(), "AES");
	}

	private byte[] encrypt(byte[] message) throws InvalidKeyException,
			InvalidAlgorithmParameterException, InvalidKeySpecException,
			NoSuchAlgorithmException, NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException {
		SecureRandom secureRandom = new SecureRandom();
		byte[] iv = secureRandom.generateSeed(ivLenght);
		byte[] salt = secureRandom.generateSeed(saltLenght);
		AlgorithmParameterSpec algorithmParameterSpec = new IvParameterSpec(iv);

		Cipher cipher = Cipher.getInstance(mode);
		cipher.init(Cipher.ENCRYPT_MODE, configureKey(salt),
				algorithmParameterSpec);
		byte[] encryptedMessageBytes = cipher.doFinal(message);

		byte[] bytesToEncode = new byte[ivLenght + encryptedMessageBytes.length
				+ saltLenght];
		System.arraycopy(iv, 0, bytesToEncode, 0, ivLenght);
		System.arraycopy(encryptedMessageBytes, 0, bytesToEncode, ivLenght,
				encryptedMessageBytes.length);
		System.arraycopy(salt, 0, bytesToEncode, ivLenght
				+ encryptedMessageBytes.length, saltLenght);

		return bytesToEncode;
	}

	private byte[] decrypt(byte[] message) throws IllegalBlockSizeException,
			BadPaddingException, NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException,
			InvalidAlgorithmParameterException, InvalidKeySpecException {
		byte[] iv = new byte[ivLenght];
		System.arraycopy(message, 0, iv, 0, ivLenght);

		byte[] salt = new byte[saltLenght];
		System.arraycopy(message, message.length - saltLenght, salt, 0,
				saltLenght);

		Cipher cipher = Cipher.getInstance(mode);
		cipher.init(Cipher.DECRYPT_MODE, configureKey(salt),
				new IvParameterSpec(iv));

		int messageDecryptedBytesLength = message.length - ivLenght
				- saltLenght;
		byte[] messageDecryptedBytes = new byte[messageDecryptedBytesLength];
		System.arraycopy(message, ivLenght, messageDecryptedBytes, 0,
				messageDecryptedBytesLength);

		return cipher.doFinal(messageDecryptedBytes);
	}

	public String encrypt(String message) throws UnsupportedEncodingException,
			InvalidKeyException, InvalidAlgorithmParameterException,
			InvalidKeySpecException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException,
			BadPaddingException {
		byte[] messageBytes = message.getBytes("utf-8");
		byte[] encrypted = encrypt(messageBytes);
		String strEncrypted = Base64.getEncoder().encodeToString(encrypted);
		return strEncrypted;
	}

	public String decrypt(String message) throws InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException,
			NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidAlgorithmParameterException, InvalidKeySpecException {
		byte[] messageBytes = Base64.getDecoder().decode(message);
		byte[] decrypted = decrypt(messageBytes);
		String strDecrypted = new String(decrypted, StandardCharsets.UTF_8);
		return strDecrypted;
	}
}
