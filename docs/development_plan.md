# AI Platform 开发文档

## 一、系统架构

### 1.1 整体架构
```
                                    [ 前端层 ]
                                        ↓
                                    [ 网关层 ]
                                        ↓
                [ 业务服务层 ]                      [ 基础服务层 ]
┌──────────┬──────────┬──────────┐    ┌──────────┬──────────┐
│  数据集  │  模型    │  模型    │    │  认证    │  系统    │
│  管理    │  训练    │  部署    │    │  服务    │  管理    │
└──────────┴──────────┴──────────┘    └──────────┴──────────┘
                    ↓                           ↓
                [ 中间件层 ]                [ 存储层 ]
        ┌──────────┬──────────┐        ┌──────────┬──────────┐
        │  Nacos   │  Redis   │        │  MySQL   │  MinIO   │
        └──────────┴──────────┘        └──────────┴──────────┘
```

### 1.2 模块依赖关系
```
aiplatform-ui ──────► aiplatform-gateway
                          ↓
     ┌────────────┬───────┴───────┬────────────┐
     ↓            ↓               ↓            ↓
aiplatform-auth   ↓         aiplatform-system  ↓
     ↓            ↓               ↓            ↓
aiplatform-api ───┴───────► aiplatform-common ◄┴── aiplatform-modules
```

## 二、开发规范

### 2.1 代码规范
1. 命名规范
   - 包名：全小写，如 `com.aiplatform.dataset`
   - 类名：大驼峰，如 `DatasetController`
   - 方法名：小驼峰，如 `getDatasetById`
   - 变量名：小驼峰，如 `datasetId`
   - 常量名：全大写下划线，如 `MAX_FILE_SIZE`

2. 注释规范
   ```java
   /**
    * 类注释：说明类的功能
    * 
    * @author 作者
    * @date 2024/01/01
    */
   
   /**
    * 方法注释：说明方法的功能
    * 
    * @param param1 参数1说明
    * @return 返回值说明
    */
   ```

3. REST API规范
   - URI格式：`/api/{version}/{module}/{resource}/{action}`
   - HTTP方法：
     - GET：查询
     - POST：创建
     - PUT：更新
     - DELETE：删除

### 2.2 数据库规范
1. 命名规范
   - 表名：小写下划线，模块名_表名，如 `dataset_version`
   - 字段名：小写下划线，如 `create_time`
   - 主键：表名_id，如 `dataset_id`

2. 字段规范
   - 必须包含：`create_by`, `create_time`, `update_by`, `update_time`
   - 所有表必须有主键
   - 使用 `varchar` 代替 `char`
   - 存储精确金额使用 `decimal`

### 2.3 前端规范
1. 文件组织
   ```
   src/
   ├── api/                # API接口
   │   └── dataset/        # 按模块划分
   ├── components/         # 公共组件
   │   ├── FilePreview/    # 文件预览
   │   └── FileDiff/       # 文件对比
   ├── views/             # 页面
   │   └── dataset/       # 按模块划分
   └── store/            # 状态管理
       └── modules/      # 按模块划分
   ```

2. 组件规范
   - 文件名：大驼峰，如 `DatasetList.vue`
   - 组件名：大驼峰，如 `DatasetList`
   - Props命名：小驼峰，如 `fileList`

## 三、模块说明

### 3.1 公共模块
1. aiplatform-common
   - 公共工具类
   - 通用注解
   - 全局异常处理
   - 通用响应对象

2. aiplatform-api
   - 服务接口定义
   - 数据传输对象
   - 服务降级处理

### 3.2 业务模块

#### 3.2.1 数据集管理模块
1. 功能清单
   - [x] 数据集基础管理
   - [x] 版本管理
   - [x] 文件管理
   - [ ] 数据标注

2. 技术要点
   - MinIO文件存储
   - 版本控制实现
   - 文件预览服务

