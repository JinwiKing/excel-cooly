package org.king.excooly.support.common;

import java.util.HashMap;
import java.util.Map;

public class LfuCacheManager {
	
	static class CacheNode{
		long frequency;
		Object key, val;
		CacheNode preNode, nextNode;
		CacheLink belongLink;
		
		public CacheNode(long initFrequency, Object initKey, Object initVal, CacheLink initBelongLink) {
			frequency = initFrequency;
			key = initKey;
			val = initVal;
			belongLink = initBelongLink;
		}
	}
	
	static class CacheLink{
		long frequency;
		CacheLink preLink, nextLink;
		CacheNode headNode, tailNode;
		
		CacheLink(long initFrequency) {
			frequency = initFrequency;
		}
		
		CacheLink(long initFrequency, CacheNode initHead) {
			frequency = initFrequency;
			headNode = initHead;
		}
		
		CacheLink(long initFrequency, CacheNode initHead, CacheNode initTail, CacheLink initPre, CacheLink initNext) {
			frequency = initFrequency;
			headNode = initHead;
			tailNode = initTail;
			preLink = initPre;
			nextLink = initNext;
		}
	}
	
	private int remain;
	private Map<Object, CacheNode> cacheMap = new HashMap<>();
	private CacheLink startLink = new CacheLink(1);
	
	public LfuCacheManager(int size) {
		remain = size == 0 ? -1 : size;
	}
	
	public void add(Object key, Object value) {
		if(key == null) throw new IllegalArgumentException("Key must not be null");
		if(remain < 0) return;
		
		CacheNode cacheNode = useCache(key);
		if(cacheNode != null) {
			cacheNode.val = value;
			return;
		}
		
		if(remain == 0) {
			CacheLink linkTailToDel = startLink;
			while(linkTailToDel.tailNode == null) {
				linkTailToDel = linkTailToDel.preLink;
			}
			
			CacheNode tailToDel = linkTailToDel.tailNode;
			if(tailToDel == linkTailToDel.headNode) {
				linkTailToDel.headNode = null;
				linkTailToDel.tailNode = null;
			}else {
				linkTailToDel.tailNode = tailToDel.preNode;
				tailToDel.preNode.nextNode = null;
			}
			tailToDel.preNode = null;
			tailToDel.nextNode = null;
			tailToDel.belongLink = null;
			
			if(linkTailToDel.headNode == null && linkTailToDel != startLink) {
				CacheLink preDel;
				if((preDel = linkTailToDel.preLink) != null) preDel.nextLink = linkTailToDel.nextLink;
				linkTailToDel.nextLink.preLink = preDel;
			}
			
			cacheMap.remove(tailToDel.key);
		}else remain--;
		
		cacheNode = new CacheNode(1, key, value, startLink);
		if(startLink.headNode != null) {
			cacheNode.nextNode = startLink.headNode;
			startLink.headNode.preNode = cacheNode;
			startLink.headNode = cacheNode;
		}else startLink.headNode = startLink.tailNode = cacheNode;
		
		cacheMap.put(key, cacheNode);
	}
	
	public Object get(Object key) {
		if(key == null) throw new IllegalArgumentException("Key must not be null");
		if(remain < 0) return null;
		
		CacheNode cacheNode = useCache(key);
		return cacheNode != null ? cacheNode.val : null;
	}
	
	private CacheNode useCache(Object key) {
		CacheNode cache = cacheMap.get(key);
		if(cache == null) return null;
		
		CacheNode pre, next;
		if((pre = cache.preNode) != null) pre.nextNode = cache.nextNode;
		if((next = cache.nextNode) != null) next.preNode = cache.preNode;
		cache.nextNode = null;
		cache.preNode = null;
		long cacheFrequency = ++cache.frequency;
		
		CacheLink belongLink = cache.belongLink, preLink;
		if(belongLink.headNode == cache && belongLink.tailNode == cache) {
			belongLink.headNode = null;
			belongLink.tailNode = null;
		}else if(belongLink.tailNode == cache) belongLink.tailNode = pre;
		else if(belongLink.headNode == cache) belongLink.headNode = next;
		if((preLink = belongLink.preLink) != null) {
			if(preLink.frequency == cacheFrequency) {
				CacheNode oldPreLinkHead = cache.nextNode = preLink.headNode;
				if(oldPreLinkHead != null) oldPreLinkHead.preNode = cache;
				preLink.headNode = cache;
			}else {
				CacheLink alink = new CacheLink(cacheFrequency, cache, cache, preLink, belongLink);
				preLink.nextLink = alink;
				belongLink.preLink = alink;
			}
		}else {
			CacheLink alink = new CacheLink(cacheFrequency, cache, cache, null, belongLink);
			belongLink.preLink = alink;
		}
		cache.belongLink = belongLink.preLink;
		if(belongLink.headNode == null && belongLink != startLink) {
			preLink = belongLink.preLink;
			CacheLink nextLink = belongLink.nextLink;
			preLink.nextLink = nextLink;
			nextLink.preLink = preLink;
			belongLink.preLink = null;
			belongLink.nextLink = null;
		}
		
		return cache;
	}
}
