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
    public DispatchResult dispatch(HttpServletRequest request,String page) {
        HttpSession session = request.getSession();
        Map<String,Object> sessionInfo = (Map<String,Object>)session.getAttribute(Constants.USER_LOGIN_INFO);
        Long userId = Long.parseLong(sessionInfo.get(ID).toString());
        UserInfo userInfo = hfClient.getUserInfoById(userId);

        DispatchResult result = new DispatchResult();
        result.addObject("name",sessionInfo.get("name"));

        switch (UserStatus.parse(userInfo.getStatus())) {
            case NEW:
                result.setPage("index_for_new_user");
                result.addObject("userInfo",userInfo);
                break;
            case SUBMITED:
                result.setPage("user_account_authorized");
                result.addObject("userInfo",userInfo);
                break;
            case AVAILABLE:
                result.setPage("index");
                result.addObject("userInfo",userInfo);
                break;
            case CANCEL:
                break;
        }
        return result;
    }
}