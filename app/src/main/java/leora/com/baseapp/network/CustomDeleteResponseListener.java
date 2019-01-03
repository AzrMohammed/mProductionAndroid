package leora.com.baseapp.network;

import org.json.JSONObject;

/**
 * Created by AZR on 26-12-2018.
 */

public interface CustomDeleteResponseListener {

    public void responseSuccess(JSONObject response);

    public void responseFailure(JSONObject response);

    public void responseError(String message);

}
