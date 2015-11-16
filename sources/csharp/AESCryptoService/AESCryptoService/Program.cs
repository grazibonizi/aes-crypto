using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace AESCryptoService
{
    class Program
    {
        static void Main(string[] args)
        {
            try
            {
                ICryptoService cryptoService = new AESCryptoService();
                var message = "Teste '1234567890-=\"!@#$%¨&*()_+´[~];/,.`{^}:?<>";
                var encryptedMessage = cryptoService.Encrypt(message);
                Console.WriteLine(string.Format("Mensagem encriptada: {0}", encryptedMessage));
                var decryptedMessage = cryptoService.Decrypt(encryptedMessage);
                Console.WriteLine(string.Format("Mensagem decriptada: {0}", decryptedMessage));
                if (message.Equals(decryptedMessage))
                    Console.WriteLine("Sucess");
                else
                    Console.WriteLine("Fail");
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
            }
            Console.Read();
        }
    }
}
