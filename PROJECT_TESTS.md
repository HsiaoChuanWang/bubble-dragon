# Bubble Dragon 測試指南

本文件說明 Bubble Dragon 目前已有的自動化測試、執行方式、建議補強項目，以及發布前的人工遊玩檢查流程。

本專案使用：

- Java 21
- Maven
- JUnit 5
- Maven Surefire Plugin

## 目前的測試狀態

目前專案有 **2 個測試類別、共 5 個 Unit Test**，尚未導入 JavaFX UI 自動化測試或完整 E2E 測試。

測試原始碼位於：

```text
src/test/java/
├─ com/bubble/dragon/physics/OverlapCheckerTest.java
└─ com/bubble/dragon/util/CountdownTimerTest.java
```

### `OverlapCheckerTest`

測試遊戲物件的矩形碰撞判定：

| 測試方法 | 驗證內容 |
| --- | --- |
| `detectsOverlap` | 兩個矩形重疊時回傳 `true` |
| `rejectsSeparatedObjects` | 兩個矩形分離時回傳 `false` |
| `touchingEdgesIsNotOverlap` | 只接觸邊界時不視為重疊 |

### `CountdownTimerTest`

測試倒數計時器的狀態與時間控制：

| 測試方法 | 驗證內容 |
| --- | --- |
| `finishesAfterDuration` | 經過指定時間後完成並停止執行 |
| `resetRestoresDuration` | 重設後恢復初始秒數並停止執行 |

## 執行測試

以下指令都應在專案根目錄執行。

執行全部測試：

```powershell
mvn test
```

清除舊建置結果後執行全部測試：

```powershell
mvn clean test
```

執行完整 Maven 驗證流程：

```powershell
mvn clean verify
```

提交程式碼前建議使用 `mvn clean verify`，確保主程式與測試程式都能從乾淨狀態重新編譯，並且所有測試通過。

### 執行單一測試類別

```powershell
mvn -Dtest=CountdownTimerTest test
mvn -Dtest=OverlapCheckerTest test
```

### 執行單一測試方法

PowerShell 中建議將含有 `#` 的參數加上雙引號：

```powershell
mvn "-Dtest=CountdownTimerTest#finishesAfterDuration" test
mvn "-Dtest=OverlapCheckerTest#touchingEdgesIsNotOverlap" test
```

### 查看測試報告

Surefire 產生的文字與 XML 報告位於：

```text
target\surefire-reports\
```

測試失敗時，先查看終端機中的第一個失敗項目，再到此資料夾查看對應測試類別的詳細輸出。

## 本專案的測試分類

### Unit Test

Unit Test 單獨驗證一小段遊戲邏輯，不啟動 JavaFX 視窗，也不需要真的遊玩。

目前的 `OverlapCheckerTest` 與 `CountdownTimerTest` 都屬於 Unit Test。未來的 `PlayerTest`、`BubbleTest`、`EnemyTest` 與 `BossTest` 也適合寫成 Unit Test。

### Integration Test

Integration Test 驗證多個元件或真實資源能否正確合作。

本專案最適合先加入的 Integration Test 是 `LevelLoaderTest`：

```text
LevelLoader → Jackson → classpath JSON → GameMap
```

它會實際讀取 `src/main/resources/maps` 中的關卡 JSON，因此能發現資源遺失、JSON 格式錯誤、欄位對應失敗等問題。

### E2E 與 UI 測試

目前沒有自動化 E2E 測試。完整 E2E 需要從 JavaFX 視窗操作首頁、故事、關卡與遊戲結束畫面，通常還要導入 TestFX，並處理動畫、時間、音效及無頭執行環境。

對目前規模而言，核心規則使用 Unit／Integration Test，完整遊戲流程使用發布前人工測試，成本與效益較合理。

## 建議新增的自動化測試

以下項目是建議方向，**目前尚未實作**。

### 第一優先：`LevelLoaderTest`

建議路徑：

```text
src/test/java/com/bubble/dragon/map/LevelLoaderTest.java
```

