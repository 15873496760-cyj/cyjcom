package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.group.Specification;
import com.pinyougou.mapper.TbSpecificationMapper;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.*;
import com.pinyougou.pojo.TbSpecificationExample.Criteria;
import com.pinyougou.sellergoods.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;


/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SpecificationServiceImpl implements SpecificationService {

	@Autowired
	private TbSpecificationMapper specificationMapper;
	@Autowired
	private TbSpecificationOptionMapper optionMapper;
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSpecification> findAll() {
		return specificationMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSpecification> page=   (Page<TbSpecification>) specificationMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Specification specification) {
		//向tb_specification这个规格表中添加数据
		specificationMapper.insert(specification.getSpec());
		//对tb_specificationOption这个表添加数据
		for (TbSpecificationOption option : specification.getSpecificationOptionList()) {
			//将当前的规格对象的id与规格选项进行绑定
			option.setSpecId(specification.getSpec().getId());
			optionMapper.insert(option);
		}
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(Specification specification){
		//修改规格表
		specificationMapper.updateByPrimaryKey(specification.getSpec());

		//先在tb_specification这个表中根据外键删除对应的记录
		TbSpecificationOptionExample example = new TbSpecificationOptionExample();
		TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
		criteria.andSpecIdEqualTo(specification.getSpec().getId());
		//根据外键id来删除规格选项
		optionMapper.deleteByExample(example);

		//在tb_specification这个表中添加对应的记录
		for (TbSpecificationOption option : specification.getSpecificationOptionList()) {
			option.setSpecId(specification.getSpec().getId());
			optionMapper.insert(option);
		}
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Specification findOne(Long id){
		//1.找到规格对象
		TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(id);
		//2.根据外键在tb_specification这个表中查询出此规格对应的规格选项列表
		TbSpecificationOptionExample example = new TbSpecificationOptionExample();
		TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
		criteria.andSpecIdEqualTo(id);
		//3.查询出这个外键关联的规格选项列表
		List<TbSpecificationOption> tbSpecificationOptions = optionMapper.selectByExample(example);
		//4.构造Specification组合对象
		Specification specification = new Specification();
		specification.setSpec(tbSpecification);
		specification.setSpecificationOptionList(tbSpecificationOptions);
		//5.返回组合对象
		return specification;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			specificationMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbSpecification specification, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSpecificationExample example=new TbSpecificationExample();
		Criteria criteria = example.createCriteria();
		
		if(specification!=null){			
						if(specification.getSpecName()!=null && specification.getSpecName().length()>0){
				criteria.andSpecNameLike("%"+specification.getSpecName()+"%");
			}
	
		}
		
		Page<TbSpecification> page= (Page<TbSpecification>)specificationMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public List<Map> findSpecList() {
		return specificationMapper.findSpecList();
	}

}
