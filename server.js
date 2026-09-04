const http = require('http');
const fs = require('fs');
const path = require('path');
const WebSocket = require('ws');
const pty = require('node-pty');

// Ensure spawn-helper has execution permissions on macOS
if (process.platform === 'darwin') {
  try {
    const helperArm64 = path.join(__dirname, 'node_modules/node-pty/prebuilds/darwin-arm64/spawn-helper');
    const helperX64 = path.join(__dirname, 'node_modules/node-pty/prebuilds/darwin-x64/spawn-helper');
    if (fs.existsSync(helperArm64)) fs.chmodSync(helperArm64, 0o755);
    if (fs.existsSync(helperX64)) fs.chmodSync(helperX64, 0o755);
  } catch (e) {
    // Non-fatal if permission cannot be changed
  }
}

const MIME_TYPES = {
  '.html': 'text/html; charset=utf-8',
  '.css': 'text/css; charset=utf-8',
  '.js': 'application/javascript; charset=utf-8',
  '.json': 'application/json; charset=utf-8',
  '.svg': 'image/svg+xml',
  '.ico': 'image/x-icon',
};

const server = http.createServer((req, res) => {
  const safeUrl = req.url === '/' ? '/index.html' : req.url;
  const filePath = path.join(__dirname, 'public', safeUrl.replace(/^\/+/, ''));

  if (!filePath.startsWith(path.join(__dirname, 'public'))) {
    res.writeHead(403);
    return res.end('Forbidden');
  }

  fs.readFile(filePath, (err, data) => {
    if (err) {
      res.writeHead(404, { 'Content-Type': 'text/plain; charset=utf-8' });
      return res.end('Not Found');
    }

    const ext = path.extname(filePath).toLowerCase();
    const contentType = MIME_TYPES[ext] || 'application/octet-stream';
    res.writeHead(200, { 'Content-Type': contentType });
    res.end(data);
  });
});

const wss = new WebSocket.Server({ server });

function getJavaExecutable() {
  if (process.env.JAVA_CMD) {
    return process.env.JAVA_CMD;
  }
  if (process.env.JAVA_HOME) {
    const javaBin = path.join(process.env.JAVA_HOME, 'bin', 'java');
    if (fs.existsSync(javaBin)) {
      return javaBin;
    }
  }
  const brewJava = '/opt/homebrew/opt/openjdk@21/bin/java';
  if (process.platform === 'darwin' && fs.existsSync(brewJava)) {
    return brewJava;
  }
  return 'java';
}

wss.on('connection', (ws) => {
  console.log('Client connected');

  const javaCmd = getJavaExecutable();
  let javaProcess;

  try {
    javaProcess = pty.spawn(
      javaCmd,
      ['-Dfile.encoding=UTF-8', '-cp', 'bin', 'warinkaan.Main'],
      {
        name: 'xterm-256color',
        cols: 80,
        rows: 24,
        cwd: process.cwd(),
        env: {
          ...process.env,
          LANG: 'ja_JP.UTF-8',
          LC_ALL: 'ja_JP.UTF-8',
        },
      }
    );
  } catch (err) {
    console.error('Failed to spawn java process:', err);
    if (ws.readyState === WebSocket.OPEN) {
      ws.send(`\r\n\x1b[31m[エラー] Javaプロセスの起動に失敗しました: ${err.message}\x1b[0m\r\n`);
      ws.close();
    }
    return;
  }

  javaProcess.onData((data) => {
    if (ws.readyState === WebSocket.OPEN) {
      ws.send(data);
    }
  });

  ws.on('message', (message) => {
    const raw = message.toString();

    // Check for resize packet
    if (raw.startsWith('{') && raw.includes('"type":"resize"')) {
      try {
        const payload = JSON.parse(raw);
        if (payload.type === 'resize' && payload.cols && payload.rows) {
          javaProcess.resize(Math.max(10, payload.cols), Math.max(5, payload.rows));
          return;
        }
      } catch (e) {
        // Fall through
      }
    }

    try {
      javaProcess.write(raw);
    } catch (e) {
      console.error('Failed to write to java process:', e);
    }
  });

  ws.on('close', () => {
    console.log('Client disconnected');
    try {
      javaProcess.kill();
    } catch (e) {}
  });

  ws.on('error', (err) => {
    console.error('WebSocket error:', err);
    try {
      javaProcess.kill();
    } catch (e) {}
  });

  javaProcess.onExit((code) => {
    console.log(`Java process exited with code ${code}`);
    if (ws.readyState === WebSocket.OPEN) {
      ws.send('\r\n\x1b[90m[セッションが終了しました。再開するには再接続ボタンを押してください]\x1b[0m\r\n');
      ws.close();
    }
  });
});

const PORT = process.env.PORT || 3000;

server.listen(PORT, () => {
  console.log(`Server listening on port ${PORT}`);
});
