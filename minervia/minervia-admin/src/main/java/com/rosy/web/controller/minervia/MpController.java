package com.rosy.web.controller.minervia;

import cn.hutool.core.bean.BeanUtil;
import com.rosy.common.core.domain.AjaxResult;
import com.rosy.minervia.domain.entity.WxLogin;
import com.rosy.minervia.domain.vo.BannerVO;
import com.rosy.minervia.domain.vo.CategoryVO;
import com.rosy.minervia.service.IBannerService;
import com.rosy.minervia.service.ICategoryService;
import com.rosy.minervia.service.IWxLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


/**
 * 专门用来处理跟小程序有关的请求
 */

@RestController
@RequestMapping("/mp")
public class MpController {
    @Autowired
    IWxLoginService wxLoginService;
    @Autowired
    ICategoryService categoryService;
    @Autowired
    IBannerService bannerService;

    @PostMapping("/login")
    public AjaxResult login(@RequestParam("js_code") String jsCode) {
        WxLogin wxLogin = wxLoginService.login(jsCode);
        return Optional.ofNullable(wxLogin)
                .map(WxLogin::getOpenid)
                .filter(openid -> !openid.isEmpty())
                .map(openid -> AjaxResult.success((Object) wxLogin.getSessionKey()))
                .orElse(AjaxResult.error(401, "登录失败"));
    }

    @GetMapping("/banners")
    public List<BannerVO> getAllBanners() {
        return BeanUtil.copyToList(bannerService.getAllBanners(), BannerVO.class);
    }

    @GetMapping("/categories")
    public List<CategoryVO> getAllCategories() {
        return BeanUtil.copyToList(categoryService.getAllCategories(), CategoryVO.class);
    }
}