package com.iteaj.iboot.module.iot.le5le;

import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.Result;
import com.iteaj.iboot.module.iot.entity.Dataset;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 乐吾乐属性绑定接口
 */
@RestController
@AllArgsConstructor
@RequestMapping("/iot/le5le")
public class Le5LeAttrController extends BaseController {

    private final Le5LeService le5LeService;

    @GetMapping("attr")
    @CrossOrigin(origins = "*")
    public List<Dataset> attr(Long groupId) {
        return le5LeService.listDatasetByDeviceGroupId(groupId);
    }
}
