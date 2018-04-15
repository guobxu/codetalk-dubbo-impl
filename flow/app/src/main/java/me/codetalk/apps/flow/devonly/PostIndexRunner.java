package me.codetalk.apps.flow.devonly;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import me.codetalk.apps.flow.post.elastic.DocPost;
import me.codetalk.apps.flow.post.elastic.repos.PostRepository;
import me.codetalk.apps.flow.post.entity.Post;
import me.codetalk.apps.flow.post.entity.vo.PostVO;
import me.codetalk.apps.flow.post.mapper.PostMapper;
import me.codetalk.util.JsonUtils;
import me.codetalk.util.StringUtils;

//@Component("postIndexRunner")
public class PostIndexRunner implements CommandLineRunner {

	@Autowired
	private PostMapper postMapper;
	
	@Autowired
	private PostRepository postRepos;
	
	@Override
	public void run(String... args) throws Exception { // index all post in database
		List<PostVO> allPosts = postMapper.selectAllPosts();
		
		for(Post post : allPosts) {
			DocPost doc = new DocPost();
			doc.setId(StringUtils.uuid());
			doc.setPostId(post.getId());
			doc.setPostType(post.getType());
			doc.setPostContent(post.getContent());
			
			Set<String> tags = new HashSet<>();
			List<Map<String, Object>> tagmapList = (List<Map<String, Object>>)JsonUtils.fromJsonObj(post.getTags(), List.class);
			for(Map<String, Object> tagmap : tagmapList) {
				tags.add(tagmap.get("t").toString().toLowerCase());
			}
			doc.setPostTags(tags);
			doc.setCreateBy(String.valueOf(post.getUserId()));
			doc.setCreateDate(post.getCreateDate());
			
			postRepos.save(doc);
		}
	}

}
