package com.pinyougou.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.mapper.TbContentMapper;
import com.pinyougou.pojo.PageResult;
import com.pinyougou.pojo.TbContent;
import com.pinyougou.pojo.TbContentExample;
import com.pinyougou.pojo.TbContentExample.Criteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;


/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper contentMapper;
	@Autowired
	private RedisTemplate redisTemplate;
	/**
	 * 查询全部
	 */
	@Override
	public List<TbContent> findAll() {
		return contentMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbContent> page=   (Page<TbContent>) contentMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbContent content) {
		contentMapper.insert(content);
		//清空缓存
		redisTemplate.boundHashOps("contentList").delete(content.getCategoryId());
	}


	/**
	 * 修改
	 */
	@Override
	public void update(TbContent content){
		contentMapper.updateByPrimaryKey(content);
		//从redis中删除原来的分类id对应的广告列表
		//查询此广告原来的分类
		Long categoryId = contentMapper.selectByPrimaryKey(content.getId()).getCategoryId();
		redisTemplate.boundHashOps("contentList").delete(categoryId+"");
		//判断是否是原来的分类（分类的id有没有发生变化）
		if(categoryId.longValue() != content.getCategoryId().longValue()){
			redisTemplate.boundHashOps("contentList").delete(content.getCategoryId()+"");
		}
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbContent findOne(Long id){
		return contentMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			//从redis中删除此分类
			Long categoryId = contentMapper.selectByPrimaryKey(id).getCategoryId();
			redisTemplate.boundHashOps("contentList").delete(categoryId);
			contentMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbContent content, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbContentExample example=new TbContentExample();
		Criteria criteria = example.createCriteria();
		
		if(content!=null){			
						if(content.getTitle()!=null && content.getTitle().length()>0){
				criteria.andTitleLike("%"+content.getTitle()+"%");
			}
			if(content.getUrl()!=null && content.getUrl().length()>0){
				criteria.andUrlLike("%"+content.getUrl()+"%");
			}
			if(content.getPic()!=null && content.getPic().length()>0){
				criteria.andPicLike("%"+content.getPic()+"%");
			}
			if(content.getStatus()!=null && content.getStatus().length()>0){
				criteria.andStatusLike("%"+content.getStatus()+"%");
			}
	
		}
		
		Page<TbContent> page= (Page<TbContent>)contentMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 *根据广告分类查询广告列表
	 * @param categoryId
	 * @return
	 */
	@Override
	public List<TbContent> findContentByCategoryId(String categoryId) {
		//首先，从缓存中取得某个分类下的广告列表
		List<TbContent> contentList = (List<TbContent>) redisTemplate.boundHashOps("contentList").get(categoryId);
		//不为空就在redis缓存中取，为空的话就查询数据库然后添加到redis缓存中
		if(contentList == null){
			//构造查询实例
			TbContentExample example = new TbContentExample();
			//定义查询条件
			Criteria criteria = example.createCriteria();
			//添加查询条件
			criteria.andCategoryIdEqualTo(new Long(categoryId));
			//得到查询结果集
			contentList = contentMapper.selectByExample(example);
			//将查询结果集放到缓存中
			redisTemplate.boundHashOps("contentList").put(categoryId,contentList);

		}
		//返回结果集
		return contentList;

	}

}
