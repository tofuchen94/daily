// app.js
const auth = require('./utils/auth.js');

App({
  globalData: {
    baseUrl: 'https://daily1116.online'
  },

  async onLaunch() {
    // 加载本地存储的 API 地址
    const savedUrl = wx.getStorageSync('baseUrl');
    if (savedUrl) {
      this.globalData.baseUrl = savedUrl;
    }

    // 自动登录
    try {
      await auth.ensureLogin();
      console.log('登录成功');
    } catch (e) {
      console.warn('登录延迟，将在首次 API 调用时重试');
    }
  }
});
