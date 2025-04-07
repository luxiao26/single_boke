package com.huanf.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.huanf.domain.Category;
import com.huanf.domain.ResponseResult;
import com.huanf.domain.Role;
import com.huanf.dto.CategoryDto;
import com.huanf.dto.ChangeRoleStatusDto;
import com.huanf.enums.AppHttpCodeEnum;
import com.huanf.service.CategoryService;
import com.huanf.utils.BeanCopyUtils;
import com.huanf.utils.WebUtils;
import com.huanf.vo.CategoryVo;
import com.huanf.vo.ExcelCategoryVo;
import com.huanf.vo.PageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author 35238
 * @date 2023/8/7 0007 14:28
 */
@RestController
@RequestMapping("/content/category")
public class CategoryController {

    //---------------------------写博文-查询文章分类的接口--------------------------------

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/listAllCategory")
    public ResponseResult listAllCategory(){
        List<CategoryVo> list = categoryService.listAllCategory();
        return ResponseResult.okResult(list);
    }

    //----------------------------分页查询文章的分类列表---------------------------------

    @GetMapping("/list")
    public ResponseResult list(Category category, Integer pageNum, Integer pageSize) {
        PageVo pageVo = categoryService.selectCategoryPage(category,pageNum,pageSize);
        return ResponseResult.okResult(pageVo);
    }

    //-----------------------------增加文章的分类--------------------------------------

    @PreAuthorize("@ps.hasPermission('content:category:list')")//权限控制，ps是PermissionService类的bean名称
    @PostMapping
    public ResponseResult add(@RequestBody CategoryDto categoryDto){
        Category category = BeanCopyUtils.copyBean(categoryDto, Category.class);
        categoryService.save(category);
        return ResponseResult.okResult();
    }

    //----------------------------删除文章的分类--------------------------------------

    @PreAuthorize("@ps.hasPermission('content:article:list')")//权限控制，ps是PermissionService类的bean名称
    @DeleteMapping
    public ResponseResult remove(@RequestParam(value = "ids")String ids) {
        if (!ids.contains(",")) {
            categoryService.removeById(ids);
        } else {
            String[] idArr = ids.split(",");
            for (String id : idArr) {
                categoryService.removeById(id);
            }
        }
        return ResponseResult.okResult();
    }
    //-----------------------------修改文章的分类--------------------------------------

    @GetMapping(value = "/{id}")
    //①根据分类的id来查询分类
    public ResponseResult getInfo(@PathVariable(value = "id")Long id){
        Category category = categoryService.getById(id);
        return ResponseResult.okResult(category);
    }

    @PreAuthorize("@ps.hasPermission('content:article:list')")//权限控制，ps是PermissionService类的bean名称
    @PutMapping
    //②根据分类的id来修改分类
    public ResponseResult edit(@RequestBody Category category){
        categoryService.updateById(category);
        return ResponseResult.okResult();
    }

    @PreAuthorize("@ps.hasPermission('content:article:list')")//权限控制，ps是PermissionService类的bean名称
    @PutMapping("/changeStatus")
    public ResponseResult changeStatus(@RequestBody CategoryDto categoryDto){
        Category category = new Category();
        category.setId(Long.valueOf(categoryDto.getCategoryId()));
        category.setStatus(categoryDto.getStatus());
        return ResponseResult.okResult(categoryService.updateById(category));
    }


    //---------------------------把分类数据写入到Excel并导出-----------------------------

    //@PreAuthorize("@ps.hasPermission('content:category:export')")//权限控制，ps是PermissionService类的bean名称
    @GetMapping("/export")
    //注意返回值类型是void
    public void export(HttpServletResponse response){
        try {
            //设置下载文件的请求头，下载下来的Excel文件叫'分类.xlsx'
            WebUtils.setDownLoadHeader("分类.xlsx",response);
            //获取需要导出的数据
            List<Category> xxcategory = categoryService.list();

            List<ExcelCategoryVo> excelCategoryVos = BeanCopyUtils.copyBeanList(xxcategory, ExcelCategoryVo.class);
            //把数据写入到Excel中，也就是把ExcelCategoryVo实体类的字段作为Excel表格的列头
            //sheet方法里面的字符串是Excel表格左下角工作簿的名字
            EasyExcel.write(response.getOutputStream(), ExcelCategoryVo.class).autoCloseStream(Boolean.FALSE).sheet("文章分类")
                    .doWrite(excelCategoryVos);

        } catch (Exception e) {
            //如果出现异常,就返回失败的json数据给前端。AppHttpCodeEnum和ResponseResult是我们在huanf-framework工程写的类
            ResponseResult result = ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR);
            //WebUtils是我们在huanf-framework工程写的类，里面的renderString方法是将json字符串写入到请求体，然后返回给前端
            WebUtils.renderString(response, JSON.toJSONString(result));
        }
    }
}