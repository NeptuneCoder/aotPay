package pay;

import java.util.Map;

/**
 */

public interface IAliResultCallback {

    void onResult(Map<String, String> res);

    String getOrderInfo();

}
