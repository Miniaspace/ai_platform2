# AI平台开发规范

## 1. 项目结构说明

### 1.1 模块说明
```
ai_platform
├── aiplatform-api        // API接口模块，提供对外REST接口
├── aiplatform-auth       // 认证授权模块，基于Spring Security
├── aiplatform-common     // 公共模块，包含工具类和通用组件
├── aiplatform-gateway    // 网关模块，基于Spring Cloud Gateway
├── aiplatform-modules    // 业务模块，包含核心业务逻辑
│   ├── aiplatform-system   // 系统管理模块
│   ├── aiplatform-dataset  // 数据集管理模块
│   ├── aiplatform-training // 模型训练模块
│   └── aiplatform-model    // 模型管理模块
└── aiplatform-ui         // 前端模块，基于Vue3
```

### 1.2 环境要求
```yaml
# 开发环境要求
Development:
  Java: "JDK 1.8"
  Maven: "3.8.0+"
  Node: "16.x"
  NPM: "8.x"
  Redis: "6.x"
  MySQL: "8.0+"
  
# 运行环境要求
Runtime:
  CPU: "8核+"
  Memory: "16GB+"
  Disk: "100GB+"
  GPU: "NVIDIA GPU with CUDA support"
```

## 1. 代码规范

### 1.1 基于RuoYi框架的代码规范

#### 1.1.1 控制器规范
```java
@RestController
@RequestMapping("/api/v1/dataset")
public class DatasetController extends BaseController {
    
    @Autowired
    private IDatasetService datasetService;
    
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('dataset:list')")
    public TableDataInfo list(Dataset dataset) {
        startPage();  // 使用RuoYi分页
        List<Dataset> list = datasetService.selectDatasetList(dataset);
        return getDataTable(list);  // 使用RuoYi返回格式
    }
    
    @PostMapping
    @Log(title = "数据集管理", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('dataset:create')")
    public AjaxResult add(@RequestBody Dataset dataset) {
        return toAjax(datasetService.insertDataset(dataset));
    }
}
```

#### 1.1.2 服务实现规范
```java
@Service
public class DatasetServiceImpl extends ServiceImpl<DatasetMapper, Dataset> 
    implements IDatasetService {
    
    @Override
    @DataSource(DataSourceType.MASTER)  // 使用RuoYi数据源注解
    public List<Dataset> selectDatasetList(Dataset dataset) {
        return baseMapper.selectDatasetList(dataset);
    }
    
    @Override
    @Transactional  // 使用Spring事务注解
    public int insertDataset(Dataset dataset) {
        dataset.setCreateBy(SecurityUtils.getUsername());  // 使用RuoYi安全工具类
        dataset.setCreateTime(DateUtils.getNowDate());    // 使用RuoYi日期工具类
        return baseMapper.insert(dataset);
    }
}
```

#### 1.1.3 实体类规范
```java
@Data
@TableName("ai_dataset")
public class Dataset extends BaseEntity {
    
    @TableId(type = IdType.AUTO)
    private Long datasetId;
    
    @NotBlank(message = "数据集名称不能为空")
    private String datasetName;
    
    @Excel(name = "数据类型")  // 使用RuoYi Excel注解
    private String dataType;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")  // 使用RuoYi日期格式
    private Date processTime;
}
```

### 1.2 前端代码规范

#### 1.2.1 Vue组件规范
```vue
<template>
  <div class="app-container">
    <!-- 使用RuoYi的布局容器 -->
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="数据集名称" prop="datasetName">
        <el-input v-model="queryParams.datasetName" placeholder="请输入数据集名称" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作按钮区域 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" 
          v-hasPermi="['dataset:create']">新增</el-button>
      </el-col>
    </el-row>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="dataList">
      <el-table-column label="数据集名称" prop="datasetName" />
      <el-table-column label="数据类型" prop="dataType" />
      <el-table-column label="创建时间" prop="createTime" width="180" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button type="text" icon="Edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['dataset:update']">修改</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 -->
    <pagination v-if="total > 0"
      v-model:total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { listDataset, addDataset } from '@/api/dataset'

// 查询参数
const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  datasetName: undefined,
  dataType: undefined
})

// 数据列表
const dataList = ref([])
const total = ref(0)
const loading = ref(false)

// 获取数据列表
const getList = async () => {
  loading.value = true
  try {
    const res = await listDataset(queryParams.value)
    dataList.value = res.rows
    total.value = res.total
  } finally {
    loading.value = false
  }
}

// 初始化
onMounted(() => {
  getList()
})
</script>
```

