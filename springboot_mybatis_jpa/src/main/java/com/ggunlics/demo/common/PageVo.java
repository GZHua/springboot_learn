package com.ggunlics.demo.common;

import cn.hutool.core.bean.BeanUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageSerializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Collection;
import java.util.List;

/**
 * 基础分页 vo
 *
 * @author ggunlics
 * @date 2021/3/4 16:25
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class PageVo<T> extends PageSerializable<T> {
    public static final int DEFAULT_NAVIGATE_PAGES = 8;
    private int pageNum;
    private int pageSize;
    private int size;
    private int pages;
    private boolean isFirstPage;
    private boolean isLastPage;

    public PageVo() {
        this.isFirstPage = false;
        this.isLastPage = false;
    }

    public PageVo(List<T> list) {
        this(list, 8);
    }

    public PageVo(List<T> list, int navigatePages) {
        super(list);
        if (list instanceof Page) {
            Page page = (Page) list;
            this.pageNum = page.getPageNum();
            this.pageSize = page.getPageSize();
            this.pages = page.getPages();
            this.size = page.size();
            this.isFirstPage = page.getPageNum() < 2;
            this.isLastPage = pageNum >= pages;
        } else if (list instanceof Collection) {
            this.pageNum = 1;
            this.pageSize = list.size();
            this.pages = this.pageSize > 0 ? 1 : 0;
            this.size = list.size();
            this.isFirstPage = false;
            this.isLastPage = false;
        }
    }

    public static <T, O> PageVo<O> convert(PageVo<T> pageVo, List<O> list) {
        PageVo<O> newPageVo = new PageVo<>();
        BeanUtil.copyProperties(pageVo, newPageVo, true);
        newPageVo.setList(list);
        return newPageVo;
    }

    public static <T> PageVo<T> of(List<T> list) {
        return new PageVo<>(list);
    }

    public static <T> PageVo<T> of(List<T> list, int navigatePages) {
        return new PageVo<>(list, navigatePages);
    }
}
