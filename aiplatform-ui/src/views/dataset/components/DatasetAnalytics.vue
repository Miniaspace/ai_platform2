<template>
  <div class="dataset-analytics">
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>数据集版本统计</span>
              <el-select v-model="chartOptions.timeRange" placeholder="时间范围" @change="loadVersionChartData">
                <el-option label="最近7天" value="7" />
                <el-option label="最近30天" value="30" />
                <el-option label="最近90天" value="90" />
                <el-option label="全部" value="all" />
              </el-select>
            </div>
          </template>
          <div class="chart-container">
            <div ref="versionChartRef" class="chart"></div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>文件类型分布</span>
              <el-select v-model="chartOptions.versionId" placeholder="版本选择" @change="loadFileTypeChartData">
                <el-option
                  v-for="version in versionList"
                  :key="version.versionId"
                  :label="version.versionNumber"
                  :value="version.versionId"
                />
              </el-select>
            </div>
          </template>
          <div class="chart-container">
            <div ref="fileTypeChartRef" class="chart"></div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>标签分布统计</span>
              <el-select v-model="chartOptions.tagType" placeholder="标签类型" @change="loadTagChartData">
                <el-option label="全部" value="all" />
                <el-option label="分类" value="CATEGORY" />
                <el-option label="属性" value="PROPERTY" />
                <el-option label="状态" value="STATUS" />
                <el-option label="自定义" value="CUSTOM" />
              </el-select>
            </div>
          </template>
          <div class="chart-container">
            <div ref="tagChartRef" class="chart"></div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>文件规模分析</span>
            </div>
          </template>
          <div class="chart-container">
            <div ref="fileSizeChartRef" class="chart"></div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <el-card class="data-overview-card">
          <template #header>
            <div class="card-header">
              <span>数据集概览</span>
            </div>
          </template>
          <el-descriptions :column="3" border>
            <el-descriptions-item label="版本总数">{{ overviewData.versionCount }}</el-descriptions-item>
            <el-descriptions-item label="文件总数">{{ overviewData.fileCount }}</el-descriptions-item>
            <el-descriptions-item label="文件总大小">{{ formatFileSize(overviewData.totalSize) }}</el-descriptions-item>
            <el-descriptions-item label="标签总数">{{ overviewData.tagCount }}</el-descriptions-item>
            <el-descriptions-item label="平均文件大小">{{ formatFileSize(overviewData.avgFileSize) }}</el-descriptions-item>
            <el-descriptions-item label="最近更新时间">{{ overviewData.lastUpdateTime }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch, defineProps, nextTick, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { listDatasetVersion } from '@/api/dataset/version'
import { getDatasetAnalytics } from '@/api/dataset/index'

const props = defineProps({
  datasetId: {
    type: [String, Number],
    required: true
  }
})

// 图表引用
const versionChartRef = ref(null)
const fileTypeChartRef = ref(null)
const tagChartRef = ref(null)
const fileSizeChartRef = ref(null)

// 图表实例
let overviewChart = null
let versionChart = null
let tagChart = null
let fileSizeChart = null
let fileTypeChart = null

// 数据和选项
const versionList = ref([])
const chartOptions = reactive({
  timeRange: '30', // 默认30天
  versionId: '',
  tagType: 'all'
})

// 概览数据
const overviewData = reactive({
  versionCount: 0,
  fileCount: 0,
  totalSize: 0,
  tagCount: 0,
  avgFileSize: 0,
  lastUpdateTime: ''
})

// 获取版本列表
const getVersionList = async () => {
  try {
    const params = {
      pageNum: 1,
      pageSize: 100
    }
    
    // 模拟数据 - 当API可用时移除此部分
    const mockResponse = {
      code: 200,
      rows: [
        {
          versionId: 1,
          name: "v1.0.0",
          isCurrent: 1
        },
        {
          versionId: 2,
          name: "v1.1.0",
          isCurrent: 0
        },
        {
          versionId: 3,
          name: "v1.2.0-dev",
          isCurrent: 0
        }
      ],
      total: 3
    };
    
    // 取消注释当API可用
    // const response = await listDatasetVersion(props.datasetId, params)
    const response = mockResponse;
    
    if (response.code === 200) {
      versionList.value = response.rows || []
      
      if (versionList.value.length > 0) {
        // 设置默认选中的版本 - 兼容字符串和数字类型
        const current = versionList.value.find(v => v.isCurrent === '1' || v.isCurrent === 1)
        if (current) {
          chartOptions.versionId = current.versionId
        } else {
          chartOptions.versionId = versionList.value[0].versionId
        }
      }
    } else {
      ElMessage.error(response.msg || '获取版本列表失败')
    }
  } catch (error) {
    console.error('获取版本列表错误:', error)
    ElMessage.error('获取版本列表失败')
  }
}

// 加载数据集概览数据
const loadOverviewData = async () => {
  try {
    // Mock data for development - remove when API is available
    const mockResponse = {
      code: 200,
      data: {
        totalFiles: 120,
        totalVersions: 5,
        totalTags: 25,
        totalSize: "28.5 MB",
        created: "2023-05-15",
        lastUpdated: "2024-07-01"
      }
    };
    
    // Uncomment when API is available
    // const response = await getDatasetAnalytics(props.datasetId, 'overview')
    const response = mockResponse;
    
    if (response.code === 200) {
      Object.assign(overviewData, response.data)
    } else {
      ElMessage.error(response.msg || '获取数据集概览失败')
    }
  } catch (error) {
    console.error('获取数据集概览错误:', error)
    ElMessage.error('获取数据集概览失败')
  }
}

// 加载版本统计图表数据
const loadVersionChartData = async () => {
  try {
    // 检查图表实例是否存在
    if (!versionChart) return
    
    // 模拟数据 - 当API可用时移除此部分
    const mockResponse = {
      code: 200,
      data: {
        dates: ['2024-01', '2024-02', '2024-03', '2024-04', '2024-05', '2024-06', '2024-07'],
        counts: [2, 5, 3, 8, 4, 6, 3]
      }
    };
    
    // 取消注释当API可用
    // const response = await getDatasetAnalytics(props.datasetId, 'version', {
    //   timeRange: chartOptions.timeRange
    // })
    const response = mockResponse;
    
    if (response.code === 200 && response.data) {
      versionChart.setOption({
        xAxis: { 
          data: response.data.dates || [] 
        },
        series: [{
          data: response.data.counts || []
        }]
      })
    } else {
      ElMessage.error(response.msg || '获取版本统计数据失败')
    }
  } catch (error) {
    console.error('获取版本统计数据错误:', error)
    ElMessage.error('获取版本统计数据失败')
  }
}

// 加载文件类型分布图表数据
const loadFileTypeChartData = async () => {
  try {
    // 检查图表是否已初始化
    const fileTypeChartContainer = document.getElementById('fileTypeChart')
    if (!fileTypeChartContainer) return
    
    // 初始化文件类型图表
    if (!fileTypeChart) {
      fileTypeChart = echarts.init(fileTypeChartContainer)
      fileTypeChart.setOption({
        title: {
          text: '文件类型分布'
        },
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b} : {c} ({d}%)'
        },
        series: [{
          name: '文件类型',
          type: 'pie',
          radius: '65%',
          center: ['50%', '50%'],
          data: [],
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }]
      })
    }
    
    // 模拟数据 - 当API可用时移除此部分
    const mockResponse = {
      code: 200,
      data: {
        types: ['JPG', 'PNG', 'TIFF', 'GIF', '其他'],
        counts: [60, 35, 20, 10, 5]
      }
    };
    
    // 取消注释当API可用
    // const response = await getDatasetAnalytics(props.datasetId, 'fileType', {
    //   versionId: currentVersionId.value
    // })
    const response = mockResponse;
    
    if (response.code === 200 && response.data) {
      fileTypeChart.setOption({
        series: [{
          data: response.data.types.map((name, index) => ({
            name,
            value: response.data.counts[index]
          }))
        }]
      })
    } else {
      ElMessage.error(response.msg || '获取文件类型分布数据失败')
    }
  } catch (error) {
    console.error('获取文件类型分布数据错误:', error)
    ElMessage.error('获取文件类型分布数据失败')
  }
}

