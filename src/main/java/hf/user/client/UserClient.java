package hf.user.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import hf.base.client.BaseClient;
import hf.base.client.DefaultClient;
import hf.base.exceptions.BizFailException;
import hf.base.model.RemoteParams;
import hf.base.model.UserInfo;
import hf.base.utils.ResponseResult;

import java.util.Map;

public class UserClient extends BaseClient {

    private static final String EDIT_USER_INFO = "/user/edit_user_info";
    private String url;

    public UserClient(String url) {
        this.url = url;
    }

    public UserInfo editUserInfo(Map<String,Object> data) {
        RemoteParams params = new RemoteParams(url).withPath(EDIT_USER_INFO).withParams(data);
        String result = super.post(params);
        ResponseResult<UserInfo> response = new Gson().fromJson(result,new TypeToken<ResponseResult<UserInfo>>(){}.getType());
        if(response.isSuccess()) {
            return response.getData();
        }
        throw new BizFailException(response.getCode(),response.getMsg());
    }
}
