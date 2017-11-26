package hf.user.api;

import hf.base.biz.CacheService;
import hf.base.client.DefaultClient;
import hf.base.contants.Constants;
import hf.base.enums.GroupType;
import hf.base.enums.UserType;
import hf.base.exceptions.BizFailException;
import hf.base.model.UserGroup;
import hf.base.model.UserInfo;
import hf.base.utils.MapUtils;
import hf.base.utils.ResponseResult;
import hf.user.client.UserClient;
import hf.user.model.RegisterRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Objects;
import static hf.base.contants.UserConstants.*;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private DefaultClient client;
    @Autowired
    private UserClient userClient;
    @Autowired
    private CacheService cacheService;

    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public ModelAndView login(HttpServletRequest request, String loginId, String password) {
        UserInfo userInfo = client.getUserInfo(loginId,password, Constants.GROUP_TYPE_CUSTOMER);
        ModelAndView modelAndView = new ModelAndView();

        if(Objects.isNull(userInfo) || Objects.isNull(userInfo.getId())) {
            modelAndView.setViewName("redirect:/login.jsp");
            return modelAndView;
        }

        doLogin(request,userInfo);

        modelAndView.setViewName("redirect:/common/index");
        modelAndView.addObject("userInfo",userInfo);
        modelAndView.addObject("userId",userInfo.getId());
        return modelAndView;
    }

    @RequestMapping(value = "/logout",method = RequestMethod.GET)
    public ModelAndView logout(HttpServletRequest request) {
        request.getSession().removeAttribute(Constants.USER_LOGIN_INFO);
        ModelAndView modelAndView = new ModelAndView("redirect:/login.jsp");
        return modelAndView;
    }

    @RequestMapping(value = "/register",method = RequestMethod.POST)
    public ModelAndView register(HttpServletRequest request) {
        String loginId = request.getParameter("loginId");
        String password = request.getParameter("password");
        String confirmpassword = request.getParameter("confirmpassword");
        String email = request.getParameter("email");
        String tel = request.getParameter("tel");
        String inviteCode = request.getParameter("inviteCode");

        ModelAndView modelAndView = new ModelAndView();

        if(StringUtils.isEmpty(loginId) || StringUtils.isEmpty(password) || StringUtils.isEmpty(confirmpassword)
                || StringUtils.isEmpty(email) || StringUtils.isEmpty(tel)) {
            modelAndView.setViewName("redirect:/user/register.jsp");
            return modelAndView;
        }

        if(!StringUtils.equals(password,confirmpassword)) {
            modelAndView.setViewName("redirect:/user/register.jsp");
            return modelAndView;
        }

        RegisterRequest registerRequest = new RegisterRequest(loginId,password,email,tel,inviteCode);
        String result = client.register(MapUtils.beanToMap(registerRequest));

        if(StringUtils.equals(result,"0000000")) {
            modelAndView.setViewName("redirect:/common/index");
            UserInfo userInfo = client.getUserInfo(loginId,password,Constants.GROUP_TYPE_CUSTOMER);
            doLogin(request,userInfo);
            return modelAndView;
        } else {
            modelAndView.setViewName("redirect:/register.jsp");
            return modelAndView;
        }
    }

    private void doLogin(HttpServletRequest request,UserInfo userInfo) {
        request.getSession().setAttribute(Constants.USER_LOGIN_INFO,
                MapUtils.buildMap(ID,userInfo.getId(),
                                        NAME,userInfo.getName(),
                                        LOGIN_ID,userInfo.getLoginId(),
                                        USER_TYPE, UserType.parse(userInfo.getType()).getDesc(),
                                        GROUP_ID,userInfo.getGroupId(),
                                        USER_STATUS,userInfo.getStatus()));
        request.getSession().setAttribute("userId",userInfo.getId());
        request.getSession().setAttribute("groupId",userInfo.getGroupId());
        cacheService.login(userInfo.getId().toString(),request.getSession().getId());
    }

    @RequestMapping(value = "/edit_user_info",method = RequestMethod.POST)
    public ModelAndView editUserInfo(HttpServletRequest request,HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView();
        Long userId = Long.parseLong(String.valueOf(request.getSession().getAttribute("userId")));
        String name = request.getParameter("name");
        String idCard = request.getParameter("idCard");
        String tel = request.getParameter("tel");
        String qq = request.getParameter("qq");
        String address = request.getParameter("address");

        try {
            UserInfo userInfo = userClient.editUserInfo(MapUtils.buildMap("userId", userId, "name", name, "idCard", idCard, "tel", tel, "qq", qq, "address", address));
            request.getSession().setAttribute(Constants.USER_LOGIN_INFO,
                    MapUtils.buildMap(ID, userInfo.getId(),
                            NAME, userInfo.getName(),
                            LOGIN_ID, userInfo.getLoginId(),
                            USER_TYPE, UserType.parse(userInfo.getType()).getDesc(),
                            GROUP_ID, userInfo.getGroupId(),
                            USER_STATUS, userInfo.getStatus()));

            modelAndView.setViewName("redirect:/common/user_group_profile");
        } catch (Exception e) {
            modelAndView.setViewName("redirect:/common/index");
        }
        return modelAndView;
    }

    @RequestMapping(value = "/edit_group_info",method = RequestMethod.POST)
    public ModelAndView editGroupInfo(HttpServletRequest request,HttpServletResponse response) {
        String groupId = request.getSession().getAttribute(GROUP_ID).toString();
        String name = request.getParameter(NAME);
        String idCard = request.getParameter("idCard");
        String tel = request.getParameter("tel");
        String address = request.getParameter("address");
        String ownerName = request.getParameter("username");
        ResponseResult<UserGroup> res = userClient.editGroupInfo(MapUtils.buildMap("groupId",groupId,"name",name,"idCard",idCard,"tel",tel,"address",address,"ownerName",ownerName,"type", GroupType.CUSTOMER.getValue()));

        ModelAndView modelAndView = new ModelAndView();

        if(res.isSuccess()) {
            modelAndView.setViewName("redirect:/common/user_account_bankcard");
            return modelAndView;
        } else {
            modelAndView.setViewName("redirect:/common/user_group_profile");
            return modelAndView;
        }
    }

    @RequestMapping(value = "/save_bank_card",method = RequestMethod.POST)
    public @ResponseBody Map<String,Object> saveBankCard(HttpServletRequest request, HttpServletResponse response) {
        String groupId = request.getSession().getAttribute("groupId").toString();
        String bank = request.getParameter("bank");
        String bankNo = request.getParameter("bankNo");
        String deposit = request.getParameter("deposit");
        String owner = request.getParameter("owner");
        String province = request.getParameter("province");
        String city = request.getParameter("city");

        ResponseResult<Boolean> responseResult = userClient.saveBankCard(
                MapUtils.buildMap("groupId",groupId,
                        "bank",bank,
                        "bankNo",bankNo,
                        "deposit",deposit,
                        "owner",owner,
                        "province",province,
                        "city",city));

        if(responseResult.isSuccess()) {
            return MapUtils.buildMap("status",true);
        }
        return MapUtils.buildMap("status",false);
    }

    @RequestMapping(value = "/finish_user_info_complete",method = RequestMethod.GET)
    public ModelAndView finishUserInfoComplete(HttpServletRequest request, HttpServletResponse response) {
        String groupId = request.getSession().getAttribute("groupId").toString();
        String userId = request.getSession().getAttribute("userId").toString();
        ResponseResult<Boolean> responseResult = userClient.submitToAdmin(groupId,userId);
        ModelAndView modelAndView = new ModelAndView();
        if(responseResult.isSuccess()) {
            modelAndView.setViewName("redirect:/common/user_account_authorized");
            return modelAndView;
        }
        modelAndView.setViewName("redirect:/common/index");
        return modelAndView;
    }

    @RequestMapping(value = "/edit_password",method = RequestMethod.POST)
    public @ResponseBody Map<String,Object> editPassword(HttpServletRequest request) {
        String userId = String.valueOf(request.getSession().getAttribute("userId"));
        String ypassword = request.getParameter("ypassword");
        String newpassword = request.getParameter("newpassword");
        String newpasswordok = request.getParameter("newpasswordok");

        boolean result = userClient.editPassword(userId,ypassword,newpassword,newpasswordok);
        return MapUtils.buildMap("status",result);
    }
}
