package hf.user.service;

import hf.base.client.DefaultClient;
import hf.base.dispatcher.DispatchResult;
import hf.base.dispatcher.Dispatcher;
import hf.base.model.Account;
import hf.base.model.UserBankCard;
import hf.base.utils.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class UserWithDrawDispatcher implements Dispatcher {
    @Autowired
    private DefaultClient client;

    @Override
    public DispatchResult dispatch(HttpServletRequest request, String page) {
        Long groupId = Long.parseLong(request.getSession().getAttribute("groupId").toString());
        Account account = client.getAccountByGroupId(groupId);
        BigDecimal amount = (account.getAmount().subtract(account.getLockAmount())).divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP);
        BigDecimal lockAmount = account.getLockAmount().divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP);
        BigDecimal paidAmount = account.getPaidAmount().divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP);

        Map<String,Object> accountInfo = MapUtils.buildMap("amount",amount,"lockAmount",lockAmount,"paidAmount",paidAmount);
        DispatchResult dispatchResult = new DispatchResult();
        dispatchResult.setPage(page);
        dispatchResult.addObject("accountInfo",accountInfo);
        List<UserBankCard> cards = client.getUserBankCard(groupId);
        dispatchResult.addObject("cards",cards);
        BigDecimal feeRate = client.getWithDrawRate();
        dispatchResult.addObject("feeRate",feeRate);
        return dispatchResult;
    }
}
