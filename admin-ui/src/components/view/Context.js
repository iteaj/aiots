import {FormContext} from "@/components/form/basic/FormContext";
import {confirm, msgError, msgLoading, msgSuccess, msgWarn} from "@/utils/message";
import {FuncNameMeta, TypeMethodMaps} from "@/utils/MetaUtils";
import SysUtils from "@/utils/SysUtils";
import CoreConsts from "@/components/CoreConsts";
import {GET, urlConfig} from "@/utils/request";
import dayjs from "dayjs";
import {Modal} from "antdv-next";
import {createVNode, defineComponent} from "vue";
import {ImportUpload} from "@/components/basic";
import {FuncMeta} from "@/components/view/FuncMeta.js";

function Unmount() {
    console.warn("此方法只能在组件挂载时才能使用");
}
function unUFuncTag() {
    console.warn("需使用[UFuncTag]作为表格按钮")
}
const VoidFun = () => {};
const VoidResolveCall = () => Promise.resolve();
let env = import.meta.env;
/**
 * 视图组件提供的操作接口
 * @see UView
 * @see IvzFuncView
 * @see IvzMenuView
 * @param context 视图组件的上下文对象{@link ViewContext}
 */
export function $View(context) {

    this.context = context;

    if(!context) {
        return console.error("未传入视图上下文对象[ViewContext]")
    }

    this.context['__$View'] = this;

    /**
     * 功能的唯一代表字段 - 比如：id
     * @see CoreConsts#DefaultRowKey
     * @return {String|Number}
     */
    this.getRowKey = function () {
        return this.context.getRowKey();
    }

    /**
     * 当前视图(功能)页的上下文对象
     * @see ViewContext
     * @return {ViewContext}
     */
    this.getViewContext = function () {
        return this.context;
    }

    /**
     * 获取功能元上下文, 只能用于以下两种组件
     * @see IvzFuncView
     * @set IvzMenuView
     * @return {void|FuncMetaContext}
     */
    this.getMetaContext = function () {
        if(!this.context.funMetasContext) {
            return console.warn("此方法只能在组件[IvzFuncView or IvzMenuView]中使用")
        }

        return this.context.funMetasContext;
    }

    /**
     * 获取编辑功能元数据
     * @param field
     * @return {void|Object}
     */
    this.getEditMeta = function (field) {
        return this.getMetaContext().getEditMeta(field);
    }

    /**
     * 获取表格功能元数据
     * @param field
     * @return {void|Object}
     */
    this.getTableMeta = function (field) {
        return this.getMetaContext().getTableMeta(field);
    }

    /**
     * 获取搜索功能元数据
     * @param field
     * @return {void|Object}
     */
    this.getSearchMeta = function (field) {
        return this.getMetaContext().getSearchMeta(field);
    }

    /**
     * 获取当前视图页的名称 eg: 用户管理、部门管理等
     * 需要在视图组件设置属性：name
     * @return {String}
     */
    this.getFuncName = function () {
        return this.context.name;
    }

    /**
     * 校验一个model是编辑还是新增
     * 默认根据 rowKey判断
     * @param model
     * @return {boolean}
     */
    this.isEdit = function (model) {
        let rowKey = this.getRowKey();
        return model[rowKey] != null;
    }

    /**
     * 返回编辑框获取数据需要的地址
     * 注：可根据情况自行替换和修改
     * @param data 操作参数
     * @param funcPath {String[]} 功能路径
     * @param params {Function} 获取请求参数回调
     * @return {Promise}
     */
    this.getRequestParams = function (data, params, funcPath) {
        let func = funcPath[0];
        params = params || CoreConsts.DefaultFuncParams[func];
        if(params instanceof Function) {
            let param = params(data, funcPath, this.getRowKey());
            if(param instanceof Promise) {
                return param;
            } else {
                return Promise.reject(`params方法只支持返回Promise对象`);
            }
        } else if(typeof params == 'object') {
            return Promise.resolve(params);
        } else {
            return Promise.resolve(data);
        }
    }

    /**
     * 打开编辑框
     * @param config {Config}
     */
    this.openForAdd = function ({uid, data, params, func, funcPath, context}) {
        let linkContext = context.getLinkContext();
        let editContext = linkContext.getChildrenContext(uid || CoreConsts.DefaultEditUid);
        if(editContext == null) {
            return console.warn(`未找到对应uid的可编辑组件[${uid || CoreConsts.DefaultEditUid}]`)
        }

        try {
            editContext.openType = 'add';
            editContext.asyncVisible(data, true).then((edit) => {
                let formContext = editContext.getFormContext();
                let formModel = formContext.getInitModel();
                this.getRequestParams({data, formModel}, params, funcPath).then((editModel) => {
                    // 先触发v-model事件
                    formContext.setEditModel(editModel)
                })
            })

        } catch (e) {
            console.error(e);
        }
    }

    /**
     * 表格新增一行
     * @param config {Config}
     */
    this.tableForAdd = function ({uid, data, params, func, funcPath, context}) {
        let linkContext = context.getLinkContext();
        let tableContext = linkContext.getChildrenContext(uid || CoreConsts.DefaultTableUid);
        if(!(tableContext instanceof TableContext)) {
            return console.warn(`未找到对应uid的表格组件[${uid || CoreConsts.DefaultTableUid}]`)
        }
        // 新增一行
        tableContext.addEditableRow();
    }

    /**
     * 操作前确认(U化确认弹框): 经页面根上下文定位UConfirm(uid=CoreConsts.DefaultConfirmUid)后由ask()打开——
     * 弹框注册进页面上下文/焦点栈, AI可在焦点视图中看到标题/内容并触发[确认/取消]按钮执行原回调;
     * UConfirm未挂载(页面未基于UView或异常)时回退antd静态确认弹框, 保证功能可用
     * @param context 功能所在组件上下文(用于定位所属页面)
     * @param config {{title: String, content: String, onOk: Function, onCancel: Function}}
     */
    this.confirmAction = function (context, {title, content, onOk, onCancel}) {
        let confirmContext = null;
        if(context) {
            let linkContext = context.getLinkContext instanceof Function ? context.getLinkContext() : context;
            if(linkContext && linkContext.getViewContext instanceof Function) {
                confirmContext = linkContext.getViewContext()
                    .getContextByUid(CoreConsts.PrimaryUid + ':' + CoreConsts.DefaultConfirmUid);
            }
        }
        if(confirmContext && confirmContext.ask instanceof Function) {
            confirmContext.ask({title, content, onOk, onCancel});
        } else {
            confirm({title, content, onOk, onCancel});
        }
    }

    /**
     * 删除主表格记录
     * @param param {Config}
     */
    this.del = function ({method, url, data, func, uid
                             , config: {confirmTitle, confirmContent}, callback
         , context, params, funcPath, reload}) {

        let linkContext = context.getLinkContext();
        let tableContext = linkContext.getChildrenContext(CoreConsts.DefaultTableUid);
        if(!(tableContext instanceof TableContext)) {
            return console.warn(`未找到对应uid的表格组件[${linkContext.uid}]`)
        }

        // 批量删除, 获取表列表数据
        if(funcPath[1] && funcPath[1] == CoreConsts.ChildFuncNameMeta.BATCH) {
            data = tableContext.getSelectedRows();
        }

        if(!data || data.length == 0) {
            return msgWarn("请选择要删除的记录");
        }

        let title = confirmTitle || CoreConsts.DelConfirmTitle;
        let content = confirmContent || CoreConsts.DelConfirmContent;

        if(tableContext.editable) { // 是可编辑的表格
            // 如果是新增的编辑行直接删除
            // 删除成功直接返回
            if(tableContext.delEditableRow()) {
                return;
            }
        }

        this.confirmAction(context, {title, content, onOk: () => {
            // 返回删除执行结果文本(成功/失败原因), 供AI动作(exec_func)同步等待确认后反馈真实结果; 失败不抛异常, UI/回调路径不变
            return this.getRequestParams(data, params, funcPath).then((params => {
                    tableContext.setLoading(true, CoreConsts.DeleteExecTipping)
                    return this.getRequestMethod({func, method})(url, params).then(({code, message, data}) => {
                        let result;
                        if (code == CoreConsts.SuccessCode) {
                            msgSuccess(message || CoreConsts.DelSuccessMsg);
                            if(reload) {
                                this.queryByFunc(linkContext); // 删除成功, 重新刷新列表
                            }
                            result = message || CoreConsts.DelSuccessMsg;
                        } else {
                            msgError(message);
                            result = `删除失败: ${message}`;
                        }

                        callback({code, message, data}, params)
                        return result;
                    }).catch(reason => {
                        console.error(reason);
                        return `删除请求异常: ${reason && reason.message || reason}`;
                    }).finally(() => tableContext.setLoading(false, ""));
                }));
            }
        });
    }

    /**
     * 打开编辑框
     * @param param {Config}
     */
    this.openForEdit = function ({uid, url, data, params
                             , func, funcPath, method, config: {}, context}) {

        let linkContext = context.getLinkContext();
        let editContext = linkContext.getChildrenContext(uid || CoreConsts.DefaultEditUid);
        if(!(editContext instanceof EditContext)) {
            return console.warn(`未找到对应uid的可编辑组件[${uid || CoreConsts.DefaultEditUid}]`)
        }

        let formModel = null;
        editContext.openType = 'edit';
        if(url) {
            editContext.asyncVisible(data, false).then((edit) => {
                this.getRequestParams({data, formModel}, params, funcPath).then((reqParam) => {
                    editContext.setLoading(true, CoreConsts.FormSpinLoadingTip);
                    this.getRequestMethod({func, method})(url, reqParam)
                        .then(({code, message, data}) => {
                            if(code == CoreConsts.SuccessCode) {
                                // 先触发v-model事件
                                editContext.getFormContext().setEditModel(data)
                            } else {
                                msgError(message);
                            }
                    }).finally(() => editContext.setLoading(false, null))
                })
            })
        } else {
            editContext.asyncVisible(data, false).then((edit) => {
                // 先触发v-model事件
                editContext.getFormContext().setEditModel(data)
            })
        }
    }

    /**
     * 编辑表格
     * @param param {Config}
     */
    this.tableForEdit = function ({uid, url, data, params
                                     , func, funcPath, method, config: {}, context}) {
        let linkContext = context.getLinkContext();
        let tableContext = linkContext.getChildrenContext(uid || CoreConsts.DefaultTableUid);
        if(!(tableContext instanceof TableContext)) {
            return console.warn(`未找到对应uid的可表格组件[${uid || CoreConsts.DefaultTableUid}]`)
        }

        tableContext.setEditRow(data);
    }

    /**
     * 新增子记录
     * 支持格式：func='add:child'
     * 设置参数：:params="{pid: 'pid'}"
     * @param param {Config}
     */
    this.openForChild = function ({uid, data, params, func, funcPath
        , config: {pid, id}, context}) {
        if(!pid) {
            return console.error(`child子功能必须在[config]指定属性pid`)
        }

        let linkContext = context.getLinkContext();
        let editContext = linkContext.getChildrenContext(CoreConsts.DefaultEditUid);
        if(!(editContext instanceof EditContext)) {
            return console.warn(`未找到对应uid的可编辑组件[${uid}]`)
        }

        editContext.asyncVisible(data, true).then(edit => {
            // 打开编辑框并且复制pid字段
            let formModel = editContext.getFormContext().getInitModel();
            // 设置pid
            formModel[pid] = data[id || this.getRowKey()];

            this.getRequestParams({data, formModel}, params, funcPath).then(editModel => {
                // 先触发v-model事件
                editContext.getFormContext().setEditModel(editModel)
                // 再触发编辑事件
                edit.$emit('edit', editModel);
            })

        })

    }

    /**
     * edit:set
     * 设置其他功能 比如：设置密码
     * @param param {Config}
     */
    this.openForSet = function ({uid, data, func, params
        , funcPath, config: {copy}, context}) {
        if(uid == CoreConsts.DefaultEditUid) {
            return console.error(`不支持set子功能编辑组件的uid[${uid}]和默认uid[${CoreConsts.DefaultEditUid}]一致`)
        }

        if(!copy) {
            copy = [];
        } else if(typeof copy == 'string') {
            copy = [copy];
        }

        let linkContext = context.getLinkContext();
        let editContext = linkContext.getChildrenContext(uid);
        if(!(editContext instanceof EditContext)) {
            return console.warn(`未找到对应uid的可编辑组件[${uid}]`)
        }

        // 批量操作
        if(data == null) {
            let tableContext = linkContext.getChildrenContext(CoreConsts.DefaultTableUid);
            data = tableContext.getSelectedRows();
            if(!data || data.length == 0) {
                return msgWarn(CoreConsts.BatchDataNullTip).then();
            }
        }

        editContext.openType = 'edit';
        // 打开编辑框后复制对应的字段到新的model
        editContext.asyncVisible(data, true).then(edit => {
            let formModel = editContext.getFormContext().getInitModel();
            this.getRequestParams({data, formModel}, params, funcPath).then(editModel => {
                // 复制属性
                copy.forEach(field => {
                    let fieldPath = field.split("=");
                    if(fieldPath.length == 1) {
                        fieldPath[1] = fieldPath[0];
                    }

                    if(data instanceof Array) {
                        editModel[fieldPath[0]] = data.map(item => item[fieldPath[1]]);
                    } else {
                        let firstField = fieldPath[0];

                        // 数组格式
                        if(firstField.endsWith("[]")) {
                            firstField = firstField.substring(0, firstField.length - 2);
                            editModel[firstField] = [data[fieldPath[1]]];
                        } else {
                            editModel[fieldPath[0]] = data[fieldPath[1]];
                        }
                        if(env.DEV && editModel[firstField] === undefined) {
                            console.warn(`要copy的字段[${fieldPath[1]}]在data里面不存在`)
                        }
                    }
                })

                // 先触发v-model事件
                editContext.getFormContext().setEditModel(editModel)
                // 再触发编辑事件
                edit.$emit('edit', editModel);
            })
        })


    }

    /**
     * 打开某个弹框(模态框或者抽屉)
     * 适用组件 UDrawer or UModal
     * @param param {Config}
     */
    this.openForVisible = function ({uid, url, method
            , funcPath, data, params, func, context}) {
        /**
         * @type {LinkContext|*}
         */
        let linkContext = context.getLinkContext()

        if(linkContext) {
            let visibleContext = linkContext.getChildrenContext(uid);
            if(visibleContext instanceof VisibleContext) {
                this.getRequestParams(data, params, funcPath).then(reqParam => {
                    if(url) {
                        visibleContext.asyncVisible(data).then((vue) => {
                            this.getRequestMethod({func, method})(url, reqParam)
                                .then(({code, message, data}) => {
                                    if(code == CoreConsts.SuccessCode) {
                                        // 再触发编辑事件
                                        vue.$emit('update:source', data);
                                    } else {
                                        msgError(message).then();
                                    }
                                }).catch(reason => console.error(reason));
                            visibleContext.childLinkContext.forEach(item => {
                                // 触发查询功能
                                item.getQueryFunc().forEach(item => item.trigger())
                            })
                        })

                    } else {
                        visibleContext.asyncVisible(data).then((vue) => {
                            let visibleContext = vue.getVisibleContext();
                            if(visibleContext instanceof VisibleContext) {
                                visibleContext.childLinkContext.forEach(item => {
                                    // 触发查询功能
                                    item.getQueryFunc().forEach(item => item.trigger())
                                })
                            }
                        })
                    }
                })
            } else {
                console.warn(`不存在[uid=${uid}]的UDrawer或者UModal组件`)
            }
        }

    }

    /**
     * 打开一个编辑框并且从指定url获取数据
     * func = edit:loading:eid:/user/detail
     * @param param {Config}
     */
    this.openForLoading = function ({url, uid, func, funcPath
        , data, context, params, method, config: {}}) {
        if(uid == CoreConsts.DefaultEditUid) {
            return console.error(`不支持loading子功能操作的编辑组件的uid[${uid}]和默认uid[${CoreConsts.DefaultEditUid}]一致`)
        }

        if(typeof url != 'string') {
            return console.error(`loading子功能必须指定url`)
        }

        let linkContext = context.getLinkContext();
        let editContext = linkContext.getChildrenContext(uid);
        if(editContext == null) {
            return console.error(`未找到对应uid的可编辑组件[${linkContext.uid}:${uid}]`)
        }

        editContext.openType = 'edit';
        // 打开编辑框后复制对应的字段到新的model
        editContext.asyncVisible(data, true).then(edit => {
            editContext.setLoading(true, CoreConsts.FormSpinLoadingTip);
            method = method || "GET";
            this.getRequestParams(data, params, funcPath).then(params => {
                this.getRequestMethod({func, method})(url, params)
                    .then(({code, message, data}) => {
                        if(code == CoreConsts.SuccessCode) {
                            edit.$emit('edit', data);
                            editContext.getFormContext().setEditModel(data);
                        } else {
                            msgError(message).then(() => {});
                        }
                    }).finally(() => editContext.setLoading(false, null))
            })
        })
    }

    /**
     * 更具查询功能点查询
     * @param linkContext {LinkContext}
     */
    this.queryByFunc = function (linkContext) {
        if(linkContext != null) {
            let queryFunc = linkContext.getQueryFunc();
            if(queryFunc != null) {
                queryFunc.forEach(item => item.trigger())
            } else {
                console.warn(`没有找到搜索功能在组件[${linkContext.uid}]`)
            }
        }
    }

    /**
     * 查询主表格数据
     * @param config {Config}配置信息
     */
    this.query = function ({uid, url, context, reload, method
                              , data , func, funcPath, params, callback}) {
        let linkContext = context.getLinkContext();

        let model = {};
        let searchContext = linkContext.getChildrenContext(CoreConsts.DefaultSearchUid);
        if(searchContext instanceof SearchContext) {
            model = searchContext.getModel();
        } else if(context instanceof DefaultFuncContext){
            //
        } else {
            model = data || model;
        }

        let tableContext = linkContext.getChildrenContext(CoreConsts.DefaultTableUid);
        if(!(tableContext instanceof TableContext)) {
            return console.warn(`未找到对应uid的表组件[${linkContext.uid}]`)
        }

        if(tableContext.pageSize && tableContext.currentPage) {
            // 完全相等重置为第一页
            // if(model[CoreConsts.PageSize] == tableContext.pageSize &&
            //     model[CoreConsts.PageCurrent] == tableContext.currentPage) {
            //     tableContext.resetPagination(null, null);
            // }

            model[CoreConsts.PageSize] = tableContext.pageSize;
            model[CoreConsts.PageCurrent] = tableContext.currentPage;
        }

        function reloading(status, tip) {
            if(reload) {
                tableContext.setLoading(status, tip);
            }
        }
        this.getRequestParams(model, params, funcPath).then(params => {
            reloading(true, CoreConsts.TableSpinLoadingTip);
            this.getRequestMethod({func, method})(url, params).then(({code, message, data}) => {
                if(code == CoreConsts.SuccessCode) {
                    if(data instanceof Array){
                        tableContext.setDataSource(data)
                    } else if(data instanceof Object) { // 需要分页
                        let {records, total} = data;

                        tableContext.setTotalRows(total);
                        tableContext.setDataSource(records)
                    }
                } else {
                    msgError(message)
                }

                callback({code, message, data});
            }).finally(() =>  reloading(false, null))
        });
    }

    /**
     * 获取详情(暂时未实现此功能)
     * @param config {Config}
     */
    this.detail = function (config) {

    }

    /**
     * 隐藏主编辑框
     * @param config {Config}
     */
    this.cancel = function ({uid, context}) {
        /**
         * @type LinkContext
         */
        let linkContext = context.getLinkContext();
        let editContext = context;
        if(uid) {
            editContext = linkContext.getChildrenContext(uid);
        }

        if(!(context instanceof EditContext)) {
            return console.error(`cancel功能只能放在编辑组件下面`)
        }

        // 关闭编辑框
        editContext.setVisible(false);
        editContext.resetStatus();
    }
    /**
     * 隐藏主编辑框
     * @param config {Config}
     */
    this.tableForCancel = function ({uid, context}) {
        /**
         * @type LinkContext
         */
        let linkContext = context.getLinkContext();
        let tableContext = linkContext.getChildrenContext(uid) || context;
        if(!(tableContext instanceof TableContext)) {
            return console.error(`cancel功能只能放在表格组件下面`)
        }

        tableContext.cancelEditableRow();
    }
    /**
     * 展开树形的表格
     * @param config {Config}
     * @param expandedRowKeys 要展开的行的key列表 不指定则展开所有
     */
    this.expanded = function ({uid, context}, expandedRowKeys) {
        let linkContext = context.getLinkContext();
        let tableContext = linkContext.getChildrenContext(CoreConsts.DefaultTableUid);
        if(tableContext == null) {
            return console.warn(`未找到对应uid的表组件[${linkContext.uid}:${CoreConsts.DefaultTableUid}]`)
        }

        tableContext.expanded(expandedRowKeys);
    }
    /**
     * 提交主编辑表单
     * @param config {Config}
     * @return void
     */
    this.submit = function ({uid, url, callback, funcPath
        , func, params, method, config: {}, funcConfig, context}) {
        let linkContext = context.getLinkContext();

        let editContext = context;
        if(!(editContext instanceof EditContext)) {
            return console.error(`submit功能只能放在编辑组件下面`)
        }

        // 返回提交执行结果文本(成功/失败原因), 供AI动作(exec_func)同步等待后向用户反馈真实结果; 失败不抛异常, UI/回调路径不变
        return editContext.getFormContext().validate().then(() => {
            let model = editContext.getModel();

            function setLoading(status, tip) {
                funcConfig.setLoading(status);
                editContext.setLoading(status, tip)
            }

            return this.getRequestParams(model, params, funcPath).then(params => {
                setLoading(true, CoreConsts.FormSpinSubmitTip);
                return this.getRequestMethod({method, func})(url, params).then(({code, message, data}) => {
                    let promise = callback({code, message, data, editContext, query: () => this.queryByFunc(linkContext)});
                    return Promise.resolve(promise).then(() => {
                        if (code == CoreConsts.SuccessCode) {
                            msgSuccess(CoreConsts.SubmitSuccessMsg);
                            editContext.setVisible(false);
                            this.queryByFunc(linkContext); // 提交成功, 重新刷新列表
                            return message || CoreConsts.SubmitSuccessMsg;
                        }
                        msgError(message);
                        return `提交信息: ${message}`;
                    }).catch(reason => {
                        return `提交信息：${reason && reason.message || reason}`;
                    });
                }).catch(reason => {
                    msgError(reason);
                    return `提交请求异常: ${reason && reason.message || reason}`;
                }).finally(() => setLoading(false))
            }).catch(reason => {
                if(reason) {
                    msgWarn(reason);
                    return `提交失败: ${reason && reason.message || reason}`;
                }
            })
        }).catch(({errorFields}) => {
            // 表单校验未通过(字段清单给AI, 便于修正后重试)
            let names = (errorFields || []).map(field => String(field && field.name || '')).filter(name => name).join(', ');
            return `表单校验未通过${names ? `: ${names}` : ''}`;
        })
    }

    /**
     * 表格编辑的提交
     * @param config {Config}
     */
    this.tableForSubmit = function ({uid, url, callback, funcPath, data
                                        , func, params, method, config: {}, funcConfig, context}) {
        let linkContext = context.getLinkContext();
        // 返回提交执行结果文本(成功/失败原因), 供AI动作(exec_func)同步等待后向用户反馈真实结果; 失败不抛异常, UI/回调路径不变
        return context.submitEditableRow().then((editModel) => {
            return this.getRequestParams(editModel, params, funcPath).then(params => {
                context.setLoading(true, CoreConsts.FormSpinSubmitTip)
                return this.getRequestMethod({method, func})(url, params).then(({code, message, data}) => {
                    let promise = callback({code, message, data, query: () => this.queryByFunc(linkContext)});
                    return Promise.resolve(promise).then(() => {
                        if (code == CoreConsts.SuccessCode) {
                            context.setEditRow(null);
                            msgSuccess(CoreConsts.SubmitSuccessMsg).then();
                            this.queryByFunc(linkContext); // 提交成功, 重新刷新列表
                            return message || CoreConsts.SubmitSuccessMsg;
                        }
                        msgError(message).then();
                        return `提交失败: ${message}`;
                    }).catch(reason => {
                        console.error(reason);
                        return `提交失败: ${reason && reason.message || reason}`;
                    });
                }).catch(reason => {
                    msgError(reason);
                    return `提交请求异常: ${reason && reason.message || reason}`;
                })
            }).catch(reason => {
                if(reason) {
                    msgWarn(reason);
                    return `提交失败: ${reason && reason.message || reason}`;
                }
            }).finally(() => context.setLoading(false, null))
        }).catch(reason => {
            console.error(reason);
            return `提交失败: ${reason && reason.message || reason}`;
        })
    }

    /**
     * 重置主编辑表单
     * @param config {Config}
     */
    this.resetEditModel = function ({uid, func, funcPath, context, params}) {
        /**
         * @type LinkContext
         */
        let linkContext = context.getLinkContext();

        let editContext = context;
        if(uid) { // 获取指定的操作组件
            editContext = linkContext.getChildrenContext(uid);
        }

        if(!(editContext instanceof EditContext)) {
            return console.error(`reset功能只能放在编辑组件下面`)
        }

        editContext.resetStatus();
        let editModel = editContext.getFormContext().getEditModel();
        // 编辑时需要重新获取详情
        if(this.isEdit(editModel)) {
            let editFunc = linkContext.getFunc(FuncNameMeta.EDIT, context.uid);
            let method = editFunc.getMethod();
            this.getRequestParams(editModel, params, funcPath).then(params => {
                editContext.setLoading(true, CoreConsts.FormSpinResetTip);
                this.getRequestMethod({method, func: FuncNameMeta.EDIT})(editFunc.getUrl(), params)
                    .then(({code, message, data}) => {
                        if(code == CoreConsts.SuccessCode) {
                            editContext.getFormContext().setEditModel(data);
                        } else {
                            msgError(message);
                        }
                    }).finally(() => editContext.setLoading(false, null))
            })

        } else { // 新增的重置只需要重置字段
            editContext.getFormContext().resetFields();
        }
    }

    /**
     * 重置搜索表单
     * @param config {Config}
     */
    this.resetSearchModel = function ({uid, context, reload}) {
        let linkContext = context.getLinkContext();

        let searchContext = context;
        if(uid) {
            searchContext = linkContext.getChildrenContext(uid);
        }

        if(!(searchContext instanceof SearchContext)) {
            return console.error(`reset功能只能放在搜索组件下面`)
        }

        searchContext.getFormContext().resetFields();
        if(reload) { // 如果需要重新刷新列表
            let tableContext = linkContext.getChildrenContext(CoreConsts.DefaultTableUid);
            if(tableContext instanceof TableContext) {
                tableContext.resetPagination(null, null);
            }
            this.queryByFunc(linkContext);
        }
    }

    /**
     * excel导出
     * @param config {Config}
     */
    this.excelExport = function ({uid, url, func, funcPath, params
         , config: {fileName}, method, context}) {
        let linkContext = context.getLinkContext();
        let searchContext = linkContext.getChildrenContext(uid || CoreConsts.DefaultSearchUid);
        if(searchContext == null) {
            return console.warn(`未找到对应uid的可搜索组件[${linkContext.uid}]`)
        }

        let model = searchContext.getModel();
        this.getRequestParams(model, params, funcPath).then(params => {
            let messageType = msgLoading(CoreConsts.ExcelDownloadTip, 0);
            let config = {responseType: 'blob'}; config[CoreConsts.CancelRespResolver] = true;
            this.getRequestMethod({func, method})(url, params, config).then(resp => {
                let {data, headers} = resp;
                if(headers['content-type']) {
                    if(headers['content-type'].indexOf('application/json') > -1 ) {
                        if(data instanceof Object) {
                            SysUtils.readBlobToJson(data, ({message}) => {
                                msgError(message).then();
                            });

                            return messageType();
                        }
                    }
                }

                let header = resp.headers["content-disposition"];
                fileName = fileName || (header != null ? decodeURI(header.split("filename=")[1]) : dayjs().format("YYYY-MM-DD") + ".xlsx");
                SysUtils.downloadFile(resp.data, "application/vnd.ms-excel;charset=utf-8", fileName)
            }).catch(reason => console.error(reason)).finally(messageType)
        })

    }

    /**
     * excel excel导入
     * @param config {Config}
     */
    this.excelImport = function ({$vue, uid, data, url, method, func, funcPath, context, params, config}) {
        let linkContext = context.getLinkContext();
        // let searchContext = linkContext.getChildrenContext(uid || CoreConsts.DefaultSearchUid);
        let fileList;
        let uploadConfig = {data, accept: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,application/vnd.ms-excel'
            , beforeUpload: (file) => {
                fileList = file;
                return Promise.reject();
            }, onReject: (file) => {
                msgWarn("文件格式错误(支持.xls, .xlsx)").then();
            }, onRemove: (file) => {
                fileList = null;
            }, templateUrl: ''}
        uploadConfig = Object.assign(uploadConfig, config['uploadConfig']);
        let assign = Object.assign({title: '', okText: "导入", icon: null, maskClosable: true, onOk: () => {
                return new Promise((resolve, reject) => {
                    if(fileList) {
                        this.getRequestParams(data, params, funcPath).then(reqParams => {
                            const formData = new FormData();
                            formData.append("file", fileList);
                            if(reqParams) {
                                formData.append("data", new Blob([JSON.stringify(reqParams)], {type: 'application/json'}))
                            }

                            this.getRequestMethod({method, func})(url, formData, {headers: {'content-type': 'multipart/form-data'}}).then(({code, message, data}) => {
                                if(code == CoreConsts.SuccessCode) {
                                    resolve();
                                } else {
                                    msgError(message).then(() => {
                                        reject(message);
                                    });
                                }
                            }).catch(reason => {
                                msgError("请求失败").then(reject);
                            })
                        })
                    } else {
                        reject("请选择文件");
                        msgError("请选择文件").then();
                    }
                })
            }}, config);

        if(!assign.content) {
            uploadConfig['maxCount'] = 1;
            assign.content = createVNode(ImportUpload, uploadConfig);
        }

        $vue['modalRef'] = Modal.info(assign);
    }

    /**
     * 下载
     * @param config {Config}
     */
    this.download = function ({url, funcPath, data, params, func, method
          , context, uid, config: {fileName, contentType}}) {
        if(funcPath[1] == CoreConsts.ChildFuncNameMeta.BATCH) { // 从列表里面获取数据
            let linkContext = context.getLinkContext();
            uid = uid || CoreConsts.DefaultTableUid
            let tableContext = linkContext.getChildrenContext(uid);
            if(tableContext instanceof TableContext) {
                data = tableContext.getSelectedRows();
            }
        }

        this.getRequestParams(data, params, funcPath).then(params => {
            let messageType = msgLoading(CoreConsts.FileDownloadTip, 0);
            let config = {responseType: 'blob'}; config[CoreConsts.CancelRespResolver] = true;
            this.getRequestMethod({method, func})(url, params, config).then(resp => {
                let {data, headers} = resp;
                if(headers['content-type']) {
                    if(headers['content-type'].indexOf('application/json') > -1 ) {
                        if(data instanceof Object) {
                            SysUtils.readBlobToJson(data, ({message}) => {
                                msgError(message).then();
                            })
                            return messageType();
                        }
                    }
                }

                let header = headers["content-disposition"];
                contentType = contentType || headers['content-type'] || 'application/octet-stream';
                fileName = fileName || (header ? decodeURI(header.split("filename=")[1]) : console.warn("未指定文件名"));
                SysUtils.downloadFile(data, contentType, fileName)
            }).finally(messageType)
        });

    }

    /**
     * 其他功能的执行操作
     * 如果需要确认请用：func='confirm'
     * @param config {Config}
     */
    this.otherFuncAjax = function ({func, data, url, reload, params, context
               , uid, funcPath, method, config: {confirmTitle, confirmContent}, callback}) {

        let linkContext = context.getLinkContext();

        function reload(status, tip) {
            if(reload) {
                let tableContext = linkContext.getChildrenContext(CoreConsts.DefaultTableUid);
                if(tableContext instanceof TableContext) {
                    tableContext.setLoading(status, tip)
                }
            }
        }

        function request() {
            // 返回执行结果文本(成功/失败原因)
            return this.getRequestParams(data, params, funcPath).then(params => {
                reload(true, CoreConsts.AjaxExecTipping);
                return this.getRequestMethod({func, method})(url, params).then(({code, message, data}) => {
                    let result;
                    if (code == CoreConsts.SuccessCode) {
                        msgSuccess(message || CoreConsts.DefaultExecSuccess);
                        if (reload) {
                            this.queryByFunc(linkContext); // 重新刷新列表
                        }
                        result = message || CoreConsts.DefaultExecSuccess;
                    } else {
                        msgError(message);
                        result = `执行失败: ${message}`;
                    }

                    callback({code, message, data}, params)
                    return result;
                }).catch(reason => {
                    console.error(reason);
                    return `请求异常: ${reason && reason.message || reason}`;
                }).finally(() => reload(false, null));
            });
        }

        if(funcPath[1] && funcPath[1].toUpperCase() == CoreConsts.ChildFuncNameMeta.CONFIRM) {
            let title = confirmTitle || CoreConsts.DefaultConfirmTitle;
            let content = confirmContent || CoreConsts.DefaultConfirmTitle;
            // onOk透传request的执行结果Promise
            return this.confirmAction(context, {title, content, onOk: () => request.call(this), onCancel: () => null})
        } else {
            return request.call(this);
        }
    }

    /**
     * 其他功能的执行操作
     * @param config {Config}
     */
    this.linkTo = function ({url, data, router}) {
        router.isReady().then(() => {
            router.push({ path: url, query: data || {}}).catch(reason => console.error(reason))
        });
    }

    /**
     * @param config {{method: (Config.method|String), func: (Config.func|String)}}
     * @return {Promise<AxiosResponse<any>>|*}
     */
    this.getRequestMethod = function ({func, method}) {
        let request = TypeMethodMaps[method];
        return request || (() => Promise.reject(`功能[${func}]查找不到请求方法[${method}]`));
    }

    /**
     * 通过前缀获取搜索上下文
     * @param uid
     * @param prefix ULinkView uid
     * @return {null | SearchContext}
     */
    this.getSearchContextByUid = function (prefix, uid) {
        let context = this.getViewContext().getContextByPrefixAndUid(prefix, uid);
        if(context == null) {
            return context;
        } else if(!(context instanceof SearchContext)) {
            console.error(`此uid[${uid}]不是搜索组件`)
            return null;
        } else {
            return context;
        }
    }

    /**
     * @param uid
     * @return {null|TableContext}
     */
    this.getTableContextByUid = function (uid) {
        let context = this.getViewContext().getContextByUid(uid);
        if(context == null) {
            return context;
        } else if(!(context instanceof TableContext)) {
            console.error(`此uid[${uid}]不是表组件`)
            return null;
        } else {
            return context;
        }
    }

    /**
     * 通过前缀获取编辑上下文
     * @param uid
     * @return {EditContext}
     */
    this.getEditContextByUid = function (uid) {
        return this.getViewContext().getContextByUid(uid);
    }
}