3. 配置说明
   - Nacos配置中心
   - 数据源配置
   - MinIO配置
   - Redis配置

4. 开发进度
   - 当前进度：95%
   - 详细开发日志：[数据集管理模块开发日志](./dataset/development_log.md)
   - 遗留问题：性能优化、单元测试

#### 3.2.2 模型训练模块（待开发）
1. 功能规划
   - 训练任务管理
   - 训练资源调度
   - 训练过程监控
   - 训练日志管理

2. 技术要点
   - 分布式训练支持
   - GPU资源管理
   - 训练框架集成

3. 配置要求
   - GPU服务器配置
   - 训练框架配置
   - 资源调度配置

#### 3.2.3 模型部署模块（待开发）
1. 功能规划
   - 模型部署管理
   - 服务编排
   - 性能监控
   - 版本控制

2. 技术要点
   - 容器化部署
   - 服务网格
   - 自动扩缩容

3. 配置要求
   - K8s集群配置
   - 服务网格配置
   - 监控系统配置

## 四、开发流程

### 4.1 环境准备
1. 基础环境
   - JDK 1.8+
   - Maven 3.6+
   - Node.js 16+
   - MySQL 5.7+
   - Redis 6.0+
   - MinIO

2. 中间件
   - Nacos 2.0+
   - RabbitMQ 3.8+

### 4.2 开发步骤
1. 代码生成
   ```bash
   # 使用代码生成器生成基础代码
   mvn generate-sources
   ```

2. 业务开发
   - 编写业务逻辑
   - 实现接口服务
   - 开发前端页面

3. 单元测试
   - 编写测试用例
   - 执行自动化测试
   - 生成测试报告

### 4.3 部署流程
1. 编译打包
   ```bash
   # 后端打包
   mvn clean package
   
   # 前端打包
   npm run build
   ```

2. 环境部署
   - 部署中间件服务
   - 部署应用服务
   - 配置负载均衡

3. 系统验证
   - 功能验证
   - 性能测试
   - 监控部署

## 五、API文档

### 5.1 接口规范
1. 请求格式
   ```json
   {
     "code": 200,
     "msg": "success",
     "data": {}
   }
   ```

2. 错误码
   - 200：成功
   - 401：未授权
   - 403：禁止访问
   - 404：资源不存在
   - 500：服务器错误

### 5.2 接口文档
详见各模块的API文档：
- [数据集管理API文档](./api/dataset.md)
- [模型训练API文档](./api/train.md)
- [模型部署API文档](./api/deploy.md)

## 六、运维文档

### 6.1 部署架构
```
                [ Nginx负载均衡 ]
                       ↓
         ┌─────────────────────────┐
         ↓                         ↓
  [ 应用服务器1 ]          [ 应用服务器2 ]
         ↓                         ↓
  [ 数据库主从集群 ]     [ 分布式存储集群 ]
```

### 6.2 监控告警
1. 系统监控
   - CPU使用率
   - 内存使用率
   - 磁盘使用率
   - 网络流量

2. 应用监控
   - 接口响应时间
   - 错误率
   - QPS
   - 并发数

3. 告警配置
   - 邮件告警
   - 短信告警
   - 钉钉告警

### 6.3 日志管理
1. 日志收集
   - 系统日志
   - 应用日志
   - 访问日志

2. 日志分析
   - ELK日志分析
   - 日志检索
   - 日志统计

## 七、版本历史

### 7.1 版本记录
| 版本号 | 发布日期 | 更新内容 | 负责人 |
|--------|----------|----------|--------|
| v1.0.0 | 2024-01-01 | 初始版本 | Admin |
| v1.1.0 | 2024-03-01 | 数据集管理模块 | Admin |

### 7.2 更新计划
1. v1.2.0 (2024-04)
   - 模型训练模块
   - 性能优化

2. v1.3.0 (2024-05)
   - 模型部署模块
   - 监控系统