package me.codetalk.auth.entity;

/**
 * 注册会话
 * @author guobxu
 *
 */
public class SignupSession {

	private String id; // UUID
	private String vcode; // 注册验证码
	private String imgdata; // 注册码图片base64数据
	private String passwdKey; // 密码加密key, 32位
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getVcode() {
		return vcode;
	}
	
	public void setVcode(String vcode) {
		this.vcode = vcode;
	}

	public String getImgdata() {
		return imgdata;
	}

	public void setImgdata(String imgdata) {
		this.imgdata = imgdata;
	}

	public String getPasswdKey() {
		return passwdKey;
	}

	public void setPasswdKey(String passwdKey) {
		this.passwdKey = passwdKey;
	}
	
	
	
}
