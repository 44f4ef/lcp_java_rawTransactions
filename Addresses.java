package LCP;
import java.util.ArrayList;
import java.util.Arrays;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Base32;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.json.JSONObject;

public class Addresses {

    /**
     * @param releaseCondition example ["sig",{"pubkey":"Ald9tkgiUZQQ1djpZgv2ez7xf1ZvYAsTLhudhvn0931w"}]
     * @return LCPAddress example A2WWHN7755YZVMXCBLMFWRSLKSZJN3FU length must be 32 char always.
     * @throws Exception
     */
    public static String generateAddress(Object[] releaseCondition) throws Exception {
        if(!(releaseCondition[0] instanceof String) || !(releaseCondition[1] instanceof JSONObject)){
            throw new Exception("malformed releaseCondition");
        }
        String releaseConditionString = LCPUtils.toString(releaseCondition);
        byte[] addressHash = generateHash(releaseConditionString.getBytes());
        return generateAddressCore(addressHash);
    }


    /**
     * @param base64publicKeyStr example: "Ald9tkgiUZQQ1djpZgv2ez7xf1ZvYAsTLhudhvn0931w"
     * @return example: 0BIB5FSRENMQ4CYADBS2OM7I3IEE32ZQG 33 length. always start with '0'
     * @throws Exception
     */
    public static String generateDeviceAddress(String base64publicKeyStr) throws Exception {
        String publicKeyStr = LCPUtils.toString(base64publicKeyStr);
        byte[] addressHash = generateHash(publicKeyStr.getBytes());
        return "0" + generateAddressCore(addressHash);
    }


    public static boolean isValidAddress(String address) throws Exception {
        if(address.length()!= 32){ //&&  address.length() != 48
            throw new Exception("wrong encoded length");
        }
        Base32 base_32 = new Base32();
        byte[] addressDecoded = base_32.decode(address);
        String binaryAddress = bytesToBinary(addressDecoded);
        String[] seperatedData = seperateData(binaryAddress);

        return verifyChecksum(binaryToByte(seperatedData[0]),binaryToByte(seperatedData[1]));
    }


    private static boolean verifyChecksum(byte[] checksum, byte[] data) throws NoSuchAlgorithmException {
        return checksum == generateChecksum(data);
    }


    private static String generateAddressCore(byte[] digest) throws Exception {
        byte[] truncatedHash = Arrays.copyOfRange(digest, 4, digest.length);
        byte[] checksum = generateChecksum(truncatedHash);
        byte[] mixedData = mixData(truncatedHash,checksum);
        Base32 base_32 = new Base32();
        return base_32.encodeAsString(mixedData);
    }


    private static byte[] generateHash(byte[] data){
        RIPEMD160Digest digest = new RIPEMD160Digest();
        digest.update(data,0,data.length);
        byte[] o = new byte[digest.getDigestSize()];
        digest.doFinal(o,0);
        return o;
    }


    private static byte[] generateChecksum(byte[] data) throws NoSuchAlgorithmException {
        /**
         * @param data usually a public key
         * @throws NoSuchAlgorithmException
         * @returns checksum this special checksum will be mixed with data to for
         *          the address
         */
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(data);
        byte[] checksum;
        checksum = new byte[]{digest[5], digest[13], digest[21], digest[29]};
        return checksum;
    }


    private static byte[] mixData(byte[] digest, byte[] checksum) throws Exception {
        if(checksum.length*8 != 32){
            throw new Exception("checksum length is not 32");
        }
        ArrayList<Integer> offsets;
        int combinedLength = (digest.length + checksum.length)*8;
        if (combinedLength == 160){
            offsets = calculateOffsets(160);//this can be made more efficient
        }
        else if(combinedLength == 228){
            offsets = calculateOffsets(288);//can be simplified
        }
        else {
            throw new Exception("bad length for checksum and digest data");
        }

        ArrayList<String> mixedDataBinary = new ArrayList<>();
        String digestBinary = bytesToBinary(digest);
        String checksumBinary = bytesToBinary(checksum);
        String[] checksumBits = checksumBinary.split("(?!^)");
        int start = 0;
        int end;
        for(int i = 0; i < offsets.size(); i++){
            end = offsets.get(i) - i;
            mixedDataBinary.add(digestBinary.substring(start,end));
            mixedDataBinary.add(checksumBits[i]);
            start = end;
        }

        if (start < digestBinary.length()){
            mixedDataBinary.add(digestBinary.substring(start));
        }
        String mixedDataBinaryString = String.join("", mixedDataBinary);
        return binaryToByte(mixedDataBinaryString);
    }


    private static String[] seperateData(String binary) throws Exception {
        ArrayList<Integer> offsets = calculateOffsets(binary.length());
        int start = 0 ;
        ArrayList<String> dataBinary = new ArrayList<>();
        ArrayList<String> checksumBinary = new ArrayList<>();

        for (Integer offset : offsets) {
            dataBinary.add(binary.substring(start,offset));
            checksumBinary.add(binary.substring(offset, offset + 1));
            start = offset+1;
        }

        if(start < binary.length()){
            dataBinary.add(binary.substring(start));
        }
        String dataString  = String.join("", dataBinary);
        String checksumString  = String.join("", checksumBinary);

        return new String[]{checksumString, dataString};
    }


    private static ArrayList<Integer> calculateOffsets(Integer length) throws Exception {

        if(length != 160 && length != 288){
            throw new IllegalArgumentException("wrong length. for offset");
        }

        final String PI ="14159265358979323846264338327950288419716939937510";
        String[] numStrArray = PI.split("");
        int[] relativeOffsets = new int[PI.length()];
        for (int i = 0; i < numStrArray.length;i++){
            relativeOffsets[i] = Integer.parseInt(numStrArray[i]);
        }

        int index = 0;
        int offset = 0;
        ArrayList<Integer> offsetsArrayList = new ArrayList<Integer>();
        for(int i = 0; i < length ; i++){
            if(relativeOffsets[i]==0){
                continue;
            }
            offset += relativeOffsets[i];
            if(length == 288){
                offset += 4;
            }
            if(offset >= length){
                break;
            }
            offsetsArrayList.add(offset);
            index += 1;
        }

        if (index !=32){
            throw new Exception("wrong number of checksum bits");
        }
        return offsetsArrayList;
    }


    private static String bytesToBinary(byte[] data){
        String zeroString = "00000000";
        String[] binaryArray = new String[data.length];
        for (int i =0 ; i < data.length ; i++) {
            String bin = "";
            if(data[i] < 0){
                bin = Integer.toString(data[i] & 0xFF, 2);
            }
            else{
                bin = Integer.toString(data[i], 2);
            }
            if(bin.length() < 8){
                String newBin = zeroString.substring(bin.length()) + bin;
                binaryArray[i] = newBin;
            }
            else{
                binaryArray[i] = bin;
            }
        }

        return String.join("", binaryArray);
    }

    private static byte[] binaryToByte(String data){
        int byteLength = data.length()/8;
        byte[] byteData = new byte[byteLength];

        for(int i = 0; i < byteLength; i++){
            byteData[i] = (byte) Integer.parseInt(data.substring(i*8,i*8+8),2);
        }
        return byteData;
    }
}
