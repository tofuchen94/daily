// API 请求封装
const app = getApp();
const auth = require('./auth.js');

function request(method, path, data) {
  const baseUrl = app ? app.globalData.baseUrl : 'http://localhost:8080';
  const header = {
    'Content-Type': 'application/json'
  };
  const token = auth.getToken();
  if (token) {
    header['Authorization'] = 'Bearer ' + token;
  }
  return new Promise((resolve, reject) => {
    wx.request({
      url: baseUrl + path,
      method: method,
      data: data,
      header: header,
      success(res) {
        if (res.statusCode === 200 && res.data.code === 0) {
          resolve(res.data.data);
        } else if (res.statusCode === 401 || res.data.code === 401) {
          // Token 过期，重新登录后重试
          auth.login().then(() => {
            request(method, path, data).then(resolve).catch(reject);
          }).catch(() => {
            wx.showModal({
              title: '登录失败',
              content: '请重新打开小程序',
              showCancel: false
            });
            reject(res.data);
          });
        } else {
          reject(res.data);
        }
      },
      fail(err) {
        wx.showToast({ title: '网络请求失败', icon: 'none' });
        reject(err);
      }
    });
  });
}

module.exports = {
  // 指标
  getMetrics: () => request('GET', '/api/metrics'),
  saveMetrics: (metrics) => request('POST', '/api/metrics', metrics),

  // 记录
  getRecords: (month) => request('GET', `/api/records?month=${month}`),
  getRecord: (date) => request('GET', `/api/records/${date}`),
  saveRecord: (data) => request('POST', '/api/records', data),
  deleteRecord: (id) => request('DELETE', `/api/records/${id}`),

  // 模版
  getTemplates: () => request('GET', '/api/templates'),
  saveTemplate: (data) => request('POST', '/api/templates', data),
  deleteTemplate: (id) => request('DELETE', `/api/templates/${id}`),
  setDefaultTemplate: (id) => request('PUT', `/api/templates/${id}/set-default`),

  // 生成总结
  generate: (date, templateId) => {
    let url = `/api/records/generate/${date}`;
    if (templateId) url += `?templateId=${templateId}`;
    return request('POST', url);
  }
};