// 加载标签分布图表数据
const loadTagChartData = async () => {
  try {
    // 检查图表实例是否存在
    if (!tagChart) return
    
    // 模拟数据 - 当API可用时移除此部分
    const mockResponse = {
      code: 200,
      data: {
        labels: ['标签A', '标签B', '标签C', '标签D', '标签E'],
        values: [25, 18, 15, 10, 8]
      }
    };
    
    // 取消注释当API可用
    // const response = await getDatasetAnalytics(props.datasetId, 'tag', {
    //   tagType: chartOptions.tagType
    // })
    const response = mockResponse;
    
    if (response.code === 200 && response.data) {
      tagChart.setOption({
        series: [{
          data: response.data.labels.map((name, index) => ({
            name,
            value: response.data.values[index]
          }))
        }]
      })
    } else {
      ElMessage.error(response.msg || '获取标签分布数据失败')
    }
  } catch (error) {
    console.error('获取标签分布数据错误:', error)
    ElMessage.error('获取标签分布数据失败')
  }
}

// 加载文件规模分布图表数据
const loadFileSizeChartData = async () => {
  try {
    // 检查图表实例是否存在
    if (!fileSizeChart) return
    
    // 模拟数据 - 当API可用时移除此部分
    const mockResponse = {
      code: 200,
      data: {
        sizes: ['0-1MB', '1-5MB', '5-10MB', '10-50MB', '50MB+'],
        counts: [45, 32, 18, 5, 2]
      }
    };
    
    // 取消注释当API可用
    // const response = await getDatasetAnalytics(props.datasetId, 'fileSize')
    const response = mockResponse;
    
    if (response.code === 200 && response.data) {
      fileSizeChart.setOption({
        xAxis: { 
          data: response.data.sizes || [] 
        },
        series: [{
          data: response.data.counts || []
        }]
      })
    } else {
      ElMessage.error(response.msg || '获取文件规模分析数据失败')
    }
  } catch (error) {
    console.error('获取文件规模分析数据错误:', error)
    ElMessage.error('获取文件规模分析数据失败')
  }
}

