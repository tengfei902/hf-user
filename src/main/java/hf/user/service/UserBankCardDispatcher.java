package hf.user.service;

import hf.base.client.DefaultClient;
import hf.base.contants.Constants;
import hf.base.dispatcher.DispatchResult;
import hf.base.dispatcher.Dispatcher;
import hf.base.model.UserBankCard;
import hf.base.model.UserGroup;
import hf.base.utils.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class UserBankCardDispatcher implements Dispatcher {
    @Autowired
    private DefaultClient client;

    @Override
    public DispatchResult dispatch(HttpServletRequest request, String page) {
        Long groupId = Long.parseLong(request.getSession().getAttribute("groupId").toString());
        List<UserBankCard> list = client.getUserBankCard(groupId);
        DispatchResult result = new DispatchResult();
        result.setPage(page);
        result.setData(MapUtils.buildMap("cards",list));
        return result;
    }
}
