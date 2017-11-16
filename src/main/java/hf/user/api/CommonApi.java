package hf.user.api;

import hf.base.dispatcher.DispatchResult;
import hf.base.dispatcher.DispatcherFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/common")
public class CommonApi {
    @Autowired
    private DispatcherFactory dispatcherFactory;

    @RequestMapping(value="/{page}")
    public ModelAndView dispatch(HttpServletRequest request, @PathVariable("page")String page) {
        ModelAndView modelAndView = new ModelAndView();
        request.setAttribute("page",page);
        DispatchResult dispatchResult = dispatcherFactory.getDispatcher(request,page).dispatch(request);
        modelAndView.setViewName(String.format("/%s",dispatchResult.getPage()));

        if(!org.apache.commons.collections.MapUtils.isEmpty(dispatchResult.getData())) {
            modelAndView.addObject(dispatchResult.getData());
        }

        return modelAndView;
    }
}
