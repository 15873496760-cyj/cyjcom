package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.*;


@Service
public class ItemSearchServiceImpl implements ItemSearchService {
    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Map<String, Object> search(Map searchMap) {
        //创建集合
        Map<String, Object> map = new HashMap<>();
        //进行高亮查询
        Map highlight = getHighlight(searchMap);
        map.putAll(highlight);

        //进行分组查询
        List<String> groupList = getGroupList(searchMap);
        map.put("categoryList",groupList);

        //将从缓存中取出来的商品集合与规格集合放入map中
        String category = "";
        if(StringUtils.isNotBlank(searchMap.get("category")+"")){
            category = searchMap.get("category")+"";
        }else{
            // 如果没有值，就取分类的第一个值作为分类的值，得到模板id，根据模板id得到品牌及规格列表
            category = groupList.get(0);
        }
        //从redis中根据此分类名称，得到模板id，再得到品牌及规格列表
        Map specAndSpecList = saveToList(category);
        //将品牌及规格列表放到大map中
        map.putAll(specAndSpecList);
        return map;
    }

    @Override
    public void importList(List list) {
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }

    /**
     * 删除商品通过商品id
     * @param goodsIds
     */
    @Override
    public void deleteByGoodsIds(List goodsIds) {
        Query query = new SimpleQuery();
        Criteria criteria = new Criteria("item_goodsid");
        criteria.is(goodsIds);
        query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

    /**
     * 高亮查询的方法
     * @param searchMap
     * @return
     */
    private Map<String, Object> getHighlight(Map searchMap) {
        Map<String, Object> map = new HashMap<>();
        //创建高亮查询
        HighlightQuery query = new SimpleHighlightQuery();
        //添加查询条件
        Criteria criteria = new Criteria("item_keywords");
        //添加条件
        criteria.is(searchMap.get("keywords"));
        query.addCriteria(criteria);

        //过滤查询

        //分类查询
        FilterQuery filterQueryCate = new SimpleFilterQuery();
        String category = (String) searchMap.get("category");
        //如果不为空的话，进行分类查询
        if (StringUtils.isNotBlank(category)) {
            //创建条件对象
            Criteria criteriaCate = new Criteria("item_category");
            //添加条件
            criteria.is(category);
            //执行条件查询
            filterQueryCate.addCriteria(criteriaCate);
            //将条件查询与高亮查询进行绑定
            query.addFilterQuery(filterQueryCate);
        }

        //按品牌查询
        FilterQuery filterQueryBrand = new SimpleFilterQuery();
        String brand = (String) searchMap.get("brand");
        //如果不为空，就进行按品牌分类
        if (StringUtils.isNotBlank( brand)) {
            //创建条件对象
            Criteria criteriaBrand = new Criteria("item_brand");
            //添加条件
            criteria.is(brand);
            //执行条件查询
            filterQueryBrand.addCriteria(criteriaBrand);
            //将条件查询结果与高亮查询进行绑定
            query.addFilterQuery(filterQueryBrand);
        }

        //按规格查询
        //如果不为空，就进行按规格查询
        FilterQuery filterQuerySpec = new SimpleFilterQuery();
        if (searchMap.get("spec") != null) {
            Map map1 = JSON.parseObject(searchMap.get("spec").toString(), Map.class);
            for (Object key: map1.keySet()) {
                //创建条件对象
                Criteria criteriaSpec = new Criteria("item_spec_" + key);
                //添加条件
                criteria.is(map1.get(key));
                //执行条件查询
                filterQuerySpec.addCriteria(criteriaSpec);
                //将按规格查询与高亮查询进行绑定
                query.addFilterQuery(filterQuerySpec);
            }
        }

        //按价格查询
        if (StringUtils.isNotEmpty(searchMap.get("price") + "")) {
        FilterQuery filterQueryPrice = new SimpleFilterQuery();
            //根据-符号进行拆分
            String[] prices = searchMap.get("price").toString().split("-");
            if (!prices[0].equals("0")) {
                //创建条件对象
                Criteria criteriaPrice = new Criteria("item_price");
                //添加条件
                criteriaPrice.greaterThanEqual(prices[0]);
                //执行条件查询
                filterQueryPrice.addCriteria(criteriaPrice);
                //将按规格查询与高亮查询进行绑定
            }
            if (!prices[1].equals("*")) {
                //创建条件对象
                Criteria criteriaPrice = new Criteria("item_price");
                //添加条件
                criteriaPrice.lessThanEqual(prices[1]);
                //执行条件查询
                filterQueryPrice.addCriteria(criteriaPrice);
                //将按规格查询与高亮查询进行绑定
            }
            query.addFilterQuery(filterQueryPrice);
        }


        //排序字段
        String sortField = "";
        //代表是升序还是降序排序
        String sortOrder = (String) searchMap.get("sort");
        if (StringUtils.isNotEmpty(searchMap.get("sortField") + "")) {
            sortField = searchMap.get("sortField") + "";
        }
        Sort sort = null;
        if (sortOrder.equals("ASC")) {
            sort = new Sort(Sort.Direction.ASC, "item_" + sortField);
        }
        if (sortOrder.equals("DESC")) {
            sort = new Sort(Sort.Direction.DESC, "item_" + sortField);
        }
        query.addSort(sort);

        //设置高亮查询的高亮字段
        HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");
        //设置高亮字段的前缀
        highlightOptions.setSimplePrefix("<span style='color:red'>");
        highlightOptions.setSimplePostfix("</span>");
        Integer page = null;
        if (StringUtils.isNotEmpty(searchMap.get("page") + "")) {
            page = Integer.parseInt(searchMap.get("page") + "");
        }
        Integer pagesize = null;
        if (StringUtils.isNotEmpty(searchMap.get("pagesize") + "")) {
            pagesize = Integer.parseInt(searchMap.get("pagesize") + "");
        }

        query.setOffset((page - 1) * pagesize);
        query.setRows(pagesize);

        //将高亮查询设置高亮选项
        query.setHighlightOptions(highlightOptions);
        //得到高亮对象页
        HighlightPage<TbItem> tbItems = solrTemplate.queryForHighlightPage(query, TbItem.class);
        //得到入口结果集
        List<HighlightEntry<TbItem>> highlighted = tbItems.getHighlighted();
        for (HighlightEntry<TbItem> tbItemHighlightEntry : highlighted) {
            //得到为有高亮字段的对象
            TbItem entity = tbItemHighlightEntry.getEntity();
            //得到高亮序列表
            List<HighlightEntry.Highlight> highlights = tbItemHighlightEntry.getHighlights();
            //判断是否有值
            if (highlights.size() > 0 && highlights.get(0).getSnipplets().size() > 0) {
                entity.setTitle(highlights.get(0).getSnipplets().get(0));
            }
            map.put("rows", tbItems.getContent());
            map.put("totalPages", tbItems.getTotalPages());
            map.put("total", tbItems.getTotalElements());
        }
        return map;
    }

    /**
     * 分组查询的方法
     * @param searchMap
     * @return
     */
    private List<String> getGroupList(Map searchMap) {
        //定义集合类
        List<String> groupList = new ArrayList<>();
        //创建查询
        Query query = new SimpleQuery();
        //创建查询条件
        Criteria criteria = new Criteria("item_keywords");
        //添加查询条件
        criteria.is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //定义分组查询
        GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(groupOptions);
        //获取分组查询页
        GroupPage<TbItem> tbItems = solrTemplate.queryForGroupPage(query, TbItem.class);
        //取得分组结果对象
        GroupResult<TbItem> item_category = tbItems.getGroupResult("item_category");
        //得到分组入口页对象
        Page<GroupEntry<TbItem>> groupEntries = item_category.getGroupEntries();
        //获取分组内容结果集
        List<GroupEntry<TbItem>> content = groupEntries.getContent();
        for (GroupEntry<TbItem> tbItemGroupEntry : content) {
            //取出内容结果集中的value值
            String groupValue = tbItemGroupEntry.getGroupValue();
            //加入到list集合中
            groupList.add(groupValue);
        }
        return groupList;
    }

    /**
     * 从缓存中取出商品列表与规格列表
     * @param category
     * @return
     */
    private Map saveToList(String category) {
        Map map = new HashMap();
        //从缓存中取出模板id
        Long itemCat = (Long)redisTemplate.boundHashOps("itemCat").get(category);
        List<Map> brandList = (List<Map>) redisTemplate.boundHashOps("brandList").get(itemCat);
        List<Map> specList = (List<Map>) redisTemplate.boundHashOps("specList").get(itemCat);
        map.put("brandList", brandList);
        map.put("specList", specList);
        return map;
    }

}
