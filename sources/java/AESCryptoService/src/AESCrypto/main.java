package AESCrypto;

public class main {

	public static void main(String[] args) {
		try {
			
			String message = "Teste '1234567890-=\"!@#$%�&*()_+�[~];/,.`{^}:?<>";
			
			AESCryptoService cryptoService = new AESCryptoService();
			
			String encryptedMessage = cryptoService.encrypt(message);
			String decryptedMessage = cryptoService.decrypt(encryptedMessage);
			
			if (message.equalsIgnoreCase(decryptedMessage)) {
				System.out.print("sucesso");
			} else {
				System.out.print("erro");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
