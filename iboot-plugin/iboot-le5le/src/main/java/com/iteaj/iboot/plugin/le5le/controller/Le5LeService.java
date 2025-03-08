package com.iteaj.iboot.plugin.le5le.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.format.DateParser;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.result.BooleanResult;
import com.iteaj.framework.result.ListResult;
import com.iteaj.framework.result.Result;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

@Component
@AllArgsConstructor
public class Le5LeService implements InitializingBean {

    private static String token;
    private static String USERNAME = "13783598587";
    private static String PASSWORD = "4MDI1OA10QWEwMjU";
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
        final JSONArray list = response.getJSONArray("list");
        if(list != null) {
            list.forEach(item -> ((JSONObject) item).put("type", "v"));
        }

        page.setRecords(list);
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
        final JSONArray list = response.getJSONArray("list");
        if(list != null) {
            list.forEach(item -> ((JSONObject) item).put("type", "2d"));
        }
        page.setRecords(list);
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
        final JSONArray list = response.getJSONArray("list");
        if(list != null) {
            list.forEach(item -> ((JSONObject) item).put("type", "3d"));
        }
        page.setRecords(list);
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
        final JSONArray list = response.getJSONArray("list");
        if(list != null) {
            list.forEach(item -> ((JSONObject) item).put("type", "v"));
        }

        page.setRecords(list);
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

    @Transactional
    public BooleanResult delDatasource(List<String> ids) {
        if(CollectionUtil.isEmpty(ids) || ids.size() > 1) {
            return BooleanResult.buildFalse("未指定要删除的记录或不支持批量删除");
        }

        final Le5LeDatasource byId = le5LeDatasourceMapper.getById(ids.get(0));
        if(byId == null) {
            return BooleanResult.buildTrue("删除成功");
        }

        return new BooleanResult(le5LeDatasourceMapper.deleteById(ids.get(0)) > 0).ifPresentAndTrue(status -> {
            final String deployDir = le5leProperties.getDeployDir();
            if(StrUtil.isBlank(deployDir)) {
                return;
            }

            final File installDir = new File(deployDir + File.separator + byId.getPath());
            if(installDir.exists() && installDir.isDirectory()) {
                FileUtil.del(installDir);
            }
        });
    }

    @Deprecated
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
        if(deviceGroupId != null) {
            fetchResult(results, deviceGroupId);
        } else {
            List<Dataset2d> deviceGroups = this.le5LeDatasourceMapper.listDeviceGroup();
            if(CollectionUtil.isNotEmpty(deviceGroups)) {
                deviceGroups.forEach(item -> {
                    fetchResult(results, Long.valueOf(item.getId()));
                });
            }
        }

