package net.smartworks.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.smartworks.factory.HvmManagerFactory;
import net.smartworks.manager.IHvmManager;
import net.smartworks.model.Filter;
import net.smartworks.model.PagingInfo;
import net.smartworks.model.SkkupssPssProject;
import net.smartworks.model.hvm.HvmAttribute;
import net.smartworks.model.hvm.HvmAttributeCond;
import net.smartworks.model.hvm.HvmProject;
import net.smartworks.model.hvm.HvmProjectCond;
import net.smartworks.security.Login;
import net.smartworks.util.HvmUtil;
import net.smartworks.util.id.IDCreator;

@Controller
public class HvmController {
	
	@RequestMapping(value="/getPagingInfo", method=RequestMethod.POST)
	public @ResponseBody List getPagingInfo(@RequestBody Map<String, Object> requestBody, HttpServletRequest request, HttpServletResponse response) throws Exception {

		int totalSize = (Integer)requestBody.get("totalSize");
		int currentPage = (Integer)requestBody.get("currentPage");
		int pageSize = (Integer)requestBody.get("pageSize");
		
		PagingInfo pi = new PagingInfo();
		
		pi.setBlockSize(10);
		pi.setPageSize(pageSize);
		pi.setPageNo(currentPage);
		pi.setTotalCount(totalSize);
		
		List<PagingInfo> list = new ArrayList<PagingInfo>();
		list.add(pi);
		
		return list;
	}
	
	@RequestMapping(value="/getSkupssProjects", method=RequestMethod.POST)
	public @ResponseBody List getSkupssProjects(@RequestBody Map<String, Object> requestBody, HttpServletRequest request, HttpServletResponse response) throws Exception {

		Login currentUser = HvmUtil.getCurrentUserInfo();
		
		IHvmManager hvmMgr = HvmManagerFactory.getInstance().getHvmManager();
		HvmProjectCond cond = new HvmProjectCond();
		
		String psId = (String)requestBody.get("psId");
		
		List<SkkupssPssProject> list = hvmMgr.getSkkupssPssProject(currentUser.getId(), psId);
		return list;
	}

	@RequestMapping(value="/getHvmEmptyAttribute", method=RequestMethod.POST)
	public @ResponseBody List getHvmEmptyAttribute(@RequestBody Map<String, Object> requestBody, HttpServletRequest request, HttpServletResponse response) throws Exception {
	
		List<HvmAttribute> attributeList = new ArrayList<HvmAttribute>();
		HvmAttribute attribute = new HvmAttribute();
		attribute.setId(IDCreator.createId("attr"));
		attributeList.add(attribute);
	
		return attributeList;
	}
	@RequestMapping(value="/getHvmEmptyProject", method=RequestMethod.POST)
	public @ResponseBody List getHvmEmptyProject(@RequestBody Map<String, Object> requestBody, HttpServletRequest request, HttpServletResponse response) throws Exception {

		List<HvmProject> projectList = new ArrayList<HvmProject>();
		HvmProject project = new HvmProject();
		project.setId(IDCreator.createId("prj"));
		
		List<HvmAttribute> attributeList = new ArrayList<HvmAttribute>();
//		HvmAttribute attribute = new HvmAttribute();
//		attribute.setId(IDCreator.createId("attr"));
//		attribute.setPrjId(project.getId());
//		attributeList.add(attribute);
//
		project.setAttributes(attributeList);
		projectList.add(project);
		
		return projectList;
	}

