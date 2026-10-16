# Bubble Dragon 專案指令

這份文件整理 Bubble Dragon 實際會用到的開發、測試與打包指令。本專案使用 Java 21、Maven、JavaFX 21、JUnit 5，以下指令預設在專案根目錄執行。

## 環境需求

- JDK 21（必須是 JDK，不能只有 JRE）
- Maven 3.9 以上
- Windows 10／11（遊戲開發不限 Windows，但本文件的安裝檔指令以 Windows 為主）
- WiX Toolset 3.x（只有建立 Windows `.exe` 安裝程式時需要）

確認環境：

```powershell
java --version
javac --version
mvn --version
jpackage --version
```

`java`、`javac` 與 Maven 顯示的 Java 版本都應為 21。`jpackage` 隨 JDK 提供；若找不到，請確認 `JAVA_HOME` 與 `PATH` 指向 JDK 21。

## 常用指令速查

| 指令 | 用途 |
| --- | --- |
| `mvn javafx:run` | 啟動遊戲 |
| `mvn clean javafx:run` | 清除舊產物後啟動遊戲 |
| `mvn compile` | 編譯主程式 |
| `mvn test` | 執行全部自動化測試（Unit 與 Integration Test） |
| `mvn clean verify` | 乾淨編譯並執行所有驗證，提交前建議使用 |
| `mvn clean javafx:jlink` | 建立自包含的 Java Runtime 與 ZIP |
| `mvn dependency:tree` | 顯示 Maven 相依套件樹 |
| `mvn clean` | 刪除 `target` 中的建置產物 |

## 啟動遊戲

一般開發時直接啟動：

```powershell
mvn javafx:run
```

若懷疑 `target` 中有舊的 class 或資源，先清除再啟動：

```powershell
mvn clean javafx:run
```

遊戲操作：

- 左、右方向鍵：移動
- `Space`：跳躍
- `Z`：吹泡泡
- `Esc`：從遊戲畫面返回首頁

## 編譯與驗證

只編譯主程式：

```powershell
mvn compile
```

編譯主程式與測試程式，但不執行測試：

```powershell
mvn test-compile -DskipTests
```

執行乾淨的完整驗證：

```powershell
mvn clean verify
```

`verify` 會重新編譯程式、複製 `src/main/resources` 資源，並執行 JUnit 測試。這是提交程式碼前最實用的檢查指令。

## 自動化測試

執行全部測試：

```powershell
mvn test
```

執行單一測試類別：

```powershell
mvn -Dtest=CountdownTimerTest test
mvn -Dtest=OverlapCheckerTest test
mvn -Dtest=PlayerTest test
mvn -Dtest=EnemyTest test
mvn -Dtest=BubbleTest test
mvn -Dtest=BossTest test
mvn -Dtest=LevelLoaderTest test
```

執行單一測試方法：

```powershell
mvn "-Dtest=CountdownTimerTest#finishesAfterDuration" test
```

PowerShell 中建議把含 `#` 的參數加上雙引號，避免解析問題。若方法名稱不同，請改成測試類別內實際的名稱。

測試報告位於：

```text
target\surefire-reports\
```

使用 PowerShell 查看所有測試摘要：

```powershell
Get-Content target\surefire-reports\*.txt
```

## 建立可攜式 Runtime

建立內含 Java、JavaFX 與遊戲模組的自包含 Runtime：

```powershell
mvn clean javafx:jlink
```

產物：

```text
target\BubbleDragonRuntime\
target\BubbleDragonRuntime.zip
```

直接啟動產生的 Runtime：

```powershell
.\target\BubbleDragonRuntime\bin\BubbleDragon.bat
```

這個版本不要求玩家另外安裝 Java。若只需要交付可解壓縮執行的版本，通常直接提供 `BubbleDragonRuntime.zip` 即可。

## 建立 Windows 可攜式應用程式

先建立 Runtime：

```powershell
mvn clean javafx:jlink
```

再建立含 `.exe` 啟動器的 app image：

