package com.huanf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huanf.domain.LoginUser;
import com.huanf.domain.ResponseResult;
import com.huanf.domain.Tag;
import com.huanf.dto.TabListDto;
import com.huanf.dto.TagListDto;
import com.huanf.mapper.TagMapper;
import com.huanf.service.TagService;
import com.huanf.utils.BeanCopyUtils;
import com.huanf.utils.SecurityUtils;
import com.huanf.vo.PageVo;
import com.huanf.vo.TagVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author 35238
 * @date 2023/8/2 0002 21:15
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    @Autowired
    //操作tag表
    private TagMapper tagMapper;

    @Override
    //查询标签列表
    public ResponseResult<PageVo> pageTagList(Integer pageNum, Integer pageSize, TagListDto tagListDto) {

        //分页查询的条件。模糊+分页
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        //第二、三个参数是互相比较。第一个参数是判空，当用户没有查询条件时，就不去比较后面两个参数
        queryWrapper.like(StringUtils.hasText(tagListDto.getName()),Tag::getName,tagListDto.getName());
        //第二、三个参数是互相比较。第一个参数是判空，当用户没有查询条件时，就不去比较后面两个参数
        queryWrapper.like(StringUtils.hasText(tagListDto.getRemark()),Tag::getRemark,tagListDto.getRemark());

        //分页查询。Page是mybatisplus提供的类，Tag是我们在huanf-framework工程写的类
        Page<Tag> page = new Page<>();
        page.setCurrent(pageNum);
        page.setSize(pageSize);
        page(page,queryWrapper);

        //封装数据返回。PageVo是我们在huanf-framework工程写的类
        PageVo pageVo = new PageVo(page.getRecords(),page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    //-------------------------------新增标签------------------------------------

    @Override
    public ResponseResult addTag(TabListDto tagListDto) {

        Tag tag = new Tag();
        //获取创建人、创建时间
        LoginUser loginUser = SecurityUtils.getLoginUser();
        //获取创建人的id
        tag.setCreateBy(loginUser.getUser().getId());

        try{
            //生成创建时间、更新时间
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //获取当前时间
            Date now = new Date();
            //将当前时间格式化为指定格式的字符串
            String strNow = dateFormat.format(now);
            //将字符串转换为Date类型
            Date date = dateFormat.parse(strNow);
            //最终得到的就是创建时间
            tag.setCreateTime(date);
        } catch (ParseException e){
            e.printStackTrace();
        }

        //修改标签名、标签的描述信息
        tag.setName(tagListDto.getName());
        tag.setRemark(tagListDto.getRemark());

        //把新增好后的数据插入数据库
        tagMapper.insert(tag);
        return ResponseResult.okResult();
    }

    //-------------------------------删除标签------------------------------------

    @Override
    public ResponseResult deleteTag(Long id) {
        // 通过数据id查找数据
        Tag tag  = tagMapper.selectById(id);
        // 把值传入数据库进行更新
        if (tag != null){
            // 把 def_flag=1 为逻辑删除
            int flag = 1;
            tagMapper.myUpdateById(id,flag);
        }
        return ResponseResult.okResult();
    }

    //-------------------------------修改标签------------------------------------

    @Override
    //①根据标签的id来查询标签
    public ResponseResult getLableById(Long id) {
        Tag tag = tagMapper.selectById(id);
        // 封装成vo响应给前端
        TagVo tagVoData = BeanCopyUtils.copyBean(tag,TagVo.class);
        return ResponseResult.okResult(tagVoData);
    }

    @Override
    //②根据标签的id来修改标签
    public ResponseResult myUpdateById(TagVo tagVo) {
        Tag tag = new Tag();
        // 获取更新时间、更新人
        LoginUser loginUser = SecurityUtils.getLoginUser();
        // 更新人的id
        tag.setUpdateBy(loginUser.getUser().getId());

        // 创建时间、更新时间
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // 获取当前时间
            Date now = new Date();
            //将当前时间格式化为指定格式的字符串
            String strNow = dateFormat.format(now);
            //将字符串转换为Date类型
            Date date = dateFormat.parse(strNow);
            //最终得到的就是创建时间
            tag.setUpdateTime(date);
        } catch (ParseException e){
            e.printStackTrace();
        }

        //修改标签id、标签名、标签的描述信息
        tag.setId(tagVo.getId());
        tag.setName(tagVo.getName());
        tag.setRemark(tagVo.getRemark());

        //把新增好后的数据插入数据库
        tagMapper.updateById(tag);
        return ResponseResult.okResult();
    }

    //---------------------------写博文-查询文章标签的接口---------------------------

    @Override
    public List<TagVo> listAllTag() {
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Tag::getId,Tag::getName);
        List<Tag> list = list(wrapper);
        List<TagVo> tagVos = BeanCopyUtils.copyBeanList(list, TagVo.class);
        return tagVos;
    }
}
