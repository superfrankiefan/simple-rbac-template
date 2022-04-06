package com.sff.rbacdemo.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sff.rbacdemo.common.model.TreeModel;
import com.sff.rbacdemo.common.properties.GlobalConstant;
import com.sff.rbacdemo.system.entity.Resource;
import com.sff.rbacdemo.system.mapper.ResourceMapper;
import com.sff.rbacdemo.system.service.ResourceService;
import com.sff.rbacdemo.system.service.RoleResourceServie;
import com.sff.rbacdemo.common.utils.TreeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.*;

@Service("resourceService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class ResourceServiceImpl extends ServiceImpl<ResourceMapper, Resource> implements ResourceService {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ResourceMapper resourceMapper;

    @Autowired
    private RoleResourceServie roleResourceService;

    @Autowired
    private WebApplicationContext applicationContext;

    @Override
    public List<Resource> findUserPermissions(String userName) {
        return this.resourceMapper.findUserPermissions(userName);
    }

    @Override
    public List<Resource> findUserResources(String userName) {
        return this.resourceMapper.findUserResources(userName);
    }

    @Override
    public List<Resource> findAllResources(Resource resource) {
        try {
            return this.resourceMapper.findAll();
        } catch (NumberFormatException e) {
            log.error("error", e);
            return new ArrayList<>();
        }
    }

    @Override
    public TreeModel<Resource> getResourceButtonTree() {
        List<TreeModel<Resource>> trees = new ArrayList<>();
        List<Resource> resources = this.findAllResources(new Resource());
        buildTrees(trees, resources);
        return TreeUtils.build(trees);
    }

    @Override
    public List<TreeModel<Resource>> getResourceTree(String manuName, int status) {
        QueryWrapper queryWrapper = new QueryWrapper();
        if(manuName != null && !manuName.isEmpty()){
            queryWrapper.like("RESOURCE_NAME", manuName);
        }
        queryWrapper.eq("STATUS", status);
        List<Resource> resources = this.resourceMapper.selectList(queryWrapper);
        List<TreeModel<Resource>> trees = new ArrayList<>();
        buildTrees(trees, resources);
        return TreeUtils.buildList(trees, GlobalConstant.ROOT_ID);
    }

    private void buildTrees(List<TreeModel<Resource>> trees, List<Resource> resources) {
        resources.forEach(resource -> {
            TreeModel<Resource> tree = new TreeModel<>();
            tree.setId(resource.getResourceId().toString());
            tree.setParentId(resource.getParentId().toString());
            tree.setStatus(resource.getResourceStatus());
            tree.setUrl(resource.getPath());
            tree.setPerms(resource.getPerms());
            tree.setOrderNo(resource.getOrderNo());
            tree.setIcon(resource.getIcon());
            tree.setText(resource.getResourceName());
            tree.setCreateTime(resource.getCreateTime().toString());
            trees.add(tree);
        });
    }

    @Override
    public TreeModel<Resource> getUserResource(String userName) {
        List<TreeModel<Resource>> trees = new ArrayList<>();
        List<Resource> resources = this.findUserResources(userName);
        resources.forEach(resource -> {
            TreeModel<Resource> tree = new TreeModel<>();
            tree.setId(resource.getResourceId().toString());
            tree.setParentId(resource.getParentId().toString());
            tree.setText(resource.getResourceName());
            tree.setIcon(resource.getIcon());
            tree.setUrl(resource.getPath());
            trees.add(tree);
        });
        return TreeUtils.build(trees);
    }

    @Override
    public Resource findByNameAndType(String resourceName, String type) {
        QueryWrapper<Resource> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("lower(resource_name)", resourceName);
        queryWrapper.eq("type", Long.valueOf(type));
        List<Resource> list = this.resourceMapper.selectList(queryWrapper);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    @Transactional
    public void addResource(Resource resource) {
        resource.setCreateTime(new Date());
        if (resource.getParentId() == null)
            resource.setParentId(0L);
        if (GlobalConstant.RES_TYPE_BTN == resource.getType()) {
            resource.setPath(null);
            resource.setIcon(null);
        }
        this.save(resource);
    }

    @Override
    @Transactional
    public void deleteMeuns(String resourceIds) {
        List<String> list = Arrays.asList(resourceIds.split(","));
        this.resourceMapper.deleteBatchIds(list);
        this.roleResourceService.deleteRoleResourcesByResourceIds(resourceIds);
        this.resourceMapper.changeToTop(list);
    }

    @Override
    public List<Map<String, String>> getAllUrl(String p1) {
        RequestMappingHandlerMapping mapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        //获取 url与类和方法的对应信息
        Map<RequestMappingInfo, HandlerMethod> map = mapping.getHandlerMethods();
        List<Map<String, String>> urlList = new ArrayList<>();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : map.entrySet()) {
            RequestMappingInfo info = entry.getKey();
            HandlerMethod handlerMethod = map.get(info);
            RequiresPermissions permissions = handlerMethod.getMethodAnnotation(RequiresPermissions.class);
            String perms = "";
            if (null != permissions) {
                perms = StringUtils.join(permissions.value(), ",");
            }
            Set<String> patterns = info.getPatternsCondition().getPatterns();
            for (String url : patterns) {
                Map<String, String> urlMap = new HashMap<>();
                urlMap.put("url", url.replaceFirst("\\/", ""));
                urlMap.put("perms", perms);
                urlList.add(urlMap);
            }
        }
        return urlList;

    }

    @Override
    public Resource findById(Long resourceId) {

        return this.resourceMapper.selectById(resourceId);
    }

    @Override
    @Transactional
    public void updateResource(Resource resource) {
        resource.setUpdateTime(new Date());
        if (resource.getParentId() == null)
            resource.setParentId(0L);
        if (GlobalConstant.RES_TYPE_BTN == resource.getType()) {
            resource.setPath(null);
            resource.setIcon(null);
        }
        this.resourceMapper.updateById(resource);
    }

}
