
# セイムパズル
### ゲーム内容
- ２つ以上連続したブロックを消すゲーム
- 連なるブロックの数が多いほど高得点

***通信要素***
- 対戦型マルチプレイを実装：
- 目標点数に1番最初に届いた方が勝者
- 複数人・複数ゲーム同時動作を想定
- 「部屋機能」自分で部屋を作成/又は参加してゲームを始められる

***役割分担明記***
一安さん  -> フロントエンド
- ゲームシステム
- GUI

修理 -> バックエンド
- サーバーシステム
- 通信(サーバー/クライアント)
- ゲーム設計

***やること***
__GUI__
- SameGame.javaをこっちに合わせてGUI.javaに変わってますちょっと確認してみてください！
- タイトル画面が欲しい！
- プレイヤー名と部屋名の入力画面
- サーバーからint[][]のブロックデータ送るので、int[][]を引数にとって読み込んでブロックを配置する関数作ってほしい！
- 1つもブロックが壊せなくなったらreloadStage()とか適当に関数呼び出してほしい！

__Server/通信__
- 今やってること：データ通信の取り決めできたので実装中
- 部屋の管理作成
- 部屋が無かったら作成→作った人がリーダー(ゲームの開始・メンバー編集の権限・ゲーム設定の権限あり)
- ゲームを一斉にスタートできるようにタイミング調整
- 

***通信プロトコル***
- すべてjson

# 共通
- 部屋でチャットする
```json
{
    "type": "chat",
    "uuid": String,
    "content": String
}
```

- プレイヤーデータ
```json
{
    "type": "playerData",
    "displayName": String,
    "uuid": String,
    "isOwner": Boolean,
    "isPlaying": Boolean,
    "isReady": Boolean,
    "score": int,
    "rank": int

}
```
-部屋データ
```json
{
    "type": "roomData",
    "roomName": String,
    "uuid": String,
    "ownerUUID": String,
    "targetScore": int,
    "maxUsers": int
}
```


- ブロックデータ
```json
{
    "type": "blockData",
    "stageLevel" : int,
    "data": String //2次配列をそのまま文字列変換
}
```

- 破壊するブロックの座標
```json
{
    "type": "breakData",
    "uuid": String,
    "roomName": String,
    "stageLevel": int,
    "score": int,
    "x": int,
    "y": int
}
```


# クライアント -> サーバー
- 接続を開始する
```json
{
    "type": "connect",
    "displayName": String,
    "uuid": String,
    "roomName": String,
}
```

- 部屋を退出=接続を終了する
```json
{
    "type": "disconnect",
    "uuid": String,
}
```

- ステータス変更(ゲーム開始宣言・準備完了宣言)
```json
{
    "type": "status",
    "uuid": String
}
```




# サーバー -> クライアント
- ゲーム開始
```json
{
    "type": "gameStart",
    "startAt": int, //エポック秒,
    "targetScore": int
}
```
- ゲーム終了
```json
{
    "type": "gameEnd",
    "winnerUUID": String,
    "hiScore": int
}
```



***元ネタ：***

![画像](https://dixq.net/sm/img/d9/1.jpg)
 