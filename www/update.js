
var Update = {
  apiAddress: null,

  /**
   * 初始化
   * @param {String} apiAddress 检查更新API地址
   */
  init: function (apiAddress) {
    Update.apiAddress = apiAddress;
  },

  /**
   * 检查更新
   */
  check: function() {
    cordova.exec(null, null, "Update", "check", [Update.apiAddress]);
  },
  manualCheck: function () {
    cordova.exec(null, null, "Update", "manual-check", [Update.apiAddress]);
  }
};

module.exports = Update;
