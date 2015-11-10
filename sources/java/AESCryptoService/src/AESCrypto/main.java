package AESCrypto;

public class main {

	public static void main(String[] args) {
		try {
			/* Testing itself*/
			//String message = "{\"NmPerson\":\"Henrique Sitta\",\"Emails\":[\"henrique.sitta@wunderman.com\"],\"Addresses\":[{\"DsAddress\":\"rua joao basso\",\"NbAddress\":\"414\",\"DsComplement\":\"apto 91\",\"NmDistrict\":\"centro\",\"DsCity\":\"sao bernardo do campo\",\"CdState\":\"SP\",\"NbPostalCode\":\"09721100\"}],\"Phones\":[{\"DsType\":\"H\",\"NbArea\":\"11\",\"NbTelephone\":\"55048765\"}]}";
			String message = "{    \"PersonGUID\": \"5C0D95BD-F63C-4A19-BE98-A1DABD42EAB7\",    \"Id\": 1,    \"NbPoints\": 100,    \"Items\": [      {        \"IdItem\": 1,        \"DsItem\": \"Produto Comode Teste 1\",        \"NbPoints\": 45.0      },  \t{        \"IdItem\": 2,        \"DsItem\": \"Produto Comode Teste 2\",        \"NbPoints\": 55.0      }    ]  }";
			
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
