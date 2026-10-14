# Bubble Dragon 必要 Unit Test 清單

按照下列順序補齊測試。測試程式完成後，將 `[ ]` 改成 `[x]`；提交前仍須確認該項測試與全部既有測試皆通過，再使用列出的 commit message 提交。

執行單一測試類別：

```powershell
mvn -Dtest=測試類別名稱 test
```

提交前執行全部驗證：

```powershell
mvn clean verify
```

## 必要測試

- [x] 1. 補強 `CountdownTimerTest`

  完成紀錄：測試程式已完成。執行驗證時受本機 Maven Central 的 `PKIX path building failed` 憑證錯誤阻擋，修復 Maven 憑證環境後仍須重新執行下方測試指令。

  必須測試：

  - 尚未啟動時，`update()` 不會減少剩餘時間。
  - `stop()` 後，`update()` 不會繼續倒數。
  - 更新時間超過剩餘時間時，剩餘秒數不會低於零。
  - 倒數完成後，狀態為 finished 且不再 running。
  - `reset()` 後恢復初始秒數，並且可以再次啟動。

  測試指令：

  ```powershell
  mvn -Dtest=CountdownTimerTest test
  ```

  Commit：

  ```text
  test: expand countdown timer unit tests
  ```

- [x] 2. 補強 `OverlapCheckerTest`

  完成紀錄：已補齊包含、四方向重疊、角落接觸、負數／小數座標及輸入順序對稱性測試。仍須在 Maven 憑證環境恢復後執行下方指令確認。

  必須測試：

  - 一個矩形完全包含另一個矩形時視為重疊。
  - 從左、右、上、下四個方向重疊時皆回傳 `true`。
  - 只有邊界或角落接觸時回傳 `false`。
  - 使用負座標或小數座標時仍能正確判定。
  - 交換兩個矩形的輸入順序後，結果保持相同。

  測試指令：

  ```powershell
  mvn -Dtest=OverlapCheckerTest test
  ```

  Commit：

  ```text
  test: expand overlap checker edge cases
  ```

- [x] 3. 新增 `PlayerTest`

  完成紀錄：已加入初始狀態、受傷、無敵期間、無敵結束、死亡狀態及 HP 下限測試。仍須在 Maven 憑證環境恢復後執行下方指令確認。

  建議檔案：

  ```text
  src/test/java/com/bubble/dragon/entity/player/PlayerTest.java
  ```

  必須測試：

  - 初始 HP 為 3、狀態為 `IDLE`，並且面向右方。
  - 第一次受傷後 HP 減少 1，並進入無敵狀態。
  - 無敵期間再次受傷不會扣除 HP。
  - 無敵時間結束後可以再次受傷。
  - HP 歸零時狀態切換為 `DEAD`。
  - 死亡後再次呼叫 `damage()`，HP 不會低於零。

  測試指令：

  ```powershell
  mvn -Dtest=PlayerTest test
  ```

  Commit：

  ```text
  test: add player state and damage unit tests
  ```

- [ ] 4. 新增 `EnemyTest`

  建議檔案：

  ```text
  src/test/java/com/bubble/dragon/entity/enemy/EnemyTest.java
  ```

  必須測試：

  - 建構時將方向正規化為 `1` 或 `-1`。
  - `reverse()` 正確反轉方向。
  - `face()` 會根據目標相對位置改變方向。
  - `JUMP` 和 `FLY` 狀態可以被捕捉。
  - 捕捉後狀態為 `TRAPPED`、速度歸零且掙脫計時器啟動。
  - 已捕捉、停止或擊敗的敵人不能再次被捕捉。
  - `escape()` 後回到 `JUMP`，並重設追逐及計時狀態。
  - `defeat()` 後狀態為 `DEFEATED`，並停止計時器。

  測試指令：

  ```powershell
  mvn -Dtest=EnemyTest test
  ```

  Commit：

  ```text
  test: add enemy state transition unit tests
  ```

