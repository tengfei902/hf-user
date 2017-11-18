package hf.user.service;

import hf.base.client.DefaultClient;
import hf.base.dispatcher.DispatchResult;
import hf.base.dispatcher.Dispatcher;
import hf.base.model.UserGroup;
import hf.base.utils.MapUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Service
public class GroupIndexDispatcher implements Dispatcher {
    @Autowired
    private DefaultClient client;

    @Override
    public DispatchResult dispatch(HttpServletRequest request,String page) {
        HttpSession session = request.getSession();
        String groupId = session.getAttribute("groupId").toString();
        UserGroup userGroup = client.getUserGroupById(Long.parseLong(groupId));
        DispatchResult dispatchResult = new DispatchResult();
        dispatchResult.setPage("user_group_profile");
        dispatchResult.setData(MapUtils.buildMap("groupInfo",userGroup));
        return dispatchResult;
    }
}
