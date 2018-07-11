package pay;

/**
 */

public interface IAliResult {

    void  onAliFailed(int code);

    void onAliSuccess();

}
