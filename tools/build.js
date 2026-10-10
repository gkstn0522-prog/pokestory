// src/game.html 을 GitHub Pages에 올릴 완성된 웹페이지(dist/)로 만듭니다.
// 사용법: node tools/build.js
const fs = require('fs');
const path = require('path');

const root = path.join(__dirname, '..');
const dist = path.join(root, 'dist');
fs.rmSync(dist, { recursive: true, force: true });
fs.mkdirSync(dist, { recursive: true });

const game = fs.readFileSync(path.join(root, 'src', 'game.html'), 'utf8');
const buildId = new Date().toISOString().replace(/[-:.TZ]/g, '').slice(0, 14);

const html = `<!doctype html>
<html lang="ko">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width,initial-scale=1,viewport-fit=cover,user-scalable=no">
<meta name="theme-color" content="#12151d">
<link rel="manifest" href="manifest.json">
<link rel="icon" href="icon.svg">
<style>:root{padding-top:env(safe-area-inset-top,0px);padding-bottom:env(safe-area-inset-bottom,0px)}[hidden]{display:none!important}</style>
</head>
<body>
${game}
<script>
if ('serviceWorker' in navigator) {
  navigator.serviceWorker.register('sw.js?b=${buildId}').catch(function () {});
}
</script>
</body>
</html>
`;
fs.writeFileSync(path.join(dist, 'index.html'), html);

const sw = fs.readFileSync(path.join(root, 'web', 'sw.js'), 'utf8').replace('__BUILD_ID__', buildId);
fs.writeFileSync(path.join(dist, 'sw.js'), sw);
for (const f of ['manifest.json', 'icon.svg']) {
  fs.copyFileSync(path.join(root, 'web', f), path.join(dist, f));
}
fs.writeFileSync(path.join(dist, '.nojekyll'), '');
console.log('빌드 완료:', buildId);