#### 1.2.2 API请求规范
```typescript
// api/dataset.ts
import request from '@/utils/request'

// 查询数据集列表
export function listDataset(query) {
  return request({
    url: '/api/v1/dataset/list',
    method: 'get',
    params: query
  })
}

// 新增数据集
export function addDataset(data) {
  return request({
    url: '/api/v1/dataset',
    method: 'post',
    data: data
  })
}
```

### 1.3 命名规范

#### 1.3.1 模块命名
```
aiplatform-auth        // 认证模块（复用RuoYi）
aiplatform-system     // 系统模块（复用RuoYi）
aiplatform-common     // 通用模块（复用RuoYi）
aiplatform-framework  // 框架模块（复用RuoYi）
aiplatform-dataset    // 数据集模块（新增）
aiplatform-training   // 训练模块（新增）
aiplatform-model      // 模型模块（新增）
```

#### 1.3.2 包结构
```
com.aiplatform
├── auth                // 认证相关（复用RuoYi）
├── system              // 系统相关（复用RuoYi）
├── common              // 通用功能（复用RuoYi）
├── framework           // 框架功能（复用RuoYi）
├── dataset             // 数据集管理（新增）
│   ├── controller     // 控制器
│   ├── service       // 服务
│   ├── mapper       // 数据访问
│   ├── domain       // 实体类
│   └── vo           // 视图对象
└── training           // 训练管理（新增）
    ├── controller
    ├── service
    ├── mapper
    ├── domain
    └── vo
```

#### 1.3.3 数据库表命名
```sql
-- 复用RuoYi的系统表
sys_user              -- 用户表
sys_role              -- 角色表
sys_menu              -- 菜单表
sys_dept              -- 部门表

-- 新增业务表
ai_dataset           -- 数据集表
ai_dataset_file      -- 数据集文件表
ai_training_job      -- 训练任务表
ai_model             -- 模型表
ai_model_version     -- 模型版本表
```

### 1.4 Git提交规范

#### 1.4.1 分支管理
```
master          // 主分支，用于生产环境
develop         // 开发分支，用于开发环境
feature/*       // 特性分支，用于新功能开发
hotfix/*        // 热修复分支，用于生产bug修复
release/*       // 发布分支，用于版本发布
```

#### 1.4.2 提交信息格式
```
<type>(<scope>): <subject>

type:
- feat: 新功能
- fix: 修复bug
- docs: 文档更新
- style: 代码格式（不影响代码运行的变动）
- refactor: 重构（既不是新增功能，也不是修改bug的代码变动）
- perf: 性能优化
- test: 增加测试
- chore: 构建过程或辅助工具的变动

示例：
feat(dataset): 添加数据集版本管理功能
fix(training): 修复训练任务无法停止的问题
docs(api): 更新API文档
``` 

## 2. 开发流程规范

### 2.1 开发环境搭建
```bash
# 1. 克隆项目
git clone <project-url>
cd ai_platform

# 2. 安装后端依赖
mvn clean install

# 3. 安装前端依赖
cd aiplatform-ui
npm install

# 4. 初始化数据库
mysql -u root -p < sql/aiplatform.sql

# 5. 配置开发环境
# application-dev.yml示例
server:
  port: 8080
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/aiplatform?useUnicode=true&characterEncoding=utf8
    username: root
    password: password
  redis:
    host: localhost
    port: 6379
```

### 2.2 开发流程
1. **需求分析**
   - 理解需求文档
   - 设计技术方案
   - 评估开发工时

2. **分支创建**
   ```bash
   # 从develop分支创建特性分支
   git checkout develop
   git pull
   git checkout -b feature/dataset-management
   ```

3. **编码实现**
   - 遵循代码规范
   - 编写单元测试
   - 本地测试验证

4. **代码提交**
   ```bash
   # 提交代码
   git add .
   git commit -m "feat(dataset): 实现数据集基础管理功能"
   git push origin feature/dataset-management
   ```

5. **代码评审**
   - 创建Pull Request
   - 等待评审通过
   - 合并到develop分支

### 2.3 测试规范

