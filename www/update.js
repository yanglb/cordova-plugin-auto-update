
var Update = {
  apiAddress: null,
  wifiOnly: false,

  /**
   * 初始化
   * @param {String} apiAddress 检查更新API地址
   * @param {bool}   wifiOnly   仅仅使用wi-fi 默认 false
   */
  init: function (apiAddress, wifiOnly) {
    if (typeof (wifiOnly) !== 'undefined') Update.wifiOnly = wifiOnly ? true : false;
    Update.apiAddress = apiAddress;
  },

  /**
   * 检查更新
   */
  check: function() {
    cordova.exec(null, null, "Update", "check", [Update.apiAddress, Update.wifiOnly]);
  },
  manualCheck: function () {
    cordova.exec(null, null, "Update", "manual-check", [Update.apiAddress, Update.wifiOnly]);
  }
};

module.exports = Update;
