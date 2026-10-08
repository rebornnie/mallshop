package com.mall.common.api;

import com.baomidou.mybatisplus.core.metadata.IPage;

import java.io.Serializable;
import java.util.List;

public class CommonPage<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer pageNum;
    private Integer pageSize;
    private Integer totalPage;
    private Long total;
    private List<T> records;

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }

    public static <T> CommonPage<T> restPage(IPage<T> pageResult) {
        CommonPage<T> result = new CommonPage<>();
        result.setPageNum((int) pageResult.getCurrent());
        result.setPageSize((int) pageResult.getSize());
        result.setTotal(pageResult.getTotal());
        result.setTotalPage((int) pageResult.getPages());
        result.setRecords(pageResult.getRecords());
        return result;
    }

    public static <T> CommonPage<T> restPage(List<T> list, long total, int pageNum, int pageSize) {
        CommonPage<T> result = new CommonPage<>();
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setTotal(total);
        result.setTotalPage((int) ((total + pageSize - 1) / pageSize));
        result.setRecords(list);
        return result;
    }
}