#### 2.3.1 单元测试
```java
@SpringBootTest
public class DatasetServiceTest {
    
    @Autowired
    private IDatasetService datasetService;
    
    @Test
    public void testCreateDataset() {
        Dataset dataset = new Dataset();
        dataset.setDatasetName("测试数据集");
        dataset.setDataType("IMAGE");
        
        int result = datasetService.insertDataset(dataset);
        Assert.assertEquals(1, result);
    }
}
```

#### 2.3.2 接口测试
```java
@SpringBootTest
@AutoConfigureMockMvc
public class DatasetControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    public void testListDatasets() throws Exception {
        mockMvc.perform(get("/api/v1/dataset/list")
                .param("pageNum", "1")
                .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
```

### 2.4 文档规范

#### 2.4.1 接口文档
```java
@Tag(name = "数据集管理")
@RestController
@RequestMapping("/api/v1/dataset")
public class DatasetController {
    
    @Operation(summary = "获取数据集列表")
    @Parameters({
        @Parameter(name = "pageNum", description = "页码", required = true),
        @Parameter(name = "pageSize", description = "每页记录数", required = true)
    })
    @GetMapping("/list")
    public TableDataInfo list(Dataset dataset) {
        // 实现代码
    }
}
```

#### 2.4.2 注释规范
```java
/**
 * 数据集服务实现类
 * 
 * @author aiplatform
 */
@Service
public class DatasetServiceImpl implements IDatasetService {
    
    /**
     * 创建数据集
     * 
     * @param dataset 数据集信息
     * @return 影响行数
     */
    @Override
    public int insertDataset(Dataset dataset) {
        // 实现代码
    }
}
```

### 2.5 安全规范

#### 2.5.1 权限控制
```java
// 使用Spring Security注解控制权限
@PreAuthorize("@ss.hasPermi('dataset:create')")
public AjaxResult createDataset(@RequestBody Dataset dataset) {
    // 实现代码
}

// 使用自定义权限注解
@DataScope(deptAlias = "d")
public List<Dataset> selectDatasetList(Dataset dataset) {
    // 实现代码
}
```

#### 2.5.2 数据加密
```java
// 敏感数据加密
@EncryptField
private String secretKey;

// 数据脱敏
@Sensitive(type = SensitiveType.PHONE)
private String phone;
```

### 2.6 日志规范

#### 2.6.1 业务日志
```java
// 使用RuoYi日志注解
@Log(title = "数据集管理", businessType = BusinessType.INSERT)
public AjaxResult add(@RequestBody Dataset dataset) {
    // 实现代码
}

// 使用日志门面
private static final Logger log = LoggerFactory.getLogger(DatasetServiceImpl.class);

public void processDataset(Dataset dataset) {
    log.info("开始处理数据集：{}", dataset.getDatasetName());
    try {
        // 处理逻辑
    } catch (Exception e) {
        log.error("处理数据集失败：", e);
        throw new ServiceException("处理数据集失败");
    }
}
```

#### 2.6.2 操作日志
```sql
-- 操作日志表
CREATE TABLE sys_oper_log (
  oper_id        bigint(20)      NOT NULL AUTO_INCREMENT    COMMENT '日志主键',
  title          varchar(50)     DEFAULT ''                 COMMENT '模块标题',
  business_type  int(2)          DEFAULT 0                  COMMENT '业务类型',
  method         varchar(100)    DEFAULT ''                 COMMENT '方法名称',
  request_method varchar(10)     DEFAULT ''                 COMMENT '请求方式',
  operator_type  int(1)          DEFAULT 0                  COMMENT '操作类别',
  oper_name      varchar(50)     DEFAULT ''                 COMMENT '操作人员',
  dept_name      varchar(50)     DEFAULT ''                 COMMENT '部门名称',
  oper_url       varchar(255)    DEFAULT ''                 COMMENT '请求URL',
  oper_ip        varchar(128)    DEFAULT ''                 COMMENT '主机地址',
  oper_param     varchar(2000)   DEFAULT ''                 COMMENT '请求参数',
  json_result    varchar(2000)   DEFAULT ''                 COMMENT '返回参数',
  status         int(1)          DEFAULT 0                  COMMENT '操作状态',
  error_msg      varchar(2000)   DEFAULT ''                 COMMENT '错误消息',
  oper_time      datetime                                   COMMENT '操作时间',
  PRIMARY KEY (oper_id)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT = '操作日志记录';
``` 