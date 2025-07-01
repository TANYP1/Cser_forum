sequenceDiagram
    participant User
    participant Frontend
    participant Backend
    participant Database
    
    User->>Frontend: 提交注册表单(用户名/邮箱/密码)
    Frontend->>Backend: 发送注册请求
    Backend->>Backend: 验证数据格式(长度/复杂度)
    Backend->>Database: 检查用户名/邮箱唯一性
    alt 已存在
        Backend-->>Frontend: 返回错误信息
    else 可用
        Backend->>Backend: 生成密码哈希(bcrypt)
        Backend->>Database: 创建用户记录
        Backend->>Backend: 生成验证邮件(可选)
        Backend-->>Frontend: 返回成功响应
        Frontend-->>User: 显示成功消息
    end