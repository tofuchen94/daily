// app.js
App({
  globalData: {
    // 后端 API 地址 - 生产环境
    baseUrl: 'https://daily1116.online'
  },

  onLaunch() {
    // 检查本地存储的 API 地址
    const savedUrl = wx.getStorageSync('baseUrl');
    if (savedUrl) {
      this.globalData.baseUrl = savedUrl;
    }
  }
});
