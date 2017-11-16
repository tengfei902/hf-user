package hf.user.api;

import hf.base.biz.CacheService;
import hf.base.client.DefaultClient;
import hf.base.contants.Constants;
import hf.base.enums.UserType;
import hf.base.model.UserInfo;
import hf.base.utils.MapUtils;
import hf.user.client.UserClient;
import hf.user.model.RegisterRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

        if(StringUtils.equals(result,"SUCCESS")) {
            modelAndView.setViewName("redirect:/common/index");
            UserInfo userInfo = client.getUserInfo(loginId,password,Constants.GROUP_TYPE_CUSTOMER);
            doLogin(request,userInfo);
            return modelAndView;
        } else {
            modelAndView.setViewName("redirect:/user/register.jsp");
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

            modelAndView.setViewName("/common/user_group_profile");
        } catch (Exception e) {
            modelAndView.setViewName("/common/index");
        }
        return modelAndView;
    }
}
