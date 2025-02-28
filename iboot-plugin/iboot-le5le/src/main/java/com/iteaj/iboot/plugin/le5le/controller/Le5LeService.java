package com.iteaj.iboot.plugin.le5le.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.result.BooleanResult;
import com.iteaj.iboot.plugin.le5le.Le5leProperties;
import com.iteaj.iboot.plugin.le5le.entity.Dataset;
import com.iteaj.iboot.plugin.le5le.entity.Dataset2d;
import com.iteaj.iboot.plugin.le5le.entity.Le5LeDatasource;
import com.iteaj.iboot.plugin.le5le.mapper.Le5LeDatasourceMapper;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class Le5LeService implements InitializingBean {

    private static String token;
    private static String USERNAME = "18059222381";
    private static String PASSWORD = "lcjg4OD12aXRlYXp";
    private final Le5LeDatasourceMapper le5LeDatasourceMapper;
    private static final String API_HOST = "https://v.le5le.com/api";
    private static final String ACCOUNT_HOST = "https://account.le5le.com/api";
    private static Logger logger = LoggerFactory.getLogger(Le5LeService.class);

    private final Le5leProperties le5leProperties;

    public String login(String username, String password) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("account", username);
        jsonObject.put("password", password);

        HttpRequest post = HttpUtil.createPost(ACCOUNT_HOST + "/account/login");
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
        JSONObject response = accountRequest("/data/v/list", jsonObject);
        page.setTotal(response.getInteger("total"));
        page.setRecords(response.getJSONArray("list"));
        return page;
    }

    /**
     * 获取大屏详情
     * @param id 大屏id
     * @return
     */
    public JSONObject get(String id, String type) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", id);
        if("2d".equals(type)) {
            return accountRequest("/data/2d/get", jsonObject);
        } else if("3d".equals(type)) {
            return accountRequest("/data/3d/get", jsonObject);
        }

        return accountRequest("/data/v/get", jsonObject);
    }

    public BooleanResult sync(String id, String type) {
        if(StrUtil.isBlank(id) || StrUtil.isBlank(type)) {
            return BooleanResult.buildFalse("参数错误");
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", id);
        JSONObject accountRequest = accountRequest("/data/v/get", jsonObject);
        Le5LeDatasource le5LeDatasource = this.le5LeDatasourceMapper.selectById(id);
        if(le5LeDatasource == null) {
            doCreateDrawing(type, accountRequest);
        } else {
            doUpdateDrawing(accountRequest, le5LeDatasource);
        }

        return BooleanResult.buildTrue("同步成功");
    }

    private void doUpdateDrawing(JSONObject accountRequest, Le5LeDatasource le5LeDatasource) {
        le5LeDatasource.setStar(accountRequest.getInteger("star"));
        le5LeDatasource.setData(accountRequest.getJSONObject("data"));
        le5LeDatasource.setName(accountRequest.getString("name"));
        le5LeDatasource.setImage(accountRequest.getString("image"));
        le5LeDatasource.setShared(accountRequest.getBoolean("shared"));
        this.le5LeDatasourceMapper.updateById(le5LeDatasource);
    }

    private void doCreateDrawing(String type, JSONObject accountRequest) {
        Le5LeDatasource le5LeDatasource;
        le5LeDatasource = new Le5LeDatasource();
        le5LeDatasource.setType(type);
        le5LeDatasource.setCreatedAt(new Date());
        le5LeDatasource.setStar(accountRequest.getInteger("star"));
        le5LeDatasource.setId(accountRequest.getString("id"));
        le5LeDatasource.setShared(accountRequest.getBoolean("shared"));
        le5LeDatasource.setImage(accountRequest.getString("image"));
        le5LeDatasource.setName(accountRequest.getString("name"));
        le5LeDatasource.setData(accountRequest.getJSONObject("data"));
        this.le5LeDatasourceMapper.insert(le5LeDatasource);
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
        JSONObject response = accountRequest("/data/2d/list", jsonObject);
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
        JSONObject response = accountRequest("/data/3d/list", jsonObject);
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
        JSONObject response = accountRequest("/data/v.component/list", jsonObject);
        page.setTotal(response.getInteger("total"));
        page.setRecords(response.getJSONArray("list"));
        return page;
    }

    public Page pageDataSources(Page page, Le5LeDatasource entity) {
        return le5LeDatasourceMapper.detailPage(page, entity);
    }

    protected JSONObject apiRequest(String url, JSONObject body) {
        return this.doRequest(API_HOST + url, body, false);
    }

    protected JSONObject accountRequest(String url, JSONObject body) {
        return this.doRequest(ACCOUNT_HOST + url, body, false);
    }

    protected JSONObject doRequest(String url, JSONObject body, boolean retry) {
        if(token == null) {
            this.login(USERNAME, PASSWORD);
        }

        HttpRequest post = HttpUtil.createPost(url);
        post.header("Authorization", token);
        HttpResponse execute = post.body(body.toString(SerializerFeature.SkipTransientField)).execute();
        if(execute.isOk()) {
            JSONObject response = JSONObject.parseObject(execute.body());
            String error = response.getString("error");
            if(error == null) {
                return response;
            } else if("没有访问权限".equals(error) && !response.containsKey("detail")) {
                if(!retry) {
                    this.login(USERNAME, PASSWORD);
                    return doRequest(url, body, true);
                } else {
                    throw new ServiceException(error);
                }
            } else {
                logger.error("<<< 请求乐吾乐接口失败 {}", error);
            }
        }

        throw new ServiceException("请求接口失败");
    }

    public JSONObject getDatasourceFromLe5Le(String id) {
        JSONObject data = new JSONObject();
        data.put("id", id);
        return this.apiRequest("/data/datasource/get", data);
    }

    public Page listDatasource(Page page) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("current", page.getCurrent());
        jsonObject.put("pageSize", page.getSize());
        JSONObject response = apiRequest("/data/datasource/list", jsonObject);
        page.setTotal(response.getInteger("total"));
        page.setRecords(response.getJSONArray("list"));
        return page;
    }

    public BooleanResult saveOrUpdate(Le5LeDatasource entity) {
        Le5LeDatasource le5LeDatasource = this.le5LeDatasourceMapper.selectOne(Wrappers.<Le5LeDatasource>lambdaQuery()
                .eq(Le5LeDatasource::getName, entity.getName()));
        if(le5LeDatasource != null) {
            return BooleanResult.buildFalse("名称已经存在["+entity.getName()+"]");
        }
        le5LeDatasource = this.le5LeDatasourceMapper.selectOne(Wrappers.<Le5LeDatasource>lambdaQuery()
                .eq(Le5LeDatasource::getDeviceGroupId, entity.getDeviceGroupId()));

        if(le5LeDatasource != null) {
            return BooleanResult.buildFalse("设备组已经存在["+entity.getDeviceGroupId()+"]");
        }

        if(entity.getId() == null) {
            if(entity.getType().equals("dataset")) {
                List<Dataset> datasets = this.listDatasetByDeviceGroupId(entity.getDeviceGroupId());
                doAddDatasource(entity, datasets);
            }

            return new BooleanResult(le5LeDatasourceMapper.insert(entity) > 0);
        } else {
            if(entity.getType().equals("dataset")) {
                List<Dataset> datasets = this.listDatasetByDeviceGroupId(entity.getDeviceGroupId());
                doUpdateDataset(entity, datasets);

            }

            return new BooleanResult(le5LeDatasourceMapper.updateById(entity) > 0);
        }
    }

    @NotNull
    private JSONObject doAddDatasource(Le5LeDatasource entity, List<Dataset> datasets) {
        JSONObject data = new JSONObject();
        JSONObject params = (JSONObject)JSONObject.toJSON(entity);
        data.put("devices", datasets);
        data.put("mode", "");
        data.put("url", "");
        params.put("data", data);
        params.remove("id");
        params.remove("star");
        params.remove("image");
        params.remove("updateAt");
        params.remove("createdAt");
        params.remove("deviceGroupId");
        params.remove("deviceGroupName");
        JSONObject response = this.apiRequest("/data/datasource/add", params);
        entity.setId(response.getString("id"));
        entity.setCreatedAt(new Date());
        return response;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    public BooleanResult delDatasource(List<String> ids) {
        List<Le5LeDatasource> le5LeDatasources = le5LeDatasourceMapper.selectBatchIds(ids);
        if(CollectionUtil.isNotEmpty(le5LeDatasources)) {
            le5LeDatasources.forEach(item -> {
                JSONObject params = new JSONObject();
                params.put("id", item.getId());
                this.apiRequest("/api/data/datasource/delete", params);
            });

            return new BooleanResult(le5LeDatasourceMapper.deleteBatchIds(ids) > 0);
        }

        return BooleanResult.buildTrue("删除成功");
    }

    public BooleanResult syncDatasource(String id) {
        Le5LeDatasource le5LeDatasource = le5LeDatasourceMapper.selectById(id);
        if(le5LeDatasource == null) {
            return BooleanResult.buildFalse("记录不存在");
        }

        if(le5LeDatasource.getType().equals("dataset")) {
            if(le5LeDatasource.getDeviceGroupId() == null) {
                return BooleanResult.buildFalse("未指定设备组");
            }
            List<Dataset> datasets = this.listDatasetByDeviceGroupId(le5LeDatasource.getDeviceGroupId());

            try {
                this.getDatasourceFromLe5Le(id);
                doUpdateDataset(le5LeDatasource, datasets);
                le5LeDatasourceMapper.updateById(le5LeDatasource);
            } catch (Exception e) { // 数据已被删除, 重新新增
                String oriId = le5LeDatasource.getId();
                le5LeDatasource.setId(null);
                doAddDatasource(le5LeDatasource, datasets);
                le5LeDatasourceMapper.update(null, Wrappers.<Le5LeDatasource>lambdaUpdate()
                        .set(Le5LeDatasource::getId, le5LeDatasource.getId())
                        .set(Le5LeDatasource::getCreatedAt, new Date())
                        .eq(Le5LeDatasource::getId, oriId));
            }


        }

        return BooleanResult.buildTrue("同步成功");
    }

    private void doUpdateDataset(Le5LeDatasource entity, List<Dataset> datasets) {
        JSONObject data = new JSONObject();
        JSONObject params = (JSONObject)JSONObject.toJSON(entity);
        data.put("devices", datasets);
        data.put("mode", "");
        data.put("url", "");
        params.put("data", data);
        params.remove("star");
        params.remove("image");
        params.remove("updateAt");
        params.remove("createdAt");
        params.remove("deviceGroupId");
        params.remove("deviceGroupName");
        entity.setUpdateAt(new Date());
        this.apiRequest("/data/datasource/update", params);
    }

    public Le5LeDatasource getDatasource(String id) {
        return le5LeDatasourceMapper.selectById(id);
    }

    public List<Dataset> listDatasetByDeviceGroupId(Long deviceGroupId) {
        List<Dataset> results = new ArrayList<>();
        List<Dataset> datasets = this.le5LeDatasourceMapper.listPointDatasetByDeviceGroupId(deviceGroupId);
        List<Dataset> warnDatasets = this.le5LeDatasourceMapper.listWarnDatasetByDeviceGroupId(deviceGroupId);
        List<Dataset> statusDatasets = this.le5LeDatasourceMapper.listStatusDatasetByDeviceGroupId(deviceGroupId);
        // 实时属性数据
        if(CollectionUtil.isNotEmpty(datasets)) {
            results.addAll(datasets);
        }

        // 属性告警
        if(CollectionUtil.isNotEmpty(warnDatasets)) {
            results.addAll(warnDatasets);
        }

        // 设备状态
        if(CollectionUtil.isNotEmpty(statusDatasets)) {
            results.addAll(statusDatasets);
        }

        return results;
    }

    @Scheduled(cron = "0 0 0/1 * * *")
    public void authScheduled() {
        this.login(USERNAME, PASSWORD);
    }

    @Scheduled(cron = "0 0 0/3 * * *")
    public void dataScheduled() {
        Page page = new Page(1, 1000);
        List<JSONObject> records = this.list(page).getRecords();
        if(CollectionUtil.isNotEmpty(records)) {
            records.forEach(item -> {
                try {
                    JSONObject detail = this.get(item.getString("id"), "dbs");
                    Le5LeDatasource le5LeDatasource = this.le5LeDatasourceMapper.selectById(item.getString("id"));
                    if(le5LeDatasource != null) {
                        this.doUpdateDrawing(detail, le5LeDatasource);
                    } else {
                        this.doCreateDrawing("dbs", detail);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public Page<Le5LeDatasource> pageDateSet(Page page, Le5LeDatasource entity) {
        return this.le5LeDatasourceMapper.pageDateSet(page, entity);
    }

    public List<Dataset2d> list2dDataset() {
        List<Dataset2d> results = new ArrayList<>();
        List<Dataset2d> deviceGroups = this.le5LeDatasourceMapper.listDeviceGroup();
        if(CollectionUtil.isNotEmpty(deviceGroups)) {
            deviceGroups.forEach(deviceGroup -> {
                final Long deviceGroupId = Long.valueOf(deviceGroup.getId());

                List<Dataset> datasets = this.le5LeDatasourceMapper.listPointDatasetByDeviceGroupId(deviceGroupId);
                List<Dataset> warnDatasets = this.le5LeDatasourceMapper.listWarnDatasetByDeviceGroupId(deviceGroupId);
                List<Dataset> statusDatasets = this.le5LeDatasourceMapper.listStatusDatasetByDeviceGroupId(deviceGroupId);
                // 实时属性数据
                if(CollectionUtil.isNotEmpty(datasets)) {
                    final List<Dataset2d> dataset2ds = datasets.stream()
                            .map(item -> new Dataset2d(item.getId(), item.getLabel()))
                            .collect(Collectors.toList());
                    deviceGroup.addChildren(new Dataset2d( "group_attr_" + deviceGroupId, "属性数据", dataset2ds));
                }

                // 属性告警
                if(CollectionUtil.isNotEmpty(warnDatasets)) {
                    final List<Dataset2d> dataset2ds = warnDatasets.stream()
                            .map(item -> new Dataset2d(item.getId(), item.getLabel()))
                            .collect(Collectors.toList());
                    deviceGroup.addChildren(new Dataset2d( "group_warn_" + deviceGroupId, "告警数据", dataset2ds));
                }

                // 设备状态
                if(CollectionUtil.isNotEmpty(statusDatasets)) {
                    final List<Dataset2d> dataset2ds = statusDatasets.stream()
                            .map(item -> new Dataset2d(item.getId(), item.getLabel()))
                            .collect(Collectors.toList());
                    deviceGroup.addChildren(new Dataset2d( "group_status_" + deviceGroupId, "设备状态", dataset2ds));
                }

                if(deviceGroup.getChildren() != null) {
                    results.add(deviceGroup);
                }
            });

        }

        return results;
    }
}
