# Bubble Dragon 專案結構

Bubble Dragon 是以 Java 21、JavaFX 21 與 Maven 製作的模組化 2D 平台遊戲。玩家使用泡泡捕捉敵人，通過第一關後進入 Boss 關，擊敗所有敵人與 Boss 並抵達出口即可獲勝。

## 技術組成

- Java 21
- JavaFX Controls 與 JavaFX Media 21.0.7
- Maven
- Jackson Databind 2.19.0（讀取關卡 JSON）
- JUnit Jupiter 5.12.2
- Java Platform Module System（`module-info.java`）
- JavaFX Canvas 與 `AnimationTimer`

JavaFX 與其他相依套件由 Maven 管理，不需要另外安裝 JavaFX SDK。

## 遊戲流程

```text
Main
  → BubbleDragonApp
  → HomeView
  → StoryView
  → GameView／GameController
      → 第一關普通敵人
      → LevelTransition
      → 第二關普通敵人與 Boss
      → 出口
  → GameOverView（勝利或失敗）
```

主要規則：

- 玩家初始 HP 為 3。
- 左、右方向鍵移動，`Space` 跳躍，`Z` 發射泡泡。
- `Esc` 會從遊戲畫面返回首頁。
- 普通泡泡先水平飛行，之後向上飄；泡泡可捕捉 `JUMP` 或 `FLY` 狀態的敵人。
- 被捕捉的敵人會啟動掙脫倒數；玩家擊破泡泡可消滅敵人，逾時則敵人掙脫。
- 第一關普通敵人全滅後啟動轉場並切換到第二關。
- 第二關必須消滅普通敵人與 Boss，出口才會出現。
- 玩家接觸出口後獲勝；HP 歸零則失敗。

## 架構原則

- `Main` 只負責呼叫 JavaFX `Application.launch()`。
- `BubbleDragonApp` 管理唯一的 `Stage`、畫面切換及全域音樂生命週期。
- `view` 建立各個 JavaFX `Scene`，不保存核心遊戲規則。
- `GameController` 協調輸入、各遊戲系統、關卡轉場與勝敗結果。
- 各 controller/system 類別拆分碰撞、泡泡、敵人、Boss 與關卡職責。
- `entity` 保存遊戲物件資料與狀態，不直接切換 Scene。
- `ui` 負責 Canvas 繪製與 HUD，不決定遊戲規則。
- `GameLoop` 使用 `AnimationTimer` 驅動每幀更新與繪製。
- 關卡資料放在 JSON 資源中，透過 Jackson 載入。

## 專案目錄

