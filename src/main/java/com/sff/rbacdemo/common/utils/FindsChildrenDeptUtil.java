package com.sff.rbacdemo.common.utils;

import com.sff.rbacdemo.system_old.dto.SimpleDepartTreeDTO;
import com.sff.rbacdemo.system_old.dto.SysDepartTreeDTO;
import com.sff.rbacdemo.system_old.entity.SysDepart;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 对应部门的表,处理并查找树级数据
 * <p>
 */
public class FindsChildrenDeptUtil {


    /**
     * queryTreeList的子方法 ====1=====
     * 该方法是s将SysDepart类型的list集合转换成SysDepartTreeDTO类型的集合
     */
    public static List<SysDepartTreeDTO> wrapTreeDataToTreeList(List<SysDepart> recordList) {
        // 在该方法每请求一次,都要对全局list集合进行一次清理
        //idList.clear();
        List<SimpleDepartTreeDTO> idList = new ArrayList<SimpleDepartTreeDTO>();
        List<SysDepartTreeDTO> records = new ArrayList<>();
        for (int i = 0; i < recordList.size(); i++) {
            SysDepart depart = recordList.get(i);
            records.add(new SysDepartTreeDTO(depart));
        }
        List<SysDepartTreeDTO> tree = findChildren(records, idList);
        setEmptyChildrenAsNull(tree);
        return tree;
    }

    /**
     * 获取 SimpleDepartTreeDTO
     *
     * @param recordList
     * @return
     */
    public static List<SimpleDepartTreeDTO> wrapTreeDataToDepartIdTreeList(List<SysDepart> recordList) {
        // 在该方法每请求一次,都要对全局list集合进行一次清理
        //idList.clear();
        List<SimpleDepartTreeDTO> idList = new ArrayList<SimpleDepartTreeDTO>();
        List<SysDepartTreeDTO> records = new ArrayList<>();
        for (int i = 0; i < recordList.size(); i++) {
            SysDepart depart = recordList.get(i);
            records.add(new SysDepartTreeDTO(depart));
        }
        findChildren(records, idList);
        return idList;
    }

    /**
     * queryTreeList的子方法 ====2=====
     * 该方法是找到并封装顶级父类的节点到TreeList集合
     */
    private static List<SysDepartTreeDTO> findChildren(List<SysDepartTreeDTO> recordList,
                                                       List<SimpleDepartTreeDTO> departIdList) {

        List<SysDepartTreeDTO> treeList = new ArrayList<>();
        for (int i = 0; i < recordList.size(); i++) {
            SysDepartTreeDTO branch = recordList.get(i);
            if (ObjectConvertUtils.isEmpty(branch.getParentId())) {
                treeList.add(branch);
                SimpleDepartTreeDTO SimpleDepartTreeDTO = new SimpleDepartTreeDTO().convert(branch);
                departIdList.add(SimpleDepartTreeDTO);
            }
        }
        getGrandChildren(treeList, recordList, departIdList);

        //idList = departIdList;
        return treeList;
    }

    /**
     * queryTreeList的子方法====3====
     * 该方法是找到顶级父类下的所有子节点集合并封装到TreeList集合
     */
    private static void getGrandChildren(List<SysDepartTreeDTO> treeList, List<SysDepartTreeDTO> recordList, List<SimpleDepartTreeDTO> idList) {

        for (int i = 0; i < treeList.size(); i++) {
            SysDepartTreeDTO model = treeList.get(i);
            SimpleDepartTreeDTO idModel = idList.get(i);
            for (int i1 = 0; i1 < recordList.size(); i1++) {
                SysDepartTreeDTO m = recordList.get(i1);
                if (m.getParentId() != null && m.getParentId().equals(model.getId())) {
                    model.getChildren().add(m);
                    SimpleDepartTreeDTO dim = new SimpleDepartTreeDTO().convert(m);
                    idModel.getChildren().add(dim);
                }
            }
            getGrandChildren(treeList.get(i).getChildren(), recordList, idList.get(i).getChildren());
        }

    }


    /**
     * queryTreeList的子方法 ====4====
     * 该方法是将子节点为空的List集合设置为Null值
     */
    private static void setEmptyChildrenAsNull(List<SysDepartTreeDTO> treeList) {
        for (int i = 0; i < treeList.size(); i++) {
            SysDepartTreeDTO model = treeList.get(i);
            if (model.getChildren().size() == 0) {
                model.setChildren(null);
                model.setIsLeaf(true);
            } else {
                setEmptyChildrenAsNull(model.getChildren());
                model.setIsLeaf(false);
            }
        }
    }
}
