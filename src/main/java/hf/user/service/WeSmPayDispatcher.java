package hf.user.service;

import hf.base.client.DefaultClient;
import hf.base.dispatcher.DispatchResult;
import hf.base.dispatcher.Dispatcher;
import hf.base.model.UserChannel;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WeSmPayDispatcher implements Dispatcher {
    @Autowired
    private DefaultClient client;

    @Override
    public DispatchResult dispatch(HttpServletRequest request, String page) {
        String groupId = request.getSession().getAttribute("groupId").toString();
        List<UserChannel> channels = client.getUserChannelList(Long.parseLong(groupId));
        List<UserChannel> wePayChannels = channels.parallelStream().filter(userChannel -> StringUtils.equalsIgnoreCase(userChannel.getChannelCode(),"02")).collect(Collectors.toList());

        DispatchResult dispatchResult = new DispatchResult();

        if(CollectionUtils.isEmpty(wePayChannels)) {
            dispatchResult.setPage("channel_no_right");
            dispatchResult.addObject("pageName","微信扫码");
            dispatchResult.addObject("channelName","微信扫码");
            return dispatchResult;
        }

        dispatchResult.setPage(page);

        return dispatchResult;
    }
}
