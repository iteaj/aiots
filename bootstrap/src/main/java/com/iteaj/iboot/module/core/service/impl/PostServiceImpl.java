package com.iteaj.iboot.module.core.service.impl;

import com.iteaj.iboot.module.core.entity.Post;
import com.iteaj.iboot.module.core.mapper.PostMapper;
import com.iteaj.iboot.module.core.service.IPostService;
import com.iteaj.framework.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 岗位管理 服务实现类
 * </p>
 *
 * @author iteaj
 * @since 2023-09-04
 */
@Service
public class PostServiceImpl extends BaseServiceImpl<PostMapper, Post> implements IPostService {

}
