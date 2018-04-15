package me.codetalk.apps.xiaoma.user.service.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.codetalk.Constants;
import me.codetalk.apps.xiaoma.service.AbstractAppService;
import me.codetalk.apps.xiaoma.user.cache.UserProjectCache;
import me.codetalk.apps.xiaoma.user.entity.UserProject;
import me.codetalk.apps.xiaoma.user.mapper.UserProjectMapper;
import me.codetalk.apps.xiaoma.user.service.UserProjectService;
import me.codetalk.auth.aop.ThreadSession;
import me.codetalk.auth.entity.Session;
import me.codetalk.util.JsonUtils;
import me.codetalk.util.MapUtils;

@Service("userProjectService")
public class UserProjectServiceImpl extends AbstractAppService implements UserProjectService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserProjectServiceImpl.class);
	
	@Autowired
	private UserProjectMapper userProjectMapper;
	
	@Autowired
	private UserProjectCache userProjectCache;
	
	@Override
	public List<UserProject> listUserProject(Long userId) {
		List<UserProject> projects = userProjectCache.getUserProjects(userId);
		if(projects == null) {
			projects = userProjectMapper.selectUserProjects(userId);
			
			userProjectCache.setUserProjects(userId, projects);
		}

		return projects;
	}
	
	@Override
	@Transactional
	public Long createUserProject(UserProject project) {
		userProjectMapper.insertUserProject(project);
		
		userProjectCache.deleteUserProjects(project.getUserId());
		
		return project.getId();
	}

	@Override
	@Transactional
	public void updateUserProject(UserProject project) {
		userProjectMapper.updateUserProject(project);
		
		userProjectCache.deleteUserProjects(project.getUserId());
	}
	
	@Override
	@Transactional
	public void deleteUserProject(Long id) {
		userProjectMapper.deleteUserProject(id);
		
		userProjectCache.deleteUserProjects(threadSession.getUserId());
	}
	
	/******************************* Service *******************************/
	static final String URI_PROJECT_LIST = "/xiaoma/api/user/project/list";
	static final String URI_PROJECT_CREATE = "/xiaoma/api/user/project/create";
	static final String URI_PROJECT_UPDATE = "/xiaoma/api/user/project/update";
	static final String URI_PROJECT_DELETE = "/xiaoma/api/user/project/delete";
	
	@Override
	public String doService(String uri, Map<String, Object> params) {
		LOGGER.info("In doService... Params = " + params);
		
		if(!threadSession.authorized()) {
			return errorWithCodeKey(Constants.CODE_ERROR_RELOGIN, "auth_err_session_notfound");
		}
		
		if(URI_PROJECT_LIST.equals(uri)) {
			return projectList(params);
		} else if(URI_PROJECT_CREATE.equals(uri)) {
			return projectCreate(params);
		} else if(URI_PROJECT_UPDATE.equals(uri)) {
			return projectUpdate(params);
		} else if(URI_PROJECT_DELETE.equals(uri)) {
			return projectDelete(params);
		}
		
		return null;
	}

	private String projectList(Map<String, Object> params) {
		LOGGER.info("In projectList... Params = " + params);
		
		Object userIdGetObj = params.get("user_id_get");
		List<UserProject> projects = this.listUserProject(userIdGetObj == null ? threadSession.getUserId() : Long.parseLong(userIdGetObj.toString()));
		
		return successWithData(projects);
	}

	private String projectCreate(Map<String, Object> params) {
		LOGGER.info("In projectCreate... Params = " + params);
		
		UserProject project = JsonUtils.readMap(params, UserProject.class);
//		Long currentMillis = System.currentTimeMillis();
//		project.setCreateDate(currentMillis);
//		project.setLastUpdate(currentMillis);
		Long projectId = createUserProject(project);
		
		return successWithData(MapUtils.toMap("user_project_id", projectId));
	}

	private String projectUpdate(Map<String, Object> params) {
		LOGGER.info("In projectCreate... Params = " + params);
		
		UserProject project = JsonUtils.readMap(params, UserProject.class);
//		project.setLastUpdate(System.currentTimeMillis());
		updateUserProject(project);
		
		return Constants.RESPONSE_SUCCESS;
	}

	private String projectDelete(Map<String, Object> params) {
		LOGGER.info("In projectDelete... Params = " + params);
		
		Long userProjectId = Long.parseLong(params.get("user_project_id").toString());
		deleteUserProject(userProjectId);
		
		return Constants.RESPONSE_SUCCESS;
	}

	

	


	

}

