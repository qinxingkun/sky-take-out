package com.sky.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 封装分页查询结果
 */
@Data
@AllArgsConstructor //生成带有所有参数的构造器
@NoArgsConstructor  //生成不带参数的构造器
public class PageResult implements Serializable {

    private long total; //总记录数

    private List records; //当前页数据集合

}