        return results;
    }

    private void fetchResult(List<Dataset> results, Long groupId) {
        List<Dataset> datasets = this.le5LeDatasourceMapper.listPointDatasetByDeviceGroupId(groupId);
        List<Dataset> warnDatasets = this.le5LeDatasourceMapper.listWarnDatasetByDeviceGroupId(groupId);
        List<Dataset> statusDatasets = this.le5LeDatasourceMapper.listStatusDatasetByDeviceGroupId(groupId);

        // 实时属性数据
        if (CollectionUtil.isNotEmpty(datasets)) {
            results.addAll(datasets);
        }

        // 属性告警
        if (CollectionUtil.isNotEmpty(warnDatasets)) {
            results.addAll(warnDatasets);
        }

        // 设备状态
        if (CollectionUtil.isNotEmpty(statusDatasets)) {
            results.addAll(statusDatasets);
        }
    }

    @Scheduled(cron = "0 0 0/1 * * *")
    public void authScheduled() {
        this.login(USERNAME, PASSWORD);
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

    public BooleanResult offlineInstall(MultipartFile file, Le5LeDatasource entity) {
        final String deployDir = le5leProperties.getDeployDir();
        if(StrUtil.isBlank(deployDir)) {
            return BooleanResult.buildFalse("未指定安装目录");
        }

        if(StrUtil.isBlank(entity.getPath())) {
            return BooleanResult.buildFalse("请求参数不完整");
        }

        final String pathCondition = String.format("where JSON_EXTRACT(data, '$.path')='%s'", entity.getPath());
        final Le5LeDatasource byPath = this.le5LeDatasourceMapper
                .selectOne(Wrappers.<Le5LeDatasource>lambdaQuery().last(pathCondition));
        Le5LeDatasource oriEntity = null;
        if(StrUtil.isNotBlank(entity.getId())) { // 更新操作
            oriEntity = this.le5LeDatasourceMapper.getById(entity.getId());
            if(oriEntity == null) {
                return BooleanResult.buildFalse("更新记录不存在");
            }

            if(!byPath.getId().equals(oriEntity.getId())) {
                return BooleanResult.buildFalse("路径[" + entity.getPath() + "]已用于["+byPath.getName()+"]");
            }
        } else {
            if(file == null) { // 新增操作文件必填
                return BooleanResult.buildFalse("未选择要上传的文件");
            }

            if(byPath != null) {
                return BooleanResult.buildFalse("路径[" + entity.getPath() + "]已用于["+byPath.getName()+"]");
            }
        }

        if(file != null) {

            final File root = new File(deployDir);
            if(!root.exists()) {
                root.mkdirs();
            }

            if(!root.isDirectory()) {
                return BooleanResult.buildFalse("安装路径不是目录");
            }

            if(oriEntity != null) {
                final String child = oriEntity.getData().getString("path"); // 安装子目录
                final File oriDeployDir = new File(root, child);
                if(oriDeployDir.exists() && oriDeployDir.isDirectory()) {
                    final boolean del = FileUtil.del(oriDeployDir);
                    if(!del) {
                        return BooleanResult.buildFalse("移除目录失败");
                    }
                }
            }

            final File zipFile = new File(root, entity.getPath() + File.separator + "source.zip");
            try {
                FileUtil.writeFromStream(file.getInputStream(), zipFile);
            } catch (IOException e) {
                logger.error("安装离线部署包失败", e);
                return BooleanResult.buildFalse("写文件失败");
            }

            entity.setType("v"); // 大屏组态
            ZipUtil.unzip(zipFile, zipFile.getParentFile(), CharsetUtil.CHARSET_GBK);
            if(!FileUtil.exist(new File(zipFile.getParentFile(), "view"))) {
                final File[] children = FileUtil.ls(zipFile.getParent());
                for (int i = 0; i < children.length; i++) {
                    final File child = children[i];
                    if(child.isDirectory()) {
                        final File view = new File(child, "view");
                        if(view.exists() && view.isDirectory()) {
                            FileUtil.move(view, zipFile.getParentFile(), true);
                            FileUtil.del(view.getParentFile());
                        }
                    }
                }
            }
            File jsonFile = new File(zipFile.getParentFile(), "/view/projects/v");
            if(!jsonFile.exists()) {
                entity.setType("2d"); // 2d图纸
                jsonFile = new File(zipFile.getParentFile(), "/view/projects/2d");
            }

            if(!jsonFile.exists()) {
                FileUtil.del(zipFile.getParentFile());
                return BooleanResult.buildFalse("非法的离线部署包");
            }

            String jsonStr = FileUtil.readUtf8String(jsonFile);
//            if(jsonStr.contains("/view?data=2d")) {
//                final String replace = String.format("/scada/%s/view/index.html?data=2d", entity.getPath());
//                jsonStr = jsonStr.replaceAll("/view?data=2d", replace);
//            }
            final JSONObject json = JSONObject.parseObject(jsonStr);
            entity.setName(json.getString("name"));
            DateTime createdAt = resolverDate(json.getString("createdAt"));
            entity.setCreatedAt(createdAt);
            entity.setUpdateAt(resolverDate(json.getString("updatedAt")));
        }

        if(oriEntity != null) {
            this.le5LeDatasourceMapper.updateById(entity);
        } else {
            entity.setId(UUID.randomUUID(true).toString());
            this.le5LeDatasourceMapper.insert(entity);
        }
        return BooleanResult.buildTrue("安装成功");
    }

    private DateTime resolverDate(String datetime) {
        if(StrUtil.isBlank(datetime)) {
            return new DateTime();
        }

        if(datetime.contains("+")) {
            return DateUtil.parse(datetime, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        }

        return DateUtil.parse(datetime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public Result<List<Le5LeDatasource>> listLe5LeDatasource(Le5LeDatasource entity) {
        return new ListResult<>(this.le5LeDatasourceMapper.selectList(Wrappers.lambdaQuery(entity)));
    }
}