// 渲染版本统计图表
const renderVersionChart = (data) => {
  if (!versionChart) {
    versionChart = echarts.init(versionChartRef.value)
  }
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: data.dates || [],
      axisLabel: {
        rotate: 45
      }
    },
    yAxis: {
      type: 'value',
      name: '版本数量'
    },
    series: [
      {
        name: '版本数',
        type: 'bar',
        data: data.counts || [],
        itemStyle: {
          color: '#3a8ee6'
        }
      }
    ]
  }
  
  versionChart.setOption(option)
}

// 渲染文件类型分布图表
const renderFileTypeChart = (data) => {
  if (!fileTypeChart) {
    fileTypeChart = echarts.init(fileTypeChartRef.value)
  }
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b} : {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      data: data.types || []
    },
    series: [
      {
        name: '文件类型',
        type: 'pie',
        radius: '60%',
        center: ['50%', '60%'],
        data: (data.types || []).map((type, index) => {
          return {
            name: type,
            value: data.counts ? data.counts[index] : 0
          }
        }),
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  }
  
  fileTypeChart.setOption(option)
}

// 渲染标签分布图表
const renderTagChart = (data) => {
  if (!tagChart) {
    tagChart = echarts.init(tagChartRef.value)
  }
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b} : {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      data: data.tags || []
    },
    series: [
      {
        name: '标签分布',
        type: 'pie',
        radius: '60%',
        center: ['50%', '60%'],
        data: (data.tags || []).map((tag, index) => {
          return {
            name: tag,
            value: data.counts ? data.counts[index] : 0
          }
        }),
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  }
  
  tagChart.setOption(option)
}

// 渲染文件规模分析图表
const renderFileSizeChart = (data) => {
  if (!fileSizeChart) {
    fileSizeChart = echarts.init(fileSizeChartRef.value)
  }
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: data.ranges || []
    },
    yAxis: {
      type: 'value',
      name: '文件数量'
    },
    series: [
      {
        name: '文件数量',
        type: 'bar',
        data: data.counts || [],
        itemStyle: {
          color: function(params) {
            const colorList = ['#19d4ae', '#5ab1ef', '#fa6e86', '#ffb980', '#0067a6', '#c4b4e4'];
            return colorList[params.dataIndex % colorList.length];
          }
        }
      }
    ]
  }
  
  fileSizeChart.setOption(option)
}