/**
 * 功能元数据对象
 * @constructor
 */
export function FuncMetaContext(editFunMetas, tableFunMetas, searchFunMetas) {

    this.queryFunc = [];
    this.editFunMetas = editFunMetas || [];
    this.tableFunMetas = tableFunMetas || [];
    this.searchFunMetas = searchFunMetas || [];

    this.getEditMeta = function (field) {
        return this.editFunMetas.find(item => item.field == field)
    }

    this.getTableMeta = function (field) {
        return this.tableFunMetas.find(item => item.field == field)
    }

    this.getSearchMeta = function (field) {
        return this.searchFunMetas.find(item => item.field == field)
    }
}

/**
 * 功能注册键统一口径: 基础键(func 或 func:行key)上追加显式uid实例段
 * 仅显式uid(页面写死的操作目标标识, 如行内物模型=model/批量=batch)参与键, 无uid时保持基础键格式(与动作引擎getFunc直查兼容)
 * @param baseKey {String}
 * @param config {FuncConfig}
 * @return {String}
 */
function funcRegisterKey(baseKey, config) {
    let instanceUid = config && config.getProp instanceof Function ? config.getProp('uid') : null;
    if(instanceUid) {
        return baseKey + ':' + instanceUid;
    }
    return baseKey;
}

/**
 * 功能查询键统一口径: 与注册组键(funcRegisterKey)同构的查询侧组键, 各上下文getFunc按此键直查funcMetas
 * 键序固定为 func:行key:uid 与注册键同构: 行段仅rowKey值有效时追加(仅TableContext传入), 实例段仅显式uid(操作目标标识)时追加, 默认操作目标值不入键(与注册侧仅显式uid参与键一致)
 * @param func {String}
 * @param uid {String} 显式操作目标标识(如行内物模型=model/批量=batch, 同行同编码多按钮定位; 无则不传)
 * @param rowKey {String|Number} 表格行key值(仅TableContext行按钮传入; 组件级上下文无行维度不传)
 * @return {String}
 */
