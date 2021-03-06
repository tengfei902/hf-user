package hf.user.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import hf.base.client.BaseClient;
import hf.base.client.DefaultClient;
import hf.base.exceptions.BizFailException;
import hf.base.model.RemoteParams;
import hf.base.model.UserGroup;
import hf.base.model.UserInfo;
import hf.base.utils.ResponseResult;

import java.util.Map;

public class UserClient extends BaseClient {

    private static final String EDIT_USER_INFO = "/user/edit_user_info";
    private static final String EDIT_GROUP_INFO = "/user/edit_user_group";
    private static final String SAVE_BANK_CARD = "/user/save_user_card";
    private static final String SUBMIT_TO_ADMIN = "/user/submit_user_to_admin";
    private static final String EDIT_PASSWORD = "/user/edit_password";

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

    public ResponseResult<UserGroup> editGroupInfo(Map<String,Object> data) {
        RemoteParams params = new RemoteParams(url).withPath(EDIT_GROUP_INFO).withParams(data);
        String result = super.post(params);
        ResponseResult<UserGroup> response = new Gson().fromJson(result,new TypeToken<ResponseResult<UserGroup>>(){}.getType());
        return response;
    }

    public ResponseResult<Boolean> saveBankCard(Map<String,Object> data) {
        RemoteParams params = new RemoteParams(url).withPath(SAVE_BANK_CARD).withParams(data);
        String result = super.post(params);
        return new Gson().fromJson(result,new TypeToken<ResponseResult<Boolean>>(){}.getType());
    }

    public ResponseResult<Boolean> submitToAdmin(String groupId,String userId) {
        RemoteParams params = new RemoteParams(url).withPath(SUBMIT_TO_ADMIN).withParam("groupId",groupId).withParam("userId",userId);
        String result = super.post(params);
        return new Gson().fromJson(result,new TypeToken<ResponseResult<Boolean>>(){}.getType());
    }

    public boolean editPassword(String userId,String ypassword,String newpassword,String newpasswordok) {
        RemoteParams params = new RemoteParams(url).withPath(EDIT_PASSWORD)
                .withParam("userId",userId)
                .withParam("ypassword",ypassword)
                .withParam("newpassword",newpassword)
                .withParam("newpasswordok",newpasswordok);

        String result = super.post(params);
        ResponseResult<Boolean> response = new Gson().fromJson(result,new TypeToken<ResponseResult<Boolean>>(){}.getType());
        return response.getData();
    }

    public ResponseResult<Boolean> saveCipherCode(String groupId,String cipherCode) {
        RemoteParams remoteParams = new RemoteParams(url).withPath("/user/save_cipher_code")
                .withParam("groupId",groupId).withParam("cipherCode",cipherCode);
        String result = super.post(remoteParams);
        return new Gson().fromJson(result,new TypeToken<ResponseResult<Boolean>>(){}.getType());
    }

    public ResponseResult<Boolean> saveCallBackUrl(String groupId,String callBackUrl) {
        RemoteParams remoteParams = new RemoteParams(url).withPath("/user/save_callback_url")
                .withParam("groupId",groupId).withParam("callBackUrl",callBackUrl);
        String result = super.post(remoteParams);
        return new Gson().fromJson(result,new TypeToken<ResponseResult<Boolean>>(){}.getType());
    }
}
