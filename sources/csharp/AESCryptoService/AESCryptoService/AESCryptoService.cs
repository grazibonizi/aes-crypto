using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using System.Security.Cryptography;
using System.Configuration;

namespace AESCryptoService
{
    public class AESCryptoService : ICryptoService
    {
        #region Attributes
        private const int blockSize = 128;
        private const CipherMode mode = CipherMode.CBC;
        private readonly int keySize;
        private readonly int interations;
        private readonly int ivLenght;
        private readonly int saltLenght;
        private readonly string password;
        #endregion

        #region Constructors
        public AESCryptoService(int keySize, int blockSize, int interations, int ivLenght, int saltLenght, string password)
        {
            this.keySize = keySize;
            this.interations = interations;
            this.ivLenght = ivLenght;
            this.saltLenght = saltLenght;
            this.password = password;
        }

        public AESCryptoService()
        {
            keySize = Convert.ToInt32(ConfigurationManager.AppSettings["keySize"]);
            interations = Convert.ToInt32(ConfigurationManager.AppSettings["interations"]);
            ivLenght = Convert.ToInt32(ConfigurationManager.AppSettings["ivLenght"]);
            saltLenght = Convert.ToInt32(ConfigurationManager.AppSettings["saltLenght"]);
            password = ConfigurationManager.AppSettings["password"];
        }
        #endregion

        #region Private Methods
        private byte[] CreateRandomSalt()
        {
            byte[] saltBytes = new byte[saltLenght];
            RandomNumberGenerator.Create().GetBytes(saltBytes);
            return saltBytes;
        }

        private Rfc2898DeriveBytes ConfigureKey(byte[] saltBytes)
        {
            return new Rfc2898DeriveBytes(password, saltBytes, interations);
        }
        
        private byte[] Encrypt(byte[] bytesToBeEncrypted)
        {
            byte[] saltBytes = CreateRandomSalt();
            byte[] encryptedBytes;
            byte[] result;

            using (MemoryStream ms = new MemoryStream())
            {
                using (RijndaelManaged AES = new RijndaelManaged())
                {
                    var key = ConfigureKey(saltBytes);
                    AES.KeySize = keySize;
                    AES.BlockSize = blockSize;
                    AES.Key = key.GetBytes(AES.KeySize / 8);
                    AES.IV = key.GetBytes(ivLenght);
                    AES.Mode = mode;
                    AES.Padding = PaddingMode.Zeros;

                    using (var cs = new CryptoStream(ms, AES.CreateEncryptor(), CryptoStreamMode.Write))
                    {
                        cs.Write(bytesToBeEncrypted, 0, bytesToBeEncrypted.Length);
                        cs.Close();
                    }
                    encryptedBytes = ms.ToArray();

                    result = new byte[ivLenght + encryptedBytes.Length + saltLenght];
                    Array.Copy(AES.IV, 0, result, 0, ivLenght);
                    Array.Copy(encryptedBytes, 0, result, ivLenght, encryptedBytes.Length);
                    Array.Copy(saltBytes, 0, result, ivLenght + encryptedBytes.Length, saltLenght);
                }

            }
            return result;
        }

        private byte[] Decrypt(byte[] bytesToBeDecrypted)
        {
            int messageLenght = bytesToBeDecrypted.Length - ivLenght - saltLenght;
            byte[] iv = new byte[ivLenght];
            byte[] messageBytes = new byte[messageLenght];
            byte[] saltBytes = new byte[saltLenght];
            byte[] decryptedBytes = null;

            Array.Copy(bytesToBeDecrypted, 0, iv, 0, ivLenght);
            Array.Copy(bytesToBeDecrypted, ivLenght, messageBytes, 0, messageLenght);
            Array.Copy(bytesToBeDecrypted, bytesToBeDecrypted.Length - saltLenght, saltBytes, 0, saltLenght);
            
            using (MemoryStream ms = new MemoryStream())
            {
                using (RijndaelManaged AES = new RijndaelManaged())
                {
                    var key = ConfigureKey(saltBytes);
                    AES.KeySize = keySize;
                    AES.BlockSize = blockSize;                    
                    AES.Key = key.GetBytes(AES.KeySize / 8);
                    AES.IV = iv;
                    AES.Mode = mode;
                    AES.Padding = PaddingMode.Zeros;

                    using (var cs = new CryptoStream(ms, AES.CreateDecryptor(), CryptoStreamMode.Write))
                    {
                        cs.Write(messageBytes, 0, messageBytes.Length);
                        cs.Close();
                    }
                    decryptedBytes = ms.ToArray();
                }
            }

            return decryptedBytes;
        }
        #endregion

        #region IAESCryptoService Methods
        public string Encrypt(string strToBeEncrypted)
        {
            byte[] passwordBytes = Encoding.UTF8.GetBytes(password);
            byte[] bytesToBeEncrypted = Encoding.UTF8.GetBytes(strToBeEncrypted);
            byte[] encryptedBytes = Encrypt(bytesToBeEncrypted);
            return Convert.ToBase64String(encryptedBytes).Trim();
        }

        public string Decrypt(string strToBeDecrypted)
        {
            byte[] passwordBytes = Encoding.UTF8.GetBytes(password);
            byte[] bytesToBeDecrypted = Convert.FromBase64String(strToBeDecrypted);
            byte[] decryptedBytes = Decrypt(bytesToBeDecrypted);
            return Encoding.UTF8.GetString(decryptedBytes).Trim();
        }
        #endregion
    }
}