function funcQueryKey(func, uid, rowKey) {
    let key = func;
    if(rowKey != null && rowKey !== '') {
        key += ':' + rowKey;
    }
    if(uid) {
        key += ':' + uid;
    }
    return key;
}

/**
 * 搜索栏
 * @param linkContext {LinkContext}
 * @constructor
 */
export function SearchContext(linkContext) {
    // 用于关联各个组件(表格、编辑、详情)
    this.uid = '';
    // 存储UFuncBtn和UFuncTag组件的信息
    this.funcMetas = {};

    /**
     * 获取功能配置: 按注册键(func[:uid])键直查, 与register/funcRegisterKey组键口径一致
     * @param func 功能编码
     * @param uid 显式操作目标标识(如model/batch, 同func多按钮并存时定位实例)
     * @return {FuncConfig | null}
     */
    this.getFunc = function (func, uid) {
        func = func.toUpperCase();
        let key = funcQueryKey(func, uid);
        let funcMeta = this.funcMetas[key];

        // 遗留问题, query和view都代表查询(无实例段时互通, 与funcMetas['QUERY']||funcMetas['VIEW']同序)
        if(!funcMeta && (func == 'QUERY' || func == 'VIEW')) {
            funcMeta = this.funcMetas[funcQueryKey(func == 'QUERY' ? 'VIEW' : 'QUERY', uid)];
        }

        return funcMeta;
    }

    /**
     * 注册功能: 键=func(显式uid按钮追加实例段func:uid, 同func多按钮并存互不覆盖, 无uid保持原键与getFunc直查兼容)
     * @param config {FuncConfig}
     */
    this.register = function (config) {
        if(config instanceof FuncConfig) {
            this.funcMetas[funcRegisterKey(config.getFunc(), config)] = config;
        }
    }

    /**
     * 注销功能(与register对称): 仅当funcMetas中当前占用者是本实例时移除(条目已被其它实例覆盖注册时不动, 避免误删存活按钮)
     * @param config {FuncConfig}
     */
    this.unregister = function (config) {
        let key = funcRegisterKey(config.getFunc(), config);
        if(this.funcMetas[key] === config) {
            delete this.funcMetas[key];
        }
    }

    this.getModel = function () {
        return this.getFormContext().getEditModel();
    }

    /**
     * 重置当前表单
     */
    this.reset = function () {
        this.getFormContext().resetFields();
    }
    /**
     * @return {FormContext}
     */
    this.getFormContext = () => new FormContext();
    /**
     * @return {LinkContext}
     */
    this.getLinkContext = () => linkContext;

    this.get$View = function () {
        // viewContext['__$View'] 不可能为空, 为空说明异常
        return this.getLinkContext().getViewContext()['__$View'] || new $View(null);
    }

}


