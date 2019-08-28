package com.zelin.util;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SolrUtil {

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private TbItemMapper itemMapper;
    public void importItemData() {
        //构建查询对象
        TbItemExample example = new TbItemExample();
        //定义查询条件
        TbItemExample.Criteria criteria = example.createCriteria();
        //添加查询条件
        criteria.andStatusEqualTo("1");
        //得到查询结果
        List<TbItem> tbItems = itemMapper.selectByExample(example);
        for (TbItem tbItem : tbItems) {
            //将数据转换之后放入map集合
            Map<String, String> map = JSON.parseObject(tbItem.getSpec(), Map.class);
            //给动态域赋值
            tbItem.setSpecMap(map);
        }
        //执行保存
        solrTemplate.saveBeans(tbItems);
        //提交
        solrTemplate.commit();
    }

}
