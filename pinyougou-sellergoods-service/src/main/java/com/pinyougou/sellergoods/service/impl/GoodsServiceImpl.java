package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.group.Goods;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.sellergoods.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 服务实现层
 * @author Administrator
 *
 */
@Transactional
@Service
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;
	@Autowired
	private TbGoodsDescMapper goodsDescMapper;
	@Autowired
	private TbItemMapper itemMapper;
	@Autowired
	private TbBrandMapper brandMapper;
	@Autowired
	private TbItemCatMapper itemCatMapper;
	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Goods goods) {
		//向tb_goods表中添加记录
		goodsMapper.insert(goods.getGoods());

		//向tb_goodsDesc表添加记录
		//设置tb_goodsDesc表的外键
		goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());
		goodsDescMapper.insert(goods.getGoodsDesc());
		//添加sku商品信息
		saveToItem(goods);


	}
	//添加sku商品信息
	private void saveToItem(Goods goods) {
		//向tb_item表添加记录
		for (TbItem item : goods.getItems()) {
			item.setTitle(goods.getGoods().getGoodsName());
			//得到品牌名字
			String brandName = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId()).getName();
			//设置品牌名字
			item.setBrand(brandName);
			//得到三级分类id
			Long category3Id = goods.getGoods().getCategory3Id();
			item.setCategoryid(category3Id);
			//根据三级分类查询分类名称
			String categoryName = itemCatMapper.selectByPrimaryKey(category3Id).getName();
			item.setCategory(categoryName);
			item.setSeller(goods.getGoods().getSellerId());
			item.setGoodsId(goods.getGoods().getId());
			item.setUpdateTime(new Date());
			//设置图片，就是得到 goodsDesc表中的imageItems这个数组中的第一张图片
			String itemImages = goods.getGoodsDesc().getItemImages();
			//转换图片json对象为java对象
			List<Map> imageMap = JSON.parseArray(itemImages,Map.class);
			//判断是否有值
			if(imageMap != null && imageMap.size() > 0){
				String url = (String) imageMap.get(0).get("url");
				item.setImage(url);

			}
			item.setCreateTime(new Date());
			//保存sku商品
			itemMapper.insert(item);
		}
	}

	/**
	 * 修改
	 */

	@Override
	public void update(Goods goods){
		//修改商品表
		goodsMapper.updateByPrimaryKey(goods.getGoods());
		goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());
		//修改商品描述表
		goodsDescMapper.updateByPrimaryKey(goods.getGoodsDesc());

		//根据goodsid在tb_item表中删除这个外键对应的值
		TbItemExample example = new TbItemExample();
		TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(goods.getGoods().getId());
		itemMapper.deleteByExample(example);

		//向tb_item中添加sku商品
		saveToItem(goods);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOne(Long id){
		//根据商品id查询出所有的商品
		TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);

		//查询商品描述表的信息
		TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);

		//查询tb_item这个sku商品表的信息
		//创建查询实例
		TbItemExample example = new TbItemExample();
		//构造此查询实例的条件
		TbItemExample.Criteria criteria = example.createCriteria();
		//添加查询条件
		criteria.andGoodsIdEqualTo(id);
		//根据实例查询sku商品列表
		List<TbItem> tbItems = itemMapper.selectByExample(example);

		//构造Goods这个组合类的对象
		Goods goods = new Goods();
		goods.setGoods(tbGoods);
		goods.setGoodsDesc(tbGoodsDesc);
		goods.setItems(tbItems);

		//返回
		return goods;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			//goodsMapper.deleteByPrimaryKey(id);
			//根据id查询出商品对象
			TbGoods goods = goodsMapper.selectByPrimaryKey(id);
			//执行逻辑删除操作
			goods.setIsDelete("1");
			//执行修改操作
			goodsMapper.updateByPrimaryKey(goods);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		
		if(goods!=null){			
						if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
				//criteria.andSellerIdLike("%"+goods.getSellerId()+"%");
				criteria.andSellerIdEqualTo(goods.getSellerId());
			}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
			if(goods.getIsDelete()!=null && goods.getIsDelete().length()>0){
				criteria.andIsDeleteLike("%"+goods.getIsDelete()+"%");
			}
	
		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}
	//商品审核
	@Override
	public void updateStatus(String[] ids, String status) {
		for (String id : ids) {
			//根据商品id查询出商品对象
			TbGoods goods = goodsMapper.selectByPrimaryKey(Long.parseLong(id));
			//修改商品的状态值
			goods.setAuditStatus(status);
			//审核商品
			goodsMapper.updateByPrimaryKey(goods);
		}
	}

}
