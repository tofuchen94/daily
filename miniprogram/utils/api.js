// API 请求封装
const app = getApp();

function request(method, path, data) {
  const baseUrl = app ? app.globalData.baseUrl : 'http://localhost:8080';
  return new Promise((resolve, reject) => {
    wx.request({
      url: baseUrl + path,
      method: method,
      data: data,
      header: {
        'Content-Type': 'application/json'
      },
      success(res) {
        if (res.statusCode === 200 && res.data.code === 0) {
          resolve(res.data.data);
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

  // 生成总结
  generate: (date, templateId) => {
    let url = `/api/records/generate/${date}`;
    if (templateId) url += `?templateId=${templateId}`;
    return request('POST', url);
  }
};
