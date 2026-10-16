# Bubble Dragon 測試指南

本文件記錄 Bubble Dragon 目前已實作的自動化測試、執行方式、測試報告，以及發布前的人工測試流程。

本專案使用：

- Java 21
- Maven
- JUnit 5
- Maven Surefire Plugin

## 測試現況

目前共有 **7 個測試類別、49 個測試方法**：

| 類型 | 測試類別 | 測試數量 | 主要範圍 |
| --- | --- | ---: | --- |
| Unit Test | `CountdownTimerTest` | 5 | 倒數、停止、完成、重設與時間下限 |
| Unit Test | `OverlapCheckerTest` | 8 | AABB 重疊、邊界、方向、座標與對稱性 |
| Unit Test | `PlayerTest` | 6 | 初始狀態、傷害、無敵時間與死亡 |
| Unit Test | `EnemyTest` | 8 | 方向、面向、捕捉、掙脫與擊敗 |
| Unit Test | `BubbleTest` | 8 | 生命週期、移動、捕捉、尺寸與抖動 |
| Unit Test | `BossTest` | 9 | 方向、傷害、無敵時間與擊敗狀態 |
| Integration Test | `LevelLoaderTest` | 5 | Jackson、關卡 JSON 與 `GameMap` 整合 |
| **合計** | **7 個類別** | **49** | |

測試原始碼位於：

```text
src/test/java/com/bubble/dragon/
├─ entity/
│  ├─ boss/BossTest.java
│  ├─ enemy/EnemyTest.java
│  ├─ player/PlayerTest.java
│  └─ weapon/BubbleTest.java
├─ map/LevelLoaderTest.java
├─ physics/OverlapCheckerTest.java
└─ util/CountdownTimerTest.java
```

目前尚未導入 TestFX、JavaFX UI 自動化測試或完整 E2E 測試。

## 執行測試

以下指令都應在專案根目錄執行。

### 執行全部測試

```powershell
mvn test
```

### 清除舊產物後執行全部測試

```powershell
mvn clean test
```

### 執行完整驗證

```powershell
mvn clean verify
```

提交或發布前應使用 `mvn clean verify`。成功時終端機會顯示：

```text
Tests run: 49, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

### 執行單一測試類別

```powershell
mvn -Dtest=CountdownTimerTest test
mvn -Dtest=OverlapCheckerTest test
mvn -Dtest=PlayerTest test
mvn -Dtest=EnemyTest test
mvn -Dtest=BubbleTest test
mvn -Dtest=BossTest test
mvn -Dtest=LevelLoaderTest test
```

### 執行單一測試方法

PowerShell 中應將含有 `#` 的參數加上雙引號：

```powershell
mvn "-Dtest=CountdownTimerTest#finishesAfterDuration" test
mvn "-Dtest=PlayerTest#firstDamageReducesHpAndStartsInvulnerability" test
```

## 測試報告

Surefire 報告位於：

```text
target\surefire-reports\
```

每個測試類別會產生兩種報告：

- `.txt`：適合直接閱讀的摘要。
- `.xml`：包含各測試方法結果，供 CI 或報告工具使用。

查看所有文字報告：

```powershell
Get-Content target\surefire-reports\*.txt
```

查看單一測試報告：

```powershell
Get-Content target\surefire-reports\com.bubble.dragon.entity.player.PlayerTest.txt
```

成功報告會顯示：

```text
Failures: 0, Errors: 0, Skipped: 0
```

其中：

- `Failures`：程式有執行，但 assertion 的實際結果不符合預期。
- `Errors`：測試執行時發生未預期例外。
- `Skipped`：測試被略過，沒有實際執行。

## 各測試涵蓋範圍

### `CountdownTimerTest`

- 尚未啟動時不會倒數。
- 停止後不會繼續倒數。
- 到達指定時間後完成並停止。
- 剩餘時間不會低於零。
- 重設後恢復初始時間並可重新啟動。

### `OverlapCheckerTest`

- 一般重疊與完全分離。
- 一個矩形包含另一個矩形。
- 從左、右、上、下四個方向重疊。
- 邊界或角落接觸不算重疊。
- 負數與小數座標。
- 交換矩形輸入順序後結果相同。

### `PlayerTest`

- 初始 HP、狀態、面向與無敵狀態。
- 第一次受傷會扣除 HP 並開啟無敵時間。
- 無敵期間不會重複扣血。
- 無敵結束後可以再次受傷。
- HP 歸零時切換為 `DEAD`。
- 死亡後 HP 不會低於零。

