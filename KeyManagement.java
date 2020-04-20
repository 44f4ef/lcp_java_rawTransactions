package LCP;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicHierarchy;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.UnreadableWalletException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;


public class KeyManagement implements KeyFunctions{
    /**
     * returns a Deterministic Hierarchy with the master key at the top
     * @param mnemonic<String> 12 words
     * @param password<String> passphrase
     * @return DeterministcHierarchy
     * */
    public static DeterministicHierarchy deriveMasterKeyHierarchy(String mnemonic,
                                                            String password)
            throws UnreadableWalletException {

        long unixTime = Instant.now().getEpochSecond();
        //"yard impulse luxury drive today throw farm pepper survey wreck glass federal";
        DeterministicSeed masterSeed = new DeterministicSeed(mnemonic,
                null, password, unixTime);
        DeterministicKey masterKey = HDKeyDerivation.createMasterPrivateKey(masterSeed.getSeedBytes());
        DeterministicHierarchy keyHierarchy = new DeterministicHierarchy(masterKey);
        return keyHierarchy;
    }

    /**
     * derives an indepedent wallet key from the path "m/44'/0'/0'" does not include
     * the master key or any hierarchy. this must be placed in a hierarchy indepedently
     * @param mnemonic<String> 12 word key seed
     * @param password<String> key password
     * @return DeterministicKey
     * */
    public static DeterministicKey deriveWalletKey(String mnemonic, String password)
            throws UnreadableWalletException {

        long unixTime = Instant.now().getEpochSecond();
        //"yard impulse luxury drive today throw farm pepper survey wreck glass federal";
        DeterministicSeed masterSeed = new DeterministicSeed(mnemonic,
                null, password, unixTime);
        DeterministicKey masterKey = HDKeyDerivation.createMasterPrivateKey(masterSeed.getSeedBytes());
        DeterministicHierarchy keyHierarchy = new DeterministicHierarchy(masterKey);
        ChildNumber purpose = new ChildNumber(44,true);
        ChildNumber coinType = new ChildNumber(0,true);
        ChildNumber account = new ChildNumber(0,true);
        List<ChildNumber> path = new ArrayList<>(){{
            add(purpose);
            add(coinType);
            add(account);
        }};
        return keyHierarchy.get(path, false,true);

    }


    public static DeterministicKey deriveSigningKey(DeterministicKey walletKey, int change, int account){
        DeterministicKey changeKey = HDKeyDerivation.deriveChildKey(walletKey, change);

        return HDKeyDerivation.deriveChildKey(changeKey,account);
    }

    public static DeterministicKey deriveAddressKey(DeterministicKey walletKey){
        DeterministicKey addressKeyNoPrivate = walletKey.dropPrivateBytes();
        return addressKeyNoPrivate.dropParent();
    }

    public static DeterministicKey deriveAddressKey(String mnemonic, String password)
            throws UnreadableWalletException {
        DeterministicKey walletKey = deriveWalletKey(mnemonic,password);
        return deriveAddressKey(walletKey);
    }
    /**
     * device keys are used to validate WebSocket connections with the hub
     * @param mnemonic<String> 12 words
     * @param password <String> passphrase
     * */
    public static DeterministicKey deriveDeviceKey(String mnemonic, String password)
        throws UnreadableWalletException{

        long unixTime = Instant.now().getEpochSecond();
        //"yard impulse luxury drive today throw farm pepper survey wreck glass federal";
        DeterministicSeed masterSeed = new DeterministicSeed(mnemonic,
                null, password, unixTime);
        DeterministicKey masterKey = HDKeyDerivation.createMasterPrivateKey(masterSeed.getSeedBytes());
        DeterministicHierarchy keyHierarchy = new DeterministicHierarchy(masterKey);
        ChildNumber purpose = new ChildNumber(1,true);
        List<ChildNumber> path = new ArrayList<>(){{
            add(purpose);
        }};

        return keyHierarchy.get(path,false,true);
    }


    public static String sign(DeterministicKey key, byte[] rawHash) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        Sha256Hash hash = Sha256Hash.wrap(rawHash);
        ECKey.ECDSASignature signature = key.sign(hash);
        byte[] rPointBytes = signature.r.toByteArray();
        byte[] sPointBytes = signature.s.toByteArray();
        byte[] concatedSigPoints = new byte[rPointBytes.length+ sPointBytes.length];
        System.arraycopy(rPointBytes, 0, concatedSigPoints, 0, rPointBytes.length);
        System.arraycopy(sPointBytes, 0, concatedSigPoints, rPointBytes.length, sPointBytes.length);
        if(concatedSigPoints[0]==0){
            byte[] sigBytes = Arrays.copyOfRange(concatedSigPoints, 1,concatedSigPoints.length);
            return Base64.getEncoder().encodeToString(sigBytes);
        }
        else{
            System.out.println("has 00 "+Arrays.toString(concatedSigPoints));
            System.out.println("doesnt have "+ Arrays.toString(concatedSigPoints));
            return Base64.getEncoder().encodeToString(concatedSigPoints);
        }

    }

    /**
     * @param key<DeterministicKey> assumed to be a either wallet key or address key with path "m/44'/0'/0'
     * @param change<Integer> zero or one
     * @param addressIndex<Integer> index of address
     */
    public static String getPublicKeyBase64(DeterministicKey key, int change, int addressIndex) {
        ChildNumber changeCN = new ChildNumber(change,false);
        ChildNumber addressIndexCN = new ChildNumber(addressIndex,false);
        DeterministicKey keyChange = HDKeyDerivation.deriveChildKey(key,changeCN);
        DeterministicKey addressPubKey = HDKeyDerivation.deriveChildKey(keyChange,addressIndexCN);
        byte[] addressBytes = addressPubKey.getPubKey();
        return Base64.getEncoder().encodeToString(addressBytes);
    }


    @Override
    public String signTransaction(byte[] rawSHA256Hash) throws NoSuchAlgorithmException {
        return null;
    }
}


