package com.sff.rbacdemo.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sff.rbacdemo.system.entity.RoleResource;

public interface RoleResourceMapper extends BaseMapper<RoleResource> {

    /**
     * 根据角色ID删除角色资源
     * @param roleId
     * @return
     */
    public int deleteByRoleId(Long roleId);

    /**
     * 根据资源ID删除角色资源
     * @param resourceId
     * @return
     */
    public int deleteByResourceId(Long resourceId);

}