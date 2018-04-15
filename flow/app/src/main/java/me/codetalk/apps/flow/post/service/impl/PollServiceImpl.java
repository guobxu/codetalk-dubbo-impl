package me.codetalk.apps.flow.post.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.codetalk.apps.flow.post.entity.Poll;
import me.codetalk.apps.flow.post.entity.PollOption;
import me.codetalk.apps.flow.post.mapper.PollMapper;
import me.codetalk.apps.flow.post.service.PollService;

@Service("pollService")
public class PollServiceImpl implements PollService {

	@Autowired
	private PollMapper pollMapper;
	
	@Override
	@Transactional
	public Long createPoll(Poll poll) {
		pollMapper.insertPoll(poll);
		
		Long pollId = poll.getId();
		List<PollOption> options = poll.getOptions();
		for(PollOption option : options) {
			option.setPollId(pollId);
		}
		pollMapper.insertPollOptions(options);
		
		return pollId;
	}

}
