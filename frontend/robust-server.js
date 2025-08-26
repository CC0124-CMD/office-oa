const http = require('http');
const fs = require('fs');
const path = require('path');
const url = require('url');

const port = 8081;
const distPath = path.join(__dirname, 'dist');

const mimeTypes = {
  '.html': 'text/html',
  '.js': 'text/javascript',
  '.css': 'text/css',
  '.json': 'application/json',
  '.png': 'image/png',
  '.jpg': 'image/jpg',
  '.gif': 'image/gif',
  '.svg': 'image/svg+xml',
  '.wav': 'audio/wav',
  '.mp4': 'video/mp4',
  '.woff': 'application/font-woff',
  '.ttf': 'application/font-ttf',
  '.eot': 'application/vnd.ms-fontobject',
  '.otf': 'application/font-otf',
  '.wasm': 'application/wasm'
};

const server = http.createServer((req, res) => {
  const parsedUrl = url.parse(req.url);
  let pathname = parsedUrl.pathname;
  
  console.log(`${req.method} ${pathname}`);
  
  // 设置CORS头
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, OPTIONS');
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type, Authorization');
  
  if (req.method === 'OPTIONS') {
    res.writeHead(200);
    res.end();
    return;
  }
  
  // 处理根路径和SPA路由
  if (pathname === '/' || pathname === '/login' || pathname === '/register') {
    pathname = '/index.html';
  }
  
  // 构建文件路径
  let filePath = path.join(distPath, pathname);
  
  // 安全检查：确保文件路径在dist目录内
  if (!filePath.startsWith(distPath)) {
    res.writeHead(403);
    res.end('Forbidden');
    return;
  }
  
  // 如果文件不存在，尝试作为SPA路由处理
  fs.access(filePath, fs.constants.F_OK, (err) => {
    if (err) {
      // 文件不存在，返回index.html（SPA路由）
      const indexPath = path.join(distPath, 'index.html');
      fs.readFile(indexPath, (err2, data) => {
        if (err2) {
          res.writeHead(404, { 'Content-Type': 'text/plain' });
          res.end('File not found');
        } else {
          res.writeHead(200, { 'Content-Type': 'text/html' });
          res.end(data);
        }
      });
    } else {
      // 文件存在，读取并返回
      fs.readFile(filePath, (err, data) => {
        if (err) {
          res.writeHead(500, { 'Content-Type': 'text/plain' });
          res.end('Internal Server Error');
        } else {
          const ext = path.extname(filePath);
          const contentType = mimeTypes[ext] || 'application/octet-stream';
          
          res.writeHead(200, { 'Content-Type': contentType });
          res.end(data);
        }
      });
    }
  });
});

server.listen(port, 'localhost', () => {
  console.log(`========================================`);
  console.log(`🚀 前端服务器启动成功！`);
  console.log(`========================================`);
  console.log(`📍 服务地址: http://localhost:${port}`);
  console.log(`📁 文件目录: ${distPath}`);
  console.log(`🌐 支持SPA路由: /, /login, /register`);
  console.log(`========================================`);
  console.log(`按 Ctrl+C 停止服务器`);
  console.log(`========================================`);
});

// 优雅关闭
process.on('SIGINT', () => {
  console.log('\n正在关闭服务器...');
  server.close(() => {
    console.log('服务器已关闭');
    process.exit(0);
  });
});

// 错误处理
server.on('error', (err) => {
  if (err.code === 'EADDRINUSE') {
    console.error(`❌ 端口 ${port} 已被占用，请先停止其他服务`);
    console.error(`💡 运行: taskkill /F /IM node.exe`);
  } else {
    console.error('❌ 服务器错误:', err);
  }
  process.exit(1);
}); 