package hf.user.client;

import hf.base.client.BaseClient;
import hf.base.model.RemoteParams;

import java.util.Map;

public class PayClient extends BaseClient {

    private String url;

    public PayClient(String url) {
        this.url = url;
    }

    public String doPay(Map<String,Object> map) {
        RemoteParams params = new RemoteParams(url).withPath("/pay/unifiedorder").withParams(map);
        return super.post(params);
    }
}
