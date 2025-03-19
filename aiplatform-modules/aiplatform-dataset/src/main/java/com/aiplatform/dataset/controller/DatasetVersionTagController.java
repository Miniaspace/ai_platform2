package com.aiplatform.dataset.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.aiplatform.common.core.web.controller.BaseController;
import com.aiplatform.common.core.web.domain.AjaxResult;
import com.aiplatform.common.core.web.page.TableDataInfo;
import com.aiplatform.common.log.annotation.Log;
import com.aiplatform.common.log.enums.BusinessType;
import com.aiplatform.common.security.annotation.RequiresPermissions;
import com.aiplatform.dataset.domain.entity.DatasetVersionTag;
import com.aiplatform.dataset.service.IDatasetVersionTagService;

/**
 * 数据集版本标签Controller
 * 
 * @author aiplatform
 */
@RestController
@RequestMapping("/dataset/version/tag")
public class DatasetVersionTagController extends BaseController {
    @Autowired
    private IDatasetVersionTagService datasetVersionTagService;

    /**
     * 查询版本标签列表
     */
    @RequiresPermissions("dataset:version:list")
    @GetMapping("/list")
    public TableDataInfo list(DatasetVersionTag versionTag) {
        startPage();
        List<DatasetVersionTag> list = datasetVersionTagService.selectDatasetVersionTagList(versionTag);
        return getDataTable(list);
    }

    /**
     * 查询版本的所有标签
     */
    @RequiresPermissions("dataset:version:query")
    @GetMapping("/list/{versionId}")
    public AjaxResult listByVersionId(@PathVariable("versionId") Long versionId) {
        return success(datasetVersionTagService.selectTagsByVersionId(versionId));
    }

    /**
     * 获取版本标签详细信息
     */
    @RequiresPermissions("dataset:version:query")
    @GetMapping("/{tagId}")
    public AjaxResult getInfo(@PathVariable("tagId") Long tagId) {
        return success(datasetVersionTagService.selectDatasetVersionTagById(tagId));
    }

    /**
     * 新增版本标签
     */
    @RequiresPermissions("dataset:version:edit")
    @Log(title = "版本标签", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DatasetVersionTag versionTag) {
        return toAjax(datasetVersionTagService.insertDatasetVersionTag(versionTag));
    }

    /**
     * 批量新增版本标签
     */
    @RequiresPermissions("dataset:version:edit")
    @Log(title = "版本标签", businessType = BusinessType.INSERT)
    @PostMapping("/batch")
    public AjaxResult batchAdd(@RequestBody List<DatasetVersionTag> versionTags) {
        return toAjax(datasetVersionTagService.batchInsertDatasetVersionTag(versionTags));
    }

    /**
     * 修改版本标签
     */
    @RequiresPermissions("dataset:version:edit")
    @Log(title = "版本标签", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DatasetVersionTag versionTag) {
        return toAjax(datasetVersionTagService.updateDatasetVersionTag(versionTag));
    }

    /**
     * 删除版本标签
     */
    @RequiresPermissions("dataset:version:edit")
    @Log(title = "版本标签", businessType = BusinessType.DELETE)
    @DeleteMapping("/{tagIds}")
    public AjaxResult remove(@PathVariable Long[] tagIds) {
        return toAjax(datasetVersionTagService.deleteDatasetVersionTagByIds(tagIds));
    }

    /**
     * 检查标签名称是否唯一
     */
    @RequiresPermissions("dataset:version:query")
    @GetMapping("/checkTagNameUnique")
    public AjaxResult checkTagNameUnique(@RequestParam("versionId") Long versionId, 
                                       @RequestParam("tagName") String tagName) {
        return success(datasetVersionTagService.checkTagNameUnique(versionId, tagName));
    }

    /**
     * 根据标签名称查找版本
     */
    @RequiresPermissions("dataset:version:query")
    @GetMapping("/findVersionByTag/{datasetId}/{tagName}")
    public AjaxResult findVersionByTag(@PathVariable("datasetId") Long datasetId,
                                     @PathVariable("tagName") String tagName) {
        return success(datasetVersionTagService.findVersionByTag(datasetId, tagName));
    }
} 