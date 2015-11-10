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
            ICryptoService cryptoService = new AESCryptoService();

        }
    }
}
