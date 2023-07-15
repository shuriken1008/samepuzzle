
# セイムパズル
### ゲーム内容
- ２つ以上連続したブロックを消すゲーム
- 連なるブロックの数が多いほど高得点

***通信要素***
- 対戦型マルチプレイを実装：
- 目標点数に1番最初に届いた方が勝者


***やること***
__GUI__
- タイトル画面が欲しい！
- プレイヤー名と部屋名の入力欄
- サーバーからint[][]データ送るので、int[][]を引数にとって読み込んでブロックを配置する関数作ってほしい！

__Server/通信__
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
    "displayName": String,
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
    "isWaiting": Boolean,
    "score": int,
    "rank": int
}
```

- ブロックデータ
```json
{
    "type": "blockData",
    "data": String //2次配列をそのまま文字列変換
}
```

- 破壊するブロックの座標
```json
{
    "type": "breakData",
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
 