```powershell
jpackage `
  --type app-image `
  --name BubbleDragon `
  --app-version 1.0.0 `
  --dest "target\portable" `
  --runtime-image "target\BubbleDragonRuntime" `
  --module "com.bubble.dragon/com.bubble.dragon.Main" `
  --icon "src\main\resources\images\BubbleDragon.ico"
```

啟動程式位於：

```text
target\portable\BubbleDragon\BubbleDragon.exe
```

壓縮整個應用程式資料夾供他人下載：

```powershell
Compress-Archive `
  -Path "target\portable\BubbleDragon" `
  -DestinationPath "target\BubbleDragon-portable-1.0.0.zip" `
  -Force
```

不可只複製 `BubbleDragon.exe`；同一資料夾內的 `app`、`runtime` 等內容也必須一起保留。

## 建立 Windows EXE 安裝程式

此步驟需要 WiX Toolset 3.x，並且 `candle.exe`、`light.exe` 可由 `PATH` 找到。

先建立 Runtime：

```powershell
mvn clean javafx:jlink
```

建立安裝程式：

```powershell
jpackage `
  --type exe `
  --name BubbleDragon `
  --app-version 1.0.0 `
  --description "Bubble Dragon Game" `
  --vendor "BubbleDragon" `
  --dest "target\installer-v1.0.0" `
  --runtime-image "target\BubbleDragonRuntime" `
  --module "com.bubble.dragon/com.bubble.dragon.Main" `
  --icon "src\main\resources\images\BubbleDragon.ico" `
  --win-upgrade-uuid "8f8a5f1e-3f82-4a0d-b67e-91b5d2cce741" `
  --win-shortcut `
  --win-menu `
  --win-dir-chooser `
  --win-per-user-install
```

產物預期位於：

```text
target\installer-v1.0.0\BubbleDragon-1.0.0.exe
```

發布新版時同步修改 `--app-version` 與輸出資料夾，例如改為 `1.0.1`；同一產品後續版本應繼續使用相同的 `--win-upgrade-uuid`，Windows 才能正確辨識升級關係。

## Maven 相依套件

查看完整相依樹：

```powershell
mvn dependency:tree
```

檢查是否有可更新版本：

```powershell
mvn versions:display-dependency-updates
mvn versions:display-plugin-updates
```

後兩個指令首次執行時，Maven 會自動下載 Versions Maven Plugin。更新 `pom.xml` 後，應重新執行：

```powershell
mvn clean verify
mvn javafx:run
```

## 清理建置產物

清除 Maven、jlink 與放在 `target` 內的 jpackage 產物：

```powershell
mvn clean
```

`mvn clean` 會刪除整個 `target`，因此其中手動放入、尚未備份的檔案也會消失。所有原始碼與遊戲資源都應放在 `src`，不要放在 `target`。

## 常見問題排查

### Maven 使用到錯誤的 Java 版本

```powershell
mvn --version
```

確認輸出中的 `Java version` 為 21。若不是，修正 `JAVA_HOME` 後開啟新的終端機。

### 修改圖片、音效、CSS 或關卡 JSON 後沒有生效

```powershell
mvn clean javafx:run
```

資源來源目錄為 `src\main\resources`；`target\classes` 只是 Maven 複製出的建置結果，不應直接修改。

### jlink 或 jpackage 找不到主模組

本專案的模組與主類別必須保持為：

```text
com.bubble.dragon/com.bubble.dragon.Main
```

相關設定位於 `src\main\java\module-info.java` 與 `pom.xml` 的 `javafx-maven-plugin`。

### 快速確認問題是否來自舊建置結果

```powershell
mvn clean verify
mvn javafx:run
```

若測試通過但遊戲啟動失敗，優先查看終端機中的 JavaFX、媒體檔或資源路徑錯誤。

## 建議工作流程

日常開發：

```powershell
mvn javafx:run
mvn test
```

提交程式碼前：

```powershell
mvn clean verify
```

準備可攜式發布版本：

```powershell
mvn clean verify
mvn javafx:jlink
.\target\BubbleDragonRuntime\bin\BubbleDragon.bat
```

最後一行會實際啟動打包後版本，請手動確認首頁、音效、兩個關卡以及遊戲結束畫面都能正常載入。