/**
 * 标签页容器(UTabs): 页面/弹框内多面板页签的上下文
 * 构造时持有UTabs的props(响应式): uid/activeKey在内部自props现取(惰性物化, 无同步维护), 面板声明由UTabs渲染期同步进paneDecls——组件仅剩模板与生命周期壳;
 * 面板内容惰性渲染: 未访问面板未挂载(内部组件上下文未注册), 已访问面板切走仅隐藏保持挂载(默认未开destroyOnHidden),
 * 首次切换至某标签后需重查焦点视图获取该面板内组件清单
 * 面板声明双通道: 1) items prop声明(推荐, 面板内容经contentRender插槽分发); 2) 默认插槽UTabPane声明(仅items未声明时启用);
 * 两通道的声明(key/tab/disabled)均由UTabs渲染期统一翻译收进paneDecls(键=面板key, 值即面板清单)——自描述与switch_tab切换目标的唯一数据源
 * @param linkContext {LinkContext} 属于哪个容器
 * @param props 组件props(uid容器标识/activeKey激活key, 读取时经响应式props现取最新)
 * @constructor
 */
export function TabContext(linkContext, props) {
    // 标签页容器标识(构造时自props.uid提取, 需在所属容器内唯一, 用于焦点视图定位与switch_tab动作目标)
    this.uid = props.uid;

    // 切换回调(UTabs注入, 驱动父组件v-model回写使a-tabs受控激活变化)
    this.switcher = null;

    // 面板声明表(键=面板key): UTabs渲染期把items与UTabPane两通道声明统一收进此处, 整体同步(每次渲染重建保持新鲜)
    this.paneDecls = {};
    /**
     * 同步面板声明(UTabs渲染期调用, 整体替换保持新鲜)
     * @param decls {Object} 键=面板key, 值={key: string, tab: string, disabled: boolean}
     */
    this.syncPaneDecls = function (decls) {
        this.paneDecls = decls || {};
    };

    this.getActiveKey = function () {
        return props.activeKey;
    };

    /**
     * 切换至指定标签(供动作switch_tab调用, 带齐全校验与友好提示)
     * @param key 标签key
     * @return {string} 执行结果描述
     */
    this.switchTo = function (key) {
        if(!(this.switcher instanceof Function)) {
            throw new Error(`标签容器[${this.uid}]未就绪(组件未挂载完成), 稍后重试`);
        }
        let pane = this.paneDecls[key];
        if(!pane) {
            throw new Error(`标签[${key}]不存在, 可选标签: ${Object.values(this.paneDecls).map(item => item.tab || item.key).join('/')}`);
        }
        if(pane.disabled) {
            throw new Error(`标签[${pane.tab || pane.key}]已禁用, 不可切换`);
        }
        if(this.activeKey === key) {
            return `当前已在标签[${pane.tab || pane.key}], 无需切换`;
        }
        this.switcher(key);
        return `已切换至标签[${pane.tab || pane.key}], 重查焦点视图获取该标签内组件清单`;
    }

    /**
     * @return {LinkContext}
     */
    this.getLinkContext = () => linkContext;
}


