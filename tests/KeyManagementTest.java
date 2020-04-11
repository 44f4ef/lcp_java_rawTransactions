package LCP.tests;


import LCP.KeyManagement;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.wallet.UnreadableWalletException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class KeyManagementTest {
    public static void main(String[] argvs) throws UnreadableWalletException, NoSuchAlgorithmException {
        makeSignature();
    }
    public static void buildWalletKey()throws UnreadableWalletException{
        String mnemonic = "glory donate cheese direct soda recycle tenant crystal curious dance paper pyramid";
        String password = "";
        DeterministicKey walletKey = KeyManagement.deriveWalletKey(mnemonic,password);
        System.out.println(walletKey.toString());
    }

    public static void buildDeviceKey()throws UnreadableWalletException{
        String mnemonic = "glory donate cheese direct soda recycle tenant crystal curious dance paper pyramid";
        String password = "";
        DeterministicKey deviceKey = KeyManagement.deriveDeviceKey(mnemonic,password);

    }

    public static void makeSignature() throws UnreadableWalletException, NoSuchAlgorithmException {
        String mnemonic = "glory donate cheese direct soda recycle tenant crystal curious dance paper pyramid";
        String password = "";
        String testString = "nignog";
        DeterministicKey walletKey = KeyManagement.deriveWalletKey(mnemonic,password);

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] rawHash = digest.digest(testString.getBytes(StandardCharsets.UTF_8));
        /*Sha256Hash hash = Sha256Hash.wrap(rawHash);
        ECKey.ECDSASignature signature = walletKey.sign(hash);
        byte[] rPointBytes = signature.r.toByteArray();
        byte[] sPointBytes = signature.s.toByteArray();
        byte[] signatureBytes = new byte[rPointBytes.length+ sPointBytes.length];
        System.arraycopy(rPointBytes, 0, signatureBytes, 0, rPointBytes.length);
        System.arraycopy(sPointBytes, 0, signatureBytes, rPointBytes.length, sPointBytes.length);
        byte[] sigBytes = Arrays.copyOfRange(signatureBytes, 1,signatureBytes.length);
        System.out.println(Arrays.toString(signatureBytes));
    */
        //String sigHex = bytesToHex(sigBytes);
       // System.out.println(sigHex);
        String sigStr = KeyManagement.sign(walletKey,rawHash);
        System.out.println(sigStr);
    }


    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

}