- [ ] 5. 新增 `BubbleTest`

  建議檔案：

  ```text
  src/test/java/com/bubble/dragon/entity/weapon/BubbleTest.java
  ```

  必須測試：

  - 新泡泡處於 `HORIZONTAL` 狀態、仍有效且沒有困住敵人。
  - `updateAge()` 正確累加存在時間。
  - `deactivate()` 將泡泡設為無效。
  - `startRising()` 將狀態改為 `RISING`、水平速度歸零並設定垂直速度。
  - `stopAtTop()` 將狀態改為 `STOPPED_AT_TOP`，且兩個方向的速度皆歸零。
  - `trap()` 正確保存敵人並切換移動狀態。
  - 捕捉敵人後泡泡尺寸改變，但中心座標保持不變。
  - 多次設定水平抖動偏移不會造成位置累積漂移。

  測試指令：

  ```powershell
  mvn -Dtest=BubbleTest test
  ```

  Commit：

  ```text
  test: add bubble lifecycle and trapping unit tests
  ```

- [ ] 6. 新增 `BossTest`

  建議檔案：

  ```text
  src/test/java/com/bubble/dragon/entity/boss/BossTest.java
  ```

  必須測試：

  - 初始 HP 為 5、狀態為 `INACTIVE`，且尚未啟動或被擊敗。
  - 設定任意方向時都會正規化為 `1` 或 `-1`。
  - `reverse()` 與 `face()` 正確調整方向。
  - 第一次有效傷害會扣除 1 HP、回傳 `true` 並開啟無敵時間。
  - 無敵期間受到傷害不會扣除 HP，並回傳 `false`。
  - 無敵時間結束後可以再次受到傷害。
  - 第五次有效傷害後狀態變為 `DEFEATED`。
  - 被擊敗時水平與垂直速度皆歸零。
  - 被擊敗後不能再次受到傷害，HP 也不會低於零。

  測試指令：

  ```powershell
  mvn -Dtest=BossTest test
  ```

  Commit：

  ```text
  test: add boss damage and state unit tests
  ```

## 必要 Integration Test

`LevelLoaderTest` 會同時驗證 `LevelLoader`、Jackson 和真實關卡 JSON 的整合，因此不是純 Unit Test，但它對本專案是必要測試。

- [ ] 7. 新增 `LevelLoaderTest`

  建議檔案：

  ```text
  src/test/java/com/bubble/dragon/map/LevelLoaderTest.java
  ```

  必須測試：

  - `/maps/level1.json` 可以成功載入。
  - `/maps/level2.json` 可以成功載入。
  - 第一關玩家起始座標為 `(80, 550)`。
  - 第二關玩家起始座標為 `(70, 560)`。
  - 第一關包含 7 個地磚與 3 個敵人。
  - 第二關包含 7 個地磚與 4 個敵人。
  - 關卡的地磚貼圖與地磚資料不是空值。
  - 載入不存在的資源時拋出 `IllegalArgumentException`。

  測試指令：

  ```powershell
  mvn -Dtest=LevelLoaderTest test
  ```

  Commit：

  ```text
  test: add level loading integration tests
  ```

## 全部完成後

- [ ] 8. 執行完整測試並確認所有測試通過

  ```powershell
  mvn clean verify
  ```

  最後檢查：

  - `target/surefire-reports` 沒有失敗項目。
  - 沒有為了讓測試通過而停用或刪除既有測試。
  - 測試彼此獨立，不依賴執行順序。
  - 測試不需要啟動 JavaFX 視窗。

  如果這一步只包含清單更新，不需要另外建立 commit；將本文件中的所有 `[ ]` 改成 `[x]`，可以和最後一組測試一起提交。若希望單獨提交清單完成狀態，可使用：

  ```text
  docs: mark required test coverage as complete
  ```

## 本清單暫不包含

以下項目目前不是必要 Unit Test，等核心測試完成後再評估：

- `CollisionSystemTest`
- `BubbleSystemTest`
- `EnemySystemTest`
- `BossSystemTest`
- `LevelTransitionTest`
- `GameControllerTest`
- TestFX 或 JavaFX UI 自動化 E2E

這些項目的依賴較多、設定成本較高。目前先用上述核心測試保護遊戲規則，完整畫面與流程則依照 `PROJECT_TESTS.md` 的人工測試清單驗證。
