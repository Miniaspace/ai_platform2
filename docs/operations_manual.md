# AI平台运维手册

## 1. 故障处理流程

### 1.1 服务不可用故障

#### 1.1.1 检查步骤
1. 检查服务器状态
```bash
# 检查系统资源
top
df -h
free -m

# 检查网络连接
netstat -tunlp
ping gateway-service
```

2. 检查服务进程状态
```bash
# 检查Java服务
jps -l
ps -ef | grep java

# 检查Python服务
ps -ef | grep python
nvidia-smi  # 检查GPU状态
```

3. 检查日志文件
```bash
# 检查系统日志
tail -f /var/log/syslog

# 检查应用日志
tail -f /app/aiplatform/logs/gateway-service.log
tail -f /app/aiplatform/logs/training-service.log
```

#### 1.1.2 处理方案
1. 重启服务
```bash
# 重启Java服务
systemctl restart aiplatform-gateway
systemctl restart aiplatform-auth
systemctl restart aiplatform-system

# 重启Python服务
systemctl restart aiplatform-training
systemctl restart aiplatform-dataset
```

2. 切换备用节点
```bash
# 修改负载均衡配置
vim /etc/nginx/conf.d/aiplatform.conf
nginx -s reload

# 更新DNS记录
kubectl patch svc gateway-service -p '{"spec":{"externalIPs":["NEW_IP"]}}'
```

3. 恢复数据备份
```bash
# 恢复数据库
mysql -u root -p aiplatform < backup.sql

# 恢复文件
tar -xzf backup.tar.gz -C /app/aiplatform/data/
```

### 1.2 性能故障

#### 1.2.1 检查步骤
1. 监控系统资源
```bash
# CPU使用率
top -b -n 1

# 内存使用
free -m
vmstat 1

# 磁盘IO
iostat -x 1
```

2. 检查数据库性能
```sql
-- 检查当前连接数
SHOW STATUS LIKE 'Threads_connected';

-- 检查慢查询
SHOW VARIABLES LIKE 'slow_query%';
SHOW VARIABLES LIKE 'long_query_time';

-- 查看慢查询日志
tail -f /var/log/mysql/mysql-slow.log
```

3. 检查缓存状态
```bash
# Redis状态
redis-cli info

# 缓存命中率
redis-cli info stats | grep hit_rate
```

#### 1.2.2 处理方案
1. 优化SQL
```sql
-- 添加索引
CREATE INDEX idx_dataset_name ON ai_dataset(name);
CREATE INDEX idx_training_status ON ai_training_job(status);

-- 优化查询
SELECT /*+ INDEX(d idx_dataset_name) */ 
    d.*, f.file_path
FROM ai_dataset d
LEFT JOIN ai_dataset_file f ON d.id = f.dataset_id
WHERE d.name LIKE 'test%';
```

2. 优化缓存
```java
@Configuration
public class CacheConfig {
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(30))
            .serializeKeysWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new GenericJackson2JsonRedisSerializer()));
            
        return RedisCacheManager.builder(factory)
            .cacheDefaults(config)
            .build();
    }
}
```

3. 资源扩容
```yaml
# Kubernetes资源扩容
apiVersion: apps/v1
kind: Deployment
metadata:
  name: aiplatform-training
spec:
  replicas: 3
  template:
    spec:
      containers:
      - name: training
        resources:
          requests:
            memory: "4Gi"
            cpu: "2"
          limits:
            memory: "8Gi"
            cpu: "4"
```

### 1.3 训练任务故障

#### 1.3.1 检查步骤
1. 检查GPU状态
```bash
# 查看GPU使用情况
nvidia-smi

# 查看GPU进程
nvidia-smi pmon

# 检查CUDA版本
nvcc --version
```

2. 检查训练日志
```bash
# 查看训练日志
tail -f /app/aiplatform/logs/training/job_*.log

# 查看GPU错误日志
dmesg | grep nvidia
```

