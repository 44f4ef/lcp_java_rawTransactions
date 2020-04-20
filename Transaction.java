package LCP;

import org.bitcoinj.crypto.DeterministicKey;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Iterator;
//total_amount + objUnit.headers_commission + naked_payload_commission)
//get total payload
public final class Transaction {

    private final JSONObject[] outputs;
    private final JSONObject[] inputs;
    private final JSONObject[] signers;
    private final JSONObject networkInfo;
    private Boolean isSigned = false;
    //private final JSONObject unSignedHeader = null;
    private final byte[] preSignature;
    private String signature = null;
    private final JSONObject signedUnit = null;
    private JSONObject signatureObject = null;

    public Transaction(JSONObject[] outputs, JSONObject[] inputs,
                       JSONObject[] signers, JSONObject networkInfo)
            throws NoSuchAlgorithmException {

        //implement verifier
        this.outputs = outputs;
        this.inputs = inputs;
        this.signers = signers;
        this.networkInfo = networkInfo;
        //this.unSignedUnit = createUnit(outputs,inputs,signers, networkInfo);
        this.preSignature = makePreSignature();
        System.out.println("this is the preSignature :"+ Arrays.toString(this.preSignature));
    }

/*
    public final String sign(KeyFunctions signer) throws NoSuchAlgorithmException {
        this.signature = signer.signTransaction(this.preSignature);
        this.isSigned = true;
        JSONObject signatureObj  = new JSONObject();
        signatureObj.put("r",this.signature);
        this.signatureObject =  signatureObj;
        return getSignedUnit();
    }

 */

    public final String sign(DeterministicKey signer) throws NoSuchAlgorithmException{
        this.signature = KeyManagement.sign(signer,this.preSignature);
        System.out.println("signature is "+this.signature);
        //System.out.println("signer pub key "+ signer.getPubKey());
        this.isSigned = true;
        JSONObject signatureObj  = new JSONObject();
        signatureObj.put("r",this.signature);
        this.signatureObject =  signatureObj;
        return getSignedUnit();
    }


    public String getSignedUnit() throws NoSuchAlgorithmException {
        JSONObject signedUnit = makeSignedUnit(this.signatureObject);
        System.out.println("signed Unit : "+ signedUnit);

        return LCPUtils.toString(signedUnit);
    }


    private byte[] makePreSignature() throws NoSuchAlgorithmException {
        JSONObject header = makeHeader(this.signers,this.networkInfo);
        JSONObject preSigObject = new JSONObject(header,JSONObject.getNames(header));
        JSONObject payload = makePayload(this.inputs,this.outputs);
        JSONObject msg = makeMsg(payload,false);
        JSONObject[] msgArr = {msg};
        preSigObject.put("messages",msgArr);
        System.out.println("presig Object :"+preSigObject);
        String preSigObjectStr = LCPUtils.toString(preSigObject);
        //String preSigObjectStr = preSigObject.toString();

        System.out.println("preSigObjectStr "+preSigObjectStr);
        byte[] preSigObjectBytes = preSigObjectStr.getBytes(StandardCharsets.UTF_8);
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] signedBytes = md.digest(preSigObjectBytes);

