
const $http = axios.create({
    timeout: 20000,
    baseURL: 'http://localhost:8080',
    headers: {
        ApiForm: 'Ivzone',
        Authorization: 'session', // 授权类型为 session
        'x-requested-with': 'XMLHttpRequest' // 是否是ajax请求
    },
    // paramsSerializer: (params) => {
    //     return Qs.stringify(params, {arrayFormat: 'indices', allowDots: true})
    // }
})

$http.interceptors.request.use(
    config => {
        return config
    },
    error => { // 请求错误处理
        Promise.reject(error)
    }
)

$http.interceptors.response.use(
    response => { // 成功请求到数据
        // 这里根据后端提供的数据进行对应的处理
        let data = response.data
        if (data.code === 500) {
            return Promise.reject(data.message)
        } else if (data.code === 200) {
            return data
        } else {
            return Promise.reject(`糟糕, 未知错误！`)
        }
    },
    error => { // 响应错误处理
        let errMsg = error;
        if (error.code === 'ECONNABORTED') {
            errMsg = '访问超时,请检查网络'
        } else {
            let response = error.response;
            if(!response) {
                errMsg = "请求失败";
            } else if (response.status === 401) {
                let data = response.data;
                let loginUrl = data.message;
                window.parent.location.href = loginUrl;
            } else if (response.status === 404) {
                errMsg = '您请求的功能不存在!';
            } else if(response.status === 403) {
                errMsg = '您没有权限访问此功能!';
            }
        }

        return Promise.reject(errMsg)
    }
)
