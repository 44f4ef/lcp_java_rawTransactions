package LCP.Transactions;

import org.json.JSONObject;
import LCP.Addresses;
import java.util.ArrayList;
/*
public class Verifier implements Verification{

    @Override
    public boolean checkOutputs(JSONObject[] outputs) throws Exception {
        for(JSONObject output : outputs){

            if(output.has("amount") &&
                    output.has("address") &&
                    (2 == output.keySet().size()) &&
                    Addresses.isValidAddress((String) output.get("address")) &&
                    (output.get("amount").getClass().getSimpleName().equals("Integer"))
                ){
                return true;
            }
            else{
                //throw Exception("error in amount or address")
            }
        }
        return false;
    }

    @Override
    public void checkInputs(JSONObject[] inputs) throws Exception {

    }

    @Override
    public void checkAuthors(ArrayList<JSONObject> authors) throws Exception {

    }

    @Override
    public void checkNetworkInfoIntegrity(JSONObject headerInfo) {

    }
}
*/