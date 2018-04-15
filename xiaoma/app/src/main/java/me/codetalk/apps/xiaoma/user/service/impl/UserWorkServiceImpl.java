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
import me.codetalk.apps.xiaoma.user.cache.UserWorkCache;
import me.codetalk.apps.xiaoma.user.entity.UserProject;
import me.codetalk.apps.xiaoma.user.entity.UserWork;
import me.codetalk.apps.xiaoma.user.mapper.UserWorkMapper;
import me.codetalk.apps.xiaoma.user.service.UserWorkService;
import me.codetalk.auth.entity.Session;
import me.codetalk.util.JsonUtils;
import me.codetalk.util.MapUtils;

@Service("userWorkService")
public class UserWorkServiceImpl extends AbstractAppService implements UserWorkService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserWorkServiceImpl.class);
	
	@Autowired
	private UserWorkMapper userWorkMapper;
	
	@Autowired
	private UserWorkCache userWorkCache;
	
	@Override
	public List<UserWork> listUserWork(Long userId) {
		List<UserWork> works = userWorkCache.getUserWorks(userId);
		if(works == null) {
			works = userWorkMapper.selectUserWorks(userId);
			
			userWorkCache.setUserWorks(userId, works);
		}

		return works;
	}
	
	@Override
	@Transactional
	public Long createUserWork(UserWork work) {
		userWorkMapper.insertUserWork(work);
		
		userWorkCache.deleteUserWorks(work.getUserId());
		
		return work.getId();
	}

	@Override
	@Transactional
	public void updateUserWork(UserWork work) {
		userWorkMapper.updateUserWork(work);
		
		userWorkCache.deleteUserWorks(work.getUserId());
	}
	
	@Override
	@Transactional
	public void deleteUserWork(Long id) {
		userWorkMapper.deleteUserWork(id);
		
		userWorkCache.deleteUserWorks(threadSession.getUserId());
	}
	
	/******************************* Service *******************************/
	static final String URI_WORK_LIST = "/xiaoma/api/user/work/list";
	static final String URI_WORK_CREATE = "/xiaoma/api/user/work/create";
	static final String URI_WORK_UPDATE = "/xiaoma/api/user/work/update";
	static final String URI_WORK_DELETE = "/xiaoma/api/user/work/delete";
	
	@Override
	public String doService(String uri, Map<String, Object> params) {
		LOGGER.info("In doService... Params = " + params);
		
//		if(!threadSession.authorized()) {
//			return errorWithCodeKey(Constants.CODE_ERROR_RELOGIN, "auth_err_session_notfound");
//		}
		Session sess = new Session();
		sess.setUserId(7L);
		threadSession.set(sess);
		
		if(URI_WORK_LIST.equals(uri)) {
			return workList(params);
		} else if(URI_WORK_CREATE.equals(uri)) {
			return workCreate(params);
		} else if(URI_WORK_UPDATE.equals(uri)) {
			return workUpdate(params);
		} else if(URI_WORK_DELETE.equals(uri)) {
			return workDelete(params);
		}
		
		return null;
	}

	private String workList(Map<String, Object> params) {
		LOGGER.info("In workList... Params = " + params);
		
		Object userIdGetObj = params.get("user_id_get");
		List<UserWork> works = this.listUserWork(userIdGetObj == null ? threadSession.getUserId() : Long.parseLong(userIdGetObj.toString()));
		
		return successWithData(works);
	}

	private String workCreate(Map<String, Object> params) {
		LOGGER.info("In workCreate... Params = " + params);
		
		UserWork work = JsonUtils.readMap(params, UserWork.class);
//		Long currentMillis = System.currentTimeMillis();
//		project.setCreateDate(currentMillis);
//		project.setLastUpdate(currentMillis);
		Long workId = createUserWork(work);
		
		return successWithData(MapUtils.toMap("user_work_id", workId));
	}

	private String workUpdate(Map<String, Object> params) {
		LOGGER.info("In workUpdate... Params = " + params);
		
		UserWork work = JsonUtils.readMap(params, UserWork.class);
//		project.setLastUpdate(System.currentTimeMillis());
		updateUserWork(work);
		
		return Constants.RESPONSE_SUCCESS;
	}

	private String workDelete(Map<String, Object> params) {
		LOGGER.info("In workDelete... Params = " + params);
		
		Long userWorkId = Long.parseLong(params.get("user_work_id").toString());
		deleteUserWork(userWorkId);
		
		return Constants.RESPONSE_SUCCESS;
	}

	

	


	

}

