using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace AESCryptoService
{
    public interface ICryptoService
    {
        string Decrypt(string strToBeDecrypted);
        string Encrypt(string strToBeEncrypted);
    }
}
