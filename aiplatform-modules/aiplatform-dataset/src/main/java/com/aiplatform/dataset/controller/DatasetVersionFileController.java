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
import com.aiplatform.dataset.domain.entity.DatasetVersionFile;
import com.aiplatform.dataset.service.IDatasetVersionFileService;

/**
 * 数据集版本文件关联Controller
 * 
 * @author aiplatform
 */
@RestController
@RequestMapping("/dataset/version")
public class DatasetVersionFileController extends BaseController {
    @Autowired
    private IDatasetVersionFileService datasetVersionFileService;

    /**
     * 查询版本文件列表
     */
    @RequiresPermissions("dataset:version:list")
    @GetMapping("/{datasetId}/version/{versionId}/files")
    public TableDataInfo list(@PathVariable("datasetId") Long datasetId,
                            @PathVariable("versionId") Long versionId,
                            DatasetVersionFile versionFile) {
        versionFile.setVersionId(versionId);
        startPage();
        List<DatasetVersionFile> list = datasetVersionFileService.selectDatasetVersionFileList(versionFile);
        return getDataTable(list);
    }

    /**
     * 查询版本的所有文件
     */
    @RequiresPermissions("dataset:version:query")
    @GetMapping("/{versionId}/files")
    public AjaxResult listByVersionId(@PathVariable("versionId") Long versionId) {
        return success(datasetVersionFileService.selectFilesByVersionId(versionId));
    }

    /**
     * 获取版本文件详细信息
     */
    @RequiresPermissions("dataset:version:query")
    @GetMapping("/version/{versionId}/files/{fileId}")
    public AjaxResult getInfo(@PathVariable("versionId") Long versionId, 
                            @PathVariable("fileId") Long fileId) {
        return success(datasetVersionFileService.selectDatasetVersionFile(versionId, fileId));
    }

    /**
     * 新增版本文件关联
     */
    @RequiresPermissions("dataset:version:edit")
    @Log(title = "版本文件关联", businessType = BusinessType.INSERT)
    @PostMapping("/{versionId}")
    public AjaxResult add(@PathVariable("versionId") Long versionId, @RequestBody DatasetVersionFile versionFile) {
        versionFile.setVersionId(versionId);
        return toAjax(datasetVersionFileService.insertDatasetVersionFile(versionFile));
    }

    /**
     * 批量新增版本文件关联
     */
    @RequiresPermissions("dataset:version:edit")
    @Log(title = "版本文件关联", businessType = BusinessType.INSERT)
    @PostMapping("/batch/{versionId}")
    public AjaxResult batchAdd(@PathVariable("versionId") Long versionId, @RequestBody List<DatasetVersionFile> versionFiles) {
        versionFiles.forEach(file -> file.setVersionId(versionId));
        return toAjax(datasetVersionFileService.batchInsertDatasetVersionFile(versionFiles));
    }

    /**
     * 修改版本文件关联
     */
    @RequiresPermissions("dataset:version:edit")
    @Log(title = "版本文件关联", businessType = BusinessType.UPDATE)
    @PutMapping("/{versionId}/{fileId}")
    public AjaxResult edit(@PathVariable("versionId") Long versionId, @PathVariable("fileId") Long fileId, @RequestBody DatasetVersionFile versionFile) {
        versionFile.setVersionId(versionId);
        versionFile.setFileId(fileId);
        return toAjax(datasetVersionFileService.updateDatasetVersionFile(versionFile));
    }

    /**
     * 删除版本文件关联
     */
    @RequiresPermissions("dataset:version:edit")
    @Log(title = "版本文件关联", businessType = BusinessType.DELETE)
    @DeleteMapping("/{versionId}/{fileId}")
    public AjaxResult remove(@PathVariable("versionId") Long versionId, @PathVariable("fileId") Long fileId) {
        return toAjax(datasetVersionFileService.deleteDatasetVersionFile(versionId, fileId));
    }

    /**
     * 批量删除版本文件关联
     */
    @RequiresPermissions("dataset:version:edit")
    @Log(title = "版本文件关联", businessType = BusinessType.DELETE)
    @DeleteMapping("/batch/{versionId}/{fileIds}")
    public AjaxResult batchRemove(@PathVariable("versionId") Long versionId, @PathVariable("fileIds") Long[] fileIds) {
        return toAjax(datasetVersionFileService.batchDeleteDatasetVersionFile(versionId, fileIds));
    }

    /**
     * 复制版本文件关联
     */
    @RequiresPermissions("dataset:version:edit")
    @Log(title = "版本文件关联", businessType = BusinessType.INSERT)
    @PostMapping("/copy/{sourceVersionId}/{targetVersionId}")
    public AjaxResult copyFiles(@PathVariable("sourceVersionId") Long sourceVersionId, 
                               @PathVariable("targetVersionId") Long targetVersionId) {
        return toAjax(datasetVersionFileService.copyVersionFiles(sourceVersionId, targetVersionId));
    }

    /**
     * 比较版本文件
     */
    @RequiresPermissions("dataset:version:query")
    @GetMapping("/version/compare/{sourceVersionId}/{targetVersionId}")
    public AjaxResult compareFiles(@PathVariable("sourceVersionId") Long sourceVersionId,
                                 @PathVariable("targetVersionId") Long targetVersionId) {
        return success(datasetVersionFileService.compareVersionFiles(sourceVersionId, targetVersionId));
    }
} 