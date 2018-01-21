package hf.user.service;

import hf.base.client.DefaultClient;
import hf.base.dispatcher.DispatchResult;
import hf.base.dispatcher.Dispatcher;
import hf.base.model.UserGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class UserCallBackDispatcher implements Dispatcher {
    @Autowired
    private DefaultClient client;

    @Override
    public DispatchResult dispatch(HttpServletRequest request, String page) {
        Long groupId = Long.parseLong(request.getSession().getAttribute("groupId").toString());
        UserGroup userGroup = client.getUserGroupById(groupId);
        DispatchResult dispatchResult = new DispatchResult();
        dispatchResult.addObject("callBackUrl",userGroup.getCallbackUrl());
        dispatchResult.setPage(page);
        return dispatchResult;
    }
}
