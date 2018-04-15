package me.codetalk.cache.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.DefaultTuple;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisZSetCommands.Tuple;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Service;

import me.codetalk.cache.service.CacheService;

/**
 * Created by Administrator on 2017/4/6.
 */
@Service
public class CacheServiceImpl implements CacheService {

	@Resource(name = "redisTemplate")  
    private RedisTemplate<String, String> redisTemplate;

    @Override
	public Long incr(String key) {
    	return redisTemplate.execute(new RedisCallback<Long>() {
            public Long doInRedis(RedisConnection connection)
                    throws DataAccessException {
            	return connection.incr(key.getBytes());
            }
        });
	}
    
    @Override
	public Long incrBy(String key, long delta) {
    	return redisTemplate.execute(new RedisCallback<Long>() {
            public Long doInRedis(RedisConnection connection)
                    throws DataAccessException {
            	return connection.incrBy(key.getBytes(), delta);
            }
        });
	}
    
    @Override
    public boolean exists(String key) {
        return redisTemplate.execute(new RedisCallback<Boolean>() {
            public Boolean doInRedis(RedisConnection connection)
                    throws DataAccessException {
                return connection.exists(key.getBytes());
            }
        });
    }

    @Override
    public boolean set(byte[] key, byte[] value, long activeTime) {
        return redisTemplate.execute(new RedisCallback<Boolean>() {
            public Boolean doInRedis(RedisConnection connection)
                    throws DataAccessException {
                boolean rs = true;
                connection.set(key, value);
                if (activeTime > 0) {
                    rs = connection.expire(key, activeTime);
                }
                return rs;
            }
        });
    }

    @Override
    public boolean set(String key, Object obj, long activeTime) {
        Jackson2JsonRedisSerializer serializer = (Jackson2JsonRedisSerializer)redisTemplate.getValueSerializer();
        byte[] valBytes = serializer.serialize(obj);

        return set(key.getBytes(), valBytes, activeTime);
    }

    @Override
    public boolean set(String key, Object obj) {
        return this.set(key, obj, 0L);
    }

    @Override
    public long delete(String key) {
        return redisTemplate.execute(new RedisCallback<Long>() {
            public Long doInRedis(RedisConnection connection)
                    throws DataAccessException {
                long result = connection.del(key.getBytes());

                return result;
            }
        });
    }
    
    public long delete(String... keys) {
    	return redisTemplate.execute(new RedisCallback<Long>() {
            public Long doInRedis(RedisConnection connection)
                    throws DataAccessException {
            	byte[][] byteArr = new byte[keys.length][];
            	for(int i = 0; i < keys.length; i++) { 
            		byteArr[i] = keys[i].getBytes();
            	}
            	
                long result = connection.del(byteArr);

                return result;
            }
        });
    }

    public Object get(final String key) {
        byte[] bytes = redisTemplate.execute(new RedisCallback<byte[]>() {
            public byte[] doInRedis(RedisConnection connection)
                    throws DataAccessException {
                return connection.get(key.getBytes());
            }
        });

        Jackson2JsonRedisSerializer serializer = (Jackson2JsonRedisSerializer)redisTemplate.getValueSerializer();
        return serializer.deserialize(bytes);
    }

    @Override
	public Object hGet(String hash, String key) {
		byte[] data = redisTemplate.execute(new RedisCallback<byte[]>() {
            public byte[] doInRedis(RedisConnection connection)
                    throws DataAccessException {
            	return connection.hGet(hash.getBytes(), key.getBytes());
            }
        });
		
		if(data == null) return null;
		
		Jackson2JsonRedisSerializer serializer = (Jackson2JsonRedisSerializer)redisTemplate.getValueSerializer();
        return serializer.deserialize(data);
	}

	@Override
	public boolean hSet(String hash, String key, Object obj) {
		return redisTemplate.execute(new RedisCallback<Boolean>() {
            public Boolean doInRedis(RedisConnection connection)
                    throws DataAccessException {
            	Jackson2JsonRedisSerializer serializer = (Jackson2JsonRedisSerializer)redisTemplate.getValueSerializer();
                byte[] valBytes = serializer.serialize(obj);
            	
            	return connection.hSet(hash.getBytes(), key.getBytes(), valBytes);
            }
        });
	}

	@Override
	public void hMSet(String hash, String[] keys, Object[] objs) {
		hMSet(hash, Arrays.asList(keys), Arrays.asList(objs));
	}
	
	@Override
	public void hMSet(String hash, List<String> keys, List<Object> objs) {
		redisTemplate.execute(new RedisCallback<Boolean>() {
            public Boolean doInRedis(RedisConnection connection)
                    throws DataAccessException {
            	Jackson2JsonRedisSerializer serializer = (Jackson2JsonRedisSerializer)redisTemplate.getValueSerializer();
            	
            	Map<byte[], byte[]> byteMap = new HashMap<>();
            	for(int i = 0; i < keys.size(); i++) {
            		byteMap.put(keys.get(i).getBytes(), serializer.serialize(objs.get(i)));
            	}

            	connection.hMSet(hash.getBytes(), byteMap);
            	
            	return Boolean.TRUE;
            }
        });
	}
	