	@RequestMapping(value="/getHvmProjectSize", method=RequestMethod.POST)
	public @ResponseBody Map getHvmProjectSize(@RequestBody Map<String, Object> requestBody, HttpServletRequest request, HttpServletResponse response) throws Exception {
	
		Login currentUser = HvmUtil.getCurrentUserInfo();
		
		IHvmManager hvmMgr = HvmManagerFactory.getInstance().getHvmManager();
		HvmProjectCond cond = new HvmProjectCond();
		String pssPrjId = (String)requestBody.get("pssPrjId");
		if (pssPrjId != null && pssPrjId.length() != 0) {
			cond.setPssPrjId(pssPrjId);
		}
		
		List<Map<String, String>> filtersList = (List<Map<String, String>>)requestBody.get("filters");
		if (filtersList != null && filtersList.size() != 0) {
			List<Filter> filters = new ArrayList<Filter>();
			for (int i = 0; i < filtersList.size(); i++) {
				Map filterMap = filtersList.get(i);
				String left = (String)filterMap.get("left");
				String operator = (String)filterMap.get("operator");
				String right = (String)filterMap.get("right");

				if (left == null || operator == null || right == null)
					continue;
				
				Filter filter = new Filter(left, operator, right);
				filters.add(filter);
			}
			if (filters.size() != 0)
				cond.setFilters(filters);
		}
		
		
		Map result = hvmMgr.getHvmProjectSize(currentUser.getId(), cond);
		return result;
	}
	@RequestMapping(value="/getHvmProjects", method=RequestMethod.POST)
	public @ResponseBody List getHvmProjects(@RequestBody Map<String, Object> requestBody, HttpServletRequest request, HttpServletResponse response) throws Exception {

		Login currentUser = HvmUtil.getCurrentUserInfo();
		
		IHvmManager hvmMgr = HvmManagerFactory.getInstance().getHvmManager();
		HvmProjectCond cond = new HvmProjectCond();
		String viewType = (String)requestBody.get("viewType");
		//String searchKey = (String)requestBody.get("searchKey");
		int pageSize = (Integer)requestBody.get("pageSize");
		int pageNo = (Integer)requestBody.get("pageNo");
		String psId = (String)requestBody.get("psId");
//		if (searchKey != null && searchKey.length() != 0) {
//			cond.setSearchKey(searchKey);
//		}
		
		
		List<Map<String, String>> filtersList = (List<Map<String, String>>)requestBody.get("filters");
		if (filtersList != null && filtersList.size() != 0) {
			List<Filter> filters = new ArrayList<Filter>();
			for (int i = 0; i < filtersList.size(); i++) {
				Map filterMap = filtersList.get(i);
				String left = (String)filterMap.get("left");
				String operator = (String)filterMap.get("operator");
				String right = (String)filterMap.get("right");
				
				if (left == null || operator == null || right == null)
					continue;
				
				Filter filter = new Filter(left, operator, right);
				filters.add(filter);
			}
			if (filters.size() != 0)
				cond.setFilters(filters);
		}
		
		
		if (psId != null && psId.length() != 0) {
			cond.setPssPrjId(psId);
		}
		cond.setPageSize(pageSize);
		cond.setPageNo(pageNo);
		
		List<HvmProject> list = hvmMgr.getHvmProjects(currentUser.getId(), cond, null);
		
		return list;
	}
	
	@RequestMapping(value="/setHvmProject", method=RequestMethod.POST)
	public @ResponseBody void setHvmProject(@RequestBody Map<String, Object> requestBody, HttpServletRequest request, HttpServletResponse response) throws Exception {

		Login currentUser = HvmUtil.getCurrentUserInfo();
		
		IHvmManager hvmMgr = HvmManagerFactory.getInstance().getHvmManager();
		Map resultMap = (Map)requestBody.get("result");
		//String setMode = (String)requestBody.get("setMode");

		ObjectMapper mapper = new ObjectMapper();
		HvmProject project = mapper.convertValue(resultMap, HvmProject.class);
		
		hvmMgr.setHvmProject(currentUser.getId(), project);
	}	
	
	@RequestMapping(value="/removeHvmProject", method=RequestMethod.POST)
	public @ResponseBody void removeHvmProject(@RequestBody Map<String, Object> requestBody, HttpServletRequest request, HttpServletResponse response) throws Exception {

		Login currentUser = HvmUtil.getCurrentUserInfo();

		IHvmManager hvmMgr = HvmManagerFactory.getInstance().getHvmManager();
		String projectId = (String)requestBody.get("projectId");
		
		//String setMode = (String)requestBody.get("setMode");

		hvmMgr.removeHvmProject(currentUser.getId(), projectId);
	}	
	
	
	
	
	
	
	
	
	
	
	@RequestMapping(value="/setHvmAttributeWithProject", method=RequestMethod.POST)
	public @ResponseBody void setHvmAttribute(@RequestBody Map<String, Object> requestBody, HttpServletRequest request, HttpServletResponse response) throws Exception {

		Login currentUser = HvmUtil.getCurrentUserInfo();
		
		IHvmManager hvmMgr = HvmManagerFactory.getInstance().getHvmManager();
		Map resultMap = (Map)requestBody.get("result");
		int attrIndex = (Integer)requestBody.get("attrIndex");
		//String setMode = (String)requestBody.get("setMode");

		ObjectMapper mapper = new ObjectMapper();
		HvmProject project = mapper.convertValue(resultMap, HvmProject.class);
		
		hvmMgr.setHvmAttributeWithProject(currentUser.getId(), project, attrIndex);
	}	
	
	@RequestMapping(value="/removeHvmAttribute", method=RequestMethod.POST)
	public @ResponseBody void removeHvmAttribute(@RequestBody Map<String, Object> requestBody, HttpServletRequest request, HttpServletResponse response) throws Exception {

		Login currentUser = HvmUtil.getCurrentUserInfo();

		IHvmManager hvmMgr = HvmManagerFactory.getInstance().getHvmManager();
		String attributeId = (String)requestBody.get("attributeId");
		
		hvmMgr.removeHvmAttribute(currentUser.getId(), attributeId);
	}	
	
	
	
	
	
	
	
	
	
	
	