/**
 * 编辑
 * @param linkContext {LinkContext} 属于哪个容器
 * @constructor
 */
export function EditContext(linkContext) {
    // 编辑弹框本身也是弹框(模态框/抽屉): 基于VisibleContext共享弹框身份与语义(标题/打开能力), 同名成员在此之后重新赋值覆盖
    VisibleContext.call(this, linkContext);

    // 用于关联各个组件(搜索、表格、详情)
    this.uid = null;
    // 当前打开类型(add or edit)
    this.openType = null;
    // 弹框标题(由UFormModal/UFormDrawer渲染期同步, 供页面骨架自描述)
    this.title = null;
    // 存储UFuncBtn和UFuncTag组件的信息
    this.funcMetas = {};

    // 获取功能组件配置: 按注册键(func[:uid])键直查, 与register/funcRegisterKey组键口径一致
    this.getFunc = function (func, uid) {
        return this.funcMetas[funcQueryKey(func.toUpperCase(), uid)];
    }

    /**
     * 注册功能: 键=func(显式uid按钮追加实例段func:uid, 同func多按钮并存互不覆盖, 无uid保持原键与getFunc直查兼容)
     * @param config {FuncConfig}
     */
    this.register = function (config) {
        let func = config.getFunc();
        this.funcMetas[funcRegisterKey(func, config)] = config;
    }

    /**
     * 注销功能(与register对称): 仅当funcMetas中当前占用者是本实例时移除(条目已被其它实例覆盖注册时不动, 避免误删存活按钮)
     * @param config {FuncConfig}
     */
    this.unregister = function (config) {
        let key = funcRegisterKey(config.getFunc(), config);
        if(this.funcMetas[key] === config) {
            delete this.funcMetas[key];
        }
    }

    this.getModel = function () {
        let formContext = this.getFormContext();
        return formContext ? formContext.getEditModel() : null;
    }

    this.setModel = function (model) {
        this.getFormContext().setEditModel(model);
    }

    /**
     * 重置当前表单字段
     */
    this.reset = function () {
        this.getFormContext().resetFields();
    }

    /**
     * 重置当前组件各个状态
     */
    this.resetStatus = function () {
        // 重置提交按钮的加载状态
        let submit = this.getLinkContext()
            .getFunc(FuncNameMeta.SUBMIT, this.uid); // 获取当前编辑组件下的提交按钮

        if(submit) {
            submit.setLoading(false);
        }

        // 重置编辑框的加载状态
        this.setLoading(false)
        // 移除表单的所有的校验
        this.getFormContext().clearValidate();
    }

    /**
     * 关闭当前弹框
     */
    this.cancel = function () {
        this.setVisible(false);
    }

    // 修改加载状态
    this.setLoading = (status, tip) => {Unmount()};
    // 修改弹框状态(模态框或者抽屉框)
    this.setVisible = (status) => {Unmount()};
    // 异步打开弹框(模态框或者抽屉框) 等表单初始化完成
    this.asyncVisible = (row, isResetToInit) => Promise.reject("未挂载");

    /**
     * @return {FormContext | null}
     */
    this.getFormContext = Unmount;
    /**
     * @return {LinkContext}
     */
    this.getLinkContext = () => linkContext;
    /**
     * @returns {$View}
     */
    this.get$View = function () {
        // viewContext['__$View'] 不可能为空, 为空说明异常
        return this.getLinkContext().getViewContext()['__$View'] || new $View(null);
    }

    // ===== 容器面(EditContext兼任LinkContext容器: 编辑弹框即自身内容的容器) =====
    // FormModal/FormDrawer provide(LinkViewContextKey, editContext): 弹框插槽内无ULinkView包裹直挂的组件(UTabs/UTable/BasicSearch等)
    // 经inject注册进弹框自身(childContextMaps)而非穿透外层容器——组件与弹框同域, 收集端(焦点视图/schema)以弹框打开状态整体过滤,
    // 杜绝直挂外层(与EditContext平级)时未打开弹框的内容(如标签页)泄漏进焦点视图;
    // 注册键/全局fullUid=外层容器uid_弹框uid:组件uid(弹框域段下划线连接, 子组件段保留冒号层级; 带完整前缀, 页面内多实例同uid弹框互不冲突);
    // 面板容器(ULinkView)收编childLinkContext的既有路径不受影响(其LinkContext.parent经Modal/Drawer位置分支统一覆盖为editContext, 与注入源无关)
    this.childContextMaps = {};
    this.addChildrenContext = function (childContext) {
        if(!(childContext && childContext.uid && linkContext && this.uid)) {
            return; // 脱离容器单独使用或弹框未注册(uid未赋值)时静默忽略不抛错(LinkContext注册必有外层容器, 编辑弹框可脱离容器)
        }
        let fullUid = linkContext.uid + "_" + this.uid + ":" + childContext.uid;
        if(!this.childContextMaps[fullUid]) {
            this.childContextMaps[fullUid] = childContext;
            this.getViewContext().addContextByUid(fullUid, childContext);
        } else {
            console.warn(`已经存在同名的组件[${fullUid}]`)
        }
    }
    this.removeChildrenContext = function (childContext) {
        if(childContext && childContext.uid && linkContext && this.uid) {
            let fullUid = linkContext.uid + "_" + this.uid + ":" + childContext.uid;
            if(this.childContextMaps[fullUid]) {
                delete this.childContextMaps[fullUid];
                this.getViewContext().removeContextByUid(fullUid);
            }
        }
    }
    /**
     * 获取直挂子组件上下文(与LinkContext#getChildrenContext同口径: 按钮执行器/联动经context.getLinkContext()定位目标)
     * 弹框内直挂表格的编辑/删除等按钮context.getLinkContext()=本弹框editContext, 由此定位同域的目标弹框/表格
     * @param uid 子组件uid(跨视图引用传完整fullUid)
     * @return {TabContext | TableContext | SearchContext | EditContext | null}
     */
    this.getChildrenContext = function (uid) {
        if(!(uid && linkContext && this.uid)) return null;
        // 1. 先在弹框自身直挂组件内查找(兼容容器语义)
        let context = this.childContextMaps[linkContext.uid + "_" + this.uid + ":" + uid];
        if(context) return context;
        // 2. 本地未找到且uid包含冒号, 视为fullUid在ViewContext全局查找(跨视图引用)
        if(uid.indexOf(':') > -1) {
            return this.getViewContext().getContextByUid(uid);
        }
        return null;
    }
    /**
     * @return {ViewContext}
     */
    this.getViewContext = () => linkContext ? linkContext.getViewContext() : null;
}
// EditContext基于VisibleContext(编辑弹框上下文同时也是弹框): instanceof VisibleContext为true, 供骨架/动作引擎以弹框视角识别
EditContext.prototype = Object.create(VisibleContext.prototype);
EditContext.prototype.constructor = EditContext;

