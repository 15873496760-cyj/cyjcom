package com.pinyougou.page.service;

public interface ItemPageService {
    /**
     * 生成商品详情页的静态页面
     * @param goodIds
     * @return
     */
    boolean genItemHtml(Long goodIds);
}
