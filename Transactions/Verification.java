package LCP.Transactions;

import org.json.JSONObject;

import java.util.ArrayList;

public interface Verification {
    public boolean checkOutputs(JSONObject[] outputs) throws Exception;
    public boolean checkInputs(JSONObject[] inputs) throws Exception;
    public boolean checkAuthors(JSONObject[] authors) throws Exception;
    public boolean checkNetworkInfoIntegrity(JSONObject headerInfo) throws Exception;
}