3. 检查数据集
```bash
# 检查数据集完整性
md5sum -c dataset_checksums.txt

# 检查数据集权限
ls -l /app/aiplatform/data/datasets/
```

#### 1.3.2 处理方案
1. 重启训练任务
```python
def restart_training_job(job_id):
    # 1. 停止当前任务
    stop_job(job_id)
    
    # 2. 清理资源
    cleanup_resources(job_id)
    
    # 3. 重新提交任务
    submit_job(job_id)
```

2. 恢复检查点
```python
def restore_from_checkpoint(job_id):
    # 1. 获取最新检查点
    checkpoint = get_latest_checkpoint(job_id)
    
    # 2. 验证检查点完整性
    if validate_checkpoint(checkpoint):
        # 3. 恢复训练状态
        restore_training_state(checkpoint)
        
    # 4. 继续训练
    resume_training(job_id)
```

3. 切换计算节点
```python
def switch_compute_node(job_id):
    # 1. 获取可用节点
    available_nodes = get_available_nodes()
    
    # 2. 选择最优节点
    best_node = select_best_node(available_nodes)
    
    # 3. 迁移任务
    migrate_job(job_id, best_node)
```

## 2. 备份恢复流程

### 2.1 数据备份策略

#### 2.1.1 数据库备份
```yaml
database_backup:
  type: incremental
  schedule: "0 2 * * *"  # 每天凌晨2点
  retention: 30  # 保留30天
  locations:
    - /backup/db/daily
    - s3://backup/db/daily
```

#### 2.1.2 文件备份
```yaml
file_backup:
  type: full
  schedule: "0 3 * * 0"  # 每周日凌晨3点
  retention: 90  # 保留90天
  locations:
    - /backup/files/weekly
    - s3://backup/files/weekly
```

#### 2.1.3 配置备份
```yaml
config_backup:
  type: full
  trigger: on-change  # 配置变更时触发
  retention: 10  # 保留最近10个版本
  locations:
    - /backup/config/versions
    - s3://backup/config/versions
```

### 2.2 恢复流程

#### 2.2.1 系统恢复步骤
```python
def restore_system():
    """系统恢复流程"""
    try:
        # 1. 停止所有服务
        stop_all_services()
        
        # 2. 恢复数据库
        restore_database()
        
        # 3. 恢复文件
        restore_files()
        
        # 4. 恢复配置
        restore_config()
        
        # 5. 启动服务
        start_services()
        
        # 6. 验证系统
        verify_system()
        
    except Exception as e:
        # 恢复失败，回滚
        rollback_restore()
        raise
```

#### 2.2.2 验证步骤
```python
def verify_system():
    """系统验证流程"""
    # 1. 检查服务状态
    check_service_status()
    
    # 2. 验证数据完整性
    verify_data_integrity()
    
    # 3. 测试核心功能
    test_core_functions()
    
    # 4. 检查监控指标
    check_metrics()
```

## 3. 性能优化指南

### 3.1 数据库优化

#### 3.1.1 索引优化
```sql
-- 创建复合索引
CREATE INDEX idx_dataset_type_status ON ai_dataset(type, status);

-- 创建全文索引
CREATE FULLTEXT INDEX idx_dataset_name_desc 
ON ai_dataset(name, description);

-- 创建部分索引
CREATE INDEX idx_training_active 
ON ai_training_job(status) 
WHERE status IN ('running', 'pending');
```

#### 3.1.2 分区表设计
```sql
-- 创建按时间分区的表
CREATE TABLE ai_training_log_YYYYMM (
    CHECK ( created_time >= DATE '2024-01-01' 
           AND created_time < DATE '2024-02-01' )
) INHERITS (ai_training_log);

-- 创建分区触发器
CREATE OR REPLACE FUNCTION training_log_insert_trigger()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO ai_training_log_202401
    VALUES (NEW.*);
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;
```

