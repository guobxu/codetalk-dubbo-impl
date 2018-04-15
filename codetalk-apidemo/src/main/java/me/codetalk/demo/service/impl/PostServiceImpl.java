package me.codetalk.demo.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;

import me.codetalk.demo.pojo.Post;
import me.codetalk.demo.pojo.Tag;
import me.codetalk.demo.service.IPostService;

@Service("postService")
public class PostServiceImpl implements IPostService {

	static final Tag TAG_JAVA = new Tag(1, "java");
	static final Tag TAG_SPRING = new Tag(11, "spring");
	static final Tag TAG_ORCL = new Tag(2, "oracle");
	static final Tag TAG_MYSQL = new Tag(3, "mysql");
	static final Tag TAG_DATABASE = new Tag(31, "database");
	static final Tag TAG_REDIS= new Tag(4, "redis");
	static final Tag TAG_IOS = new Tag(5, "ios");
	static final Tag TAG_MOBILE = new Tag(6, "mobile");
	static final Tag TAG_ANDROID = new Tag(7, "android");
	static final Tag TAG_HTML5 = new Tag(8, "html5");
	static final Tag TAG_WEB = new Tag(81, "web");
	static final Tag TAG_BLAH = new Tag(9, "blah");
	
	static Long randomDate() {
		Long millisPerDay = 24 * 60 * 60 * 1000L;
		Random rand = new Random();
		
		return System.currentTimeMillis() - rand.nextInt(30) * millisPerDay;
	}
	
	@Override
	public void createPost(Post post) {
		ALL_POSTS.add(0, post);
	}

	@Override
	public List<Post> findPost(int begin, int count) {
		List<Post> posts = new ArrayList<>();
		
		int total = ALL_POSTS.size(), end = begin + count;
		for(int i = begin; i < end && i < total; i++) {
			posts.add(ALL_POSTS.get(i));
		}
		
		return posts;
	}
	
	@Override
	public List<Tag> findAllTags() {
		return ALL_TAGS;
	}

	@Override
	public Tag getTagByText(String text) {
		for(Tag tag : ALL_TAGS) {
			if(tag.getText().equals(text)) return tag;
		}
		
		return null;
	}
	
	@Override
	public List<Tag> getTagsByText(List<String> tags) {
		List<Tag> tagList = new ArrayList<>();
		if(tags == null) return tagList;
		
		for(String tag : tags) {
			Tag tagobj = getTagByText(tag);
			if(tagobj != null) tagList.add(tagobj);
		}
		
		return tagList;
	}
	
	static List<Post> ALL_POSTS = new ArrayList<>();
	static {
		ALL_POSTS.add(new Post.Builder().setId(1L).setAuthor("guobxu").setContent("This is a post about java & redis, the latter is so performant!")
				.setCreateDate(randomDate()).setTags(Arrays.asList(new Tag[] {TAG_JAVA, TAG_REDIS})).build());
		ALL_POSTS.add(new Post.Builder().setId(2L).setAuthor("rod").setContent("Spring, the most popular lightweight J2EE framework, earn its popularity since 2003 & Rod's classic book.")
				.setCreateDate(randomDate()).setTags(Arrays.asList(new Tag[] {TAG_JAVA, TAG_SPRING})).build());
		ALL_POSTS.add(new Post.Builder().setId(3L).setAuthor("sqlguru").setContent("I use mysql a lot much, it's so cute and performant. And i just wonder why some people say it's outdate, i cannot find the reason. blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah")
				.setCreateDate(randomDate()).setTags(Arrays.asList(new Tag[] {TAG_MYSQL, TAG_DATABASE, TAG_ORCL})).build());
		ALL_POSTS.add(new Post.Builder().setId(4L).setAuthor("guobxu").setContent("This is a blah blah blah... post, it's just nonsense! Do you like it?")
				.setCreateDate(randomDate()).setTags(Arrays.asList(new Tag[] {TAG_BLAH})).build());
		ALL_POSTS.add(new Post.Builder().setId(5L).setAuthor("webdev").setContent("I am a webdev from china. But it seems webdev in china is far way from the world.")
				.setCreateDate(randomDate()).setTags(Arrays.asList(new Tag[] {TAG_HTML5, TAG_WEB})).build());
		ALL_POSTS.add(new Post.Builder().setId(6L).setAuthor("guobxu").setContent("This is a post without tags.")
				.setCreateDate(randomDate()).setTags(Arrays.asList(new Tag[] {})).build());
		ALL_POSTS.add(new Post.Builder().setId(7L).setAuthor("iosjunior").setContent("I am new to iOS development, now i am learning from some books. Is there any other better way?")
				.setCreateDate(randomDate()).setTags(Arrays.asList(new Tag[] {TAG_IOS, TAG_MOBILE})).build());
		ALL_POSTS.add(new Post.Builder().setId(8L).setAuthor("androidfine").setContent("Android is better than iOS. No, it's far better than iOS.")
				.setCreateDate(randomDate()).setTags(Arrays.asList(new Tag[] {TAG_IOS, TAG_MOBILE, TAG_ANDROID})).build());
		ALL_POSTS.add(new Post.Builder().setId(9L).setAuthor("cacheGOD").setContent("This is a post about redis. blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah")
				.setCreateDate(randomDate()).setTags(Arrays.asList(new Tag[] {TAG_REDIS})).build());
		ALL_POSTS.add(new Post.Builder().setId(10L).setAuthor("user1").setContent("This is a blah post!")
				.setCreateDate(randomDate()).setTags(Arrays.asList(new Tag[] {TAG_BLAH})).build());
		ALL_POSTS.add(new Post.Builder().setId(11L).setAuthor("user1").setContent("Hi guys, I am new to this app or should i say this is a forum......BTW, i use Java.")
				.setCreateDate(randomDate()).setTags(Arrays.asList(new Tag[] {TAG_BLAH, TAG_JAVA})).build());
		ALL_POSTS.add(new Post.Builder().setId(12L).setAuthor("guobxu").setContent("分布式应用的构建需要多种技术, 后端有java / mysql / oracle, 前端有ios/android/html5 等.")
				.setCreateDate(randomDate()).setTags(Arrays.asList(new Tag[] {TAG_JAVA, TAG_IOS, TAG_HTML5, TAG_MYSQL, TAG_ANDROID})).build());
	}
	
	static List<Tag> ALL_TAGS = null;
	static {
		ALL_TAGS = Arrays.asList(new Tag[]{TAG_JAVA,TAG_SPRING,TAG_ORCL,TAG_MYSQL,TAG_DATABASE,TAG_REDIS,TAG_IOS,TAG_MOBILE,TAG_ANDROID,TAG_HTML5,TAG_WEB,TAG_BLAH,});
	}
	
}













