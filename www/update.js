
var Update = {
  /**
   * 初始化
   * @param {String} apiAddress 检查更新API地址
   */
  init: function (apiAddress) {
    cordova.exec(null, null, "Update", "init", []);
  },

  /**
   * 检查更新
   */
  check: function() {
    cordova.exec(null, null, "Update", "check", []);
  }
};

module.exports = Update;