/**
 * 表格组件{UTable | UEditTable}上下文对象
 * @param linkContext {LinkContext} ULinkView组件上下文对象
 * @constructor
 */
export function TableContext(linkContext) {
    // 用于关联各个组件(搜索、编辑、详情)
    this.uid = null;

    // 在使用UFuncTag#data组件时, 将可以获取该值
    this.CurrentRow = null;

    this.reset = true; // 重置分页
    this.pageSize = null;
    this.currentPage = null;
    // 存储UBtn和UTag组件的信息
    this.funcMetas = {};

    // 是否是可编辑表格
    this.editable = false;

    // 获取在编辑的行
    this.getEditRow = () => null;
    // 设置当前正在编辑的行
    this.setEditRow = (row) => null;

    // 新增编辑行
    this.addEditableRow = () => null;
    // 删除编辑行
    this.delEditableRow = () => false;
    // 取消编辑行
    this.cancelEditableRow = () => null
    // 保存编辑行
    this.submitEditableRow = () => Promise.resolve();

    this.getRowKey = () => null;

    // 可编辑单元格控件实例登记(渲染期CellEditProvider按[行:列字段]注册, 供写值后触发已绑定事件; 行卸载随控件注销自动清理)
    // 行键=rowKey值(与行按钮注册/动作引擎定位同口径: 数据刷新后行对象引用变化, 值键仍能命中复用行注册的控件)
    this.cellInstances = new Map();
    // 行键解析: 配置了rowKey且行含该字段时取行键值(String化), 否则回退行对象引用(未配置rowKey的表格, 行不可精确定位)
    this.cellRowKeyOf = function (record) {
        if(record && record instanceof Object) {
            let rowKeyField = this.getRowKey();
            if(rowKeyField) {
                let rowValue = record[rowKeyField];
                if(rowValue != null && rowValue !== '') {
                    return 'k:' + rowValue;
                }
            }
            return record;
        }
        return null;
    }
    this.registerCell = function (record, field, instance) {
        let row = this.cellRowKeyOf(record);
        if(row == null) {
            return;
        }
        let rowMap = this.cellInstances.get(row);
        if(!rowMap) {
            this.cellInstances.set(row, rowMap = new Map());
        }
        rowMap.set(field, instance);
    }
    this.unregisterCell = function (record, field, instance) {
        let row = this.cellRowKeyOf(record);
        if(row == null) {
            return;
        }
        let rowMap = this.cellInstances.get(row);
        // 仅删除与当前实例匹配的注册(避免旧控件卸载时误删新注册实例)
        if(rowMap && (instance === undefined || rowMap.get(field) === instance)) {
            rowMap.delete(field);
            if(rowMap.size === 0) {
                this.cellInstances.delete(row);
            }
        }
    }
    /**
     * 获取可编辑单元格当前渲染的控件实例(键口径与registerCell一致: rowKey值优先, 未配置时行对象引用)
     * @param record 行数据对象(含rowKey字段值或与注册时同引用)
     * @param field 列字段
     * @return {Object|null} 控件实例(混入MixinsForm含fireChange/fireSelect, 未注册返回null)
     */
    this.getCellInstance = function (record, field) {
        let row = this.cellRowKeyOf(record);
        if(row == null) {
            return null;
        }
        let rowMap = this.cellInstances.get(row);
        return rowMap ? (rowMap.get(field) || null) : null;
    }

    /**
     * 获取功能配置: 按注册键(func[:行key][:uid])键直查, 与register组键口径一致(funcQueryKey);
     * 行按钮传行key值(如 EDIT:1001), 同行同编码多按钮追加显式uid(如 OPEN:1001:model); 表头级按钮仅传func
     * @param func 功能编码
     * @param uid 显式操作目标标识(如行内物模型=model/批量=batch, 同行同编码多按钮时必传; 表头级按钮不传)
     * @param rowKey 表格行key值(行按钮定位; 表头级按钮不传)
     * @return {FuncConfig|null}
     */
    this.getFunc = function (func, uid, rowKey) {
        return this.funcMetas[funcQueryKey(func.toUpperCase(), uid, rowKey)];
    }

    /**
     * @return {Array | null}
     */
    this.getColumns = () => null;

    /**
     * 注册功能: 表格行按钮按 func+行数据key 组合注册(funcMetas[func:data[getRowKey()]], 同一行不同功能/不同行同功能互不覆盖);
     * 无行数据(表头级按钮)或未配置rowKey/数据无该字段时回退按func注册;
     * 显式配置uid的按钮(操作目标标识, 如行内物模型=model/批量=batch)键追加实例段(func:行key:uid): 同行同编码多按钮互不覆盖,
     * 无uid按钮保持原键(单实例场景, 与动作引擎直查兼容)
     * @param config {FuncConfig}
     */
    this.register = function (config) {
        let func = config.getFunc();
        let key = func;
        let data = config.getProp('data');
        if(data) {
            let rowValue = data[this.getRowKey()];
            if(rowValue != null && rowValue !== '') {
                key = func + ':' + rowValue;
            }
        }
        // 实例段: 仅显式uid(页面写死的操作目标标识)参与键(funcRegisterKey), 无uid时保持 func[:行key] 原键格式
        this.funcMetas[funcRegisterKey(key, config)] = config;
    }

    /**
     * 注销功能(与register对称): 按 func+行数据key 组合键移除本实例注册的条目(行卸载后重新注册的数据自然覆盖)
     * @param config {FuncConfig}
     */
    this.unregister = function (config) {
        let func = config.getFunc();
        let key = func;
        let data = config.getProp('data');
        if(data) {
            let rowValue = data[this.getRowKey()];
            if(rowValue != null && rowValue !== '') {
                key = func + ':' + rowValue;
            }
        }
        // 实例段(与register对称): 仅显式uid参与键(funcRegisterKey), 无uid时保持 func[:行key] 原键格式
        key = funcRegisterKey(key, config);
        if(this.funcMetas[key] === config) {
            delete this.funcMetas[key];
        }
    }

    this.del = function (url, data) {}

    /**
     * 展开行
     * @param expandedRowKeys 要展开的行key, 不传则展开所有行
     */
    this.expanded = (expandedRowKeys) => {};

    /**
     * 重置当前页
     */
    this.resetPagination = (page, size) => {};

    /**
     * 获取当前页码
     */
    this.getCurrentPage = function () {
        return this.currentPage;
    }

    /**
     * 获取每页条数
     */
    this.getPageSize = function () {
        return this.pageSize;
    }

    /**
     * 获取当前点击的行
     * 使用此方法必须在表格的操作栏里面使用以下组件作为功能按钮
     * @see UTag 必须设置data属性
     */
    this.getCurrentRow = function () {
        return this.CurrentRow || unUFuncTag();
    }

    /**
     * 页码改变
     * @param current 当前页
     * @param pageSize 每页条数
     */
    this.pageChange = function (current, pageSize) {
        this.pageSize = pageSize;
        this.currentPage = current;

        linkContext.getQueryFunc().forEach(item => {
            item.trigger();
        })
    }

    /**
     * 每页条数改变事件
     * @param current 当前页
     * @param pageSize 每页条数
     */
    this.sizeChange = function (current, pageSize) {
        this.pageSize = pageSize;
        this.currentPage = current;

        linkContext.getQueryFunc().forEach(item => {
            item.trigger();
        })
    }

    /**
     * 获取当前选中的行key列表
     * @returns {*[]}
     */
    this.getSelectedRowKeys = () => [];

    /**
     * 获取当前选中的行列表
     * @returns {*[]}
     */
    this.getSelectedRows = () => [];

    /**
     * @return {LinkContext}
     */
    this.getLinkContext = () => linkContext;

    /**
     * 获取当前页面视图
     * @returns {$View}
     */
    this.get$View = function () {
        // viewContext['__$View'] 不可能为空, 为空说明异常
        return this.getLinkContext().getViewContext()['__$View'] || new $View(null);
    }

    /**
     * 粘表头
     * @param status {Boolean}
     */
    this.setSticky = (status) => {}

    /**
     * 设置表格的加载状态
     * @param tip {String}
     * @param status {Boolean}
     */
    this.setLoading = (status, tip) => {};

    /**
     * 设置数据源
     * @param dataSource {Boolean}
     */
    this.setDataSource = (dataSource) => {};
}

/**
 * 详情
 * @param linkContext {LinkContext}
 * @constructor
 */
