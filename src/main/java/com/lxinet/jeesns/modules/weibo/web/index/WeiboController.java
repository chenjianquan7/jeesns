package com.lxinet.jeesns.modules.weibo.web.index;

import com.lxinet.jeesns.core.dto.ResponseModel;
import com.lxinet.jeesns.core.entity.Page;
import com.lxinet.jeesns.core.utils.MemberUtil;
import com.lxinet.jeesns.core.web.BaseController;
import com.lxinet.jeesns.modules.mem.entity.Member;
import com.lxinet.jeesns.modules.weibo.entity.Weibo;
import com.lxinet.jeesns.modules.weibo.service.IWeiboCommentService;
import com.lxinet.jeesns.modules.weibo.service.IWeiboService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.annotation.Resource;
import java.util.List;

/**
 * Created by zchuanzhao on 2016/12/20.
 */
@Controller("weiboIndexController")
@RequestMapping("/weibo")
public class WeiboController extends BaseController {
    private static final String MEMBER_FTL_PATH = "/index/weibo/";
    @Resource
    private IWeiboService weiboService;
    @Resource
    private IWeiboCommentService weiboCommentService;

    @RequestMapping(value = "/publish",method = RequestMethod.POST)
    @ResponseBody
    public ResponseModel publish(String content){
        Member loginMember = MemberUtil.getLoginMember(request);
        if(loginMember == null){
            return new ResponseModel(-1,"请先登录");
        }
        return weiboService.save(loginMember,content);
    }

    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public String list(Model model){
        Page page = new Page(request);
        ResponseModel responseModel = weiboService.listByPage(page,0);
        model.addAttribute("model",responseModel);
        List<Weibo> hotList = weiboService.hotList();
        model.addAttribute("hotList",hotList);
        return MEMBER_FTL_PATH + "list";
    }

    @RequestMapping(value = "/detail/{weiboId}",method = RequestMethod.GET)
    public String detail(@PathVariable("weiboId") Integer weiboId, Model model){
        Weibo weibo = weiboService.findById(weiboId);
        model.addAttribute("weibo",weibo);
        return MEMBER_FTL_PATH + "detail";
    }

    @RequestMapping(value="/delete/{weiboId}",method = RequestMethod.GET)
    @ResponseBody
    public Object delete(@PathVariable("weiboId") Integer weiboId){
        Member loginMember = MemberUtil.getLoginMember(request);
        ResponseModel responseModel = weiboService.userDelete(loginMember,weiboId);
        if(responseModel.getCode() == 0){
            responseModel.setCode(2);
            responseModel.setUrl(request.getContextPath() + "/weibo/list");
        }
        return responseModel;
    }


    @RequestMapping(value="/comment/{weiboId}",method = RequestMethod.POST)
    @ResponseBody
    public Object comment(@PathVariable("weiboId") Integer weiboId, String content){
        Member loginMember = MemberUtil.getLoginMember(request);
        if(loginMember == null){
            return new ResponseModel(-1,"请先登录");
        }
        return weiboCommentService.save(loginMember,content,weiboId);
    }

    @RequestMapping(value="/commentList/{weiboId}.json",method = RequestMethod.GET)
    @ResponseBody
    public Object commentList(@PathVariable("weiboId") Integer weiboId){
        Page page = new Page(request);
        if(weiboId == null){
            weiboId = 0;
        }
        return weiboCommentService.listByWeibo(page,weiboId);
    }
}
