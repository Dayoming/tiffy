const { defineConfig } = require('@vue/cli-service');

module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    proxy: {
      '/api': {
        target: `http://${process.env.VUE_APP_IP}:${process.env.VUE_APP_PORT}`,
        changeOrigin: true,
      },
    },
    client: {
        overlay: {
            runtimeErrors: (error) => {
                if (error.message === 'ResizeObserver loop limit exceeded') {
                    return false;
                }
                return true;
            }
        }
    }
  },
});
