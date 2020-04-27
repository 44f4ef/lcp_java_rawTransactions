package LCP;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

public final class LCPKey implements KeyFunctions {
    public final DeterministicKey key;

    public LCPKey(DeterministicKey key){
        this.key = key;
    }


    public LCPKey deriveChild(int changeIndex, int addressIndex){
        ChildNumber changeCN = new ChildNumber(changeIndex,false);
        ChildNumber addressIndexCN = new ChildNumber(addressIndex,false);
        DeterministicKey keyChange = HDKeyDerivation.deriveChildKey(key,changeCN);
        DeterministicKey signingKey = HDKeyDerivation.deriveChildKey(keyChange,addressIndexCN);
        return new LCPKey(signingKey);
    }


    public String signTransaction(byte[] preSignature) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        Sha256Hash hash = Sha256Hash.wrap(preSignature);
        ECKey.ECDSASignature signature = this.key.sign(hash);

        /*LCP signatures are a concatenation of the r and s point without the
        *leading 0x00
        * this next code segment just prepares the signatures to conform to that
        * standard.
        * */
        byte[] rPointBytes = signature.r.toByteArray();
        byte[] sPointBytes = signature.s.toByteArray();
        byte[] sigBytesHas00 = new byte[rPointBytes.length+ sPointBytes.length];
        System.arraycopy(rPointBytes, 0, sigBytesHas00, 0, rPointBytes.length);
        System.arraycopy(sPointBytes, 0, sigBytesHas00, rPointBytes.length, sPointBytes.length);
        byte[] sigBytes = Arrays.copyOfRange(sigBytesHas00, 1,sigBytesHas00.length);

        return Base64.getEncoder().encodeToString(sigBytes);
    }

}