```text
BubbleDragon/
├─ pom.xml
├─ README.md
├─ PROJECT_COMMANDS.md
├─ PROJECT_STRUCTURE.md
├─ PROJECT_TESTS.md
├─ .gitignore
└─ src/
   ├─ main/
   │  ├─ java/
   │  │  ├─ module-info.java
   │  │  └─ com/bubble/dragon/
   │  │     ├─ Main.java
   │  │     ├─ BubbleDragonApp.java
   │  │     ├─ controller/
   │  │     │  ├─ GameController.java
   │  │     │  ├─ LevelManager.java
   │  │     │  ├─ LevelTransition.java
   │  │     │  ├─ CollisionSystem.java
   │  │     │  ├─ BubbleSystem.java
   │  │     │  ├─ EnemySystem.java
   │  │     │  └─ BossSystem.java
   │  │     ├─ entity/
   │  │     │  ├─ GameObject.java
   │  │     │  ├─ player/
   │  │     │  │  ├─ Player.java
   │  │     │  │  └─ PlayerState.java
   │  │     │  ├─ enemy/
   │  │     │  │  ├─ Enemy.java
   │  │     │  │  └─ EnemyState.java
   │  │     │  ├─ weapon/
   │  │     │  │  ├─ Bubble.java
   │  │     │  │  └─ BubbleMovementState.java
   │  │     │  └─ boss/
   │  │     │     ├─ Boss.java
   │  │     │     ├─ BossBubble.java
   │  │     │     └─ BossState.java
   │  │     ├─ game/
   │  │     │  └─ GameLoop.java
   │  │     ├─ map/
   │  │     │  ├─ GameMap.java
   │  │     │  ├─ LevelLoader.java
   │  │     │  └─ Tile.java
   │  │     ├─ physics/
   │  │     │  └─ OverlapChecker.java
   │  │     ├─ ui/
   │  │     │  ├─ GameCanvas.java
   │  │     │  ├─ HUD.java
   │  │     │  ├─ ImageLoader.java
   │  │     │  ├─ PlayerRenderer.java
   │  │     │  ├─ EnemyRenderer.java
   │  │     │  ├─ BossRenderer.java
   │  │     │  └─ TileRenderer.java
   │  │     ├─ util/
   │  │     │  ├─ Constants.java
   │  │     │  ├─ CountdownTimer.java
   │  │     │  ├─ GameAudio.java
   │  │     │  └─ RelaxMusic.java
   │  │     └─ view/
   │  │        ├─ HomeView.java
   │  │        ├─ StoryView.java
   │  │        ├─ GameView.java
   │  │        └─ GameOverView.java
   │  └─ resources/
   │     ├─ css/application.css
   │     ├─ fonts/
   │     │  ├─ Huninn-Regular.ttf
   │     │  └─ LilitaOne-Regular.ttf
   │     ├─ images/
   │     ├─ sounds/
   │     └─ maps/
   │        ├─ level1.json
   │        └─ level2.json
   └─ test/java/com/bubble/dragon/
      ├─ entity/
      │  ├─ boss/BossTest.java
      │  ├─ enemy/EnemyTest.java
      │  ├─ player/PlayerTest.java
      │  └─ weapon/BubbleTest.java
      ├─ map/LevelLoaderTest.java
      ├─ physics/OverlapCheckerTest.java
      └─ util/CountdownTimerTest.java
```

`target/` 是 Maven、jlink 與 jpackage 的產物目錄，已由 `.gitignore` 排除，不屬於原始碼結構。

## Package 職責

| Package | 職責 |
| --- | --- |
| `com.bubble.dragon` | JavaFX 啟動、主視窗、Scene 切換與全域音樂 |
| `controller` | 遊戲流程、輸入、碰撞、敵人、泡泡、Boss、關卡及轉場 |
| `entity` | 所有遊戲物件共用的座標、尺寸與速度 |
| `entity.player` | 玩家 HP、方向、著地、無敵時間與動畫狀態 |
| `entity.enemy` | 普通敵人的方向、行為狀態、捕捉與掙脫倒數 |
| `entity.weapon` | 玩家泡泡的移動階段、生命週期與困敵資料 |
| `entity.boss` | Boss HP、狀態、方向、無敵時間與攻擊泡泡 |
| `game` | JavaFX `AnimationTimer` 遊戲主迴圈 |
| `map` | JSON 關卡資料、地磚模型與資源載入 |
| `physics` | AABB 矩形重疊判斷 |
| `ui` | Canvas 遊戲繪製、圖片載入、角色 renderer 與 HUD |
| `util` | 常數、倒數計時、遊戲音效與背景音樂 |
| `view` | 首頁、故事、遊戲及勝敗 Scene |

## 核心類別

### 啟動與畫面

- `Main`：JVM 入口，啟動 `BubbleDragonApp`。
- `BubbleDragonApp`：設定 Stage、載入字型、管理首頁／故事／遊戲／結果畫面，以及背景音樂。
- `HomeView`：開始按鈕與操作提示。
- `StoryView`：依序播放四張漫畫並進入遊戲。
- `GameView`：建立 Canvas、HUD、鍵盤事件及 `GameLoop`。
- `GameOverView`：依勝敗建立結果畫面，提供重新開始或返回首頁。

