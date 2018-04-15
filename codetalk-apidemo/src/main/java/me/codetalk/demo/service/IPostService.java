package me.codetalk.demo.service;

import java.util.List;

import me.codetalk.demo.pojo.Post;
import me.codetalk.demo.pojo.Tag;

public interface IPostService {

	public void createPost(Post post);
	
	public List<Post> findPost(int begin, int count);

	public List<Tag> findAllTags();
	
	public Tag getTagByText(String text);
	
	public List<Tag> getTagsByText(List<String> tags);
	
}
