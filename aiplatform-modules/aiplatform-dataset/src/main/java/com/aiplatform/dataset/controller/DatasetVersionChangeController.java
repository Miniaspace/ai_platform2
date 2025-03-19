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
import com.aiplatform.dataset.domain.entity.DatasetVersionChange;
import com.aiplatform.dataset.service.IDatasetVersionChangeService;

/**
 * 数据集版本变更记录Controller
 * 
 * @author aiplatform
 */
@RestController
@RequestMapping("/dataset/version/change")
public class DatasetVersionChangeController extends BaseController {
    @Autowired
    private IDatasetVersionChangeService datasetVersionChangeService;

    /**
     * 查询版本变更记录列表
     */
    @RequiresPermissions("dataset:version:list")
    @GetMapping("/list")
    public TableDataInfo list(DatasetVersionChange versionChange) {
        startPage();
        List<DatasetVersionChange> list = datasetVersionChangeService.selectDatasetVersionChangeList(versionChange);
        return getDataTable(list);
    }

    /**
     * 查询版本的所有变更记录
     */
    @RequiresPermissions("dataset:version:query")
    @GetMapping("/list/{versionId}")
    public AjaxResult listByVersionId(@PathVariable("versionId") Long versionId) {
        return success(datasetVersionChangeService.selectChangesByVersionId(versionId));
    }

    /**
     * 获取版本变更记录详细信息
     */
    @RequiresPermissions("dataset:version:query")
    @GetMapping("/{changeId}")
    public AjaxResult getInfo(@PathVariable("changeId") Long changeId) {
        return success(datasetVersionChangeService.selectDatasetVersionChangeById(changeId));
    }

    /**
     * 新增版本变更记录
     */
    @RequiresPermissions("dataset:version:edit")
    @Log(title = "版本变更记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DatasetVersionChange versionChange) {
        return toAjax(datasetVersionChangeService.insertDatasetVersionChange(versionChange));
    }

    /**
     * 批量新增版本变更记录
     */
    @RequiresPermissions("dataset:version:edit")
    @Log(title = "版本变更记录", businessType = BusinessType.INSERT)
    @PostMapping("/batch")
    public AjaxResult batchAdd(@RequestBody List<DatasetVersionChange> versionChanges) {
        return toAjax(datasetVersionChangeService.batchInsertDatasetVersionChange(versionChanges));
    }

    /**
     * 修改版本变更记录
     */
    @RequiresPermissions("dataset:version:edit")
    @Log(title = "版本变更记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DatasetVersionChange versionChange) {
        return toAjax(datasetVersionChangeService.updateDatasetVersionChange(versionChange));
    }

    /**
     * 删除版本变更记录
     */
    @RequiresPermissions("dataset:version:edit")
    @Log(title = "版本变更记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{changeIds}")
    public AjaxResult remove(@PathVariable Long[] changeIds) {
        return toAjax(datasetVersionChangeService.deleteDatasetVersionChangeByIds(changeIds));
    }

    /**
     * 记录文件添加变更
     */
    @RequiresPermissions("dataset:version:edit")
    @Log(title = "版本变更记录", businessType = BusinessType.INSERT)
    @PostMapping("/record/add/{versionId}/{fileId}")
    public AjaxResult recordFileAdd(@PathVariable("versionId") Long versionId,
                                  @PathVariable("fileId") Long fileId,
                                  @RequestParam("changeDesc") String changeDesc) {
        return toAjax(datasetVersionChangeService.recordFileAdd(versionId, fileId, changeDesc));
    }

    /**
     * 记录文件修改变更
     */
    @RequiresPermissions("dataset:version:edit")
    @Log(title = "版本变更记录", businessType = BusinessType.INSERT)
    @PostMapping("/record/modify/{versionId}/{fileId}/{oldFileId}")
    public AjaxResult recordFileModify(@PathVariable("versionId") Long versionId,
                                     @PathVariable("fileId") Long fileId,
                                     @PathVariable("oldFileId") Long oldFileId,
                                     @RequestParam("changeDesc") String changeDesc) {
        return toAjax(datasetVersionChangeService.recordFileModify(versionId, fileId, oldFileId, changeDesc));
    }

    /**
     * 记录文件删除变更
     */
    @RequiresPermissions("dataset:version:edit")
    @Log(title = "版本变更记录", businessType = BusinessType.INSERT)
    @PostMapping("/record/delete/{versionId}/{fileId}")
    public AjaxResult recordFileDelete(@PathVariable("versionId") Long versionId,
                                     @PathVariable("fileId") Long fileId,
                                     @RequestParam("changeDesc") String changeDesc) {
        return toAjax(datasetVersionChangeService.recordFileDelete(versionId, fileId, changeDesc));
    }
} 