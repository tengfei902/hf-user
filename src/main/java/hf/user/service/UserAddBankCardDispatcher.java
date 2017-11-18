package hf.user.service;

import hf.base.contants.Constants;
import hf.base.dispatcher.DispatchResult;
import hf.base.dispatcher.Dispatcher;
import hf.base.utils.MapUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class UserAddBankCardDispatcher implements Dispatcher {

    @Override
    public DispatchResult dispatch(HttpServletRequest request, String page) {
        DispatchResult dispatchResult = new DispatchResult();
        dispatchResult.setPage(page);
        dispatchResult.setData(MapUtils.buildMap("bankNames", Constants.bankNames));
        return dispatchResult;
    }
}
