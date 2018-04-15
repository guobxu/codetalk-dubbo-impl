package me.codetalk.demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import me.codetalk.Constants;
import me.codetalk.demo.pojo.Post;
import me.codetalk.demo.pojo.Tag;
import me.codetalk.demo.service.IPostService;
import me.codetalk.util.JsonUtils;

@RestController
public class PostController {

	@Autowired
	private IPostService postService;
	
	@RequestMapping(value = "/post/create", method = RequestMethod.POST)
	public String createPost(@RequestBody Map<String, Object> params) {
		Long id = Long.parseLong(params.get("post_id").toString()), 
				createDate = Long.parseLong(params.get("create_date").toString());
		String content = params.get("post_content").toString(), 
				author = params.get("post_author").toString();
		List<Tag> tagList = postService.getTagsByText((List<String>) params.get("post_tags"));
		
		Post post = new Post.Builder().setId(id).setAuthor(author).setContent(content).setCreateDate(createDate)
				.setTags(tagList).build();
				
		postService.createPost(post);
		
		return Constants.RESPONSE_SUCCESS;
	}
	
	@RequestMapping(value = "/post/list", method = RequestMethod.GET)
	public String listPost(HttpServletRequest request) {
		int begin = Integer.parseInt(request.getParameter("begin")),
				count = Integer.parseInt(request.getParameter("count"));
				
		List<Post> posts = postService.findPost(begin, count);

		return successWithData(posts);
	}
	
	@RequestMapping(value = "/tag/listall", method = RequestMethod.GET)
	public String findAllTags() {
		List<Tag> tags = postService.findAllTags();

		return successWithData(tags);
	}
	
	protected String successWithData(Object data) {
    	Map<String, Object> rtObj = new HashMap<String, Object>();
    	rtObj.put(Constants.KEY_CODE, Constants.CODE_SUCCESS);
    	rtObj.put(Constants.KEY_DATA, data);
    	
    	return JsonUtils.toJson(rtObj);
    }
	
}
