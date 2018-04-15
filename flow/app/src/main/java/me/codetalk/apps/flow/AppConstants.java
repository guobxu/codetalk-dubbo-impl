package me.codetalk.apps.flow;

public final class AppConstants {

	public static final String APP_ID = "me.codetalk.flowapp";
	public static final String APP_SERVICE_KEY = "C6B0E40CBFED1E72F3993BD32954471C"; // 服务票据key
	
	public static final Long AUTH_STR_EXPIRE = 15 * 60 * 1000L; // 通信认证串有效时间, 15分钟
	
	// ES
	public static final String INDEX_FLOWAPP = "flowapp";
	
	public static final String INDEX_TYPE_POST = "post";
	
	// MESG
	public static final String MESG_POST_CREATE = "flow-post-create";
	public static final String MESG_COMMENT_CREATE = "flow-comment-create";
	
	// POST 类型
	public static final int POST_TYPE_BASIC = 1;    // 普通帖子
    public static final int POST_TYPE_POLL = 2;     // 投票
	
	// ARTICLE 状态 1正在阅读 2已读 3不感兴趣 4稍后阅读
    public static final int ARTICLE_READING = 1;
    public static final int ARTICLE_READ = 2;    
    public static final int ARTICLE_NOT_FAVOR = 3;    
    public static final int ARTICLE_FOR_LATER = 4;    
	
	
    public static final String MESG_ARTICLE_RM = "flow-article-remove";	// 刷新文章
    
    
    
}
