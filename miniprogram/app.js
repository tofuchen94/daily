// app.js
App({
  globalData: {
    // 后端 API 地址 - 开发时改为你的电脑 IP
    // 微信开发者工具 -> 设置 -> 代理 -> 不校验合法域名（勾选）
    baseUrl: 'http://localhost:8080'
  },

  onLaunch() {
    // 检查本地存储的 API 地址
    const savedUrl = wx.getStorageSync('baseUrl');
    if (savedUrl) {
      this.globalData.baseUrl = savedUrl;
    }
  }
});
