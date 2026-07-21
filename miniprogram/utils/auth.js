// 微信登录 + Token 管理
const TOKEN_KEY = 'auth_token';

function getToken() {
  try {
    return wx.getStorageSync(TOKEN_KEY) || '';
  } catch (e) {
    return '';
  }
}

function setToken(token) {
  wx.setStorageSync(TOKEN_KEY, token);
}

function clearToken() {
  wx.removeStorageSync(TOKEN_KEY);
}

function isLoggedIn() {
  return !!getToken();
}

/**
 * 登录：wx.login() 拿 code，调后端换 JWT
 */
function login() {
  return new Promise((resolve, reject) => {
    wx.login({
      success(res) {
        if (!res.code) {
          reject(new Error('wx.login 失败'));
          return;
        }
        // 直接在这里调用，避免循环依赖
        const baseUrl = getApp().globalData.baseUrl;
        wx.request({
          url: baseUrl + '/api/auth/login',
          method: 'POST',
          data: { code: res.code },
          header: { 'Content-Type': 'application/json' },
          success(resp) {
            if (resp.statusCode === 200 && resp.data.code === 0) {
              const { token } = resp.data.data;
              setToken(token);
              resolve(resp.data.data);
            } else {
              reject(new Error(resp.data.msg || '登录失败'));
            }
          },
          fail(err) {
            reject(err);
          }
        });
      },
      fail(err) {
        reject(err);
      }
    });
  });
}

/**
 * 确保登录态有效，若无效则自动登录
 */
async function ensureLogin() {
  if (isLoggedIn()) {
    // 简单检查：用 getToken() 存在就行
    // 后端拦截器会校验 token 有效性
    return getToken();
  }
  const result = await login();
  return result.token;
}

module.exports = {
  getToken,
  setToken,
  clearToken,
  isLoggedIn,
  login,
  ensureLogin
};
