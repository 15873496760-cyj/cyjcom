package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class ItemSearchServiceImpl implements ItemSearchService {
    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    public Map<String, Object> search(Map searchMap) {
        //创建集合
        Map<String, Object> map = new HashMap<>();
        //构建查询条件
        Query query = new SimpleQuery("*:*");
        //创建查询
        Criteria criteria = null;
        if (searchMap == null) {
            //创建查询条件
            criteria = new Criteria("item_keywords");
        } else {
            criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        }
        //添加条件
        query.addCriteria(criteria);
        query.setOffset(0);
        query.setRows(40);
        //进行分页
        ScoredPage<TbItem> tbItems = solrTemplate.queryForPage(query, TbItem.class);
        //获取结果集
        List<TbItem> content = tbItems.getContent();
        map.put("rows", content);
        return map;
    }
}