	@Override
	public long hIncrBy(String hash, String key, long delta) {
		Long val = redisTemplate.execute(new RedisCallback<Long>() {
            public Long doInRedis(RedisConnection connection)
                    throws DataAccessException {
            	return connection.hIncrBy(hash.getBytes(), key.getBytes(), delta);
            }
        });
		
		return val;
	}
	
	@Override
	public Long hDel(String hash, String key) {
		return redisTemplate.execute(new RedisCallback<Long>() {
            public Long doInRedis(RedisConnection connection)
                    throws DataAccessException {
            	return connection.hDel(hash.getBytes(), key.getBytes());
            }
        });
	}
	
	@Override
	public List<Object> hMGet(String hash, List<String> keys) {
		String[] keysArr = new String[keys.size()];
		keys.toArray(keysArr);
		
		return hMGet(hash, keysArr);
	}
	
	@Override
	public List<Object> hMGet(String hash, String[] keys) {
		List<byte[]> bytesList = redisTemplate.execute(new RedisCallback<List<byte[]>>() {
            public List<byte[]> doInRedis(RedisConnection connection)
                    throws DataAccessException {
            	byte[][] bytesArr = new byte[keys.length][];
            	for(int i = 0; i < keys.length; i++) {
            		bytesArr[i] = keys[i].getBytes();
            	}
            	List<byte[]> bytesList = connection.hMGet(hash.getBytes(), bytesArr);
            	
            	return bytesList;
            }
        });
		
		if(bytesList == null) return null;
		
		Jackson2JsonRedisSerializer serializer = (Jackson2JsonRedisSerializer)redisTemplate.getValueSerializer();
		List<Object> objList = new ArrayList<Object>();
		for(byte[] bytes : bytesList) {
			objList.add(serializer.deserialize(bytes));
		}
		
        return objList;
	}
	
	@Override
	public Object doCallback(RedisCallback callback) {
		return redisTemplate.execute(callback);
	}

	@Override
	public boolean setNX(String key, Object obj) {
		 Jackson2JsonRedisSerializer serializer = (Jackson2JsonRedisSerializer)redisTemplate.getValueSerializer();
	     byte[] valBytes = serializer.serialize(obj);
	     return setNX(key.getBytes(), valBytes);
    }
	
	@Override
    public boolean setNX(byte[] key, byte[] value) {
        return redisTemplate.execute(new RedisCallback<Boolean>() {
            public Boolean doInRedis(RedisConnection connection)
                    throws DataAccessException {
                return connection.setNX(key, value);
            }
        });
    }
	
	@Override
	public Set<byte[]> keys(String key) {
		return (Set<byte[]>) redisTemplate.execute(new RedisCallback<Set<byte[]>>() {
            public Set<byte[]> doInRedis(RedisConnection connection)
                    throws DataAccessException {
            	return connection.keys(key.getBytes());
            }
        });
	}

    @Override
    public boolean setNX(String key, Object obj, long activeTime) {
        Jackson2JsonRedisSerializer serializer = (Jackson2JsonRedisSerializer)redisTemplate.getValueSerializer();
        byte[] valBytes = serializer.serialize(obj);
        if(activeTime != 0)
            return setNX(key.getBytes(), valBytes,activeTime);
        else
            return setNX(key.getBytes(), valBytes);
    }

    private boolean setNX(byte[] key, byte[] value,long activeTime) {
        return redisTemplate.execute(new RedisCallback<Boolean>() {
            public Boolean doInRedis(RedisConnection connection)
                    throws DataAccessException {
                boolean rs = connection.setNX(key, value);
                if(rs && activeTime > 0){
                    connection.expire(key,activeTime);
                }
                return rs;
            }
        });
    }

	@Override
	public boolean hsetNX(String key, String field, Object obj) {
		Jackson2JsonRedisSerializer serializer = (Jackson2JsonRedisSerializer) redisTemplate.getValueSerializer();
		byte[] valBytes = serializer.serialize(obj);
		return hsetNX(key.getBytes(), field.getBytes(), valBytes);
	}