建議驗證：

- `/maps/level1.json` 可以成功載入。
- `/maps/level2.json` 可以成功載入。
- 第一關包含 7 個地磚與 3 個敵人。
- 第二關包含 7 個地磚與 4 個敵人。
- 玩家起始位置符合 JSON。
- 地磚貼圖清單與碰撞資料不是空值。
- 載入不存在的資源時拋出 `IllegalArgumentException`。

這是目前最值得新增的測試，因為遊戲執行時高度依賴兩個關卡 JSON。

### 第二優先：`PlayerTest`

建議路徑：

```text
src/test/java/com/bubble/dragon/entity/player/PlayerTest.java
```

建議驗證：

- 初始生命值為 3，狀態為 `IDLE`。
- 第一次受傷後生命值減少並進入無敵時間。
- 無敵時間內重複受傷不會再次扣血。
- 無敵時間結束後可以再次受傷。
- 生命值歸零時狀態切換為 `DEAD`。
- 生命值不會低於零。
- 面向方向與著地狀態可以正確更新。

### 第三優先：`BubbleTest`

建議路徑：

```text
src/test/java/com/bubble/dragon/entity/weapon/BubbleTest.java
```

建議驗證：

- 新泡泡處於 `HORIZONTAL` 狀態且為有效狀態。
- `startRising()` 將狀態改為 `RISING`，水平速度歸零。
- `stopAtTop()` 將狀態改為 `STOPPED_AT_TOP`，速度全部歸零。
- `deactivate()` 後泡泡不再有效。
- 捕捉敵人後尺寸、中心位置、移動狀態與速度正確。
- 重複套用抖動偏移不會造成位置持續漂移。

### 第四優先：`EnemyTest`

建議路徑：

```text
src/test/java/com/bubble/dragon/entity/enemy/EnemyTest.java
```

建議驗證：

- 建構時方向會正規化為 `1` 或 `-1`。
- `reverse()` 正確反轉方向。
- `face()` 會依目標相對位置調整方向。
- `JUMP` 與 `FLY` 狀態可以被捕捉。
- 捕捉後狀態為 `TRAPPED`、速度歸零並啟動計時器。
- `escape()` 後回到 `JUMP` 並重設追逐與計時狀態。
- `defeat()` 後狀態為 `DEFEATED`。

### 第五優先：`BossTest`

建議路徑：

```text
src/test/java/com/bubble/dragon/entity/boss/BossTest.java
```

建議驗證：

- 初始生命值為 5，狀態為 `INACTIVE`。
- 方向永遠正規化為 `1` 或 `-1`。
- 有效傷害會扣除生命值並開啟無敵時間。
- 無敵時間內不會重複受傷。
- 第五次有效傷害後切換成 `DEFEATED`。
- 被擊敗後水平與垂直速度皆為零。
- 被擊敗後不能再次受到傷害。

### 最後考慮：`GameControllerTest`

`GameController` 同時管理輸入、關卡、碰撞、泡泡、敵人、Boss、轉場及勝敗結果，測試範圍較大，而且部分邏輯依賴 JavaFX 的 `KeyCode`。

若要測試，建議先把勝敗判定或關卡完成規則抽成不依賴 JavaFX 的獨立類別，再驗證：

- 玩家生命值歸零時只回報一次失敗。
- 第一關敵人全滅後啟動關卡轉場。
- 第二關普通敵人與 Boss 全滅後顯示出口。
- 玩家接觸出口後只回報一次勝利。

不建議為了測試而直接存取 private 欄位或大量使用反射。

## 撰寫測試的基本格式

測試類別應放在與被測類別相同的 package 結構下，檔名以 `Test` 結尾：

```java
package com.bubble.dragon.entity.player;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PlayerTest {

    @Test
    void firstDamageReducesHpAndStartsInvulnerability() {
        Player player = new Player(80, 550);

        player.damage();

        assertEquals(2, player.getHp());
        assertTrue(player.isInvulnerable());
    }
}
```

建議採用 Arrange、Act、Assert 三段概念：

