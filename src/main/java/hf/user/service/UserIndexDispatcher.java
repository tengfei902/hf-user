package hf.user.service;

import hf.base.client.DefaultClient;
import hf.base.contants.Constants;
import hf.base.dispatcher.DispatchResult;
import hf.base.dispatcher.Dispatcher;
import hf.base.enums.UserStatus;
import hf.base.model.UserInfo;
import hf.base.utils.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import static hf.base.contants.UserConstants.*;

@Service
public class UserIndexDispatcher implements Dispatcher {
    @Autowired
    private DefaultClient hfClient;

    @Override
    public DispatchResult dispatch(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Map<String,String> sessionInfo = (Map<String,String>)session.getAttribute(Constants.USER_LOGIN_INFO);
        UserInfo userInfo = hfClient.getUserInfoById(Long.parseLong(sessionInfo.get(ID)));

        DispatchResult result = new DispatchResult();

        switch (UserStatus.parse(userInfo.getStatus())) {
            case NEW:
                result.setPage("user_account_profile");
                result.setData(MapUtils.buildMap("userInfo",userInfo));
                break;
            case SUBMITED:
                result.setPage("user_account_authorized");
                result.setData(MapUtils.buildMap("userInfo",userInfo));
                break;
            case AVAILABLE:
                result.setPage("index");
                result.setData(MapUtils.buildMap("userInfo",userInfo));
                break;
            case CANCEL:
                break;
        }
        return result;
    }
}