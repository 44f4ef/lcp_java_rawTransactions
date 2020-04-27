package LCP;

import org.bitcoinj.crypto.DeterministicKey;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

    }


    public final String sign(DeterministicKey signer) throws NoSuchAlgorithmException{
        this.signature = KeyManagement.sign(signer,this.preSignature);
        this.isSigned = true;
        JSONObject signatureObj  = new JSONObject();
        signatureObj.put("r",this.signature);

        String pubKeyB64 = KeyManagement.getPublicKeyBase64(signer,0,0);
        JSONObject publicKeyJSON = new JSONObject();
        publicKeyJSON.put("pubkey",pubKeyB64);
        Object[] releaseCondition  = new Object[]{"sig",publicKeyJSON};
        //signatureObj.put("definition",releaseCondition);
        this.signatureObject =  signatureObj;
        return getSignedUnit();
    }


    public String getSignedUnit() throws NoSuchAlgorithmException {
        JSONObject signedUnit = makeSignedUnit(this.signatureObject);

        return LCPUtils.toString(signedUnit);
    }


    private byte[] makePreSignature() throws NoSuchAlgorithmException {
        JSONObject header = makeHeader(this.signers,this.networkInfo);
        JSONObject preSigObject = new JSONObject(header,JSONObject.getNames(header));
        JSONObject payload = makePayload(this.inputs,this.outputs);
        JSONObject msg = makeMsg(payload,false);
        JSONObject[] msgArr = {msg};
        preSigObject.put("messages",msgArr);
        String preSigObjectStr = LCPUtils.toString(preSigObject);
        byte[] preSigObjectBytes = preSigObjectStr.getBytes(StandardCharsets.UTF_8);
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] signedBytes = md.digest(preSigObjectBytes);

        return signedBytes;
    }


    private JSONObject makeSignedUnit(JSONObject signatureObject)
            throws NoSuchAlgorithmException {
        JSONObject baseSignerObject = this.signers[0];
        JSONObject authSignerObject = new JSONObject(baseSignerObject,
                JSONObject.getNames(baseSignerObject)).put("authentifiers",
                signatureObject);
        JSONObject[] authSignerArr = {authSignerObject};
        return makeUnit(this.outputs,this.inputs,authSignerArr,this.networkInfo);

    }


    private JSONObject makeUnit(JSONObject[] outputs,
                                JSONObject[] inputs,
                                JSONObject[] signerInfo,
                                JSONObject networkInfo) throws NoSuchAlgorithmException {


        JSONObject payload = makePayload(inputs,outputs);
        JSONObject msg = makeMsg(payload,true);
        JSONObject[] msgArr = {msg};
        JSONObject header = makeHeader(signerInfo, networkInfo);
        JSONObject unit = new JSONObject(header, JSONObject.getNames(header));

        int headerCommission = calcHeaderCommission(header);//changed this
        int payloadCommission = calcPayloadCommission(msgArr);

        JSONObject headerNoAuth = makeHeader(this.signers,networkInfo);
        String unitHash =
                calcUnitHash(header, makeMsg(payload,false));

        unit.put("unit",unitHash);
        unit.put("headers_commission",headerCommission);
        unit.put("payload_commission",payloadCommission);

        unit.put("messages",msgArr);
        return unit;

    }

    private JSONObject makeMsg(JSONObject payload,
                               boolean includeInputOutputs)//or just metadata
            throws NoSuchAlgorithmException {

        JSONObject message = new JSONObject();
        String app = "payment";
        String payloadLocation = "inline";
        message.put("app",app);
        message.put("payload_location",payloadLocation);
        message.put("payload_hash",LCPUtils.shaHash(payload));
        if(includeInputOutputs){
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
        return header;
    }
/*
    private Boolean verifyInputAmount(JSONObject[] inputs){

    }
*/
    private int calcHeaderCommission(JSONObject header){
        final int PARENT_UNITS_SIZE = 88;
        JSONObject newHeader = new JSONObject(header, JSONObject.getNames(header));
        newHeader.remove("parent_units");

        return calcSize(newHeader) + PARENT_UNITS_SIZE;
    }


    private int calcPayloadCommission(JSONObject[] payload){

        return calcSize(payload);
    }


    private int calcSize(Object element){

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
                length += calcSize(jsonObj.get(key));

            }
            return length;
        }
        else if(element instanceof Object[]){
            int length = 0;
            Object[] headerElementArray = (Object[]) element;
            for(Object arrMember : headerElementArray){

                length += calcSize(arrMember);

            }
            return length;
        }
        else if(element instanceof Boolean){
            return 1;
        }
        return 0;
    }


    private String calcUnitHash(JSONObject header,//header commission
                                JSONObject msgNoPayload)
            throws NoSuchAlgorithmException {

        JSONObject headerNoAuthentification = makeHeader(this.signers, this.networkInfo);
        JSONObject partialUnit = new JSONObject(header, JSONObject.getNames(header));
        JSONObject[] messages = {msgNoPayload};
        partialUnit.put("messages",messages);
        String contentHash = LCPUtils.shaHash(partialUnit);
        headerNoAuthentification.put("content_hash",contentHash);

        return LCPUtils.shaHash(headerNoAuthentification);
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
