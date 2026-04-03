# quant-stock-platform

一个可直接在 IDEA 运行的 Spring Boot 量化选股项目，包含：

- 后端 Java 接口（实时行情、策略评估、回测）。
- 前端 H5 页面（自动刷新 UI）。
- LSTM 趋势预测（工程化轻量实现）。
- 策略自动调参。
- 强化学习仓位建议。
- 风控管理。

## 目录结构

```text
src/main/java/com/example/quant
├── config                  # 启动配置、种子数据
├── controller              # REST API
├── dto                     # 接口 DTO
├── entity                  # JPA 实体
├── repository              # 数据访问层
└── service
    ├── backtest            # 回测框架
    ├── indicator           # 指标计算（MA/MACD/CCI/RSI）
    ├── model               # LSTM/自动调参/RL
    └── risk                # 风控
```

## 启动

### 1) 使用默认 H2（开箱即跑）

```bash
mvn spring-boot:run
```

访问：<http://localhost:8080>

### 2) 切换 MySQL

1. 本地准备 MySQL 数据库 `quant_stock`
2. 修改 `src/main/resources/application-mysql.yml` 用户名/密码
3. 启动：

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```

## 核心策略（按你的要求）

- 日线价格在 15 日均线、20 日均线之上。
- 周线 15 均线过滤。
- 周线 MACD 红柱（日线 MACD 红/绿均可）。
- 价格在 20 日均线附近（±3% 可调）。
- CCI 与 RSI 背离信号。
- 自动调参与风险仓位联动。

## API

- `GET /api/health`
- `GET /api/recommendations`
- `GET /api/backtest`
