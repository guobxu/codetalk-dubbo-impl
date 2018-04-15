package me.codetalk.messaging;

public class MesgObj {

	private String id; 		// uuid
	private String type; 	// message type
	private Object data; 	// Map<String, Object> || List<Map<String, Object>>
	
	private Long createDate;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
	
	public Long getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Long createDate) {
		this.createDate = createDate;
	}
	
	@Override
	public String toString() {
		return "{ id = " + id + 
				", type = " + type +
				", createDate = " + createDate +
				", obj = " + data.toString() +
				"}";
	}

	
	
}
