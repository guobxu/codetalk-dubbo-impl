package me.codetalk.proxy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import me.codetalk.Constants;
import me.codetalk.auth.service.AuthService;
import me.codetalk.storage.exception.FileStorageException;
import me.codetalk.storage.fdfs.FdfsConstants;
import me.codetalk.storage.fdfs.service.IFdfsService;
import me.codetalk.util.MapBuilder;

@RestController
public class FileUploadProxy extends AbstractServiceProxy {

	public static final Logger LOGGER = LoggerFactory.getLogger(FileUploadProxy.class);
	
	@Autowired
	private IFdfsService storageService;
	
	@Autowired
	private AuthService authService;
	
	@Value("${proxy.file-server}")
	private String fileServer;
	
	@RequestMapping(value = "/upload", method = RequestMethod.PUT)
    public String doUpload(@RequestParam("file") MultipartFile[] files, HttpServletRequest request) {
    	LOGGER.info("In doUpload...");
    	
    	Object[] authInfo = extractAuthInfo(request);
    	if(authInfo == null) {
    		return errorWithKey("err_auth_missing");
    	}
    	
    	Long userId = (Long)authInfo[0];
    	String accessToken = (String)authInfo[1], authStr = (String)authInfo[2];
    	
    	// message
//    	String uri = request.getRequestURI(), clientIp = request.getRemoteAddr();
//    	Map<String, Object> params = new MapBuilder<String, Object>()
//    										.put(Constants.PARAM_USER_ID, userId)
//    										.put(Constants.PARAM_URI, uri).put(Constants.PARAM_CLIENT_IP, clientIp).build();
//    	sendFileUploadMesg(params);
    	
    	try {
        	if(!authService.isLoggedIn(userId, accessToken, authStr)) {
        		return errorWithCodeKey(Constants.CODE_ERROR_RELOGIN, "auth_err_session_notfound");
        	}

        	List<String> fileUrlList = new ArrayList<>();
			for(MultipartFile file : files) {
				fileUrlList.add(storeFile(file));
			}
			
			return successWithData(fileUrlList);
    	} catch(IOException ioe) {
    		LOGGER.error(ioe.getMessage(), ioe);
    		
    		return errorWithKey("fdfs_upload_error");
    	} catch(FileStorageException ex) {
    		LOGGER.error(ex.getMessage(), ex);
    		
    		return errorWithKey("fdfs_upload_error");
    	} catch(Exception ex) {
    		LOGGER.error(ex.getMessage(), ex);
    		
    		return errorWithKey("app_exception_msg");
    	}
    }
	
	/**
	 * 
	 * @param file
	 * @return 返回完成的文件HTTP路径
	 * @throws FileStorageException
	 * @throws IOException
	 */
	private String storeFile(MultipartFile file) throws FileStorageException, IOException {
		// upload
		byte[] data = file.getBytes();
		
		// meta info
		Map<String, String> metas = new HashMap<String, String>();
    	
		String name = file.getOriginalFilename(), type = file.getContentType();
    	metas.put(FdfsConstants.META_KEY_FILENAME, name);
    	metas.put(FdfsConstants.META_KEY_FILELENGTH, String.valueOf(data.length));
    	metas.put(FdfsConstants.META_KEY_CONTENT_TYPE, type);
		
		String serverFile = storageService.store(data, metas);
		if(serverFile == null) {
			throw new FileStorageException(km.get("fdfs_upload_error"));
		}
		
		return fileServer + serverFile;
	}
	
	/**
	 * 发送文件上传消息
	 * 
	 * @param params
	 */
//	private void sendFileUploadMesg(Map<String, Object> params) {
//		mesgSender.sendMessage(ProxyConstants.MESG_FILE_UP, params);
//	}
	
}
