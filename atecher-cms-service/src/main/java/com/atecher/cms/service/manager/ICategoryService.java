package com.atecher.cms.service.manager;

import com.atecher.cms.common.model.Page;
import com.atecher.cms.common.model.TreeNode;
import com.atecher.cms.model.manager.Category;

import java.util.List;
import java.util.Map;

/**
 * Created by hanhongwei on 2017/10/11.
 */
public interface ICategoryService {

    List<Category> queryCategoryList();

    Page<Category> selectCategoryForPage(int pageNo,int limit,Map<String, Object> param);

    List<TreeNode> selectCategoryByParent(Map<String, Object> param);

    Category getCategory(Integer categoryId);

    int disabledCategory(Integer categoryId);

    int checkCategoryPath(Category category);

    int updateCategory(Category category);

    void insertCategory(Category category);

}
