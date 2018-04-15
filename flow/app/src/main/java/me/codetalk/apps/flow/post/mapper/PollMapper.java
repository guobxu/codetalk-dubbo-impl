package me.codetalk.apps.flow.post.mapper;

import java.util.List;

import me.codetalk.apps.flow.post.entity.Poll;
import me.codetalk.apps.flow.post.entity.PollOption;

public interface PollMapper {

	public void insertPoll(Poll poll);
	
	public void insertPollOptions(List<PollOption> options);
	
}