### `EnemyTest`

- 方向正規化、反轉與朝向目標。
- `JUMP` 和 `FLY` 狀態可以被捕捉。
- 捕捉後停止移動並啟動掙脫計時器。
- 不允許捕捉不符合條件的敵人。
- 掙脫後回到跳躍狀態並重設暫時資料。
- 被擊敗後停止計時且不能再被捕捉。

### `BubbleTest`

- 初始移動狀態與有效狀態。
- 存在時間累加與停用。
- 上升與到達頂端後停止。
- 捕捉並保存敵人。
- 捕捉後改變尺寸但保持中心點。
- 抖動偏移不會累積造成漂移。

### `BossTest`

- 初始 HP 與狀態。
- 方向正規化、反轉與朝向目標。
- 有效傷害會扣除 HP 並開啟無敵時間。
- 無敵期間忽略重複傷害。
- 無敵結束後可以再次受到傷害。
- 第五次有效傷害後被擊敗並停止移動。
- 被擊敗後不能再次受傷，HP 不會低於零。

### `LevelLoaderTest`

這是 Integration Test，實際驗證以下元件的合作：

```text
LevelLoader → Jackson → classpath JSON → GameMap
```

涵蓋內容：

- 第一關與第二關可以成功載入。
- 玩家起始座標符合 JSON。
- 地磚與敵人數量正確。
- 關卡貼圖和地磚資料完整。
- 載入不存在的關卡時拋出 `IllegalArgumentException`。

## 自動化測試的限制

目前測試主要保護資料與遊戲規則，不會啟動 JavaFX 視窗，因此下列項目仍需人工確認：

- 首頁、故事、遊戲和結束畫面的外觀。
- 圖片、自訂字型和 CSS 是否正確載入。
- 背景音樂和音效是否正常播放。
- 動畫速度與實際遊玩手感。
- 第一關、轉場、第二關和 Boss 戰的完整流程。
- 打包後 Runtime 是否能在沒有開發環境的電腦執行。

## 發布前人工測試清單

### 建置

- [ ] 執行 `mvn clean verify`，確認 49 個測試全部通過。
- [ ] 執行 `mvn clean javafx:jlink`，確認成功產生 Runtime。
- [ ] 使用 `target\BubbleDragonRuntime\bin\BubbleDragon.bat` 啟動打包後版本。

### 畫面與資源

- [ ] 首頁、故事、第一關、第二關及遊戲結束畫面正常顯示。
- [ ] 圖片、CSS 和中文字型沒有遺失或亂碼。
- [ ] 背景音樂、吹泡泡、受傷及 Boss 音效正常。

### 遊戲流程

- [ ] 左右移動、跳躍和吹泡泡操作正常。
- [ ] 玩家與地磚的碰撞正常，不會穿過平台。
- [ ] 泡泡可以捕捉敵人，敵人可被擊敗或掙脫。
- [ ] 玩家受傷與無敵時間表現正常。
- [ ] 第一關敵人全滅後正確進入第二關。
- [ ] Boss 登場、攻擊、受傷與擊敗流程正常。
- [ ] 第二關所有敵人和 Boss 被擊敗後才顯示出口。
- [ ] 接觸出口後顯示勝利畫面。
- [ ] HP 歸零後顯示失敗畫面。
- [ ] 重新開始會重設所有遊戲狀態。

## 建議工作流程

修改單一類別時先執行對應測試，例如：

```powershell
mvn -Dtest=PlayerTest test
```

完成一組修改後執行：

```powershell
mvn test
```

提交或發布前執行：

```powershell
mvn clean verify
```

自動化測試通過後，仍需依發布前人工測試清單完整遊玩一次。

## 常見問題

### `mvn clean` 無法刪除打包後的 EXE

先關閉執行中的遊戲，再移除 `target` 內檔案的唯讀屬性：

```powershell
attrib -R "target\*" /S /D
mvn clean verify
```

### 出現 `PKIX path building failed`

這代表 Java 無法信任 Maven Central 的 TLS 憑證鏈，測試通常尚未開始。請檢查 JDK truststore、Maven proxy／mirror 設定，以及防毒軟體或公司網路的 HTTPS 掃描。

### 測試通過但遊戲無法啟動

目前自動化測試不涵蓋 JavaFX UI、圖片及音效。請執行：

```powershell
mvn javafx:run
```

再檢查終端機中的 JavaFX、媒體檔或資源路徑錯誤。
