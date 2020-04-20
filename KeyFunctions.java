package LCP;

import java.security.NoSuchAlgorithmException;

interface KeyFunctions {
    public String signTransaction(byte[] rawSHA256Hash) throws NoSuchAlgorithmException;
}
