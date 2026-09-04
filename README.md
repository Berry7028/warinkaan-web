# わりんかーん Web Terminal

Javaで作成したCUI割り勘アプリ「わりんかーん」を、ブラウザ上のターミナルから操作できるようにしたWebアプリケーションです。

## 技術スタック

- **UI / デザイン**: xterm.js (FitAddon), Scandinavian Design (静謐でミニマルな北欧デザイン)
- **バックエンド**: Node.js, WebSocket (`ws`), 疑似端末制御 (`node-pty`)
- **コアロジック**: Java 17/21 (`warinkaan.Main`)
- **コンテナ / デプロイ**: Docker, Railway

## ディレクトリ構成

```text
warinkaan-web/
├── Dockerfile
├── .dockerignore
├── package.json
├── server.js
├── README.md
├── src/
│   ├── module-info.java
│   └── warinkaan/
│       ├── Main.java
│       ├── controller/
│       ├── model/
│       ├── service/
│       ├── util/
│       └── view/
└── public/
    └── index.html
```

## ローカル開発手順

### 前提条件
- Node.js 20以上
- Java Development Kit (JDK 17 または 21)

### 1. 依存パッケージのインストール
```bash
npm install
```

### 2. Javaプログラムのビルド
```bash
npm run build:java
```

### 3. サーバー起動
```bash
npm start
```
起動後、ブラウザで `http://localhost:3000` にアクセスしてください。

## Railwayへのデプロイ手順

1. 本ディレクトリ（`warinkaan-web`）をGitHubリポジトリにプッシュします。
2. Railwayダッシュボードで **New Project** → **Deploy from GitHub repo** を選択します。
3. リポジトリを選択すると、ルートにある `Dockerfile` が自動認識されてビルド・デプロイが開始されます。
4. デプロイ完了後、**Settings** → **Networking** → **Generate Domain** で公開URLを発行し、アクセスします。