### 遊戲控制

- `GameController`：保存按鍵狀態，依序更新玩家、敵人、泡泡、Boss、轉場及勝敗條件。
- `LevelManager`：載入兩個關卡，建立地磚與普通敵人，並切換目前關卡資料。
- `LevelTransition`：將玩家從第一關平滑移動至第二關落點。
- `CollisionSystem`：套用移動並處理遊戲物件與實心地磚的碰撞。
- `BubbleSystem`：玩家射擊冷卻、泡泡移動、困敵、破泡及清除。
- `EnemySystem`：普通敵人的跳躍、飛行、停止、追逐、掙脫與接觸傷害。
- `BossSystem`：Boss 登場、巡邏、追逐、俯衝、攻擊泡泡與受傷判定。

### 模型與狀態

- `GameObject`：共用位置、尺寸、速度與邊界計算。
- `PlayerState`：`IDLE`、`MOVING`、`JUMPING`、`DEAD`。
- `EnemyState`：`JUMP`、`FLY`、`STOP`、`TRAPPED`、`DEFEATED`。
- `BubbleMovementState`：`HORIZONTAL`、`RISING`、`STOPPED_AT_TOP`。
- `BossState`：`INACTIVE`、`SCREAM`、`FOLD_WINGS`、`PATROL`、`DIVING_ATTACK`、`DEFEATED`。
- `GameMap`：對應關卡 JSON 的玩家出生點、地磚、貼圖與敵人資料。
- `Tile`：地磚渲染位置、碰撞區、solid 狀態與貼圖排列。

## 資源

### 關卡

- `maps/level1.json`：第一關泥土地圖，包含 7 個地磚與 3 個普通敵人。
- `maps/level2.json`：第二關石頭地圖，包含 7 個地磚與 4 個普通敵人；Boss 由 `BossSystem` 管理，不寫在 JSON 中。

### 圖片

`images/` 包含首頁、故事、遊戲、勝敗背景，四張漫畫，玩家／敵人／Boss 動畫圖片，兩組地磚、出口及 Windows 圖示。

泡泡本身由 Canvas 繪製，不使用獨立泡泡圖片。

### 音效

- `relax-music.mp3`：首頁、故事與結果畫面的背景音樂。
- `battle-music.mp3`：遊戲中的戰鬥音樂。
- `bubble-sound.mp3`：玩家發射泡泡。
- `pop-bubble.mp3`：泡泡破裂或擊中效果。
- `damage-sound.mp3`：玩家受傷。
- `boss-scream.mp3`：Boss 登場叫聲。

### 字型與 CSS

- `Huninn-Regular.ttf`：中文介面字型。
- `LilitaOne-Regular.ttf`：標題或拉丁字元顯示。
- `application.css`：JavaFX 控制項與畫面樣式。

## Java 模組

模組名稱為：

```text
com.bubble.dragon
```

主要依賴：

- `javafx.graphics`
- `javafx.controls`
- `javafx.media`
- `com.fasterxml.jackson.databind`

`com.bubble.dragon.map` 對 Jackson 開放反射存取。jlink 與 jpackage 使用的模組入口為：

```text
com.bubble.dragon/com.bubble.dragon.Main
```

## 自動化測試

目前共有 7 個測試類別、49 個測試方法：

- 6 個 Unit Test 類別：計時器、碰撞、玩家、敵人、泡泡與 Boss。
- 1 個 Integration Test 類別：載入兩個真實關卡 JSON。

測試細節與人工驗收清單請參考 `PROJECT_TESTS.md`。

## 建置與執行

常用指令：

```powershell
mvn javafx:run
mvn test
mvn clean verify
mvn clean javafx:jlink
```

完整的開發、測試、jlink 與 Windows jpackage 指令請參考 `PROJECT_COMMANDS.md`。