#### 3.1.3 查询优化
```sql
-- 使用EXISTS代替IN
SELECT * FROM ai_dataset d
WHERE EXISTS (
    SELECT 1 FROM ai_training_job t 
    WHERE t.dataset_id = d.id
);

-- 使用UNION ALL代替UNION
SELECT * FROM ai_training_log_202401
UNION ALL
SELECT * FROM ai_training_log_202402;

-- 使用索引提示
SELECT /*+ INDEX(d idx_dataset_type_status) */
    d.*, f.file_path
FROM ai_dataset d
LEFT JOIN ai_dataset_file f ON d.id = f.dataset_id
WHERE d.type = 'image' AND d.status = 'active';
```

### 3.2 缓存优化

#### 3.2.1 Redis配置
```yaml
redis:
  # 内存配置
  maxmemory: 4gb
  maxmemory-policy: allkeys-lru
  
  # 持久化配置
  save:
    - 900 1    # 900秒内有1个修改
    - 300 10   # 300秒内有10个修改
    - 60 10000 # 60秒内有10000个修改
    
  # 主从配置
  replication:
    role: master
    slaves:
      - host: redis-slave-1
        port: 6379
      - host: redis-slave-2
        port: 6379
```

#### 3.2.2 缓存策略
```java
@Configuration
public class CacheConfig {
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        // 设置不同缓存空间的配置
        Map<String, RedisCacheConfiguration> configMap = new HashMap<>();
        
        // 数据集缓存配置
        configMap.put("dataset", RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofHours(1))
            .prefixCacheNameWith("ds:"));
            
        // 训练任务缓存配置
        configMap.put("training", RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(5))
            .prefixCacheNameWith("tr:"));
            
        // 模型缓存配置
        configMap.put("model", RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofDays(1))
            .prefixCacheNameWith("md:"));
            
        return RedisCacheManager.builder(factory)
            .withInitialCacheConfigurations(configMap)
            .build();
    }
}
```

## 4. 扩容方案

### 4.1 水平扩容

#### 4.1.1 Kubernetes配置
```yaml
# 网关服务扩容
apiVersion: apps/v1
kind: Deployment
metadata:
  name: aiplatform-gateway
spec:
  replicas: 3
  template:
    spec:
      containers:
      - name: gateway
        resources:
          requests:
            memory: "2Gi"
            cpu: "1"
          limits:
            memory: "4Gi"
            cpu: "2"

# 训练服务扩容
apiVersion: apps/v1
kind: Deployment
metadata:
  name: aiplatform-training
spec:
  replicas: 2
  template:
    spec:
      containers:
      - name: training
        resources:
          requests:
            memory: "8Gi"
            cpu: "4"
            nvidia.com/gpu: "1"
          limits:
            memory: "16Gi"
            cpu: "8"
            nvidia.com/gpu: "1"
```

#### 4.1.2 负载均衡配置
```nginx
# Nginx配置
upstream gateway {
    least_conn;  # 最少连接数算法
    server gateway-1:8080;
    server gateway-2:8080;
    server gateway-3:8080;
    keepalive 32;
}

upstream training {
    ip_hash;  # 会话保持
    server training-1:8000;
    server training-2:8000;
}
```

### 4.2 垂直扩容

#### 4.2.1 资源配置
```yaml
# 数据库资源
database:
  cpu: 8
  memory: 32Gi
  storage: 1Ti
  
# 计算节点资源
compute_node:
  cpu: 16
  memory: 64Gi
  gpu: 4
  storage: 2Ti
  
# 缓存资源
redis:
  cpu: 4
  memory: 16Gi
  
# 对象存储资源
minio:
  storage: 5Ti
```

#### 4.2.2 JVM配置
```bash
# 应用服务JVM配置
JAVA_OPTS="\
    -Xms4g \
    -Xmx8g \
    -XX:+UseG1GC \
    -XX:MaxGCPauseMillis=200 \
    -XX:+UseStringDeduplication \
    -XX:+HeapDumpOnOutOfMemoryError \
    -XX:HeapDumpPath=/app/logs/heapdump.hprof \
    -Xlog:gc*:/app/logs/gc.log:time,uptime:filecount=5,filesize=100m"
``` 