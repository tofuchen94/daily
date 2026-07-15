const api = require('../../utils/api');
const app = getApp();

Page({
  data: {
    // 指标定义
    metricDefs: [],
    newMetricName: '',
    newMetricUnit: '',
    savingMetrics: false,

    // 模版
    templates: [],
    selectedTemplateIndex: 0,
    templateName: '',
    templateContent: '',
    savingTemplate: false,

    // API
    baseUrl: ''
  },

  onShow() {
    this.loadMetrics();
    this.loadTemplates();
    this.setData({ baseUrl: app.globalData.baseUrl });
  },

  // ========== 指标管理 ==========
  async loadMetrics() {
    try {
      const metrics = await api.getMetrics();
      this.setData({ metricDefs: metrics.map(m => ({
        name: m.name,
        unit: m.unit || '',
        sortOrder: m.sortOrder
      }))});
    } catch (e) {
      // 静默失败
    }
  },

  onNewNameInput(e) {
    this.setData({ newMetricName: e.detail.value });
  },

  onNewUnitInput(e) {
    this.setData({ newMetricUnit: e.detail.value });
  },

  addMetric() {
    const name = this.data.newMetricName.trim();
    const unit = this.data.newMetricUnit.trim();
    if (!name) {
      wx.showToast({ title: '请输入指标名称', icon: 'none' });
      return;
    }
    this.setData({
      metricDefs: [...this.data.metricDefs, {
        name,
        unit,
        sortOrder: this.data.metricDefs.length
      }],
      newMetricName: '',
      newMetricUnit: ''
    });
  },

  deleteMetric(e) {
    const index = e.currentTarget.dataset.index;
    const defs = [...this.data.metricDefs];
    defs.splice(index, 1);
    defs.forEach((d, i) => d.sortOrder = i);
    this.setData({ metricDefs: defs });
  },

  async saveMetrics() {
    this.setData({ savingMetrics: true });
    try {
      await api.saveMetrics(this.data.metricDefs.map((m, i) => ({
        name: m.name,
        unit: m.unit,
        sortOrder: i
      })));
      wx.showToast({ title: '指标已保存', icon: 'success' });
    } catch (e) {
      wx.showToast({ title: '保存失败', icon: 'none' });
    }
    this.setData({ savingMetrics: false });
  },

  // ========== 模版管理 ==========
  async loadTemplates() {
    try {
      const templates = await api.getTemplates();
      let selIndex = 0;
      if (templates.length > 0) {
        const defIndex = templates.findIndex(t => t.isDefault === 1);
        selIndex = defIndex >= 0 ? defIndex : 0;
      }
      this.setData({
        templates,
        selectedTemplateIndex: selIndex,
        templateName: templates.length > 0 ? templates[selIndex].name : '',
        templateContent: templates.length > 0 ? templates[selIndex].content : ''
      });
    } catch (e) {
      // 静默
    }
  },

  onTemplateSelect(e) {
    const index = parseInt(e.detail.value);
    const tpl = this.data.templates[index];
    this.setData({
      selectedTemplateIndex: index,
      templateName: tpl.name,
      templateContent: tpl.content
    });
  },

  onTemplateNameInput(e) {
    this.setData({ templateName: e.detail.value });
  },

  onTemplateContentInput(e) {
    this.setData({ templateContent: e.detail.value });
  },

  async saveTemplate() {
    if (!this.data.templateName.trim()) {
      wx.showToast({ title: '请输入模版名称', icon: 'none' });
      return;
    }
    this.setData({ savingTemplate: true });
    try {
      const tpl = {
        name: this.data.templateName,
        content: this.data.templateContent,
        isDefault: 1  // 新保存的设为默认
      };
      // 如果已有模版被选中，更新它
      if (this.data.templates.length > 0) {
        tpl.id = this.data.templates[this.data.selectedTemplateIndex].id;
      }
      await api.saveTemplate(tpl);
      wx.showToast({ title: '模版已保存', icon: 'success' });
      this.loadTemplates();
    } catch (e) {
      wx.showToast({ title: '保存失败', icon: 'none' });
    }
    this.setData({ savingTemplate: false });
  },

  async deleteTemplate() {
    if (this.data.templates.length <= 1) return;
    wx.showModal({
      title: '确认删除',
      content: '确定要删除该模版吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            const tpl = this.data.templates[this.data.selectedTemplateIndex];
            await api.deleteTemplate(tpl.id);
            wx.showToast({ title: '已删除', icon: 'success' });
            this.loadTemplates();
          } catch (e) {
            wx.showToast({ title: '删除失败', icon: 'none' });
          }
        }
      }
    });
  },

  // ========== API 地址 ==========
  onBaseUrlInput(e) {
    this.setData({ baseUrl: e.detail.value });
  },

  saveBaseUrl() {
    app.globalData.baseUrl = this.data.baseUrl;
    wx.setStorageSync('baseUrl', this.data.baseUrl);
    wx.showToast({ title: '地址已保存', icon: 'success' });
  }
});