export function DetailContext(linkContext) {
    // 用于关联各个组件(搜索、编辑、表格)
    this.uid = '';

    // 存储UFuncBtn和UFuncTag组件的信息
    this.funcMetas = {};

    // 获取功能组件配置: 按注册键(func[:uid])键直查, 与regFunc/funcRegisterKey组键口径一致
    this.getFunc = function (func, uid) {
        return this.funcMetas[funcQueryKey(func.toUpperCase(), uid)];
    }
    this.regFunc = function (func, config) {
        // 键=func(显式uid按钮追加实例段func:uid, 同func多按钮并存互不覆盖, 无uid保持原键与getFunc直查兼容)
        this.funcMetas[funcRegisterKey(func, config)] = config;
        linkContext.registerFunc(config);
    }

    /**
     * 注销功能(与regFunc对称): 仅当funcMetas中当前占用者是本实例时移除(条目已被其它实例覆盖注册时不动, 避免误删存活按钮)
     * @param config {FuncConfig}
     */
    this.unregister = function (config) {
        let key = funcRegisterKey(config.getFunc(), config);
        if(this.funcMetas[key] === config) {
            delete this.funcMetas[key];
        }
    }
    /**
     * @return {LinkContext}
     */
    this.getLinkContext = () => linkContext;

    this.get$View = function () {
        // viewContext['__$View'] 不可能为空, 为空说明异常
        return linkContext.getViewContext()['__$View'] || new $View(null);
    }
}

/**
 * 默认功能上下文, 由View组件提供
 * @param linkContext {LinkContext} ULinkView组件上下文对象
 * @constructor
 */
export function DefaultFuncContext(linkContext) {

    this.uid = CoreConsts.DefaultFuncContextUid;

    this.funcMetas = new Map();

    /**
     * @param funcMeta {FuncMeta | FuncConfig}
     */
    this.register = (funcMeta) => {
        if(funcMeta instanceof FuncConfig) {
            const funcConfig = funcMeta;
            const label = funcConfig.title();
            funcConfig['__key__'] = SysUtils.nextId('btn:')
            funcMeta = new FuncMeta(funcConfig['__key__'], label, 'func', funcConfig)
        }

        this.funcMetas.set(funcMeta.key, funcMeta);
    };

    /**
     * @param funcMeta {string | FuncMeta}
     */
    this.unregister = (funcMeta) => {
        const key = funcMeta instanceof FuncMeta ? funcMeta.key
            : funcMeta instanceof FuncConfig ? funcMeta['__key__'] : funcMeta;
        this.funcMetas.delete(key);
    }

    /**
     * 按key取已注册的FuncMeta(funcMetas为Map<key,FuncMeta>, key即唯一标识)
     * @param key {String} FuncMeta.key
     * @return {FuncMeta|undefined}
     */
    this.getFunc = (key) => this.funcMetas.get(key);

    /**
     * @return {LinkContext}
     */
    this.getLinkContext = () => linkContext;

    /**
     * @returns {$View}
     */
    this.get$View = function () {
        // viewContext['__$View'] 不可能为空, 为空说明异常
        return this.getLinkContext().getViewContext()['__$View'] || new $View(null);
    }
}

/**
 * 视图上下文
 * @constructor
 */
export function ViewContext(props) {

    /**
     * @type {{String: EditContext | DetailContext | SearchContext | TableContext}}
     */
    this.uidContextMaps = {} // 声明fullUid的上下文对象
    /**
     * 组合容器组件上下文对象列表
     * @type {{'prefix:uid': LinkContext}}
     */
    this.linkContextMaps = {}

    this.funMetasContext = new FuncMetaContext();

    /**
     * 功能点权限校验
     * @return {{default: boolean, type: BooleanConstructor}}
     */
    this.isAuth = function () {
        return props.urlAuth;
    }

    /**
     * 获取视图名称
     * @return {String}
     */
    this.getName = function () {
        return props.name;
    }

    /**
     * 返回功能的唯一key
     * @return {String}
     */
    this.getRowKey = function () {
        return props.rowKey;
    }

    /**
     * 获取元数的id获取对应上下文
     * @param fullUid ${prefix}:${uid}
     * @return {EditContext | TableContext | SearchContext | DetailContext}
     */
    this.getContextByUid = function (fullUid) {
        return this.uidContextMaps[fullUid];
    }

    /**
     *
     * @param prefix ULinkView组件uid
     * @param uid 其他组件uid
     * @return {EditContext|TableContext|SearchContext|DetailContext}
     */
    this.getContextByPrefixAndUid = function (prefix, uid) {
        return this.getContextByUid(prefix + ":" + uid);
    }

    /**
     * 增加上下文
     * @param fullUid = prefix:uid
     * @param context {EditContext|TableContext|SearchContext|DetailContext}
     */
    this.addContextByUid = function (fullUid, context) {
        if(fullUid && context) {
            if(!this.uidContextMaps[fullUid]) {
                this.uidContextMaps[fullUid] = context;
            } else {
                console.warn(`已经存在同名的组件[${fullUid}]`)
            }
        } else {
            console.error(`新增Context失败, 错误的参数[fullUid or context]`)
        }
    }

    /**
     * 移除上下文
     * @param fullUid = prefix:uid
     */
    this.removeContextByUid = function (fullUid) {
        if(fullUid) {
            if(this.uidContextMaps[fullUid]) {
                delete this.uidContextMaps[fullUid];
            }
        } else {
            console.error(`移除Context失败, 错误的参数[fullUid or context]`)
        }
    }

    /**
     * @param uid {String}
     * @param context {LinkContext}
     */
    this.addLinkContextByUid = function (uid, context) {
        if(!(context instanceof LinkContext)) {
            throw new Error(`参数错误[context]`)
        }

        if(this.linkContextMaps[uid]) {
            throw new Error(`uid为[${uid}]的ULinkView组件已经存在`)
        }

        this.linkContextMaps[uid] = context;
    }

    this.removeLinkContextByUid = function (uid, context) {
        if(!(context instanceof LinkContext)) {
            throw new Error(`参数错误[context]`)
        }

        let childContextMaps = context.childContextMaps || {};
        Object.keys(childContextMaps).forEach(fullUid => {
            this.removeContextByUid(fullUid)
        })

        delete this.linkContextMaps[uid];
    }
    /**
     * @param uid ULinkView#uid
     * @return LinkContext
     */
    this.getLinkContextByUid = function (uid) {
        return this.linkContextMaps[uid];
    }

    /**
     * 焦点视图栈: 弹框打开入栈/关闭移除, 栈顶=当前焦点视图(栈空=焦点是页面本身)
     * 入栈元素为弹框上下文(VisibleContext/EditContext), 由弹框基座(MixinsVisible/MixinsEditItem)watch visible驱动同步
     * 页面骨架据此裁剪非焦点弹窗(仅留占位行), 减少AI上下文体积
     * @type {Array<VisibleContext|EditContext>}
     */
    this.focusStack = [];
    /** 弹框打开: 入栈(已存在则先移除再入栈, 保证栈顶=最新打开的弹框) */
    this.pushFocus = function (context) {
        if(!context) return;
        const index = this.focusStack.indexOf(context);
        if(index > -1) {
            this.focusStack.splice(index, 1);
        }
        this.focusStack.push(context);
    }
    /** 弹框关闭/卸载: 从栈移除 */
    this.removeFocus = function (context) {
        if(!context) return;
        const index = this.focusStack.indexOf(context);
        if(index > -1) {
            this.focusStack.splice(index, 1);
        }
    }
    this.getFocusStack = function () {
        return this.focusStack;
    }
}

export function EventContext() {

    /**
     *
     * @type {*[Function]}
     */
    this.listeners = [];

    /**
     * 注册监听器
     * @param callback
     */
    this.register = function (callback) {
        this.listeners.push(callback);
    }

    /**
     * 执行监听
     * @param args
     */
    this.notify = function (args) {
        this.listeners.forEach(item => item(args))
    }
}

/**
 * 联动容器上下文
 * @param uid {String}
 * @param parent {LinkContext}
 * @param viewContext {ViewContext}
 * @constructor
 */