	public boolean hsetNX(byte[] key, byte[] field, byte[] value) {
		return redisTemplate.execute(new RedisCallback<Boolean>() {
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				return connection.hSetNX(key, field, value);
			}
		});
	}
	
	public Map<byte[], byte[]> hGetAll(String key) {
		Map<byte[], byte[]> map = hGetAll(key.getBytes());
		return map;
	}
	
	public Map<byte[], byte[]> hGetAll(byte[] key) {
		return (Map<byte[], byte[]>) redisTemplate.execute(new RedisCallback<Map<byte[], byte[]>>() {
			public Map<byte[], byte[]> doInRedis(RedisConnection connection) throws DataAccessException {
				return connection.hGetAll(key);
			}
		});
	}
	
	public Long hDel(String key,List<String> keys) {
		return (Long) redisTemplate.execute(new RedisCallback<Long>() {
			public Long doInRedis(RedisConnection connection) throws DataAccessException {
				byte[][] byteArr = new byte[keys.size()][];
            	for(int i = 0; i < keys.size(); i++) { 
            		byteArr[i] = keys.get(i).getBytes();
            	}
				return connection.hDel(key.getBytes(), byteArr);
			}
		});
	}
	
	public boolean expire(String key, long seconds) {
		return (Boolean) redisTemplate.execute(new RedisCallback<Boolean>() {
			
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				return connection.expire(key.getBytes(), seconds);
			}
			
		});
	}

	@Override
	public Double zIncrBy(String key, String member, double scoreDelta) {
		return (Double) redisTemplate.execute(new RedisCallback<Double>() {
			
			public Double doInRedis(RedisConnection connection) throws DataAccessException {
				return connection.zIncrBy(key.getBytes(), scoreDelta, member.getBytes());
			}
			
		});
	}

	@Override
	public List<String> zRange(String key, long start, long end) {
		return (List<String>) redisTemplate.execute(new RedisCallback<List<String>>() {
			
			public List<String> doInRedis(RedisConnection connection) throws DataAccessException {
				Set<byte[]> bytesSet = connection.zRange(key.getBytes(), start, end);

				return bytesSetToList(bytesSet);
			}
			
		});
	}

	@Override
	public List<String> zRevRange(String key, long start, long end) {
		return (List<String>) redisTemplate.execute(new RedisCallback<List<String>>() {
			
			public List<String> doInRedis(RedisConnection connection) throws DataAccessException {
				Set<byte[]> bytesSet = connection.zRevRange(key.getBytes(), start, end);
				
				return bytesSetToList(bytesSet);
			}
			
		});
	}
	
	private List<String> bytesSetToList(Set<byte[]> bytesSet) {
		List<String> memberList = new ArrayList<>();
		
		if(bytesSet == null || bytesSet.isEmpty()) return memberList;
		for(byte[] bytes : bytesSet) {
			memberList.add(new String(bytes));
		}
		
		return memberList;
	}

	@Override
	public Map<String, Double> zRangeWithScore(String key, long start, long end) {
		return (Map<String, Double>) redisTemplate.execute(new RedisCallback<Map<String, Double>>() {
			
			public Map<String, Double> doInRedis(RedisConnection connection) throws DataAccessException {
				Set<Tuple> tupleSet = connection.zRangeWithScores(key.getBytes(), start, end);
				
				return tupleSetToMap(tupleSet);
			}
			
		});
	}

	@Override
	public Map<String, Double> zRevRangeWithScore(String key, long start, long end) {
		return (Map<String, Double>) redisTemplate.execute(new RedisCallback<Map<String, Double>>() {
			
			public Map<String, Double> doInRedis(RedisConnection connection) throws DataAccessException {
				Set<Tuple> tupleSet = connection.zRevRangeWithScores(key.getBytes(), start, end);
				
				return tupleSetToMap(tupleSet);
			}
			
		});
	}

	private Map<String, Double> tupleSetToMap(Set<Tuple> tupleSet) {
		Map<String, Double> memberScores = new LinkedHashMap<String, Double>();
		
		if(tupleSet == null || tupleSet.isEmpty()) return memberScores;
		for(Tuple tuple : tupleSet) {
			memberScores.put(new String(tuple.getValue()), tuple.getScore());
		}
		
		return memberScores;
	}

	@Override
	public Long zRem(String key, List<String> memberList) {
		return redisTemplate.execute(new RedisCallback<Long>() {
			
			public Long doInRedis(RedisConnection connection) throws DataAccessException {
				int len = memberList.size();
				byte[][] mbytes = new byte[len][];
				for(int i = 0; i < len; i++) {
					mbytes[i] = memberList.get(i).getBytes();
				}
				
				return connection.zRem(key.getBytes(), mbytes);
			}
			
		});
	}

	@Override
	public Long zRem(String key, String[] members) {
		return zRem(key, Arrays.asList(members));
	}

	@Override
	public Long zLen(String key) {
		return redisTemplate.execute(new RedisCallback<Long>() {
			
			public Long doInRedis(RedisConnection connection) throws DataAccessException {
				return connection.zCard(key.getBytes());
			}
			
		});
	}

	@Override
	public void zAdd(String key, String member, Double score) {
		redisTemplate.execute(new RedisCallback<Boolean>() {
			
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				return connection.zAdd(key.getBytes(), score, member.getBytes());
			}
			
		});
	}

	@Override
	public void zAdd(String key, List<String> memberList, List<Double> scoreList) {
		redisTemplate.execute(new RedisCallback<Long>() {
			
			public Long doInRedis(RedisConnection connection) throws DataAccessException {
				Set<Tuple> tupleSet = new HashSet<>();
				for(int i = 0, len = memberList.size(); i < len; i++) {
					String member = memberList.get(i);
					Double score = scoreList.get(i);
					
					tupleSet.add(new DefaultTuple(member.getBytes(), score));
				}
				
				return connection.zAdd(key.getBytes(), tupleSet);
			}
			
		});
	}

	

	
	
}
