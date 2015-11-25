<?php

class AES {

    protected $password;
    protected $cipher;
    protected $mode;
    protected $saltSize;
	protected $keySize;
	protected $interactions;
	protected $hashType;

    /**
     * 
     * @param type $key
     * @param type $blockSize
     * @param type $mode
     */
    function __construct($password = null, $blockSize = null, $saltSize = null, $keySize = null, $interactions = null) {
        $this->setPassword($password);
        $this->setBlockSize($blockSize);
        $this->mode = MCRYPT_MODE_CBC;
		$this->saltSize = $saltSize;
		$this->keySize = $keySize;
		$this->interactions = $interactions;
		$this->hashType = "sha1";
    }

    /**
     * 
     * @param type $key
     */
    public function setPassword($password) {
        $this->password = $password;
    }

    /**
     * 
     * @param type $blockSize
     */
    public function setBlockSize($blockSize) {
        switch ($blockSize) {
            case 128:
                $this->cipher = MCRYPT_RIJNDAEL_128;
                break;

            case 192:
                $this->cipher = MCRYPT_RIJNDAEL_192;
                break;

            case 256:
                $this->cipher = MCRYPT_RIJNDAEL_256;
                break;
        }
    }
	
	protected function getIVSize(){
		return mcrypt_get_iv_size($this->cipher, $this->mode);
	}
	
    protected function generateIV() {
		return mcrypt_create_iv($this->getIVSize(), MCRYPT_RAND);
	}
	
	protected function generateSalt(){
		return substr(sha1(mt_rand()),0,$this->saltSize);
	}
	
	function pbkdf2($password, $salt, $interactions, $keySize){
		return hash_pbkdf2($this->hashType, $password, $salt, $interactions, $keySize, true);
	}

    /**
     * @return type
     * @throws Exception
     */
    public function encrypt($data) {
		$salt = $this->generateSalt();
		$iv = $this->generateIV();
		$key = $this->pbkdf2($this->password, $salt, $this->interactions, $this->keySize);
		$encrypted = mcrypt_encrypt($this->cipher, $key, $data, $this->mode, $iv);
		$encoded = base64_encode($iv . $encrypted . $salt);
        
		return trim($encoded);
    }

    /**
     * 
     * @return type
     * @throws Exception
     */
    public function decrypt($data) {
		$decoded = base64_decode($data);
		$iv = substr($decoded, 0, $this->getIVSize());
		$salt = substr($decoded, (-1)*$this->saltSize);
		$message = substr($decoded, $this->getIVSize(), strlen($decoded) - strlen($salt) - strlen($iv));
		
		$key = $this->pbkdf2($this->password, $salt, $this->interactions, $this->keySize);		
		
		$decryted = mcrypt_decrypt($this->cipher, $key, $message, $this->mode, $iv);
		
		return trim($decryted);
    }

}
