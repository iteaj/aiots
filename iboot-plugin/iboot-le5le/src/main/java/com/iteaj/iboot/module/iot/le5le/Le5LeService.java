package com.iteaj.iboot.module.iot.le5le;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public class Le5LeService implements InitializingBean {

    private static String token;
    private static final String HOST = "https://account.le5le.com/api";
    private static Logger logger = LoggerFactory.getLogger(Le5LeService.class);

    public String login(String username, String password) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("account", username);
        jsonObject.put("password", password);

        HttpRequest post = HttpUtil.createPost(HOST + "/account/login");
        HttpResponse execute = post.body(jsonObject.toJSONString()).execute();
        if(execute.isOk()) {
            JSONObject parseObject = JSONObject.parseObject(execute.body());
            if(!parseObject.containsKey("error")) {
                return token = parseObject.getString("token");
            } else {
                logger.error("<<< 登录乐吾乐失败 {}", parseObject.getString("error"));
            }
        } else {
            logger.error("<<< 登录乐吾乐失败 {}", execute.getStatus());
        }

        throw new ServiceException("登录乐吾乐失败");
    }

    /**
     * 获取大屏图纸列表
     * @param page
     * @return
     */
    public Page list(Page page) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("current", page.getCurrent());
        jsonObject.put("pageSize", page.getSize());
        JSONObject response = doRequest("/data/v/list", jsonObject);
        page.setTotal(response.getInteger("total"));
        page.setRecords(response.getJSONArray("list"));
        return page;
    }

    /**
     * 获取大屏详情
     * @param id 大屏id
     * @return
     */
    public JSONObject get(String id) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", id);
        return doRequest("/data/v/get", jsonObject);
    }

    /**
     * 获取2d图纸列表
     * @param page
     * @return
     */
    public Page list2d(Page page) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("current", page.getCurrent());
        jsonObject.put("pageSize", page.getSize());
        JSONObject response = doRequest("/data/2d/list", jsonObject);
        page.setTotal(response.getInteger("total"));
        page.setRecords(response.getJSONArray("list"));
        return page;
    }

    /**
     * 获取3d图纸列表
     * @param page
     * @return
     */
    public Page list3d(Page page) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("current", page.getCurrent());
        jsonObject.put("pageSize", page.getSize());
        JSONObject response = doRequest("/data/3d/list", jsonObject);
        page.setTotal(response.getInteger("total"));
        page.setRecords(response.getJSONArray("list"));
        return page;
    }

    /**
     * 获取大屏组件列表
     * @param page
     * @return
     */
    public Page listComponents(Page page) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("current", page.getCurrent());
        jsonObject.put("pageSize", page.getSize());
        JSONObject response = doRequest("/data/v.component/list", jsonObject);
        page.setTotal(response.getInteger("total"));
        page.setRecords(response.getJSONArray("list"));
        return page;
    }

    protected JSONObject doRequest(String url, JSONObject body) {
        if(token == null) {
            this.login("18059222381", "lcjg4OD12aXRlYXp");
        }

        HttpRequest post = HttpUtil.createPost(HOST + url);
        post.header("Authorization", token);
        HttpResponse execute = post.body(body.toJSONString()).execute();
        if(execute.isOk()) {
            JSONObject response = JSONObject.parseObject(execute.body());
            String error = response.getString("error");
            if(error == null) {
                return response;
            } else if("没有访问权限".equals(error)) {
                this.login("18059222381", "lcjg4OD12aXRlYXp");
                return doRequest(url, body);
            } else {
                logger.error("<<< 请求乐吾乐接口失败 {}", error);
            }
        }

        throw new ServiceException("请求接口失败");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
//        this.login("18059222381", "lcjg4OD12aXRlYXp");
//        Page page = this.listComponents(new Page());
//        Page list = this.list(new Page());
//        if(list.getTotal() > 0) {
//            JSONObject o = (JSONObject)list.getRecords().get(0);
//            JSONObject id = this.get(o.getString("id"));
//            System.out.println(id);
//        }
//        Page list2d = this.list2d(new Page());
//        Page list3d = this.list3d(new Page());
//        System.out.println(page.getRecords());
    }
}
