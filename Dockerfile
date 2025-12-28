# =============================================================================
# Spring Boot 應用程式 Dockerfile
# =============================================================================
#
# 什麼是 Docker？
#
# Docker 是一個容器化平台，可以將應用程式和其依賴打包成一個「容器」。
# 容器就像一個輕量級的虛擬機，可以在任何支援 Docker 的環境中運行。
#
# 為什麼要用 Docker？
#
# 1. 環境一致性：
#    「在我的電腦可以跑」不再是問題，容器確保所有環境都一樣。
#
# 2. 快速部署：
#    容器啟動只需要幾秒鐘，比虛擬機快很多。
#
# 3. 資源隔離：
#    每個容器都是獨立的，不會互相影響。
#
# 4. 易於擴展：
#    需要更多處理能力？多開幾個容器就好。
#
# 這個 Dockerfile 使用「多階段建構」（Multi-stage Build）：
# - 第一階段：建構應用程式（較大的映像）
# - 第二階段：只複製需要的檔案（較小的映像）
#
# 這樣最終的映像會比較小，部署更快。
#
# =============================================================================

# -----------------------------------------------------------------------------
# 第一階段：建構階段（Build Stage）
# -----------------------------------------------------------------------------
# 使用 Maven 映像來建構專案
# eclipse-temurin 是 Eclipse 基金會提供的 OpenJDK 發行版
FROM eclipse-temurin:17-jdk AS builder

# 設定工作目錄
# 後續的指令都會在這個目錄下執行
WORKDIR /app

# 複製 Maven Wrapper 和設定檔
# 先複製這些檔案可以利用 Docker 的快取機制
# 如果這些檔案沒有變更，Docker 會使用快取的層
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# 給 mvnw 執行權限（Linux 環境需要）
RUN chmod +x mvnw

# 下載依賴（這一層會被快取，加速後續建構）
# -B: 批次模式，不顯示進度
# dependency:go-offline: 下載所有依賴到本地
RUN ./mvnw dependency:go-offline -B

# 複製原始碼
COPY src src

# 建構應用程式
# -DskipTests: 跳過測試（測試應該在 CI/CD 流程中執行）
# package: 打包成 JAR 檔
RUN ./mvnw package -DskipTests

# -----------------------------------------------------------------------------
# 第二階段：執行階段（Runtime Stage）
# -----------------------------------------------------------------------------
# 使用較小的 JRE 映像（不需要 JDK 的編譯工具）
FROM eclipse-temurin:17-jre

# 設定工作目錄
WORKDIR /app

# 建立非 root 使用者（安全性最佳實踐）
# 容器不應該以 root 身份運行
RUN groupadd -r spring && useradd -r -g spring spring

# 從建構階段複製 JAR 檔
# --from=builder: 指定從哪個階段複製
COPY --from=builder /app/target/*.jar app.jar

# 切換到非 root 使用者
USER spring:spring

# 暴露應用程式的連接埠
# 這只是文件說明，實際開放需要在 docker run 時用 -p 指定
EXPOSE 8081

# 健康檢查
# Docker 會定期執行這個命令來檢查容器是否健康
# --interval: 檢查間隔
# --timeout: 超時時間
# --retries: 失敗幾次後標記為不健康
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
    CMD curl -f http://localhost:8081/actuator/health || exit 1

# JVM 調優參數
# -XX:+UseContainerSupport: 讓 JVM 正確識別容器的資源限制
# -XX:MaxRAMPercentage=75.0: 最多使用 75% 的可用記憶體
# -Djava.security.egd: 加速隨機數產生
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

# 啟動應用程式
# 使用 exec 格式（不是 shell 格式），讓 Java 成為 PID 1
# 這樣 Docker 的信號（如 SIGTERM）可以正確傳遞給 Java
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
