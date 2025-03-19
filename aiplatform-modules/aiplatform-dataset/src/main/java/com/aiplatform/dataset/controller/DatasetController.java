package com.aiplatform.dataset.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.aiplatform.common.core.utils.poi.ExcelUtil;
import com.aiplatform.common.core.web.controller.BaseController;
import com.aiplatform.common.core.web.domain.AjaxResult;
import com.aiplatform.common.core.web.page.TableDataInfo;
import com.aiplatform.common.log.annotation.Log;
import com.aiplatform.common.log.enums.BusinessType;
import com.aiplatform.common.security.annotation.RequiresPermissions;
import com.aiplatform.dataset.domain.entity.Dataset;
import com.aiplatform.dataset.service.IDatasetService;

/**
 * 数据集管理Controller
 * 
 * @author aiplatform
 */
@RestController
@RequestMapping("/dataset")
public class DatasetController extends BaseController
{
    @Autowired
    private IDatasetService datasetService;

    private static final Logger log = LoggerFactory.getLogger(DatasetController.class);

    /**
     * 查询数据集列表
     */
    @RequiresPermissions("dataset:dataset:list")
    @GetMapping("/list")
    public TableDataInfo list(Dataset dataset)
    {
        log.info("Received dataset list request: {}", dataset);
        startPage();
        List<Dataset> list = datasetService.selectDatasetList(dataset);
        log.info("Found {} datasets", list.size());
        return getDataTable(list);
    }

    /**
     * 导出数据集列表
     */
    @RequiresPermissions("dataset:dataset:export")
    @Log(title = "数据集", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Dataset dataset)
    {
        List<Dataset> list = datasetService.selectDatasetList(dataset);
        ExcelUtil<Dataset> util = new ExcelUtil<Dataset>(Dataset.class);
        util.exportExcel(response, list, "数据集数据");
    }

    /**
     * 获取数据集详细信息
     */
    @RequiresPermissions("dataset:dataset:query")
    @GetMapping(value = "/{datasetId}")
    public AjaxResult getInfo(@PathVariable("datasetId") Long datasetId)
    {
        return success(datasetService.selectDatasetById(datasetId));
    }

    /**
     * 新增数据集
     */
    @RequiresPermissions("dataset:dataset:add")
    @Log(title = "数据集", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Dataset dataset)
    {
        return toAjax(datasetService.insertDataset(dataset));
    }

    /**
     * 修改数据集
     */
    @RequiresPermissions("dataset:dataset:edit")
    @Log(title = "数据集", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Dataset dataset)
    {
        return toAjax(datasetService.updateDataset(dataset));
    }

    /**
     * 删除数据集
     */
    @RequiresPermissions("dataset:dataset:remove")
    @Log(title = "数据集", businessType = BusinessType.DELETE)
    @DeleteMapping("/{datasetIds}")
    public AjaxResult remove(@PathVariable Long[] datasetIds)
    {
        return toAjax(datasetService.deleteDatasetByIds(datasetIds));
    }
} 