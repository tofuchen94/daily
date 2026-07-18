const api = require('../../utils/api');

Page({
  data: {
    recordDate: '',
    recordDateText: '',
    metrics: [],        // [{name, unit, sortOrder, value}]
    note: '',
    summary: '',
    saving: false,
    generating: false
  },

  async onShow() {
    this.setToday();
    await this.loadMetrics();    // 先确保指标定义加载完成
    this.loadTodayRecord();      // 再加载今日记录（此时 this.data.metrics 已有数据）
  },

  // 设置默认日期为今天
  setToday() {
    const now = new Date();
    const y = now.getFullYear();
    const m = String(now.getMonth() + 1).padStart(2, '0');
    const d = String(now.getDate()).padStart(2, '0');
    this.setData({
      recordDate: `${y}-${m}-${d}`,
      recordDateText: `${y}年${m}月${d}日`
    });
  },

  // 加载指标定义
  async loadMetrics() {
    try {
      const metrics = await api.getMetrics();
      // 用 Promise + callback 确保 setData 真正完成
      return new Promise((resolve) => {
        this.setData({
          metrics: metrics.map(m => ({
            name: m.name,
            unit: m.unit || '',
            sortOrder: m.sortOrder,
            value: ''
          }))
        }, resolve);
      });
    } catch (e) {
      // 静默失败
    }
  },

  // 加载今天已有记录
  async loadTodayRecord() {
    try {
      const record = await api.getRecord(this.data.recordDate);
      if (record) {
        const metricMap = {};
        (record.metrics || []).forEach(m => {
          metricMap[m.metricName] = m.metricValue;
        });
        const noteMetric = (record.metrics || []).find(m => m.metricName === '备注');
        this.setData({
          metrics: this.data.metrics.map(m => ({
            ...m,
            value: metricMap[m.name] || ''
          })),
          note: noteMetric ? noteMetric.metricValue : ''
        });
      }
    } catch (e) {
      // 没有记录或加载失败
    }
  },

  // 日期变化
  onDateChange(e) {
    const date = e.detail.value;
    const [y, m, d] = date.split('-');
    this.setData({
      recordDate: date,
      recordDateText: `${y}年${parseInt(m)}月${parseInt(d)}日`,
      summary: ''
    });
    this.loadTodayRecord();
  },

  // 指标输入
  onMetricInput(e) {
    const index = e.currentTarget.dataset.index;
    const value = e.detail.value;
    this.setData({
      [`metrics[${index}].value`]: value
    });
  },

  // 备注输入
  onNoteInput(e) {
    this.setData({ note: e.detail.value });
  },

  // 保存记录
  async saveRecord() {
    this.setData({ saving: true });
    try {
      const metricList = this.data.metrics
        .filter(m => m.value !== '')
        .map(m => ({
          metricName: m.name,
          metricValue: m.value,
          unit: m.unit
        }));

      // 备注也作为一条指标
      if (this.data.note) {
        metricList.push({
          metricName: '备注',
          metricValue: this.data.note,
          unit: ''
        });
      }

      await api.saveRecord({
        recordDate: this.data.recordDate,
        metrics: metricList
      });

      wx.showToast({ title: '保存成功', icon: 'success' });
    } catch (e) {
      wx.showToast({ title: '保存失败', icon: 'none' });
    }
    this.setData({ saving: false });
  },

  // 生成总结
  async generateSummary() {
    this.setData({ generating: true });
    try {
      const summary = await api.generate(this.data.recordDate, null);
      this.setData({ summary });
    } catch (e) {
      wx.showToast({ title: '生成失败，请检查模版配置', icon: 'none' });
    }
    this.setData({ generating: false });
  },

  // 复制总结
  copySummary() {
    wx.setClipboardData({
      data: this.data.summary,
      success() {
        wx.showToast({ title: '已复制到剪贴板', icon: 'success' });
      }
    });
  }
});