        return signedBytes;
    }


    private JSONObject makeSignedUnit(JSONObject signatureObject)
            throws NoSuchAlgorithmException {
        JSONObject baseSignerObject = this.signers[0];
        System.out.println("signers object "+this.signers[0]);
        JSONObject authSignerObject = new JSONObject(baseSignerObject,
                JSONObject.getNames(baseSignerObject)).put("authentifiers",
                signatureObject);
        JSONObject[] authSignerArr = {authSignerObject};
        System.out.println("signer array : "+ Arrays.toString(authSignerArr));
        //JSONObject[] header = {makeHeader(this.signers,this.networkInfo)};
        return makeUnit(this.outputs,this.inputs,authSignerArr,this.networkInfo);

    }


    private JSONObject makeUnit(JSONObject[] outputs,
                                JSONObject[] inputs,
                                JSONObject[] signerInfo,
                                JSONObject networkInfo)
            throws NoSuchAlgorithmException {


        JSONObject payload = makePayload(inputs,outputs);
        JSONObject msg = makeMsg(payload,true);
        JSONObject[] msgArr = {msg};
        JSONObject header = makeHeader(signerInfo, networkInfo);
        //this.unSignedHeader
        JSONObject unit = new JSONObject(header, JSONObject.getNames(header));

        int headerCommission = calcHeaderCommission(header);//changed this
        int payloadCommission = calcPayloadCommission(msgArr);
        JSONObject headerNoAuth = makeHeader(this.signers,networkInfo);
        String unitHash =
                calcUnitHash(header, headerNoAuth, makeMsg(payload,false));

        unit.put("unit",unitHash);
        unit.put("header_commission",headerCommission);
        unit.put("payload_commission",payloadCommission);

        unit.put("messages",msgArr);
        return unit;

    }

    private JSONObject makeMsg(JSONObject payload,
                               boolean withPayload)//or just metadata
            throws NoSuchAlgorithmException {

        JSONObject message = new JSONObject();
        String app = "payment";
        String payloadLocation = "inline";
        message.put("app",app);
        message.put("payload_location",payloadLocation);
        message.put("payload_hash",LCPUtils.shaHash(payload));
        if(withPayload){
            message.put("payload",payload);
            return message;
        }
        return message;
    }


    private JSONObject makePayload(JSONObject[] inputs, JSONObject[] outputs){
        JSONObject payload = new JSONObject();
        payload.put("outputs",outputs);
        payload.put("inputs",inputs);
        return payload;
    }


    private JSONObject makeHeader(JSONObject[] authors,
                                  JSONObject chainInfoFromNetwork){

        JSONObject header = new JSONObject();
        String version = "1.0";
        String alt = "1";
        header.put("last_ball",chainInfoFromNetwork.get("last_stable_mc_ball"));
        header.put("last_ball_unit",chainInfoFromNetwork.get("last_stable_mc_ball_unit"));
        header.put("version",version);
        header.put("alt",alt);
        header.put("authors",authors);
        header.put("witness_list_unit",chainInfoFromNetwork.get("witness_list_unit"));
        header.put("parent_units",chainInfoFromNetwork.get("parent_units"));
        /*
        if(!withAuthentication){
            JSONObject newHeader = new JSONObject(header, JSONObject.getNames(header));
            JSONObject[] oldAuthorsArr = (JSONObject[]) header.get("authors");
            JSONObject[] newAuthorsArr = new JSONObject[oldAuthorsArr.length];
            JSONObject newAuthor = new JSONObject();
            for(int i =0 ; i < oldAuthorsArr.length;i++){
                newAuthorsArr[i] = newAuthor.put("address",oldAuthorsArr[i].get("address"));
            }
            newHeader.put("authors",newAuthorsArr);
            return newHeader;
        }
        else {
            return header;
        }
        */
        System.out.println("this is the header "+header);
        return header;
    }


    private int calcHeaderCommission(JSONObject header){
        final int PARENT_UNITS_SIZE = 88;
        JSONObject newHeader = new JSONObject(header, JSONObject.getNames(header));
        newHeader.remove("parent_units");
        System.out.println("header without parent_units "+ newHeader);
        System.out.println("header size is "+(calcSize(newHeader) + PARENT_UNITS_SIZE));

        return calcSize(newHeader) + PARENT_UNITS_SIZE;
    }


    private int calcPayloadCommission(JSONObject[] payload){

        System.out.print("this the payload");
        //System.out.println(payload);
        return calcSize(payload);
    }


    private int calcSize(Object element){
        System.out.print("the element is ");
        System.out.println(element);
        if(element == null){
            return 0;
        }
        else if(element instanceof String){
            return  ((String) element).length();
        }
        else if(element instanceof Integer){
            return 8;
        }
        else if(element instanceof JSONObject){
            int length = 0;
            JSONObject jsonObj = (JSONObject) element;
            Iterator<String> keys = jsonObj.keys();
            while(keys.hasNext()){
                String key = keys.next();
                System.out.print("key is ");
                System.out.println(key);
                System.out.print("attribute is ");
                System.out.println(jsonObj.get(key));
                length += calcSize(jsonObj.get(key));
                System.out.print("length is  ");
                System.out.println(length);
            }
            return length;
        }
        else if(element instanceof Object[]){
            int length = 0;
            Object[] headerElementArray = (Object[]) element;
            for(Object arrMember : headerElementArray){
                System.out.print("arr member is ");
                System.out.println(arrMember);
                length += calcSize(arrMember);
                System.out.print("length is ");
                System.out.println(length);
            }
            return length;
        }
        else if(element instanceof Boolean){
            return 1;
        }
        return 0;
    }


    private String calcUnitHash(JSONObject header, JSONObject headerNoAuth,
                                JSONObject msgNoPayload)
            throws NoSuchAlgorithmException {

        JSONObject partialUnit = new JSONObject(header, JSONObject.getNames(header));
        JSONObject[] messages = {msgNoPayload};
        partialUnit.put("messages",messages);
        String contentHash = LCPUtils.shaHash(partialUnit);
        headerNoAuth.put("content_hash",contentHash);

        return LCPUtils.shaHash(headerNoAuth);
    }

    public JSONObject[] getOutputs() {
        return outputs;
    }

    public JSONObject[] getInputs() {
        return inputs;
    }

    public JSONObject[] getSigners() {
        return signers;
    }

    public JSONObject getNetworkInfo() {
        return networkInfo;
    }

    public Boolean getSigned() {
        return isSigned;
    }


    public byte[] getPreSignature() {
        return preSignature;
    }

    public String getSignature() {
        return signature;
    }
}
