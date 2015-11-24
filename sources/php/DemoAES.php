<?php
include 'AES.php';
$data = "Teste '1234567890-=\"!@#$%¨&*()_+´[~];/,.`{^}:?<>";
$imputKey = "123456";
$blockSize = 128;
$saltSize = 16;
$keySize = 16;
$interactions = 1000;
$aes = new AES($imputKey, $blockSize, $saltSize, $keySize, $interactions);

$enc = $aes->encrypt($data);
$dec=$aes->decrypt($enc);
echo "After encryption: ".$enc."<br/>";
echo "After decryption: ".$dec."<br/>";

?>