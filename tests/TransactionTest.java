package LCP.tests;

import LCP.*;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class TransactionTest {
    public static void main(String[] args) throws Exception {
        //signatureTest();
        makeTransaction();
    }


    public static void makeTransaction() throws Exception {

        String mnemonic = LCPConstants.MNEMONIC;
        String password = LCPConstants.PASSWORD;
        DeterministicKey walletKey = KeyManagement.deriveWalletKey(mnemonic,password);
        DeterministicKey changeKey = HDKeyDerivation.deriveChildKey(walletKey,0);
        DeterministicKey signerKey = HDKeyDerivation.deriveChildKey(changeKey,0);
        LCPKey key = new LCPKey(walletKey);
        //DeterministicKey signingKey = key.deriveChild(0,0).key;

        String pubKeyB64 = KeyManagement.getPublicKeyBase64(walletKey,0,0);
        System.out.println("public key is : "+pubKeyB64);

        String address = Addresses.makeAddressFromPubKey(pubKeyB64);
        System.out.println("address is : "+address);


        JSONObject testUnit = manualUnit();//manualUnit();//parseUnit(LCPConstants.TEST_UNIT);
        Transaction transaction = new Transaction((JSONObject[])testUnit.get("outputs"),
                (JSONObject[])testUnit.get("inputs"),(JSONObject[])testUnit.get("signers"),
                (JSONObject) testUnit.get("network"));

        String signedUnit = transaction.sign(signerKey);

        System.out.println("signed unit : "+ signedUnit);

    }


    public static JSONObject parseUnit(String unit){
        JSONObject testUnit = new JSONObject(unit);

        JSONObject networkInfo = new JSONObject();

        networkInfo.put("witness_list_unit","FxtHGkOHjv7aXajkg00/22Xp7VVXZEvT5cXfe8BSZEQ=");
        networkInfo.put("last_stable_mc_ball_unit",testUnit.get("last_ball_unit"));
        networkInfo.put("last_stable_mc_ball",testUnit.get("last_ball"));
        JSONArray parent_units_jsonarray = (JSONArray) testUnit.get("parent_units");

        String[] parent_units = new String[parent_units_jsonarray.length()];
        for (int i=0;i<parent_units_jsonarray.length();i++){
            parent_units[i] = (String) parent_units_jsonarray.get(i);
        }
        networkInfo.put("parent_units",parent_units);

        JSONArray messages = (JSONArray) testUnit.get("messages");
        JSONObject message = messages.getJSONObject(0);
        JSONObject payload = (JSONObject) message.get("payload");
        JSONArray unitInputs = (JSONArray) payload.get("inputs");
        ArrayList<JSONObject> unitInputJSONArr = new ArrayList<>();
        if (unitInputs != null) {
            for (int i=0;i<unitInputs.length();i++){
                unitInputJSONArr.add(unitInputs.getJSONObject(i));
            }
        }
        JSONObject[] testInputs = new JSONObject[unitInputJSONArr.size()];
        for (int i=0;i<unitInputJSONArr.size();i++){
            testInputs[i] = unitInputJSONArr.get(i);
        }

        ArrayList<JSONObject> unitOutputJSONArr = new ArrayList<JSONObject>();
        JSONArray unitOutputs = (JSONArray) payload.get("outputs");
        if (unitOutputs != null) {
            for (int i=0;i<unitOutputs.length();i++){
                unitOutputJSONArr.add(unitOutputs.getJSONObject(i));
            } // parent unit 1 input 1 output 1 something missing in header
        }//parent units 2 , input 1 output 2 right

        JSONObject[] testOutputs = new JSONObject[unitOutputJSONArr.size()];
        for (int i=0;i<unitOutputJSONArr.size();i++){
            testOutputs[i] = unitOutputJSONArr.get(i);
        }

        JSONObject signer = new JSONObject();
        signer.put("address","V2RGTCA5ZYDNHF7C3EUJMNOFGY3NALKR");
        JSONObject[] signers = {signer};

        JSONObject testTransaction = new JSONObject();
        testTransaction.put("header",networkInfo);
        testTransaction.put("inputs",testInputs);
        testTransaction.put("outputs",testOutputs);
        testTransaction.put("signers",signers);
        testTransaction.put("network",networkInfo);
        return testTransaction;
    }


    public static JSONObject manualUnit(){
        JSONObject networkInfo = new JSONObject();

        networkInfo.put("witness_list_unit","FxtHGkOHjv7aXajkg00/22Xp7VVXZEvT5cXfe8BSZEQ=");
        networkInfo.put("last_stable_mc_ball_unit","dY6IDjJ9Bsc/P6ANQn2cl3xuD5MGxXTywP/U1oMtkeQ=");
        networkInfo.put("last_stable_mc_ball","7hnIVnBCGeq+77tRwiVnrCGWUA8GlSp1K3/SNtg7FUM=");
        String[] parent_units = {"hzi/+wkcfdwjv4jz/AgBS4U6Ki0cBZC6RWeWGJVIe90="};
        networkInfo.put("parent_units",parent_units);

        JSONObject testInput = new JSONObject();
        testInput.put("unit","tDJcPp0o3bpnh0A8ZIXK4OSnXF0vxSid+76+ZjrkIvA=");
        testInput.put("message_index",0);
        testInput.put("output_index",0);
        JSONObject[] testInputs = {testInput};

        JSONObject testOutput = new JSONObject();
        testOutput.put("address","V2RGTCA5ZYDNHF7C3EUJMNOFGY3NALKR");
        testOutput.put("amount",1167030);
        JSONObject testOutput2 = new JSONObject();
        testOutput2.put("address","V2RGTCA5ZYDNHF7C3EUJMNOFGY3NALKR");
        testOutput2.put("amount",(int) 500);
        JSONObject[] testOutputs = {testOutput,testOutput2};

        JSONObject signer = new JSONObject();
        signer.put("address","V2RGTCA5ZYDNHF7C3EUJMNOFGY3NALKR");
        JSONObject[] signers = {signer};

        JSONObject testTransaction = new JSONObject();

        testTransaction.put("header",networkInfo);
        testTransaction.put("inputs",testInputs);
        testTransaction.put("outputs",testOutputs);
        testTransaction.put("signers",signers);
        testTransaction.put("network",networkInfo);
        return testTransaction;
    }
}
