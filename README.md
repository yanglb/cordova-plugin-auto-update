# cordova-plugin-auto-update
Cordova Android App 自动更新插件

此插件基于 https://github.com/czy1121/update 项目开发，感谢 czy1121 ！

## 使用方法

### 安装
```sh
cordova plugin add cordova-plugin-auto-update
```

### JS使用
```js
// 设置接口地址
// isWifiOnly 默认 false
update.init('api address', 'isWifiOnly');

// 手动检查
update.manualCheck();

// 自动检查
update.check();

```

详细文档请参考 https://github.com/czy1121/update 
