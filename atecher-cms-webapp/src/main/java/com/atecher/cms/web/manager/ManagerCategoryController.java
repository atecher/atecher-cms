package com.atecher.cms.web.manager;

import com.atecher.cms.common.model.Page;
import com.atecher.cms.common.service.IGenericService;
import com.atecher.cms.model.manager.Category;
import com.atecher.cms.web.common.GenericActionController;
import com.atecher.cms.web.util.Constants;
import com.atecher.cms.web.util.Message;
import com.atecher.cms.web.util.ResponseResult;
import com.atecher.cms.web.util.WebForwardConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
/**
 * 描述：分类信息管理控制类
 */
@Controller
@RequestMapping(value = "/manage/category")
public class ManagerCategoryController extends GenericActionController{
	@Autowired
	private IGenericService genericService;
	/**
	 * 描述：查询分类信息
	 * @作者 mark.han
	 * @日期 2014-7-29
	 * @邮箱 hongwei.han@qq.com
	 * @return
	 */
	@RequestMapping(method=RequestMethod.GET)
	public String index() {
		return WebForwardConstants.MANAGER_CATEGORY_LIST;
	}
	
	@RequestMapping(value="/data",method=RequestMethod.POST)
	@ResponseBody
	public Page<Category> data(HttpServletRequest request) throws ParseException, IOException {
		int index = request.getParameter("offset") == null ? 0 : Integer.parseInt(request.getParameter("offset"));
		int limit = request.getParameter("limit") == null ? 10 : Integer.parseInt(request.getParameter("limit"));
		int pageNo=index/limit+1;
		HashMap<String, Object> params= new HashMap<>();
		String sort=request.getParameter("sort");
		if(StringUtils.isNotEmpty(sort)){
			params.put("sort", sort);
			params.put("order", request.getParameter("order"));
		}
		params.put("search", request.getParameter("search"));
		return genericService.selectForPage("com.atecher.cms.mapper.manager.CategoryMapper.selectCategoryForPage", pageNo, limit, params);
	}
	
	@RequestMapping(value = "/add")
	public String addCategory(){
		return WebForwardConstants.MANAGER_CATEGORY_EDIT;
	}
	/**
	 * 描述：编辑分类入口
	 * @param category_id
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/edit/{category_id}",method = RequestMethod.GET)
	public String editCategory(@PathVariable("category_id") Integer category_id,Model model){
		if(category_id!=null){
			Category category=genericService.getOne("com.atecher.cms.mapper.manager.CategoryMapper.getCategory", category_id);
			Category parent=genericService.getOne("com.atecher.cms.mapper.manager.CategoryMapper.getCategory", category.getParent_id());
			model.addAttribute("category",category);
			model.addAttribute("parent",parent);
		}
		return WebForwardConstants.MANAGER_CATEGORY_EDIT;
	}
	/**
	 * 描述：保存分类信息
	 * @作者 mark.han
	 * @日期 2014-7-29
	 * @邮箱 hongwei.han@qq.com
	 * @param category
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/save",method = RequestMethod.POST)
	public String saveCategory(@ModelAttribute("category") Category category,Model model) throws Exception{
		if(category==null){
			category=new Category();
		}
		if((Integer)genericService.getOne("com.atecher.cms.mapper.manager.CategoryMapper.checkCategoryPath", category)>0){
			model.addAttribute(Constants.BIZ_MESS, Message.WARNING("错误提示：访问路径已经存在！"));
			model.addAttribute("category",category);
			return WebForwardConstants.MANAGER_CATEGORY_EDIT;
		}
		Category parent=genericService.getOne("com.atecher.cms.mapper.manager.CategoryMapper.getCategory", category.getParent_id());
		if(parent!=null){
			category.setCategory_level(parent.getCategory_level()+1);
		}else{
			category.setCategory_level(1);
		}
		
		if(category.getCategory_id()!=null){
			genericService.update("com.atecher.cms.mapper.manager.CategoryMapper.updateCategory", category);
		}else{
			genericService.insert("com.atecher.cms.mapper.manager.CategoryMapper.insertCategory", category);
		}
		model.addAttribute(Constants.BIZ_MESS, Message.SUCCESS("成功提示：保存成功！"));
		return editCategory(category.getCategory_id(), model);
	}
	@RequestMapping(value = "/disable/{category_id}",method = RequestMethod.POST)
	@ResponseBody
	public ResponseResult disableCategory(@PathVariable("category_id") Integer category_id){
		int count=genericService.update("com.atecher.cms.mapper.manager.CategoryMapper.disableCategory",category_id);
		if(count>=1){
			return new ResponseResult("success");
		}else{
			return new ResponseResult("fail");
		}

	}
	
}