	@RequestMapping(value="/getHvmAttributeSize", method=RequestMethod.POST)
	public @ResponseBody Map getHvmAttributeSize(@RequestBody Map<String, Object> requestBody, HttpServletRequest request, HttpServletResponse response) throws Exception {
	
		Login currentUser = HvmUtil.getCurrentUserInfo();
		
		IHvmManager hvmMgr = HvmManagerFactory.getInstance().getHvmManager();
		HvmAttributeCond cond = new HvmAttributeCond();
//		String searchKey = (String)requestBody.get("searchKey");
//		if (searchKey != null && searchKey.length() != 0) {
//			cond.setSearchKey(searchKey);
//		}
		
		List<Map<String, String>> filtersList = (List<Map<String, String>>)requestBody.get("filters");
		if (filtersList != null && filtersList.size() != 0) {
			List<Filter> filters = new ArrayList<Filter>();
			for (int i = 0; i < filtersList.size(); i++) {
				Map filterMap = filtersList.get(i);
				String left = (String)filterMap.get("left");
				String operator = (String)filterMap.get("operator");
				String right = (String)filterMap.get("right");

				if (left == null || operator == null || right == null)
					continue;
				
				Filter filter = new Filter(left, operator, right);
				filters.add(filter);
			}
			if (filters.size() != 0)
				cond.setFilters(filters);
		}
		
		Map result = hvmMgr.getHvmAttributeSize(currentUser.getId(), cond);
		return result;
	}
	
	@RequestMapping(value="/getHvmAttributes", method=RequestMethod.POST)
	public @ResponseBody List getHvmAttributes(@RequestBody Map<String, Object> requestBody, HttpServletRequest request, HttpServletResponse response) throws Exception {

		Login currentUser = HvmUtil.getCurrentUserInfo();
		
		IHvmManager hvmMgr = HvmManagerFactory.getInstance().getHvmManager();
		HvmAttributeCond cond = new HvmAttributeCond();
		String viewType = (String)requestBody.get("viewType");
		//String searchKey = (String)requestBody.get("searchKey");
		int pageSize = (Integer)requestBody.get("pageSize");
		int pageNo = (Integer)requestBody.get("pageNo");
		
		String orderColumn = (String)requestBody.get("orderColumn");
		boolean isDescending = false;
		if (requestBody.get("isDescending") != null) {
			isDescending = (Boolean)requestBody.get("isDescending");
		}
		
//		if (searchKey != null && searchKey.length() != 0) {
//			cond.setSearchKey(searchKey);
//		}
		cond.setPageSize(pageSize);
		cond.setPageNo(pageNo);
		
		cond.setOrderColumn(orderColumn);
		cond.setDescending(isDescending);
		
		
		List<Map<String, String>> filtersList = (List<Map<String, String>>)requestBody.get("filters");
		if (filtersList != null && filtersList.size() != 0) {
			List<Filter> filters = new ArrayList<Filter>();
			for (int i = 0; i < filtersList.size(); i++) {
				Map filterMap = filtersList.get(i);
				String left = (String)filterMap.get("left");
				String operator = (String)filterMap.get("operator");
				String right = (String)filterMap.get("right");

				if (left == null || operator == null || right == null)
					continue;
				
				Filter filter = new Filter(left, operator, right);
				filters.add(filter);
			}
			if (filters.size() != 0)
				cond.setFilters(filters);
		}		
		
		
		List<HvmAttribute> list = hvmMgr.getHvmAttributes(currentUser.getId(), cond);
		
		return list;
	}
	
	@RequestMapping(value = "/getCurrentUser", method = RequestMethod.GET)
    public @ResponseBody Map getCurrentUser(HttpSession session) {
        Login userDetails = (Login)SecurityContextHolder.getContext().getAuthentication().getDetails();
         
		//getUserDetailInfo and set Session

		Map resultMap = new HashMap();
		resultMap.put("id", userDetails.getId());
		resultMap.put("picture", userDetails.getPicture());
		resultMap.put("username", userDetails.getUsername());
		return resultMap;
    }
	@RequestMapping(value = "login_success", method = RequestMethod.GET)
    public void login_success(HttpSession session) {
        //CustomUserDetails userDetails = (CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getDetails();
         
		//getUserDetailInfo and set Session
		System.out.println("LOGIN_SUCCESS !!@@!!");
        session.setAttribute("userLoginInfo", "");
    }
	@RequestMapping(value = { "/login" }, method = RequestMethod.GET)
	public ModelAndView homePage() {
		System.out.println("Request LOGIN PAGE !!@@!!");
		ModelAndView model = new ModelAndView();
		model.setViewName("login");
		return model;
	}
	
}
