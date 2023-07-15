
# セイムパズル
### ゲーム内容
- ２つ以上連続したブロックを消すゲーム
- 連なるブロックの数が多いほど高得点

***通信要素***
- 対戦型マルチプレイを実装：
- 目標点数に先に届いた方が勝者


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
- type <*required*>
- - connect
- -


***元ネタ：***

![画像](https://dixq.net/sm/img/d9/1.jpg)
 