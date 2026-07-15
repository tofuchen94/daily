const api = require('../../utils/api');

Page({
  data: {
    selectedMonth: '',
    selectedMonthText: '',
    records: [],
    showEdit: false,
    editDate: '',
    editRecordId: null,
    editMetrics: [],
    editMetricsOriginal: []
  },

  onShow() {
    const now = new Date();
    const y = now.getFullYear();
    const m = String(now.getMonth() + 1).padStart(2, '0');
    this.setData({
      selectedMonth: `${y}-${m}`,
      selectedMonthText: `${y}年${parseInt(m)}月`
    });
    this.loadRecords();
  },

  onMonthChange(e) {
    const month = e.detail.value;
    const [y, m] = month.split('-');
    this.setData({
      selectedMonth: month,
      selectedMonthText: `${y}年${parseInt(m)}月`
    });
    this.loadRecords();
  },

  async loadRecords() {
    try {
      const records = await api.getRecords(this.data.selectedMonth);
      this.setData({ records });
    } catch (e) {
      this.setData({ records: [] });
    }
  },

  viewRecord(e) {
    // 点击查看 - 暂时做编辑
    this.editRecord(e);
  },

  editRecord(e) {
    const record = e.currentTarget.dataset.record;
    const metricsDef = (record.metrics || []).filter(m => m.metricName !== '备注');
    this.setData({
      showEdit: true,
      editDate: record.recordDate,
      editRecordId: record.id,
      editMetrics: metricsDef.map(m => ({
        name: m.metricName,
        unit: m.unit,
        value: m.metricValue
      })),
      editMetricsOriginal: JSON.parse(JSON.stringify(metricsDef))
    });
  },

  onEditMetricInput(e) {
    const index = e.currentTarget.dataset.index;
    this.setData({
      [`editMetrics[${index}].value`]: e.detail.value
    });
  },

  async saveEdit() {
    try {
      const metrics = this.data.editMetrics
        .filter(m => m.value !== '')
        .map(m => ({
          metricName: m.name,
          metricValue: m.value,
          unit: m.unit
        }));

      await api.saveRecord({
        recordDate: this.data.editDate,
        metrics: metrics
      });

      wx.showToast({ title: '修改成功', icon: 'success' });
      this.setData({ showEdit: false });
      this.loadRecords();
    } catch (e) {
      wx.showToast({ title: '修改失败', icon: 'none' });
    }
  },

  deleteConfirm(e) {
    const id = e.currentTarget.dataset.id;
    wx.showModal({
      title: '确认删除',
      content: '删除后不可恢复，确定要删除吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            await api.deleteRecord(id);
            wx.showToast({ title: '已删除', icon: 'success' });
            this.loadRecords();
          } catch (e) {
            wx.showToast({ title: '删除失败', icon: 'none' });
          }
        }
      }
    });
  },

  closeEdit() {
    this.setData({ showEdit: false });
  },

  stopPropagation() {
    // 阻止事件冒泡
  }
});
