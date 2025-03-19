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
import com.aiplatform.dataset.domain.entity.DatasetVersion;
import com.aiplatform.dataset.service.IDatasetVersionService;

/**
 * 数据集版本管理Controller
 * 
 * @author aiplatform
 */
@RestController
@RequestMapping("/dataset/version")
public class DatasetVersionController extends BaseController {
    @Autowired
    private IDatasetVersionService datasetVersionService;

    /**
     * 查询数据集版本列表
     */
    @RequiresPermissions("dataset:version:list")
    @GetMapping("/list")
    public TableDataInfo list(DatasetVersion datasetVersion) {
        startPage();
        List<DatasetVersion> list = datasetVersionService.selectDatasetVersionList(datasetVersion);
        return getDataTable(list);
    }

    /**
     * 获取数据集版本详细信息
     */
    @RequiresPermissions("dataset:version:query")
    @GetMapping(value = "/{versionId}")
    public AjaxResult getInfo(@PathVariable("versionId") Long versionId) {
        return success(datasetVersionService.selectDatasetVersionById(versionId));
    }

    /**
     * 获取数据集当前版本
     */
    @RequiresPermissions("dataset:version:query")
    @GetMapping(value = "/current/{datasetId}")
    public AjaxResult getCurrentVersion(@PathVariable("datasetId") Long datasetId) {
        return success(datasetVersionService.selectCurrentVersion(datasetId));
    }

    /**
     * 创建数据集版本
     */
    @RequiresPermissions("dataset:version:create")
    @Log(title = "数据集版本管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult create(@RequestBody DatasetVersion datasetVersion) {
        return toAjax(datasetVersionService.createVersion(datasetVersion));
    }

    /**
     * 修改数据集版本
     */
    @RequiresPermissions("dataset:version:edit")
    @Log(title = "数据集版本管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult update(@RequestBody DatasetVersion datasetVersion) {
        return toAjax(datasetVersionService.updateVersion(datasetVersion));
    }

    /**
     * 切换数据集版本
     */
    @RequiresPermissions("dataset:version:switch")
    @Log(title = "数据集版本管理", businessType = BusinessType.UPDATE)
    @PutMapping("/switch/{datasetId}/{versionId}")
    public AjaxResult switchVersion(@PathVariable("datasetId") Long datasetId, @PathVariable("versionId") Long versionId) {
        return toAjax(datasetVersionService.switchVersion(datasetId, versionId));
    }

    /**
     * 发布数据集版本
     */
    @RequiresPermissions("dataset:version:publish")
    @Log(title = "数据集版本管理", businessType = BusinessType.UPDATE)
    @PutMapping("/publish/{versionId}")
    public AjaxResult publishVersion(@PathVariable("versionId") Long versionId) {
        return toAjax(datasetVersionService.publishVersion(versionId));
    }

    /**
     * 废弃数据集版本
     */
    @RequiresPermissions("dataset:version:discard")
    @Log(title = "数据集版本管理", businessType = BusinessType.UPDATE)
    @PutMapping("/discard/{versionId}")
    public AjaxResult discardVersion(@PathVariable("versionId") Long versionId) {
        return toAjax(datasetVersionService.discardVersion(versionId));
    }

    /**
     * 删除数据集版本
     */
    @RequiresPermissions("dataset:version:remove")
    @Log(title = "数据集版本管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{versionIds}")
    public AjaxResult remove(@PathVariable Long[] versionIds) {
        return toAjax(datasetVersionService.deleteVersionByIds(versionIds));
    }

    /**
     * 检查版本名称是否唯一
     */
    @RequiresPermissions("dataset:version:query")
    @GetMapping("/checkVersionNameUnique")
    public boolean checkVersionNameUnique(@RequestParam("datasetId") Long datasetId, @RequestParam("versionName") String versionName) {
        return datasetVersionService.checkVersionNameUnique(datasetId, versionName);
    }

    /**
     * 获取版本树
     */
    @RequiresPermissions("dataset:version:query")
    @GetMapping("/tree/{datasetId}")
    public AjaxResult getVersionTree(@PathVariable("datasetId") Long datasetId) {
        return success(datasetVersionService.getVersionTree(datasetId));
    }
} 