export function LinkContext(uid, viewContext, parent) {
    /**
     * 容器标识前缀, 此uid将作为子组件的前缀
     * @type {String}
     */
    this.uid = uid;

    /**
     * 父上下文
     * @type {LinkContext | ViewContext}
     */
    this.parent = parent;

    /**
     *  子组件的上下文对象
     * @type {{String: EditContext | TableContext | DetailContext | SearchContext}}
     */
    this.childContextMaps = {};

    /**
     * @type {{String: EventContext}}
     */
    this.events = {};

    /**
     * 监听事件
     * @param event
     * @param callback
     */
    this.on = (event, callback) => {
        if(!event || !callback) {
            throw new Error("监听事件失败[event or callback不能为null]");
        }

        /**
         * @type EventContext
         */
        let context = this.events[event];
        if(!(context instanceof EventContext)) {
            this.events[event] = context = new EventContext();
        }

        context.register(callback);
    }

    /**
     * 触发事件
     * @param event
     * @param args
     */
    this.emit = (event, args) => {
        /**
         * @type EventContext
         */
        let context = this.events[event];
        if(context instanceof EventContext) {
            context.notify(args);
        } else {
            console.warn(`没有创建对应的监听器[${event}]`)
        }
    }

    /**
     * @type {Array<FuncConfig>}
     */
    this.queryFuncs = [];

    /**
     * 注册功能: 按钮注册到组件上下文funcMetas(权威存储), 此处仅聚合查询按钮供挂载/翻页/弹框打开后触发查询
     * @param config {FuncConfig}
     */
    this.registerFunc = function (config) {
        let func = config.getFunc();
        let context = config.getContext();

        context.register(config);

        if(func == FuncNameMeta.QUERY) {
            this.queryFuncs.push(config);
        }
    }

    /**
     * 注销功能(UFuncBtn/UFuncTag卸载时调用): 仅移除本实例注册的条目
     * (条目已被其它实例覆盖注册时不动, 避免误删仍存活的功能按钮)
     * @param config {FuncConfig}
     */
    this.unregisterFunc = function (config) {
        let func = config.getFunc();
        let context = config.getContext();

        // 1. 组件上下文funcMetas: 委托上下文自身unregister对称移除(仅移除本实例注册的条目)
        if(context.unregister instanceof Function) {
            context.unregister(config);
        } else { // 兜底: 上下文未提供unregister时直接按实例身份移除(键含显式uid实例段, 与各上下文register口径一致)
            let meta = context.funcMetas;
            let key = funcRegisterKey(func, config);
            if(meta && meta[key] === config) {
                delete meta[key];
            }
        }
        // 2. 同步移除查询按钮列表中的该实例(LinkView挂载期触发查询用)
        let qIndex = this.queryFuncs.indexOf(config);
        if(qIndex > -1) {
            this.queryFuncs.splice(qIndex, 1);
        }
    }

    /**
     * 获取指定功能(操作目标匹配): 按需遍历本容器内组件上下文的funcMetas定位(不维护funcs登记表, 组件funcMetas为权威存储)
     * @param func 功能点
     * @param toUid 要操作的uid
     * @return {FuncConfig|null}
     */
    this.getFunc = function (func, toUid) {
        // func大小写归一(与各组件上下文getFunc的toUpperCase口径一致): 功能编码恒以大写比较
        func = func && String(func).toUpperCase();
        for(let context of Object.values(this.childContextMaps)) {
            if(!(context && context.funcMetas)) {
                continue;
            }
            for(let config of Object.values(context.funcMetas)) {
                if(func == config.getFunc() && config.getUid() == toUid) {
                    return config;
                }
            }
        }
        return null;
    }

    /**
     * @return {Array<FuncConfig>}
     */
    this.getQueryFunc = function () {
        return this.queryFuncs;
    }

    /**
     * 获取子组件的上下文对象
     * @param uid 子组件的uid(支持跨视图引用，如"UView:paramDrawer")
     * @return {EditContext | SearchContext | DetailContext | TableContext | VisibleContext}
     */
    this.getChildrenContext = function (uid) {
        if(!uid) return null;

        // 1. 先在本地LinkView内查找 (兼容现有行为)
        let context = this.childContextMaps[this.uid+":"+uid];
        if(context) return context;

        // 2. 本地未找到且uid包含冒号，视为fullUid在ViewContext全局查找 (跨视图引用)
        if(uid.indexOf(':') > -1) {
            return this.getViewContext().getContextByUid(uid);
        }

        return null;
    }

    /**
     * 增加子组件上下文对象
     * @param childContext {EditContext | SearchContext | DetailContext | TableContext | VisibleContext}
     */
    this.addChildrenContext = function (childContext) {
        if(!childContext.uid) {
            throw new Error(`组件不存在uid属性`)
        }

        let fullUid = this.uid + ":" + childContext.uid;
        if(!this.childContextMaps[fullUid]) {
            this.childContextMaps[fullUid] = childContext;
            this.getViewContext().addContextByUid(fullUid, childContext);
        } else {
            console.warn(`已经存在同名的组件[${fullUid}]`)
        }
    }

    this.removeChildrenContext = function (childContext) {
        if(childContext.uid) {
            let fullUid = this.uid + ":" + childContext.uid;
            if(this.childContextMaps[fullUid]) {
                delete this.childContextMaps[fullUid];
                this.getViewContext().removeContextByUid(fullUid);
            }
        }
    }

    /**
     * @return {ViewContext}
     */
    this.getViewContext = () => viewContext;

    viewContext.addLinkContextByUid(uid, this);
}

export function FuncConfig(props, context, funcPath) {
    /**
     * @type {Function}
     * @return {String}
     */
    this.title = () => props.title;

    /**
     * @type {Function}
     * @return {String}
     */
    this.getUrl = () => props.url;

    /**
     * 返回功能名称
     * @return {String}
     */
    this.getFunc = () => funcPath[0]

    /**
     * 返回要操作的uid
     * @return String
     */
    this.getUid = () => {
        if(!props['uid']) {
            let uid = CoreConsts.FuncOperationUid[this.getFunc()];
            //
            if(CoreConsts.SelfComponentConst == uid) {
                uid = this.getContext().uid;
            }

            return uid;
        } else {
            return props['uid'];
        }
    }

    /**
     * 返回子功能名称
     * @return {String}
     */
    this.getChildFunc = () => funcPath[1]

    /**
     * @param key
     * @return {Object}
     */
    this.getProp = (key) => props[key]

    /**
     * @return {string}
     */
    this.getMethod = () => props.method ? props.method.toUpperCase() : CoreConsts.FuncMethodMaps[funcPath[0]];
    /**
     * @return {EditContext | DetailContext | SearchContext | TableContext}
     */
    this.getContext = () => context;
    /**
     * 触发此功能
     */
    this.trigger = () => null

    /**
     * @param status {Boolean}
     */
    this.setLoading = status => null;
}
export function ChildConfig() {

    /**
     * @type {String | Array}
     */
    this.copy = null;

    /**
     * @type {String | Number | *}
     */
    this.pid = null;

    /**
     * 确认标题
     * @type {String}
     */
    this.confirmTitle = null;

    /**
     * 确认内容
     * @type {String}
     */
    this.confirmContent = null;
}

/**
 * @param vue vue实例uid
 * @constructor
 */
export function Config(vue) {

    /**
     * @type {Vue}
     */
    this.$vue = vue;

    /**
     * @type {Router}
     */
    this.router = null

    /**
     * @see LinkContext#uid
     * @type {String}
     */
    this.uid = null;
    /**
     * @see LinkContext#uid
     * @type {SearchContext | DetailContext | EditContext | TableContext}
     */
    this.context = null;
    /**
     * 功能地址
     * @type {String}
     */
    this.url = null;

    /**
     * 请求的参数
     * @type {Function | Object}
     * @param data
     * @return {Promise}
     */
    this.params = null;

    /**
     * 功能参数
     * @type {ChildConfig}
     */
    this.config = null;

    /**
     * 要操作的主功能
     * @type {String}
     */
    this.func = null;

    /**
     * 重新加载列表
     * @type {boolean}
     */
    this.reload = false

    /**
     * 功能路径
     * @type {*[]}
     */
    this.funcPath = null;

    /**
     * 要操作的数据
     * @type {Object | Array}
     */
    this.data = null;

    /**
     * ajax请求方法 get、post、...
     * @see CoreConsts#FuncMethodMaps
     * @type {String}
     */
    this.method = null;

    /**
     * 回调
     * @type {Function}
     * @return {Promise}
     */
    this.callback = null;

    /**
     * @type {FuncConfig}
     */
    this.funcConfig = null;

    /**
     *
     * @param uid
     * @param func
     * @param url
     * @param config
     * @param method
     * @param data
     * @param params
     * @param context {DetailContext | EditContext | TableContext | SearchContext | VisibleContext}
     * @param router {Router}
     * @param funcConfig {FuncConfig}
     * @return {Config}
     */
    this.build = function ({uid, func, url, config, method, data, params, reload, callback}, context, router, funcConfig) {
        this.url = url;
        this.uid = uid;
        this.data = data;
        this.router = router;
        this.config = config;
        this.reload = reload;
        this.params = params;
        this.context = context;
        this.funcConfig = funcConfig;
        this.callback = callback || VoidResolveCall
        this.funcPath = func.split(":")
            .map(item => item.toUpperCase());
        this.func = this.funcPath[0];
        this.method = method || CoreConsts.FuncMethodMaps[this.func];
        return this;
    }
}

/**
 * 普通类型的模态框或者抽屉上下文
 * @param linkContext
 * @constructor
 */
export function VisibleContext(linkContext) {
    // 用于关联各个组件(搜索、表格、详情)
    this.uid = null;
    // 弹框/抽屉标题(由UModal/UDrawer渲染期同步, 供页面骨架自描述)
    this.title = null;

    // 存储UFuncBtn和UFuncTag组件的信息
    this.funcMetas = {};

    /**
     * @type {Array<LinkContext>}
     */
    this.childLinkContext = [];

    // 获取功能组件配置: 按注册键(func[:uid])键直查, 与regFunc/funcRegisterKey组键口径一致
    this.getFunc = function (func, uid) {
        return this.funcMetas[funcQueryKey(func.toUpperCase(), uid)];
    }

    this.regFunc = function (func, config) {
        // 键=func(显式uid按钮追加实例段func:uid, 同func多按钮并存互不覆盖, 无uid保持原键与getFunc直查兼容)
        this.funcMetas[funcRegisterKey(func, config)] = config;
        linkContext.registerFunc(config);
    }

    /**
     * 注销功能(与regFunc对称): 仅当funcMetas中当前占用者是本实例时移除(条目已被其它实例覆盖注册时不动, 避免误删存活按钮)
     * @param config {FuncConfig}
     */
    this.unregister = function (config) {
        let key = funcRegisterKey(config.getFunc(), config);
        if(this.funcMetas[key] === config) {
            delete this.funcMetas[key];
        }
    }

    // 修改加载状态
    this.setLoading = (status, tip) => {Unmount()};
    // 修改弹框状态(模态框或者抽屉框)
    this.setVisible = (status) => {Unmount()};
    // 异步打开弹框(模态框或者抽屉框) 等表单初始化完成
    this.asyncVisible = (row) => Promise.reject("未挂载");

    /**
     * 关闭当前弹框(挂载后setVisible已被MixinsVisible替换为组件方法, 供close_dialog动作统一关闭)
     */
    this.cancel = function () {
        this.setVisible(false);
    }

    /**
     * @return {LinkContext}
     */
    this.getLinkContext = () => linkContext;

    /**
     * @returns {$View}
     */
    this.get$View = function () {
        // viewContext['__$View'] 不可能为空, 为空说明异常
        return this.getLinkContext().getViewContext()['__$View'] || new $View(null);
    }
}
