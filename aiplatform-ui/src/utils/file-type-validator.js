/**
 * 文件类型验证工具
 */
export class FileTypeValidator {
  // 支持的文件类型
  static SUPPORTED_TYPES = {
    // 图片
    'image/jpeg': true,
    'image/png': true,
    'image/gif': true,
    'image/bmp': true,
    'image/webp': true,
    // 文档
    'application/pdf': true,
    'application/msword': true,
    'application/vnd.openxmlformats-officedocument.wordprocessingml.document': true,
    'application/vnd.ms-excel': true,
    'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet': true,
    'application/vnd.ms-powerpoint': true,
    'application/vnd.openxmlformats-officedocument.presentationml.presentation': true,
    'text/plain': true,
    // 音频
    'audio/mpeg': true,
    'audio/wav': true,
    'audio/mp3': true,
    'audio/ogg': true,
    // 视频
    'video/mp4': true,
    'video/mpeg': true,
    'video/quicktime': true,
    'video/x-msvideo': true,
    'video/x-ms-wmv': true,
    // 压缩文件
    'application/zip': true,
    'application/x-rar-compressed': true,
    'application/x-7z-compressed': true,
    // 机器人数据
    'application/x-ros-bag': true,
    'application/x-ros-msg': true,
    'application/x-ros-launch': true,
    'application/x-pointcloud': true,
    'application/x-3d-model': true,
    'application/x-sensor-data': true,
    'application/x-config': true,
    'application/x-map': true,
    'application/x-trajectory': true,
    'application/x-calibration': true
  }

  /**
   * 验证文件类型是否支持
   * @param {string} type 文件类型
   * @returns {boolean} 是否支持
   */
  static validate(type) {
    return !!this.SUPPORTED_TYPES[type]
  }

  /**
   * 获取支持的文件类型列表
   * @returns {string[]} 支持的文件类型列表
   */
  static getSupportedTypes() {
    return Object.keys(this.SUPPORTED_TYPES)
  }

  /**
   * 获取文件类型的描述
   * @param {string} type 文件类型
   * @returns {string} 文件类型描述
   */
  static getTypeDescription(type) {
    const typeMap = {
      'image': '图片',
      'application/pdf': 'PDF文档',
      'application/msword': 'Word文档',
      'application/vnd.openxmlformats-officedocument.wordprocessingml.document': 'Word文档',
      'application/vnd.ms-excel': 'Excel表格',
      'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet': 'Excel表格',
      'application/vnd.ms-powerpoint': 'PowerPoint演示文稿',
      'application/vnd.openxmlformats-officedocument.presentationml.presentation': 'PowerPoint演示文稿',
      'text/plain': '文本文件',
      'audio': '音频文件',
      'video': '视频文件',
      'application/zip': 'ZIP压缩文件',
      'application/x-rar-compressed': 'RAR压缩文件',
      'application/x-7z-compressed': '7Z压缩文件',
      'application/x-ros-bag': 'ROS包文件',
      'application/x-ros-msg': 'ROS消息文件',
      'application/x-ros-launch': 'ROS启动文件',
      'application/x-pointcloud': '点云数据文件',
      'application/x-3d-model': '3D模型文件',
      'application/x-sensor-data': '传感器数据文件',
      'application/x-config': '配置文件',
      'application/x-map': '地图文件',
      'application/x-trajectory': '轨迹数据文件',
      'application/x-calibration': '标定数据文件'
    }

    if (type.startsWith('image/')) {
      return typeMap['image']
    }
    if (type.startsWith('audio/')) {
      return typeMap['audio']
    }
    if (type.startsWith('video/')) {
      return typeMap['video']
    }
    return typeMap[type] || '未知类型'
  }
} 