1. Arrange：建立測試資料與物件。
2. Act：執行要測試的行為。
3. Assert：確認實際結果。

測試方法名稱應描述行為與結果，例如：

```text
firstDamageReducesHpAndStartsInvulnerability
missingLevelThrowsIllegalArgumentException
touchingEdgesIsNotOverlap
```

每個測試只聚焦一個明確行為，並避免依賴其他測試的執行順序。

## 發布前人工測試清單

目前沒有 JavaFX E2E，因此每次準備發布時應執行以下人工檢查。

### 建置檢查

- [ ] 執行 `mvn clean verify` 並確認所有測試通過。
- [ ] 執行 `mvn clean javafx:jlink` 並確認成功產生 Runtime。
- [ ] 使用 `target\BubbleDragonRuntime\bin\BubbleDragon.bat` 啟動打包後版本。
- [ ] 確認打包後版本不依賴開發環境中的檔案。

### 畫面與資源

- [ ] 首頁背景、標題、按鈕與圖示正常顯示。
- [ ] 故事圖片與切換動畫正常。
- [ ] 自訂字型成功載入，中文沒有亂碼。
- [ ] 第一關與第二關地圖、角色、敵人、Boss、泡泡及出口圖片正常。
- [ ] 背景音樂與各種音效可以播放，沒有找不到媒體資源的錯誤。

### 第一關流程

- [ ] 左右移動、跳躍與吹泡泡操作正常。
- [ ] 玩家不會穿過實心地磚。
- [ ] 泡泡可以捕捉敵人。
- [ ] 被捕捉的敵人可以被擊敗或在倒數後掙脫。
- [ ] 玩家接觸敵人時正確扣血，無敵時間內不會連續扣血。
- [ ] 所有敵人被擊敗後正確進入第二關。

### 第二關與 Boss

- [ ] 轉場完成後 Boss 會依延遲正常登場。
- [ ] Boss 巡邏、追逐、俯衝與發射泡泡行為正常。
- [ ] Boss 受傷後有無敵時間，不會被同一批泡泡瞬間扣完生命值。
- [ ] Boss 生命值歸零後停止移動並進入擊敗狀態。
- [ ] 普通敵人和 Boss 全部被擊敗後才顯示出口。
- [ ] 玩家碰到出口後顯示勝利畫面。

### 失敗與重新開始

- [ ] 玩家生命值歸零後顯示失敗畫面。
- [ ] 勝利或失敗結果只觸發一次。
- [ ] 從遊戲結束畫面重新開始時，生命值、關卡、敵人、Boss、泡泡與計時狀態都正確重設。
- [ ] 返回首頁後可以再次正常開始遊戲。

## 建議的日常流程

修改單一規則時，先執行相關測試：

```powershell
mvn -Dtest=OverlapCheckerTest test
```

完成一組修改後：

```powershell
mvn test
```

提交或發布前：

```powershell
mvn clean verify
```

發布前再依人工測試清單完整遊玩一次。若未來遊戲畫面、流程或平台數量大幅增加，再評估導入 TestFX 做少量關鍵 E2E 測試。

## 測試執行問題

### Maven 無法下載測試或建置外掛

若出現以下訊息：

```text
PKIX path building failed
certificate_unknown
```

代表 Java 無法信任 Maven Central 的 TLS 憑證鏈，通常與公司代理伺服器、防毒軟體的 HTTPS 掃描、JDK truststore 或 Maven mirror 設定有關，並非 JUnit 測試失敗。

請先確認：

```powershell
java --version
mvn --version
```

兩者都應使用 JDK 21，再檢查 Maven 的 `settings.xml`、代理設定與 JDK 憑證。不要使用停用 TLS 憑證驗證的方式當作長期解法。

### 測試通過但遊戲仍無法啟動

目前自動化測試只涵蓋碰撞與計時器，並不代表 JavaFX、圖片、音效、關卡流程都正常。請另外執行：

```powershell
mvn javafx:run
```

並完成必要的人工遊玩檢查。