// 格式化文件大小
const formatFileSize = (bytes) => {
  if (bytes === 0 || bytes === undefined || bytes === null) return '0 B';
  
  const k = 1024;
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];
  
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
}

// 窗口大小变化时重绘图表
const handleResize = () => {
  if (overviewChart) overviewChart.resize()
  if (versionChart) versionChart.resize()
  if (tagChart) tagChart.resize()
  if (fileSizeChart) fileSizeChart.resize()
  if (fileTypeChart) fileTypeChart.resize()
}

// 初始化图表
const initCharts = async () => {
  try {
    // 等待DOM元素渲染完成
    await nextTick()
    
    // 确保容器元素存在
    const overviewChartContainer = document.getElementById('overviewChart')
    const versionChartContainer = document.getElementById('versionChart')
    const tagChartContainer = document.getElementById('tagChart')
    const fileSizeChartContainer = document.getElementById('fileSizeChart')
    
    // 创建图表实例前先进行检查
    if (overviewChartContainer) {
      overviewChart = echarts.init(overviewChartContainer)
    }
    
    if (versionChartContainer) {
      versionChart = echarts.init(versionChartContainer)
      
      // 设置基本配置
      versionChart.setOption({
        title: {
          text: '版本创建趋势'
        },
        tooltip: {
          trigger: 'axis'
        },
        xAxis: {
          type: 'category',
          data: []
        },
        yAxis: {
          type: 'value'
        },
        series: [{
          name: '版本数',
          type: 'line',
          data: [],
          smooth: true,
          lineStyle: {
            width: 2
          }
        }]
      })
    }
    
    if (tagChartContainer) {
      tagChart = echarts.init(tagChartContainer)
      
      // 设置基本配置
      tagChart.setOption({
        title: {
          text: '标签分布'
        },
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b} : {c} ({d}%)'
        },
        series: [{
          name: '标签数量',
          type: 'pie',
          radius: '65%',
          center: ['50%', '50%'],
          data: [],
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }]
      })
    }
    
    if (fileSizeChartContainer) {
      fileSizeChart = echarts.init(fileSizeChartContainer)
      
      // 设置基本配置
      fileSizeChart.setOption({
        title: {
          text: '文件大小分布'
        },
        tooltip: {
          trigger: 'axis'
        },
        xAxis: {
          type: 'category',
          data: []
        },
        yAxis: {
          type: 'value'
        },
        series: [{
          name: '文件数量',
          type: 'bar',
          data: [],
          barWidth: '40%'
        }]
      })
    }
    
    // 加载初始数据
    await loadOverviewData()
    await loadVersionChartData()
    await loadFileTypeChartData()
    await loadTagChartData()
    await loadFileSizeChartData()
    
    // 添加窗口大小变化的监听器
    window.addEventListener('resize', handleResize)
  } catch (error) {
    console.error('初始化图表失败:', error)
    ElMessage.error('初始化图表失败')
  }
}

// 监听datasetId变化
watch(() => props.datasetId, (newValue) => {
  if (newValue) {
    getVersionList()
    // 重新初始化图表
    initCharts()
  }
})

// 生命周期钩子
onMounted(async () => {
  await getVersionList()
  // 等待DOM更新后初始化图表
  setTimeout(() => {
    initCharts()
  }, 100)
})

// 组件卸载时清理
onUnmounted(() => {
  if (overviewChart) {
    overviewChart.dispose()
    overviewChart = null
  }
  if (versionChart) {
    versionChart.dispose()
    versionChart = null
  }
  if (tagChart) {
    tagChart.dispose()
    tagChart = null
  }
  if (fileSizeChart) {
    fileSizeChart.dispose()
    fileSizeChart = null
  }
  if (fileTypeChart) {
    fileTypeChart.dispose()
    fileTypeChart = null
  }
  
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.dataset-analytics {
  width: 100%;
}

.chart-card {
  height: 400px;
  margin-bottom: 20px;
}

.data-overview-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-container {
  height: 320px;
  width: 100%;
}

.chart {
  height: 100%;
  width: 100%;
}
</style> 